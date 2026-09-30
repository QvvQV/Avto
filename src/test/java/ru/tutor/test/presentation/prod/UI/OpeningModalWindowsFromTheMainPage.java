package ru.tutor.test.presentation.prod.UI;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import ru.tutor.api.orders.PaymentApi;
import ru.tutor.base.BaseUiTest;
import ru.tutor.config.TestConfig;
import ru.tutor.constants.AddressPage;
import ru.tutor.flow.account.RenewalModalFlow;
import ru.tutor.flow.admin.AdminFlow;
import ru.tutor.flow.landing.CardioLandingFlow;
import ru.tutor.flow.purchase.PaymentMethodFlow;
import ru.tutor.flow.purchase.PurchaseFlow;
import ru.tutor.flow.purchase.RevshareFlow;
import ru.tutor.page.landing.CardioLandingPage;
import ru.tutor.page.account.Registration;
import ru.tutor.page.admin.AdminOrdersPage;
import ru.tutor.page.modals.Product;
import ru.tutor.page.modals.UnlockNextWeek;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.page.orders.SberPage;
import ru.tutor.page.orders.TtPage;
import ru.tutor.steps.orders.OrderSteps;
import ru.tutor.steps.orders.OrderToggleSubscriptionSteps;
import ru.tutor.steps.orders.PaymentSteps;

import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;
import ru.tutor.constants.Tags;

@Epic("Платежи через Cloud/валидной картой Sber")
@Feature("Тестирование различных сценариев оплаты")
@DisplayName("🧪 Тестирование платежной системы Cloud/валидной картой Sber")
public class OpeningModalWindowsFromTheMainPage extends BaseUiTest {

    private final AdminOrdersPage adminOrdersPage = new AdminOrdersPage();
    private final TtPage ttPage = new TtPage();
    private final CloudPage cloudPage = new CloudPage();
    private final SberPage sberPage = new SberPage();
    private final Registration registration = new Registration();
    private final Product product = new Product();
    private final UnlockNextWeek unlockNextWeek = new UnlockNextWeek();

    private final OrderSteps orderSteps = new OrderSteps(
            TestConfig.getCurrentApiUrl()
    );

    private final PurchaseFlow purchaseFlow = new PurchaseFlow(ttPage, cloudPage, sberPage);
    private final AdminFlow adminFlow = new AdminFlow(adminOrdersPage);
    private final RevshareFlow revshareFlow = new RevshareFlow(cloudPage, orderSteps);


    // ====API====
    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private final PaymentApi paymentApi = new PaymentApi(BASE_URL);
    private final PaymentSteps paymentSteps = new PaymentSteps(BASE_URL);
    private final PaymentMethodFlow paymentMethodFlow = new PaymentMethodFlow(ttPage, cloudPage, paymentApi, paymentSteps, sberPage);
    private final CardioLandingPage cardioLandingPage = new CardioLandingPage();
    private final CardioLandingFlow cardioLandingFlow = new CardioLandingFlow(cardioLandingPage, paymentApi, paymentSteps);
    private final RenewalModalFlow renewalModalFlow = new RenewalModalFlow(registration, product, unlockNextWeek,
            cloudPage, ttPage, sberPage, revshareFlow,
            new OrderToggleSubscriptionSteps(TestConfig.getCurrentApiUrl()));


    private void getAvailablePaymentSystemsSuccess() {
        Response response = step("GET /available-payment-systems?aff_rid=" + TestConfig.getAffRid(),
                () -> paymentApi.getAvailablePaymentSystems()
        );
        paymentSteps.assertPaymentSystems(response, "buttons.name");
        paymentSteps.assertModalVersion(response);
    }

    private void openPremiumMethodButtonsDev() {
        step("Открытие главной страницы (dev)", () ->
                open(AddressPage.URL_MAIN_REV));
        ttPage.selectSubscription(TtPage.SubscriptionType.PREMIUM);
        step("Ввод email и клик 'Оплатить' -> появление кнопок способов", () -> {
            cloudPage.setDateModal();
            cloudPage.assertPaymentMethodButtonsVisible();
        });
    }

//    @Test
    @Tag(Tags.UI)
    @DisplayName("SberPay: появление 4 кнопок и клик -> drawer")
    void paymentMethod_SberPay_Drawer() {
//        assumeTrue(!TestConfig.isProd(), "Этап 1 — только dev");
        getAvailablePaymentSystemsSuccess();
        openPremiumMethodButtonsDev();
        cloudPage.clickSberPayAndVerifyDrawer();
    }

//    @Test
//    @Tag("UI")
//    @DisplayName("Renewal: логин в ЛК → модалка оплаты пожизненного → фиксация iframe")
//    void paymentMethod_Renewal_PaymentModal_Life_Iframe() {
//        renewalModalFlow.login();
//        renewalModalFlow.openFirstProductLesson();
//        renewalModalFlow.processToPaymentModal();
//        if (TestConfig.isProd()) {
//
//            renewalModalFlow.assertPaymentFrameVisible();
//        } else {
//            cloudPage.clickCardPageBtn();
//            refresh();
//            WaitHelper.waitForPageLoad();
//            renewalModalFlow.verifyProductPresent(563);
//        }
//    }
//
//    @Test
//    @Tag("UI")
//    @DisplayName("Renewal: логин -> модалка оплаты Реневал -> фиксация iframe")
//    void paymentMethod_Renewal_PaymentModal_iframe() {
//        renewalModalFlow.login();
//        renewalModalFlow.openPayments99AndAssertIframe();
//        if (!TestConfig.isProd()) {
//            renewalModalFlow.verifyProductPresent(264);
//        }
//    }
}
