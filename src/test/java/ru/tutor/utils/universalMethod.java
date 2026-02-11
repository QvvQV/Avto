package ru.tutor.utils;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static io.qameta.allure.Allure.step;

/**
 * Универсальные методы для тестов
 */

public class universalMethod {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    /**
     * Универсальный метод для безопасного ожидания и клика для Cloud
     */
    @Step("Клик по элементу: {elementName}")
    public static void safeClick(SelenideElement element, String elementName) {
        element
                .shouldBe(visible, DEFAULT_TIMEOUT)
                .shouldBe(enabled, DEFAULT_TIMEOUT)
                .click();
        WaitHelper.waitForAnimationComplete();
        AllureAttachments.attachText("Клик выполнен", "✓ Нажат элемент: " +  elementName);
    }

    /**
     * Универсальный метод для заполнения поля
     */
    @Step("Заполнение поля: {fieldName}")
    public static void safeSetValue(SelenideElement element, String value, String fieldName) {
        element
                .shouldBe(visible, DEFAULT_TIMEOUT)
                .shouldBe(enabled, DEFAULT_TIMEOUT)
                .setValue(value);

        AllureAttachments.attachText("Поле заполнено", "✓ Поле '" + fieldName
                + "' заполнено значением: " + (fieldName.contains("CVV") || fieldName.contains("пароль") ? "***" : value));
    }

    /**
     * Универсальный метод для клика по кнопке с проверками
     */
    @Step("Клик по кнопке: {buttonName}")
    public static void clickButton(SelenideElement button, String buttonName) {
        step("Клик по кнопке '" + buttonName + "'", () -> {
            button.scrollTo()
                    .shouldBe(visible, DEFAULT_TIMEOUT)
                    .shouldBe(interactable, DEFAULT_TIMEOUT)
                    .click();

            WaitHelper.waitForAnimationComplete();
            AllureAttachments.attachText("Кнопка нажата", "✅ " + buttonName);
        });
    }

    /**
     * Универсальный метод для клика по кнопке с ожиданием загрузки страницы
     */
    @Step("Клик по кнопке с переходом: {buttonName}")
    public static void clickButtonWithPageLoad(SelenideElement button, String buttonName) {
        step("Клик по кнопке '" + buttonName + "' с переходом", () -> {
            String originalUrl = WebDriverRunner.url();

            button.scrollTo()
                    .shouldBe(visible, DEFAULT_TIMEOUT)
                    .shouldBe(interactable, DEFAULT_TIMEOUT)
                    .click();

            WaitHelper.waitForPageLoad();
            WaitHelper.waitForAnimationComplete();

            if (!WebDriverRunner.url().equals(originalUrl)) {
                AllureAttachments.attachText("Переход",
                        "Успешный переход после клика на: " + buttonName);
            }

            AllureAttachments.attachText("Кнопка нажата", "✅ " + buttonName + " - переход выполнен");
        });
    }

    /**
     * Универсальная проверка что кнопка enabled
     */

    public static void verifyButtonIsEnabled(SelenideElement button, String buttonName) {
        step("Проверка что кнопка '" + buttonName + "' enabled", () -> {
            button.shouldBe(visible, DEFAULT_TIMEOUT)
                    .shouldNotHave(attribute("disabled"))
                    .shouldBe(interactable)
                    .shouldHave(exactText(buttonName));

            AllureAttachments.attachText("Валидация", "✅ Кнопка '" + buttonName + "' доступна для клика");
        });
    }
}
