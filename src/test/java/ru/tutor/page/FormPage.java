package ru.tutor.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import io.qameta.allure.Step;
import net.datafaker.Faker;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;

public class FormPage {

    private final Faker faker = new Faker();

    // Локаторы для первой формы
    private final SelenideElement nameInput = $("input[placeholder='Укажите имя']");
    private final SelenideElement dateInput = $("input[type='date']");
    private final SelenideElement calculateButton = $("button.intro__calculation-button");

    // Локаторы для модального окна
    private final SelenideElement emailInput = $("#email");
    private final SelenideElement submitButton = $("#mainFormaSubmitBtn");

    // Локатор для определения версии A/B теста
    private final SelenideElement pencilIcon = $("img[alt='pen']");

    @Step("Открыть главную страницу")
    public FormPage openMainPage() {
        com.codeborne.selenide.Selenide.open("https://matrixbf5.tutorplace.ru/");
        return this;
    }

    @Step("Заполнить форму расчета: имя и дата рождения")
    public FormPage fillCalculationForm(String name, String birthDate) {
        nameInput.shouldBe(Condition.visible, Duration.ofSeconds(10))
                .setValue(name);

        // Для поля даты используем JavaScript
        com.codeborne.selenide.Selenide.executeJavaScript(
                "arguments[0].value = arguments[1];",
                dateInput, birthDate
        );

        return this;
    }

    @Step("Нажать кнопку 'Рассчитать матрицу'")
    public FormPage clickCalculateButton() {
        calculateButton.shouldBe(Condition.enabled, Duration.ofSeconds(10))
                .click();
        return this;
    }

    @Step("Дождаться появления модального окна с email полем")
    public FormPage waitForModal() {
        emailInput.shouldBe(Condition.visible, Duration.ofSeconds(15));
        submitButton.shouldBe(Condition.exist, Duration.ofSeconds(10));
        return this;
    }

    @Step("Определить версию A/B теста по наличию иконки карандаша")
    public int determineABVersion() {
        // Ждем немного для стабилизации
        com.codeborne.selenide.Selenide.sleep(1000);

        // Проверяем наличие иконки карандаша
        boolean hasPencilIcon = pencilIcon.exists() && pencilIcon.isDisplayed();

        if (hasPencilIcon) {
            return 1; // Версия с иконкой
        } else {
            return 0; // Версия без иконки
        }
    }

    @Step("Заполнить email")
    public FormPage fillEmail(String email) {
        emailInput.shouldBe(Condition.visible, Duration.ofSeconds(5))
                .setValue(email);
        return this;
    }

    @Step("Сгенерировать случайный email")
    public String generateRandomEmail() {
        return faker.internet().emailAddress();
    }

    @Step("Нажать кнопку 'Получить доступ'")
    public FormPage clickSubmitButton() {
        submitButton.shouldBe(Condition.enabled, Duration.ofSeconds(5))
                .click();
        return this;
    }

    @Step("Сделать скриншот текущего состояния")
    public void takeScreenshot(String description) {
        com.codeborne.selenide.Selenide.screenshot(description);
    }

    @Step("Получить текущий URL")
    public String getCurrentUrl() {
        return com.codeborne.selenide.WebDriverRunner.url();
    }

    @Step("Проверить наличие ошибок валидации")
    public boolean hasValidationErrors() {
        return $(".error, .text-danger, .validation-error")
                .exists();
    }
}
