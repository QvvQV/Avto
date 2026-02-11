package ru.tutor.base;

import io.qameta.allure.restassured.AllureRestAssured;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import ru.tutor.utils.AllureAttachments;

import static io.restassured.RestAssured.given;

/**
 * Базовые ручки для API ручек
 */
public abstract class BaseApi {
    protected final String baseUrl;

    protected BaseApi(String baseUrl) {
        this.baseUrl = baseUrl;
        RestAssured.baseURI = baseUrl;

        // Глобальная настройка RestAssured
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Step("POST запрос к {endpoint}")
    protected Response post(String endpoint, String body) {
        return given()
                .filter(new AllureRestAssured())
                .header("Content-Type", "application/json")
                .header("Accept", "application/json, text/plain, */*")
                .header("Origin", "https://client.ttplace.ru")
                .header("Referer", "https://client.ttplace.ru/")
//                .header("Origin", "https://client.tutorplace.ru/")
//                .header("Referer", "https://client.tutorplace.ru/")
                .body(body)
                .log().all() // Логирование запроса
                .when()
                .post(endpoint)
                .then()
                .log().ifError() // Логирование при ошибке
                .extract()
                .response();
    }

    @Step("GET запрос к {endpoint}")
    protected Response get(String endpoint) {
        return given()
                .filter(new AllureRestAssured())
                .header("Accept", "application/json, text/plain, */*")
                .log().all()
                .when()
                .get(endpoint)
                .then()
                .log().ifError()
                .extract()
                .response();
    }

    // Дополнительные методы для разных типов контента
    @Step("POST запрос с form-data к {endpoint}")
    protected Response postFormData(String endpoint, String body) {
        return given()
                .filter(new AllureRestAssured())
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept", "application/json")
                .body(body)
                .log().all()
                .when()
                .post(endpoint)
                .then()
                .log().ifError()
                .extract()
                .response();
    }
}