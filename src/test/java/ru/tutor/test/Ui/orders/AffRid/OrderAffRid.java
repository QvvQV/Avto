package ru.tutor.test.Ui.orders.AffRid;

import io.qameta.allure.Feature;
import org.junit.jupiter.api.*;
import ru.tutor.api.PaymentApi;
import ru.tutor.base.BaseUiTest;
import ru.tutor.config.TestConfig;
import ru.tutor.flow.*;
import ru.tutor.page.CardioLandingPage;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.page.orders.SberPage;
import ru.tutor.page.orders.TtPage;
import ru.tutor.steps.PaymentSteps;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderAffRid extends BaseUiTest {

    private final TtPage ttPage = new TtPage();
    private final CloudPage cloudPage = new CloudPage();
    private final SberPage sberPage = new SberPage();
    private final CardioLandingPage cardioLandingPage = new CardioLandingPage();

    // ====API====
    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private final PaymentApi paymentApi = new PaymentApi(BASE_URL);
    private final PaymentSteps paymentSteps = new PaymentSteps(BASE_URL);

    // ====FLOW====

    private final CardioLandingFlow cardioLandingFlow = new CardioLandingFlow(cardioLandingPage, paymentApi, paymentSteps);
    private final PaymentMethodFlow paymentMethodFlow = new PaymentMethodFlow(ttPage, cloudPage, paymentApi, paymentSteps, sberPage);


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
