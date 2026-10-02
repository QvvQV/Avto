package ru.tutor.test.Ui.orders;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import ru.tutor.base.BaseUiTest;
import ru.tutor.config.TestConfig;
import ru.tutor.flow.account.RegistrationFlow;
import ru.tutor.flow.account.RenewalModalFlow;
import ru.tutor.flow.purchase.PurchaseFlow;
import ru.tutor.flow.purchase.PurchaseResult;
import ru.tutor.flow.purchase.RevshareFlow;
import ru.tutor.flow.tariff.LifetimeTariffFlow;
import ru.tutor.flow.tariff.Premium99Flow;
import ru.tutor.page.modals.Product;
import ru.tutor.page.modals.UnlockNextWeek;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.page.orders.SberPage;
import ru.tutor.page.orders.TtPage;
import ru.tutor.steps.account.AccountSteps;
import ru.tutor.steps.account.AuthSteps;
import ru.tutor.steps.admin.AdminSteps;
import ru.tutor.steps.orders.OrderSteps;
import ru.tutor.steps.orders.OrderToggleSubscriptionSteps;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import ru.tutor.constants.Tags;

@Epic("Платежи через Cloud/валидной картой Sber")
@Feature("Тестирование различных сценариев оплаты")
@DisplayName("🧪 Тестирование платежной системы Cloud/валидной картой Sber")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CloudTest extends BaseUiTest {


    private final TtPage ttPage = new TtPage();
    private final CloudPage cloudPage = new CloudPage();
    private final SberPage sberPage = new SberPage();
    private final Product product = new Product();
    private final UnlockNextWeek unlockNextWeek = new UnlockNextWeek();

    private final OrderSteps orderSteps = new OrderSteps(
            TestConfig.getCurrentApiUrl()
    );
    private final AuthSteps authSteps = new AuthSteps(TestConfig.getCurrentApiUrl());
    private final AdminSteps adminSteps = new AdminSteps(TestConfig.getCurrentApiUrl());
    private final AccountSteps accountSteps = new AccountSteps(TestConfig.getCurrentApiUrl());
    private final OrderToggleSubscriptionSteps orderToggleSubscriptionSteps = new OrderToggleSubscriptionSteps(TestConfig.getCurrentApiUrl());

    // ====FLOW====
    private final PurchaseFlow purchaseFlow = new PurchaseFlow(ttPage, cloudPage, sberPage);
    private final RevshareFlow revshareFlow = new RevshareFlow(cloudPage, orderSteps);
    private final RegistrationFlow registrationFlow = new RegistrationFlow();
    private final RenewalModalFlow renewalModalFlow = new RenewalModalFlow(product, unlockNextWeek,
            cloudPage, ttPage, sberPage, revshareFlow, orderToggleSubscriptionSteps);
    private final LifetimeTariffFlow lifetimeTariffFlow = new LifetimeTariffFlow(
            purchaseFlow, cloudPage, unlockNextWeek, renewalModalFlow,
            authSteps, accountSteps, adminSteps, orderSteps
    );
    private final Premium99Flow premium99Flow = new Premium99Flow(
            purchaseFlow, cloudPage, renewalModalFlow,
            authSteps, accountSteps, adminSteps, orderSteps
    );


    @AfterEach
    public void afterEachTest() {
        try {
            renewalModalFlow.logoutAfterTest();
        } catch (RuntimeException ignored) {
        }
    }


    @Test
    @Order(1)
//        @RepeatedTest(10)
    @Tag(Tags.E2E)
    @DisplayName("Оплата с главной за 399 руб")
    public void PayMain399() {
        if (TestConfig.isProd()) {
            purchaseFlow.verifyModalOpens(TtPage.SubscriptionType.PREMIUM, TestConfig.getProdMainBaseUrl());
        } else {
            PurchaseResult result = purchaseFlow.premium();
            OrderSteps.validateSuccessfulPayment(cloudPage);
            assertNotNull(result.email());
            adminSteps.assertOrderExists(result.email());
        }
    }

    @Test
    @Order(2)
//    @RepeatedTest(10)
    @Tag(Tags.E2E)
    @DisplayName("Оплата с главной за 99 руб")
    public void PayMain99() {
        if (TestConfig.isProd()) {
            purchaseFlow.verifyModalOpens(TtPage.SubscriptionType.LITE, TestConfig.getProdMainBaseUrl());
        } else {
            PurchaseResult result = purchaseFlow.lite();
            OrderSteps.validateSuccessfulPayment(cloudPage);
            assertNotNull(result.email());
            adminSteps.assertOrderExists(result.email());
        }
    }

    @Test
    @Order(3)
    @Tag(Tags.E2E)
    @DisplayName("Оплата с главной за Пожизненный")
    public void PayMainLife() {
        if (TestConfig.isProd()) {
            purchaseFlow.verifyModalOpens(TtPage.SubscriptionType.LIFE, TestConfig.getProdMainBaseUrl());
        } else {

            PurchaseResult result = purchaseFlow.life();
            OrderSteps.validateSuccessfulPayment(cloudPage);
            assertNotNull(result.email());
            adminSteps.assertOrderExists(result.email());
        }
    }

    @Test
    @Order(4)
    @Tag(Tags.E2E)
    @DisplayName("Оплата Долями с главной за Пожизненный Cloud")
    public void PayMainLifeInstallments() {
        if (TestConfig.isProd()) {
            purchaseFlow.verifyInstallmentOpens(TtPage.SubscriptionType.LIFE, TestConfig.getProdMainBaseUrl());
        } else {

            PurchaseResult result = purchaseFlow.lifeInstallments();
            assertNotNull(result.email());
            adminSteps.assertOrderExists(result.email());
        }
    }

    @Test
    @Order(5)
    @Tag(Tags.E2E)
    @DisplayName("Оплата 'Плати частями' через Sber")
    public void PayInstallmentsPartsSber() {
        if (TestConfig.isProd()) {
            purchaseFlow.verifyPartPaymentOpens(TtPage.SubscriptionType.LIFE, TestConfig.getProdMainBaseUrl());
        } else {
            PurchaseResult result = purchaseFlow.partPayment();
            assertNotNull(result.email());
            adminSteps.assertOrderExists(result.email());
        }
    }

//    @Test
    @RepeatedTest(10)
    @Order(6)
    @Tag(Tags.E2E)
    @DisplayName("Оплата по тестовому домену")
    public void PayFromTesterProd() {
        assumeTrue(TestConfig.isProd(),
                "Тест запускается только на прод-окружении (env.ui=prod)");
        revshareFlow.executeProd();
    }

    @Test
    @Order(7)
    @Tag(Tags.E2E)
    @DisplayName("Сокращение доступа через админку и оплата lifetime-тарифа (POST pay-lifetime-tariff)")
    public void ExtendAccessAndPayLifetimeTariff() {
        if (TestConfig.isProd()) {
            lifetimeTariffFlow.createLifetimeForInactiveUserProd();
        } else {
            lifetimeTariffFlow.createLifetimeForInactiveUser();
        }
    }

    @Test
    @Order(8)
    @Tag(Tags.E2E)
    @DisplayName("Сокращение доступа через админку и оплата RENEWAL-тарифа")
    public void ExtendAccessAndPayRenewal() {
        if (TestConfig.isProd()) {
            lifetimeTariffFlow.createRenewalForInactiveUserProd();
        } else {
            lifetimeTariffFlow.createRenewalForInactiveUser();
        }
    }

    @Test
    @Order(9)
    @Tag(Tags.E2E)
    @DisplayName("Премиум 99₽: заказ Лайт → подписка API (623,625) → Cloud → продукт 264")
    public void Premium99FromHotNovelties() {
        if (TestConfig.isProd()) {
            premium99Flow.createPremium99FromHotNoveltiesProd();
        } else {
            premium99Flow.createPremium99FromHotNovelties();
        }
    }

    @Test
    @Order(10)
    @Tag(Tags.E2E)
    @DisplayName("Регистрация нового пользователя → вход → приветственная форма → клик на первый курс")
    @Severity(SeverityLevel.CRITICAL)
    void registerUserAndOpenFirstCourse() {
        registrationFlow.registerNewUserAndOpenFirstCourse();
    }
}