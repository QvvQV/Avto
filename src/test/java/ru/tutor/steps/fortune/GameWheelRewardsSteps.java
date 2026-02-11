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

/**
 * Шаги и логика колеса для тестов колеса фортуны
 */

import java.util.Map;

public class GameWheelRewardsSteps {
    private final GameWheelApi gameWheelApi;

    public GameWheelRewardsSteps(String baseUrl) {
        this.gameWheelApi = new GameWheelApi(baseUrl);
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
                if (expectedReward.getName().equals(actualReward.getName())) {
                    result.setNameMatches(true);
                } else {
                    result.setNameMatches(false);
                    result.setMessage(String.format("Название не совпадает: ожидалось '%s', получено '%s'",
                            expectedReward.getName(), actualReward.getName()));
                }

                // Проверяем шанс
                if (GameWheelRewardsConstants.isChanceMatches(rewardId, actualReward.getChance())) {
                    result.setChanceMatches(true);
                } else {
                    result.setChanceMatches(false);
                    if (!result.hasMessage()) {
                        result.setMessage(String.format("Шанс не совпадает: ожидалось %.4f%%, получено %.4f%%",
                                expectedReward.getChance(), actualReward.getChance()));
                    }
                }

                // Проверяем тип награды
                if (expectedReward.getRewardType().equals(actualReward.getRewardType())) {
                    result.setTypeMatches(true);
                } else {
                    result.setTypeMatches(false);
                    if (!result.hasMessage()) {
                        result.setMessage(String.format("Тип не совпадает: ожидался '%s', получен '%s'",
                                expectedReward.getRewardType(), actualReward.getRewardType()));
                    }
                }

                // Проверяем значение награды
                if (expectedReward.getRewardValue().equals(actualReward.getRewardValue())) {
                    result.setValueMatches(true);
                } else {
                    result.setValueMatches(false);
                    if (!result.hasMessage()) {
                        result.setMessage(String.format("Значение не совпадает: ожидалось %d, получено %d",
                                expectedReward.getRewardValue(), actualReward.getRewardValue()));
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

        assertTrue(statusCode == 401,
                "Should return 401 for missing/invalid token. Got: " + statusCode);
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
        public boolean isFound() { return found; }
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

        public boolean isPerfectMatch() {
            return found && nameMatches && chanceMatches && typeMatches && valueMatches;
        }
    }

    @Step("Генерация детального отчета о расхождениях")
    public void generateDetailedDiscrepancyReport(List<GameWheelRewardResponse> rewards,
                                                   Map<Long, GameWheelRewardsSteps.RewardValidationResult> validationResults) {

        StringBuilder discrepancyReport = new StringBuilder();
        discrepancyReport.append("=".repeat(80)).append("\n");
        discrepancyReport.append("ДЕТАЛЬНЫЙ ОТЧЕТ О РАСХОЖДЕНИЯХ С ЭТАЛОНОМ\n");
        discrepancyReport.append("=".repeat(80)).append("\n\n");

        // Группируем результаты по типам расхождений
        List<GameWheelRewardsSteps.RewardValidationResult> perfectMatches = new ArrayList<>();
        List<GameWheelRewardsSteps.RewardValidationResult> nameMismatches = new ArrayList<>();
        List<GameWheelRewardsSteps.RewardValidationResult> chanceMismatches = new ArrayList<>();
        List<GameWheelRewardsSteps.RewardValidationResult> typeMismatches = new ArrayList<>();
        List<GameWheelRewardsSteps.RewardValidationResult> valueMismatches = new ArrayList<>();
        List<GameWheelRewardsSteps.RewardValidationResult> multipleMismatches = new ArrayList<>();
        List<GameWheelRewardsSteps.RewardValidationResult> notFound = new ArrayList<>();

        for (GameWheelRewardsSteps.RewardValidationResult result : validationResults.values()) {
            if (!result.isFound()) {
                notFound.add(result);
            } else if (result.isPerfectMatch()) {
                perfectMatches.add(result);
            } else {
                // Анализируем тип расхождения
                int mismatchCount = 0;
                if (!result.isNameMatches()) mismatchCount++;
                if (!result.isChanceMatches()) mismatchCount++;
                if (!result.isTypeMatches()) mismatchCount++;
                if (!result.isValueMatches()) mismatchCount++;

                if (mismatchCount > 1) {
                    multipleMismatches.add(result);
                } else {
                    if (!result.isNameMatches()) nameMismatches.add(result);
                    if (!result.isChanceMatches()) chanceMismatches.add(result);
                    if (!result.isTypeMatches()) typeMismatches.add(result);
                    if (!result.isValueMatches()) valueMismatches.add(result);
                }
            }
        }

        // Статистика расхождений
        discrepancyReport.append("СТАТИСТИКА РАСХОЖДЕНИЙ:\n");
        discrepancyReport.append("-".repeat(40)).append("\n");
        discrepancyReport.append(String.format("Всего проверено наград: %d\n", validationResults.size()));
        discrepancyReport.append(String.format("Полностью соответствуют: %d\n", perfectMatches.size()));
        discrepancyReport.append(String.format("Не найдены в эталоне: %d\n", notFound.size()));
        discrepancyReport.append(String.format("Расхождения в названии: %d\n", nameMismatches.size()));
        discrepancyReport.append(String.format("Расхождения в шансе: %d\n", chanceMismatches.size()));
        discrepancyReport.append(String.format("Расхождения в типе: %d\n", typeMismatches.size()));
        discrepancyReport.append(String.format("Расхождения в значении: %d\n", valueMismatches.size()));
        discrepancyReport.append(String.format("Множественные расхождения: %d\n", multipleMismatches.size()));
        discrepancyReport.append(String.format("Общий процент соответствия: %.1f%%\n\n",
                (perfectMatches.size() * 100.0) / validationResults.size()));

        // Детали по не найденным наградам
        if (!notFound.isEmpty()) {
            discrepancyReport.append("❌ НЕ НАЙДЕНЫ В ЭТАЛОНЕ:\n");
            discrepancyReport.append("-".repeat(40)).append("\n");
            for (GameWheelRewardsSteps.RewardValidationResult result : notFound) {
                discrepancyReport.append(String.format("ID: %d | Название: %s\n",
                        result.getRewardId(), result.getRewardName()));
                if (result.hasMessage()) {
                    discrepancyReport.append("     Причина: ").append(result.getMessage()).append("\n");
                }
                discrepancyReport.append("\n");
            }
        }

        // Детали по расхождениям в названии
        if (!nameMismatches.isEmpty()) {
            discrepancyReport.append("🔄 РАСХОЖДЕНИЯ В НАЗВАНИИ:\n");
            discrepancyReport.append("-".repeat(40)).append("\n");
            for (GameWheelRewardsSteps.RewardValidationResult result : nameMismatches) {
                // Находим ожидаемое название по ID
                GameWheelRewardsConstants.ExpectedReward expected =
                        GameWheelRewardsConstants.getExpectedRewardById(result.getRewardId());
                if (expected != null) {
                    discrepancyReport.append(String.format("ID: %d\n", result.getRewardId()));
                    discrepancyReport.append(String.format("  Ожидалось: '%s'\n", expected.getName()));
                    discrepancyReport.append(String.format("  Получено:  '%s'\n", result.getRewardName()));
                    discrepancyReport.append("\n");
                }
            }
        }

        // Детали по расхождениям в шансе (самое важное!)
        if (!chanceMismatches.isEmpty()) {
            discrepancyReport.append("🎯 РАСХОЖДЕНИЯ В ШАНСЕ (CHANCE):\n");
            discrepancyReport.append("-".repeat(40)).append("\n");
            for (GameWheelRewardsSteps.RewardValidationResult result : chanceMismatches) {
                GameWheelRewardsConstants.ExpectedReward expected =
                        GameWheelRewardsConstants.getExpectedRewardById(result.getRewardId());
                if (expected != null) {
                    double difference = Math.abs(expected.getChance() - result.getActualChance());
                    discrepancyReport.append(String.format("ID: %d | Название: %s\n",
                            result.getRewardId(), result.getRewardName()));
                    discrepancyReport.append(String.format("  Ожидаемый шанс: %.4f%%\n", expected.getChance()));
                    discrepancyReport.append(String.format("  Фактический шанс: %.4f%%\n", result.getActualChance()));
                    discrepancyReport.append(String.format("  Разница: %.4f%%\n", difference));
                    discrepancyReport.append(String.format("  Допустимая погрешность: ±%.4f%%\n",
                            GameWheelRewardsConstants.CHANCE_TOLERANCE));

                    // Определяем серьезность расхождения
                    String severity = difference <= GameWheelRewardsConstants.CHANCE_TOLERANCE ?
                            "В пределах допустимого" : "КРИТИЧЕСКОЕ";
                    discrepancyReport.append(String.format("  Статус: %s\n", severity));
                    discrepancyReport.append("\n");
                }
            }
        }

        // Детали по расхождениям в типе
        if (!typeMismatches.isEmpty()) {
            discrepancyReport.append("🏷️ РАСХОЖДЕНИЯ В ТИПЕ НАГРАДЫ:\n");
            discrepancyReport.append("-".repeat(40)).append("\n");
            for (GameWheelRewardsSteps.RewardValidationResult result : typeMismatches) {
                GameWheelRewardsConstants.ExpectedReward expected =
                        GameWheelRewardsConstants.getExpectedRewardById(result.getRewardId());
                if (expected != null) {
                    // Находим фактический тип из ответа
                    String actualType = rewards.stream()
                            .filter(r -> r.getId().equals(result.getRewardId()))
                            .findFirst()
                            .map(GameWheelRewardResponse::getRewardType)
                            .orElse("не найден");

                    discrepancyReport.append(String.format("ID: %d | Название: %s\n",
                            result.getRewardId(), result.getRewardName()));
                    discrepancyReport.append(String.format("  Ожидался тип: '%s'\n", expected.getRewardType()));
                    discrepancyReport.append(String.format("  Получен тип:  '%s'\n", actualType));
                    discrepancyReport.append("\n");
                }
            }
        }

        // Детали по расхождениям в значении
        if (!valueMismatches.isEmpty()) {
            discrepancyReport.append("💰 РАСХОЖДЕНИЯ В ЗНАЧЕНИИ НАГРАДЫ:\n");
            discrepancyReport.append("-".repeat(40)).append("\n");
            for (GameWheelRewardsSteps.RewardValidationResult result : valueMismatches) {
                GameWheelRewardsConstants.ExpectedReward expected =
                        GameWheelRewardsConstants.getExpectedRewardById(result.getRewardId());
                if (expected != null) {
                    // Находим фактическое значение из ответа
                    Integer actualValue = rewards.stream()
                            .filter(r -> r.getId().equals(result.getRewardId()))
                            .findFirst()
                            .map(GameWheelRewardResponse::getRewardValue)
                            .orElse(null);

                    discrepancyReport.append(String.format("ID: %d | Название: %s\n",
                            result.getRewardId(), result.getRewardName()));
                    discrepancyReport.append(String.format("  Ожидаемое значение: %d\n", expected.getRewardValue()));
                    discrepancyReport.append(String.format("  Полученное значение: %s\n",
                            actualValue != null ? actualValue : "null"));
                    if (actualValue != null) {
                        int difference = Math.abs(expected.getRewardValue() - actualValue);
                        discrepancyReport.append(String.format("  Разница: %d\n", difference));
                    }
                    discrepancyReport.append("\n");
                }
            }
        }

        // Детали по множественным расхождениям
        if (!multipleMismatches.isEmpty()) {
            discrepancyReport.append("⚠ МНОЖЕСТВЕННЫЕ РАСХОЖДЕНИЯ:\n");
            discrepancyReport.append("-".repeat(40)).append("\n");
            for (GameWheelRewardsSteps.RewardValidationResult result : multipleMismatches) {
                GameWheelRewardsConstants.ExpectedReward expected =
                        GameWheelRewardsConstants.getExpectedRewardById(result.getRewardId());
                if (expected != null) {
                    // Находим фактическую награду из ответа
                    GameWheelRewardResponse actualReward = rewards.stream()
                            .filter(r -> r.getId().equals(result.getRewardId()))
                            .findFirst()
                            .orElse(null);

                    if (actualReward != null) {
                        discrepancyReport.append(String.format("ID: %d | Название: '%s' (ожидалось: '%s')\n",
                                result.getRewardId(), actualReward.getName(), expected.getName()));

                        if (!result.isNameMatches()) {
                            discrepancyReport.append("  • Название не совпадает\n");
                        }

                        if (!result.isChanceMatches()) {
                            double diff = Math.abs(expected.getChance() - actualReward.getChance());
                            discrepancyReport.append(String.format("  • Шанс: ожидалось %.4f%%, получено %.4f%% (разница: %.4f%%)\n",
                                    expected.getChance(), actualReward.getChance(), diff));
                        }

                        if (!result.isTypeMatches()) {
                            discrepancyReport.append(String.format("  • Тип: ожидался '%s', получен '%s'\n",
                                    expected.getRewardType(), actualReward.getRewardType()));
                        }

                        if (!result.isValueMatches()) {
                            discrepancyReport.append(String.format("  • Значение: ожидалось %d, получено %d\n",
                                    expected.getRewardValue(), actualReward.getRewardValue()));
                        }

                        discrepancyReport.append("\n");
                    }
                }
            }
        }

        // Сводная таблица всех расхождений
        if (!perfectMatches.isEmpty() || !notFound.isEmpty() ||
                !nameMismatches.isEmpty() || !chanceMismatches.isEmpty() ||
                !typeMismatches.isEmpty() || !valueMismatches.isEmpty() ||
                !multipleMismatches.isEmpty()) {

            discrepancyReport.append("📊 СВОДНАЯ ТАБЛИЦА ВСЕХ ПРОВЕРЕННЫХ НАГРАД:\n");
            discrepancyReport.append("-".repeat(100)).append("\n");
            discrepancyReport.append(String.format("%-4s | %-35s | %-12s | %-10s | %-8s | %-8s | %s\n",
                    "ID", "Название", "Шанс", "Тип", "Значение", "Статус", "Примечание"));
            discrepancyReport.append("-".repeat(100)).append("\n");

            // Сортируем по ID для удобства чтения
            validationResults.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        GameWheelRewardsSteps.RewardValidationResult result = entry.getValue();
                        GameWheelRewardsConstants.ExpectedReward expected =
                                GameWheelRewardsConstants.getExpectedRewardById(result.getRewardId());

                        String status;
                        String note = "";

                        if (!result.isFound()) {
                            status = "❌ НЕ НАЙДЕН";
                            note = "Отсутствует в эталоне";
                        } else if (result.isPerfectMatch()) {
                            status = "✅ СООТВЕТСТВУЕТ";
                        } else {
                            status = "⚠ РАСХОЖДЕНИЯ";
                            List<String> mismatches = new ArrayList<>();
                            if (!result.isNameMatches()) mismatches.add("название");
                            if (!result.isChanceMatches()) mismatches.add("шанс");
                            if (!result.isTypeMatches()) mismatches.add("тип");
                            if (!result.isValueMatches()) mismatches.add("значение");
                            note = String.join(", ", mismatches);
                        }

                        String chanceInfo = expected != null ?
                                String.format("%.4f%%", expected.getChance()) : "N/A";
                        String typeInfo = expected != null ? expected.getRewardType() : "N/A";
                        String valueInfo = expected != null ?
                                String.valueOf(expected.getRewardValue()) : "N/A";

                        discrepancyReport.append(String.format("%-4d | %-35s | %-12s | %-10s | %-8s | %-8s | %s\n",
                                result.getRewardId(),
                                result.getRewardName().length() > 35 ?
                                        result.getRewardName().substring(0, 32) + "..." :
                                        result.getRewardName(),
                                chanceInfo,
                                typeInfo,
                                valueInfo,
                                status,
                                note));
                    });

            discrepancyReport.append("-".repeat(100)).append("\n");
        }

        // Заключение
        discrepancyReport.append("\n").append("=".repeat(80)).append("\n");
        discrepancyReport.append("ЗАКЛЮЧЕНИЕ:\n");
        discrepancyReport.append("-".repeat(40)).append("\n");

        long totalMismatches = nameMismatches.size() + chanceMismatches.size() +
                typeMismatches.size() + valueMismatches.size() +
                multipleMismatches.size();

        if (totalMismatches == 0 && notFound.isEmpty()) {
            discrepancyReport.append("✅ ВСЕ НАГРАДЫ ПОЛНОСТЬЮ СООТВЕТСТВУЮТ ЭТАЛОНУ\n");
            discrepancyReport.append("Отличный результат! Все проверки пройдены успешно.\n");
        } else {
            discrepancyReport.append("НАЙДЕНЫ РАСХОЖДЕНИЯ С ЭТАЛОНОМ:\n");
            discrepancyReport.append(String.format("• Всего расхождений: %d\n", totalMismatches));
            discrepancyReport.append(String.format("• Не найдено в эталоне: %d\n", notFound.size()));

            if (chanceMismatches.size() > 0) {
                discrepancyReport.append("⚠ ВНИМАНИЕ: Расхождения в шансах могут влиять на баланс игры!\n");
            }

            discrepancyReport.append("\nРекомендации:\n");
            discrepancyReport.append("1. Проверить актуальность эталонных значений\n");
            discrepancyReport.append("2. Убедиться в правильности конфигурации наград на сервере\n");
            discrepancyReport.append("3. Согласовать изменения с бизнес-требованиями\n");
        }

        discrepancyReport.append("\n").append("=".repeat(80)).append("\n");

        // Прикрепляем отчет к Allure
        Allure.addAttachment("Детальный отчет о расхождениях", "text/plain", discrepancyReport.toString());

        // Также создаем сокращенную версию для быстрого просмотра
        if (totalMismatches > 0 || notFound.size() > 0) {
            StringBuilder quickReport = new StringBuilder();
            quickReport.append("🚨 ОБНАРУЖЕНЫ РАСХОЖДЕНИЯ:\n\n");

            if (notFound.size() > 0) {
                quickReport.append("Не найдены в эталоне (").append(notFound.size()).append("):\n");
                notFound.forEach(r -> quickReport.append("  • ID ").append(r.getRewardId())
                        .append(": ").append(r.getRewardName()).append("\n"));
            }

            if (chanceMismatches.size() > 0) {
                quickReport.append("\nРасхождения в шансах (").append(chanceMismatches.size()).append("):\n");
                chanceMismatches.forEach(r -> {
                    GameWheelRewardsConstants.ExpectedReward expected =
                            GameWheelRewardsConstants.getExpectedRewardById(r.getRewardId());
                    if (expected != null) {
                        quickReport.append("  • ID ").append(r.getRewardId())
                                .append(": ожидалось ").append(String.format("%.4f", expected.getChance()))
                                .append("%, получено ").append(String.format("%.4f", r.getActualChance()))
                                .append("%\n");
                    }
                });
            }

            Allure.addAttachment("Краткий отчет о проблемах", "text/plain", quickReport.toString());
        }
    }
}