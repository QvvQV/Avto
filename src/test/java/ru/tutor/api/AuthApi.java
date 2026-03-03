package ru.tutor.api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.base.BaseApi;
import ru.tutor.constants.ApiEndpoints;

import java.util.Map;

public class AuthApi extends BaseApi {

    public AuthApi(String baseUrl) {
        super(baseUrl);
    }

    @Step
    public Response login(String email, String password) {
        return post(
                ApiEndpoints.LOGIN,
                Map.of("email", email, "password", password)
        );
    }

    @Step("API: Логин с email: {email}")
    public String loginAndGetToken(String email, String password) {
        Response response = login(email, password);

        response.then().statusCode(200);
        return response.jsonPath().getString("token");
    }
}