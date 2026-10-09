package ru.tutor.test.Ui.orders.renewal;

import io.qameta.allure.Feature;
import org.junit.jupiter.api.*;
import ru.tutor.base.BaseUiTest;
import ru.tutor.config.TestConfig;
import ru.tutor.flow.account.RenewalModalFlow;
import ru.tutor.flow.purchase.RevshareFlow;
import ru.tutor.page.account.Registration;
import ru.tutor.page.modals.Product;
import ru.tutor.page.modals.UnlockNextWeek;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.page.orders.SberPage;
import ru.tutor.page.orders.TtPage;
import ru.tutor.steps.orders.OrderSteps;
import ru.tutor.steps.orders.OrderToggleSubscriptionSteps;
import ru.tutor.utils.WaitHelper;

import static com.codeborne.selenide.Selenide.refresh;
import ru.tutor.constants.Tags;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag(Tags.E2E)
public class OrderRenewal extends BaseUiTest {

    private final TtPage ttPage = new TtPage();
    private final CloudPage cloudPage = new CloudPage();
    private final SberPage sberPage = new SberPage();
    private final Registration registration = new Registration();
    private final Product product = new Product();
    private final UnlockNextWeek unlockNextWeek = new UnlockNextWeek();
    private final OrderSteps orderSteps = new OrderSteps(
            TestConfig.getCurrentApiUrl()
    );
    private final RevshareFlow revshareFlow = new RevshareFlow(cloudPage, orderSteps);
    private final OrderToggleSubscriptionSteps orderToggleSubscriptionSteps = new OrderToggleSubscriptionSteps(TestConfig.getCurrentApiUrl());

    // ====FLOW====
    private final RenewalModalFlow renewalModalFlow = new RenewalModalFlow(product, unlockNextWeek,
            cloudPage, ttPage, sberPage, revshareFlow, orderToggleSubscriptionSteps);

    //    ===Renewal в ЛК===

    @Test
    @Order(1)
    @Feature("Renewal в ЛК")
    @Tag(Tags.UI_API)
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
            renewalModalFlow.verifyProductPresent(RenewalModalFlow.LIFE_PRODUCT_ID);
        }
        renewalModalFlow.logoutAfterTest();
    }

    @Test
    @Order(2)
    @Feature("Renewal в ЛК")
    @Tag(Tags.UI_API)
    @DisplayName("Renewal: логин -> модалка оплаты Реневал -> фиксация iframe")
    void paymentMethod_Renewal_PaymentModal_iframe() {
        renewalModalFlow.login();
        renewalModalFlow.openPayments99AndAssertIframe();
        if (!TestConfig.isProd()) {
            renewalModalFlow.verifyProductPresent(RenewalModalFlow.RENEWAL_PRODUCT_ID);
        }
        renewalModalFlow.logoutAfterTest();
    }
}
