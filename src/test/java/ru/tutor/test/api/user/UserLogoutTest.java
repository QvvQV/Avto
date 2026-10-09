package ru.tutor.test.api.user;


import io.qameta.allure.*;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import ru.tutor.steps.account.AuthSteps;
import ru.tutor.config.TestConfig;

import static io.qameta.allure.Allure.step;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import ru.tutor.constants.Tags;

@Epic("API Тесты пользователя")
@Feature("Выход из системы")
@DisplayName("Тесты эндпоинта /user/logout")
@Tag(Tags.INTEGRATION)
@Tag(Tags.CONTRACT)
public class UserLogoutTest {

    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();

    private final AuthSteps userSteps = new AuthSteps(BASE_URL);

    private String authToken;

    @BeforeEach
    void setup() {
        authToken = step("Логин", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );
    }

    @Test
    @DisplayName("Успешный выход из системы")
    @Story("Основной сценарий")
    void successfulLogout() {

        Response logoutResponse = step("POST /user/logout", () ->
                userSteps.logout(authToken)
        );

        step("Проверка ответа логаута", () -> {
            logoutResponse.then()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body(equalTo("[]"));
        });
    }

    @Test
    @DisplayName("Попытка выхода без авторизации")
    @Story("Обработка ошибок")
    void logoutWithoutToken() {
        Response response = userSteps.logout("");

        assertTrue(response.getStatusCode() == 401 || response.getStatusCode() == 403,
                "Без токена должен быть 401 или 403. Получено: " + response.getStatusCode());
    }

    @Test
    @DisplayName("Попытка выхода с невалидным токеном")
    void logoutWithInvalidToken() {
        Response response = userSteps.logout("invalid");

        assertEquals(401, response.getStatusCode(), "С невалидным токеном должен быть 401 или 403. Получено: " + response.getStatusCode());
    }

    @Test
    @DisplayName("Выход и повторный логин")
    @Story("Жизненный цикл сессии")
    void logoutAndLoginAgain() {

       userSteps.logout(authToken);

        String secondToken = userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD);

        assertNotEquals(authToken, secondToken, "После повторного логина должен быть новый токен");

    }

    @AfterEach
    void tearDown() {
        step("Завершение теста", () ->
                Allure.addAttachment("Тест завершён", "text/plain",
                        "тест " + this.getClass().getSimpleName() + " выполнен в " +
                                java.time.LocalDateTime.now()
                )
        );
    }
}
