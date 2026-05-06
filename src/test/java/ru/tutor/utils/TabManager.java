package ru.tutor.utils;

import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import ru.tutor.constants.AddressPage;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.url;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Класс Управления вкладками от простых переходов между стр, до переходов внутри платёжных страниц/модалок банковскими данными
 */
public class TabManager {

    @Step("Получить список всех вкладок")
    private static List<String> getAllTabs() {
        return new ArrayList<>(WebDriverRunner.getWebDriver().getWindowHandles());
    }

    @Step("Переключиться на предыдущую вкладку")
    private static void switchToLastTab() {
        List<String> tabs = getAllTabs();
        WebDriverRunner.getWebDriver().switchTo().window(tabs.getLast());
    }

    @Step("Переходим к вкладке Order в админке")
    public static void goToTheOrderTab() {
        step("Переходим к вкладке Order в админке", () -> {
            closeWindow();
            switchToLastTab();
            closeWindow();
            switchToLastTab();
        });
    }

    @Step("Переключение на новое окно браузера для Sber")
    public static void switchToNewWindow() {
        String currentWindowHandle = webdriver().driver().getWebDriver().getWindowHandle();
        Set<String> windowHandles = webdriver().driver().getWebDriver().getWindowHandles();

        for (String windowHandle : windowHandles) {

            if (!windowHandle.equals(currentWindowHandle)) {
                webdriver().driver().getWebDriver().switchTo().window(windowHandle);
                AllureAttachments.attachText("Переключение окна",
                        "Переключено на окно: " + windowHandle);
                break;
            }
        }
    }

    @Step("Открытие новой вкладки для админки")
    public static void openAdminInNewWindow(String originalWindow) {
        step("Открытие админки в новом окне", () -> {
            int originalWindowCount = WebDriverRunner.getWebDriver().getWindowHandles().size();

            executeJavaScript("window.open('" + AddressPage.DEV_ADMIN + "', '_blank');");
            WaitHelper.waitForNewWindow(originalWindowCount + 1, Duration.ofSeconds(5));
            switchToNewWindow(originalWindow);
            AllureAttachments.attachScreenshot("Админка открыта");
        });
    }

    public static void switchToPaymentPageAndVerify() {
        step("Переход на страницу оплаты", ()-> {
            //Запоминаем исходную вкладку
            String originalWindow = getWebDriver().getWindowHandle();

            //Ждём открытия новой вкладки
            WaitHelper.waitForNewWindow(2, Duration.ofSeconds(15));

            //переключаемся на новую вкладку
            switchToNewWindow(originalWindow);

            // Ждём загрузки страницы
            WaitHelper.waitForPageLoad();

            // Проверяем Url
            verifyPaymentPageUrl();

            WaitHelper.waitForAnimationComplete();
            WaitHelper.waitForPageLoad();

            AllureAttachments.attachScreenshot("Страница оплаты");
        });
    }

    public static void switchToNewWindow(String originalWindow) {
        Set<String> windowHandles = getWebDriver().getWindowHandles();

        for (String windowHandle : windowHandles) {
            if (!windowHandle.equals(originalWindow)) {
                switchTo().window(windowHandle);
                AllureAttachments.attachText("Переключение окна",
                        "переключено на новое окно: " + windowHandle);
                break;
            }
        }
    }

    private static void verifyPaymentPageUrl(){
        String currentUrl = url();

        //Проверяем различные возможные платежные системы
        assert currentUrl != null;
        boolean isPaymentPage = currentUrl.contains("sber") ||
                currentUrl.contains("tinkoff") ||
                currentUrl.contains("cloudpayments") ||
                currentUrl.contains("payment") ||
                currentUrl.contains("pay") ||
                currentUrl.contains("secure");

        assertTrue(isPaymentPage,
                "Неверный URL платежной страницы. Ожидался RL платёжного провайдера, а получен: " + currentUrl);

        AllureAttachments.attachText("Проверка URL",
                "✓ URL платежной страницы корректен: " + currentUrl);
    }
}