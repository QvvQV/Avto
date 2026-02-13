package ru.tutor.page;

import com.codeborne.selenide.SelenideElement;
import net.datafaker.Faker;
import org.openqa.selenium.Keys;
import ru.tutor.utils.WaitHelper;
import ru.tutor.utils.universalMethod;

import java.util.Locale;
import java.util.Objects;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;

/*
 * Класс для страницы логина/регистрации в UI
 */

public class Registration {

    private static final Faker faker = new Faker(Locale.ENGLISH);

    // // Название кнопок
    private static final String LOGIN_TEXT = "Войти";
    private static final String RECOVER_PASS_TEXT = "Восстановить пароль";
    private static final String REGISTRATION_TEXT = "Зарегистрироваться";
    private static final String HEAD2_TEXT = "Восстановление пароля";
    private static final String RECOVER_TEXT = "Я понял(а)";
    private static final String REGISTER_TEXT = "Зарегистрироваться";

//    // Селекторы
    private static final SelenideElement EMAIL_INPUT = $("[name='email']");
    private static final SelenideElement PASSWORD_INPUT = $("[name='password']");
    private static final SelenideElement LOGIN_BUTTON = $$("button").find(exactText(LOGIN_TEXT));
    private static final SelenideElement btnYandex = $("[data-t='personal-button:text']");

    private static final SelenideElement RECOVER_PASS = $$("a").find(exactText(RECOVER_PASS_TEXT));
    private static final SelenideElement REGISTRATION = $$("a").find(exactText(REGISTRATION_TEXT));
    private static final SelenideElement PASSWORD_ERROR = $$(".sc-uVWWZ.bBKkoe").get(1);
    private static final SelenideElement REPEAT_PASSWORD_ERROR = $$(".sc-uVWWZ.bBKkoe").get(2);
    private static final SelenideElement LOGIN_ERROR1 = $x("//p[contains(@class, '_errorMessage_') and contains(text(), 'Не подходит адрес почты?')]");

    //    Страница Восстановление пароля
    private static final SelenideElement HEAD2_BUTTON = $$("h2").find(exactText(HEAD2_TEXT));
    private static final SelenideElement P_TEXT = $("._subtitle_i0xzq_34");
    private static final SelenideElement P_TEXT1 = $$("._subtitle_12k9l_33").get(1);
    private static final SelenideElement P_TEXT2 = $$("._subtitle_12k9l_33").get(2);
    private static final SelenideElement RECOVER_BUTTON = $("button");
    private static final SelenideElement RECOVER_BUTTON1 = $$("button").find(exactText(RECOVER_TEXT));
    private static final SelenideElement BACK_BUTTON = $("._goBackBtn_5y16r_104");

    //    Страница Регистрации
    private static final SelenideElement NAME_INPUT = $("[name='name']");
    private static final SelenideElement HEAD2_TEXT_REGISTER = $$("h2").find(exactText("Регистрация"));
    private static final SelenideElement NEXT_BUTTON = $$("button").find(exactText("Далее"));
    private static final SelenideElement REGISTER_BUTTON = $$("button").find(exactText(REGISTER_TEXT));
    private static final SelenideElement NUMBER_INPUT = $$("input").get(1);
    private static final SelenideElement TELEGRAMM_INPUT = $("[name='telegram']");
    private static final SelenideElement BACK_BUTTON_REGISTER = $("._goBackBtn_cj1gp_140");
    private static final SelenideElement LOGIN_BUTTON_FROM_REGISTER = $$("a").find(exactText(LOGIN_TEXT));
    private static final SelenideElement REPEAT_PASSWORD_INPUT = $("[name='repeatPassword']");

    //ЛК
    private static final SelenideElement HEAD2_TEXTMod = $$("h2").find(exactText("Добро пожаловать на TutorPlace"));
    private static final SelenideElement HEAD2_ADD_KURS = $$("h2").find(exactText("Добавленное"));

    private static final String email =  "tp5052_4@test.ru";
    private static final String valid_password = "12345678";
    private static final String invalid_password = "123";

    public static void randomEmail() {
        step("вводим рандомный адрес", () ->
            EMAIL_INPUT.sendKeys(faker.internet().emailAddress())
        );
    }

    // Метод для получения элемента ошибки по тексту
    public static SelenideElement getErrorElement(String errorText) {
        return $x("//p[contains(@class, '_errorMessage_') and contains(text(), '" + errorText + "')]");
    }

    // Метод для получения текста ошибки
    public static String getErrorMessageText(String expectedError) {
        return getErrorElement(expectedError)
                .shouldBe(visible)
                .getText();
    }

    public static void notKorrectEmail() {
        step("вводим некорректный адрес", () ->
            EMAIL_INPUT.sendKeys("kval")
        );
    }

    public static void validEmail() {
        step("вводим ранее зарегистрированный адрес", () ->
            EMAIL_INPUT.sendKeys(email)
        );
    }

    public static void validPass() {
        step("вводим стандартный пароль", () ->
            PASSWORD_INPUT.sendKeys(valid_password)
        );
    }

    public static void validRepeatPass() {
        step("поле Повторить Пароль заполняем валидным паролем", () ->
            REPEAT_PASSWORD_INPUT.sendKeys(valid_password)
        );
    }

    public static void PassThreeNumber() {
        step("поле Пароль заполнено менее 8 символов", () ->
            PASSWORD_INPUT.sendKeys(invalid_password)
        );
    }

    public static void NotEquelRepeatPass() {
        step("поле Повторить Пароль не совпадает с Паролем", () ->
            REPEAT_PASSWORD_INPUT.sendKeys(invalid_password)
        );
    }

    public static void clickBtnLogin() {
        step("кликаем по кнопке 'Войти'", () ->
            universalMethod.clickButton(LOGIN_BUTTON, LOGIN_TEXT)
        );
    }

    public static void notShowErrorPass() {
        PASSWORD_ERROR.shouldNot(visible);
    }

    public static void notShowErrorRepeatPass() {
        REPEAT_PASSWORD_ERROR.shouldNot(visible);
    }

    public static void clickRecoverPass() {
        step("нажимаем на кнопку восстановить пароль", () ->
            universalMethod.clickButton(RECOVER_PASS, RECOVER_PASS_TEXT)
        );
    }

    public static String textBtnRegistration() {
        return REGISTRATION.shouldBe(visible).getText();
    }

    public static void clickRegistr() {
        universalMethod.clickButton(REGISTRATION, REGISTRATION_TEXT);
    }

//    Страница восстановления пароля

    public static String showH2() {
        return HEAD2_BUTTON.shouldBe(visible).getText();
    }

    public static void showP() {
        P_TEXT.shouldBe(visible).getText();
    }

    public static void clickBtnRecover() {
        step("кликаем по кнопке восстановить пароль", () ->
            universalMethod.clickButton(RECOVER_BUTTON, RECOVER_PASS_TEXT )
        );
    }

    public static void clickBtnBack() {
        step("кликаем по кнопке в виде стрелки", () ->
            BACK_BUTTON.click()
        );
    }

    //    Страница Регистрации
    public static String showH2Reg() {
        return HEAD2_TEXT_REGISTER.shouldBe(visible).getText();
    }

    public static void inputName() {
        step("Вводим имя", () ->
            NAME_INPUT.setValue(faker.name().firstName())
        );
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
        step("удаляем имя из поля", () ->
            NAME_INPUT.doubleClick().sendKeys(Keys.DELETE)
        );
    }

    public static void inputTelegramm() {
        step("Заполняем графу телеграмм", () ->
            TELEGRAMM_INPUT.sendKeys(faker.internet().emailAddress())
        );
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
        step("Кликаем по кнопке далее", () ->
            universalMethod.clickButton(NEXT_BUTTON, "Следующий шаг")
        );
    }

    public static void clickBtnRegister() {
        step("Кликаем по кнопке Зарегистироваться", () ->
            universalMethod.clickButton(REGISTER_BUTTON, REGISTER_TEXT)
        );
    }

    public static String disablBtnRegist() {
        return NEXT_BUTTON.getAttribute("disabled");
    }

    public static String showErrorRepeatPass() {
        return REPEAT_PASSWORD_ERROR.shouldBe(visible).getText();
    }

    public static void clickBtnBackFromRegistr() {
        step("Кликаем по кнопке назад на стр Зарегистироваться", () ->
            universalMethod.clickButton(BACK_BUTTON_REGISTER, "Назад")
        );
    }

    public static void clickBtnEnter() {
        step("Кликаем по кнопке Войти на стр зарегистрироваться", () ->
            universalMethod.clickButton(LOGIN_BUTTON_FROM_REGISTER, LOGIN_TEXT)
        );
    }

    public static void validPerson() {
        validEmail();
        validPass();
        clickBtnLogin();
        WaitHelper.waitForAnimationComplete();
        WaitHelper.waitForPageLoad();
    }
}