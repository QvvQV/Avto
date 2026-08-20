package ru.tutor.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.api.UserApi;
import ru.tutor.base.BaseApiSteps;
import ru.tutor.model.RegistrationRequest;
import ru.tutor.model.RegistrationResponse;
import ru.tutor.utils.AllureAttachments;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegistrationSteps extends BaseApiSteps {

    private final UserApi userApi;
    private final AuthSteps authSteps;
    private String currentAuthToken;

    public RegistrationSteps(String baseUrl) {
        super(baseUrl);
        this.userApi  = new UserApi(baseUrl);
        this.authSteps = new AuthSteps(baseUrl);
    }

    @Step("POST /register - регистрация нового пользователя")
    public Response register(RegistrationRequest request) {
        AllureAttachments.attachJson("Registration request",
                String.format("""
                                {
                                    "email": "%s",
                                    "name": "%s",
                                    "phone": "%s",
                                    "telegram": "%s",
                                    "approvePolicy": %s,
                                    "gender": %d
                                }""",
                        request.getEmail(),
                        request.getName(),
                        request.getPhone() != null ? request.getPhone() : "",
                        request.getTelegram() != null ? request.getTelegram() : "",
                        request.getApprovePolicy(),
                        request.getGender() != null ? request.getGender() : 0));

        Response response = userApi.register(request);
        AllureAttachments.attachJson("Registration Response", response.getBody().asString());
        AllureAttachments.attachJson("Registration Headers", response.getHeaders().toString());
        return response;
    }

    @Step("Выполнить регистрацию и получить ответ")
    public RegistrationResponse registerAndGetResponse(RegistrationRequest request) {
        Response response = register(request);
        return validateRegistrationResponse(response);
    }

    @Step("Валидация ответа регистрации")
    public RegistrationResponse validateRegistrationResponse(Response response) {
        assertBasicResponse(response);

        String message = response.jsonPath().getString("message");
        assertJsonFieldEquals(response, "message", "ok");

        RegistrationResponse registrationResponse = response.as(RegistrationResponse.class);
        assertNotNull(registrationResponse, "Ответ регистрации не должен быть null");

        registrationResponse.setSuccess(true);

        return registrationResponse;
    }

    @Step("Проверка ошибки регистрации")
    public void validateRegistrationError(Response response, int expectedStatusCode, String expectedMessagePart) {
        int statusCode = response.getStatusCode();

        AllureAttachments.attachText("Ошибка регистрации",
                String.format("""
                                ❌ Регистрация не удалась
                                📊 Статус код: %d (ожидался: %d)
                                📝 Сообщение: %s
                                """,
                        statusCode,
                        expectedStatusCode,
                        response.getBody().asString()));

        assertErrorStatusCode(response, expectedStatusCode);

        String body = response.getBody().asString();
        if (expectedMessagePart != null && !expectedMessagePart.isEmpty()) {
            assertTrue(body.toLowerCase().contains(expectedMessagePart.toLowerCase()),
                    "Ответ должен содержать: " + expectedMessagePart);
        }
    }

    @Step("Регистрация и автоматический вход")
    public String registerAndLogin(RegistrationRequest request) {
        RegistrationResponse registrationResponse = registerAndGetResponse(request);

        if (!registrationResponse.isSuccessful()) {
            throw new IllegalStateException("Registration failed: " + registrationResponse.getMessage());
        }

        String token = registrationResponse.getToken();

        if (token == null || token.isEmpty()) {
            token = authSteps.loginAndGetToken(request.getEmail(), request.getPassword());
        }

        currentAuthToken = token;

        AllureAttachments.attachText("Регистрация и вход",
                String.format("""
                                ✅ Пользователь зарегистрирован и авторизован
                                📧 Email: %s
                                🆔 User ID: %d
                                """,
                        request.getEmail(),
                        registrationResponse.getUserId()));

        return token;
    }

//    @Step("Создание случайного пользователя (регистрация + вход)")
//    public UserProfile createRandomUser() {
//        RegistrationRequest request = RegistrationRequest.generateRandom();
//        String token = registerAndLogin(request);
//
//        Response profileResponse = getProfile(token);
//        UserProfile profile = validateProfileResponse(profileResponse, request.getEmail());
//
//        currentUserProfile = profile;
//
//        AllureAttachments.attachText("Создан случайный пользователь",
//                String.format("""
//                                🆕 Новый пользователь создан
//                                📧 Email: %s
//                                👤 Имя: %s
//                                🆔 ID: %d
//                                """,
//                        profile.getEmail(),
//                        profile.getName(),
//                        profile.getId()));
//
//        return profile;
//    }

    @Step("Валидация конфликта email")
    public void validateExistingEmailError(Response response) {
        assertNoSensitiveData(response);
        assertErrorStatusCode(response, 400);
        assertContentTypeJson(response);
        assertAnyFieldExists(response, "message", "error");
    }

    @Step("Проверка ошибки валидации")
    public void validateValidationError(Response response, int expectedStatus) {
        assertErrorStatusCode(response, expectedStatus);
        assertContentTypeJson(response);
        assertAnyFieldExists(response, "errors", "error", "message");
    }

    @Step("Проверка времени ответа (максимум {maxTimeMs} мс)")
    public void validateResponseTime(Response response, long maxTimeMs) {
        long responseTime = response.getTime();
        assertTrue(responseTime <= maxTimeMs,
                "Время ответа должно быть меньше " + maxTimeMs + "мсб но было: " + responseTime + "мс");
    }
}
