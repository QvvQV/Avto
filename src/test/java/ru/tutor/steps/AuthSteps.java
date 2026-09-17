package ru.tutor.steps;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.constants.AddressPage;
import ru.tutor.model.LoginResponse;
import ru.tutor.model.UserProfile;
import ru.tutor.api.UserApi;
import ru.tutor.base.BaseApiSteps;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WaitHelper;

import java.util.Base64;

import static com.codeborne.selenide.Selenide.open;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthSteps extends BaseApiSteps {

    private final UserApi userApi;
    private final ProfileSteps profileSteps;
    private String currentAuthToken;

    public AuthSteps(String baseUrl) {
        super(baseUrl);
        this.userApi = new UserApi(baseUrl);
        this.profileSteps = new ProfileSteps(baseUrl);
    }

    @Step("Получение токена из вкладки пользователя")
    public String getTokenFromUserTab() {
        String token = AdminToUserLoginSteps.switchToTabAndGetToken();
        if (token == null || token.isEmpty()) {
            throw new IllegalStateException("не удаллось получить токен из вкладки пользователя");
        }
        this.currentAuthToken = token;
        return token;
    }

    @Step("POST /user/login - вход пользователя {email}")
    public Response login(String email, String password) {
       return step("Вход пользователя", () -> {
           Response response = userApi.login(email, password);
           AllureAttachments.attachJson("Login Response", response.getBody().asString());
           AllureAttachments.attachJson("Login Headers", response.getHeaders().toString());
           return response;
       });
    }

    @Step("Выполнить логин и получить токен")
    public LoginResponse loginAndGetTokens(String email, String password) {

        Response response = login(email, password);

        // Проверяем базовую структуру ответа
        assertSuccessStatusCode(response);

        LoginResponse tokens = response.getBody().as(LoginResponse.class);
        assertNotNull(tokens.token(), "token не должен быть null");
        assertNotNull(tokens.refreshToken(), "refresh_token не должен быть null");

        currentAuthToken = tokens.token();

        return tokens;
    }

    @Step("Вход в ЛК через refresh_token + возврат токена")
    public String loginViaRefreshToken(String email, String password) {
        LoginResponse tokens = loginAndGetTokens(email, password);
        step("Переход в ЛК через refresh_token", () -> {
            open(AddressPage.LOGIN + "?refresh_token=" + tokens.refreshToken());
            WaitHelper.waitForPageLoad();
        });
        return tokens.token();
    }

    public String loginAndGetToken(String email, String password) {
        return loginAndGetTokens(email, password).token();
    }

    @Step("Выполнить логин и получить данные пользователя")
    public UserProfile loginAndGetUserProfile(String email, String password) {
        String token = loginAndGetToken(email, password);
        Response profileResponse = profileSteps.getProfile(token);
        UserProfile profile = profileSteps.validateProfileResponse(profileResponse, email);

        AllureAttachments.attachText("Профиль пользователя",
                String.format("ID: %d\nEmail: %s", profile.getId(), profile.getEmail()));
        return profile;
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
            assertSuccessStatusCode(response);

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

    @Step("Проверить структуру LoginResponse")
    public void validateLoginResponse(LoginResponse loginResponse) {
        assertNotNull(loginResponse, "Ответ логина не должен быть null");
        assertNotNull(loginResponse.token(), "Токен не должен быть null");
        assertTrue(loginResponse.token().length() > 10, "Токен должен быть достаточно длинным");

        assertTrue(loginResponse.token().matches("^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]*$"),
                "Токен должен быть в формате JWT");

        if (loginResponse.refreshToken() != null) {
            assertTrue(loginResponse.refreshToken().length() > 10,
                    "Refresh token должен быть достаточно длинным");
        }

        if (loginResponse.expiresIn() != null) {
            assertTrue(loginResponse.expiresIn() > 0,
                    "Expires in должен быть положительным числом");
        }
    }

    @Step("GET /user -проверка авторизации")
    public Response checkAuth(String authToken) {
        Response response = userApi.checkAuth(authToken);
        AllureAttachments.attachJson("Check Auth Response", response.getBody().asString());
        return response;
    }

    @Step("Валидация успешной проверки авторизации")
    public void validateAuthCheckResponse(Response response) {
        assertBasicResponse(response);
        assertJsonFieldEquals(response, "$", "OK");
    }
}
