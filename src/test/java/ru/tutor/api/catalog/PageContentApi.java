package ru.tutor.api.catalog;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;
import ru.tutor.base.BaseApi;

import static io.restassured.RestAssured.given;

public class PageContentApi extends BaseApi {

    public PageContentApi(String baseUrl) {
        super(baseUrl);
    }

    /**
     * Get страница
     */
    public Response getPage(String endpoint) {
        return given()
                .filter(new AllureRestAssured())
                .header("Authorization", "Basic ")
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
                .header("Authorization", "Basic ")
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
                .header("Authorization", "Basic ")
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
                .header("Authorization", "Basic ")
                .when()
                .delete(baseUrl + endpoint)
                .then()
                .extract()
                .response();
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
