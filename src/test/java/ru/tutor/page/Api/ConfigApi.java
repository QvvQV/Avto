package ru.tutor.page.Api;

import io.restassured.response.Response;
import io.qameta.allure.restassured.AllureRestAssured;
import ru.tutor.base.BaseApi;

import static io.restassured.RestAssured.given;

public class ConfigApi extends BaseApi {
    private final String basicAuth;

    public ConfigApi(String baseUrl, String basicAuth) {
        super(baseUrl);
        this.basicAuth = basicAuth;
    }

    public Response getConfig() {
        return given()
                .filter(new AllureRestAssured())
                .header("Authorization", "Basic " + basicAuth)
                .header("Accept", "application/json")
                .log().all()
                .when()
                .get("/api/config")
                .then()
                .log().ifError()
                .extract()
                .response();
    }

    public Response createConfig(String body) {
        return given()
                .filter(new AllureRestAssured())
                .header("Authorization", "Basic " + basicAuth)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(body)
                .log().all()
                .when()
                .post("/api/config")
                .then()
                .log().ifError()
                .extract()
                .response();
    }

    public Response updateConfig(String body) {
        return given()
                .filter(new AllureRestAssured())
                .header("Authorization", "Basic " + basicAuth)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(body)
                .log().all()
                .when()
                .put("/api/config")
                .then()
                .log().ifError()
                .extract()
                .response();
    }

    public Response deleteConfig() {
        return given()
                .filter(new AllureRestAssured())
                .header("Authorization", "Basic " + basicAuth)
                .header("Accept", "application/json")
                .log().all()
                .when()
                .delete("/api/config")
                .then()
                .log().ifError()
                .extract()
                .response();
    }
// Метод без авторизации для тестирования безопасности
    public Response getConfigWithAuth() {
        return given()
                .filter(new AllureRestAssured())
                .header("Accept", "application/json")
                .log().all()
                .when()
                .get("/api/config")
                .then()
                .log().ifError()
                .extract()
                .response();
    }

}
