package ru.tutor.utils;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;

import java.time.Duration;
import java.util.Objects;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.sleep;
import static io.qameta.allure.Allure.step;

/**
 * Универсальные методы для тестов
 */

public class UniversalMethod {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(20);

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
        WaitHelper.waitForPageLoad();
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
     * Универсальный метод для клика по кнопке с проверками и скроллингом до кнопки
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

            if (!Objects.equals(WebDriverRunner.url(), originalUrl)) {
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

    /**
     * Универальный метод для получепния текста элемента
     */
    public static String getElementText(SelenideElement element, String elementName) {
        String text = element.shouldBe(visible, DEFAULT_TIMEOUT).getText().trim();
        Allure.addAttachment("Текст элемента '" + elementName + "'", text);
        return text;
    }

    /**
     * Универсальный метод для получения атрибута href
     */
    @Step("Получение href элемента: {elementName}")
    public static String getElementHref(SelenideElement element, String elementName) {
        String href = element.shouldBe(visible, DEFAULT_TIMEOUT).getAttribute("href");
        Allure.addAttachment("HREF элемента '" + elementName + "'", href);
        return href;
    }

    /**
     * Клик по кнопке способа оплаты: переход, скриншот, выход из фрейма,
     * и опциональная проводка оплаты (при paymentAction != null)
     */
    public static void clickPaymentGateway(SelenideElement button, String gatewayName, Runnable paymentAction) {
        safeClick(button, "Кнопка «" + gatewayName + "»");
        sleep(2000);
        AllureAttachments.attachScreenshot("Переход по кнопке «" + gatewayName + "»");
        FrameHelper.switchToDefaultContent();
        if (paymentAction != null) {
            paymentAction.run();
        }
    }
}