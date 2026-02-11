package ru.tutor.test;

import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.*;

import ru.tutor.base.BaseUiTest;
import ru.tutor.page.Registration;
import ru.tutor.page.addressPage;

import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;

public class loginTest extends BaseUiTest {

    @BeforeEach
    void beforeEach() {
        step("Открытие стр", () -> {
            open(addressPage.login);
        });
        step("проверяем title", () -> {
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
    @AllureId("15")
    //        @RepeatedTest(3)
    @DisplayName("открытие стр login")
    public void OpenPageLogin() {
    }

    @Test
    @AllureId("16")
    @DisplayName("текст ошибки при не зарегистрирован пользователе")
    public void showTextErrorFromNotUser() {
        Registration.randomEmail();
        Registration.validPass();
        step("кликаем по кнопке Войти", Registration::clickBtnLogin);
        step("Проверяем текст ошибки под полем Логин", () -> {
            String error = Registration.getErrorMessageText("Не подходит адрес почты?");
            Assertions.assertEquals("Не подходит адрес почты? Напишите нам, и мы поможем восстановить доступ!", error);
        });
        step("Проверяем текст ошибки под полем Пароль", () -> {
            String invalidPassLogin = Registration.getErrorMessageText("Неправильный логин или пароль");
            Assertions.assertEquals("Неправильный логин или пароль", invalidPassLogin);
        });
    }

    @Test
    @AllureId("22")
    @DisplayName("текст ошибки при некорректном логине")
    public void showTextErrorFromNotKorrectLogin() {
        Registration.notKorrectEmail();
        Registration.validPass();
        step("Кликаем по кнопке Войти", Registration::clickBtnLogin);
        step("Проверяем текст ошибки под полем Логин", () -> {
            String error = Registration.getErrorMessageText("Не подходит адрес почты?");
            Assertions.assertEquals("Не подходит адрес почты? Напишите нам, и мы поможем восстановить доступ!", error);
        });
        step("Проверяем что ошибка одна на стр", () -> {
            Registration.notShowErrorPass();
        });
    }

//    @Test
    @AllureId("27")
    @DisplayName("открытие стр Recover - восстановление пароля")
    public void OpenPageRecover() {
        Registration.clickRecoverPass();
        step("Проверяем текст Заголовка на стр восстановить пароль", () -> {
            Assertions.assertEquals("Восстановление пароля", Registration.showH2());
        });
        step("Проверяем видимость текста Подзаголовка на стр восстановить пароль", () -> {
            Registration.showP();
        });
    }

    @Test
    @AllureId("17")
    @DisplayName("текст ошибки при пустом поле на стр Восстановить пароль")
    public void showTextErrorFromRecoverPass() {
        Registration.clickRecoverPass();
        Registration.clickBtnRecover();
        step("Проверяем текст ошибки под полем ваша электронная почта", () -> {
            String error = Registration.getErrorMessageText("Это поле обязательное");
            Assertions.assertEquals("Это поле обязательное", error);
        });
    }

    @Test
    @AllureId("25")
    @DisplayName("текст сообщения после ввода адреса почты")
    public void showTextAfterPutEmail() {
        Registration.clickRecoverPass();
        Registration.randomEmail();
        Registration.clickBtnRecover();
        step("Сравниванием текст после ввода почты", () -> {
            String error = Registration.getErrorMessageText("Пользователь не найден!");
            Assertions.assertEquals("Пользователь не найден!", error);
        });
    }

    @Test
    @AllureId("24")
    @DisplayName("переход со стр восстановить пароль на стр логин/пароль")
    public void backRecoveryLogin() {
        Registration.clickRecoverPass();
        step("проверяем Заголовок на стр Восстановить пароль", () -> {
            Assertions.assertEquals("Восстановление пароля", Registration.showH2());
        });
        Registration.clickBtnBack();
        step("Проверяем что попали на стр Логин/пароль", () -> {
            Assertions.assertEquals("Зарегистрироваться", Registration.textBtnRegistration());
        });
    }

    @Test
    @AllureId("28")
    @DisplayName("переход на стр регистрации")
    public void goRegistr() {
        step("кликаем по кнопке регистрация", Registration::clickRegistr);
        step("Проверяем заголовок стр", () -> {
            Assertions.assertEquals("Регистрация", Registration.showH2Reg());
        });
        step("проверяем что кнопка Далее disable=true", () -> {
            Assertions.assertEquals("true", Registration.disablBtnRegist());
        });
    }

    @Test
    @AllureId("26")
    @DisplayName("заполнение полей")
    public void inputField() {
        step("кликаем по кнопке регистрация", Registration::clickRegistr);
        step("Проверяем заголовок стр", () -> {
            Assertions.assertEquals("Регистрация", Registration.showH2Reg());
        });
        Registration.inputName();
        Registration.inputNumber();
        Registration.inputTelegramm();
        step("Проверяем что в полях имя/телеграмм/телефон есть значения", () -> {
            Assertions.assertEquals("все поля заполнены", Registration.emptyValue());
        });
    }

    @Test
    @AllureId("30")
    @DisplayName("текст ошибки под полем имя")
    public void showTextErrorUnderName() {
        step("кликаем по кнопке регистрация", Registration::clickRegistr);
        step("Проверяем заголовок стр", () -> {
            Assertions.assertEquals("Регистрация", Registration.showH2Reg());
        });
        Registration.inputName();
        Registration.inputTelegramm();
        Registration.inputNumber();
        Registration.deleteName();
        step("проверяем что кнопка Далее disable=false", () -> {
            Assertions.assertNull(Registration.disablBtnRegist());
        });
        Registration.clickBtnRegistr();
        step("сверяем текст ошибки под полем имя", () -> {
            String error = Registration.getErrorMessageText("Это поле обязательное");
            Assertions.assertEquals("Это поле обязательное", error);
        });
        step("Проверяем что поле имя пустое", () -> {
            Assertions.assertEquals("поле имя пустое", Registration.emptyValue());
        });
    }

    @Test
    @AllureId("21")
    @DisplayName("текст ошибки под полем почта/пароль/повторите пароль на стр регистрация")
    public void showTextErrorUnderEmailPassRepeat() {
        step("кликаем по кнопке регистрация", Registration::clickRegistr);
        step("Проверяем заголовок стр", () -> {
            Assertions.assertEquals("Регистрация", Registration.showH2Reg());
        });
        Registration.inputName();
        Registration.clickBtnRegistr();
        step("Проверяем заголовок стр", () -> {
            Assertions.assertEquals("Регистрация", Registration.showH2Reg());
        });
        Registration.clickBtnRegister();
        step("Проверяем текст ошибки под полем адресом электронной почты", () -> {
            String error = Registration.getErrorMessageText("Введите корректный адрес");
            Assertions.assertEquals("Введите корректный адрес", error);
        });
        step("Проверяем текст ошибки под полем Пароль", () -> {
            String errorPass = Registration.getErrorMessageText("Пароль должен содержать не менее 8 символов");
            Assertions.assertEquals("Пароль должен содержать не менее 8 символов", errorPass);
        });
        step("Проверяем текст ошибки под полем Повторить Пароль", () -> {
            String errorPassRepeat = Registration.getErrorMessageText("Это поле обязательное");
            Assertions.assertEquals("Это поле обязательное", errorPassRepeat);
        });
    }

    @Test
    @AllureId("29")
    @DisplayName("текст ошибки под полем пароль/повторите пароль на стр регистрация")
    public void showTextErrorUnderPassRepeat() {
        step("кликаем по кнопке регистрация", Registration::clickRegistr);
        step("Проверяем заголовок стр", () -> {
            Assertions.assertEquals("Регистрация", Registration.showH2Reg());
        });
        Registration.inputName();
        Registration.clickBtnRegistr();
        step("Проверяем заголовок стр", () -> {
            Assertions.assertEquals("Регистрация", Registration.showH2Reg());
        });
        Registration.randomEmail();
        Registration.clickBtnRegister();
        step("Проверяем текст ошибки под полем Пароль", () -> {
            String errorPass = Registration.getErrorMessageText("Пароль должен содержать не менее 8 символов");
            Assertions.assertEquals("Пароль должен содержать не менее 8 символов", errorPass);
        });
        step("Проверяем текст ошибки под полем Повторите Пароль", () -> {
            String errorPassRepeat = Registration.getErrorMessageText("Это поле обязательное");
            Assertions.assertEquals("Это поле обязательное", errorPassRepeat);
        });
    }

    @Test
    @AllureId("32")
    @DisplayName("текст ошибки при пароле из 3-х цифр на стр регистрация")
    public void showTextErrorUnderPassNumber3() {
        step("кликаем по кнопке регистрация", Registration::clickRegistr);
        step("Проверяем заголовок стр", () -> {
            Assertions.assertEquals("Регистрация", Registration.showH2Reg());
        });
        Registration.inputName();
        Registration.clickBtnRegistr();
        step("Проверяем заголовок стр", () -> {
            Assertions.assertEquals("Регистрация", Registration.showH2Reg());
        });
        Registration.randomEmail();
        Registration.PassThreeNumber();
        Registration.clickBtnRegister();
        step("Проверяем текст ошибки под полем Пароль", () -> {
            String errorPass = Registration.getErrorMessageText("Пароль должен содержать не менее 8 символов");
            Assertions.assertEquals("Пароль должен содержать не менее 8 символов", errorPass);
        });
        step("Проверяем текст ошибки под полем Повторите Пароль", () -> {
            String error = Registration.getErrorMessageText("Пароли должны совпадать");
            Assertions.assertEquals("Пароли должны совпадать", error);
        });
    }

    @Test
    @AllureId("19")
    @DisplayName("текст ошибки при Пароль != Повторите пароль на стр регистрация")
    public void showTextErrorPassNotEquelRepeatPass() {
        step("кликаем по кнопке регистрация", Registration::clickRegistr);
        step("Проверяем заголовок стр", () -> {
            Assertions.assertEquals("Регистрация", Registration.showH2Reg());
        });
        Registration.inputName();
        Registration.clickBtnRegistr();
        step("Проверяем заголовок стр", () -> {
            Assertions.assertEquals("Регистрация", Registration.showH2Reg());
        });
        Registration.randomEmail();
        Registration.validPass();
        Registration.NotEquelRepeatPass();
        Registration.clickBtnRegister();
        step("Проверяем текст ошибки под полем Пароль", () -> {
            String error = Registration.getErrorMessageText("Пароли должны совпадать");
            Assertions.assertEquals("Пароли должны совпадать", error);
        });
        step("Проверяем что ошибка одна на стр", () -> {
            Registration.notShowErrorPass();
            Registration.notShowErrorRepeatPass();
        });
    }

    @Test
    @AllureId("23")
    @DisplayName("возвращаемся на стр ввода имени из стр зарегистрироваться")
    public void backFromRegistr() {
        step("кликаем по кнопке регистрация", Registration::clickRegistr);
        step("Проверяем заголовок стр", () -> {
            Assertions.assertEquals("Регистрация", Registration.showH2Reg());
        });
        Registration.inputName();
        String exist = Registration.valueName();
        Registration.clickBtnRegistr();
        step("Проверяем заголовок стр", () -> {
            Assertions.assertEquals("Регистрация", Registration.showH2Reg());
        });
        Registration.clickBtnBackFromRegistr();
        step("проверяем что мы на стр для ввода имени, прошлое значение имени с текущим", () -> {
            Assertions.assertEquals(exist, Registration.valueName());
        });
    }

    @Test
    @AllureId("18")
    @DisplayName("переходим со стр зарегистрироваться на стр войти")
    public void RegisterGoEnter() {

        step("кликаем по кнопке регистрация", Registration::clickRegistr);
        step("Проверяем заголовок стр", () -> {
            Assertions.assertEquals("Регистрация", Registration.showH2Reg());
        });
        Registration.inputName();
        Registration.clickBtnRegistr();
        step("Проверяем заголовок стр", () -> {
            Assertions.assertEquals("Регистрация", Registration.showH2Reg());
        });
        Registration.clickBtnEnter();
        step("Проверяем что попали на стр Логин/пароль", () -> {
            Assertions.assertEquals("Зарегистрироваться", Registration.textBtnRegistration());
        });
    }

    @Test
    @AllureId("54")
    @DisplayName("переход со стр зарегистр на стр ЛК")
    public void EnterForPersonalAccount() {

        step("кликаем по кнопке регистрация", Registration::clickRegistr);
        step("Проверяем заголовок стр", () -> {
            Assertions.assertEquals("Регистрация", Registration.showH2Reg());
        });
        Registration.inputName();
        Registration.clickBtnRegistr();
        step("Проверяем заголовок стр", () -> {
            Assertions.assertEquals("Регистрация", Registration.showH2Reg());
        });
        Registration.randomEmail();
        Registration.validPass();
        Registration.validRepeatPass();
        Registration.clickBtnRegister();
        step("Проверяем что попали на стр Личного кабинета", () -> {
            Assertions.assertEquals("https://client.ttplace.ru/user/catalog", WebDriverRunner.getWebDriver().getCurrentUrl());
        });
    }

    @Test
    @AllureId("57")
    @DisplayName("регистрируемся с ранее созданного акк")
    public void RegistrathionFromEarlyAcc() {

        Registration.validEmail();
        Registration.validPass();
        step("кликаем по кнопке Войти", Registration::clickBtnLogin);
        step("Проверяем что попали на стр Личного кабинета", () -> {
            Assertions.assertEquals("https://client.ttplace.ru/user/main", WebDriverRunner.getWebDriver().getCurrentUrl());
        });
    }


//    @Test
//    @AllureId("1")
//    @DisplayName("Открытие главной страницы")
//    @Owner("Vadim")
//    public void testOps() {
//        step("Ввести в адресную строку -> https://ttplace.ru/");
//        step("кликнуть enter");
//    }

}
