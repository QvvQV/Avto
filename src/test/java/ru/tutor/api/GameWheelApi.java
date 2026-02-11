package ru.tutor.api;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

import ru.tutor.base.BaseApi;

/**
 * Ручка для колеса фортуны
 */

public class GameWheelApi extends BaseApi {

    public GameWheelApi(String baseUrl) {
        super(baseUrl);
    }

    @Step("GET /user/game-wheel/rewards - получение списка наград игрового колеса")
    public Response getGameWheelRewards(String authToken) {
        return given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .log().all()
                .when()
                .get(baseUrl + "/user/game-wheel/rewards")
                .then()
                .log().all()
                .extract().response();
    }

    @Step("POST /user/game-wheel/start-game - запуск игры на колесе")
    public Response startGame(String authToken) {
        return given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .log().all()
                .when()
                .post(baseUrl + "/user/game-wheel/start-game")
                .then()
                .log().all()
                .extract().response();
    }

    @Step("GET /user/reward/list - получение списка наград пользователя")
    public Response getUserRewards(String authToken) {
        return given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .log().all()
                .when()
                .get(baseUrl + "/user/reward/list")
                .then()
                .log().all()
                .extract().response();
    }

    @Step("POST /user/reward/opеn/{id} - удаление подарка из списка призов")
    public Response openReward(String authToken, Long rewardId) {
        return given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .log().all()
                .when()
                .post(baseUrl + "/user/reward/open/{id}", rewardId)
                .then()
                .log().all()
                .extract().response();
    }

    @Step("GET /user/reward/history - получение истории забранных наград пользователя")
    public Response getUserRewardHistory(String authToken) {
        return given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .log().all()
                .when()
                .get(baseUrl + "/user/reward/history")
                .then()
                .log().all()
                .extract().response();
    }
}
