package ru.tutor.test.Ui.orders;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import ru.tutor.api.PaymentApi;
import ru.tutor.base.BaseUiTest;
import ru.tutor.config.TestConfig;
import ru.tutor.flow.*;
import ru.tutor.page.CardioLandingPage;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.page.orders.SberPage;
import ru.tutor.page.admin.AdminOrdersPage;
import ru.tutor.page.orders.TtPage;
import ru.tutor.steps.OrderSteps;
import ru.tutor.steps.PaymentSteps;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@Epic("Платежи через Cloud/валидной картой Sber")
@Feature("Тестирование различных сценариев оплаты")
@DisplayName("🧪 Тестирование платежной системы Cloud/валидной картой Sber")
public class CloudTest extends BaseUiTest {

    private final AdminOrdersPage adminOrdersPage = new AdminOrdersPage();
    private final TtPage ttPage = new TtPage();
    private final CloudPage cloudPage = new CloudPage();
    private final SberPage sberPage = new SberPage();
    private final CardioLandingPage cardioLandingPage = new CardioLandingPage();

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
    private final CardioLandingFlow  cardioLandingFlow = new CardioLandingFlow(cardioLandingPage, paymentApi, paymentSteps);
    private final PaymentMethodFlow paymentMethodFlow = new PaymentMethodFlow(ttPage, cloudPage, paymentApi, paymentSteps, sberPage);




    //    @Test
//            @RepeatedTest(2)
    @DisplayName("Оплата с главной на СисАк Oфферум")
    public void PayFromMainOfferrum() {

        PurchaseResult result = purchaseFlow.premiumAffRid();
        assertNotNull(result.email());
        adminFlow.assertOrderExists(result.email());
    }

    @Test
//    @RepeatedTest(10)
    @DisplayName("Оплата с главной за 99 руб")
    public void PayMain99() {
        if (TestConfig.isProd()) {
            purchaseFlow.verifyModalOpens(TtPage.SubscriptionType.LITE);
        } else {
            PurchaseResult result = purchaseFlow.lite();
            assertNotNull(result.email());
            adminFlow.assertOrderExists(result.email());
        }
    }

    @Test
//        @RepeatedTest(3)
    @DisplayName("Оплата с главной за 399 руб")
    public void PayMain399() {
        if (TestConfig.isProd()) {
            purchaseFlow.verifyModalOpens(TtPage.SubscriptionType.PREMIUM);
        } else {

            PurchaseResult result = purchaseFlow.premium();
            assertNotNull(result.email());
            adminFlow.assertOrderExists(result.email());
        }
    }

    @Test
    @DisplayName("Оплата с главной за Пожизненный")
    public void PayMainLife() {
        if (TestConfig.isProd()) {
            purchaseFlow.verifyModalOpens(TtPage.SubscriptionType.LIFE);
        } else {

            PurchaseResult result = purchaseFlow.life();
            assertNotNull(result.email());
            adminFlow.assertOrderExists(result.email());
        }
    }

    @Test
    @DisplayName("Оплата Долями с главной за Пожизненный Cloud")
    public void PayMainLifeInstallments() {
        if (TestConfig.isProd()) {
            purchaseFlow.verifyInstallmentOpens(TtPage.SubscriptionType.LIFE);
        } else {

            PurchaseResult result = purchaseFlow.lifeInstallments();
            assertNotNull(result.email());
            adminFlow.assertOrderExists(result.email());
        }
    }

    @Test
    @DisplayName("Оплата 'Плати частями' через Sber")
    public void PayInstallmentsPartsSber() {
        if (TestConfig.isProd()) {
            purchaseFlow.verifyPartPaymentOpens(TtPage.SubscriptionType.LIFE);
        } else {
            PurchaseResult result = purchaseFlow.partPayment();
            assertNotNull(result.email());
            adminFlow.assertOrderExists(result.email());
        }
    }

    @Test
    @DisplayName("Оплата по тестовому домену")
    public void PayFromRevShare() {
        assumeTrue(TestConfig.isProd(),
                "Тест запускается только на прод-окружении (env.ui=prod)");
        revshareFlow.executeProd();
    }


    //    === AFF_RID===
    @Test
    @Tag("API + UI")
    @DisplayName("Сбер картой: появление 4 кнопок на Главной и клик -> drawer")
    void paymentMethod_SberCard() {
        paymentMethodFlow.sberCardPayment();
    }

    @Test
    @Tag("API + UI")
    @DisplayName("Тинькофф картой: появление 4 кнопок на Главной и клик -> drawer")
    void paymentMethod_TinkCard() {
        paymentMethodFlow.tinkPayment();
    }

    @Test
    @Tag("API + UI")
    @DisplayName("Other: появление 4 кнопок на Главной и клик -> drawer")
    void paymentMethod_Other() {
        paymentMethodFlow.otherPayment();
    }

    @Test
    @Tag("API + UI")
    @DisplayName("Cloud: появление 4 кнопок на стр Лендинга и клик -> drawer")
    void paymentMethod_Cloud_Drawer_Landing(){
        assumeTrue(TestConfig.isProd(), "Только прод");
        cardioLandingFlow.tinkPayment();
    }

    @Test
    @Tag("API + UI")
    @DisplayName("Other: появление 4 кнопок на стр Лендинга и клик -> drawer")
    void paymentMethod_Other_Drawer_Landing(){
        assumeTrue(TestConfig.isProd(), "Только прод");
        cardioLandingFlow.otherPayment();
    }

    @Test
    @Tag("API + UI")
    @DisplayName("Sber: появление 4 кнопок на стр Лендинга и клик -> drawer")
    void paymentMethod_Sber_Drawer_Landing(){
        assumeTrue(TestConfig.isProd(), "Только прод");
        cardioLandingFlow.sberCardPayment();
    }
}