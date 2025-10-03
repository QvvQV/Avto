package ru.tutor.api;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class ApiClient {
    protected final RequestSpecification baseRequestSpec;

    public ApiClient(String baseUrl) {
        this.baseRequestSpec = new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setContentType(ContentType.JSON)
                .addHeader("Accept", "application/json, text/plain, */*")
                .addHeader("Origin", "https://client.ttplace.ru")
                .addHeader("Referer", "https://client.ttplace.ru/")
                .addFilter(new AllureRestAssured())
                .build();
    }

    public ApiClient(String baseUrl, String token) {
        this.baseRequestSpec = new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setContentType(ContentType.JSON)
                .addHeader("Accept", "application/json, text/plain, */*")
                .addHeader("Origin", "https://client.ttplace.ru")
                .addHeader("Referer", "https://client.ttplace.ru/")
                .addHeader("Authorization", "Bearer " + token)
                .addFilter(new AllureRestAssured())
                .build();
    }
}
