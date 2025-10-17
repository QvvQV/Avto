package ru.tutor.utils;

import java.time.Duration;
import java.util.Set;

import static com.codeborne.selenide.Selenide.switchTo;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.url;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PaymentPageHelper {

    public static void switchToPaymentPageAndVerify() {
        step("Переход на страницу оплаты", ()-> {
            //Запоминаем исходную вкладку
            String originalWindow = getWebDriver().getWindowHandle();

            //Ждём открытия новой вкладки
            WaitHelper.waitForNewWindow(2, Duration.ofSeconds(15));

            //переключаемся на новую вкладку
            switchToNewWindow(originalWindow);

            // Ждём загрузки старницы
            WaitHelper.waitForPageLoad();

            // Проверяем Url
            verifyPaymentpageUrl();

            WaitHelper.waitForPageLoad();

            AllureAttachments.attachScreenshot("Страница оплаты");
        });
    }

    private static void switchToNewWindow(String originalWindow) {
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

    private static void verifyPaymentpageUrl(){
        String currentUrl = url();

        //Проверяем различные возможные платежные системы
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
