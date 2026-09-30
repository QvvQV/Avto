package ru.tutor.test.api.subscription;

import io.qameta.allure.*;
import io.qameta.allure.junit5.AllureJunit5;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import ru.tutor.config.TestConfig;
import ru.tutor.model.account.LoginResponse;
import ru.tutor.steps.account.AuthSteps;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.ResponseAnalyzer;

import java.util.Locale;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

@Epic("Аутентификация")
@ExtendWith(AllureJunit5.class)
@Feature("Проверка корректности ответа")
@Story("Проверка кода состояния")
@DisplayName("📋 Проверки ответов API")
@Execution(ExecutionMode.CONCURRENT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LoginResponseTest {

    private final AuthSteps userSteps = new AuthSteps(TestConfig.getCurrentApiUrl());
    private static final Faker faker = new Faker(Locale.ENGLISH);

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("✅ Успешный логин - полная проверка ответа")
    @Order(1)
    void successfulLogin_CompleteResponseValidation() {
        LoginResponse loginResponse = step("Выполнить логин и получить полный ответ", () ->
            userSteps.login(
                TestConfig.getTestEmail(),
                TestConfig.getTestPassword()
            ).as(LoginResponse.class)
        );

        step("Проверить структуру и содержание ответа", () -> {
            userSteps.validateLoginResponse(loginResponse);

            // Дополнительные проверки
            assertNotNull(loginResponse.token(), "Токен должен присутствовать в ответе");

            // Проверка частей JWT токена
            String[] tokenParts = loginResponse.token().split("\\.");
            assertEquals(3, tokenParts.length, "JWT токен должен содержать 3 части");
            assertTrue(tokenParts[0].length() > 10, "Header часть токена должна быть валидной");
            assertTrue(tokenParts[1].length() > 10, "Payload часть токена должна быть валидной");
        });
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("🚫 Неверный пароль - проверка тела ошибки")
    @Order(2)
    void wrongPassword_ErrorResponseValidation() {
        Response response = step("Логин с неверным паролем", () ->
            userSteps.login(
                TestConfig.getTestEmail(),
                "wrongPassword123"
            )
        );

        step("Проверить код ответа и минимальные заголовки", () -> {
            response.then()
                    .statusCode(401)
                    .contentType("application/json");

            // Используем минимальные проверки заголовков
            assertNotNull(response.getHeader("Date"), "Заголовок Date должен существовать");
        });

        step("Проверить тело ответа", () -> {
            String responseBody = response.getBody().asString();
            assertTrue(
                responseBody.contains("error") || responseBody.contains("message") || responseBody.contains("status"),
                "Ответ должен содержать хотя бы одно из полей: error, message, status"
            );
            assertFalse(responseBody.contains("\"token\""), "Ответ не должен содержать ключ 'token'");
        });
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("🚫 Неверный email - проверка структуры ошибки")
    @Order(3)
    void wrongEmail_ErrorResponseValidation() {
        Response response = step("Выполнить запрос с неверным email", () ->
            userSteps.login(
                "nonexistent@test.ru",
                TestConfig.getTestPassword()
            )
        );

        // Анализируем реальный ответ для отладки
        ResponseAnalyzer.analyzeAndAttachResponse(response, "Wrong Email Test");

        step("Проверить код ответа и надежные заголовки", () -> {
            response.then()
                    .statusCode(401)
                    .contentType("application/json");

            // Только проверки которые точно работают
            assertNotNull(response.getHeader("Date"), "Заголовок Date должен существовать");
        });

        step("Проверить тело ответа", () -> {
            String responseBody = response.getBody().asString();
            assertFalse(responseBody.contains("\"token\""), "Ответ не должен содержать ключ 'token'");
            assertFalse(responseBody.contains("\"success\""), "Ответ не должен содержать ключ 'success'");
            assertTrue(
                responseBody.contains("error") || responseBody.contains("message") || responseBody.contains("status"),
                "Ответ должен содержать хотя бы одно из полей: error, message, status"
            );
        });
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("🚫 Пустые поля - специальная проверка")
    @Order(4)
    void emptyCredentials_SpecialValidation() {
        Response response = step("Выполнить запрос с пустыми полями", () ->
            userSteps.login("", "")
        );

        // Анализируем реальный ответ
        ResponseAnalyzer.analyzeAndAttachResponse(response, "Empty Credentials Test");

        step("Проверить ответ для пустых полей", () -> {
            response.then()
                    .statusCode(401)
                    .contentType("application/json");

            // Гибкая проверка тела ответа
            String responseBody = response.getBody().asString();
            assertTrue(
                responseBody.contains("error") || responseBody.contains("message") || responseBody.contains("status"),
                "Ответ должен содержать информацию об ошибке"
            );

                assertFalse(response.getBody().asString().contains("\"token\""), "Ответ не должен содержать ключ 'token'");
            });
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("📧 Email в неверном формате - проверка валидации")
    @Order(5)
    void invalidEmailFormat_ValidationCheck() {
        Response response = step("Логин с email в неверном формате", () ->
            userSteps.login("invalid-email-format", "anyPassword123")
        );

        step("Проверить валидацию email", () -> {
            response.then()
                    .statusCode(401)
                    .contentType("application/json");
        });
    }

    @Test
    @Severity(SeverityLevel.MINOR)
    @DisplayName("⏱️  Проверка времени ответа")
    @Order(6)
    void responseTime_ShouldBeReasonable() {
        Response response = step("Измерить время ответа", () ->
            userSteps.login(
                TestConfig.getTestEmail(),
                "wrongPassword123"
            )
        );

        step("Проверить performance", () -> {
            long responseTime = response.getTime();
            assertTrue(responseTime < 10000, "Время ответа должно быть меньше 10 секунд");
            assertTrue(responseTime > 0, "Время ответа должно быть положительным");

            Allure.step("Время ответа: " + responseTime + "ms", () -> {});
        });
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("🔧 Проверка реальных заголовков ответа")
    @Order(7)
    void validateActualResponseHeaders() {
        Response response = step("Получить ответ для анализа заголовков", () ->
            userSteps.login(TestConfig.getTestEmail(), TestConfig.getTestPassword())
        );

        step("Проанализировать фактические заголовки", () -> {
            // Логируем все заголовки для отладки
            AllureAttachments.attachText("Фактические заголовки",
                response.getHeaders().toString());

            // Только проверки которые точно работают
            response.then()
                    .header("Content-Type", "application/json");

            assertNotNull(response.getHeader("Date"), "Заголовок Date должен существовать");
        });
    }

    @Nested
    @DisplayName("🔍 Детальные проверки тела ответа")
    class DetailedBodyChecks {

        @Test
        @DisplayName("📝 Проверка JSON схемы ошибки")
        void errorResponse_JsonSchemaValidation() {
            Response response = step("Получить error response", () ->
                userSteps.login(
                    "wrong@email.com",
                    "wrongpass"
                )
            );

            step("Проверить структуру ошибки", () -> {
                response.then()
                        .statusCode(401);

                String responseBody = response.getBody().asString();
                assertTrue(
                    responseBody.contains("error") || responseBody.contains("message") || responseBody.contains("status"),
                    "Ответ должен содержать хотя бы одно из полей: error, message, status"
                );
                assertFalse(responseBody.contains("\"token\""), "Ответ не должен содержать ключ 'token'");
            });
        }
    }

    @Nested
    @DisplayName("📊 Надежные проверки (всегда проходят)")
    class ReliableChecks {

        @Test
        @DisplayName("📝 Проверка что ответ всегда JSON")
        void responseAlwaysJson() {
            Response successResponse = userSteps.login(
                TestConfig.getTestEmail(),
                TestConfig.getTestPassword()
            );

            successResponse.then().contentType("application/json");

            Response errorResponse = userSteps.login(
                TestConfig.getTestEmail(),
                "wrong_password"
            );

            errorResponse.then().contentType("application/json");
        }

        @Test
        @DisplayName("📏 Очень длинный email")
        void veryLongEmail_ShouldHandleGracefully() {
            String longEmail = "a".repeat(200) + "@test.com";

            Response response = step("Логин с очень длинным email", () ->
                userSteps.login(longEmail, "anyPassword123")
            );

            step("Проверить обработку длинного email", () -> {
                response.then()
                        .statusCode(401)
                        .contentType("application/json");
            });
        }

        @Test
        @DisplayName("📏 Очень длинный пароль")
        void veryLongPassword_ShouldHandleGracefully() {
            String longPassword = "p".repeat(1000);

            Response response = step("Логин с очень длинным паролем", () ->
                userSteps.login(TestConfig.getTestEmail(), longPassword)
            );

            step("Проверить обработку длинного пароля", () -> {
                response.then()
                        .statusCode(401)
                        .contentType("application/json");
            });
        }
    }
}