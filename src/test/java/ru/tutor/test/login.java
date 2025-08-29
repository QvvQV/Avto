package ru.tutor.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Attachment;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeOptions;
import ru.tutor.page.CloudPage;
import ru.tutor.page.Registration;
import ru.tutor.page.addressPage;

import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;

public class login {

    //    @BeforeEach
    @BeforeAll
    public static void setupAll() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        Configuration.browserCapabilities = options;
        Configuration.browserSize = "1920x1080";
        SelenideLogger.addListener("allure.properties", new AllureSelenide()
                .screenshots(true)
                .savePageSource(true));
    }

    @Attachment(value = "Screenshot", type = "image/png", fileExtension = "png")
    public void takeScreenshot() {
        ((TakesScreenshot) WebDriverRunner.getWebDriver()).getScreenshotAs(OutputType.BYTES);
    }

    @AfterAll
    public static void quitDriver() {
        closeWebDriver();
        SelenideLogger.removeListener("allure.properties");
    }

    @Test
//        @RepeatedTest(3)
    @DisplayName("открытие стр login")
    public void OpenPageLogin() {
        step("Открытие стр", () -> {
            open(addressPage.login);
        });
        step("проверяем title", () -> {
            String expected = "TutorPlace";
            Assertions.assertEquals(expected, title());
        });

    }

    @Test
    @DisplayName("текст ошибки при пустых полях логин/пароль")
    public void showTextErrorFromEmpty() {
        step("Открытие стр", () -> {
            open(addressPage.login);
        });
        step("проверяем title", () -> {
            String expected = "TutorPlace";
            Assertions.assertEquals(expected, title());
        });
        step("кликаем по кнопке Войти", () -> {
            Registration.clickBtnLogin();
        });
        step("Проверяем что определённого текста не появилось на стр", () -> {
            Registration.notShowErrorLoginTwo();
        });
        step("Проверяем текст ошибки под полем Логин", () -> {
            Assertions.assertEquals("Это поле обязательное", Registration.showErrorLogin());
        });
        step("Проверяем текст ошибки под полем Пароль", () -> {
            Assertions.assertEquals("Это поле обязательное", Registration.showErrorPass());
        });
    }

    @Test
    @DisplayName("текст ошибки при пустом поле пароль")
    public void showTextErrorFromEmptyPass() {
        step("Открытие стр", () -> {
            open(addressPage.login);
        });
        step("проверяем title", () -> {
            String expected = "TutorPlace";
            Assertions.assertEquals(expected, title());
        });
        Registration.randomEmail();
        step("кликаем по кнопке Войти", () -> {
            Registration.clickBtnLogin();
        });
        step("Проверяем что ошибка одна на стр", () -> {
            Registration.notShowErrorPass();
        });
        step("Проверяем текст ошибки под полем Пароль", () -> {
            Assertions.assertEquals("Это поле обязательное", Registration.showErrorLogin());
        });
    }

    @Test
    @DisplayName("текст ошибки при пустом поле Логин")
    public void showTextErrorFromEmptyLogin() {
        step("Открытие стр", () -> {
            open(addressPage.login);
        });
        step("проверяем title", () -> {
            String expected = "TutorPlace";
            Assertions.assertEquals(expected, title());
        });
        Registration.validPass();
        step("кликаем по кнопке Войти", () -> {
            Registration.clickBtnLogin();
        });
        step("Проверяем что ошибка одна на стр", () -> {
            Registration.notShowErrorPass();
        });
        step("Проверяем текст ошибки под полем Пароль", () -> {
            Assertions.assertEquals("Это поле обязательное", Registration.showErrorLogin());
        });
    }

    @Test
    @DisplayName("текст ошибки при не зарегистрирован пользователе")
    public void showTextErrorFromNotUser() {
        step("Открытие стр", () -> {
            open(addressPage.login);
        });
        step("проверяем title", () -> {
            String expected = "TutorPlace";
            Assertions.assertEquals(expected, title());
        });
        Registration.randomEmail();
        Registration.validPass();
        step("кликаем по кнопке Войти", () -> {
            Registration.clickBtnLogin();
        });
        step("Проверяем текст ошибки под полем Логин", () -> {
            Assertions.assertEquals("Не подходит адрес почты? Напишите нам, и мы поможем восстановить доступ!", Registration.showErrorLoginTwo());
        });
        step("Проверяем текст ошибки под полем Пароль", () -> {
            Assertions.assertEquals("Неправильный логин или пароль", Registration.showErrorLogin());
        });
    }

    @Test
    @DisplayName("открытие стр Recover - восстановление пароля")
    public void OpenPageRecover() {
        step("Открытие стр", () -> {
            open(addressPage.login);
        });
        step("проверяем title", () -> {
            String expected = "TutorPlace";
            Assertions.assertEquals(expected, title());
        });
        Registration.clickRecoverPass();
        step("проверяем текст Заголовка на стр восстановить пароль", () -> {
            Assertions.assertEquals("Восстановление пароля", Registration.showH2());
        });
        step("проверяем текст Подзаголовка на стр восстановить пароль", () -> {
            Registration.showP();
        });
    }

    @Test
    @DisplayName("текст ошибки при пустом поле на стр Восстановить пароль")
    public void showTextErrorFromRecoverPass() {
        step("Открытие стр", () -> {
            open(addressPage.login);
        });
        step("проверяем title", () -> {
            String expected = "TutorPlace";
            Assertions.assertEquals(expected, title());
        });
        Registration.clickRecoverPass();
        Registration.clickBtnRecover();
        step("Проверяем текст ошибки под полем ваша электронная почта", () -> {
            Assertions.assertEquals("Это поле обязательное", Registration.showErrorLogin());
        });
    }

    @Test
    @DisplayName("текст сообщения после ввода адреса почты")
    public void showTextAfterPutEmail() {
        step("Открытие стр", () -> {
            open(addressPage.login);
        });
        step("проверяем title", () -> {
            String expected = "TutorPlace";
            Assertions.assertEquals(expected, title());
        });
        Registration.clickRecoverPass();
        Registration.randomEmail();
        Registration.clickBtnRecover();
        step("Сравниванием текст после ввода почты", () -> {
            Assertions.assertEquals("Новый пароль был отправлен на почту " + CloudPage.atrEmail(), Registration.showP());
            Assertions.assertEquals("Обязательно проверьте папку спам.", Registration.showP1());
            Assertions.assertEquals("Если письмо так и не пришло - напиши нам, и мы можем восстановить доступ!", Registration.showP2());
            Assertions.assertEquals("Я понял(а)", Registration.textBtnRecover());
        });
    }

    @Test
    @DisplayName("переход со стр восстановить пароль на стр логин/пароль")
    public void backRecoveryLogin() {
        step("Открытие стр", () -> {
            open(addressPage.login);
        });
        step("проверяем title", () -> {
            String expected = "TutorPlace";
            Assertions.assertEquals(expected, title());
        });
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
    @DisplayName("переход на стр регистрации")
    public void goRegistr(){
        step("Открытие стр", () -> {
            open(addressPage.login);
        });
        step("проверяем title", () -> {
            String expected = "TutorPlace";
            Assertions.assertEquals(expected, title());
        });
        step("кликаем по кнопке регистрация",()->{
            Registration.clickRegistr();
        });
        step("Проверяем заголовок стр",()->{
            Assertions.assertEquals("Регистрация профиля",Registration.showH2Reg());
        });
        step("проверяем что кнопка Далее disable=true",()->{
           Assertions.assertEquals("true",Registration.disablBtnRegist());
        });
    }

    @Test
    @DisplayName("заполнение полей")
    public void inputField(){
        step("Открытие стр", () -> {
            open(addressPage.login);
        });
        step("проверяем title", () -> {
            String expected = "TutorPlace";
            Assertions.assertEquals(expected, title());
        });
        step("кликаем по кнопке регистрация",()->{
            Registration.clickRegistr();
        });
        step("Проверяем заголовок стр",()->{
            Assertions.assertEquals("Регистрация профиля",Registration.showH2Reg());
        });
        Registration.inputName();
        Registration.inputNumber();
        Registration.inputTelegramm();
        step("Проверяем что в полях имя/телеграмм/телефон есть значения",()->{
            Assertions.assertEquals("все поля заполнены", Registration.emptyValue());
        });
    }

    @Test
    @DisplayName("текст ошибки под полем имя")
    public void showTextErrorUnderName(){
                step("Открытие стр", () -> {
            open(addressPage.login);
        });
        step("проверяем title", () -> {
            String expected = "TutorPlace";
            Assertions.assertEquals(expected, title());
        });
        step("кликаем по кнопке регистрация",()->{
            Registration.clickRegistr();
        });
        step("Проверяем заголовок стр",()->{
            Assertions.assertEquals("Регистрация профиля",Registration.showH2Reg());
        });
        Registration.inputName();
        Registration.inputTelegramm();
        Registration.inputNumber();
        Registration.deleteName();
        step("проверяем что кнопка Далее disable=false",()->{
            Assertions.assertEquals(null,Registration.disablBtnRegist());
        });
        Registration.clickBtnRegistr();
        step("сверяем текст ошибки под полем имя",()->{
            Assertions.assertEquals("Это поле обязательное",Registration.showErrorLogin());
        });
        step("Проверяем что поле имя пустое",()->{
            Assertions.assertEquals("поле имя пустое",Registration.emptyValue());
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
