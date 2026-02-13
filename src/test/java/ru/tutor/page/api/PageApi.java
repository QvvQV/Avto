package ru.tutor.page.api;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;
import ru.tutor.base.BaseApi;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static io.restassured.RestAssured.given;

public class PageApi extends BaseApi {
    private final String encodedAuth;
    private final String authHeader;

    public PageApi(String baseUrl, String username, String password) {
        super(baseUrl);
        String credentials = username + ":" + password;
        this.encodedAuth = Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        this.authHeader = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }


    /**
     * Get страница
     */
    public Response getPage(String endpoint) {
        return given()
                .filter(new AllureRestAssured())
                .header("Authorization", "Basic " + encodedAuth)
                .when()
                .get(baseUrl + endpoint)
                .then()
                .extract()
                .response();
    }

    /**
     * Post страница
     */
    public Response createPage(String endpoint, Object body) {
        return given()
                .filter(new AllureRestAssured())
                .header("Authorization", "Basic " + encodedAuth)
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post(baseUrl + endpoint)
                .then()
                .extract()
                .response();
    }

    /**
     * Put страница
     */
    public Response updatePage(String endpoint, Object body) {
        return given()
                .filter(new AllureRestAssured())
                .header("Authorization", "Basic " + encodedAuth)
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .put(baseUrl + endpoint)
                .then()
                .extract()
                .response();
    }

    /**
     * Delete страница
     */
    public Response deletePage(String endpoint) {
        return given()
                .filter(new AllureRestAssured())
                .header("Authorization", "Basic " + encodedAuth)
                .when()
                .delete(baseUrl + endpoint)
                .then()
                .extract()
                .response();
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getAuthHeader() {
        return authHeader;
    }
}
