package ru.tutor.steps.fortune;

import io.qameta.allure.Step;
import ru.tutor.api.GameWheelApi;
import io.restassured.response.Response;
import ru.tutor.base.BaseApiSteps;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
public class GameWheelMiscSteps extends BaseApiSteps {

    private final GameWheelApi gameWheelApi;

    public GameWheelMiscSteps (String baseUrl) {
        super(baseUrl);
        this.gameWheelApi = new GameWheelApi(baseUrl);
    }

    // --- rewards_group ---
    @Step("GET /user/game-wheel/rewards_groups")
    public Response getUserGameWheelRewardsGroups(String authToken) {
        return gameWheelApi.getRewardsGroups(authToken);
    }

    @Step("Валидация групп наград")
    public void validateRewardsGroupsItems(Response response) {
        assertBasicResponse(response);

        List<Map<String, Object>> rewardsGroups = response.jsonPath().getList("$");
        assertNotNull(rewardsGroups, "Список групп наград не должен быть null");
        assertFalse(rewardsGroups.isEmpty(), "Список групп наград не должен быть пуст");

        for (int i =0; i < rewardsGroups.size(); i++) {
            Map<String, Object> rewardsGroup = rewardsGroups.get(i);
            assertAll("Элемент [" + i + "] «" + rewardsGroup.get("name") + "»",
                    () -> assertTrue(((Number) rewardsGroup.get("id")).intValue() > 0,
                            "ID должен быть > 0"),
                    () -> assertNotNull(rewardsGroup.get("name"),
                            "Название не должно быть null"),
                    () -> assertNotNull(rewardsGroup.get("iconSmallUrl"),
                            "iconSmallUrl не должен быть null"),
                    () -> assertNotNull(rewardsGroup.get("iconUrl"),
                            "iconUrl не должен быть null"),
                    () -> assertNotNull(rewardsGroup.get("iconSmallAltUrl"),
                            "iconSmallAltUrl не должен быть null")
            );
        }
    }

    @Step("GET /user/game-wheel/timer-left-seconds")
    public Response getUserGameWheelTimerLeft(String authToken) {
        return gameWheelApi.getTimarLeftSeconds(authToken);
    }

    @Step("Валидация таймера")
    public void validateTimerLeftSeconds(Response response) {

        assertBasicResponse(response);
        int seconds = response.jsonPath().getInt("$");
        assertTrue(seconds >= 0, "timer-left-seconds должен быть >= 0, получен: " + seconds);
    }

    @Step("GET /user/game-wheel/rewards-recently-won-all")
    public Response getUserGameWheelRewardsRecentlyWonAll(String authToken) {
        return gameWheelApi.getRewardsRecentlyWon(authToken);
    }

    @Step("Валидация недавнего победителя")
    public void validateRecentlyWonItem(Response response) {
        assertBasicResponse(response);

        List<Map<String, Object>> rewards = response.jsonPath().getList("$");
        assertNotNull(rewards, "Список недавних победителей не должен быть null");
        assertFalse(rewards. isEmpty(), "список недавних победителей не должен быть пуст");

        for (int i =0; i < rewards.size(); i++) {
            Map<String, Object> reward = rewards.get(i);
            assertAll("Победитель [" + i + "] «" + reward.get("name") + "»",
                    () -> assertTrue((((Number) reward.get("id")).intValue() > 0), "id должен быть > 0"),
                    () -> assertNotNull(reward.get("name"), "name не должен быть null"),
                    () -> assertNotNull(reward.get("surname"), "surname не должен быть null"),
                    () -> assertNotNull(reward.get("prizeText"), "prizeText не должен быть null"),
                    () -> assertNotNull(reward.get("prizeType"), "prizeType не должен быть null"),
                    () -> assertTrue(List.of("diamond", "experience", "prize", "valuation").contains(reward.get("prizeType")),
                            "prizeType должен быть допустимым: " + reward.get("prizeType")),
                    () -> assertTrue(((Number) reward.get("score")).intValue() > 0, "score должен быть > 0"),
                    () -> assertNotNull(reward.get("imageUrl"), "imageUrl не должен быть null")
            );
        }
    }
}