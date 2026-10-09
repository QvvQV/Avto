package ru.tutor.test.api.user;

import io.qameta.allure.*;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import ru.tutor.config.TestConfig;
import ru.tutor.flow.account.FreshUserFixture;
import ru.tutor.steps.account.AccountSteps;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import ru.tutor.constants.Tags;
import ru.tutor.steps.account.AuthSteps;

@Epic("API Тесты пользователя")
@Feature("Управление профилем")
@DisplayName("Тесты смены пароля")
@Tag(Tags.INTEGRATION)
public class EditPasswordTest {

    private final AccountSteps userSteps = new AccountSteps(TestConfig.getCurrentApiUrl());
    private final AuthSteps authSteps = new AuthSteps(TestConfig.getCurrentApiUrl());
    private final String TEST_PASSWORD = TestConfig.getTestPassword();
    private static final String newPassword = "87654321";

    private final FreshUserFixture freshUserFixture = new FreshUserFixture();
    private static String freshEmail;

    private static String token;

    @BeforeAll
    public static void setUp() {
        Allure.label("component", "API");
        Allure.label("feature", "Password");
    }

    @BeforeEach
    void setUpAuth() {
        FreshUserFixture.FreshUser freshUser = step("Регистрация нового пользователя (POST /register)",
                freshUserFixture::register);
        token = freshUser.token();
        freshEmail = freshUser.email();
    }

    @Test
    @Order(1)
    @DisplayName("PUT /user/edit/pass - успешная смена пароля")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    void editPasswordSuccess() {
        // Меняем на тот же пароль, чтобы потом можно было залогиниться
        Response response = step("PUT /user/edit/pass", () ->
                userSteps.editPassword(token, TEST_PASSWORD, newPassword)
        );
        step("Валидация ответа (message=ok)", () ->
                userSteps.validateEditPasswordSuccess(response)
                );
        step("Вход с новым паролем -> 200 + токен", () ->
                authSteps.loginAndGetTokens(freshEmail, newPassword));
        step("Вход со старым паролем -> 401", () -> {
            Response oldLogin = authSteps.login(freshEmail, TEST_PASSWORD);
            assertEquals(401, oldLogin.getStatusCode());
            oldLogin.then().contentType(ContentType.JSON);
        });
    }

    @Test
    @Order(2)
    @DisplayName("PUT /user/edit/pass с неверным старым паролем")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
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
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
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
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
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
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void editEmptyOldPassword() {
        Response response = step("PUT /user/edit/pass - пустой старый пароль", () ->
                userSteps.editPassword(token, "", newPassword)
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
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void editEmptyNewPassword() {
        Response response = step("PUT /user/edit/pass - пустой новый пароль", () ->
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
