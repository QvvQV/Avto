package ru.tutor.test.api;

import io.qameta.allure.*;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.tutor.steps.RegisterSteps;
import ru.tutor.base.BaseApiTest;
import ru.tutor.utils.AllureAttachments;

import java.util.Locale;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@Epic("User Registration")
@Feature("Registration Functionality")
@Story("User Registration and Validation")
@DisplayName("👤 Тесты регистрации пользователей")
@Execution(ExecutionMode.CONCURRENT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RegisterTest extends BaseApiTest {
    private final RegisterSteps registerSteps = new RegisterSteps(API_BASE_URL);
    private static final Faker faker = new Faker(new Locale("en"));

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("✅ Успешная регистрация нового пользователя")
    @Order(1)
    void successfulRegistration_CompleteValidation() {
        String email = faker.internet().emailAddress();
        String name = faker.name().fullName();
        String phone = registerSteps.generateUniquePhone();

        step("Подготовить данные регистрации", () -> {
            registerSteps.attachRegistrationData(email, name, phone, true);
        });

        Response response = step("Выполнить регистрацию пользователя", () ->
                registerSteps.registerUser(email, name, "12345678", phone, "@telegram", true));

        step("Проверить успешную регистрацию", () -> {
            registerSteps.validateSuccessfulRegistration(response);
            registerSteps.validateRequireHeaders(response);
            registerSteps.validateNoSensitiveDate(response);
        });
        step("Проверить performance", () -> {
            registerSteps.validateResponseTime(response, 5000);
        });
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("✅ Успешная регистрация с минимальными данными")
    @Order(2)
    void successfulRegistration_MinimalData() {
        String email = faker.internet().emailAddress();
        String name = faker.name().firstName();

        Response response = step("Регистрация с минимальными данными", () ->
                registerSteps.registerUser(email, name, "12345678", "", "", true));

        step("Проверить успешную регистрацию", () -> {
            registerSteps.validateSuccessfulRegistration(response);
        });
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("🚫 Регистрация с существующим email")
    @Order(3)
    void registrationWithExistingEmail_ConflictCheck() {
        Response response = step("регистрация с существующим email", () ->
                registerSteps.registerUser("tp5052_2@test.ru", "Test User", "12345678", "", "", true));
        step("Проверить конфликт email", () -> {
            registerSteps.validateEmailConflict(response);
            registerSteps.validateNoSensitiveDate(response);
        });
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("🚫 Регистрация без согласия с политикой")
    @Order(4)
    void registrationWithoutPolicyApproval_ValidationError() {
        String email = faker.internet().emailAddress();
        String name = faker.name().firstName();

        Response response = step("Регистрация без согласия с политикой", () ->
                registerSteps.registerUser(email, name, "12345678", "", "", false));

        step("Проверить ошибку валидации", () -> {
            registerSteps.validateValidationError(response, 422);
        });
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("🚫 Регистрация с пустым email")
    @Order(5)
    void registrationWithEmptyEmail_ValidationError() {
        String name = faker.name().firstName();
        Response response = step("Регистрация с пустым email", () ->
                registerSteps.registerUser("", name, "12345678", "", "", true));
        step("Проверить валидацию email", () -> {
            registerSteps.validateValidationError(response, 422);
        });
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("🚫 Регистрация с пустым именем")
    @Order(6)
    void registrationWithEmptyName_ValidationError() {
        String email = faker.internet().emailAddress();

        Response response = step("Регистрация с пустым именем", () ->
                registerSteps.registerUser(email, "", "12345678", "", "", true));
        step("Проверить валидацию имен", () -> {
            registerSteps.validateValidationError(response, 422);
        });
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("🚫 Регистрация с коротким паролем")
    @Order(7)
    void registrationWithShortPassword_ValidationError() {
        String email = faker.internet().emailAddress();
        String name = faker.name().fullName();

        Response response = step("регистрация с коротким паролем", () ->
                registerSteps.registerUser(email, name, "12345", "", "", true));

        step("Проверить валидацию пароля", () -> {
            registerSteps.validateValidationError(response, 422);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid-email",
            "test@",
            "@domain.com",
            "test@domain",
            "test@domain.",
            "test @domain.com",
            "test@ domain.com"
    })
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("📧 Регистрация с невалидными email форматами")
    @Order(8)
    void registrationWithInvalidEmailFormats_ValidationErrors(String invalidEmail) {
        Response response = step("регистрация с невалидным email: " + invalidEmail, () ->
                registerSteps.registerUser(invalidEmail, "test", "12345678", "", "", true));
        step("Проверить валидацию email формата", () -> {
            registerSteps.validateValidationError(response, 422);
        });
    }

    @Nested
    @DisplayName("🔍 Детальные проверки валидации")
    class DetailValidationCheks {

        @Test
        @DisplayName("📏 Очень длинное имя пользователя")
        void veryLongUserName_ShouldHandleGracefully() {
            String email = faker.internet().emailAddress();
            String longName = faker.name().firstName().repeat(10);

            Response response = step("Регистарция с очень длинным именем", () ->
                    registerSteps.registerUser(email, longName, "12345678", "", "", true));
            step("Проверить обработку длинного имени", () -> {
                int statusCode = response.getStatusCode();
                assertTrue(statusCode == 200 || statusCode == 422,
                        "Ожидался статус 200 или 422, но получили: " + statusCode);
            });
        }

        @ParameterizedTest
        @ValueSource(ints = {50, 75, 150}) //разная длина
        @DisplayName("📏 Очень длинное email")
        void veryLongEmail_ShouldHandleGracefully(int emailLength) {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String longEmail = "a".repeat(emailLength - timestamp.length() - 15) + "." + timestamp + "@test.com";
            String name = faker.name().firstName();

            step("Подготовить данные с длинным email (" + emailLength + " символов", () ->{
                registerSteps.attachRegistrationData(longEmail, name, "",true);
                AllureAttachments.attachText("Детали длинного email",
                        "Запрошенная длина: " + emailLength + " символов\n" +
                        "Фактическая длина: " + longEmail.length() + " символов\n" +
                        "Email: " + longEmail);
            });

            Response response = step("Регистрация с очень длинным email (" + emailLength + " символов)", () ->
                    registerSteps.registerUser(longEmail, name, "12345678", "", "", true));
            step("Проверить обработку длинного email (" + emailLength + " символов)", () -> {
                int statusCode = response.getStatusCode();
                assertTrue(statusCode >= 200 && statusCode < 500,
                        "Api должно обработать запрос с email длиной " + emailLength +
                                " символов, но получили:" + statusCode);

                if (statusCode == 400){
                    String message = response.jsonPath().getString("message");
                    AllureAttachments.attachText("Причина ошибка 400", message);
                }
            });
        }

        @Test
        @DisplayName("🔣 Спецсимволы в имени")
        void specialCharactersInName_BehaviorCheck() {
            String email = faker.internet().emailAddress();
            String nameWithSpecialChars = "Test-User_Name123";

            Response response = step("Регистрация с спецсимволами в имени", () ->
                    registerSteps.registerUser(email, nameWithSpecialChars, "12345678", "", "", true));
            step("Проверить обработку спецсмволов", () -> {
                int statusCode = response.getStatusCode();
                assertTrue(statusCode == 200 || statusCode == 422,
                        "Ожидался статус 200 или 422, но получили: " + statusCode);
            });
        }

        @Test
        @DisplayName("📝 Проверка что ответ всегда JSON")
        void responseAlwaysJson() {
            String email = faker.internet().emailAddress();
            String name = faker.name().firstName();

            Response successResponse = registerSteps.registerUser(email, name, "12345678", "", "", true);
            Response errorResponse = registerSteps.registerUser("tp5052_2@test.ru", "Test User", "12345678", "", "", true);

            successResponse.then().contentType("application/json");
            errorResponse.then().contentType("application/json");
        }

        @Nested
        @DisplayName("⚡ Performance и Security проверки")
        class PerformanceAndSecurityChecks {

            @Test
            @DisplayName("⏱️ Проверка времени ответа для разных сценариев")
            void responseTimeForDifferentScenarios() {
                step("Проверить время ответа для успешной регистрации", () -> {
                    String email = faker.internet().emailAddress();
                    String name = faker.name().firstName();
                    Response response = registerSteps.registerUser(email, name, "12345678", "", "", true);
                    registerSteps.validateResponseTime(response, 3000);
                });

                step("Проверить время ответа для ошибки валидации", () -> {
                    Response response = registerSteps.registerUser("invalid-email", "test", "12345678", "", "", true);
                    registerSteps.validateResponseTime(response, 3000);
                });
            }

            @Test
            @DisplayName("🔒 Проверка отсутствия чувствительных данных")
            void noSensitivedataInregistrationResponse() {
                String[] testEmails = {
                        faker.internet().emailAddress(),
                        "tp5052_2@test.ru",
                        "invalid-email"
                };

                for (String email : testEmails) {
                    Response response = registerSteps.registerUser(email, "test", "12345678", "", "", true);
                    registerSteps.validateNoSensitiveDate(response);
                }
            }

            @Test
            @DisplayName("🔐 Проверка безопасности паролей")
            void passwordSecurityValidation() {
                String email = faker.internet().emailAddress();
                String name = faker.name().firstName();

                Response response = registerSteps.registerUser(email, name, "12345678", "", "", true);
                String responseBody = response.getBody().asString().toLowerCase();

                // Пароль не должен возвращаться в ответе
                assertFalse(responseBody.contains("12345678"), "Пароль не должен быть в ответе");
            }
        }
    }
}