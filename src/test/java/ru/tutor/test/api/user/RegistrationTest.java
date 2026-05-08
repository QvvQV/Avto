package ru.tutor.test.api.user;

import io.qameta.allure.*;
import io.qameta.allure.junit5.AllureJunit5;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tutor.config.TestConfig;
import ru.tutor.model.RegistrationRequest;
import ru.tutor.model.RegistrationResponse;
import ru.tutor.steps.UserSteps;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(AllureJunit5.class)
@Epic("API Тесты аутентификации")
@Feature("Регистрация пользователей")
@DisplayName("тесты эндпоинта /register")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RegistrationTest {

    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private final UserSteps userSteps = new UserSteps(BASE_URL);

    private static String registeredEmail;

    @Test
    @Order(1)
    @DisplayName("✅ Успешная регистрация нового пользователя")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("smoke")
    @Tag("positive")
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

            registeredEmail = request.getEmail();

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
    @DisplayName("✅ Регистарция с минимальным набором данных")
    @Severity(SeverityLevel.NORMAL)
    @Tag("positive")
    void registerMinimalUser_Sucess() {
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
    @DisplayName("✅ Регистрация и автоматический вход")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("integration")
    void registerAndLogin_Success() {
        RegistrationRequest request = RegistrationRequest.generateRandom();

        String token = step("Регистрация и вход", () -> userSteps.registerAndLogin(request)
        );

        assertNotNull(token, "Токен не должен быть null");
        assertFalse(token.isEmpty(), "Токен не должен быть пустым");

        Response profileResponse = step("Проверка токена", () ->
                userSteps.getProfile(token)
        );

        profileResponse.then().statusCode(200);

        Allure.addAttachment("Успех", "text/plain",
                String.format("✅ Пользователь %s зарегистрирован и авторизован", request.getEmail()));
    }

    @Test
    @Order(4)
    @DisplayName("❌ Регистрация без согласия с политикой")
    @Severity(SeverityLevel.NORMAL)
    @Tag("validation")
    @Tag("negative")
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
    @DisplayName("❌ Регистрация с невалидным email")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    @Tag("validation")
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
    @DisplayName("❌ Регистрация с пустым именем")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    @Tag("validation")
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
    @DisplayName("❌ Регистрация с коротким паролем")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    @Tag("validation")
    void registerWithShortPassword_ShouldFail() {
        RegistrationRequest request = RegistrationRequest.generateWithShortPassword();

        Response response = step("Регистрация с коротким паролем", () ->
                userSteps.register(request)
        );

        step("Проверка ошибки", () ->
                userSteps.validateRegistrationError(response, 422, "shortPassword")
        );
    }

    @Test
    @Order(8)
    @DisplayName("✅ Регистрация с длинным паролем")
    @Severity(SeverityLevel.NORMAL)
    @Tag("positive")
    @Tag("validation")
    void registerWithLongPassword_Success() {
        RegistrationRequest request = RegistrationRequest.generateWithLongPassword();

        RegistrationResponse response = step("Регистрация с длинным паролем", () ->
                userSteps.registerAndGetResponse(request)
        );

        assertTrue(response.isSuccessful(), "Registration should be successful");
    }

    @Test
    @Order(9)
    @DisplayName("📊 Массовая регистрация пользователей")
    @Severity(SeverityLevel.MINOR)
    @Tag("stress")
    void massRegistration() {
        int count = 3;

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
    @DisplayName("🔄 Регистрация с разными gender значениями")
    @Severity(SeverityLevel.MINOR)
    @Tag("positive")
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
    @DisplayName("✅ Регистрация с пустым номером телефона")
    @Severity(SeverityLevel.NORMAL)
    @Tag("positive")
    void registerWithEmptyPhone(){
        RegistrationRequest request = RegistrationRequest.generateWithEmptyPhone();

        RegistrationResponse response = step("Регистрация с пустым номером", () ->
                userSteps.registerAndGetResponse(request)
        );

        assertTrue(response.isSuccessful(), "Registration should be successful");
    }
}