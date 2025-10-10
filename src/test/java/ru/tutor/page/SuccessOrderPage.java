package ru.tutor.page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import ru.tutor.utils.UrlRedirectHelper;
import ru.tutor.utils.WaitHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static ru.tutor.utils.WaitHelper.waitForPageLoad;

public class SuccessOrderPage {

    // ИСПРАВЛЕНИЕ: используем правильный селектор с двойными кавычками
    private final SelenideElement loginButton = $("a[href*=\"loginToken\"]");
    private final SelenideElement orderNumberElement = $(".order-number");
    private final SelenideElement successPageIndicator = $(".success-page, [class*='success']");


//    public SuccessOrderPage openModified(String originalRedirectUrl) {
//        String modifiedUrl = UrlRedirectHelper.simpleReplaceDomain(originalRedirectUrl, "ttplace.ru");
//        Selenide.open(modifiedUrl);
//        waitForPageLoad();
//        fixLoginButtonHref();
//        return this;
//    }
//
//    public String getOrderNumber(){
//        return orderNumberElement.getText();
//    }
//
//    public void clickLoginButton(){
//        fixLoginButtonHref();
//        loginButton.shouldBe(visible, Duration.ofSeconds(10)).click();
//    }
//
//    public String getLoginButtonHref(){
//        return loginButton.getAttribute("href");
//    }
//
//    public static SuccessOrderPage waitForRedirectAndOpen(String originalUrl, long timeoutMs){
//        String modifiedUrl = UrlRedirectHelper.simpleReplaceDomain(originalUrl, "ttplace.ru");
//        Selenide.open(modifiedUrl);
//
//        SuccessOrderPage page = new SuccessOrderPage();
//        page.waitForPageLoad();
//        page.fixLoginButtonHref();
//
//        return page;
//    }
//
//    public void fixLoginButtonHref(){
//        UrlRedirectHelper.modifyElementHref("a[href*=\"loginToken\"]", "client.ttplace.ru");
//    }
//
//    public void fixLoginButtonHrefByXpath() {
//        UrlRedirectHelper.modifyElementHrefByXPath("//a[contains(@href, 'loginToken')]", "client.ttplace.ru");
//    }
//
//    public static SuccessOrderPage openAndFixPage(String url){
//        UrlRedirectHelper.checkAndFixCurrentUrl("ttplace.ru");
//
//        SuccessOrderPage page = new SuccessOrderPage();
//        page.waitForPageLoad();
//        page.fixLoginButtonHref();
//
//        return page;
//    }
//
//    private void waitForPageLoad(){
//        successPageIndicator.shouldBe(visible, Duration.ofSeconds(15));
//        WaitHelper.waitForPageLoad();
//    }
//}

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
        loginButton.click();
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
     * Исправляет атрибут href кнопки входа (ИСПРАВЛЕННАЯ ВЕРСИЯ)
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
