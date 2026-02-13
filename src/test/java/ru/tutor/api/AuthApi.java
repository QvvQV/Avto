package ru.tutor.api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.model.LoginRequest;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class AuthApi extends ApiClient {
    private static final String LOGIN_PATH = "/user/login";

    public AuthApi(String baseUrl) {
        super(baseUrl);
    }

    @Step("API: Логин с email: {email}")
    public Response login(String email, String password) {
        return given()
                .spec(baseRequestSpec)
                .body(Map.of(
                    "email", email,
                    "password", password
                ))
            .when()
                .post(LOGIN_PATH);
    }

    @Step("API: Логин с объектом запроса")
    public Response login(LoginRequest loginRequest) {
        return given()
                .spec(baseRequestSpec)
                .body(loginRequest)
            .when()
                .post(LOGIN_PATH);
    }
}
