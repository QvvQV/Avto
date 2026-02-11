package ru.tutor.api;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.tutor.base.BaseApi;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class ClubApi extends BaseApi {

    public ClubApi(String baseUrl) {
        super(baseUrl);
    }

    /**
     * GET /user/club/statistics - получение статистики клуба
     */
    public Response getClubStatistics(String authToken) {
        return given()
                .filter(new AllureRestAssured())
                .header("Authorization", "Bearer " + authToken)
                .header("Accept", "application/json, text/plain, */*")
                .header("Origin", "https://client.ttplace.ru")
                .header("Referer", "https://client.ttplace.ru/")
                .log().all()
                .when()
                .get("user/club/statistics")
                .then()
                .log().all()
                .extract().response();
    }

    /**
     * GET /user/club/members - получение списка участников клуба (если есть такой эндпоинт)
     */
    public Response getClubMembers(String authToken) {
        return given()
                .filter(new AllureRestAssured())
                .header("Authorization", "Bearer " + authToken)
                .header("Accept", "application/json, text/plain, */*")
                .header("Origin", "https://client.ttplace.ru")
                .header("Referer", "https://client.ttplace.ru/")
                .log().all()
                .when()
                .get("user/club/member")
                .then()
                .log().all()
                .extract().response();
    }

    /**
     * POST /user/club/request-withdrawal - запрос на вывод средств
     */
    public Response requestWithdrawal(String authToken, Map<String, Object> payload) {
        return given()
                .filter(new AllureRestAssured())
                .header("Authorization", "Bearer " + authToken)
                .header("Accept", "application/json, text/plain, */*")
                .header("Origin", "https://client.ttplace.ru")
                .header("Referer", "https://client.ttplace.ru/")
                .header("Referer", "https://client.ttplace.ru/")
                .log().all()
                .when()
                .post(baseUrl + "user/club/request-withdrawal")
                .then()
                .log().all()
                .extract().response();
    }
}
