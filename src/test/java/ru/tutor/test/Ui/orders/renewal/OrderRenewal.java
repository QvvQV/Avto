package ru.tutor.test.Ui.orders.renewal;

import io.qameta.allure.Feature;
import org.junit.jupiter.api.*;
import ru.tutor.api.PaymentApi;
import ru.tutor.base.BaseUiTest;
import ru.tutor.config.TestConfig;
import ru.tutor.flow.*;
import ru.tutor.page.CardioLandingPage;
import ru.tutor.page.Registration;
import ru.tutor.page.admin.AdminOrdersPage;
import ru.tutor.page.modals.Product;
import ru.tutor.page.modals.SecretMenu;
import ru.tutor.page.modals.UnlockNextWeek;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.page.orders.SberPage;
import ru.tutor.page.orders.TtPage;
import ru.tutor.steps.OrderSteps;
import ru.tutor.steps.PaymentSteps;
import ru.tutor.utils.WaitHelper;

import static com.codeborne.selenide.Selenide.refresh;

public class OrderRenewal extends BaseUiTest {

    private final AdminOrdersPage adminOrdersPage = new AdminOrdersPage();
    private final TtPage ttPage = new TtPage();
    private final CloudPage cloudPage = new CloudPage();
    private final SberPage sberPage = new SberPage();
    private final CardioLandingPage cardioLandingPage = new CardioLandingPage();
    private final Registration registration = new Registration();
    private final Product product = new Product();
    private final UnlockNextWeek unlockNextWeek = new UnlockNextWeek();

    private final OrderSteps orderSteps = new OrderSteps(
            TestConfig.getCurrentApiUrl()
    );

    // ====API====
    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private final PaymentApi paymentApi = new PaymentApi(BASE_URL);
    private final PaymentSteps paymentSteps = new PaymentSteps(BASE_URL);

    // ====FLOW====
    private final PurchaseFlow purchaseFlow = new PurchaseFlow(ttPage, cloudPage, sberPage);
    private final AdminFlow adminFlow = new AdminFlow(adminOrdersPage);
    private final RevshareFlow revshareFlow = new RevshareFlow(cloudPage, orderSteps);
    private final CardioLandingFlow cardioLandingFlow = new CardioLandingFlow(cardioLandingPage, paymentApi, paymentSteps);
    private final PaymentMethodFlow paymentMethodFlow = new PaymentMethodFlow(ttPage, cloudPage, paymentApi, paymentSteps, sberPage);
    private final RenewalModalFlow renewalModalFlow = new RenewalModalFlow(registration, product, unlockNextWeek,
            cloudPage, ttPage, sberPage);

    //    ===Renewal в ЛК===

    @Test
    @Feature("Renewal в ЛК")
    @Tag("API + UI")
    @DisplayName("Renewal: логин в ЛК → модалка оплаты пожизненного → фиксация iframe")
    void paymentMethod_Renewal_PaymentModal_Life_Iframe() {
        renewalModalFlow.login();
        renewalModalFlow.openFirstProductLesson();
        renewalModalFlow.processToPaymentModal();
        if (TestConfig.isProd()) {

            renewalModalFlow.assertPaymentFrameVisible();
        } else {
            cloudPage.clickCardPageBtn();
            refresh();
            WaitHelper.waitForPageLoad();
            renewalModalFlow.verifyProductPresent(563);
        }
//        new SecretMenu().logoutCurrentUser();
        renewalModalFlow.logoutAfterTest();
    }

    @Test
    @Feature("Renewal в ЛК")
    @Tag("API + UI")
    @DisplayName("Renewal: логин -> модалка оплаты Реневал -> фиксация iframe")
    void paymentMethod_Renewal_PaymentModal_iframe() {
        renewalModalFlow.login();
        renewalModalFlow.openPayments99AndAssertIframe();
        if (!TestConfig.isProd()) {
            renewalModalFlow.verifyProductPresent(264);
        }
//        new SecretMenu().logoutCurrentUser();
        renewalModalFlow.logoutAfterTest();
    }
}
