package ru.tutor.flow;

import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import ru.tutor.config.TestConfig;
import ru.tutor.flow.builder.SubscriptionPurchaseBuilder;
import ru.tutor.flow.builder.SubscriptionPurchaseResult;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.page.orders.SberPage;
import ru.tutor.page.admin.AdminLoginPage;
import ru.tutor.page.admin.AdminOrdersPage;
import ru.tutor.page.orders.TtPage;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.TabManager;
import ru.tutor.utils.WaitHelper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;

public class SubscriptionTestFlow {

    // Поле для хранения флага
    private boolean currentOrderHasAffRid = false;

    private String userEmail;
    private final TtPage ttPage;
    private final CloudPage cloudPage;
    private final SberPage sberPage;
    private final AdminOrdersPage adminOrdersPage;

    private void purchase(Consumer<SubscriptionPurchaseBuilder> configurator) {
        SubscriptionPurchaseBuilder builder = SubscriptionPurchaseBuilder.create(ttPage, cloudPage, sberPage);
        configurator.accept(builder);

        this.currentOrderHasAffRid = builder.hasAffRid();
        SubscriptionPurchaseResult result = builder.execute();
        this.userEmail = result.userEmail();
    }

    public SubscriptionTestFlow(TtPage ttPage, CloudPage cloudPage, SberPage sberPage, AdminOrdersPage adminOrdersPage) {
        this.ttPage = ttPage;
        this.cloudPage = cloudPage;
        this.sberPage = sberPage;
        this.adminOrdersPage = adminOrdersPage;
    }

    // Короткие методы-обертки для различных сценариев покупки
    @Step("Оплата Премиума по Клауду")
    public SubscriptionTestFlow purchasePremiumSubscription() {
        purchase(builder ->
                builder.forSubscription(TtPage.SubscriptionType.PREMIUM)
                        .via(SubscriptionPurchaseBuilder.PaymentSystem.CLOUD)
                        .withPaymentMethod(SubscriptionPurchaseBuilder.PaymentMethod.STANDARD)
        );
        return this;
    }

    @Step("Оплата по aff_rid ссылке Клауд")
    public SubscriptionTestFlow purchasePremiumSubscriptionAffRid() {
        purchase(builder ->
                builder.forSubscription(TtPage.SubscriptionType.PREMIUM)
                        .via(SubscriptionPurchaseBuilder.PaymentSystem.CLOUD)
                        .withPaymentMethod(SubscriptionPurchaseBuilder.PaymentMethod.STANDARD)
                        .withAffRid()
        );
        return this;
    }

    @Step("Оплата Пожизненного доступа по Клауду")
    public void purchaseLifeSubscription() {
        purchase(builder ->
                builder.forSubscription(TtPage.SubscriptionType.LIFE)
                        .via(SubscriptionPurchaseBuilder.PaymentSystem.CLOUD)
                        .withPaymentMethod(SubscriptionPurchaseBuilder.PaymentMethod.STANDARD)
        );
    }

    @Step("Оплата долями по Клауду")
    public void purchaseLifeInstallmentsSubscription() {
        purchase(builder ->
                builder.forSubscription(TtPage.SubscriptionType.LIFE)
                        .via(SubscriptionPurchaseBuilder.PaymentSystem.CLOUD)
                        .withPaymentMethod(SubscriptionPurchaseBuilder.PaymentMethod.INSTALLMENTS)
        );
    }

    @Step("Оплата Лайт по Клауду")
    public SubscriptionTestFlow purchaseLiteSubscription() {
        purchase(builder ->
                builder.forSubscription(TtPage.SubscriptionType.LITE)
                        .via(SubscriptionPurchaseBuilder.PaymentSystem.CLOUD)
                        .withPaymentMethod(SubscriptionPurchaseBuilder.PaymentMethod.STANDARD)
        );
        return this;
    }

    @Step("Оплата Премиум по карте Сбер")
    public SubscriptionTestFlow purchasePremiumSubscriptionSber() {
        purchase(builder ->
                builder.forSubscription(TtPage.SubscriptionType.PREMIUM)
                        .via(SubscriptionPurchaseBuilder.PaymentSystem.SBER)
                        .withPaymentMethod(SubscriptionPurchaseBuilder.PaymentMethod.APPROVED_CARD)
        );
        return this;
    }

    @Step("Оплата Лайт по карте Сбер")
    public SubscriptionTestFlow purchaseLiteSubscriptionSber() {
        purchase(builder ->
                builder.forSubscription(TtPage.SubscriptionType.LITE)
                        .via(SubscriptionPurchaseBuilder.PaymentSystem.SBER)
                        .withPaymentMethod(SubscriptionPurchaseBuilder.PaymentMethod.APPROVED_CARD)
        );
        return this;
    }

    @Step("Оплата Пожизненного по карте Сбер")
    public void purchaseLifeSubscriptionSber() {
        purchase(builder ->
                builder.forSubscription(TtPage.SubscriptionType.LIFE)
                        .via(SubscriptionPurchaseBuilder.PaymentSystem.SBER)
                        .withPaymentMethod(SubscriptionPurchaseBuilder.PaymentMethod.APPROVED_CARD)
        );
    }

    @Step("Оплата Премиум по карте Сбер с фиксированным остатком = 100р")
    public SubscriptionTestFlow purchasePremiumSubscriptionFixSber() {
        purchase(builder ->
                builder.forSubscription(TtPage.SubscriptionType.PREMIUM)
                        .via(SubscriptionPurchaseBuilder.PaymentSystem.SBER)
                        .withPaymentMethod(SubscriptionPurchaseBuilder.PaymentMethod.FIXED_BALANCE_CARD)
        );

        return this;
    }

    @Step("Ошибка 'Недостаточно средств' по карте Сбер")
    public SubscriptionTestFlow purchasePremiumSubscriptionInsufficientFundsSber() {
        purchase(builder ->
                builder.forSubscription(TtPage.SubscriptionType.PREMIUM)
                        .via(SubscriptionPurchaseBuilder.PaymentSystem.SBER)
                        .withPaymentMethod(SubscriptionPurchaseBuilder.PaymentMethod.INSUFFICIENT_FUNDS_CARD)
                        .withExpectedError(TestConfig.Texts.ERROR_MESSAGE)
        );
        this.captureScreenshot();
        return this;
    }

    @Step("Ошибка 'Невалидная карта' по карте Сбер")
    public SubscriptionTestFlow purchasePremiumSubscriptionExpireSber() {
        purchase(builder ->
                builder.forSubscription(TtPage.SubscriptionType.PREMIUM)
                        .via(SubscriptionPurchaseBuilder.PaymentSystem.SBER)
                        .withPaymentMethod(SubscriptionPurchaseBuilder.PaymentMethod.EXPIRED_CARD)
                        .withExpectedError(TestConfig.Texts.ERROR_MESSAGE)
        );
        this.captureScreenshot();
        return this;
    }

    // Методы для работы с админкой

    @Step("Рекурент по тарифу Премиум")
    public void processRecurrentInAdmin() {
        step("Проведение рекуррентного платежа через админку", () -> {
            String originalWindow = WebDriverRunner.getWebDriver().getWindowHandle();

            try {
                TabManager.openAdminInNewWindow(originalWindow);
                loginToAdmin();
                adminOrdersPage
                        .withUtmCheck(currentOrderHasAffRid)
                        .navigateToOrders()
                        .findOrderByEmail(userEmail)
                        .processRecurrentPayment();
                WaitHelper.waitForAnimationComplete();
            } finally {
                // Можно добавить закрытие окна админки если нужно
            }
        });
    }

    @Step("Рекуррент по тарифу Лайт")
    public void processRecurrentInAdminLite() {
        processRecurrentInAdmin();
        step("Проведение рекуррентного платежа Lite через админку", () -> {
            sleep(2000);
            adminOrdersPage.processRecurrentPayment();
            refresh();
            WaitHelper.waitForAnimationComplete();
            WaitHelper.waitForPageLoad();
//            AllureAttachments.attachScreenshot("Заказ найден в него перешли");
        });
    }

    @Step("Переход в Заказ по Тарифу Пожизненный доступ")
    public void processUnlimitedInAdmin() {
        step("Переход в заказ для пожизненного доступа через админку", () -> {
            String originalWindow = WebDriverRunner.getWebDriver().getWindowHandle();

            try {
                TabManager.openAdminInNewWindow(originalWindow);
                loginToAdmin();
                processRecurrentPayment();
            } finally {
                // Можно добавить закрытие окна админки если нужно
            }
        });
    }

    @Step("Авторизация в админке")
    private void loginToAdmin() {
        step("Авторизация в админке", () -> {
            WaitHelper.waitForPageLoad();
            WaitHelper.waitForAnimationComplete();
            String currentUrl = WebDriverRunner.getWebDriver().getCurrentUrl();

            if (currentUrl.contains("/auth")) {
                AdminLoginPage.login();
                AllureAttachments.attachScreenshot("Авторизация в админке успешна");
            } else {
                Allure.addAttachment("Авторизация в админке",
                        "✅ Уже авторизованы, пропускаем логин");
            }
        });
    }

    @Step("Поиск заказа для проведения рекурентного платежа")
    private void processRecurrentPayment() {
        step("Поиск заказа", () -> {
            adminOrdersPage
                    .navigateToOrders()
                    .findOrderByEmail(userEmail)
                    .processRecurrentPayment();
            WaitHelper.waitForAnimationComplete();
            WaitHelper.waitForPageLoad();
        });
    }

    // ==================== Методы для перехода в ЛК ====================

    public void logTestCompletion() {
        step("Завершение теста", () -> {
            String userEmail = adminOrdersPage.getLastSearchedEmail();
            AllureAttachments.attachText("Тест завершен",
                    "✅ Все проверки рекуррентных платежей выполнены успешно\n" +
                            "📧 Email: " + userEmail + "\n" +
                            "⏱️ Время выполнения: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        });
    }

    public void captureScreenshot() {
        AllureAttachments.attachScreenshot("Скриншот после теста");
    }
}