package ru.tutor.api;

import io.restassured.response.Response;
import ru.tutor.base.BaseApi;
import ru.tutor.constants.ApiEndpoints;

public class ResetApi extends BaseApi {

    public ResetApi(String baseUrl) {
        super(baseUrl);
    }

    public Response resetPassword(String email) {
        String payload = String.format("{\"email\": \"%s\"}", email);
        return post(ApiEndpoints.RESET_PASSWORD, payload);
    }
}
