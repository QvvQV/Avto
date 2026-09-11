package ru.tutor.test.Ui.orders.renewal;

import io.qameta.allure.Feature;
import org.junit.jupiter.api.*;
import ru.tutor.base.BaseUiTest;
import ru.tutor.config.TestConfig;
import ru.tutor.flow.*;
import ru.tutor.page.Registration;
import ru.tutor.page.modals.Product;
import ru.tutor.page.modals.UnlockNextWeek;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.page.orders.SberPage;
import ru.tutor.page.orders.TtPage;
import ru.tutor.utils.WaitHelper;

import static com.codeborne.selenide.Selenide.refresh;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderRenewal extends BaseUiTest {

    private final TtPage ttPage = new TtPage();
    private final CloudPage cloudPage = new CloudPage();
    private final SberPage sberPage = new SberPage();
    private final Registration registration = new Registration();
    private final Product product = new Product();
    private final UnlockNextWeek unlockNextWeek = new UnlockNextWeek();

    // ====FLOW====
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
        renewalModalFlow.logoutAfterTest();
    }
}
