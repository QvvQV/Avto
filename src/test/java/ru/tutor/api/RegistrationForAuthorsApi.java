package ru.tutor.api;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.tutor.base.BaseApi;

import static io.restassured.RestAssured.given;

/**
 * Ручка для создания заказа Автора задача ТР-5735
 */

public class RegistrationForAuthorsApi extends BaseApi {

    public RegistrationForAuthorsApi(String baseUrl) {
        super(baseUrl);
    }

    @Step("POST https://api.dev.tutorplace.ru/order")
    public Response registrationAuthor() {

        Boolean isAuthorRegister = true;
        String email = "tbgesta22322@as.qq";
        String name = "bulka";

        return given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .body("{\"isAuthorRegister\":\"" + isAuthorRegister + "\"email\":\"" + email + "\"name\":\"" + name + "\"}")
                .log().all()
                .when()
                .post("https://api.dev.tutorplace.ru/order")
                .then()
                .statusCode(200)
                .log().all()
                .extract().response();

     }
}