package ru.tutor.steps.fortune;

import io.qameta.allure.Step;
import ru.tutor.api.fortune.GameWheelApi;
import io.restassured.response.Response;
import ru.tutor.base.BaseApiSteps;
import ru.tutor.model.fortune.GameWheelRewardResponse;
import ru.tutor.utils.AllureAttachments;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/*
 * Шаги и логика колеса для тестов колеса фортуны
 */

public class GameWheelRewardsSteps extends BaseApiSteps {

    private final GameWheelApi gameWheelApi;

    public GameWheelRewardsSteps (String baseUrl) {
        super(baseUrl);
        this.gameWheelApi = new GameWheelApi(baseUrl);
    }

    @Step("Получение списка наград")
    public Response getGameWheelRewards(String authToken) {
        Response response = gameWheelApi.getGameWheelRewards(authToken);
        AllureAttachments.attachJson("Game wheel Rewards Response", response.getBody().asString());
        AllureAttachments.attachJson("Game wheel Rewards Headers", response.getHeaders().toString());
        return response;
    }

    @Step("Валидация успешного ответа со списком наград")
    public List<GameWheelRewardResponse> validateGameWheelRewardsResponse(Response response) {
        assertSuccessStatusCode(response);
        assertContentTypeJson(response);

        response.then()
                .body("$", instanceOf(List.class))
                .body("$.size()", greaterThan(0));

        // Преобразуем в модель
        List<GameWheelRewardResponse> rewards = response.jsonPath()
                .getList("$", GameWheelRewardResponse.class);

        assertNotNull(rewards, "Список наград колеса фортуны равен null");
        assertFalse(rewards.isEmpty(), "Game wheel rewards list is empty");

        return rewards;
    }

    @Step("Валидация структуры каждой награды")
    public void validaterewardStructure(List<GameWheelRewardResponse> rewards) {
        for (int i = 0; i < rewards.size(); i++) {
            GameWheelRewardResponse reward = rewards.get(i);
            String rewardInfo = "Награда #" + (i + 1) + " (ID: " + reward.getId() + ")";

            assertAll(rewardInfo + " - проверка структуры",
                    () -> assertNotNull(reward.getId(), "id не должен быть null"),
                    () -> assertNotNull(reward.getName(), "name не должен быть null"),
                    () -> assertNotNull(reward.getChance(), "chance не должен быть null"),
                    () -> assertNotNull(reward.getRewardType(), "rewardType не должен быть null"),
                    () -> assertNotNull(reward.getRewardValue(), "rewardValue не должен быть null"),

                    () -> assertNotNull(reward.getIconSmallUrl(), "iconSmallUrl не должен быть null"),
                    () -> assertNotNull(reward.getIconUrl(), "iconUrl не должен быть null"),
                    () -> assertNotNull(reward.getShowValue(), "iconShowValue не должен быть null"),
                    () -> assertNotNull(reward.getIconSmallAltUrl(), "iconSmallAltUrl не должен быть null")
            );

            // Проверка значений
            assertTrue(reward.getChance() >= 0 && reward.getChance() <= 100,
                    rewardInfo + " - chance должен быть от 0 до 100");
            assertTrue(reward.getRewardValue() >= 0,
                    rewardInfo + " - rewardValue должен быть >= 0");
        }
    }

    // Обновленный RewardValidationResult
    public static class RewardValidationResult {
        private final Long rewardId;
        private final String rewardName;
        private boolean found;
        private boolean nameMatches;
        private boolean chanceMatches;
        private boolean typeMatches;
        private boolean valueMatches;
        private double actualChance;
        private String message;

        public RewardValidationResult(Long rewardId, String rewardName) {
            this.rewardId = rewardId;
            this.rewardName = rewardName;
        }

        // Геттеры и сеттеры
        public Long getRewardId() { return rewardId; }
        public String getRewardName() { return rewardName; }
        public boolean isNotFound() { return found; }
        public boolean isNameMatches() { return nameMatches; }
        public boolean isChanceMatches() { return chanceMatches; }
        public boolean isTypeMatches() { return typeMatches; }
        public boolean isValueMatches() { return valueMatches; }
        public double getActualChance() { return actualChance; }
        public String getMessage() { return message; }

        public void setFound(boolean found) { this.found = found; }
        public void setNameMatches(boolean nameMatches) { this.nameMatches = nameMatches; }
        public void setChanceMatches(boolean chanceMatches) { this.chanceMatches = chanceMatches; }
        public void setTypeMatches(boolean typeMatches) { this.typeMatches = typeMatches; }
        public void setValueMatches(boolean valueMatches) { this.valueMatches = valueMatches; }
        public void setActualChance(double actualChance) { this.actualChance = actualChance; }
        public void setMessage(String message) { this.message = message; }

        public boolean hasMessage() { return message != null && !message.isEmpty(); }
    }
}