package ru.tutor.flow;


import com.codeborne.selenide.WebDriverRunner;
import ru.tutor.config.TestConfig;
import ru.tutor.flow.builder.SubscriptionPurchaseBuilder;
import ru.tutor.flow.builder.SubscriptionPurchaseResult;
import ru.tutor.page.CloudPage;
import ru.tutor.page.SberPage;
import ru.tutor.page.addressPage;
import ru.tutor.page.admin.AdminLoginPage;
import ru.tutor.page.admin.AdminOrdersPage;
import ru.tutor.page.ttPage;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WaitHelper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubscriptionTestFlow {

    private String userEmail;
    private final ttPage ttPage;
    private final CloudPage cloudPage;
    private final SberPage sberPage;
    private final AdminOrdersPage adminOrdersPage;

    public SubscriptionTestFlow(ttPage ttPage, CloudPage cloudPage, SberPage sberPage, AdminOrdersPage adminOrdersPage) {
        this.ttPage = ttPage;
        this.cloudPage = cloudPage;
        this.sberPage = sberPage;
        this.adminOrdersPage = adminOrdersPage;
    }

    // Основной метод для создания покупок через Builder
    private SubscriptionPurchaseResult purchaseSubscription(SubscriptionPurchaseBuilder builder) {
        SubscriptionPurchaseResult result = builder.execute();
        this.userEmail = result.getUserEmail();
        return result;
    }

    // Короткие методы-обертки для различных сценариев покупки
    public SubscriptionTestFlow purchasePremiumSubscription() {
        purchaseSubscription(
                SubscriptionPurchaseBuilder.create(ttPage, cloudPage, sberPage)
                        .forPremium()
                        .viaCloud()
                        .withStandardPayment()
        );
        return this;
    }

    public SubscriptionTestFlow purchaseLifeSubscription() {
        purchaseSubscription(
                SubscriptionPurchaseBuilder.create(ttPage, cloudPage, sberPage)
                        .forLife()
                        .viaCloud()
                        .withStandardPayment()
        );
        return this;
    }

    public SubscriptionTestFlow purchaseLifeInstallmentsSubscription() {
        purchaseSubscription(
                SubscriptionPurchaseBuilder.create(ttPage, cloudPage, sberPage)
                        .forLife()
                        .viaCloud()
                        .withInstallments()
        );
        return this;
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

    public SubscriptionTestFlow purchasePremiumSubscriptionSber() {
        purchaseSubscription(
                SubscriptionPurchaseBuilder.create(ttPage, cloudPage, sberPage)
                        .forPremium()
                        .viaSber()
                        .withApprovedCard()
        );
        return this;
    }

    public SubscriptionTestFlow purchaseLiteSubscriptionSber() {
        purchaseSubscription(
                SubscriptionPurchaseBuilder.create(ttPage, cloudPage, sberPage)
                        .forLite()
                        .viaSber()
                        .withApprovedCard()
        );
        return this;
    }

    public SubscriptionTestFlow purchaseLifeSubscriptionSber() {
        purchaseSubscription(
                SubscriptionPurchaseBuilder.create(ttPage, cloudPage, sberPage)
                        .forLife()
                        .viaSber()
                        .withApprovedCard()
        );
        return this;
    }

    public SubscriptionTestFlow purchasePremiumSubscriptionFixSber() {
        purchaseSubscription(
                SubscriptionPurchaseBuilder.create(ttPage, cloudPage, sberPage)
                        .forPremium()
                        .viaSber()
                        .withFixedBalanceCard()
        );
        return this;
    }

    public SubscriptionTestFlow purchasePremiumSubscriptionInsufficientFundsSber() {
        purchaseSubscription(
                SubscriptionPurchaseBuilder.create(ttPage, cloudPage, sberPage)
                        .forPremium()
                        .viaSber()
                        .withInsufficientFundsCard()
                        .withExpectedError(TestConfig.Texts.ERROR_MESSAGE)
        );
        return this.captureScreenshot();
    }

    public SubscriptionTestFlow purchasePremiumSubscriptionExpireSber() {
        purchaseSubscription(
                SubscriptionPurchaseBuilder.create(ttPage, cloudPage, sberPage)
                        .forPremium()
                        .viaSber()
                        .withExpiredCard()
                        .withExpectedError(TestConfig.Texts.ERROR_MESSAGE)
        );
        return this.captureScreenshot();
    }

    // Методы для работы с админкой (без изменений)
    public SubscriptionTestFlow processRecurrentInAdmin() {
        step("Проведение рекуррентного платежа через админку", () -> {
            String originalWindow = WebDriverRunner.getWebDriver().getWindowHandle();

            try {
                openAdminInNewWindow(originalWindow);
                loginToAdmin();
                processRecurrentPayment();
                WaitHelper.waitForAnimationComplete();
            } finally {
                // Можно добавить закрытие окна админки если нужно
            }
        });
        return this;
    }

    public SubscriptionTestFlow processRecurrentInAdminLite() {
        processRecurrentInAdmin();
        step("Проведение рекуррентного платежа Lite через админку", () -> {
            sleep(2000);
            adminOrdersPage.processRecurrentPayment();
            refresh();
            WaitHelper.waitForAnimationComplete();
            WaitHelper.waitForPageLoad();
            AllureAttachments.attachScreenshot("Заказ найден в него перешли");
        });
        return this;
    }

    private void openAdminInNewWindow(String originalWindow) {
        step("Открытие админки в новом окне", () -> {
            int originalWindowCount = WebDriverRunner.getWebDriver().getWindowHandles().size();

            executeJavaScript("window.open('" + addressPage.devAdmin + "', '_blank');");
            WaitHelper.waitForNewWindow(originalWindowCount + 1, Duration.ofSeconds(5));
            switchToNewWindow(originalWindow);
            AllureAttachments.attachScreenshot("Админка открыта");
        });
    }

    private void loginToAdmin() {
        step("Авторизация в админке", () -> {
            AdminLoginPage.login();
            AllureAttachments.attachScreenshot("Авторизация в админке успешна");
        });
    }

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

    private void switchToNewWindow(String originalWindow) {
        for (String windowHandle : WebDriverRunner.getWebDriver().getWindowHandles()) {
            if (!windowHandle.equals(originalWindow)) {
                switchTo().window(windowHandle);
                WaitHelper.waitForPageLoad();
                break;
            }
        }
    }

    public void logTestCompletion() {
        step("Завершение теста", () -> {
            AllureAttachments.attachText("Тест завершен",
                    "✅ Все проверки рекуррентных платежей выполнены успешно\n" +
                            "📧 Email: " + userEmail + "\n" +
                            "⏱️ Время выполнения: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        });
    }

    public String getUserEmail() {
        return userEmail;
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

    public SubscriptionTestFlow captureScreenshot() {
        AllureAttachments.attachScreenshot("Скриншот после теста");
        return this;
    }
}