package ru.tutor.api;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.tutor.base.BaseApi;

import static io.restassured.RestAssured.given;

public class UserApi extends BaseApi {

    public UserApi(String baseUrl) {
        super(baseUrl);
    }

    /**
     * POST /user/login - вход пользователя
     */

    public Response login(String email, String password) {
        String body = String.format(
                "{\"email\":\"%s\",\"password\":\"%s\"}",
                email, password
        );

        return given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .accept("application/json")
                .header("Origin", "https://client.ttplace.ru")
                .header("Referer", "https://client.ttplace.ru/")
                .body(body)
                .log().all()
                .when()
                .post(baseUrl + "/user/login")
                .then()
                .log().all()
                .extract().
                response();
    }

    /**
     * GET /user/profile - получение профиля пользователя
     */
    public Response getProfile(String authToken) {
        return given()
                .filter(new AllureRestAssured())
                .header("Authorization", "Bearer " + authToken)
                .header("Accept", "application/json, text/plain, */*")
                .header("Origin", "https://client.ttplace.ru")
                .header("Referer", "https://client.ttplace.ru/")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .log().all()
                .when()
                .get("/user/profile")
                .then()
                .log().all()
                .extract().response();
    }

    /**
     * POST /user/logout - выход пользователя из системы
     *
     * @param authToken Bearer токен пользователя
     * @return Response с результатом логаута
     */

    public Response logout(String authToken) {
        return given()
                .filter(new AllureRestAssured())
                .header("Authorization", "Bearer " + authToken)
                .header("Accept", "application/json, text/plain, */*")
                .header("Origin", "https://client.ttplace.ru")
                .header("Referer", "https://client.ttplace.ru/")
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .post("/user/logout")
                .then()
                .log().all()
                .extract().response();
    }

    /**
     * GET /user/game-stats - получение игровой статистики
     */

    public Response getGameStats(String authToken) {
        return given()
                .filter(new AllureRestAssured())
                .header("Authorization", "Bearer " + authToken)
                .header("Accept", "application/json, text/plain, */*")
                .header("Origin", "https://client.ttplace.ru")
                .header("Referer", "https://client.ttplace.ru/")
                .log().all()
                .when()
                .get("user/game-stats")
                .then()
                .log().all()
                .extract().response();
    }
}
