package ru.tutor.steps.fortune;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import ru.tutor.api.GameWheelApi;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.tutor.constants.GameWheelRewardsConstants;
import ru.tutor.model.fortune.GameWheelRewardResponse;
import ru.tutor.utils.AllureAttachments;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/*
 * Шаги и логика колеса для тестов колеса фортуны
 */

public record GameWheelRewardsSteps (GameWheelApi gameWheelApi) {

    public static GameWheelRewardsSteps withBaseUrl(String baseUrl) {
        return new GameWheelRewardsSteps(new GameWheelApi(baseUrl));
    }

    @Step("Получение списка наград")
    public Response getGameWheelRewards(String authToken) {
        Response response = this.gameWheelApi.getGameWheelRewards(authToken);
        AllureAttachments.attachJson("Game wheel Rewards Response", response.getBody().asString());
        AllureAttachments.attachJson("Game wheel Rewards Headers", response.getHeaders().toString());
        return response;
    }

    @Step("Валидация успешного ответа со списком наград")
    public List<GameWheelRewardResponse> validateGameWheelRewardsResponse(Response response) {
        response.then()
                .statusCode(200)
                .contentType(ContentType.JSON);

        response.then()
                .body("$", instanceOf(List.class))
                .body("$.size()", greaterThan(0));

        // Преобразуем в модель
        List<GameWheelRewardResponse> rewards = response.jsonPath()
                .getList("$", GameWheelRewardResponse.class);

        assertNotNull(rewards, "Game wheel rewards list is null");
        assertFalse(rewards.isEmpty(), "Game wheel rewards list is empty");

        return rewards;
    }

    @Step("Валидация структуры каждой награды")
    public void validaterewardStructure(List<GameWheelRewardResponse> rewards) {
        for (int i = 0; i < rewards.size(); i++) {
            GameWheelRewardResponse reward = rewards.get(i);
            String rewardInfo = "Награда #" + (i + 1) + " (ID: " + reward.getId() + ")";

            assertAll(rewardInfo + " - проверка структуры",
                    () -> assertNotNull(reward.getId(), "id should not be null"),
                    () -> assertNotNull(reward.getName(), "name should not be null"),
                    () -> assertNotNull(reward.getChance(), "chance should not be null"),
                    () -> assertNotNull(reward.getRewardType(), "rewardType should not be null"),
                    () -> assertNotNull(reward.getRewardValue(), "rewardValue should not be null")
            );

            // Проверка значений
            assertTrue(reward.getChance() >= 0 && reward.getChance() <= 100,
                    rewardInfo + " - chance should be between 0 and 100");
            assertTrue(reward.getRewardValue() >= 0,
                    rewardInfo + " - rewardValue should be >= 0");
        }
    }

    @Step("Сравнение с эталонными значениями наград")
    public Map<Long, RewardValidationResult> validateAgainstExpectedValues(List<GameWheelRewardResponse> actualRewards) {
        Map<Long, RewardValidationResult> validationResults = new HashMap<>();

        // Проверяем каждую актуальную награду
        for (GameWheelRewardResponse actualReward : actualRewards) {
            Long rewardId = actualReward.getId();
            String rewardName = actualReward.getName();

            RewardValidationResult result = new RewardValidationResult(rewardId, rewardName);

            // Получаем эталонную награду по ID из констант
            GameWheelRewardsConstants.ExpectedReward expectedReward =
                    GameWheelRewardsConstants.getExpectedRewardById(rewardId);

            if (expectedReward == null) {
                result.setFound(false);
                result.setMessage("Награда с ID " + rewardId + " не найдена в эталонных данных");
            } else {
                result.setFound(true);
                result.setActualChance(actualReward.getChance());

                // Проверяем название
                if (expectedReward.name().equals(actualReward.getName())) {
                    result.setNameMatches(true);
                } else {
                    result.setNameMatches(false);
                    result.setMessage(String.format("Название не совпадает: ожидалось '%s', получено '%s'",
                            expectedReward.name(), actualReward.getName()));
                }

                // Проверяем шанс
                if (GameWheelRewardsConstants.isChanceMatches(rewardId, actualReward.getChance())) {
                    result.setChanceMatches(true);
                } else {
                    result.setChanceMatches(false);
                    if (!result.hasMessage()) {
                        result.setMessage(String.format("Шанс не совпадает: ожидалось %.4f%%, получено %.4f%%",
                                expectedReward.chance(), actualReward.getChance()));
                    }
                }

                // Проверяем тип награды
                if (expectedReward.rewardType().equals(actualReward.getRewardType())) {
                    result.setTypeMatches(true);
                } else {
                    result.setTypeMatches(false);
                    if (!result.hasMessage()) {
                        result.setMessage(String.format("Тип не совпадает: ожидался '%s', получен '%s'",
                                expectedReward.rewardType(), actualReward.getRewardType()));
                    }
                }

                // Проверяем значение награды
                if (expectedReward.rewardValue().equals(actualReward.getRewardValue())) {
                    result.setValueMatches(true);
                } else {
                    result.setValueMatches(false);
                    if (!result.hasMessage()) {
                        result.setMessage(String.format("Значение не совпадает: ожидалось %d, получено %d",
                                expectedReward.rewardValue(), actualReward.getRewardValue()));
                    }
                }
            }

            validationResults.put(rewardId, result);
        }

        return validationResults;
    }

    @Step("Проверка ответа без авторизации")
    public void validateUnauthorizedResponse(Response response) {
        int statusCode = response.getStatusCode();
        Allure.addAttachment("Ответ без авторизации", "text/plain",
                "Статус код: " + statusCode + "\n" +
                        "Ожидается: 401");

        assertEquals(401, statusCode, "Should return 401 for missing/invalid token. Got: " + statusCode);
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