package ru.tutor.steps;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.tutor.api.UserApi;
import ru.tutor.config.TestConfig;
import ru.tutor.constants.PromoCodeTestData;
import ru.tutor.model.*;
import ru.tutor.model.fortune.GameStatsResponse;
import ru.tutor.utils.AllureAttachments;

import java.util.Base64;
import java.util.List;

import static io.qameta.allure.Allure.step;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Шаги для работы с ЛК пользователя
 */

public class UserSteps {
    private final UserApi userApi;
    private UserProfile currentUserProfile;
    private String currentAuthToken;

    public UserSteps(String baseUrl) {
        this.userApi = new UserApi(baseUrl);
    }

    // ==================== РАБОТА С ТОКЕНОМ ИЗ ВКЛАДКИ ====================

    /**
     * Получение токена из текущей вкладки пользователя
     */
    @Step("Получение токена из вкладки пользователя")
    public String getTokenFromUserTab(){
        String token = AdminToUserLoginSteps.switchToTabAndGetToken();

        if (token == null || token.isEmpty()) {
            throw new IllegalStateException("не удалось получить токен из владки пользователя");
        }

        this.currentAuthToken = token;
        return token;
    }

    /**
     * Обновление токена из вкладки пользователя
     */
    @Step("Обновление токена из вкладки пользователя")
    public UserSteps refreshTokenFromUserTab() {
        getTokenFromUserTab();
        return this;
    }

    // ==================== РАБОТА С ЛОГИНОМ ====================

    @Step("POST /user/login - вход пользователя {email}")
    public Response login(String email, String password) {
        Response response = userApi.login(email, password);
        AllureAttachments.attachJson("Login Response", response.getBody().asString());
        AllureAttachments.attachJson("Login Headers", response.getHeaders().toString());
        return response;
    }

    @Step("Выполнить логин и получить токен")
    public String loginAndGetToken(String email, String password) {
        Response response = login(email, password);

        // Проверяем базовую структуру ответа
        response.then()
                .statusCode(200)
                .body("$", hasKey("token"))
                .body("token", instanceOf(String.class))
                .body("token", not(emptyOrNullString()));

        // Извлекаем токен
        String token = response.jsonPath().getString("token");
        currentAuthToken = token;

        // Логируем часть токена для отладки
        AllureAttachments.attachText("Получен Bearer токен",
                "Токен получен успешно (первые 50 символов): " +
                        token.substring(0, Math.min(50, token.length())) + "...");

        return token;
    }

    @Step("GET /user/profile - получить профиль пользователя")
    public Response getProfile(String authToken) {
        Response response = userApi.getProfile(authToken);
        AllureAttachments.attachJson("Profile Response", response.getBody().asString());
        AllureAttachments.attachJson("Profile Headers", response.getHeaders().toString());
        return response;
    }

    @Step("Проверить ответ профиля")
    public UserProfile validateProfileResponse(Response response, String expectedEmail) {
        response.then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("email", equalTo(expectedEmail))
                .body("isTariffLifetime", notNullValue())
                .body("isYtSubscribed", notNullValue())
                .body("isVkSubscribed", notNullValue())
                .body("isTgSubscribed", notNullValue());

        UserProfile userProfile = response.as(UserProfile.class);
        assertNotNull(userProfile, "UserProfile should not be null");

        return userProfile;
    }

    @Step("Получить профиль с текущим токеном")
    public String getCurrentAuthToken() {
        if (currentAuthToken == null) {
            throw new IllegalStateException("Токен не получен. Сначала выполните login!");
        }
        return currentAuthToken;
    }

    @Step("POST /user/logout - выход пользователя")
    public Response logout(String authToken) {
        Response response = userApi.logout(authToken);

        step("Проверка ответа логаута", () -> {
            response.then()
                    .statusCode(200)
                    .contentType(ContentType.JSON);

            Allure.addAttachment("Logout Response",
                    "✅ Статус: 200 OK\n📦 Тело: " + response.getBody().asString());
        });

        // Логируем информацию о JWT токене
        step("Информация о токене", () -> {
            String[] parts = authToken.split("\\.");
            if (parts.length >= 2) {
                String payload = new String(Base64.getDecoder().decode(parts[1]));
                Allure.addAttachment("JWT Payload", "application/json", payload);
            }
        });

        // Не проверяем токен, так как знаем, что он продолжит работать
        Allure.addAttachment("Примечание",
                "⚠️ Сервер использует stateless JWT токены.\n" +
                        "Токен будет работать до истечения срока действия.\n" +
                        "Для полного выхода закройте браузер или удалите токен локально.");

        return response;
    }

    @Step("Валидация успешного логаута")
    public LogoutResponse validateSuccessfulLogout(Response response) {
        // Проверяем базовые заголовки и статус код
        response.then()
                .statusCode(200)
                .contentType("application/json")
                .header("Access-Control-Allow-Origin", equalTo(TestConfig.getCurrentClientDomain()))
                .header("Access-Control-Allow-Credentials", equalTo("true"));

        // Парсим ответ
        LogoutResponse logoutResponse = response.as(LogoutResponse.class);
        assertNotNull(logoutResponse, "Logout response should not be null");

        // Проверяем успешность операции
        if (logoutResponse.getSuccess() != null) {
            assertTrue(logoutResponse.getSuccess(), "Logout should be successful");
        }

        return logoutResponse;
    }

    @Step("Проверка что токен больше не работает после логаута")
    public void verifyTokenInvalidAfterLogout(String authToken) {
        Response profileResponse = userApi.getProfile(authToken);

        int statusCode = profileResponse.getStatusCode();
        AllureAttachments.attachText("Проверка токена после логаута",
                "Статус код при попытке доступа: " + statusCode + "\n" +
                        "Результат: " + ((statusCode == 401 || statusCode == 403) ? "✅ Токен инвалидирован" :
                        "⚠ Токен все еще работает")
        );
        assertTrue(statusCode == 401 || statusCode == 403,
                "После логаута токен должен быть невалидным. Получен статус: " + statusCode);
    }

    @Step("Полный цикл: логин → действия → логаут")
    public Response fullAuthCycle(String email, String password) {
        Response response = userApi.login(email, password);
        String token = response.jsonPath().getString("token");
        assertNotNull(token, "token should not be null");

        Response profileResponse = userApi.getProfile(token);
        profileResponse.then()
                .statusCode(200);

        return userApi.logout(token);
    }

    @Step("GET /user/game-stats - получить игровую статистику")
    public Response getGameStats(String authToken) {
        Response response = userApi.getGameStats(authToken);
        AllureAttachments.attachJson("Game Stats Response", response.getBody().asString());
        AllureAttachments.attachJson("Game Stats Headers", response.getHeaders().toString());
        return response;
    }

    @Step("Валидация ответа игровой статистики")
    public GameStatsResponse validateGameStatsResponse(Response response) {
        // Проверяем статус код и заголовки
        response.then()
                .statusCode(200)
                .contentType("application/json")
                .header("Access-Control-Allow-Origin", equalTo(TestConfig.getCurrentClientDomain()))
                .header("Access-Control-Allow-Credentials", equalTo("true"));

        // Проверяем основные поля
        response.then()
                .body("level", notNullValue())
                .body("experience", notNullValue())
                .body("experienceLevel", notNullValue())
                .body("levelUpLimit", notNullValue())
                .body("diamond", notNullValue())
                .body("season", notNullValue())
                .body("hasLoginToday", notNullValue())
                .body("days", instanceOf(List.class));

        // Парсим в модель
        GameStatsResponse stats = response.as(GameStatsResponse.class);
        assertNotNull(stats, "Game stats should not be null");

        return stats;
    }


    @Step("Выполнить логин и получить данные пользователя")
    public UserProfile loginAndGetUserProfile(String email, String password) {
        String token = loginAndGetToken(email, password);

        Response profileResponse = getProfile(token);

        UserProfile profile = validateProfileResponse(profileResponse, email);

        this.currentAuthToken = token;
        this.currentUserProfile = profile;

        AllureAttachments.attachText("Профиль пользователя",
                String.format("ID: %d\nEmail: %s",
                        profile.getId(),
                        profile.getEmail()));

        return profile;
    }

    // ==================== РЕГИСТРАЦИЯ ====================

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
        response.then()
                .statusCode(200)
                .contentType("application/json")
                .header("Access-Control-Allow-Origin", equalTo(TestConfig.getCurrentClientDomain()))
                .header("Access-Control-Allow-Credentials", equalTo("true"));

        response.then()
                .body("$", hasKey("message"))
                .body("message", equalTo("ok"));

        RegistrationResponse registrationResponse = response.as(RegistrationResponse.class);
        assertNotNull(registrationResponse, "Registration response should not be null");

        registrationResponse.setSuccess(true);

        return registrationResponse;
    }

    @Step("Проверка успешной регистрации")
    public void validateSuccessfulRegistration(RegistrationResponse response, String expectedEmail) {
        assertTrue(response.isSuccessful() || "ok".equals(response.getMessage()),
                "Registration response should be: success");
        if (response.hasUserId()) {
            assertTrue(response.getUserId() > 0, "user ID should be positive");
        }

        if (response.getEmail() != null) {
            assertEquals(expectedEmail, response.getEmail(), "Email address should match");
        }

        AllureAttachments.attachText("Регистрация успешна",
                String.format("""
                                ✅ Пользователь успешно зарегистрирован
                                📧 Email: %s
                                🆔 User ID: %d
                                🔑 Token: %s
                                📝 Сообщение: %s
                                """,
                        expectedEmail,
                        response.getUserId() != null ? response.getUserId() : "не указан",
                        response.getShortToken() != null ? response.getShortToken() : "не получен",
                        response.getMessage()));
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

        assertTrue(statusCode == expectedStatusCode || statusCode == 400 || statusCode == 422,
                "Should return error status code. Got: " + statusCode);

        String body = response.getBody().asString();
        if (expectedMessagePart != null && !expectedMessagePart.isEmpty()) {
            assertTrue(body.toLowerCase().contains(expectedMessagePart.toLowerCase()),
                    "Response should contain: " + expectedMessagePart);
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
            token = loginAndGetToken(request.getEmail(), request.getPassword());
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

    @Step("Создание случайного пользователя (регистрация + вход)")
    public UserProfile createRandomUser() {
        RegistrationRequest request = RegistrationRequest.generateRandom();
        String token = registerAndLogin(request);

        Response profileResponse = getProfile(token);
        UserProfile profile = validateProfileResponse(profileResponse, request.getEmail());

        currentUserProfile = profile;

        AllureAttachments.attachText("Создан случайный пользователь",
                String.format("""
                                🆕 Новый пользователь создан
                                📧 Email: %s
                                👤 Имя: %s
                                🆔 ID: %d
                                """,
                        profile.getEmail(),
                        profile.getName(),
                        profile.getId()));

        return profile;
    }

    // ==================== ПРОМОКОДЫ ====================

    @Step("POST /user/promo-codes/apply - применение промокода")
    public Response applyPromoCode(String authToken, ApplyPromoCodeRequest request) {
        String effectiveToken = authToken;
        if (effectiveToken == null || effectiveToken.isEmpty()) {
            if (currentAuthToken != null && !currentAuthToken.isEmpty()) {
                effectiveToken = currentAuthToken;
                Allure.addAttachment("Использование токена", "text/plain", "Используем сохранённый токен из контекста");
            } else {
                effectiveToken = getTokenFromUserTab();
                Allure.addAttachment("Использование токена", "text/plain", "Получен новый токен из вкладки пользователя");
            }
        }

        AllureAttachments.attachJson("Apply Promo Code Request",
                String.format("{\"promoCodeName\":\"%s\",\"orderNumber\":\"%s\"}",
                        request.getPromoCodeName(), request.getOrderNumber()));
        Response response = userApi.applyPromoCode(authToken, request);
        AllureAttachments.attachJson("Apply Promo Code Response", response.getBody().asString());
        AllureAttachments.attachJson("Apply Promo Code Headers", response.getHeaders().toString());
        return response;
    }

    @Step("Валидация успешного ответа при применении промокода")
    public ApplyPromoCodeResponse validateSuccessfulPromoCodeResponse(Response response) {
        response.then()
                .statusCode(200)
                .contentType("application/json")
                .header("Access-Control-Allow-Origin", equalTo(TestConfig.getCurrentClientDomain()))
                .header("Access-Control-Allow-Credentials", equalTo("true"));

        response.then()
                .body("$", hasKey("message"));

        ApplyPromoCodeResponse promoResponse = response.as(ApplyPromoCodeResponse.class);
        assertNotNull(promoResponse, "Promo code response should not be null");
        assertTrue(PromoCodeTestData.isSuccessResponse(promoResponse.getMessage()), "Promo code response should be success");
//        assertTrue(promoResponse.isSuccessful(), "Promo code response should be: success");

        AllureAttachments.attachText("Промокод применен",
                String.format("""
                                ✅ Промокод успешно применен
                                📝 Сообщение: %s
                                """,
                        promoResponse.getMessage()));

        return promoResponse;
    }

    @Step("Валидация ответа с ошибкой при применении промокода")
    public ApplyPromoCodeResponse validatePromoCodeErrorResponse(Response response, int expectedStatusCode, String expectedError) {
        int statusCode = response.getStatusCode();

        response.then()
                .statusCode(expectedStatusCode)
                .contentType("application/json");

        response.then()
                .body("$", hasKey("error"));

        ApplyPromoCodeResponse promoResponse = response.as(ApplyPromoCodeResponse.class);
        assertNotNull(promoResponse, "Promo code response should not be null");
        assertTrue(promoResponse.hasError(), "Should have error message");

        String actualErrorType = PromoCodeTestData.getErrorType(promoResponse.getError());

        if (expectedError != null && !expectedError.isEmpty()) {
            assertTrue(PromoCodeTestData.isExpectedError(promoResponse.getError(), expectedError),
                    String.format("Error should contain '%s'. Actual: '%s'",
                            expectedError, actualErrorType, promoResponse.getError()));
        }

        AllureAttachments.attachText("Ошибка применения промокода",
                String.format("""
                        ❌ Промокод не применен
                        📊 Статус: %d
                        📝 Ошибка: %s
                        """,
                        statusCode,
                        actualErrorType,
                        promoResponse.getError()));

        return promoResponse;
    }

    @Step("Применение промокода с проверкой успеха")
    public ApplyPromoCodeResponse applyPromoCodeAndValidate(String promoCodeName,
                                                            String orderNumber) {
        String authToken = getTokenFromUserTab();
        ApplyPromoCodeRequest request = ApplyPromoCodeRequest.of(promoCodeName, orderNumber);
        Response response = applyPromoCode(authToken, request);
        return validateSuccessfulPromoCodeResponse(response);
    }

    @Step("Применение промокода с ожидаемой ошибкой")
    public ApplyPromoCodeResponse applyPromoCodeAndExpectError(String promoCodeName,
                                                               String orderNumber, String expectedError) {
        String authToken = getTokenFromUserTab();

        ApplyPromoCodeRequest request = ApplyPromoCodeRequest.of(promoCodeName, orderNumber);
        Response response = applyPromoCode(authToken, request);
        return validatePromoCodeErrorResponse(response, 400, expectedError);
    }

//    @Step("Применение промокода текущим пользователем")
//    public ApplyPromoCodeResponse applyPromoCodeForCurrentUser(String promoCodeName, String orderNumber) {
//        if (currentAuthToken == null) {
//            throw new IllegalStateException("Токен не получен. Сначала выполните login!");
//        }
//        return applyPromoCodeAndValidate(currentAuthToken, promoCodeName, orderNumber);
//    }
}
