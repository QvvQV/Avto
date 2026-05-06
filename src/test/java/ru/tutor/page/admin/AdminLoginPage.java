package ru.tutor.page.admin;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Allure;
import ru.tutor.utils.WaitHelper;
import ru.tutor.utils.UniversalMethod;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static io.qameta.allure.Allure.step;

public class AdminLoginPage {

    private final SelenideElement LOGIN = $("#root > div > div > div > form > div:nth-child(1) > div.input__wrapper > input");
    private final SelenideElement PASSWORD = $("[name='password']");
    private final SelenideElement LOGIN_BUTTON = $$("button").find(exactText("войти"));

    public void login() {
        // Проверяем, не авторизованы ли уже

        if (!isLoggedIn()) {

            String adminUsername = "admin";
            String adminPassword = "admin";
            step("вводим валидный email", () -> {
                LOGIN.setValue(adminUsername);
            });
            step("вводим валидный пароль", () -> {
                PASSWORD.setValue(adminPassword);
            });
            step("кликаем по кнопке войти", () -> {
                UniversalMethod.safeClick(LOGIN_BUTTON, "Войти");
                WaitHelper.waitForPageLoad();
                WaitHelper.waitForAnimationComplete();
            });
        } else {
            Allure.addAttachment("Авторизация", "Уже аторизованы, пропускаем логин");
        }
    }

    private boolean isLoggedIn() {
        // Проверяем наличие элемента, который есть только после логина
        try {
            return WebDriverRunner.getWebDriver().getCurrentUrl().contains("dashboard")
                    && !WebDriverRunner.getWebDriver().getCurrentUrl().contains("auth");
        } catch (Exception e) {
            return false;
        }
    }
}
