package ru.tutor.base;

import io.qameta.allure.restassured.AllureRestAssured;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.response.Response;
import ru.tutor.config.TestConfig;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import ru.tutor.constants.ApiEndpoints;
import ru.tutor.model.RegistrationRequest;

import static io.restassured.RestAssured.given;

/**
 * Базовые ручки для API ручек
 */
public abstract class BaseApi {
    protected final String baseUrl;
    protected final RequestSpecification baseSpec;

    protected BaseApi(String baseUrl) {
        this.baseUrl = TestConfig.getCurrentApiUrl();
        this.baseSpec = new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .addFilter(new AllureRestAssured())
                .setConfig(RestAssured.config()
                        .logConfig(LogConfig.logConfig()
                                .enableLoggingOfRequestAndResponseIfValidationFails()))
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json, text/plain, */*")
                .addHeader("Origin", TestConfig.getCurrentClientDomain())
                .addHeader("Referer", TestConfig.getCurrentClientDomain())
                .build();


        // Глобальная настройка RestAssured
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Step("POST запрос к {endpoint}")
    protected Response postWithToken(String endpoint, String token, Object body, Object... pathParams) {
        String formattedEndpoint = String.format(endpoint, pathParams);

        return given(baseSpec)
//                .header("Origin", "https://client.tutorplace.ru/")
//                .header("Referer", "https://client.tutorplace.ru/")
                .header("Authorization", "Bearer " + token)
                .body(body)
                .log().all() // Логирование запроса
                .when()
                .post(formattedEndpoint)
                .then()
                .log().ifError() // Логирование при ошибке
                .extract()
                .response();
    }

    @Step("POST запрос к {endpoint}")
    protected Response post(String endpoint, Object body) {
        return given(baseSpec)
//                .header("Authorization", "Bearer " + token)
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
    protected Response getWithToken(String endpoint, String token) {
        return given(baseSpec)
                .header("Authorization", "Bearer " + token)
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
        return given(baseSpec)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(body)
                .log().all()
                .when()
                .post(endpoint)
                .then()
                .log().ifError()
                .extract()
                .response();
    }

    @Step("DELETE запрос к {endpoint}")
    protected Response deleteWithToken(String endpoint, String token) {
        return given(baseSpec)
                .header("Authorization", "Bearer " + token)
                .log().all()
                .when()
                .delete(endpoint)
                .then()
                .log().ifError()
                .extract()
                .response();
    }

    @Step("DELETE запрос к {endpoint}")
    protected Response deleteWithTokenObject(String endpoint, String token, Object body, Object... pathParams) {
        String formattedEndpoint = String.format(endpoint, pathParams);

        return given(baseSpec)
                .header("Authorization", "Bearer " + token)
                .log().all()
                .when()
                .delete(formattedEndpoint)
                .then()
                .log().ifError()
                .extract()
                .response();
    }

    @Step("PUT запрос к {endpoint}")
    protected Response putWithToken(String endpoint, String token) {
        return given(baseSpec)
                .header("Authorization", "Bearer " + token)
                .log().all()
                .when()
                .put(endpoint)
                .then()
                .log().ifError()
                .extract()
                .response();
    }

    @Step("PUT запрос к {endpoint}")
    protected Response putWithTokenObject(String endpoint, String token, Object body, Object... pathParams) {
        String formattedEndpoint = String.format(endpoint, pathParams);
        return given(baseSpec)
                .header("Authorization", "Bearer " + token)
                .body(body)
                .log().all()
                .when()
                .put(formattedEndpoint)
                .then()
                .log().ifError()
                .extract()
                .response();
    }
}