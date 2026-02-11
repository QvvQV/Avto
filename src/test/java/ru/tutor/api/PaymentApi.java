package ru.tutor.api;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.tutor.base.BaseApi;
import ru.tutor.model.order.OrderRequest;
//import ru.tutor.model.order.PaymentRequest;
//import ru.tutor.steps.PaymentSteps;

import static io.restassured.RestAssured.given;

public class PaymentApi extends BaseApi {

    public PaymentApi (String baseUrl) {
        super(baseUrl);
    }
//
//    @Step("POST /order - создание платежа в CloudPayments")
//    public Response createOrder(OrderRequest paymentRequest, String authToken) {
//        return given()
//                .filter(new AllureRestAssured())
//                .contentType(ContentType.JSON)
//                .header("Authorization", "Bearer " + authToken)
//                .body(paymentRequest)
//                .log().all()
//                .when()
//                .post(baseUrl + "/order")
//                .then()
//                .log().all()
//                .extract()
//                .response();
//    }

    @Step("POST /order - создание платежа без авторизации")
    public Response createOrderWithoutAuth(OrderRequest orderRequest) {
        return given()
                .filter(new AllureRestAssured())
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
