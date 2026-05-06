package ru.tutor.api;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.tutor.base.BaseApi;

import static io.restassured.RestAssured.given;

/**
 * Ручка для колеса фортуны
 */

public class StoriesApi extends BaseApi {

    public StoriesApi(String baseUrl) {
        super(baseUrl);
    }

    @Step("POST /user/stories/screen - как часто видит пользователь сторис")
    public Response storyScreen(String authToken) {
        return given(baseSpec)
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .log().all()
                .when()
                .post(baseUrl + "/user/stories/screen")
//                .get(baseUrl + "/user/stories")
                .then()
                .log().all()
                .extract().response();
    }
}
