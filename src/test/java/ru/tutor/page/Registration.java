package ru.tutor.page;

import com.codeborne.selenide.SelenideElement;
import net.datafaker.Faker;
import org.openqa.selenium.Keys;

import java.util.Locale;
import java.util.Objects;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static io.qameta.allure.Allure.step;

public class Registration {
    //    Страница Логин
    private static final Faker faker = new Faker(new Locale("en"));
    private static SelenideElement email = $("[name=\"email\"]");
    private static SelenideElement password = $("[name=\"password\"]");
    private static SelenideElement btnLogin = $$("button").find(exactText("Войти"));
    private static SelenideElement btnYandex = $("[data-t=\"personal-button:text\"]");

    private static SelenideElement recoverPass = $$("a").find(exactText("Восстановить пароль"));
    private static SelenideElement registration = $$("a").find(exactText("Зарегистрироваться"));
//    private static SelenideElement errorLogin = $$("span").find(exactText("Это поле обязательное"));
    private static SelenideElement errorLogin = $$(".sc-uVWWZ.bBKkoe").get(0);
//    private static SelenideElement errorPass = $$("span").find(exactText("Это поле обязательное"));
    private static SelenideElement errorPass = $$(".sc-uVWWZ.bBKkoe").get(1);
    private static SelenideElement errorRepeatPass = $$(".sc-uVWWZ.bBKkoe").get(2);
    private static SelenideElement errorLogin1 = $(".sc-hCPjZK.gODAEi");

    //    Страница Восстановление пароля
    private static SelenideElement h2Text = $$("h2").find(exactText("Восстановление пароля"));
    private static SelenideElement pText = $("._subtitle_12k9l_33");
    private static SelenideElement pText1 = $$("._subtitle_12k9l_33").get(1);
    private static SelenideElement pText2 = $$("._subtitle_12k9l_33").get(2);
    private static SelenideElement btnRecover = $("button");
    private static SelenideElement btnRecover1 = $$("button").find(exactText("Я понял(а)"));
    private static SelenideElement btnBack = $("._goBackBtn_i0xzq_92");

    //    Страница Регистрации
    private static SelenideElement name = $("[name=\"name\"]");
    private static SelenideElement h2TextReg = $$("h2").find(exactText("Регистрация профиля"));
    private static SelenideElement btnRegistr = $$("button").find(exactText("Далее"));
    private static SelenideElement btnRegistеr = $$("button").find(exactText("Зарегистрироваться"));
    private static SelenideElement number = $$("input").get(1);
    private static SelenideElement telegramm = $("[name=\"telegram\"]");
    private static SelenideElement btnBackRegistr = $("._goBackBtn_1aggk_169");
    private static SelenideElement btnLoginFromRegistr = $$("a").find(exactText("Войти"));
    private static SelenideElement repeatPass = $("[name=\"repeatPassword\"]");

    //ЛК
    private static SelenideElement h2TextMod = $$("h2").find(exactText("Добро пожаловать на TutorPlace"));
    private static SelenideElement h2AddKurs = $$("h2").find(exactText("Добавленное"));


    public static void randomEmail() {
        step("вводим рандомный адрес", () -> {
            email.sendKeys(faker.internet().emailAddress());
        });
    }

    public static void notKorrectEmail() {
        step("вводим некорректный адрес", () -> {
            email.sendKeys("kval");
        });
    }

    public static void validEmail() {
        step("вводим ранее зарегистрированный адрес", () -> {
            email.sendKeys("tp5052_2@test.ru");
        });
    }

    public static void validPass() {
        step("вводим стандартный пароль", () -> {
            password.sendKeys("12345678");
        });
    }

    public static void validRepeatPass() {
        step("поле Повторить Пароль заполняем валидным паролем", () -> {
            repeatPass.sendKeys("12345678");
        });
    }

    public static void PassThreeNumber() {
        step("поле Пароль заполнено менее 8 символов", () -> {
            password.sendKeys("123");
        });
    }

    public static void NotEquelRepeatPass() {
        step("поле Повторить Пароль не совпадает с Паролем", () -> {
            repeatPass.sendKeys("123");
        });
    }

    public static void clickBtnLogin() {
        step("кликаем по кнопке 'Войти'", () -> {
            btnLogin.shouldBe(visible).click();
        });
    }

    public static String showErrorLogin() {
        return errorLogin.shouldBe(visible).getText();
    }

    public static String showErrorLoginTwo() {
        return errorLogin1.shouldBe(visible).getText();
    }

    public static String showErrorPass() {
        return errorPass.shouldBe(visible).getText();
    }

    public static SelenideElement notShowErrorPass() {
        return errorPass.shouldNot(visible);
    }

    public static SelenideElement notShowErrorRepeatPass() {
        return errorRepeatPass.shouldNot(visible);
    }

    public static SelenideElement notShowErrorLoginTwo() {
        return errorLogin1.shouldNot(visible);
    }

    public static void clickRecoverPass() {
        step("нажимаем на кнопку восстановить пароль", () -> {
            recoverPass.click();
        });
    }

    public static String textBtnRegistration() {
        return registration.shouldBe(visible).getText();
    }

    public static void clickRegistr() {
        registration.click();
    }

//    Страница восстановления пароля

    public static String showH2() {
        return h2Text.shouldBe(visible).getText();
    }

    public static String showP() {
        return pText.shouldBe(visible).getText();
    }

    public static void clickBtnRecover() {
        step("кликаем по кнопке восстановить пароль", () -> {
            btnRecover.click();
        });
    }

    public static String showP1() {
        return pText1.shouldBe(visible).getText();
    }

    public static String showP2() {
        return pText2.shouldBe(visible).getText();
    }

    public static String textBtnRecover() {
        return btnRecover1.getText();
    }

    public static void clickBtnBack() {
        step("кликаем по кнопке в виде стрелки", () -> {
            btnBack.click();
        });
    }

    //    Страница Регистрации
    public static String showH2Reg() {
        return h2TextReg.shouldBe(visible).getText();
    }

    public static void inputName() {
        step("Вводим имя", () -> {
            name.sendKeys(faker.name().firstName());
        });
    }

    public static void inputNumber() {
        step("Заполняем графу телефон", () -> {
            number.sendKeys(faker.number().digits(5));
        });
    }

    public static String valueName() {
        return name.getAttribute("value");
    }

    public static void deleteName() {
        step("удаляем имя из поля", () -> {
            name.doubleClick().sendKeys(Keys.DELETE);
        });
    }

    public static String valueTelephone() {
        return number.getAttribute("value");
    }

    public static void inputTelegramm() {
        step("Заполняем графу телеграмм", () -> {
            telegramm.sendKeys(faker.internet().emailAddress());
        });
    }

    public static String valueTelegramm() {
        return telegramm.getAttribute("value");
    }

    public static String emptyValue() {
        if (Objects.requireNonNull(telegramm.getAttribute("value")).isEmpty()) {
            return "поле телеграмм пустое";
        }
        if (Objects.requireNonNull(number.getAttribute("value")).isEmpty()) {
            return "поле телефон пустое";
        }
        if (Objects.requireNonNull(name.getAttribute("value")).isEmpty()) {
            return "поле имя пустое";
        } else return "все поля заполнены";
    }

    public static void clickBtnRegistr() {
        step("Кликаем по кнопке далее", () -> {
            btnRegistr.shouldBe(visible).click();
        });
    }

    public static void clickBtnRegister() {
        step("Кликаем по кнопке Зарегистироваться", () -> {
            btnRegistеr.shouldBe(visible).click();
        });
    }

    public static String disablBtnRegist() {
        return btnRegistr.getAttribute("disabled");
    }

    public static String showErrorRepeatPass() {
        return errorRepeatPass.shouldBe(visible).getText();
    }

    public static void clickBtnBackFromRegistr() {
        step("Кликаем по кнопке назад на стр Зарегистироваться", () -> {
            btnBackRegistr.shouldBe(visible).click();
        });
    }

    public static void clickBtnEnter() {
        step("Кликаем по кнопке Войти на стр зарегистрироваться", () -> {
            btnLoginFromRegistr.shouldBe(visible).click();
        });
    }

    //ЛК
    public static String showH2Mod() {
        return h2TextMod.shouldBe(visible).getText();
    }

    public static String showH2AddKurs() {
        return h2AddKurs.shouldBe(visible).getText();
    }

}
