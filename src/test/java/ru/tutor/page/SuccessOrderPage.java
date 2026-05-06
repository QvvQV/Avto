package ru.tutor.page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.tutor.config.TestConfig;
import ru.tutor.utils.UrlRedirectHelper;
import ru.tutor.utils.UniversalMethod;

import static com.codeborne.selenide.Selenide.$;
import static io.qameta.allure.Allure.step;

public class SuccessOrderPage {

    private final SelenideElement loginButton = $("a[href*=\"login\"]");
    private final SelenideElement orderNumberElement = $(".order-number");
    private final SelenideElement successPageIndicator = $(".success-page, [class*='success']");

    public SuccessOrderPage openModified(String originalRedirectUrl) {
        // Модифицируем URL редиректа используя исправленный метод
        String modifiedUrl = UrlRedirectHelper.simpleReplaceDomain(originalRedirectUrl, "ttplace.ru");
        Selenide.open(modifiedUrl);

        // Модифицируем href кнопки входа
        fixLoginButtonHref();

        return this;
    }

    public String getOrderNumber() {
        return orderNumberElement.getText();
    }

    @Step("Клик по кнопке логин")
    public void clickLoginButton() {
       step("Клик по кнопке для перехода в ЛК", ()-> {
           // Перед кликом убеждаемся, что href правильный
           fixLoginButtonHref();
           UniversalMethod.safeClick(loginButton, "Логин");
       });
    }

    public String getLoginButtonHref() {
        return loginButton.getAttribute("href");
    }

    /**
     * Ожидает редирект и открывает модифицированную страницу (упрощенная версия)
     */
    public SuccessOrderPage waitForRedirectAndOpen(String originalUrl, long timeoutMs) {
        // Простая реализация - сразу модифицируем URL
        String modifiedUrl = UrlRedirectHelper.simpleReplaceDomain(originalUrl, TestConfig.getCurrentUiUrl());
        Selenide.open(modifiedUrl);

        SuccessOrderPage page = new SuccessOrderPage();
        page.fixLoginButtonHref();

        return page;
    }

    /**
     * Исправляет атрибут href кнопки входа
     */
    public void fixLoginButtonHref() {
        // ИСПРАВЛЕНИЕ: используем селектор с двойными кавычками
        UrlRedirectHelper.modifyElementHref("a[href*=\"loginToken\"]", TestConfig.getCurrentClientDomain());
    }

    /**
     * Альтернативный метод исправления через XPath
     */
    public void fixLoginButtonHrefByXPath() {
        UrlRedirectHelper.modifyElementHrefByXPath("//a[contains(@href, 'loginToken')]", TestConfig.getCurrentClientDomain());
    }

    /**
     * Простая проверка и исправление текущей страницы
     */
    public SuccessOrderPage openAndFixPage(String url) {
        // Проверяем и исправляем URL если нужно
        UrlRedirectHelper.checkAndFixCurrentUrl(TestConfig.getCurrentUiUrl());

        SuccessOrderPage page = new SuccessOrderPage();
        page.fixLoginButtonHref();

        return page;
    }
}
