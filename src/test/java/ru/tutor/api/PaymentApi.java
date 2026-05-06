package ru.tutor.api;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.tutor.base.BaseApi;
import ru.tutor.model.order.OrderRequest;

import static io.restassured.RestAssured.given;

public class PaymentApi extends BaseApi {

    public PaymentApi (String baseUrl) {
        super(baseUrl);
    }

    @Step("POST /order - создание платежа без авторизации")
    public Response createOrderWithoutAuth(OrderRequest orderRequest) {
        return given(baseSpec)
                .contentType(ContentType.JSON)
                .body(orderRequest)
                .log().all()
                .when()
                .post(baseUrl + "/order")
                .then()
                .log().all()
                .extract().response();
    }
}
