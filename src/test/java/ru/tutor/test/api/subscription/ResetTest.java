package ru.tutor.test.api.subscription;

import io.qameta.allure.*;
import io.qameta.allure.junit5.AllureJunit5;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.tutor.config.TestConfig;
import ru.tutor.steps.ResetSteps;
import ru.tutor.utils.AllureAttachments;

import java.util.Locale;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

@Epic("Восстановить Пароль")
@ExtendWith(AllureJunit5.class)
@Feature("Функция сброса пароля")
@Story("Запрос на сброс пароля и его подтверждение")
@DisplayName("🔐 Тесты сброса пароля")
@Execution(ExecutionMode.CONCURRENT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class ResetTest {
    private final ResetSteps resetSteps = new ResetSteps(TestConfig.getCurrentApiUrl());
    private static final Faker faker = new Faker(Locale.ENGLISH);

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("🚫 Сброс пароля с несуществующим email")
    @Order(2)
    void resetWithUnknownEmail_UserNotFound() {
        Response response = step("Отправить запрос с несуществующим email", () ->
                resetSteps.requestPasswordReset(faker.internet().emailAddress())
        );

        step("Проверить ошибку 'User not found'", () -> {
            response.then()
                    .statusCode(400)
                    .contentType("application/json");

            String message = response.jsonPath().getString("message");
            assertEquals("User not found", message, "Ожидалось сообщение 'User not found'");
            assertNotNull(message, "Ответ должен содержать ключ 'message'");
        });

        step("Проверить отсутствие чувствительных данных", () -> {
            String responseBody = response.getBody().asString().toLowerCase();
            assertFalse(responseBody.contains("12345678"));
            assertFalse(responseBody.contains("secret"));
        });
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("🚫 Сброс пароля с пустым email")
    @Order(3)
    void resetWithEmptyEmail_ValidationError() {
        Response response = step("Отправить запрос с пустым email", () ->
                resetSteps.requestPasswordReset("")
        );

        step("Проверить ошибку валидации", () -> {
            response.then()
                    .statusCode(422)
                    .contentType("application/json");

            // Анализируем реальную структуру ошибки
            String responseBody = response.getBody().asString();
            AllureAttachments.attachText("Тело ответа при пустом email", responseBody);
        });
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("✅ Сброс пароля с валидным email")
    @Order(1)
    void resetWithValidEmail_Success() {
        Response response = step("Отправить запрос сброса пароля для валидного email", () ->
                resetSteps.requestPasswordReset("anika.torphy@yahoo.com")
        );

        step("Проверить успешный ответ", () -> {
            response.then()
                    .statusCode(200)
                    .contentType("application/json");

            // Гибкая проверка тела ответа
            String responseBody = response.getBody().asString();
            if (!responseBody.trim().isEmpty() &&
                    !responseBody.equals("[]") &&
                    response.getContentType().contains("json")) {
                assertFalse(responseBody.contains("\"error\""), "Ответ не должен содержать ключ 'error'");
            }
        });
    }

    @Test
    @DisplayName("🔴 Базовая проверка неуспешного сброса")
    void basicUnsuccessfulResetCheck() {
        Response response = resetSteps.requestPasswordReset("nonexistent@example.com");

            response.then()
                    .statusCode(400)
                    .contentType("application/json");
            String message = response.jsonPath().getString("message");
            assertEquals("User not found", message, "Ожидалось сообщение 'User not found'");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid-email",
            "test@",
            "@domain.com",
            "test@domain",
            "test@domain."
    })
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("📧 Сброс пароля с невалидными email форматами")
    @Order(5)
    void resetWithInvalidEmailFormats_ValidationErrors(String invalidEmail) {
        Response response = step("Отправить запрос с невалидным email: " + invalidEmail, () ->
                resetSteps.requestPasswordReset(invalidEmail)
        );

        step("Проверить ошибку валидации для: " + invalidEmail, () -> {
            response.then()
                    .statusCode(422)
                    .contentType("application/json");
        });
    }

    @Test
    @Severity(SeverityLevel.MINOR)
    @DisplayName("🔠 Сброс пароля с email в верхнем регистре")
    @Order(6)
    void resetWithUppercaseEmail_NormalizationCheck() {
        Response response = step("Отправить запрос с email в верхнем регистре", () ->
                resetSteps.requestPasswordReset("ANIKA.TORPHY@YAHOO.COM")
        );

        step("Проверить обработку регистра", () -> {
            int statusCode = response.getStatusCode();
            assertTrue(statusCode == 200 || statusCode == 400 || statusCode == 404,
                    "Ожидался статус 200, 400 или 404, но получен: " + statusCode);

            AllureAttachments.attachText("Статус ответа для email в верхнем регистре",
                    String.valueOf(statusCode));
        });
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("🔣 Сброс пароля с email содержащим спецсимволы")
    @Order(7)
    void resetWithEmailWithSpecialChars_BehaviorCheck() {
        Response response = step("Отправить запрос с email содержащим спецсимволы", () ->
                resetSteps.requestPasswordReset("test+tag@domain.com")
        );

        step("Проверить обработку спецсимволов", () -> {
            int statusCode = response.getStatusCode();
            assertTrue(statusCode == 200 || statusCode == 400 || statusCode == 422,
                    "Ожидался статус 200, 400 или 422, но получен: " + statusCode);

            // Логируем результат для анализа
            AllureAttachments.attachText("Обработка спецсимволов",
                    "Email: test+tag@domain.com\nStatus: " + statusCode);
        });
    }

    @Test
    @Severity(SeverityLevel.MINOR)
    @DisplayName("🔄 Повторный запрос сброса пароля")
    @Order(8)
    void resetPasswordTwice_RateLimitCheck() {
        String email = "anika.torphy@yahoo.com";

        Response firstResponse = step("Первый запрос сброса пароля", () ->
                resetSteps.requestPasswordReset(email)
        );

        Response secondResponse = step("Второй запрос сброса пароля", () ->
                resetSteps.requestPasswordReset(email)
        );

        step("Проверить обработку повторного запроса", () -> {
            int statusCode = secondResponse.getStatusCode();
            assertTrue(statusCode == 200 || statusCode == 400 || statusCode == 429,
                    "Ожидался статус 200, 400 или 429, но получен: " + statusCode);
 
            if (statusCode == 429) {
                String message = secondResponse.jsonPath().getString("message");
                assertEquals("Too many requests", message, "Ожидалось сообщение 'Too many requests'");
            }
        });
    }

    @Nested
    @DisplayName("🔍 Детальные проверки различных email форматов")
    class EmailFormatValidation {

        @Test
        @DisplayName("📝 Проверка различных спецсимволов в email")
        void variousSpecialCharactersInEmail() {
            String[] testEmails = {
                    "test+tag@domain.com",     // плюс в локальной части
                    "test.tag@domain.com",     // точка в локальной части
                    "test-tag@domain.com",     // дефис в локальной части
                    "test_tag@domain.com",     // подчеркивание в локальной части
                    "test@sub.domain.com"      // точка в доменной части
            };

            for (String email : testEmails) {
                Response response = step("Тестирование email: " + email, () ->
                        resetSteps.requestPasswordReset(email)
                );

                step("Проверить ответ для: " + email, () -> {
                    int statusCode = response.getStatusCode();
                    assertTrue(statusCode == 200 || statusCode == 400 || statusCode == 422,
                            "Для email " + email + " ожидался статус 200, 400 или 422, но получен: " + statusCode);

                    // Логируем результат
                    AllureAttachments.attachText("Результат для " + email,
                            "Status: " + statusCode);
                });
            }
        }
    }
}
