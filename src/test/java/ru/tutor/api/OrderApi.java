package ru.tutor.api;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.tutor.base.BaseApi;

import static io.restassured.RestAssured.given;

/**
 * Ручка для отписки до Лайта
 */

public class OrderApi extends BaseApi {

    public OrderApi(String baseUrl) {
        super(baseUrl);
    }

    // Лайт подписка

    @Step("POST /user/orders/toggle_subscription/{orderId} - Отписка пользователя до Лайт")
    public Response toggleOrderSubscription(String authToken, Integer orderId) {
        return given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .log().all()
                .when()
                .post(baseUrl + "/user/orders/toggle_subscription/" + orderId)
                .then()
                .log().all()
                .extract().response();
    }
}