package ru.tutor.api;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.tutor.base.BaseApi;
import ru.tutor.constants.ApiEndpoints;

import java.util.HashMap;
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
    public Response reactivatePremium(String authToken, String affRid) {

        String affRidValue = affRid != null ? affRid : "null";
        Allure.addAttachment("aff_rid", "Значение: " +  affRidValue);

        Object requestBody;

        if (affRid != null && !affRid.isEmpty()) {
            Map<String, String> bodyMap = new HashMap<>();
            bodyMap.put("aff_rid", affRid);
            requestBody = bodyMap;

            Allure.addAttachment("Тело запроса", String.format("Содержит aff_rid: %s", affRid));
        } else {
            requestBody = "[]";
            Allure.addAttachment("Тело запроса",
                    "Пустой массив (aff_rid отсутсвует)");
        }

        return postWithToken(
                ApiEndpoints.ORDERS_REACTIVATE_PREMIUM,
                authToken,
                requestBody
        );
    }

    @Step("POST /user/orders/reactivate-premium-by-email - Восстановление премиума по ссылке из email-рассылки")
    public Response reactivatePremiumByEmail(String authToken) {

        String aff_rid = executeJavaScript (
                "return localStorage.getItem('aff_rid') || 'default_aff_rid';"
        );

        return given(baseSpec)
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .body("{\"aff_rid\":\"" + aff_rid + "\"}")
                .log().all()
                .when()
                .post(baseUrl + ApiEndpoints.ORDERS_REACTIVATE_PREMIUM_BY_EMAIL)
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


        return given(baseSpec)
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
}