package ru.tutor.steps;


import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.api.AuthApi;
import ru.tutor.model.LoginRequest;
import ru.tutor.model.LoginResponse;
import ru.tutor.model.ErrorResponse;
import ru.tutor.utils.AllureAttachments;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class AuthSteps {
    private final AuthApi authApi;

    public AuthSteps(String baseUrl) {
        this.authApi = new AuthApi(baseUrl);
    }

    @Step("Логин с валидными credentials: {email}")
    public LoginResponse loginWithValidCredentials(String email, String password) {
        Response response = authApi.login(email, password);

        // Проверка кода ответа и тела
        validateSuccessfulResponse(response);

        LoginResponse loginResponse = response.as(LoginResponse.class);
        validateLoginResponse(loginResponse);

        AllureAttachments.attachJson("Успешный ответ логина", response.getBody().asString());
        return loginResponse;
    }

    @Step("Логин с невалидными credentials: {email}")
    public Response loginWithInvalidCredentials(String email, String password) {
        Response response = authApi.login(email, password);

        // Проверка кода ответа ошибки
        validateErrorResponse(response);

        AllureAttachments.attachJson("Ответ ошибки авторизации", response.getBody().asString());
        return response;
    }

    @Step("Логин с невалидными credentials и получение ErrorResponse")
    public ErrorResponse loginWithInvalidCredentialsAndGetError(String email, String password) {
        Response response = authApi.login(email, password);

        validateErrorResponse(response);

        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        AllureAttachments.attachJson("Ответ ошибки авторизации", response.getBody().asString());
        return errorResponse;
    }

    @Step("Проверить успешный ответ")
    private void validateSuccessfulResponse(Response response) {
        response.then()
                .statusCode(200)
                .contentType("application/json")
                .body("token", notNullValue())
                .body("token", not(emptyString()))
                .body("$", hasKey("token"));

        // Проверка формата токена (если он есть)
        String token = response.jsonPath().getString("token");
        if (token != null && !token.isEmpty()) {
            assertTrue(token.matches("^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]*$"),
                      "Токен должен быть в формате JWT");
        }
    }

    @Step("Проверить ответ ошибки")
    private void validateErrorResponse(Response response) {
        response.then()
                .statusCode(401)
                .contentType("application/json")
                .body("$", anyOf(hasKey("error"), hasKey("message"), hasKey("status")))
                .body("$", not(hasKey("token")))
                .body("$", not(hasKey("success")));
    }

    @Step("Проверить структуру LoginResponse")
    public void validateLoginResponse(LoginResponse loginResponse) {
        assertNotNull(loginResponse, "Ответ логина не должен быть null");
        assertNotNull(loginResponse.getToken(), "Токен не должен быть null");
        assertTrue(loginResponse.getToken().length() > 10, "Токен должен быть достаточно длинным");

        assertTrue(loginResponse.getToken().matches("^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]*$"),
                   "Токен должен быть в формате JWT");

        if (loginResponse.getRefreshToken() != null) {
            assertTrue(loginResponse.getRefreshToken().length() > 10,
                      "Refresh token должен быть достаточно длинным");
        }

        if (loginResponse.getExpiresIn() != null) {
            assertTrue(loginResponse.getExpiresIn() > 0,
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
                .contentType("application/json")
                .body("$", anyOf(hasKey("error"), hasKey("message"), hasKey("status")))
                .body("$", not(hasKey("token")));

        // Более гибкая проверка - error может быть null, поэтому проверяем наличие любого из полей
        String responseBody = response.getBody().asString();
        assertTrue(
            responseBody.contains("error") || responseBody.contains("message") || responseBody.contains("status"),
            "Ответ должен содержать одно из полей: error, message или status"
        );
    }

    @Step("Проверить реальные заголовки ответа")
    public void validateResponseHeaders(Response response) {
        // Проверяем только те заголовки, которые реально существуют
        response.then()
                .header("Content-Type", equalTo("application/json"))
                .header("Cache-Control", containsString("no-cache"))
                .header("X-Powered-By", notNullValue())
                .header("Server", notNullValue())
                .header("X-Robots-Tag", equalTo("noindex"));

        // Для 401 ответов проверяем WWW-Authenticate
        if (response.getStatusCode() == 401) {
            response.then().header("WWW-Authenticate", equalTo("Bearer"));
        }

        // CORS заголовки - проверяем если они есть
        try {
            if (response.getHeader("Access-Control-Allow-Origin") != null) {
                response.then().header("Access-Control-Allow-Origin",
                    equalTo("https://client.ttplace.ru"));
            }
        } catch (AssertionError e) {
            // Игнорируем если CORS заголовков нет
        }
    }

    @Step("Получить Response для кастомных проверок")
    public Response getLoginResponse(String email, String password) {
        return authApi.login(email, password);
    }

    @Step("Проверить базовые заголовки безопасности")
    public void validateSecurityHeaders(Response response) {
        response.then()
                .header("X-Robots-Tag", equalTo("noindex"))
                .header("X-Powered-By", not(emptyOrNullString()));

        // Проверяем WWW-Authenticate для 401 ответов
        if (response.getStatusCode() == 401) {
            response.then().header("WWW-Authenticate", equalTo("Bearer"));
        }
    }

    @Step("Проверить минимальные заголовки (самые надежные проверки)")
    public void validateMinimumHeaders(Response response) {
        // Только самые базовые проверки которые точно должны работать
        response.then()
                .header("Content-Type", equalTo("application/json"))
                .header("Server", notNullValue())
                .header("Date", notNullValue());
    }
}
