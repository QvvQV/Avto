package ru.tutor.test.api;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.tutor.config.TestConfig;
import ru.tutor.steps.AuthSteps;
import ru.tutor.utils.ResponseAnalyzer;

import static io.qameta.allure.Allure.step;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@Epic("Authentication")
@Feature("Reliable Tests")
@DisplayName("🟢 Надежные тесты логина")
class ReliableLoginTest {

    private final AuthSteps authSteps = AuthSteps.withBaseUrl(TestConfig.getApiBaseUrl());

    @Test
    @AllureId("86")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("✅ Успешный логин - минимальные проверки")
    void successfulLogin_MinimalChecks() {
        Response response = authSteps.getLoginResponse(
                TestConfig.getTestEmail(),
                TestConfig.getTestPassword()
        );

        step("Проверить статус код", () -> {
            response.then().statusCode(200);
        });

        step("Проверить Content-Type", () -> {
            response.then().contentType("application/json");
        });

        step("Проверить наличие токена", () -> {
            String token = response.jsonPath().getString("token");
            assertNotNull(token, "Токен не должен быть null");
            assertFalse(token.isEmpty(), "Токен не должен быть пустым");
        });
    }

    @Test
    @AllureId("88")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("🚫 Неуспешный логин - минимальные проверки")
    void unsuccessfulLogin_MinimalChecks() {
        Response response = authSteps.getLoginResponse(
                TestConfig.getTestEmail(),
                "wrong_password"
        );

        ResponseAnalyzer.analyzeAndAttachResponse(response, "Unsuccessful Login");

        step("Проверить статус код", () -> {
            response.then().statusCode(401);
        });

        step("Проверить Content-Type", () -> {
            response.then().contentType("application/json");
        });

        step("Проверить отсутствие токена", () -> {
            response.then().body("$", not(hasKey("token")));
        });
    }

    @Test
    @AllureId("84")
    @DisplayName("🔍 Анализ реальной структуры ответов")
    void analyzeActualResponseStructure() {
        step("Анализ успешного ответа", () -> {
            Response successResponse = authSteps.getLoginResponse(
                    TestConfig.getTestEmail(),
                    TestConfig.getTestPassword()
            );
            ResponseAnalyzer.analyzeAndAttachResponse(successResponse, "Successful Login Analysis");
        });

        step("Анализ ошибочного ответа", () -> {
            Response errorResponse = authSteps.getLoginResponse(
                    TestConfig.getTestEmail(),
                    "wrong_password"
            );
            ResponseAnalyzer.analyzeAndAttachResponse(errorResponse, "Error Login Analysis");
        });
    }
}
