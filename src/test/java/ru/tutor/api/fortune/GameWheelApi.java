package ru.tutor.api.fortune;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import ru.tutor.base.BaseApi;
import ru.tutor.constants.EndpointsFortune;

/**
 * Ручка для колеса фортуны
 */

public class GameWheelApi extends BaseApi {

    public GameWheelApi(String baseUrl) {
        super(baseUrl);
    }

    @Step("GET /user/game-wheel/rewards - получение списка наград игрового колеса")
    public Response getGameWheelRewards(String authToken) {
        return getWithToken(EndpointsFortune.GAME_WHEEL_REWARDS, authToken);
    }

    @Step("POST /user/game-wheel/start-game - запуск игры на колесе")
    public Response startGame(String authToken) {
        return postWithToken(EndpointsFortune.GAME_WHEEL_START, authToken, "[]");
    }

    @Step("GET /user/reward/list - получение списка наград пользователя")
    public Response getUserRewards(String authToken) {
        return getWithToken(EndpointsFortune.GAME_REWARD_LIST, authToken);
    }

    @Step("POST /user/reward/open/{id} - удаление подарка из списка призов")
    public Response openReward(String authToken, Long rewardId) {
        return postWithToken(EndpointsFortune.GAME_REWARD_OPEN_ID, authToken, "[]", rewardId);
    }

    @Step("GET /user/reward/history - получение истории забранных наград пользователя")
    public Response getUserRewardHistory(String authToken) {
        return getWithToken(EndpointsFortune.GAME_REWARD_HISTORY, authToken);
    }

    @Step("GET /user/game-wheel/rewards_groups - группы наград")
    public Response getRewardsGroups(String authToken) {
        return getWithToken(EndpointsFortune.GAME_WHEEL_REWARDS_GROUPS, authToken);
    }

    @Step("GET /user/game-wheel/timer-left-seconds - возвращение кол-ва секунд")
    public Response getTimarLeftSeconds(String authToken) {
        return getWithToken(EndpointsFortune.GAME_WHEEL_TIMER_LEFT_SECONDS, authToken);
    }

    @Step("GET /user/game-wheel/rewards-recently-won-all - список недавних победителей")
    public Response getRewardsRecentlyWon(String authToken) {
        return getWithToken(EndpointsFortune.GAME_WHEEL_REWARDS_RECENTLY_WON_ALL, authToken);
    }
}