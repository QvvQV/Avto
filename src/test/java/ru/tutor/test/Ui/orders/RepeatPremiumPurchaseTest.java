package ru.tutor.test.Ui.orders;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.tutor.base.BaseUiTest;
import ru.tutor.config.TestConfig;
import ru.tutor.flow.account.PremiumWelcomeBackFlow;
import ru.tutor.flow.account.RenewalModalFlow;
import ru.tutor.flow.purchase.RevshareFlow;
import ru.tutor.page.account.Registration;
import ru.tutor.page.modals.Product;
import ru.tutor.page.modals.UnlockNextWeek;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.page.orders.SberPage;
import ru.tutor.page.orders.TtPage;
import ru.tutor.steps.admin.AdminSteps;
import ru.tutor.steps.orders.OrderSteps;
import ru.tutor.steps.orders.OrderToggleSubscriptionSteps;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assumptions.assumeTrue;
import ru.tutor.constants.Tags;

// TP-7673 Убрать ограничение на дубли для вернувшихся пользователей
@Epic("Повторная покупка")
@Feature("Сценарий «Рады вновь вас видеть»")
@DisplayName("🧪 Повторная покупка → модальное окно «Рады вновь вас видеть»")
public class RepeatPremiumPurchaseTest extends BaseUiTest {

    private final TtPage ttPage = new TtPage();
    private final CloudPage cloudPage = new CloudPage();
    private final SberPage  sberPage = new SberPage();
    private final Registration  registration = new Registration();
    private final Product product = new Product();
    private final UnlockNextWeek unlockNextWeek = new UnlockNextWeek();
    private final OrderSteps orderSteps = new OrderSteps(TestConfig.getCurrentApiUrl());
    private final RevshareFlow revshareFlow = new RevshareFlow(cloudPage, orderSteps);
    private final OrderToggleSubscriptionSteps orderToggleSubscriptionSteps = new OrderToggleSubscriptionSteps(TestConfig.getCurrentApiUrl());
    private final RenewalModalFlow renewalModalFlow = new RenewalModalFlow(
            product, unlockNextWeek, cloudPage, ttPage, sberPage,
            revshareFlow, orderToggleSubscriptionSteps
    );
    private final AdminSteps adminSteps = new AdminSteps(TestConfig.getCurrentApiUrl());
    private final PremiumWelcomeBackFlow premiumWelcomeBackFlow =
            new PremiumWelcomeBackFlow(ttPage, cloudPage, adminSteps);

    @ParameterizedTest
    @MethodSource("welcomeBackCombinations")
    @Tag(Tags.UI)
    @DisplayName("Активный тариф {0} → попытка купить {1} → появление ссылки «Авторизация»")
    @Severity(SeverityLevel.CRITICAL)
    void repeatPurchaseShowsWelcomeBackModal(TtPage.SubscriptionType active,
                                            TtPage.SubscriptionType target) {
        assumeTrue(!TestConfig.isProd(), "Тест запускается только на dev-окружение");

        premiumWelcomeBackFlow.purchaseAndCheckWelcomeBack(active, target);
    }

    static Stream<Arguments> welcomeBackCombinations() {
        return Stream.of(
                Arguments.of(TtPage.SubscriptionType.PREMIUM, TtPage.SubscriptionType.PREMIUM),
                Arguments.of(TtPage.SubscriptionType.PREMIUM, TtPage.SubscriptionType.LITE),
                Arguments.of(TtPage.SubscriptionType.LITE, TtPage.SubscriptionType.LITE),
                Arguments.of(TtPage.SubscriptionType.LIFE, TtPage.SubscriptionType.LIFE),
                Arguments.of(TtPage.SubscriptionType.LIFE, TtPage.SubscriptionType.PREMIUM),
                Arguments.of(TtPage.SubscriptionType.LIFE, TtPage.SubscriptionType.LITE)
        );
    }

    @ParameterizedTest
    @MethodSource("payCombinations")
    @Tag(Tags.UI)
    @DisplayName("Активный {0} → оплата {1} тем же email → заказ с тарифом «{1}» в админке")
    @Severity(SeverityLevel.CRITICAL)
    void repeatPurchasePaysTargetTariff(TtPage.SubscriptionType active, TtPage.SubscriptionType target) {
        assumeTrue(!TestConfig.isProd(), "Тест запускается только на dev-окружение");
        premiumWelcomeBackFlow.purchaseAndPay(active, target);
    }

    static Stream<Arguments> payCombinations() {
        return Stream.of(
                Arguments.of(TtPage.SubscriptionType.LITE, TtPage.SubscriptionType.PREMIUM),
                Arguments.of(TtPage.SubscriptionType.LITE, TtPage.SubscriptionType.LIFE),
                Arguments.of(TtPage.SubscriptionType.PREMIUM, TtPage.SubscriptionType.LIFE)
        );
    }

    @Test
    @Order(1)
    @Tag(Tags.UI)
    @DisplayName("Активный Реневалл → покупка Премиум → появление ссылки «Авторизация»")
    @Severity(SeverityLevel.CRITICAL)
    void premiumWithActiveRenewalShowsWelcomeBackModal() {
        assumeTrue(!TestConfig.isProd(), "Тест запускается только на dev-окружение");

        ActiveRenewal active = createActiveRenewalOrderAndLogout();
        premiumWelcomeBackFlow.assertWelcomeBackForExistingEmail(active.email(), TtPage.SubscriptionType.PREMIUM);
    }

    @Test
    @Order(2)
    @Tag(Tags.UI)
    @DisplayName("Активный Реневалл → покупка Лайт → появление ссылки «Авторизация»")
    @Severity(SeverityLevel.CRITICAL)
    void liteWithActiveRenewalShowsWelcomeBackModal() {
        assumeTrue(!TestConfig.isProd(), "Тест запускается только на dev-окружение");

        ActiveRenewal active = createActiveRenewalOrderAndLogout();
        premiumWelcomeBackFlow.assertWelcomeBackForExistingEmail(active.email(), TtPage.SubscriptionType.LITE);
    }

    @Test
    @Order(3)
    @Tag(Tags.UI)
    @DisplayName("Активный Реневалл → покупка Пожизненный → модалку не показываем, старые заказы status=2")
    @Severity(SeverityLevel.CRITICAL)
    void lifetimeWithActiveRenewalShowsWelcomeBackModal() {
        assumeTrue(!TestConfig.isProd(), "Тест запускается только на dev-окружение");

        ActiveRenewal active = createActiveRenewalOrderAndLogout();
        premiumWelcomeBackFlow.purchaseLifetimeForExistingEmail(
                active.email(), active.liteOrderId(), active.renewalOrderId());
    }

    @Test
    @Order(4)
    @Tag(Tags.UI)
    @DisplayName("Замороженный Премиум → покупка Премиум → появление ссылки «Авторизация»")
    @Severity(SeverityLevel.CRITICAL)
    void frozenPremiumTryBuyPremiumShowsModal() {
        assumeTrue(!TestConfig.isProd(), "Тест запускается только на dev-окружение");
        String email = renewalModalFlow.createFrozenPremiumAndLogout();
        premiumWelcomeBackFlow.assertWelcomeBackForExistingEmail(email, TtPage.SubscriptionType.PREMIUM);
    }

    @Test
    @Order(5)
    @Tag(Tags.UI)
    @DisplayName("Замороженный Лайт → покупка Лайт → появление ссылки «Авторизация»")
    @Severity(SeverityLevel.CRITICAL)
    void frozenLiteTryBuyPremiumShowsModal() {
        assumeTrue(!TestConfig.isProd(), "Тест запускается только на dev-окружение");
        String email = renewalModalFlow.createFrozenLiteAndLogout();
        premiumWelcomeBackFlow.assertWelcomeBackForExistingEmail(email, TtPage.SubscriptionType.LITE);
    }

    @Test
    @Order(6)
    @Tag(Tags.UI)
    @DisplayName("Замороженный Реневалл → покупка Премиум → появление ссылки «Авторизация»")
    @Severity(SeverityLevel.CRITICAL)
    void frozenRenewalTryBuyPremiumShowsModal() {
        assumeTrue(!TestConfig.isProd(), "Тест запускается только на dev-окружение");
        RenewalModalFlow.RenewalClient client = renewalModalFlow.createFrozenRenewalOrderAndLogout();
        premiumWelcomeBackFlow.assertWelcomeBackForExistingEmail(client.email(), TtPage.SubscriptionType.PREMIUM);
    }

    @Test
    @Order(7)
    @Tag(Tags.UI)
    @DisplayName("Замороженный Реневалл → покупка Лайт → появление ссылки «Авторизация»")
    @Severity(SeverityLevel.CRITICAL)
    void frozenRenewalTryBuyLiteShowsModal() {
        assumeTrue(!TestConfig.isProd(), "Тест запускается только на dev-окружение");
        RenewalModalFlow.RenewalClient client = renewalModalFlow.createFrozenRenewalOrderAndLogout();
        premiumWelcomeBackFlow.assertWelcomeBackForExistingEmail(client.email(), TtPage.SubscriptionType.LITE);
    }

    private record ActiveRenewal(String email, String liteOrderId, String renewalOrderId) {}

    private ActiveRenewal createActiveRenewalOrderAndLogout() {
        renewalModalFlow.login();
        String email = renewalModalFlow.getUserEmail();
        String liteOrderId = adminSteps.assertOrderExists(email);
        renewalModalFlow.openPayments99AndAssertIframe();
        String renewalOrderId = adminSteps.assertOrderExists(email);
        renewalModalFlow.verifyProductPresent(RenewalModalFlow.RENEWAL_PRODUCT_ID);
        renewalModalFlow.logoutAfterTest();
        return new ActiveRenewal(email, liteOrderId, renewalOrderId);
    }

    @AfterEach
    void clearBrowserSession() {
        renewalModalFlow.logoutAfterTest();
    }
}