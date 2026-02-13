package ru.tutor.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;

public class FormVersionPage {

    private final SelenideElement nameInput = $("input[placeholder='Укажите имя']");
    private final SelenideElement dateInput = $("input[type='date']");
    private final SelenideElement submitButton = $("button.intro__calculation-button");
    private final SelenideElement modalSubmitButton = $("#mainFormaSubmitBtn");
    private final SelenideElement emailInput = $("#email");

    @Step("Открыть главную страницу")
    public FormVersionPage openMainPage() {
        com.codeborne.selenide.Selenide.open("https://matrixbf5.tutorplace.ru/");
        return this;
    }

    @Step("Заполнить форму: имя = {name}, дата = {birthDate}")
    public FormVersionPage fillCalculationForm(String name, String birthDate) {
        nameInput.shouldBe(Condition.visible).setValue(name);
        dateInput.shouldBe(Condition.visible).setValue(birthDate);
        return this;
    }

    @Step("Нажать кнопку 'Рассчитать матрицу'")
    public FormVersionPage clickCalculateButton() {
        submitButton.shouldBe(Condition.enabled).click();
        return this;
    }

    @Step("Дождаться появления модального окна")
    public FormVersionPage waitForModal() {
        // Проверяем, что элемент существует в DOM и не скрыт через CSS
        modalSubmitButton.should(Condition.exist);

        // Ждем пока элемент станет видимым (используем JavaScript для проверки видимости)
        modalSubmitButton.shouldBe(Condition.visible, java.time.Duration.ofSeconds(10));

        // Дополнительная проверка через JavaScript
        boolean isVisible = Boolean.TRUE.equals(executeJavaScript(
                "return arguments[0].offsetParent !== null && " +
                        "arguments[0].style.display !== 'none' && " +
                        "arguments[0].style.visibility !== 'hidden' && " +
                        "arguments[0].getBoundingClientRect().width > 0 && " +
                        "arguments[0].getBoundingClientRect().height > 0",
                modalSubmitButton
        ));

        if (!isVisible) {
            throw new AssertionError("Элемент #mainFormaSubmitBtn не видим на странице");
        }

        return this;
    }

    @Step("Заполнить email: {email}")
    public FormVersionPage fillEmail(String email) {
        emailInput.shouldBe(Condition.visible).setValue(email);
        return this;
    }

    @Step("Нажать кнопку 'Получить доступ'")
    public FormVersionPage submitForm() {
        // Проверяем, что кнопка кликабельна
        modalSubmitButton.shouldBe(Condition.enabled).click();
        return this;
    }

    @Step("Проверить редирект на страницу заказа")
    public void checkRedirectToOrderPage() {
        webdriver().shouldHave(urlContaining("/order"));
    }

    @Step("Получить cookie X-AB-FORM-VERSION")
    public String getFormVersionCookie() {
        try {
            var cookie = com.codeborne.selenide.WebDriverRunner.getWebDriver()
                    .manage()
                    .getCookieNamed("X-AB-FORM-VERSION");
            return cookie != null ? cookie.getValue() : "cookie_not_found";
        } catch (Exception e) {
            return "error_getting_cookie: " + e.getMessage();
        }
    }

    @Step("Получить значение formVersion из localStorage")
    public String getFormVersionFromLocalStorage() {
        try {
            return executeJavaScript("return localStorage.getItem('formVersion') || 'not_found'");
        } catch (Exception e) {
            return "error_getting_localstorage: " + e.getMessage();
        }
    }

    @Step("Получить текущий URL")
    public String getCurrentUrl() {
        return com.codeborne.selenide.WebDriverRunner.url();
    }

    @Step("Сделать скриншот страницы")
    public void takeScreenshot(String description) {
        com.codeborne.selenide.Selenide.screenshot(description);
    }

    @Step("Ждать загрузки страницы")
    public void waitForPageLoad() {
        executeJavaScript("return document.readyState").equals("complete");
        com.codeborne.selenide.Selenide.sleep(1000); // Небольшая задержка для стабильности
    }
}
