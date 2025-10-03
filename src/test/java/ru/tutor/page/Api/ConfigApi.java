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
                .when()
                .get("/api/config")
                .then()
                .extract()
                .response();
    }

    public Response createConfig(String body) {
        return given()
                .filter(new AllureRestAssured())
                .header("Authorization", "Basic " + basicAuth)
                .body(body)
                .when()
                .post("/api/config")
                .then()
                .extract()
                .response();
    }

    public Response updateConfig(String body) {
        return given()
                .filter(new AllureRestAssured())
                .header("Authorization", "Basic " + basicAuth)
                .body(body)
                .when()
                .put("/api/config")
                .then()
                .extract()
                .response();
    }

    public Response deleteConfig() {
        return given()
                .filter(new AllureRestAssured())
                .header("Authorization", "Basic " + basicAuth)
                .when()
                .delete("/api/config")
                .then()
                .extract()
                .response();
    }
}
