package ru.tutor.page;

import com.codeborne.selenide.SelenideElement;
import net.datafaker.Faker;
import org.openqa.selenium.Keys;
import ru.tutor.config.TestConfig;
import ru.tutor.utils.WaitHelper;
import ru.tutor.utils.UniversalMethod;

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
    private final SelenideElement EMAIL_INPUT = $("[id='buy-modal-v2-email']");
    private final SelenideElement PASSWORD_INPUT = $("[name='password']");
    private final SelenideElement LOGIN_BUTTON = $$("button").find(exactText(LOGIN_TEXT));
    private final SelenideElement btnYandex = $("[data-t='personal-button:text']");

    private final SelenideElement RECOVER_PASS = $$("a").find(exactText(RECOVER_PASS_TEXT));
    private final SelenideElement REGISTRATION = $$("a").find(exactText(REGISTRATION_TEXT));
    private final SelenideElement PASSWORD_ERROR = $$(".sc-uVWWZ.bBKkoe").get(1);
    private final SelenideElement REPEAT_PASSWORD_ERROR = $$(".sc-uVWWZ.bBKkoe").get(2);
    private final SelenideElement LOGIN_ERROR1 = $x("//p[contains(@class, '_errorMessage_') and contains(text(), 'Не подходит адрес почты?')]");

    //    Страница Восстановление пароля
    private final SelenideElement HEAD2_BUTTON = $$("h2").find(exactText(HEAD2_TEXT));
    private final SelenideElement P_TEXT = $("._subtitle_i0xzq_34");
    private final SelenideElement P_TEXT1 = $$("._subtitle_12k9l_33").get(1);
    private final SelenideElement P_TEXT2 = $$("._subtitle_12k9l_33").get(2);
    private final SelenideElement RECOVER_BUTTON = $("button");
    private final SelenideElement RECOVER_BUTTON1 = $$("button").find(exactText(RECOVER_TEXT));
    private final SelenideElement BACK_BUTTON = $("._goBackBtn_5y16r_104");

    //    Страница Регистрации
    private final SelenideElement NAME_INPUT = $("[name='name']");
    private final SelenideElement HEAD2_TEXT_REGISTER = $$("h2").find(exactText("Регистрация"));
    private final SelenideElement NEXT_BUTTON = $$("button").find(exactText("Далее"));
    private final SelenideElement REGISTER_BUTTON = $$("button").find(exactText(REGISTER_TEXT));
    private final SelenideElement NUMBER_INPUT = $$("input").get(1);
    private final SelenideElement TELEGRAMM_INPUT = $("[name='telegram']");
    private final SelenideElement BACK_BUTTON_REGISTER = $("._goBackBtn_cj1gp_140");
    private final SelenideElement LOGIN_BUTTON_FROM_REGISTER = $$("a").find(exactText(LOGIN_TEXT));
    private final SelenideElement REPEAT_PASSWORD_INPUT = $("[name='repeatPassword']");

    //ЛК
    private final SelenideElement HEAD2_TEXTMod = $$("h2").find(exactText("Добро пожаловать на TutorPlace"));
    private final SelenideElement HEAD2_ADD_KURS = $$("h2").find(exactText("Добавленное"));

    // Тестовые данные
    private static final String EMAIL =  "tp5052_4@test.ru";
    private static final String VALID_PASSWORD = TestConfig.getTestPassword();
    private static final String INVALID_PASSWORD = "123";

    public void randomEmail() {
        step("вводим рандомный адрес", () ->
            EMAIL_INPUT.sendKeys(faker.internet().emailAddress())
        );
    }

    // Метод для получения элемента ошибки по тексту
    public SelenideElement getErrorElement(String errorText) {
        return $x("//p[contains(@class, '_errorMessage_') and contains(text(), '" + errorText + "')]");
    }

    // Метод для получения текста ошибки
    public String getErrorMessageText(String expectedError) {
        return getErrorElement(expectedError)
                .shouldBe(visible)
                .getText();
    }

    public void notCorrectEmail() {
        step("вводим некорректный адрес", () ->
            EMAIL_INPUT.sendKeys("kval")
        );
    }

    public void validEmail() {
        step("вводим ранее зарегистрированный адрес", () ->
            EMAIL_INPUT.sendKeys(EMAIL)
        );
    }

    public void validPass() {
        step("вводим стандартный пароль", () ->
            PASSWORD_INPUT.sendKeys(VALID_PASSWORD)
        );
    }

    public void validRepeatPass() {
        step("поле Повторить Пароль заполняем валидным паролем", () ->
            REPEAT_PASSWORD_INPUT.sendKeys(VALID_PASSWORD)
        );
    }

    public void PassThreeNumber() {
        step("поле Пароль заполнено менее 8 символов", () ->
            PASSWORD_INPUT.sendKeys(INVALID_PASSWORD)
        );
    }

    public void NotEquelRepeatPass() {
        step("поле Повторить Пароль не совпадает с Паролем", () ->
            REPEAT_PASSWORD_INPUT.sendKeys(INVALID_PASSWORD)
        );
    }

    public void clickBtnLogin() {
        step("кликаем по кнопке 'Войти'", () ->
            UniversalMethod.clickButton(LOGIN_BUTTON, LOGIN_TEXT)
        );
    }

    public void notShowErrorPass() {
        PASSWORD_ERROR.shouldNot(visible);
    }

    public void notShowErrorRepeatPass() {
        REPEAT_PASSWORD_ERROR.shouldNot(visible);
    }

    public void clickRecoverPass() {
        step("нажимаем на кнопку восстановить пароль", () ->
            UniversalMethod.clickButton(RECOVER_PASS, RECOVER_PASS_TEXT)
        );
    }

    public String textBtnRegistration() {
        return REGISTRATION.shouldBe(visible).getText();
    }

    public void clickRegistr() {
        UniversalMethod.clickButton(REGISTRATION, REGISTRATION_TEXT);
    }

//    Страница восстановления пароля

    public String showH2() {
        return HEAD2_BUTTON.shouldBe(visible).getText();
    }

    public void showP() {
        P_TEXT.shouldBe(visible).getText();
    }

    public void clickBtnRecover() {
        step("кликаем по кнопке восстановить пароль", () ->
            UniversalMethod.clickButton(RECOVER_BUTTON, RECOVER_PASS_TEXT )
        );
    }

    public void clickBtnBack() {
        step("кликаем по кнопке в виде стрелки", () ->
            BACK_BUTTON.click()
        );
    }

    //    Страница Регистрации
    public String showH2Reg() {
        return HEAD2_TEXT_REGISTER.shouldBe(visible).getText();
    }

    public void inputName() {
        step("Вводим имя", () ->
            NAME_INPUT.setValue(faker.name().firstName())
        );
    }

    public void inputPhone() {
        step("Заполняем графу телефон", () -> {
            String phoneNumber = faker.numerify("9#########");
            for (char digit : phoneNumber.toCharArray()){
                NUMBER_INPUT.sendKeys(String.valueOf(digit));
                sleep(50); // небольшая задержка между вводом символов
            }
        });
    }

    public String valueName() {
        return NAME_INPUT.getAttribute("value");
    }

    public void deleteName() {
        step("удаляем имя из поля", () ->
            NAME_INPUT.doubleClick().sendKeys(Keys.DELETE)
        );
    }

    public void inputTelegram() {
        step("Заполняем графу телеграмм", () ->
            TELEGRAMM_INPUT.sendKeys(faker.internet().emailAddress())
        );
    }

    public String emptyValue() {
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

    public void clickBtnRegistr() {
        step("Кликаем по кнопке далее", () ->
            UniversalMethod.clickButton(NEXT_BUTTON, "Следующий шаг")
        );
    }

    public void clickBtnRegister() {
        step("Кликаем по кнопке Зарегистироваться", () ->
            UniversalMethod.clickButton(REGISTER_BUTTON, REGISTER_TEXT)
        );
    }

    public String disableBtnRegistr() {
        return NEXT_BUTTON.getAttribute("disabled");
    }

    public String showErrorRepeatPass() {
        return REPEAT_PASSWORD_ERROR.shouldBe(visible).getText();
    }

    public void clickBtnBackFromRegistr() {
        step("Кликаем по кнопке назад на стр Зарегистироваться", () ->
            UniversalMethod.clickButton(BACK_BUTTON_REGISTER, "Назад")
        );
    }

    public void clickBtnEnter() {
        step("Кликаем по кнопке Войти на стр зарегистрироваться", () ->
            UniversalMethod.clickButton(LOGIN_BUTTON_FROM_REGISTER, LOGIN_TEXT)
        );
    }

    public void validPerson() {
        validEmail();
        validPass();
        clickBtnLogin();
        WaitHelper.waitForAnimationComplete();
        WaitHelper.waitForPageLoad();
    }
}