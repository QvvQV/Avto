package ru.tutor.flow;

import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import ru.tutor.config.TestConfig;
import ru.tutor.flow.builder.SubscriptionPurchaseBuilder;
import ru.tutor.flow.builder.SubscriptionPurchaseResult;
import ru.tutor.page.CloudPage;
import ru.tutor.page.SberPage;
import ru.tutor.page.admin.AdminLoginPage;
import ru.tutor.page.admin.AdminOrdersPage;
import ru.tutor.page.TtPage;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.TabManager;
import ru.tutor.utils.WaitHelper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubscriptionTestFlow {

    private String userEmail;
    private final TtPage ttPage;
    private final CloudPage cloudPage;
    private final SberPage sberPage;
    private final AdminOrdersPage adminOrdersPage;

    public SubscriptionTestFlow(TtPage ttPage, CloudPage cloudPage, SberPage sberPage, AdminOrdersPage adminOrdersPage) {
        this.ttPage = ttPage;
        this.cloudPage = cloudPage;
        this.sberPage = sberPage;
        this.adminOrdersPage = adminOrdersPage;
    }

    // Основной метод для создания покупок через Builder
    private SubscriptionPurchaseResult purchaseSubscription(SubscriptionPurchaseBuilder builder) {
        SubscriptionPurchaseResult result = builder.execute();
        this.userEmail = result.userEmail();
        return result;
    }

    private SubscriptionPurchaseResult purchaseSubscriptionAffRid(SubscriptionPurchaseBuilder builder) {
        SubscriptionPurchaseResult result = builder.executeAffRid();
        this.userEmail = result.userEmail();
        return result;
    }

    // Короткие методы-обертки для различных сценариев покупки
    public SubscriptionTestFlow purchasePremiumSubscription() {
        SubscriptionPurchaseResult result = purchaseSubscription(
                SubscriptionPurchaseBuilder.create(ttPage, cloudPage, sberPage)
                        .forPremium()
                        .viaCloud()
                        .withStandardPayment()
        );
        this.userEmail = result.userEmail();
        return this;
    }

    // Оплата для aff_rid ссылок

    public SubscriptionTestFlow purchasePremiumSubscriptionAffRid() {
        SubscriptionPurchaseResult result = purchaseSubscriptionAffRid(
                SubscriptionPurchaseBuilder.create(ttPage, cloudPage, sberPage)
                        .forPremium()
                        .viaCloud()
                        .withStandardPayment()
        );
        this.userEmail = result.userEmail();
        return this;
    }

    public void purchaseLifeSubscription() {
        purchaseSubscription(
                SubscriptionPurchaseBuilder.create(ttPage, cloudPage, sberPage)
                        .forLife()
                        .viaCloud()
                        .withStandardPayment()
        );
    }

    public void purchaseLifeInstallmentsSubscription() {
        purchaseSubscription(
                SubscriptionPurchaseBuilder.create(ttPage, cloudPage, sberPage)
                        .forLife()
                        .viaCloud()
                        .withInstallments()
        );
    }

    public SubscriptionTestFlow purchaseLiteSubscription() {
        purchaseSubscription(
                SubscriptionPurchaseBuilder.create(ttPage, cloudPage, sberPage)
                        .forLite()
                        .viaCloud()
                        .withStandardPayment()
        );
        return this;
    }

    @Step("Оплата Премиум по карте Сбер")
    public SubscriptionTestFlow purchasePremiumSubscriptionSber() {
        purchaseSubscription(
                SubscriptionPurchaseBuilder.create(ttPage, cloudPage, sberPage)
                        .forPremium()
                        .viaSber()
                        .withApprovedCard()
        );
        return this;
    }

    @Step("Оплата Лайт по карте Сбер")
    public SubscriptionTestFlow purchaseLiteSubscriptionSber() {
        purchaseSubscription(
                SubscriptionPurchaseBuilder.create(ttPage, cloudPage, sberPage)
                        .forLite()
                        .viaSber()
                        .withApprovedCard()
        );
        return this;
    }

    @Step("Оплата Пожизненного по карте Сбер")
    public void purchaseLifeSubscriptionSber() {
        purchaseSubscription(
                SubscriptionPurchaseBuilder.create(ttPage, cloudPage, sberPage)
                        .forLife()
                        .viaSber()
                        .withApprovedCard()
        );
    }

    @Step("Оплата Премиум по карте Сбер с фиксированным остатком = 100р")
    public void purchasePremiumSubscriptionFixSber() {
        purchaseSubscription(
                SubscriptionPurchaseBuilder.create(ttPage, cloudPage, sberPage)
                        .forPremium()
                        .viaSber()
                        .withFixedBalanceCard()
        );
    }

    @Step("Ошибка 'Недостаточно средств' по карте Сбер")
    public void purchasePremiumSubscriptionInsufficientFundsSber() {
        purchaseSubscription(
                SubscriptionPurchaseBuilder.create(ttPage, cloudPage, sberPage)
                        .forPremium()
                        .viaSber()
                        .withInsufficientFundsCard()
                        .withExpectedError(TestConfig.Texts.ERROR_MESSAGE)
        );
        this.captureScreenshot();
    }

    @Step("Ошибка 'Невалидная карта' по карте Сбер")
    public void purchasePremiumSubscriptionExpireSber() {
        purchaseSubscription(
                SubscriptionPurchaseBuilder.create(ttPage, cloudPage, sberPage)
                        .forPremium()
                        .viaSber()
                        .withExpiredCard()
                        .withExpectedError(TestConfig.Texts.ERROR_MESSAGE)
        );
        this.captureScreenshot();
    }

    // Методы для работы с админкой

    @Step("Рекурент по тарифу Премиум")
    public void processRecurrentInAdmin() {
        step("Проведение рекуррентного платежа через админку", () -> {
            String originalWindow = WebDriverRunner.getWebDriver().getWindowHandle();

            try {
                TabManager.openAdminInNewWindow(originalWindow);
                loginToAdmin();
                processRecurrentPayment();
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
                adminOrdersPage
                        .navigateToOrders()
                        .findOrderByEmail(userEmail)
                        .processRecurrentPayment();
                WaitHelper.waitForAnimationComplete();
                WaitHelper.waitForPageLoad();
            } finally {
                // Можно добавить закрытие окна админки если нужно
            }
        });
    }

    @Step("Авторизация в админке")
    private void loginToAdmin() {
        step("Авторизация в админке", () -> {
            AdminLoginPage.login();
            AllureAttachments.attachScreenshot("Авторизация в админке успешна");
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
//            AllureAttachments.attachScreenshot("Заказ найден в него перешли");
        });
    }

    public void logTestCompletion() {
        step("Завершение теста", () -> {
            String userEmail = adminOrdersPage.getLastSearchedEmail();
            AllureAttachments.attachText("Тест завершен",
                    "✅ Все проверки рекуррентных платежей выполнены успешно\n" +
                            "📧 Email: " + userEmail + "\n" +
                            "⏱️ Время выполнения: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        });
    }

    public SubscriptionTestFlow verifyErrorMessage(String expectedError) {
        step("Проверка сообщения об ошибке", () -> {
            String actualError = sberPage.getErrorText();
            assertEquals(expectedError, actualError);
            AllureAttachments.attachText("Проверка ошибки",
                    "Ожидалось: " + expectedError + "\nФактически: " + actualError);
        });
        return this;
    }

    public void captureScreenshot() {
        AllureAttachments.attachScreenshot("Скриншот после теста");
    }
}