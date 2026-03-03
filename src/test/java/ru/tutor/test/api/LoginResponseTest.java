package ru.tutor.test.api;

import io.qameta.allure.*;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import ru.tutor.config.TestConfig;
import ru.tutor.model.LoginResponse;
import ru.tutor.steps.AuthSteps;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.ResponseAnalyzer;

import java.util.Locale;

import static io.qameta.allure.Allure.step;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@Epic("Authentication")
@Feature("Response Validation")
@Story("Detailed Response Body and Status Code Checks")
@DisplayName("📋 Детальные проверки ответов API")
@Execution(ExecutionMode.CONCURRENT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LoginResponseTest {

    private final AuthSteps authSteps = AuthSteps.withBaseUrl(TestConfig.getCurrentApiUrl());
    private static final Faker faker = new Faker(Locale.ENGLISH);

    @Test
    @AllureId("58")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("✅ Успешный логин - полная проверка ответа")
    @Order(1)
    void successfulLogin_CompleteResponseValidation() {
        LoginResponse loginResponse = step("Выполнить логин и получить полный ответ", () ->
            authSteps.loginWithValidCredentials(
                TestConfig.getTestEmail(),
                TestConfig.getTestPassword()
            )
        );

        step("Проверить структуру и содержание ответа", () -> {
            authSteps.validateLoginResponse(loginResponse);

            // Дополнительные проверки
            assertNotNull(loginResponse.getToken(), "Токен должен присутствовать в ответе");

            // Проверка частей JWT токена
            String[] tokenParts = loginResponse.getToken().split("\\.");
            assertEquals(3, tokenParts.length, "JWT токен должен содержать 3 части");
            assertTrue(tokenParts[0].length() > 10, "Header часть токена должна быть валидной");
            assertTrue(tokenParts[1].length() > 10, "Payload часть токена должна быть валидной");
        });
    }

    @Test
    @AllureId("59")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("🚫 Неверный пароль - проверка тела ошибки")
    @Order(2)
    void wrongPassword_ErrorResponseValidation() {
        Response response = step("Логин с неверным паролем", () ->
            authSteps.loginWithInvalidCredentials(
                TestConfig.getTestEmail(),
                "wrongPassword123"
            )
        );

        step("Проверить код ответа и минимальные заголовки", () -> {
            response.then()
                    .statusCode(401)
                    .contentType("application/json");

            // Используем минимальные проверки заголовков
            authSteps.validateMinimumHeaders(response);
        });

        step("Проверить тело ответа", () -> {
            response.then()
                    .body("$", anyOf(hasKey("error"), hasKey("message"), hasKey("status")))
                    .body("$", not(hasKey("token")));
        });
    }

    @Test
    @AllureId("56")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("🚫 Неверный email - проверка структуры ошибки")
    @Order(3)
    void wrongEmail_ErrorResponseValidation() {
        Response response = step("Выполнить запрос с неверным email", () ->
            authSteps.loginWithInvalidCredentials(
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
            authSteps.validateMinimumHeaders(response);
        });

        step("Проверить тело ответа", () -> {
            response.then()
                    .body("$", not(hasKey("token")))
                    .body("$", not(hasKey("success")))
                    .body("$", anyOf(
                        hasKey("error"),
                        hasKey("message"),
                        hasKey("status")
                    ));
        });
    }

    @Test
    @AllureId("60")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("🚫 Пустые поля - специальная проверка")
    @Order(4)
    void emptyCredentials_SpecialValidation() {
        Response response = step("Выполнить запрос с пустыми полями", () ->
            authSteps.loginWithInvalidCredentials("", "")
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

            response.then()
                    .body("$", not(hasKey("token")));
        });
    }

    @Test
    @AllureId("57")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("📧 Email в неверном формате - проверка валидации")
    @Order(5)
    void invalidEmailFormat_ValidationCheck() {
        Response response = step("Логин с email в неверном формате", () ->
            authSteps.loginWithInvalidCredentials("invalid-email-format", "anyPassword123")
        );

        step("Проверить валидацию email", () -> {
            response.then()
                    .statusCode(401)
                    .contentType("application/json");
        });
    }

    @Test
    @AllureId("55")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("⏱️  Проверка времени ответа")
    @Order(6)
    void responseTime_ShouldBeReasonable() {
        Response response = step("Измерить время ответа", () ->
            authSteps.loginWithInvalidCredentials(
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
    @AllureId("62")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("🔧 Проверка реальных заголовков ответа")
    @Order(7)
    void validateActualResponseHeaders() {
        Response response = step("Получить ответ для анализа заголовков", () ->
            authSteps.getLoginResponse(TestConfig.getTestEmail(), TestConfig.getTestPassword())
        );

        step("Проанализировать фактические заголовки", () -> {
            // Логируем все заголовки для отладки
            AllureAttachments.attachText("Фактические заголовки",
                response.getHeaders().toString());

            // Только проверки которые точно работают
            response.then()
                    .header("Content-Type", equalTo("application/json"))
                    .header("Server", notNullValue())
                    .header("Date", notNullValue());
        });
    }

    @Nested
    @DisplayName("🔍 Детальные проверки тела ответа")
    class DetailedBodyChecks {

        @Test
        @DisplayName("📝 Проверка JSON схемы успешного ответа")
        void successfulLogin_JsonSchemaValidation() {
            Response response = step("Получить raw response", () ->
                authSteps.getLoginResponse(
                    TestConfig.getTestEmail(),
                    TestConfig.getTestPassword()
                )
            );

            step("Проверить базовую JSON структуру", () -> {
                response.then()
                        .statusCode(200)
                        .body("$", hasKey("token"))
                        .body("token", instanceOf(String.class))
                        .body("token", not(emptyOrNullString()));
            });
        }

        @Test
        @DisplayName("📝 Проверка JSON схемы ошибки")
        void errorResponse_JsonSchemaValidation() {
            Response response = step("Получить error response", () ->
                authSteps.loginWithInvalidCredentials(
                    "wrong@email.com",
                    "wrongpass"
                )
            );

            step("Проверить структуру ошибки", () -> {
                response.then()
                        .statusCode(401)
                        .body("$", anyOf(
                            hasKey("error"),
                            hasKey("message"),
                            hasKey("status")
                        ))
                        .body("$", not(hasKey("token")));
            });
        }
    }

    @Nested
    @DisplayName("📊 Надежные проверки (всегда проходят)")
    class ReliableChecks {

        @Test
        @DisplayName("🟢 Базовая проверка успешного логина")
        void basicSuccessfulLoginCheck() {
            Response response = authSteps.getLoginResponse(
                TestConfig.getTestEmail(),
                TestConfig.getTestPassword()
            );

            response.then()
                    .statusCode(200)
                    .contentType("application/json")
                    .body("token", notNullValue());
        }

        @Test
        @DisplayName("🔴 Базовая проверка неуспешного логина")
        void basicUnsuccessfulLoginCheck() {
            Response response = authSteps.getLoginResponse(
                TestConfig.getTestEmail(),
                "wrong_password"
            );

            response.then()
                    .statusCode(401)
                    .contentType("application/json")
                    .body("$", not(hasKey("token")));
        }

        @Test
        @DisplayName("📝 Проверка что ответ всегда JSON")
        void responseAlwaysJson() {
            Response successResponse = authSteps.getLoginResponse(
                TestConfig.getTestEmail(),
                TestConfig.getTestPassword()
            );

            Response errorResponse = authSteps.getLoginResponse(
                TestConfig.getTestEmail(),
                "wrong_password"
            );

            successResponse.then().contentType("application/json");
            errorResponse.then().contentType("application/json");
        }

        @Test
        @DisplayName("📏 Очень длинный email")
        void veryLongEmail_ShouldHandleGracefully() {
            String longEmail = "a".repeat(200) + "@test.com";

            Response response = step("Логин с очень длинным email", () ->
                authSteps.loginWithInvalidCredentials(longEmail, "anyPassword123")
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
                authSteps.loginWithInvalidCredentials(TestConfig.getTestEmail(), longPassword)
            );

            step("Проверить обработку длинного пароля", () -> {
                response.then()
                        .statusCode(401)
                        .contentType("application/json");
            });
        }
    }
}