package ru.tutor.utils;

import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.Wait;
import static com.codeborne.selenide.Selenide.sleep;
import static io.qameta.allure.Allure.step;

/**
 * Универсальные методы для ожидания элементов на странице
 */

public class WaitHelper {

    public static void waitForPageLoad(){
        Wait().withTimeout(Duration.ofSeconds(30)).until(driver ->
                ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));
    }

    public static void waitForUrlContains(String expectedText, Duration timeout){
        WebDriverWait wait = new WebDriverWait(WebDriverRunner.getWebDriver(), timeout);
        wait.until(driver -> driver.getCurrentUrl().contains(expectedText));
    }

    public static void waitForPaymentProcessing(){
        waitForUrlContains("/success/", Duration.ofSeconds(20));
    }

    // Утилитарные методы
    public static void waitFor(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Ожидание для анимаций и модальных окон
    public static void waitForAnimationComplete() {
        step("Ожидание завершения анимации", ()->{
            sleep(1500); //Базовая задержка для анимаций
            Wait().withTimeout(Duration.ofSeconds(10)).until(driver -> {
                try {
                    return (Boolean) ((JavascriptExecutor) driver)
                            .executeScript("return jQuery(':animated').length === 0");
                } catch (Exception e) {
                    // jQuery не доступен, считаем что анимации завершены
                    return true;
                }
            });
        });
    }

    public static void waitForModalToClose() {
        step("Ожидание закрытия модального окна", ()->{
            waitForAnimationComplete();

            // Проверяем что основные модальные элементы исчезли
            String script = """
                const modals = document.querySelectorAll('[role="dialog"], .modal, [class*="modal"]');
                const visibleModals = Array.from(modals).filter(modal => {
                    const style = window.getComputedStyle(modal);
                    return style.display !== 'none' && modal.offsetParent !== null;
                });
                return visibleModals.length === 0;
            """;

            Wait().withTimeout(Duration.ofSeconds(10)).until(driver -> (Boolean) ((JavascriptExecutor) driver).executeScript(script));
        });
    }

    @Step("Ожидание открытия нового окна/вкладки")
    public static void waitForNewWindow(int expectedWindowsCount, Duration timeout) {
        step("Ожидание открытия новой вкладки", ()->{
            WebDriverWait wait = new WebDriverWait(WebDriverRunner.getWebDriver(), timeout);
            wait.until(driver -> driver.getWindowHandles().size() >= expectedWindowsCount);

            AllureAttachments.attachText("Окна браузера",
                    "Количество открытых вкладок: " + WebDriverRunner.getWebDriver().getWindowHandles().size());
        });
    }

    @Step("Ожидание конкретного URL")
    public static void waitForUrlPattern(String pattern, Duration timeout) {
        WebDriverWait wait = new WebDriverWait(WebDriverRunner.getWebDriver(), timeout);
        wait.until(driver -> driver.getCurrentUrl().contains(pattern));
    }
}


