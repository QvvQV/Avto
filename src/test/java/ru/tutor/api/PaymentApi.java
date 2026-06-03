package ru.tutor.api;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.tutor.base.BaseApi;
import ru.tutor.constants.ApiEndpoints;

import static io.restassured.RestAssured.given;

public class PaymentApi extends BaseApi {

    public PaymentApi (String baseUrl) {
        super(baseUrl);
    }

    @Step("POST /order - создание заказа (multipart) без авторизации")
    public Response createOrderWithoutAuthMultipart(String email, String products) {
        return given(baseSpec)
                .contentType("multipart/form-data")
                .header("x-requested-with", "XMLHttpRequest")
                .config(RestAssured.config()
                .encoderConfig(EncoderConfig.encoderConfig()
                        .encodeContentTypeAs("multipart/form-data", ContentType.TEXT)))
                .multiPart("order[email]", email)
                .multiPart("order[products][]", products)
                .log().all()
                .when()
                .post(ApiEndpoints.ORDER)
                .then()
                .log().all()
                .extract().response();
    }
}
