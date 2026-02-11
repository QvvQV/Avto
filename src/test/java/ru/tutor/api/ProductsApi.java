package ru.tutor.api;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;
import ru.tutor.base.BaseApi;

import static io.restassured.RestAssured.given;

public class ProductsApi extends BaseApi {

    public ProductsApi(String baseUrl) {
        super(baseUrl);
    }

    /**
     * GET /user/products - получение списка продуктов пользователя
     */
    public Response getUserProducts(String authToken) {
        return given()
                .filter(new AllureRestAssured())
                .header("Authorization", "Bearer " + authToken)
                .header("Accept", "application/json, text/plain, */*")
                .header("Origin", "https://client.ttplace.ru")
                .header("Referer", "https://client.ttplace.ru/")
                .log().all()
                .when()
                .get("/user/products")
                .then()
                .log().all()
                .extract().response();
    }

    /**
     * GET /user/products/{productId} - получение конкретного продукта
     */

    public Response getProductById(String authToken, String productId) {
        return given()
                .filter(new AllureRestAssured())
                .header("Authorization", "Bearer " + authToken)
                .header("Accept", "application/json, text/plain, */*")
                .header("Origin", "https://client.ttplace.ru")
                .header("Referer", "https://client.ttplace.ru/")
                .log().all()
                .when()
                .get("/user/products/" + productId)
                .then()
                .log().all()
                .extract().response();
    }
}
