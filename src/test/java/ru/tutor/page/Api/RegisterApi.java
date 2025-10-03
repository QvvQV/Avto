package ru.tutor.page.Api;

import io.restassured.response.Response;
import ru.tutor.base.BaseApi;

public class RegisterApi extends BaseApi {

    public RegisterApi(String baseUrl) {
        super(baseUrl);
    }

    public Response registerUser(String email, String name, String password, String phone, String telegram, boolean approvePolicy) {
        String payload = String.format(
                "{ \"email\": \"%s\", \"name\": \"%s\", \"password\": \"%s\", \"phone\": \"%s\", \"telegram\": \"%s\", \"approvePolicy\": %s }",
                email, name, password, phone, telegram, approvePolicy
        );

        return post("/register", payload);
    }

    public Response registerWithAllData(String email, String name, String password, String phone, String telegram){
        return registerUser(email, name, password, phone, telegram, true);
    }
}
