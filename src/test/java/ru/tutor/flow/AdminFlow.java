package ru.tutor.flow;

import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import ru.tutor.page.admin.AdminLoginPage;
import ru.tutor.page.admin.AdminOrdersPage;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.TabManager;
import ru.tutor.utils.WaitHelper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Работа с админкой (поиск заказа, рекурренты, отписки)
 */
public class AdminFlow {

    private final AdminOrdersPage adminOrdersPage;
    private final AdminLoginPage adminLoginPage = new AdminLoginPage();
    private boolean currentOrderHasAffRid = false;

    public AdminFlow(AdminOrdersPage adminOrdersPage) {
        this.adminOrdersPage = adminOrdersPage;
    }

    public AdminFlow withUtmCheck(boolean check) {
        this.currentOrderHasAffRid = check;
        return this;
    }

    @Step("Открытие админки и авторизация")
    public void openAndLogin() {
        String originalWindow = WebDriverRunner.getWebDriver().getWindowHandle();
        try {
            TabManager.openAdminInNewWindow(originalWindow);
            login();
        } finally {
            switchTo().window(originalWindow);
        }
    }

    public void login() {
        step("Авторизация в админке", () -> {
            WaitHelper.waitForPageLoad();
            WaitHelper.waitForAnimationComplete();
            String currentUrl = WebDriverRunner.getWebDriver().getCurrentUrl();
            if( currentUrl.contains("/auth")) {
                adminLoginPage.login();
                AllureAttachments.attachScreenshot("Авторизация в админке успешна");
            } else {
                Allure.addAttachment("Авторизация в админке",
                        "Уже авторизованы, пропускаем логин");
            }
        });
    }

    @Step("Поиск заказа по email: {email} ")
    public String findOrder(String email) {
        adminOrdersPage
                .navigateToOrders()
                .findOrderByEmail(email);
        return adminOrdersPage.getLastOrderNumber();
    }

    @Step("Проверка существования заказа в админке")
    public void assertOrderExists(String email) {
        String originalWindow = WebDriverRunner.getWebDriver().getWindowHandle();
        try {
            TabManager.openAdminInNewWindow(originalWindow);
            login();
            String orderNumber = findOrder(email);
            assertNotNull(orderNumber, "Заказ для email " + email + " должен существовать в админке");
            AllureAttachments.attachText("Заказ найден", "Номер заказа: " + orderNumber);
        } finally {
            closeWindow();
        }
    }

    @Step("Реккурент по тарифу Премиум")
    public void processRecurrent(String email) {
        String originalWindow = WebDriverRunner.getWebDriver().getWindowHandle();
        try {
            TabManager.openAdminInNewWindow(originalWindow);
            login();
            adminOrdersPage
                    .withUtmCheck(currentOrderHasAffRid)
                    .navigateToOrders()
                    .findOrderByEmail(email)
                    .processRecurrentPayment();
            WaitHelper.waitForAnimationComplete();
        } finally {
            // можно добавить закрытие окна
        }
    }

    @Step("Реккурент по тарифу Лайт")
    public void processRecurrentLite(String email) {
        processRecurrent(email);
        step("Проведение рекуррентного платежа Lite через админку", () -> {
            WaitHelper.waitForWebhookProcessed();
            adminOrdersPage.processRecurrentPayment();
            refresh();
            WaitHelper.waitForAnimationComplete();
            WaitHelper.waitForPageLoad();
        });
    }

    @Step("Переход в заказ для Пожизненного доступа")
    public void processUnlimited(String email) {
        String originalWindow = WebDriverRunner.getWebDriver().getWindowHandle();
        try {
            TabManager.openAdminInNewWindow(originalWindow);
            login();
            adminOrdersPage
                    .navigateToOrders()
                    .findOrderByEmail(email)
                    .processRecurrentPayment();
            WaitHelper.waitForAnimationComplete();
            WaitHelper.waitForPageLoad();
        } finally {
            // можно добавить закрытие окна
        }
    }

    @Step("Завершение теста")
    public void logCompletion() {
        step("Завершение теста", () -> {
            String email = adminOrdersPage.getLastSearchedEmail();
            AllureAttachments.attachText("Тест завершен",
                    "Все проверки рекуррентных платежей выполнены успешно\n" +
                    "Email: " + email + "Время выполнения: " + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        });
    }
}
