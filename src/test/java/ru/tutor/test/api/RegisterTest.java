package ru.tutor.test.api;

import io.qameta.allure.*;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.tutor.config.TestConfig;
import ru.tutor.steps.RegisterSteps;
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
class RegisterTest {
    private final RegisterSteps registerSteps = new RegisterSteps(TestConfig.getCurrentApiUrl());
    private final Faker faker = new Faker(Locale.ENGLISH);

    private static final String TEST_PASSWORD = TestConfig.getTestPassword();
    private final String TEST_EMAIL = faker.internet().emailAddress();
    private final String TEST_FIRST_NAME = faker.name().fullName();


    @Test
//    @RepeatedTest(3)
    @AllureId("73")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("✅ Успешная регистрация нового пользователя")
    @Order(1)
    void successfulRegistration_CompleteValidation() {
        String phone = registerSteps.generateUniquePhone();

        step("Подготовить данные регистрации", () ->
            registerSteps.attachRegistrationData(TEST_EMAIL, TEST_FIRST_NAME, phone, true)
        );

        Response response = step("Выполнить регистрацию пользователя", () ->
                registerSteps.registerUser(TEST_EMAIL, TEST_FIRST_NAME, TEST_PASSWORD, phone, "@telegram", true));

        step("Проверить успешную регистрацию", () -> {
            registerSteps.validateSuccessfulRegistration(response);
            registerSteps.validateRequireHeaders(response);
            registerSteps.validateNoSensitiveDate(response);
        });
    }

    @Test
    @AllureId("74")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("✅ Успешная регистрация с минимальными данными")
    @Order(2)
    void successfulRegistration_MinimalData() {

        Response response = step("Регистрация с минимальными данными", () ->
                registerSteps.registerUser(TEST_EMAIL, TEST_FIRST_NAME, TEST_PASSWORD, "", "", true));

        step("Проверить успешную регистрацию", () ->
            registerSteps.validateSuccessfulRegistration(response)
        );
    }

    @Test
    @AllureId("72")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("🚫 Регистрация с существующим email")
    @Order(3)
    void registrationWithExistingEmail_ConflictCheck() {
        Response response = step("регистрация с существующим email", () ->
                registerSteps.registerUser("tp5052_2@test.ru", "Test User", TEST_PASSWORD, "", "", true));
        step("Проверить конфликт email", () -> {
            registerSteps.validateEmailConflict(response);
            registerSteps.validateNoSensitiveDate(response);
        });
    }

    @Test
    @AllureId("69")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("🚫 Регистрация без согласия с политикой")
    @Order(4)
    void registrationWithoutPolicyApproval_ValidationError() {

        Response response = step("Регистрация без согласия с политикой", () ->
                registerSteps.registerUser(TEST_EMAIL, TEST_FIRST_NAME, TEST_PASSWORD, "", "", false));

        step("Проверить ошибку валидации", () ->
            registerSteps.validateValidationError(response, 422)
        );
    }

    @Test
    @AllureId("75")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("🚫 Регистрация с пустым email")
    @Order(5)
    void registrationWithEmptyEmail_ValidationError() {
        Response response = step("Регистрация с пустым email", () ->
                registerSteps.registerUser("", TEST_FIRST_NAME, TEST_PASSWORD, "", "", true));
        step("Проверить валидацию email", () ->
            registerSteps.validateValidationError(response, 422)
        );
    }

    @Test
    @AllureId("71")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("🚫 Регистрация с пустым именем")
    @Order(6)
    void registrationWithEmptyName_ValidationError() {

        Response response = step("Регистрация с пустым именем", () ->
                registerSteps.registerUser(TEST_EMAIL, "", TEST_PASSWORD, "", "", true));
        step("Проверить валидацию имен", () ->
            registerSteps.validateValidationError(response, 422)
        );
    }

    @Test
    @AllureId("70")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("🚫 Регистрация с коротким паролем")
    @Order(7)
    void registrationWithShortTEST_PASSWORD_ValidationError() {

        Response response = step("регистрация с коротким паролем", () ->
                registerSteps.registerUser(TEST_EMAIL, TEST_FIRST_NAME, "12345", "", "", true));

        step("Проверить валидацию пароля", () ->
            registerSteps.validateValidationError(response, 422)
        );
    }

    @ParameterizedTest
    @AllureId("77")
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
                registerSteps.registerUser(invalidEmail, "test", TEST_PASSWORD, "", "", true));
        step("Проверить валидацию email формата", () ->
            registerSteps.validateValidationError(response, 422)
        );
    }

    @Nested
    @DisplayName("🔍 Детальные проверки валидации")
    class DetailValidationCheks {

        @Test
        @DisplayName("📏 Очень длинное имя пользователя")
        void veryLongUserName_ShouldHandleGracefully() {
            String longName = faker.name().firstName().repeat(10);

            Response response = step("Регистарция с очень длинным именем", () ->
                    registerSteps.registerUser(TEST_EMAIL, longName, TEST_PASSWORD, "", "", true));
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

            step("Подготовить данные с длинным email (" + emailLength + " символов", () ->{
                registerSteps.attachRegistrationData(longEmail, TEST_FIRST_NAME, "",true);
                AllureAttachments.attachText("Детали длинного email",
                        "Запрошенная длина: " + emailLength + " символов\n" +
                        "Фактическая длина: " + longEmail.length() + " символов\n" +
                        "Email: " + longEmail);
            });

            Response response = step("Регистрация с очень длинным email (" + emailLength + " символов)", () ->
                    registerSteps.registerUser(longEmail, TEST_FIRST_NAME, TEST_PASSWORD, "", "", true));
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
            String nameWithSpecialChars = "Test-User_Name123";

            Response response = step("Регистрация с спецсимволами в имени", () ->
                    registerSteps.registerUser(TEST_EMAIL, nameWithSpecialChars, TEST_PASSWORD, "", "", true));
            step("Проверить обработку спецсмволов", () -> {
                int statusCode = response.getStatusCode();
                assertTrue(statusCode == 200 || statusCode == 422,
                        "Ожидался статус 200 или 422, но получили: " + statusCode);
            });
        }

        @Test
        @DisplayName("📝 Проверка что ответ всегда JSON")
        void responseAlwaysJson() {

            Response successResponse = registerSteps.registerUser(TEST_EMAIL, TEST_FIRST_NAME, TEST_PASSWORD, "", "", true);
            Response errorResponse = registerSteps.registerUser("tp5052_2@test.ru", "Test User", TEST_PASSWORD, "", "", true);

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
                    Response response = registerSteps.registerUser(TEST_EMAIL, TEST_FIRST_NAME, TEST_PASSWORD, "", "", true);
                    registerSteps.validateResponseTime(response, 3000);
                });

                step("Проверить время ответа для ошибки валидации", () -> {
                    Response response = registerSteps.registerUser("invalid-email", "test", TEST_PASSWORD, "", "", true);
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
                    Response response = registerSteps.registerUser(email, "test", TEST_PASSWORD, "", "", true);
                    registerSteps.validateNoSensitiveDate(response);
                }
            }

            @Test
            @DisplayName("🔐 Проверка безопасности паролей")
            void TEST_PASSWORDSecurityValidation() {

                Response response = registerSteps.registerUser(TEST_EMAIL, TEST_FIRST_NAME, TEST_PASSWORD, "", "", true);
                String responseBody = response.getBody().asString().toLowerCase();

                // Пароль не должен возвращаться в ответе
                assertFalse(responseBody.contains(TEST_PASSWORD), "Пароль не должен быть в ответе");
            }
        }
    }
}