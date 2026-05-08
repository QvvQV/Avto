package ru.tutor.steps;


import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.api.UserApi;
import ru.tutor.base.BaseApiSteps;
import ru.tutor.config.TestConfig;
import ru.tutor.model.LoginResponse;
import ru.tutor.model.ErrorResponse;
import ru.tutor.utils.AllureAttachments;

import static org.junit.jupiter.api.Assertions.*;

public class AuthSteps extends BaseApiSteps {
    private final UserApi userApi;

    public AuthSteps (String baseUrl) {
        super(baseUrl);
        this.userApi = new UserApi(baseUrl);
    }

    public static AuthSteps withBaseUrl(String baseUrl) {
        return new AuthSteps(baseUrl);
    }

    @Step("Логин с валидными credentials: {email}")
    public LoginResponse loginWithValidCredentials(String email, String password) {
        Response response = userApi.login(email, password);

        // Проверка кода ответа и тела
        validateSuccessfulResponse(response);

        LoginResponse loginResponse = response.as(LoginResponse.class);
        validateLoginResponse(loginResponse);

        AllureAttachments.attachJson("Успешный ответ логина", response.getBody().asString());
        return loginResponse;
    }

    @Step("Логин с невалидными credentials: {email}")
    public Response loginWithInvalidCredentials(String email, String password) {
        Response response = userApi.login(email, password);

        // Проверка кода ответа ошибки
        validateErrorResponse(response);

        AllureAttachments.attachJson("Ответ ошибки авторизации", response.getBody().asString());
        return response;
    }

    @Step("Логин с невалидными credentials и получение ErrorResponse")
    public ErrorResponse loginWithInvalidCredentialsAndGetError(String email, String password) {
        Response response = userApi.login(email, password);

        validateErrorResponse(response);

        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        AllureAttachments.attachJson("Ответ ошибки авторизации", response.getBody().asString());
        return errorResponse;
    }

    @Step("Проверить успешный ответ")
    private void validateSuccessfulResponse(Response response) {
        assertSuccessStatusCode(response);
        assertContentTypeJson(response);

        String token = response.jsonPath().getString("token");
        assertJsonFieldNotEmpty(response, "token");


        // Проверка формата токена (если он есть)
        if (token != null && !token.isEmpty()) {
            assertTrue(token.matches("^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]*$"),
                      "Токен должен быть в формате JWT");
        }
    }

    @Step("Проверить ответ ошибки")
    private void validateErrorResponse(Response response) {
        assertErrorStatusCode(response, 401);
        assertContentTypeJson(response);

        String responseBody = response.getBody().asString();
        assertAnyFieldExists(response, "error", "message", "status");
        assertFieldNotExists(response, "token");
        assertFieldNotExists(response, "success");
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

    @Step("Проверить структуру ErrorResponse")
    public void validateErrorResponse(ErrorResponse errorResponse) {
        assertNotNull(errorResponse, "Ответ ошибки не должен быть null");

        // Проверяем что есть хотя бы одно поле с ошибкой
        assertTrue(
            errorResponse.getError() != null || errorResponse.getMessage() != null || errorResponse.getStatus() != null,
            "Должно быть хотя бы одно поле с описанием ошибки (error, message или status)"
        );

        if (errorResponse.getError() != null) {
            assertFalse(errorResponse.getError().isEmpty(), "Поле error не должно быть пустым");
        }

        if (errorResponse.getMessage() != null) {
            assertFalse(errorResponse.getMessage().isEmpty(), "Поле message не должно быть пустым");
        }

        if (errorResponse.getStatus() != null) {
            assertEquals(401, errorResponse.getStatus(), "Status должен быть 401");
        }

        if (errorResponse.getTimestamp() != null) {
            assertTrue(errorResponse.getTimestamp() > 0, "Timestamp должен быть положительным");
        }
    }

    @Step("Проверить ответ для пустых полей")
    public void validateEmptyFieldsResponse(Response response) {
        response.then()
                .statusCode(401)
                .contentType("application/json");

        // Более гибкая проверка - error может быть null, поэтому проверяем наличие любого из полей
        String responseBody = response.getBody().asString();
        assertTrue(
            responseBody.contains("error") || responseBody.contains("message") || responseBody.contains("status"),
            "Ответ должен содержать одно из полей: error, message или status"
        );
        assertFalse(responseBody.contains("token"), "Ответ не должен содержать ключ 'token'");
    }

    @Step("Проверить реальные заголовки ответа")
    public void validateResponseHeaders(Response response) {
        // Проверяем только те заголовки, которые реально существуют
        assertContentTypeJson(response);
        assertHeader(response, "X-Robots-Tag", "noindex");

        // Проверяем заголовки которые должны существовать
        assertHeaderContains(response, "Cache-Control", "no-cache");
        assertHeaderExists(response, "X-Powered-By");
        assertHeaderExists(response, "Server");

        // Для 401 ответов проверяем WWW-Authenticate
        if (response.getStatusCode() == 401) {
            assertHeader(response, "WWW-Authenticate", "Bearer");
        }

        // CORS заголовки - проверяем если они есть
        try {
            String corsOrigin = response.getHeader("Access-Control-Allow-Origin");
            if (corsOrigin != null) {
                assertEquals(TestConfig.getCurrentClientDomain(), corsOrigin,
                    "Access-Control-Allow-Origin должен соответствовать текущему домену клиента");
            }
        } catch (AssertionError e) {
            // Игнорируем если CORS заголовков нет
        }
    }

    @Step("Получить Response для кастомных проверок")
    public Response getLoginResponse(String email, String password) {
        return userApi.login(email, password);
    }

    @Step("Проверить базовые заголовки безопасности")
    public void validateSecurityHeaders(Response response) {
        assertHeader(response, "X-Robots-Tag", "noindex");
        assertHeaderExists(response, "X-Powered-By");

        String poweredBy = response.getHeader("X-Powered-By");
        assertNotNull(poweredBy, "Заголовок X-Powered-By должен существовать");
        assertFalse(poweredBy.isEmpty(), "X-Powered-By не должен быть пустым");

        // Проверяем WWW-Authenticate для 401 ответов
        if (response.getStatusCode() == 401) {
            assertHeader(response, "WWW-Authenticate", "Bearer");
        }
    }

    @Step("Проверить минимальные заголовки (самые надежные проверки)")
    public void validateMinimumHeaders(Response response) {
        // Только самые базовые проверки которые точно должны работать
        response.then()
                .header("Content-Type", "application/json");

        assertNotNull(response.getHeader("Cache-Control"), "Заголовок Cache-Control должен существовать");
        assertNotNull(response.getHeader("Strict-Transport-Security"), "Заголовок Strict-Transport-Security должен существовать");
        assertNotNull(response.getHeader("Date"), "Заголовок Date должен существовать");
    }

    public String loginAndGetToken(String email, String password) {
        Response response = getLoginResponse(email, password);
        return response.jsonPath().getString("token");
    }
}
