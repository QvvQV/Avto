package ru.tutor.test.Ui.orders;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import ru.tutor.base.BaseUiTest;
import ru.tutor.config.TestConfig;
import ru.tutor.flow.*;
import ru.tutor.page.Registration;
import ru.tutor.page.modals.Product;
import ru.tutor.page.modals.UnlockNextWeek;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.page.orders.SberPage;
import ru.tutor.page.admin.AdminOrdersPage;
import ru.tutor.page.orders.TtPage;
import ru.tutor.steps.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@Epic("Платежи через Cloud/валидной картой Sber")
@Feature("Тестирование различных сценариев оплаты")
@DisplayName("🧪 Тестирование платежной системы Cloud/валидной картой Sber")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CloudTest extends BaseUiTest {

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
    private final AuthSteps authSteps = new AuthSteps(TestConfig.getCurrentApiUrl());
    private final AdminSteps adminSteps = new AdminSteps(TestConfig.getCurrentApiUrl());
    private final AccountSteps accountSteps = new AccountSteps(TestConfig.getCurrentApiUrl());
    private final OrderToggleSubscriptionSteps orderToggleSubscriptionSteps = new OrderToggleSubscriptionSteps(TestConfig.getCurrentApiUrl());

    // ====FLOW====
    private final PurchaseFlow purchaseFlow = new PurchaseFlow(ttPage, cloudPage, sberPage);
    private final AdminFlow adminFlow = new AdminFlow(adminOrdersPage);
    private final RevshareFlow revshareFlow = new RevshareFlow(cloudPage, orderSteps);
    private final RenewalModalFlow renewalModalFlow = new RenewalModalFlow(registration, product, unlockNextWeek,
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
//        @RepeatedTest(3)
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

    @Test
    @Order(6)
    @DisplayName("Оплата по тестовому домену")
    public void PayFromRevShare() {
        assumeTrue(TestConfig.isProd(),
                "Тест запускается только на прод-окружении (env.ui=prod)");
        revshareFlow.executeProd();
    }

    @Test
    @Order(7)
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
    @DisplayName("Премиум 99₽: заказ Лайт → подписка API (623,625) → Cloud → продукт 264")
    public void Premium99FromHotNovelties() {
        assumeTrue(!TestConfig.isProd(),
                "Тест запускается только на dev-окружении (env.ui != prod)");
        premium99Flow.createPremium99FromHotNovelties();
    }
}