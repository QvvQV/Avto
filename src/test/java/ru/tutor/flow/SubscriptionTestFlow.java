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
            } finally {
                // Можно добавить закрытие окна админки если нужно
            }
        });
        return this;
    }

    private void openAdminInNewWindow(String originalWindow) {
        step("Открытие админки в новом окне", () -> {
            executeJavaScript("window.open('" + addressPage.devAdmin + "', '_blank');");
            WaitHelper.waitForNewWindow(2, Duration.ofSeconds(5));
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
            AllureAttachments.attachScreenshot("Заказ найден в него перешли");
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
//import com.codeborne.selenide.WebDriverRunner;
//import ru.tutor.config.TestConfig;
//import ru.tutor.page.CloudPage;
//import ru.tutor.page.SberPage;
//import ru.tutor.page.addressPage;
//import ru.tutor.page.admin.AdminLoginPage;
//import ru.tutor.page.admin.AdminOrdersPage;
//import ru.tutor.page.ttPage;
//import ru.tutor.utils.AllureAttachments;
//import ru.tutor.utils.WaitHelper;
//
//import java.time.Duration;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
//import static com.codeborne.selenide.Selenide.*;
//import static io.qameta.allure.Allure.step;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//public class SubscriptionTestFlow {
//
//    private String userEmail;
//    private final ttPage ttPage;
//    private final CloudPage cloudPage;
//    private final AdminOrdersPage adminOrdersPage;
//    private final SberPage sberPage;
//
//    public SubscriptionTestFlow(ttPage ttPage, CloudPage cloudPage, SberPage sberPage, AdminOrdersPage adminOrdersPage) {
//        this.ttPage = ttPage;
//        this.cloudPage = cloudPage;
//        this.adminOrdersPage = adminOrdersPage;
//        this.sberPage = sberPage;
//    }
//
//    public SubscriptionTestFlow purchasePremiumSubscription() {
//        step("Оформление и оплата Premium подписки Cloud", () -> {
//            step("Открытие страницы", () -> {
//                open(addressPage.urlMain);
//            });
//            step("Проверка заголовка главной страницы", () -> {
//                assertEquals(TestConfig.Texts.MAIN_PAGE_TITLE, title());
//                AllureAttachments.attachText("Проверка страницы", "Главная страница загружена корректно");
//            });
//
//            ttPage.clickBtnOpenFor();
//            ttPage.clickPremium();
//
//            CloudPage.setDateModal();
//
//            userEmail = CloudPage.atrEmail();
//            AllureAttachments.attachText("Email пользователя", userEmail);
//
//            CloudPage.clickCardPageBtn();
//        });
//        return this;
//    }
//
//    public SubscriptionTestFlow purchaseLifeSubscription() {
//        step("Оформление и оплата Life подписки Cloud", () -> {
//            step("Открытие страницы", () -> {
//                open(addressPage.urlMain);
//            });
//            step("Проверка заголовка главной страницы", () -> {
//                assertEquals(TestConfig.Texts.MAIN_PAGE_TITLE, title());
//                AllureAttachments.attachText("Проверка страницы", "Главная страница загружена корректно");
//            });
//
//            ttPage.clickBtnOpenFor();
//            ttPage.clickLife();
//            CloudPage.setDateModal();
//
//            userEmail = CloudPage.atrEmail();
//            AllureAttachments.attachText("Email пользователя", userEmail);
//
//            CloudPage.clickCardPageBtn();
//
//        });
//        return this;
//    }
//
//    public SubscriptionTestFlow purchaseLifeInstallmentsSubscription() {
//        step("Оформление и оплата Life подписки Cloud", () -> {
//            step("Открытие страницы", () -> {
//                open(addressPage.urlMain);
//            });
//            step("Проверка заголовка главной страницы", () -> {
//                assertEquals(TestConfig.Texts.MAIN_PAGE_TITLE, title());
//                AllureAttachments.attachText("Проверка страницы", "Главная страница загружена корректно");
//            });
//
//            ttPage.clickBtnOpenFor();
//            ttPage.clickLife();
//            CloudPage.setDateModalInstallments();
//
//            userEmail = CloudPage.atrEmail();
//            AllureAttachments.attachText("Email пользователя", userEmail);
//
//            CloudPage.clickCardPageBtn();
//        });
//        return this;
//    }
//
//    public SubscriptionTestFlow purchaseLiteSubscription() {
//        step("Оформление и оплата Lite подписки Cloud", () -> {
//            step("Открытие страницы", () -> {
//                open(addressPage.urlMain);
//            });
//            step("Проверка заголовка главной страницы", () -> {
//                assertEquals(TestConfig.Texts.MAIN_PAGE_TITLE, title());
//                AllureAttachments.attachText("Проверка страницы", "Главная страница загружена корректно");
//            });
//
//            ttPage.clickBtnOpenFor();
//            ttPage.clickLite99();
//            CloudPage.setDateModal();
//
//            userEmail = CloudPage.atrEmail();
//            AllureAttachments.attachText("Email пользователя", userEmail);
//
//            CloudPage.clickCardPageBtn();
//        });
//        return this;
//    }
//
//    public SubscriptionTestFlow purchasePremiumSubscriptionSber() {
//        step("Оформление и оплата Premium подписки Sber", () -> {
//            step("Открытие страницы", () -> {
//                open(addressPage.urlMain);
//            });
//            step("Проверка заголовка главной страницы", () -> {
//                assertEquals(TestConfig.Texts.MAIN_PAGE_TITLE, title());
//                AllureAttachments.attachText("Проверка страницы", "Главная страница загружена корректно");
//            });
//
//            ttPage.clickBtnOpenFor();
//            ttPage.clickPremium();
//
//            SberPage.fillEmailAndProcessModal();
//
//
//            userEmail = SberPage.getLastUsedEmail();
//            AllureAttachments.attachText("Email пользователя", userEmail);
//
//            SberPage.switchToNewWindow();
//            SberPage.payWithApprovedCard();
//
//        });
//        return this;
//    }
//
//    public SubscriptionTestFlow purchaseLiteSubscriptionSber() {
//        step("Оформление и оплата Lite подписки Sber", () -> {
//            step("Открытие страницы", () -> {
//                open(addressPage.urlMain);
//            });
//            step("Проверка заголовка главной страницы", () -> {
//                assertEquals(TestConfig.Texts.MAIN_PAGE_TITLE, title());
//                AllureAttachments.attachText("Проверка страницы", "Главная страница загружена корректно");
//            });
//
//            ttPage.clickBtnOpenFor();
//            ttPage.clickLite99();
//
//            SberPage.fillEmailAndProcessModal();
//
//
//            userEmail = SberPage.getLastUsedEmail();
//            AllureAttachments.attachText("Email пользователя", userEmail);
//
//            SberPage.switchToNewWindow();
//            SberPage.payWithApprovedCard();
//
//        });
//        return this;
//    }
//
//    public SubscriptionTestFlow purchaseLifeSubscriptionSber() {
//        step("Оформление и оплата Life подписки Sber", () -> {
//            step("Открытие страницы", () -> {
//                open(addressPage.urlMain);
//            });
//            step("Проверка заголовка главной страницы", () -> {
//                assertEquals(TestConfig.Texts.MAIN_PAGE_TITLE, title());
//                AllureAttachments.attachText("Проверка страницы", "Главная страница загружена корректно");
//            });
//
//            ttPage.clickBtnOpenFor();
//            ttPage.clickLife();
//
//            SberPage.fillEmailAndProcessModal();
//
//            userEmail = SberPage.getLastUsedEmail();
//            AllureAttachments.attachText("Email пользователя", userEmail);
//
//            SberPage.switchToNewWindow();
//            SberPage.payWithApprovedCard();
//        });
//        return this;
//    }
//
//    public SubscriptionTestFlow purchasePremiumSubscriptionFixSber() {
//        step("Оформление и оплата Premium подписки Sber с фиксированным остатком", () -> {
//            step("Открытие страницы", () -> {
//                open(addressPage.urlMain);
//            });
//            step("Проверка заголовка главной страницы", () -> {
//                assertEquals(TestConfig.Texts.MAIN_PAGE_TITLE, title());
//                AllureAttachments.attachText("Проверка страницы", "Главная страница загружена корректно");
//            });
//
//            ttPage.clickBtnOpenFor();
//            ttPage.clickPremium();
//
//            SberPage.fillEmailAndProcessModal();
//
//
//            userEmail = SberPage.getLastUsedEmail();
//            AllureAttachments.attachText("Email пользователя", userEmail);
//
//            SberPage.switchToNewWindow();
//            SberPage.payWithFixedBalanceCard();
//
//        });
//        return this;
//    }
//
//    public SubscriptionTestFlow purchasePremiumSubscriptionInsufficientFundsSber() {
//        step("Оформление и оплата Premium подписки Sber с недостаточно средств", () -> {
//            step("Открытие страницы", () -> {
//                open(addressPage.urlMain);
//            });
//            step("Проверка заголовка главной страницы", () -> {
//                assertEquals(TestConfig.Texts.MAIN_PAGE_TITLE, title());
//                AllureAttachments.attachText("Проверка страницы", "Главная страница загружена корректно");
//            });
//
//            ttPage.clickBtnOpenFor();
//            ttPage.clickPremium();
//
//            SberPage.fillEmailAndProcessModal();
//
//
//            userEmail = SberPage.getLastUsedEmail();
//            AllureAttachments.attachText("Email пользователя", userEmail);
//
//            SberPage.switchToNewWindow();
//            SberPage.payWithInsufficientFundsCard();
//            virifyErrorMessage(TestConfig.Texts.ERROR_MESSAGE);
//            captureScreenshot();
//
//        });
//        return this;
//    }
//
//    public SubscriptionTestFlow purchasePremiumSubscriptionExpireSber() {
//                step("Оформление и оплата Premium подписки Sber с просроченной карты", () -> {
//            step("Открытие страницы", () -> {
//                open(addressPage.urlMain);
//            });
//            step("Проверка заголовка главной страницы", () -> {
//                assertEquals(TestConfig.Texts.MAIN_PAGE_TITLE, title());
//                AllureAttachments.attachText("Проверка страницы", "Главная страница загружена корректно");
//            });
//
//            ttPage.clickBtnOpenFor();
//            ttPage.clickPremium();
//
//            SberPage.fillEmailAndProcessModal();
//
//
//            userEmail = SberPage.getLastUsedEmail();
//            AllureAttachments.attachText("Email пользователя", userEmail);
//
//            SberPage.switchToNewWindow();
//            SberPage.payWithExpiredCard();
//            virifyErrorMessage(TestConfig.Texts.ERROR_MESSAGE);
//            captureScreenshot();
//
//        });
//        return this;
//    }
//
//
//    public SubscriptionTestFlow processRecurrentInAdmin() {
//        step("Проведение рекуррентного платежа через админку", () -> {
//            String originalWindow = WebDriverRunner.getWebDriver().getWindowHandle();
//
//            try {
//                openAdminInNewWindow(originalWindow);
//                loginToAdmin();
//                processRecurrentPayment();
//            } finally {
//                // Можно добавить закрытие окна админки если нужно
//            }
//        });
//        return this;
//    }
//
//    private void openAdminInNewWindow(String originalWindow) {
//        step("Открытие админки в новом окне", () -> {
//            executeJavaScript("window.open('" + addressPage.devAdmin + "', '_blank');");
//            WaitHelper.waitForNewWindow(2, Duration.ofSeconds(5));
//            switchToNewWindow(originalWindow);
//            AllureAttachments.attachScreenshot("Админка открыта");
//        });
//    }
//
//    private void loginToAdmin() {
//        step("Авторизация в админке", () -> {
//            AdminLoginPage.login();
//            AllureAttachments.attachScreenshot("Авторизация в админке успешна");
//        });
//    }
//
//    private void processRecurrentPayment() {
//        step("Поиск заказа", () -> {
//            adminOrdersPage
//                    .navigateToOrders()
//                    .findOrderByEmail(userEmail)
//                    .processRecurrentPayment();
//            WaitHelper.waitForAnimationComplete();
//            AllureAttachments.attachScreenshot("Заказ найден в него перешли");
//        });
//    }
//
//    private void switchToNewWindow(String originalWindow) {
//        for (String windowHandle : WebDriverRunner.getWebDriver().getWindowHandles()) {
//            if (!windowHandle.equals(originalWindow)) {
//                switchTo().window(windowHandle);
//                WaitHelper.waitForPageLoad();
//                break;
//            }
//        }
//    }
//
//    public void logTestCompletion() {
//        step("Завершение теста", () -> {
//            AllureAttachments.attachText("Тест завершен",
//                    "✅ Все проверки рекуррентных платежей выполнены успешно\n" +
//                            "📧 Email: " + userEmail + "\n" +
//                            "⏱️ Время выполнения: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
//        });
//    }
//
//    public String getUserEmail() {
//        return userEmail;
//    }
//
//    public SubscriptionTestFlow virifyErrorMessage(String expectedError) {
//        step("Проверка сообщения об ошибке", () -> {
//            String actualError = SberPage.getErrorText();
//            assertEquals(expectedError, actualError);
//            AllureAttachments.attachText("Проверка ошибки",
//                    "Ожидалось: " + expectedError + "\nФактически: " + actualError);
//        });
//        return this;
//    }
//
//    public SubscriptionTestFlow captureScreenshot() {
//        AllureAttachments.attachScreenshot("Скриншот после теста");
//        return this;
//    }
//}