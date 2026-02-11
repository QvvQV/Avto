package ru.tutor.test.api.user;

import io.qameta.allure.*;
import io.qameta.allure.junit5.AllureJunit5;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tutor.steps.UserSteps;
import ru.tutor.config.TestConfig;


import ru.tutor.steps.AuthSteps;

import static io.qameta.allure.Allure.step;
import static org.hamcrest.Matchers.*;


@ExtendWith(AllureJunit5.class)
@Epic("API Тесты пользователя")
@Feature("Аутентификация и профиль")
@DisplayName("Тесты: Логин → Получение профиля → Проверка данных")
@Story("Полный сценарий аутентификации")
public class UserLoginProfileTest {

    private ru.tutor.steps.UserSteps userSteps;
    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();
    private final AuthSteps authSteps = new AuthSteps(TestConfig.getApiBaseUrl());

    @BeforeEach
    void setUp() {
        userSteps = new UserSteps(TestConfig.getApiBaseUrl());
    }

    @Test
    @DisplayName("Полный сценарий: Логин → Профиль → Проверка флагов")
    void fullLoginProfileScenario() {
        // 1. Логин и получение токена
        String token = authSteps.loginAndGetToken(
                TEST_EMAIL,
                TEST_PASSWORD
        );

        // 2. Получение профиля
        Response profileResponse = userSteps.getProfile(token);

        // 3. Проверки
        step("Проверка кода ответа и email", () -> {
            profileResponse.then()
                    .statusCode(200)
                    .body("email", equalTo(TEST_EMAIL));
        });

        step("Проверка name = dfs", () -> {
            profileResponse.then()
                    .body("name", equalTo("dfs"));
        });

        step("Проверка флага isTariffLifetime = true", () -> {
            profileResponse.then()
                    .body("isTariffLifetime", equalTo(true));
        });

        step("Проверка флага isYtSubscribed = false", () -> {
            profileResponse.then()
                    .body("isYtSubscribed", equalTo(false));
        });

        step("Проверка флага isVkSubscribed = false", () ->{
            profileResponse.then()
                    .body("isVkSubscribed", equalTo(false));
        });
        step("Проверка флага isTgSubscribed = false", () -> {
            profileResponse.then()
                    .body("isTgSubscribed", equalTo(false));
        });
    }
}