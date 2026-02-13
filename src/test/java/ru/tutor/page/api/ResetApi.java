package ru.tutor.page.api;

import io.restassured.response.Response;
import ru.tutor.base.BaseApi;

public class ResetApi extends BaseApi {

    public ResetApi(String baseUrl) {
        super(baseUrl);
    }

    public Response resetPassword(String email) {
        String payload = String.format("{\"email\": \"%s\"}", email);
        return post("/reset", payload);
    }
}
