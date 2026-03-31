package ru.tutor.api;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;
import ru.tutor.base.BaseApi;
import ru.tutor.config.TestConfig;

import static io.restassured.RestAssured.given;

public class ProductsApi extends BaseApi {

    public ProductsApi(String baseUrl) {
        super(baseUrl);
    }

    /**
     * GET /user/products - получение списка продуктов пользователя
     */
    public Response getUserProducts(String authToken) {
        return getWithToken("user/products", authToken);
    }


    /**
     * GET /user/products/all - получение всех доступных продуктов
     */
    public Response getAllProducts(String authToken) {
        return getWithToken("user/products/all", authToken);
    }

    /**
     * GET /user/products/{productId} - получение конкретного продукта
     */

    public Response getProductById(String authToken, String productId) {
        return given()
                .filter(new AllureRestAssured())
                .header("Authorization", "Bearer " + authToken)
                .header("Accept", "application/json, text/plain, */*")
                .header("Origin", TestConfig.getCurrentClientDomain())
                .header("Referer", TestConfig.getCurrentClientDomain())
                .log().all()
                .when()
                .get("/user/products/" + productId)
                .then()
                .log().all()
                .extract().response();
    }
}
