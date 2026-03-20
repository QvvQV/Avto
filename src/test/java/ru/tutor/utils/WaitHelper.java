package ru.tutor.utils;

import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Objects;
import java.util.function.BooleanSupplier;

import static com.codeborne.selenide.Selenide.Wait;
import static com.codeborne.selenide.Selenide.sleep;
import static com.codeborne.selenide.WebDriverRunner.*;
import static io.qameta.allure.Allure.step;

/**
 * Универсальные методы для ожидания элементов на странице
 */

public class WaitHelper {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);

    public static void waitForPageLoad() {
        Wait().withTimeout(DEFAULT_TIMEOUT).until(driver ->
                Objects.equals(((JavascriptExecutor) driver).executeScript("return document.readyState"), "complete"));
    }

    public static void waitForUrlContains(String expectedText, Duration timeout) {
        Wait().withTimeout(timeout).until(driver -> Objects.requireNonNull(url()).contains(expectedText));
    }

    public static void waitForPaymentProcessing() {
        waitForUrlContains("/success/", DEFAULT_TIMEOUT);
    }

    // Утилитарные методы
    public static void waitFor(BooleanSupplier condition, Duration timeout, String message) {
        long startTime = System.currentTimeMillis();
        long endTime = startTime + timeout.toMillis();

        while (System.currentTimeMillis() < endTime) {
            if (condition.getAsBoolean()) {
                return;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Ожидание прервано", e);
            }
        }
        throw new AssertionError(message + " (таймаут " + timeout.getSeconds() + " с)");
    }

    // Ожидание для анимаций и модальных окон
    public static void waitForAnimationComplete() {
        step("Ожидание завершения анимации", () -> {
            sleep(1500); //Базовая задержка для анимаций
            Wait().withTimeout(DEFAULT_TIMEOUT).until(driver -> {
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
        step("Ожидание закрытия модального окна", () -> {
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

            Wait().withTimeout(DEFAULT_TIMEOUT).until(driver -> (Boolean) ((JavascriptExecutor) driver).executeScript(script));
        });
    }

    @Step("Ожидание открытия нового окна/вкладки")
    public static void waitForNewWindow(int expectedWindowsCount, Duration timeout) {
        step("Ожидание открытия новой вкладки", () -> {
            Wait()
                    .withTimeout(timeout)
                    .pollingEvery(DEFAULT_TIMEOUT)
                    .until(driver -> getWebDriver().getWindowHandles().size() >= expectedWindowsCount);


            AllureAttachments.attachText("Окна браузера",
                    "Количество открытых вкладок: " + getWebDriver().getWindowHandles().size());
        });
    }

    @Step("Ожидание конкретного URL")
    public static void waitForUrlPattern(String pattern, Duration timeout) {
        WebDriverWait wait = new WebDriverWait(WebDriverRunner.getWebDriver(), timeout);
        wait.until(driver -> Objects.requireNonNull(driver.getCurrentUrl()).contains(pattern));
    }
}


