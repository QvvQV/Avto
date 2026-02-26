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

//    @Step("GET /user/game-wheel/rewards - получение списка наград игрового колеса")
//    public Response getGameWheelRewards(String authToken) {
//        return given()
//                .filter(new AllureRestAssured())
//                .contentType(ContentType.JSON)
//                .header("Authorization", "Bearer " + authToken)
//                .log().all()
//                .when()
//                .get(baseUrl + "/user/game-wheel/rewards")
//                .then()
//                .log().all()
//                .extract().response();
//    }

    @Step("POST /user/stories/screen - как часто видит пользователь сторис")
    public Response storyScreen(String authToken) {
        return given()
                .filter(new AllureRestAssured())
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

//    @Step("POST /user/reward/open/{id} - удаление подарка из списка призов")
//    public Response openReward(String authToken, Long rewardId) {
//        return given()
//                .filter(new AllureRestAssured())
//                .contentType(ContentType.JSON)
//                .header("Authorization", "Bearer " + authToken)
//                .log().all()
//                .when()
//                .post(baseUrl + "/user/reward/open/{id}", rewardId)
//                .then()
//                .log().all()
//                .extract().response();
//    }
}
