package ru.tutor.api;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

import ru.tutor.base.BaseApi;

public class ClubSzDetailsApi extends BaseApi {

    public ClubSzDetailsApi(String baseUrl) {
        super(baseUrl);
    }

    /**
     * POST /user/club/sz-details - сохранение деталей самозанятого
     */
    public Response saveSzDetails(String authToken, Map<String, Object> payload) {
        return given()
                .filter(new AllureRestAssured())
                .header("Authorization", "Bearer " + authToken)
                .header("Accept", "application/json, text/plain, */*")
                .header("Origin", "https://client.ttplace.ru")
                .header("Referer", "https://client.ttplace.ru/")
                .contentType(ContentType.JSON)
                .body(payload)
                .log().all()
                .when()
                .post(baseUrl + "/user/club/sz-details")
                .then()
                .log().all()
                .extract().response();
    }

    /**
     * POST /user/club/ip-details - сохранение деталей ИП
     */
    public Response saveIpDetails(String authToken, Map<String, Object> payload) {
        return given()
                .filter(new AllureRestAssured())
                .header("Authorization", "Bearer " + authToken)
                .header("Accept", "application/json, text/plain, */*")
                .header("Origin", "https://client.ttplace.ru")
                .header("Referer", "https://client.ttplace.ru/")
                .contentType(ContentType.JSON)
                .body(payload)
                .log().all()
                .when()
                .post(baseUrl + "/user/club/ip-details")
                .then()
                .log().all()
                .extract().response();
    }

    /**
     * GET /user/club/ip-details - получение деталей ИП
     */
    public Response getIpDetails(String authToken) {
        return given()
                .filter(new AllureRestAssured())
                .header("Authorization", "Bearer " + authToken)
                .header("Accept", "application/json, text/plain, */*")
                .header("Origin", "https://client.ttplace.ru")
                .header("Referer", "https://client.ttplace.ru/")
                .log().all()
                .when()
                .get(baseUrl + "/user/club/ip-details")
                .then()
                .log().all()
                .extract().response();
    }

    /**
     * DELETE /user/club/details - получение деталей
     */
    public Response deleteClubDetails(String authToken) {
        return given()
                .filter(new AllureRestAssured())
                .header("Authorization", "Bearer " + authToken)
                .header("Accept", "application/json, text/plain, */*")
                .header("Origin", "https://client.ttplace.ru")
                .header("Referer", "https://client.ttplace.ru/")
                .log().all()
                .when()
                .delete(baseUrl + "/user/club/details")
                .then()
                .log().all()
                .extract().response();
    }
}
