package ru.tutor.page.api;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.tutor.base.BaseApi;

import java.util.List;

import static io.restassured.RestAssured.given;

public class OrderApi extends BaseApi {

    public OrderApi(String baseUrl) {
        super(baseUrl);
    }

    /**
     * Создать заказ
     */
    public Response createOrder(String email, List<String> products) {
        return given()
                .redirects().follow(true)
                .config(io.restassured.RestAssured.config()
                        .encoderConfig(EncoderConfig.encoderConfig()
                                .encodeContentTypeAs("multipart/form-data", ContentType.TEXT)))
                .filter(new AllureRestAssured())
                .contentType("multipart/form-data")
                .multiPart("order[email]", email)
                .multiPart("order[products][]", String.join(",", products))
                .when()
                .post(baseUrl + "/order")
                .then()
                .extract()
                .response();
    }

    /**
     * Создать intent для оплаты
     */
    public Response createIntent(String orderId) {
        return given()
                .redirects().follow(true)
                .filter(new AllureRestAssured())
                .contentType("application/json")
                .body("{\"orderId\": \"" + orderId + "\"}")
                .when()
                .post(baseUrl + "/order/intent")
                .then()
                .extract()
                .response();
    }

    /**
     * Проверить BIN карты
     */
    public Response checkBin(String bin) {
        return given()
                .redirects().follow(true)
                .filter(new AllureRestAssured())
                .contentType("application/json")
                .body("{\"bin\": \"" + bin + "\"}")
                .when()
                .post(baseUrl + "/order/bininfo")
                .then()
                .extract()
                .response();
    }

    /**
     * Оплатить заказ
     */
    public Response payOrder(String orderId, String cardNumber, String exp, String cvc) {
        String body = String.format(
                "{\"orderId\":\"%s\",\"cardNumber\":\"%s\",\"exp\":\"%s\",\"cvc\":\"%s\"}",
                orderId, cardNumber, exp, cvc
        );

        return given()
                .redirects().follow(true)
                .filter(new AllureRestAssured())
                .contentType("application/json")
                .body(body)
                .when()
                .post(baseUrl + "/order/pay")
                .then()
                .extract()
                .response();
    }
}
