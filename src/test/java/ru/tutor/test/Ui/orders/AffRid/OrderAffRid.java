package ru.tutor.test.Ui.orders.AffRid;

import io.qameta.allure.Feature;
import org.junit.jupiter.api.*;
import ru.tutor.api.orders.PaymentApi;
import ru.tutor.base.BaseUiTest;
import ru.tutor.config.TestConfig;
import ru.tutor.flow.landing.CardioLandingFlow;
import ru.tutor.flow.purchase.PaymentMethodFlow;
import ru.tutor.page.landing.CardioLandingPage;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.page.orders.SberPage;
import ru.tutor.page.orders.TtPage;
import ru.tutor.steps.orders.PaymentSteps;

import static org.junit.jupiter.api.Assumptions.assumeTrue;
import ru.tutor.constants.Tags;

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
    @Order(1)
    @Tag(Tags.UI_API)
    @Tag(Tags.E2E)
    @Feature("Оформление по AFF_RID")
    @DisplayName("Сбер картой: появление 4 кнопок на Главной и клик -> drawer")
    void paymentMethod_SberCard() {
        paymentMethodFlow.sberCardPayment();
    }

    @Test
    @Order(2)
    @Tag(Tags.UI_API)
    @Tag(Tags.E2E)
    @Feature("Оформление по AFF_RID")
    @DisplayName("Тинькофф картой: появление 4 кнопок на Главной и клик -> drawer")
    void paymentMethod_TinkCard() {
        paymentMethodFlow.tinkPayment();
    }

    @Test
    @Order(3)
    @Tag(Tags.UI_API)
    @Tag(Tags.E2E)
    @Feature("Оформление по AFF_RID")
    @DisplayName("Other: появление 4 кнопок на Главной и клик -> drawer")
    void paymentMethod_Other() {
        paymentMethodFlow.otherPayment();
    }

    @Test
    @Order(4)
    @Tag(Tags.UI_API)
    @Tag(Tags.E2E)
    @Feature("Оформление по AFF_RID")
    @DisplayName("Cloud: появление 4 кнопок на стр Лендинга и клик -> drawer")
    void paymentMethod_Cloud_Drawer_Landing(){
        assumeTrue(TestConfig.isProd(), "Только прод");
        cardioLandingFlow.tinkPayment();
    }

    @Test
    @Order(5)
    @Tag(Tags.UI_API)
    @Tag(Tags.E2E)
    @Feature("Оформление по AFF_RID")
    @DisplayName("Other: появление 4 кнопок на стр Лендинга и клик -> drawer")
    void paymentMethod_Other_Drawer_Landing(){
        assumeTrue(TestConfig.isProd(), "Только прод");
        cardioLandingFlow.otherPayment();
    }

    @Test
    @Order(6)
    @Tag(Tags.UI_API)
    @Tag(Tags.E2E)
    @Feature("Оформление по AFF_RID")
    @DisplayName("Sber: появление 4 кнопок на стр Лендинга и клик -> drawer")
    void paymentMethod_Sber_Drawer_Landing(){
        assumeTrue(TestConfig.isProd(), "Только прод");
        cardioLandingFlow.sberCardPayment();
    }
}
