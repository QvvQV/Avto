package ru.tutor.page;

import com.codeborne.selenide.SelenideElement;
import net.datafaker.Faker;
import org.openqa.selenium.Keys;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WaitHelper;

import java.util.Locale;
import java.util.Objects;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;

public class Registration {

    private static final Faker faker = new Faker(new Locale("en"));

//    // Селекторы
    private static SelenideElement EMAIL_INPUT = $("[name=\"email\"]");
    private static SelenideElement PASSWORD_INPUT = $("[name=\"password\"]");
    private static SelenideElement LOGIN_BUTTON = $$("button").find(exactText("Войти"));
    private static SelenideElement btnYandex = $("[data-t=\"personal-button:text\"]");

    private static SelenideElement RECOVER_PASS = $$("a").find(exactText("Восстановить пароль"));
    private static SelenideElement REGISTRATION = $$("a").find(exactText("Зарегистрироваться"));
//    private static SelenideElement LOGIN_ERROR = $$("span").find(exactText("Это поле обязательное"));
    private static SelenideElement LOGIN_ERROR = $$(".sc-uVWWZ.bBKkoe").get(0);
//    private static SelenideElement PASSWORD_ERROR = $$("span").find(exactText("Это поле обязательное"));
    private static SelenideElement PASSWORD_ERROR = $$(".sc-uVWWZ.bBKkoe").get(1);
    private static SelenideElement REPEAT_PASSWORD_ERROR = $$(".sc-uVWWZ.bBKkoe").get(2);
    private static SelenideElement LOGIN_ERROR1 = $(".sc-hCPjZK.gODAEi");

    //    Страница Восстановление пароля
    private static SelenideElement HEAD2_TEXT = $$("h2").find(exactText("Восстановление пароля"));
    private static SelenideElement P_TEXT = $("._subtitle_12k9l_33");
    private static SelenideElement P_TEXT1 = $$("._subtitle_12k9l_33").get(1);
    private static SelenideElement P_TEXT2 = $$("._subtitle_12k9l_33").get(2);
    private static SelenideElement RECOVER_BUTTON = $("button");
    private static SelenideElement RECOVER_BUTTON1 = $$("button").find(exactText("Я понял(а)"));
    private static SelenideElement BACK_BUTTON = $("._goBackBtn_i0xzq_92");

    //    Страница Регистрации
    private static SelenideElement NAME_INPUT = $("[name=\"name\"]");
    private static SelenideElement HEAD2_TEXT_REGISTER = $$("h2").find(exactText("Регистрация профиля"));
    private static SelenideElement NEXT_BUTTON = $$("button").find(exactText("Далее"));
    private static SelenideElement REGISTER_BUTTON = $$("button").find(exactText("Зарегистрироваться"));
    private static SelenideElement NUMBER_INPUT = $$("input").get(1);
    private static SelenideElement TELEGRAMM_INPUT = $("[name=\"telegram\"]");
    private static SelenideElement BACK_BUTTON_REGISTER = $("._goBackBtn_1aggk_169");
    private static SelenideElement LOGIN_BUTTON_FROM_REGISTER = $$("a").find(exactText("Войти"));
    private static SelenideElement REPEAT_PASSWORD_INPUT = $("[name=\"repeatPassword\"]");

    //ЛК
    private static SelenideElement HEAD2_TEXTMod = $$("h2").find(exactText("Добро пожаловать на TutorPlace"));
    private static SelenideElement HEAD2_ADD_KURS = $$("h2").find(exactText("Добавленное"));

    public static void randomEmail() {
        step("вводим рандомный адрес", () -> {
            EMAIL_INPUT.sendKeys(faker.internet().emailAddress());
        });
    }

    public static void notKorrectEmail() {
        step("вводим некорректный адрес", () -> {
            EMAIL_INPUT.sendKeys("kval");
        });
    }

    public static void validEmail() {
        step("вводим ранее зарегистрированный адрес", () -> {
            EMAIL_INPUT.sendKeys("tp5052_4@test.ru");
        });
    }

    public static void validPass() {
        step("вводим стандартный пароль", () -> {
            PASSWORD_INPUT.sendKeys("12345678");
        });
    }

    public static void validRepeatPass() {
        step("поле Повторить Пароль заполняем валидным паролем", () -> {
            REPEAT_PASSWORD_INPUT.sendKeys("12345678");
        });
    }

    public static void PassThreeNumber() {
        step("поле Пароль заполнено менее 8 символов", () -> {
            PASSWORD_INPUT.sendKeys("123");
        });
    }

    public static void NotEquelRepeatPass() {
        step("поле Повторить Пароль не совпадает с Паролем", () -> {
            REPEAT_PASSWORD_INPUT.sendKeys("123");
        });
    }

    public static void clickBtnLogin() {
        step("кликаем по кнопке 'Войти'", () -> {
            LOGIN_BUTTON.shouldBe(visible).click();
        });
    }

    public static String showErrorLogin() {
        return LOGIN_ERROR.shouldBe(visible).getText();
    }

    public static String showErrorLoginTwo() {
        return LOGIN_ERROR1.shouldBe(visible).getText();
    }

    public static String showErrorPass() {
        return PASSWORD_ERROR.shouldBe(visible).getText();
    }

    public static SelenideElement notShowErrorPass() {
        return PASSWORD_ERROR.shouldNot(visible);
    }

    public static SelenideElement notShowErrorRepeatPass() {
        return REPEAT_PASSWORD_ERROR.shouldNot(visible);
    }

    public static SelenideElement notShowErrorLoginTwo() {
        return LOGIN_ERROR1.shouldNot(visible);
    }

    public static void clickRecoverPass() {
        step("нажимаем на кнопку восстановить пароль", () -> {
            RECOVER_PASS.click();
        });
    }

    public static String textBtnRegistration() {
        return REGISTRATION.shouldBe(visible).getText();
    }

    public static void clickRegistr() {
        REGISTRATION.click();
    }

//    Страница восстановления пароля

    public static String showH2() {
        return HEAD2_TEXT.shouldBe(visible).getText();
    }

    public static String showP() {
        return P_TEXT.shouldBe(visible).getText();
    }

    public static void clickBtnRecover() {
        step("кликаем по кнопке восстановить пароль", () -> {
            RECOVER_BUTTON.click();
        });
    }

    public static String showP1() {
        return P_TEXT1.shouldBe(visible).getText();
    }

    public static String showP2() {
        return P_TEXT2.shouldBe(visible).getText();
    }

    public static String textBtnRecover() {
        return RECOVER_BUTTON1.getText();
    }

    public static void clickBtnBack() {
        step("кликаем по кнопке в виде стрелки", () -> {
            BACK_BUTTON.click();
        });
    }

    //    Страница Регистрации
    public static String showH2Reg() {
        return HEAD2_TEXT_REGISTER.shouldBe(visible).getText();
    }

    public static void inputName() {
        step("Вводим имя", () -> {
            NAME_INPUT.setValue(faker.name().firstName());
        });
    }

    public static void inputNumber() {
        step("Заполняем графу телефон", () -> {
            String phoneNumber = faker.numerify("9#########");
            for (char digit : phoneNumber.toCharArray()){
                NUMBER_INPUT.sendKeys(String.valueOf(digit));
                sleep(50); // небольшая задержка между вводом символов
            }
        });
    }

    public static String valueName() {
        return NAME_INPUT.getAttribute("value");
    }

    public static void deleteName() {
        step("удаляем имя из поля", () -> {
            NAME_INPUT.doubleClick().sendKeys(Keys.DELETE);
        });
    }

    public static String valueTelephone() {
        return NUMBER_INPUT.getAttribute("value");
    }

    public static void inputTelegramm() {
        step("Заполняем графу телеграмм", () -> {
            TELEGRAMM_INPUT.sendKeys(faker.internet().emailAddress());
        });
    }

    public static String valueTelegramm() {
        return TELEGRAMM_INPUT.getAttribute("value");
    }

    public static String emptyValue() {
        if (Objects.requireNonNull(TELEGRAMM_INPUT.getAttribute("value")).isEmpty()) {
            return "поле телеграмм пустое";
        }
        if (Objects.requireNonNull(NUMBER_INPUT.getAttribute("value")).isEmpty()) {
            return "поле телефон пустое";
        }
        if (Objects.requireNonNull(NAME_INPUT.getAttribute("value")).isEmpty()) {
            return "поле имя пустое";
        } else return "все поля заполнены";
    }

    public static void clickBtnRegistr() {
        step("Кликаем по кнопке далее", () -> {
            NEXT_BUTTON.shouldBe(visible).click();
        });
    }

    public static void clickBtnRegister() {
        step("Кликаем по кнопке Зарегистироваться", () -> {
            REGISTER_BUTTON.shouldBe(visible).click();
        });
    }

    public static String disablBtnRegist() {
        return NEXT_BUTTON.getAttribute("disabled");
    }

    public static String showErrorRepeatPass() {
        return REPEAT_PASSWORD_ERROR.shouldBe(visible).getText();
    }

    public static void clickBtnBackFromRegistr() {
        step("Кликаем по кнопке назад на стр Зарегистироваться", () -> {
            BACK_BUTTON_REGISTER.shouldBe(visible).click();
        });
    }

    public static void clickBtnEnter() {
        step("Кликаем по кнопке Войти на стр зарегистрироваться", () -> {
            LOGIN_BUTTON_FROM_REGISTER.shouldBe(visible).click();
        });
    }

    //ЛК
    public static String showH2Mod() {
        return HEAD2_TEXTMod.shouldBe(visible).getText();
    }

    public static String showH2AddKurs() {
        return HEAD2_ADD_KURS.shouldBe(visible).getText();
    }

    public static void validPerson() {
        validEmail();
        validPass();
        clickBtnLogin();
        WaitHelper.waitForPageLoad();
        AllureAttachments.attachScreenshot("Главная страница");
    }

}
