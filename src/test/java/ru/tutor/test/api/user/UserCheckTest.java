package ru.tutor.test.api.user;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import ru.tutor.steps.account.AuthSteps;
import ru.tutor.config.TestConfig;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;
import ru.tutor.constants.Tags;


@Epic("API Тесты пользователя")
@Feature("Аутентификация и профиль")
@DisplayName("Тесты: ")

@Tag(Tags.INTEGRATION)
@Tag(Tags.CONTRACT)
public class UserCheckTest {

    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();
    private final AuthSteps userSteps = new AuthSteps(BASE_URL);
    private static String token;

    @BeforeEach
    void setUp() {
        // Логин перед каждым тестом
        token = step("Логин", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );
    }

    @Test
    @DisplayName("Проверка авторизации")
    void checkAuthorization() {
        Response response = step("GET /user с валидным токеном", () ->
                userSteps.checkAuth(token)
        );

        step("Валидация ответа", () ->
                userSteps.validateAuthCheckResponse(response)
        );
    }

    @Test
    @DisplayName("Проверка авторизации без токена (401)")
    @Severity(SeverityLevel.CRITICAL)
    void checkAuthWithoutToken() {
        Response response = step("GET /user без токена", () ->
                userSteps.checkAuth("")
        );

        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @DisplayName("Проверка авторизации с невалидным токеном (401)")
    @Severity(SeverityLevel.CRITICAL)
    void checkAuthWithInvalidToken() {
        Response response = step ("GET /user с невалидным токеном", () ->
                userSteps.checkAuth("invalid_token")
        );

        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode()));
    }
}
