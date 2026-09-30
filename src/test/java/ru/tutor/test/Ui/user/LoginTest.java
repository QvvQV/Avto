package ru.tutor.test.Ui.user;

import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import org.junit.jupiter.api.*;

import ru.tutor.base.BaseUiTest;
import ru.tutor.page.account.Registration;
import ru.tutor.constants.AddressPage;

import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;

public class LoginTest extends BaseUiTest {

    private final Registration registration = new Registration();

    @BeforeEach
    void beforeEach() {

        step("Открытие стр", () ->
            open(AddressPage.LOGIN)
        );
        step("Проверяем title", () -> {
            String expected = "TutorPlace";
            Assertions.assertEquals(expected, title());
        });
    }

    @AfterAll
    public static void quitDriver() {
        closeWebDriver();
        SelenideLogger.removeListener("AllureSelenide");
    }

    @Test
    @Order(1)
    @DisplayName("Текст ошибки при не зарегистрирован пользователе")
    public void showTextErrorFromNotUser() {
        registration.randomEmail();
        registration.validPass();
        step("кликаем по кнопке Войти", registration::clickBtnLogin);
        step("Проверяем текст ошибки под полем Логин", () -> {
            String error = registration.getErrorMessageText("Не подходит адрес почты?");
            Assertions.assertEquals("Не подходит адрес почты? Напишите нам, и мы поможем восстановить доступ!", error);
        });
        step("Проверяем текст ошибки под полем Пароль", () -> {
            String invalidPassLogin = registration.getErrorMessageText("Неправильный логин или пароль");
            Assertions.assertEquals("Неправильный логин или пароль", invalidPassLogin);
        });
    }

    @Test
    @Order(2)
    @DisplayName("Текст ошибки при некорректном логине")
    public void showTextErrorFromNotСorrectLogin() {
        registration.notCorrectEmail();
        registration.validPass();
        step("Кликаем по кнопке Войти", registration::clickBtnLogin);
        step("Проверяем текст ошибки под полем Логин", () -> {
            String error = registration.getErrorMessageText("Не подходит адрес почты?");
            Assertions.assertEquals("Не подходит адрес почты? Напишите нам, и мы поможем восстановить доступ!", error);
        });
        step("Проверяем что ошибка одна на стр", registration::notShowErrorPass);
    }

//    @Test

    @DisplayName("открытие стр Recover - восстановление пароля")
    public void OpenPageRecover() {
        registration.clickRecoverPass();
        step("Проверяем текст Заголовка на стр восстановить пароль", () ->
            Assertions.assertEquals("Восстановление пароля", registration.showH2())
        );
        step("Проверяем видимость текста Подзаголовка на стр восстановить пароль", registration::showP);
    }

    @Test
    @Order(3)
    @DisplayName("текст ошибки при пустом поле на стр Восстановить пароль")
    public void showTextErrorFromRecoverPass() {
        registration.clickRecoverPass();
        registration.clickBtnRecover();
        step("Проверяем текст ошибки под полем ваша электронная почта", () -> {
            String error = registration.getErrorMessageText("Это поле обязательное");
            Assertions.assertEquals("Это поле обязательное", error);
        });
    }

    @Test
    @Order(4)
    @DisplayName("текст сообщения после ввода адреса почты")
    public void showTextAfterPutEmail() {
        registration.clickRecoverPass();
        registration.randomEmail();
        registration.clickBtnRecover();
        step("Сравниванием текст после ввода почты", () -> {
            String error = registration.getErrorMessageText("Пользователь не найден!");
            Assertions.assertEquals("Пользователь не найден!", error);
        });
    }

    @Test
    @Order(5)
    @DisplayName("переход со стр восстановить пароль на стр логин/пароль")
    public void backRecoveryLogin() {
        registration.clickRecoverPass();
        step("проверяем Заголовок на стр Восстановить пароль", () ->
            Assertions.assertEquals("Восстановление пароля", registration.showH2())
        );
        registration.clickBtnBack();
        step("Проверяем что попали на стр Логин/пароль", () ->
            Assertions.assertEquals("Зарегистрироваться", registration.textBtnRegistration())
        );
    }

    @Test
    @Order(6)
    @DisplayName("переход на стр регистрации")
    public void goRegister() {
        step("кликаем по кнопке регистрация", registration::clickRegistr);
        step("Проверяем заголовок стр", () ->
            Assertions.assertEquals("Регистрация", registration.showH2Reg())
        );
        step("проверяем что кнопка Далее disable=true", () ->
            Assertions.assertEquals("true", registration.disableBtnRegistr())
        );
    }

    @Test
    @Order(7)
    @DisplayName("заполнение полей")
    public void inputField() {
        step("кликаем по кнопке регистрация", registration::clickRegistr);
        step("Проверяем заголовок стр", () ->
            Assertions.assertEquals("Регистрация", registration.showH2Reg())
        );
        registration.inputName();
        registration.inputPhone();
        registration.inputTelegram();
        step("Проверяем что в полях имя/телеграмм/телефон есть значения", () ->
            Assertions.assertEquals("все поля заполнены", registration.emptyValue())
        );
    }

    @Test
    @Order(8)
    @DisplayName("текст ошибки под полем имя")
    public void showTextErrorUnderName() {
        step("кликаем по кнопке регистрация", registration::clickRegistr);
        step("Проверяем заголовок стр", () ->
            Assertions.assertEquals("Регистрация", registration.showH2Reg())
        );
        registration.inputName();
        registration.inputTelegram();
        registration.inputPhone();
        registration.deleteName();
        step("проверяем что кнопка Далее disable=false", () ->
            Assertions.assertNull(registration.disableBtnRegistr())
        );
        registration.clickBtnRegistr();
        step("сверяем текст ошибки под полем имя", () -> {
            String error = registration.getErrorMessageText("Это поле обязательное");
            Assertions.assertEquals("Это поле обязательное", error);
        });
        step("Проверяем что поле имя пустое", () ->
            Assertions.assertEquals("поле имя пустое", registration.emptyValue())
        );
    }

    @Test
    @Order(9)
    @DisplayName("текст ошибки под полем почта/пароль/повторите пароль на стр регистрация")
    public void showTextErrorUnderEmailPassRepeat() {
        step("кликаем по кнопке регистрация", registration::clickRegistr);
        step("Проверяем заголовок стр", () ->
            Assertions.assertEquals("Регистрация", registration.showH2Reg())
        );
        registration.inputName();
        registration.clickBtnRegistr();
        step("Проверяем заголовок стр", () ->
            Assertions.assertEquals("Регистрация", registration.showH2Reg())
        );
        registration.clickBtnRegister();
        step("Проверяем текст ошибки под полем адресом электронной почты", () -> {
            String error = registration.getErrorMessageText("Введите корректный адрес");
            Assertions.assertEquals("Введите корректный адрес", error);
        });
        step("Проверяем текст ошибки под полем Пароль", () -> {
            String errorPass = registration.getErrorMessageText("Пароль должен содержать не менее 8 символов");
            Assertions.assertEquals("Пароль должен содержать не менее 8 символов", errorPass);
        });
        step("Проверяем текст ошибки под полем Повторить Пароль", () -> {
            String errorPassRepeat = registration.getErrorMessageText("Это поле обязательное");
            Assertions.assertEquals("Это поле обязательное", errorPassRepeat);
        });
    }

    @Test
    @Order(10)
    @DisplayName("текст ошибки под полем пароль/повторите пароль на стр регистрация")
    public void showTextErrorUnderPassRepeat() {
        step("кликаем по кнопке регистрация", registration::clickRegistr);
        step("Проверяем заголовок стр", () ->
            Assertions.assertEquals("Регистрация", registration.showH2Reg())
        );
        registration.inputName();
        registration.clickBtnRegistr();
        step("Проверяем заголовок стр", () ->
            Assertions.assertEquals("Регистрация", registration.showH2Reg())
        );
        registration.randomEmail();
        registration.clickBtnRegister();
        step("Проверяем текст ошибки под полем Пароль", () -> {
            String errorPass = registration.getErrorMessageText("Пароль должен содержать не менее 8 символов");
            Assertions.assertEquals("Пароль должен содержать не менее 8 символов", errorPass);
        });
        step("Проверяем текст ошибки под полем Повторите Пароль", () -> {
            String errorPassRepeat = registration.getErrorMessageText("Это поле обязательное");
            Assertions.assertEquals("Это поле обязательное", errorPassRepeat);
        });
    }

    @Test
    @Order(11)
    @DisplayName("текст ошибки при пароле из 3-х цифр на стр регистрация")
    public void showTextErrorUnderPassNumber3() {
        step("кликаем по кнопке регистрация", registration::clickRegistr);
        step("Проверяем заголовок стр", () ->
            Assertions.assertEquals("Регистрация", registration.showH2Reg())
        );
        registration.inputName();
        registration.clickBtnRegistr();
        step("Проверяем заголовок стр", () ->
            Assertions.assertEquals("Регистрация", registration.showH2Reg())
        );
        registration.randomEmail();
        registration.PassThreeNumber();
        registration.clickBtnRegister();
        step("Проверяем текст ошибки под полем Пароль", () -> {
            String errorPass = registration.getErrorMessageText("Пароль должен содержать не менее 8 символов");
            Assertions.assertEquals("Пароль должен содержать не менее 8 символов", errorPass);
        });
        step("Проверяем текст ошибки под полем Повторите Пароль", () -> {
            String error = registration.getErrorMessageText("Пароли должны совпадать");
            Assertions.assertEquals("Пароли должны совпадать", error);
        });
    }

    @Test
    @Order(12)
    @DisplayName("текст ошибки при Пароль != Повторите пароль на стр регистрация")
    public void showTextErrorPassNotEqualRepeatPass() {
        step("кликаем по кнопке регистрация", registration::clickRegistr);
        step("Проверяем заголовок стр", () ->
            Assertions.assertEquals("Регистрация", registration.showH2Reg())
        );
        registration.inputName();
        registration.clickBtnRegistr();
        step("Проверяем заголовок стр", () ->
            Assertions.assertEquals("Регистрация", registration.showH2Reg())
        );
        registration.randomEmail();
        registration.validPass();
        registration.NotEquelRepeatPass();
        registration.clickBtnRegister();
        step("Проверяем текст ошибки под полем Пароль", () -> {
            String error = registration.getErrorMessageText("Пароли должны совпадать");
            Assertions.assertEquals("Пароли должны совпадать", error);
        });
        step("Проверяем что ошибка одна на стр", () -> {
            registration.notShowErrorPass();
            registration.notShowErrorRepeatPass();
        });
    }

    @Test
    @Order(13)
    @DisplayName("возвращаемся на стр ввода имени из стр зарегистрироваться")
    public void backFromRegister() {
        step("кликаем по кнопке регистрация", registration::clickRegistr);
        step("Проверяем заголовок стр", () ->
            Assertions.assertEquals("Регистрация", registration.showH2Reg())
        );
        registration.inputName();
        String exist = registration.valueName();
        registration.clickBtnRegistr();
        step("Проверяем заголовок стр", () ->
            Assertions.assertEquals("Регистрация", registration.showH2Reg())
        );
        registration.clickBtnBackFromRegistr();
        step("проверяем что мы на стр для ввода имени, прошлое значение имени с текущим", () ->
            Assertions.assertEquals(exist, registration.valueName())
        );
    }

    @Test
    @Order(14)
    @DisplayName("переходим со стр зарегистрироваться на стр войти")
    public void RegisterGoEnter() {

        step("кликаем по кнопке регистрация", registration::clickRegistr);
        step("Проверяем заголовок стр", () ->
            Assertions.assertEquals("Регистрация", registration.showH2Reg())
        );
        registration.inputName();
        registration.clickBtnRegistr();
        step("Проверяем заголовок стр", () ->
            Assertions.assertEquals("Регистрация", registration.showH2Reg())
        );
        registration.clickBtnEnter();
        step("Проверяем что попали на стр Логин/пароль", () ->
            Assertions.assertEquals("Зарегистрироваться", registration.textBtnRegistration())
        );
    }

    @Test
    @Order(15)
    @DisplayName("переход со стр зарегистр на стр ЛК")
    public void EnterForPersonalAccount() {

        step("кликаем по кнопке регистрация", registration::clickRegistr);
        step("Проверяем заголовок стр", () ->
            Assertions.assertEquals("Регистрация", registration.showH2Reg())
        );
        registration.inputName();
        registration.clickBtnRegistr();
        step("Проверяем заголовок стр", () ->
            Assertions.assertEquals("Регистрация", registration.showH2Reg())
        );
        registration.randomEmail();
        registration.validPass();
        registration.validRepeatPass();
        registration.clickBtnRegister();
        step("Проверяем что попали на стр Личного кабинета", () ->
            Assertions.assertEquals("https://client.ttplace.ru/user/catalog", WebDriverRunner.getWebDriver().getCurrentUrl())
        );
    }

    @Test
    @Order(16)
    @DisplayName("регистрируемся с ранее созданного акк")
    public void RegistrationFromEarlyAcc() {

        registration.validEmail();
        registration.validPass();
        step("кликаем по кнопке Войти", registration::clickBtnLogin);
        step("Проверяем что попали на стр Личного кабинета", () ->
            Assertions.assertEquals("https://client.ttplace.ru/user/main", WebDriverRunner.getWebDriver().getCurrentUrl())
        );
    }
}
