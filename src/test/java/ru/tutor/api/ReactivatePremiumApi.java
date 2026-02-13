package ru.tutor.api;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.tutor.base.BaseApi;

import java.util.Map;

import static com.codeborne.selenide.Selenide.executeJavaScript;
import static io.restassured.RestAssured.given;

/**
 * Ручки для работы восстановления Премиума из письма внутренней почты / по e-mail рассылке
 */

public class ReactivatePremiumApi extends BaseApi {

    public ReactivatePremiumApi(String baseUrl) {
        super(baseUrl);
    }

    // Реактивация заказа по внутренней почте

    @Step("POST /user/orders/reactivate-premium - Реактивация пользователя Лайт по внутренней почте")
    public Response reactivatePremium(String authToken) {

        String aff_rid = executeJavaScript (
                "return localStorage.getItem('aff_rid') || 'default_aff_rid';"
        );

        return given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .body("{\"aff_rid\":\"" + aff_rid + "\"}")
                .log().all()
                .when()
                .post(baseUrl + "/user/orders/reactivate-premium")
                .then()
                .statusCode(200)
                .log().all()
                .extract().response();
    }

    @Step("POST /user/orders/reactivate-premium-by-email - Восстановление премиума по ссылке из email-рассылки")
    public Response reactivatePremiumByEmail(String authToken) {

        String aff_rid = executeJavaScript (
                "return localStorage.getItem('aff_rid') || 'default_aff_rid';"
        );

        return given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .body("{\"aff_rid\":\"" + aff_rid + "\"}")
                .log().all()
                .when()
                .post(baseUrl + "/user/orders/reactivate-premium-by-email")
                .then()
                .statusCode(200)
                .log().all()
                .extract().response();
    }

    @Step("POST /user/letter - письмо внутренней почты Premium")
    public Response reactivatePremiumLetter(String authToken) {
        Map<String,Object> letterData = Map.of(
                "category", "reactivate",
                "dateOffset", 0,
                "body", "Активируй бесплатный доступ ко всем курсам платформы на 30 дней",
                "header", "У нас есть подарок для тебя!",
                "subHeader", "Убедись в этом собственноручно!"
        ) ;


        return given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .body(letterData)
                .log().all()
                .when()
                .post(baseUrl + "/user/letter")
                .then()
                .statusCode(201)
                .log().all()
                .extract().response();
    }

//    public OrderResponse toggleSubscriptionAndGetResponse(String authToken) {
//        Response response = toggleSubscription(authToken);
//
//        return response
//                .then()
//                .statusCode(200)
//                .extract()
//                .as(OrderResponse.class);
//    }

//    public Response toggleSubscriptionWithInvalidToken() {
//        return given()
//                .header("Authorization", "Bearer invalid_token_123")
//                .contentType(ContentType.JSON)
//                .when()
//                .post()
//    }
}