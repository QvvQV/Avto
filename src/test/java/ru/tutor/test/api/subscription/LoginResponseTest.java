package ru.tutor.test.api.subscription;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import ru.tutor.config.TestConfig;
import ru.tutor.model.account.LoginResponse;
import ru.tutor.steps.account.AuthSteps;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

import ru.tutor.constants.Tags;

@Epic("Аутентификация")
@Feature("Проверка корректности ответа")
@DisplayName("Тесты эндпоинта POST /user/login")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag(Tags.INTEGRATION)
@Tag(Tags.API)
public class LoginResponseTest {

    private static final String LOGIN_ENDPOINT = "POST /user/login";

    private final AuthSteps userSteps = new AuthSteps(TestConfig.getCurrentApiUrl());

    @Test
    @Order(1)
    @DisplayName("Успешный логин - полная проверка ответа")
    @Story("Успешный вход")
    @Severity(SeverityLevel.BLOCKER)
    @Tag(Tags.POSITIVE)
    void successfulLogin() {
        Response response = step(LOGIN_ENDPOINT + " с валидными данными", () ->
                userSteps.login(
                        TestConfig.getTestEmail(),
                        TestConfig.getTestPassword()
                )
        );

        LoginResponse loginResponse = userSteps.validateSuccessfulLogin(response);
        userSteps.validateTokenReadBack(loginResponse.token());
    }

    @Test
    @Order(2)
    @DisplayName("Неверный пароль - проверка тела ошибки")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void wrongPassword() {
        Response response = step(LOGIN_ENDPOINT + " с неверным паролем", () ->
                userSteps.login(
                        TestConfig.getTestEmail(),
                        "wrongPassword123"
                )
        );

        userSteps.validateAuthError(response);
    }

    @Test
    @Order(3)
    @DisplayName("Неверный email - проверка структуры ошибки")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void wrongEmail() {
        Response response = step(LOGIN_ENDPOINT + " с несуществующим email", () ->
                userSteps.login(
                        "nonexistent@test.ru",
                        TestConfig.getTestPassword()
                )
        );

        userSteps.validateAuthError(response);
    }

    @Test
    @Order(4)
    @DisplayName("Пустые поля - проверка тела ошибки")
    @Story("Валидация")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void emptyCredentials() {
        Response response = step(LOGIN_ENDPOINT + " с пустыми полями", () ->
                userSteps.login("", "")
        );

        userSteps.validateAuthError(response);
    }

    @Test
    @Order(5)
    @DisplayName("Email в неверном формате - проверка валидации")
    @Story("Валидация")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void invalidEmailFormat() {
        Response response = step(LOGIN_ENDPOINT + " с email в неверном формате", () ->
                userSteps.login("invalid-email-format", "anyPassword123")
        );

        userSteps.validateAuthError(response);
    }

    @Test
    @Order(6)
    @DisplayName("Очень длинный email - проверка тела ошибки")
    @Story("Граничные значения")
    @Severity(SeverityLevel.MINOR)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void veryLongEmail() {
        String longEmail = "a".repeat(200) + "@test.com";

        Response response = step(LOGIN_ENDPOINT + " с очень длинным email", () ->
                userSteps.login(longEmail, "anyPassword123")
        );

        userSteps.validateAuthError(response);
    }

    @Test
    @Order(7)
    @DisplayName("Очень длинный пароль - проверка тела ошибки")
    @Story("Граничные значения")
    @Severity(SeverityLevel.MINOR)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void veryLongPassword() {
        String longPassword = "p".repeat(1000);

        Response response = step(LOGIN_ENDPOINT + " с очень длинным паролем", () ->
                userSteps.login(TestConfig.getTestEmail(), longPassword)
        );

        userSteps.validateAuthError(response);
    }
}