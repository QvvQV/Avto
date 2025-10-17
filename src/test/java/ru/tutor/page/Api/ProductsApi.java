package ru.tutor.page.Api;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;
import ru.tutor.base.BaseApi;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static io.restassured.RestAssured.given;

public class ProductsApi extends BaseApi {
//    private final String baseCatalogUrl = "https://ttplace.ru/catalog";
//    private final String baseProductUrl = "https://ttplace.ru/catalog/product";
    private final String authHeader;

    public ProductsApi(String baseUrl, String login, String password) {
        super(baseUrl);
        this.authHeader = "Basic " + Base64.getEncoder()
                .encodeToString((login + ":" + password).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Запрос страницы каталога с параметром page
     */
    public Response getCatalogPage(int page) {
        return given()
                .filter(new AllureRestAssured())
                .header("Authorization", authHeader)
                .header("Accept", "*/*")
                .when()
                .get(baseUrl + "/catalog?page=" + page)
                .then()
                .extract()
                .response();
    }

    /**
     * Запрос конкретного товара
     */
    public Response getProduct(String productCode) {
        return given()
                .filter(new AllureRestAssured())
                .header("Authorization", authHeader)
                .header("Accept", "*/*")
                .when()
                .get(baseUrl + "/catalog/product/" + productCode)
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
