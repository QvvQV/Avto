package ru.tutor.test.api.user;


import io.qameta.allure.*;
import io.qameta.allure.junit5.AllureJunit5;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tutor.steps.AuthSteps;
import ru.tutor.steps.UserSteps;
import ru.tutor.config.TestConfig;

import static io.qameta.allure.Allure.step;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AllureJunit5.class)
@Epic("API Тесты пользователя")
@Feature("Выход из системы")
@DisplayName("Тесты эндпоинта /user/logout")
public class UserLogoutTest {

    private final AuthSteps authSteps = AuthSteps.withBaseUrl(TestConfig.getCurrentApiUrl());
    private ru.tutor.steps.UserSteps userSteps;
    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();

    @BeforeEach
    void setup() {
        userSteps = new UserSteps(TestConfig.getCurrentApiUrl());
    }

    @Test
    @DisplayName("Успешный выход из системы")
    @Story("Основной сценарий")
    void successfulLogout() {
        String token = step("Логин и получение токена", () ->
                authSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );
        Response logoutResponse = step("POST /user/logout", () ->
                userSteps.logout(token)
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
        String firstToken = authSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD);

        Response response = userSteps.logout(firstToken);
        response.then()
                .statusCode(200);

        String secondToken = authSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD);

        assertNotEquals(firstToken, secondToken, "После повторного логина должен быть новый токен");

        Response response2 = userSteps.getProfile(secondToken);
        response2.then()
                .statusCode(200)
                .body("email", equalTo(TEST_EMAIL));
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
