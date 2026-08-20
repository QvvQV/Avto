package ru.tutor.test.api.user;

import io.qameta.allure.*;
import io.qameta.allure.junit5.AllureJunit5;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tutor.config.TestConfig;
import ru.tutor.steps.AccountSteps;
import ru.tutor.steps.AuthSteps;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(AllureJunit5.class)
@Epic("API Тесты пользователя")
@Feature("Управление профилем")
@DisplayName("Тесты смены пароля")
public class EditPasswordTest {

    private final AccountSteps userSteps = new AccountSteps(TestConfig.getCurrentApiUrl());
    private final AuthSteps authSteps = new AuthSteps(TestConfig.getCurrentApiUrl());
    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();

    private static String token;

    @BeforeAll
    public static void setUp() {
        Allure.label("component", "API");
        Allure.label("feature", "Password");
    }

    @BeforeEach
    void setUpAuth() {
        token = step("Логин пользователя", () ->
                authSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD));
    }

    @Test
    @Order(1)
    @DisplayName("PUT /user/edit/pass - успешная смена пароля")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("positive")
    void editPasswordSuccess() {
        // Меняем на тот же пароль, чтобы потом можно было залогиниться
        Response response = step("PUT /user/edit/pass", () ->
                userSteps.editPassword(token, TEST_PASSWORD, TEST_PASSWORD)
        );
        step("Валидация ответа", () ->
                userSteps.validateEditPasswordSuccess(response)
                );
    }

    @Test
    @Order(2)
    @DisplayName("PUT /user/edit/pass с неверным старым паролем")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void editPasswordWrongOldPassword() {
        Response response = step ("PUT /user/edit/pass с неверным старым паролем", () ->
                userSteps.editPassword(token, "wrong_pass", TEST_PASSWORD)
        );
        step("Проверка ошибки", () ->
                userSteps.validateEditPasswordError(response, "ERR_INCORRECT_PREVIOUS_PASSWORD")
        );
    }

    @Test
    @Order(3)
    @DisplayName("PUT /user/edit/pass без авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void editPasswordWithoutAuth() {
        Response response = step("PUT /user/edit/pass без токена", () ->
                userSteps.editPassword("", TEST_PASSWORD, TEST_PASSWORD)
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(4)
    @DisplayName("PUT /user/edit/pass невалидный токен")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void editPasswordInvalidToken() {
        Response response = step("PUT /user/edit/pass - невалидный токен", () ->
                userSteps.editPassword("invalid", TEST_PASSWORD, TEST_PASSWORD)
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(5)
    @DisplayName("PUT /user/edit/pass пустой старый пароль")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void editEmptyOldPassword() {
        Response response = step("PUT /user/edit/pass - пустой старый пароль", () ->
                userSteps.editPassword(token, "", TEST_PASSWORD)
        );
        step("Валидация ответа", () -> {
            assertEquals(422, response.getStatusCode());
            String message = response.jsonPath().getString("error.messages.oldPassword");
            assertNotNull(message);
            assertEquals("Значение не должно быть пустым.", message);
        });
    }

    @Test
    @Order(6)
    @DisplayName("PUT /user/edit/pass пустой новый пароль")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void editEmptyNewPassword() {
        Response response = step("PUT /user/edit/pass - пустой старый пароль", () ->
                userSteps.editPassword(token, TEST_PASSWORD, "")
        );
        step("Валидация ответа", () -> {
            assertEquals(422, response.getStatusCode());
            String message = response.jsonPath().getString("error.messages.newPassword");
            assertNotNull(message);
            assertEquals("Значение не должно быть пустым.", message);
        });
    }

    /**
     * Todo тест на короткий пароль вовзращает статус 200. Проверка условия на кол-во знаков на стороне фронта
     */
}
