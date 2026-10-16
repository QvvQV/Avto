package ru.tutor.test.api.user;

import io.qameta.allure.*;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.tutor.config.TestConfig;
import ru.tutor.model.account.RegistrationRequest;
import ru.tutor.model.account.RegistrationResponse;
import ru.tutor.steps.account.ProfileSteps;
import ru.tutor.steps.account.RegistrationSteps;

import java.util.Locale;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;
import ru.tutor.constants.Tags;


@Epic("API Тесты аутентификации")
@Feature("Регистрация пользователей")
@DisplayName("тесты эндпоинта /register")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag(Tags.INTEGRATION)
@Tag(Tags.API)
public class RegistrationTest {

    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private final RegistrationSteps userSteps = new RegistrationSteps(BASE_URL);
    private final ProfileSteps profile = new ProfileSteps(BASE_URL);


    private final Faker faker = new Faker(Locale.ENGLISH);

    private static final String TEST_PASSWORD = TestConfig.getTestPassword();
    private final String TEST_FIRST_NAME = faker.name().fullName();

    @Test
    @Order(1)
    @DisplayName("Успешная регистрация нового пользователя")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    void registerNewUser_Success() {
        RegistrationRequest request = RegistrationRequest.generateRandom();

        Allure.addAttachment("Регистрационные данные", "text/plain",
                String.format("Email: %s\nИмя: %s\nГендер: %s",
                        request.getEmail(), request.getName(), request.getGender()));

        Response response = step("Отправка запроса на регистрацию", () ->
                userSteps.register(request)
        );

        RegistrationResponse registrationResponse = step("Валидация ответа", () ->
                userSteps.validateRegistrationResponse(response)
        );

        step("Проверка успешной регистрации", () -> {
            assertEquals("ok", registrationResponse.getMessage(), "Сообщение должно быть 'ok'");

            assertTrue(registrationResponse.isSuccessful(), "Регистрация должна быть успешной");

            String registeredEmail = request.getEmail();

            Allure.addAttachment("Результат", "text/plain",
                    String.format("""
                                    ✅ Регистрация успешна
                                    📧 Email: %s
                                    📝 Ответ сервера: %s
                                    """,
                            registeredEmail,
                            registrationResponse.getMessage()));
        });
    }

    @Test
    @Order(2)
    @DisplayName("Регистарция с минимальным набором данных")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.POSITIVE)
    void registerMinimalUser_Success() {
        RegistrationRequest request = RegistrationRequest.generateMinimal();
        RegistrationResponse response = step("Регистрация с минимальными данными", () ->
                userSteps.registerAndGetResponse(request)
        );

        assertTrue(response.isSuccessful(), "Регистрация должна быть успешной");

        Allure.addAttachment("Минимальные данные", "text/plain",
                String.format("Создан пользователь: %s", request.getEmail()));
    }

    @Test
    @Order(3)
    @DisplayName("Регистрация и автоматический вход")
    @Severity(SeverityLevel.CRITICAL)
    void registerAndLogin_Success() {
        RegistrationRequest request = RegistrationRequest.generateRandom();

        String token = step("Регистрация и вход", () -> userSteps.registerAndLogin(request)
        );

        assertNotNull(token, "Токен не должен быть null");
        assertFalse(token.isEmpty(), "Токен не должен быть пустым");

        Response profileResponse = step("Проверка токена", () ->
                profile.getProfile(token)
        );

        profileResponse.then().statusCode(200);

        Allure.addAttachment("Успех", "text/plain",
                String.format("✅ Пользователь %s зарегистрирован и авторизован", request.getEmail()));
    }

    @Test
    @Order(4)
    @DisplayName("Регистрация без согласия с политикой")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void registerWithoutPolicyApproval_ShouldFail() {
        RegistrationRequest request = RegistrationRequest.generateWithoutPolicyApproval();

        Response response = step("Регистрация без согласия с политикой", () ->
                userSteps.register(request)
        );

        step("Проверка ошибки", () ->
                userSteps.validateRegistrationError(response, 422, "approvePolicy")
        );
    }

    @Test
    @Order(5)
    @DisplayName("Регистрация с невалидным email")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void registerWithInvalidEmail_ShouldFail() {
        RegistrationRequest request = RegistrationRequest.generateWithInvalidEmail();

        Response response = step("Регистрация с невалидным email", () ->
                userSteps.register(request)
        );

        step("Проверка ошибки", () ->
                userSteps.validateRegistrationError(response, 422, "email")
        );
    }

    @Test
    @Order(6)
    @DisplayName("Регистрация с пустым именем")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void registerWithEmptyName_ShouldFail() {
        RegistrationRequest request = RegistrationRequest.generateWithEmptyName();

        Response response = step("Регистрация с пустым именем", () ->
                userSteps.register(request)
        );

        step("Проверка ошибки", () ->
                userSteps.validateRegistrationError(response, 422, "name")
        );
    }

    @Test
    @Order(7)
    @DisplayName("Регистрация с коротким паролем")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void registerWithShortPassword_ShouldFail() {
        RegistrationRequest request = RegistrationRequest.generateWithShortPassword();

        Response response = step("Регистрация с коротким паролем", () ->
                userSteps.register(request)
        );

        step("Проверка ошибки", () ->
                userSteps.validateRegistrationError(response, 422, "shortPassword")
        );
        Allure.addAttachment("Ошибка", "Тест падает, потому что на беке нет валидации emails, она на фронте");
    }

    @Test
    @Order(8)
    @DisplayName("Регистрация с длинным паролем")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.POSITIVE)
    void registerWithLongPassword_Success() {
        RegistrationRequest request = RegistrationRequest.generateWithLongPassword();

        RegistrationResponse response = step("Регистрация с длинным паролем", () ->
                userSteps.registerAndGetResponse(request)
        );

        assertTrue(response.isSuccessful(), "Registration should be successful");
    }

    @Test
    @Order(9)
    @DisplayName("Массовая регистрация пользователей")
    @Severity(SeverityLevel.MINOR)
    void massRegistration() {
        int count = 7;

        step("Регистрация " + count + " пользователей", () -> {
            for (int i = 1; i <= count; i++) {
                RegistrationRequest request = RegistrationRequest.generateRandom();
                RegistrationResponse response = userSteps.registerAndGetResponse(request);

                assertTrue(response.isSuccessful(), "Регистрация #" + i + " должна быть успешной");

            }
        });

        Allure.addAttachment("Итог", "text/plain",
                String.format("✅ Успешно зарегистрировано %d пользователей", count));
    }

    @Test
    @Order(10)
    @DisplayName("Регистрация с разными gender значениями")
    @Severity(SeverityLevel.MINOR)
    @Tag(Tags.POSITIVE)
    void registerWithDifferentGenders() {
        Integer[] genders = {0, 1, 2};

        for (Integer gender : genders) {
            step("Регистрация с gender = " + gender, () -> {
                RegistrationRequest request = RegistrationRequest.generateRandom();

                RegistrationRequest customRequest = new RegistrationRequest(
                        request.getEmail(),
                        request.getName(),
                        request.getPhone(),
                        request.getPassword(),
                        request.getTelegram(),
                        request.getApprovePolicy(),
                        gender
                );

                Response response = userSteps.register(customRequest);
                RegistrationResponse registrationResponse = userSteps.validateRegistrationResponse(response);

                assertTrue(registrationResponse.isSuccessful(),
                        "Регистрация с gender=" + gender + " должна быть успешной");

                Allure.addAttachment("Gender=" + gender, "text/plain",
                        String.format("✅ Пользователь создан: %s", request.getEmail())
                );
            });
        }
    }

    @Test
    @Order(11)
    @DisplayName(" Регистрация с пустым номером телефона")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.POSITIVE)
    void registerWithEmptyPhone() {
        RegistrationRequest request = RegistrationRequest.generateWithEmptyPhone();

        RegistrationResponse response = step("Регистрация с пустым номером", () ->
                userSteps.registerAndGetResponse(request)
        );

        assertTrue(response.isSuccessful(), "Registration should be successful");
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName(" Регистрация с существующим email")
    @Order(12)
    @Tag(Tags.CONTRACT)
    void registrationWithExistingEmail_ConflictCheck() {
        RegistrationRequest request = new RegistrationRequest("tp5052_2@test.ru", "Test User", "", TEST_PASSWORD,"", true, 0);
        Response response = step("Регистрация с существующим email", () ->
                userSteps.register(request));
        step("Провера конфликта", () ->
            userSteps.validateExistingEmailError(response)

        );
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName(" Регистрация с пустым email")
    @Order(13)
    @Tag(Tags.CONTRACT)
    void registrationWithEmptyEmail_ValidationError() {
        RegistrationRequest request = new RegistrationRequest("", TEST_FIRST_NAME, "", TEST_PASSWORD, "", true, 0);
        Response response = step("Регистрация с пустым email", () ->
                userSteps.register(request));
        step("Проверить валидацию email", () ->
                userSteps.validateValidationError(response, 422)
        );
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
    @Order(14)
    @DisplayName(" Регистрация с невалидными email форматами")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)

    @Tag(Tags.CONTRACT)
    void registrationWithInvalidEmailFormats_ValidationErrors(String invalidEmail) {
        RegistrationRequest request = RegistrationRequest.generateRandom();
        RegistrationRequest customRequest = new RegistrationRequest(
                invalidEmail, request.getName(), request.getPhone(), request.getPassword(), request.getTelegram(), true, 0
        );
        Response response = step("Регистрация с невалидным email: " + invalidEmail, () ->
                userSteps.register(customRequest));
        step("Проверка валидации", () ->
                userSteps.validateRegistrationError(response, 422, "email")
        );
    }

    @Nested
    @DisplayName(" Детальные проверки валидации")
    class DetailValidationCheks {

        @Test
        @Order(15)
        @DisplayName("Очень длинное имя пользователя")
        @Severity(SeverityLevel.MINOR)
        void veryLongUserName_ShouldHandleGracefully() {
            RegistrationRequest request = RegistrationRequest.generateRandom();
            String longName = faker.name().firstName().repeat(36);
            RegistrationRequest  customRequest = new RegistrationRequest(
                    request.getEmail(), longName, request.getPhone(), request.getPassword(),
                    request.getTelegram(), true, request.getGender()
            );

            Response response = step("Регистарция с очень длинным именем", () ->
                    userSteps.register(customRequest));
            step("Проверить обработку длинного имени", () -> {
                int statusCode = response.getStatusCode();
                assertEquals(200, statusCode, "Ожидался статус 200, но получили: " + statusCode);
            });
        }

        @Test
        @Order(16)
        @DisplayName(" Спецсимволы в имени")
        @Severity(SeverityLevel.MINOR)
        void specialCharactersInName_BehaviorCheck() {
            RegistrationRequest request = RegistrationRequest.generateRandom();
            String nameWithSpecialChars = "Test-User_Name123";
            RegistrationRequest customRequest = new RegistrationRequest(
                    request.getEmail(), nameWithSpecialChars, request.getPhone(),
                    request.getPassword(), request.getTelegram(), true, request.getGender()
            );

            Response response = step("Регистрация с спецсимволами в имени", () ->
                    userSteps.register(customRequest));
            step("Проверить обработку спецсмволов", () -> {
                int statusCode = response.getStatusCode();
                assertTrue(statusCode == 200 || statusCode == 422,
                        "Ожидался статус 200 или 422, но получили: " + statusCode);
            });
        }
    }

    @Test
    @Order(17)
    @DisplayName(" Проверка что ответ всегда JSON")
    @Severity(SeverityLevel.MINOR)
    void responseAlwaysJson() {
        RegistrationRequest successReq = RegistrationRequest.generateRandom();

        Response successResponse = step("Успешная регистрация", () ->
                userSteps.register(successReq));
        successResponse.then().contentType("application/json");

        Response errorResponse = step("Регистрация с существующим email", () ->
                userSteps.register(new RegistrationRequest("tp5052_2@test.ru",
                        "Test User", "", TEST_PASSWORD, "", true, 0)));
        errorResponse.then().contentType("application/json");
    }

    @Nested
    @DisplayName("⚡ Performance и Security проверки")
    class PerformanceAndSecurityChecks {

        @Test
        @Order(18)
        @DisplayName(" Проверка времени ответа для разных сценариев")
        @Severity(SeverityLevel.MINOR)
        void responseTimeForDifferentScenarios() {
            step("Проверка времени ответа для успешной регистрации", () -> {
                RegistrationRequest  request = RegistrationRequest.generateRandom();
                Response response = step("Регистрация", () -> userSteps.register(request));
                userSteps.validateResponseTime(response, 3000);
            });
        }

        @Test
        @Order(19)
        @DisplayName(" Проверка безопасности паролей")
        @Severity(SeverityLevel.MINOR)
        @Tag(Tags.NEGATIVE)
        void TEST_PASSWORDSecurityValidation() {

            RegistrationRequest request = RegistrationRequest.generateRandom();

            Response response = userSteps.register(request);
            String responseBody = response.getBody().asString().toLowerCase();

            // Пароль не должен возвращаться в ответе
            assertFalse(responseBody.contains(TEST_PASSWORD), "Пароль не должен быть в ответе");
        }
    }
}