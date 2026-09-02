package ru.tutor.api;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.tutor.base.BaseApi;
import ru.tutor.config.TestConfig;
import ru.tutor.constants.AddressPage;
import ru.tutor.constants.ApiEndpoints;
import ru.tutor.utils.AllureAttachments;

import java.util.Map;

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


    @Step("GET /available-payment-systems - доступные платёжные системы")
    public Response getAvailablePaymentSystems() {

        return given(baseSpec)
                .queryParam("aff_rid", TestConfig.getAffRid())
                .log().all()
                .when()
                .get(ApiEndpoints.AVAILABLE_PAYMENT_SYSTEMS)
                .then()
                .log().all()
                .extract()
                .response();
    }

    @Step("GET /domain - информация по домену cardio")
    public Response getDomain() {

        return given(baseSpec)
                .header("Referer", AddressPage.CARDIO_DOMAIN_URL_AFF_RID)
                .queryParam("aff_rid", TestConfig.getAffRid())
                .log().all()
                .when()
                .get(AddressPage.CARDIO_DOMAIN_URL)
                .then()
                .log().all()
                .extract()
                .response();
    }
}
