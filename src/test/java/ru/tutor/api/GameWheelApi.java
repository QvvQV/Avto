package ru.tutor.api;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

import ru.tutor.base.BaseApi;
import ru.tutor.constants.ApiEndpoints;

/**
 * Ручка для колеса фортуны
 */

public class GameWheelApi extends BaseApi {

    public GameWheelApi(String baseUrl) {
        super(baseUrl);
    }

    @Step("GET /user/game-wheel/rewards - получение списка наград игрового колеса")
    public Response getGameWheelRewards(String authToken) {
        return getWithToken(ApiEndpoints.GAME_WHEEL_REWARDS, authToken);
    }

    @Step("POST /user/game-wheel/start-game - запуск игры на колесе")
    public Response startGame(String authToken) {
        return postWithToken(ApiEndpoints.GAME_WHEEL_START, authToken, "[]");
    }

    @Step("GET /user/reward/list - получение списка наград пользователя")
    public Response getUserRewards(String authToken) {
        return getWithToken(ApiEndpoints.GAME_REWARD_LIST, authToken);
    }

    @Step("POST /user/reward/open/{id} - удаление подарка из списка призов")
    public Response openReward(String authToken, Long rewardId) {
        return postWithToken(ApiEndpoints.GAME_REWARD_OPEN_ID, authToken, "[]", rewardId);
    }

    @Step("GET /user/reward/history - получение истории забранных наград пользователя")
    public Response getUserRewardHistory(String authToken) {
        return getWithToken(ApiEndpoints.GAME_REWARD_HISTORY, authToken);
    }
}