package ru.tutor.api.catalog;

import io.restassured.response.Response;
import ru.tutor.base.BaseApi;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static io.restassured.RestAssured.given;

public class CatalogApi extends BaseApi {
    private final String encodedAuth;
    private final String authHeader;

    public CatalogApi(String baseUrl, String username, String password) {
        super(baseUrl);
        String credentials = username + ":" + password;
        this.encodedAuth = Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        this.authHeader = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }

    public Response getCatalog(String endpoint) {
        return given(baseSpec)
                .header("Authorization", "Basic " + encodedAuth)
                .when()
                .get("/catalog/" + endpoint)
                .then()
                .extract()
                .response();
    }

    public Response createCatalogItem(String endpoint, String body) {
        return given(baseSpec)
                .header("Authorization", "Basic " + encodedAuth)
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/catalog/" + endpoint)
                .then()
                .extract()
                .response();
    }

    public Response updateCatalogItem(String endpoint, String body) {
        return given(baseSpec)
                .header("Authorization", "Basic " + encodedAuth)
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .put("/catalog/" + endpoint)
                .then()
                .extract()
                .response();
    }

    public Response deleteCatalogItem(String endpoint) {
        return given(baseSpec)
                .header("Authorization", "Basic " + encodedAuth)
                .when()
                .delete("/catalog/" + endpoint)
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
