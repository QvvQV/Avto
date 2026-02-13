package ru.tutor.page.admin;

import com.codeborne.selenide.SelenideElement;
import ru.tutor.utils.WaitHelper;
import ru.tutor.utils.universalMethod;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static io.qameta.allure.Allure.step;

public class AdminLoginPage {

    private final static SelenideElement LOGIN = $("#root > div > div > div > form > div:nth-child(1) > div.input__wrapper > input");
    private final static SelenideElement PASSWORD= $("[name='password']");
    private final static SelenideElement LOGIN_BUTTON = $$("button").find(exactText("войти"));

        public static void login() {

            String adminUsername = "admin";
            String adminPassword = "admin";
        step("вводим валидный email", () -> {
            LOGIN.setValue(adminUsername);
        });
        step("вводим валидный пароль", () -> {
            PASSWORD.setValue(adminPassword);
        });
        step("кликаем по кнопке войти", () -> {
            universalMethod.safeClick(LOGIN_BUTTON, "Войти");
            WaitHelper.waitForPageLoad();
            WaitHelper.waitForAnimationComplete();
        });
    }
}
