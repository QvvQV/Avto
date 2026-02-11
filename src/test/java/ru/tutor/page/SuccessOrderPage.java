package ru.tutor.page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import ru.tutor.utils.UrlRedirectHelper;
import ru.tutor.utils.universalMethod;

import static com.codeborne.selenide.Selenide.$;

public class SuccessOrderPage {

    private final SelenideElement loginButton = $("a[href*=\"loginToken\"]");
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

    public void clickLoginButton() {
        // Перед кликом убеждаемся, что href правильный
        fixLoginButtonHref();
        universalMethod.safeClick(loginButton, "Логин");
//        loginButton.click();
    }

    public String getLoginButtonHref() {
        return loginButton.getAttribute("href");
    }

    /**
     * Ожидает редирект и открывает модифицированную страницу (упрощенная версия)
     */
    public static SuccessOrderPage waitForRedirectAndOpen(String originalUrl, long timeoutMs) {
        // Простая реализация - сразу модифицируем URL
        String modifiedUrl = UrlRedirectHelper.simpleReplaceDomain(originalUrl, "ttplace.ru");
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
        UrlRedirectHelper.modifyElementHref("a[href*=\"loginToken\"]", "client.ttplace.ru");
    }

    /**
     * Альтернативный метод исправления через XPath
     */
    public void fixLoginButtonHrefByXPath() {
        UrlRedirectHelper.modifyElementHrefByXPath("//a[contains(@href, 'loginToken')]", "client.ttplace.ru");
    }

    /**
     * Простая проверка и исправление текущей страницы
     */
    public static SuccessOrderPage openAndFixPage(String url) {
        // Проверяем и исправляем URL если нужно
        UrlRedirectHelper.checkAndFixCurrentUrl("ttplace.ru");

        SuccessOrderPage page = new SuccessOrderPage();
        page.fixLoginButtonHref();

        return page;
    }
}
