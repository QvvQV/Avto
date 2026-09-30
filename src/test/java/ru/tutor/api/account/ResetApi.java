package ru.tutor.api.account;

import io.restassured.response.Response;
import ru.tutor.base.BaseApi;
import ru.tutor.constants.EndpointsAccount;

public class ResetApi extends BaseApi {

    public ResetApi(String baseUrl) {
        super(baseUrl);
    }

    public Response resetPassword(String email) {
        String payload = String.format("{\"email\": \"%s\"}", email);
        return post(EndpointsAccount.RESET_PASSWORD, payload);
    }
}
