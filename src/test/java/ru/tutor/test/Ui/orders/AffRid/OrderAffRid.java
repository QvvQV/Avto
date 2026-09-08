package ru.tutor.test.Ui.orders.AffRid;

import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.tutor.api.PaymentApi;
import ru.tutor.base.BaseUiTest;
import ru.tutor.config.TestConfig;
import ru.tutor.flow.*;
import ru.tutor.page.CardioLandingPage;
import ru.tutor.page.Registration;
import ru.tutor.page.admin.AdminOrdersPage;
import ru.tutor.page.modals.Product;
import ru.tutor.page.modals.UnlockNextWeek;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.page.orders.SberPage;
import ru.tutor.page.orders.TtPage;
import ru.tutor.steps.OrderSteps;
import ru.tutor.steps.PaymentSteps;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class OrderAffRid extends BaseUiTest {

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

    //    === AFF_RID===
    @Test
    @Order(7)
    @Tag("API + UI")
    @Feature("Оформление по AFF_RID")
    @DisplayName("Сбер картой: появление 4 кнопок на Главной и клик -> drawer")
    void paymentMethod_SberCard() {
        paymentMethodFlow.sberCardPayment();
    }

    @Test
    @Order(8)
    @Tag("API + UI")
    @Feature("Оформление по AFF_RID")
    @DisplayName("Тинькофф картой: появление 4 кнопок на Главной и клик -> drawer")
    void paymentMethod_TinkCard() {
        paymentMethodFlow.tinkPayment();
    }

    @Test
    @Order(9)
    @Tag("API + UI")
    @Feature("Оформление по AFF_RID")
    @DisplayName("Other: появление 4 кнопок на Главной и клик -> drawer")
    void paymentMethod_Other() {
        paymentMethodFlow.otherPayment();
    }

    @Test
    @Order(10)
    @Tag("API + UI")
    @Feature("Оформление по AFF_RID")
    @DisplayName("Cloud: появление 4 кнопок на стр Лендинга и клик -> drawer")
    void paymentMethod_Cloud_Drawer_Landing(){
        assumeTrue(TestConfig.isProd(), "Только прод");
        cardioLandingFlow.tinkPayment();
    }

    @Test
    @Order(11)
    @Tag("API + UI")
    @Feature("Оформление по AFF_RID")
    @DisplayName("Other: появление 4 кнопок на стр Лендинга и клик -> drawer")
    void paymentMethod_Other_Drawer_Landing(){
        assumeTrue(TestConfig.isProd(), "Только прод");
        cardioLandingFlow.otherPayment();
    }

    @Test
    @Order(12)
    @Tag("API + UI")
    @Feature("Оформление по AFF_RID")
    @DisplayName("Sber: появление 4 кнопок на стр Лендинга и клик -> drawer")
    void paymentMethod_Sber_Drawer_Landing(){
        assumeTrue(TestConfig.isProd(), "Только прод");
        cardioLandingFlow.sberCardPayment();
    }
}
