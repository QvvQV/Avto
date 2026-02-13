package ru.tutor.steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.tutor.api.UserApi;
import ru.tutor.model.fortune.GameStatsResponse;
import ru.tutor.model.LoginResponse;
import ru.tutor.model.LogoutResponse;
import ru.tutor.model.UserProfile;
import ru.tutor.utils.AllureAttachments;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Шаги для работы с ЛК пользователя
 */

public class UserSteps {
    private final UserApi userApi;
    private String currentAuthToken;

    public UserSteps(String baseUrl) {
        this.userApi = new UserApi(baseUrl);
    }

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

    @Step("Получить профиль с текущим токеном")
    public Response getProfileWithCurrentToken() {
        if (currentAuthToken == null) {
            throw new IllegalStateException("Токен не получен. Сначала выполните логин.");
        }
        return getProfile(currentAuthToken);
    }

    @Step("Проверить ответ логина")
    public LoginResponse validateLoginResponse(Response response) {
        response.then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("token", not(emptyOrNullString()))
                .body("username", not(emptyOrNullString()));

        LoginResponse loginResponse = response.as(LoginResponse.class);
        assertNotNull(loginResponse, "Login response should not be null");
        assertNotNull(loginResponse.getToken(), "token should not be null");

        return loginResponse;
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

    public String getCurrentAuthToken() {
        return currentAuthToken;
    }

    @Step("POST /user/logout - выход пользователя")
    public Response logout(String authToken) {
        Response response = userApi.logout(authToken);

        AllureAttachments.attachJson("Logout Response", response.getBody().asString());
        AllureAttachments.attachJson("Logout Headers", response.getHeaders().toString());

        return response;
    }

    @Step("Валидация успешного логаута")
    public LogoutResponse validateSuccessfulLogout(Response response) {
        // Проверяем базовые заголовки и статус код
        response.then()
                .statusCode(200)
                .contentType("application/json")
                .header("Access-Control-Allow-Origin", equalTo("https://client.ttplace.ru"))
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
                .header("Access-Control-Allow-Origin", equalTo("https://client.ttplace.ru"))
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

    @Step("Получить ID пользователя из профиля")
    public Integer getUserIdFromProfile(String authToken) {
        Response response = getProfile(authToken);

        response.then()
                .statusCode(200)
                .contentType("application/json");

        Integer userId = response.jsonPath().getInt("id");

        if (userId == null) {
            throw new RuntimeException("ID пользователя не найден в ответе профиля");
        }

        AllureAttachments.attachJson("Получен ID пользователя",
                "User ID: " + userId + "\n" +
                "Email: " + response.jsonPath().getString("email"));

        return userId;
    }

    @Step("Получить ID текущего пользователя")
    public Integer getCurrentUserId() {
        if (currentAuthToken == null) {
            throw new IllegalStateException("Токен не получен. Сначала выполните логин.");
        }
        return getUserIdFromProfile(currentAuthToken);
    }

    @Step("Получить полную информацию о профиле пользователя")
    public UserProfile getFullUserProfile(String authToken) {
        Response response = getProfile(authToken);
        UserProfile profile = validateProfileResponse(response, null);

        AllureAttachments.attachText("Полная информация профиля",
                "ID: " + profile.getId() + "\n" +
                "Email: " + profile.getEmail());

        return profile;
    }
}
