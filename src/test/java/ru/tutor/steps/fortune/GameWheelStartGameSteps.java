package ru.tutor.steps.fortune;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import ru.tutor.api.GameWheelApi;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.tutor.constants.GameWheelRewardsConstants;
import ru.tutor.model.fortune.GameWheelStartGameResponse;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Шаги для запуска колеса, с отчётом
 */

public record  GameWheelStartGameSteps(GameWheelApi gameWheelApi) {

    public static GameWheelStartGameSteps withBaseUrl(String baseUrl) {
        return new GameWheelStartGameSteps(new GameWheelApi(baseUrl));
    }


    @Step("Запуск игры на колесе")
    public Response startGame(String authToken) {
        return this.gameWheelApi.startGame(authToken);
    }

    @Step("Валидация успешного ответа при запуске игры")
    public GameWheelStartGameResponse validateStartGameResponse(Response response) {
        // Базовая валидация ответа
        response.then()
                .statusCode(200)
                .contentType(ContentType.JSON);

        // Валидация структуры JSON
        response.then()
                .body("id", notNullValue())
                .body("name", notNullValue())
                .body("type", notNullValue())
                .body("value", notNullValue())
                .body("userRewardId", notNullValue());

        // Преобразуем в модель
        GameWheelStartGameResponse prize = response.as(GameWheelStartGameResponse.class);

        validatePrizeFields(prize);

        Allure.addAttachment("Успешный запуск", "text/plain",
                String.format("Получен приз: %s (ID: %d)", prize.getName(), prize.getId()));

        return prize;
    }

    @Step("Детальная валидация полей приза")
    public void validatePrizeFields(GameWheelStartGameResponse prize) {
        assertAll(
                () -> assertNotNull(prize.getId(), "id должен быть не null"),
                () -> assertTrue(prize.getId() > 0, "id должен быть положительным числом"),

                // Name
                () -> assertNotNull(prize.getName(), "name должен быть не null"),
                () -> assertFalse(prize.getName().trim().isEmpty(), "name не должен быть пустым"),
                () -> assertTrue(prize.getName().length() <= 100, "name не должен превышать 100 символов"),

                // Type
                () -> assertNotNull(prize.getType(), "type должен быть не null"),
                () -> assertTrue(
                        GameWheelRewardsConstants.ExpectedStartGamePrize.isValidType(prize.getType()),
                        String.format("type должен быть одним из допустимых: %s. Получено: %s",
                                Arrays.toString(GameWheelRewardsConstants.ALLOWED_TYPES),
                                prize.getType())
                ),

                // Value
                () -> assertNotNull(prize.getValue(), "value должен быть не null"),
                () -> assertTrue(
                        GameWheelRewardsConstants.ExpectedStartGamePrize.isValueValidForType(
                                prize.getType(), prize.getValue()),
                        GameWheelRewardsConstants.ExpectedStartGamePrize.getValueErrorMessage(
                                prize.getType(), prize.getValue())
                ),

                // userRewardId
                () -> assertNotNull(prize.getUserRewardId(), "userRewardId должен быть не null"),
                () -> assertTrue(prize.getUserRewardId() > 0, "userRewardId должен быть больше 0")
        );

        // Сохраняем userRewardId для проверки уникальности
        Long lastUserRewardId = prize.getUserRewardId();
    }

    @Step("Проверка соответствия приза эталонным данным")
    public PrizeValidationResult validateAgainstExpectedValues(GameWheelStartGameResponse prize) {
        Long prizeId = prize.getId();
        String prizeName = prize.getName();

        PrizeValidationResult result = new PrizeValidationResult(prizeId, prizeName);

        // Получаем эталонный приз по ID
        GameWheelRewardsConstants.ExpectedStartGamePrize expectedPrize =
                GameWheelRewardsConstants.ExpectedStartGamePrize.getExpectedStartGamePrizeById(prizeId);

        if (expectedPrize == null) {
            // Приз не найден в эталонных данных
            result.setFound(false);
            result.setMessage(String.format("Приз с ID %d не найден в эталонных данных", prizeId));
            result.setStatus(PrizeValidationResult.Status.UNKNOWN);
        } else {
            result.setFound(true);
            result.setExpectedName(expectedPrize.name());
            result.setExpectedType(expectedPrize.type());
            result.setExpectedMinValue(expectedPrize.minValue());
            result.setExpectedMaxValue(expectedPrize.maxValue());

            // Проверяем соответствие каждого поля
            boolean allMatches = true;
            StringBuilder validationDetails = new StringBuilder();

            // Проверка названия
            if (expectedPrize.name().equals(prize.getName())) {
                result.setNameMatches(true);
                validationDetails.append("✅ Название совпадает\n");
            } else {
                result.setNameMatches(false);
                allMatches = false;
                validationDetails.append(String.format("❌ Название не совпадает:\n  Ожидалось: '%s'\n  Получено:  '%s'\n",
                        expectedPrize.name(), prize.getName()));
            }

            // Проверка типа
            if (expectedPrize.type().equals(prize.getType())) {
                result.setTypeMatches(true);
                validationDetails.append("✅ Тип совпадает\n");
            } else {
                result.setTypeMatches(false);
                allMatches = false;
                validationDetails.append(String.format("❌ Тип не совпадает:\n  Ожидался: '%s'\n  Получен:  '%s'\n",
                        expectedPrize.type(), prize.getType()));
            }

            // Проверка значения
            if (prize.getValue() >= expectedPrize.minValue() &&
                    prize.getValue() <= expectedPrize.maxValue()) {
                result.setValueMatches(true);
                validationDetails.append(String.format("✅ Значение в диапазоне: %d (ожидалось %d-%d)\n",
                        prize.getValue(), expectedPrize.minValue(), expectedPrize.maxValue()));
            } else {
                result.setValueMatches(false);
                allMatches = false;
                validationDetails.append(String.format("❌ Значение вне диапазона:\n  Получено: %d\n  Ожидалось: %d-%d\n",
                        prize.getValue(), expectedPrize.minValue(), expectedPrize.maxValue()));
            }

            // Устанавливаем статус
            if (allMatches) {
                result.setStatus(PrizeValidationResult.Status.FULL_MATCH);
                result.setMessage("Полное соответствие эталонным данным");
            } else {
                result.setStatus(PrizeValidationResult.Status.PARTIAL_MATCH);
                result.setMessage("Есть расхождения с эталонными данными");
            }

            result.setValidationDetails(validationDetails.toString());
        }

        // Генерируем отчет в Allure
        generatePrizeValidationReport(result, prize);

        return result;
    }

    // ================ ВАЛИДАЦИЯ ОШИБОК ================

    @Step("Проверка ответа без авторизации")
    public void validateUnauthorizedResponse(Response response) {
        int statusCode = response.getStatusCode();
        String responseBody = response.getBody().asString();

        Allure.addAttachment("Ответ без авторизации", "text/plain",
                String.format("Статус код: %d\nОжидается: 401\nТело ответа: %s",
                        statusCode, responseBody));

        assertAll("Проверка ответа без авторизации",
                () -> assertEquals(401, statusCode,
                        String.format("Должен возвращать 401 при отсутствии/невалидном токене. Получено: %d", statusCode)),
                () -> assertNotNull(responseBody, "Тело ответа не должно быть null"),
                () -> assertFalse(responseBody.isEmpty(), "Тело ответа не должно быть пустым")
        );
    }

    // ================ ОТЧЕТНОСТЬ ================

    @Step("Генерация детального отчета о выигранном призе")
    public void generateDetailedPrizeReport(GameWheelStartGameResponse prize) {
        StringBuilder report = new StringBuilder();
        report.append("=".repeat(70)).append("\n");
        report.append("🎡 ДЕТАЛЬНЫЙ ОТЧЕТ О ВЫИГРАННОМ ПРИЗЕ\n");
        report.append("=".repeat(70)).append("\n\n");

        // Основная информация
        report.append("📋 ОСНОВНАЯ ИНФОРМАЦИЯ:\n");
        report.append("-".repeat(40)).append("\n");
        report.append(String.format("🎁 ID приза:          %d\n", prize.getId()));
        report.append(String.format("📛 Название:          %s\n", prize.getName()));
        report.append(String.format("🏷️ Тип:               %s\n", prize.getType()));
        report.append(String.format("💰 Значение:          %d\n", prize.getValue()));
        report.append(String.format("🆔 ID награды:        %d\n", prize.getUserRewardId()));

        // Проверка по константам
        boolean isValidType = GameWheelRewardsConstants.ExpectedStartGamePrize.isValidType(prize.getType());
        boolean isValidValue = GameWheelRewardsConstants.ExpectedStartGamePrize.isValueValidForType(
                prize.getType(), prize.getValue());

        report.append(String.format("✅ Тип валиден:       %s\n", isValidType ? "Да" : "Нет"));
        report.append(String.format("✅ Значение валидно:  %s\n", isValidValue ? "Да" : "Нет"));

        // URL иконок
        report.append("\n🖼️ ИКОНКИ:\n");
        report.append("-".repeat(40)).append("\n");
        report.append(String.format("Маленькая иконка:    %s\n",
                prize.getIconSmallUrl() != null ? prize.getIconSmallUrl() : "Не указана"));
        report.append(String.format("Полная иконка:       %s\n",
                prize.getIconUrl() != null ? prize.getIconUrl() : "Не указана"));

        // Сравнение с эталоном
        GameWheelRewardsConstants.ExpectedStartGamePrize expectedPrize =
                GameWheelRewardsConstants.ExpectedStartGamePrize.getExpectedStartGamePrizeById(prize.getId());

        report.append("\n📊 СРАВНЕНИЕ С ЭТАЛОНОМ:\n");
        report.append("-".repeat(40)).append("\n");

        if (expectedPrize != null) {
            boolean nameMatch = expectedPrize.name().equals(prize.getName());
            boolean typeMatch = expectedPrize.type().equals(prize.getType());
            boolean valueInRange = prize.getValue() >= expectedPrize.minValue() &&
                    prize.getValue() <= expectedPrize.maxValue();

            report.append(String.format("📝 Название: %s (%s)\n",
                    nameMatch ? "✅ Совпадает" : "❌ Не совпадает",
                    nameMatch ? "OK" : String.format("ожидалось: %s", expectedPrize.name())));

            report.append(String.format("🏷️ Тип:      %s (%s)\n",
                    typeMatch ? "✅ Совпадает" : "❌ Не совпадает",
                    typeMatch ? "OK" : String.format("ожидался: %s", expectedPrize.type())));

            report.append(String.format("💰 Значение: %s (%s)\n",
                    valueInRange ? "✅ В диапазоне" : "❌ Вне диапазона",
                    valueInRange ? String.format("%d", prize.getValue()) :
                            String.format("%d, ожидалось %d-%d",
                                    prize.getValue(), expectedPrize.minValue(), expectedPrize.maxValue())));

            // Общая оценка
            if (nameMatch && typeMatch && valueInRange) {
                report.append("\n🎯 ОБЩАЯ ОЦЕНКА: ПОЛНОЕ СООТВЕТСТВИЕ ЭТАЛОНУ!\n");
            } else {
                report.append("\n⚠ ОБЩАЯ ОЦЕНКА: ЕСТЬ РАСХОЖДЕНИЯ С ЭТАЛОНОМ\n");
            }
        } else {
            report.append("🔍 Приз не найден в эталонных данных\n");
            report.append("Это может быть новый или нестандартный приз\n");
            report.append("Рекомендуется добавить его в константы\n");
        }

        report.append("\n").append("=".repeat(70)).append("\n");

        Allure.addAttachment("Детальный отчет о призе", "text/plain", report.toString());
    }

    @Step("Генерация краткого отчета о валидации")
    public void generateValidationSummary(PrizeValidationResult result, GameWheelStartGameResponse prize) {
        StringBuilder summary = new StringBuilder();
        summary.append("🎲 КРАТКИЙ ОТЧЕТ О ВАЛИДАЦИИ ПРИЗА\n");
        summary.append("-".repeat(50)).append("\n");

        summary.append(String.format("Приз: %s (ID: %d)\n", prize.getName(), prize.getId()));
        summary.append(String.format("Статус: %s\n", result.getStatus().getDisplayName()));

        if (result.isFound()) {
            summary.append(String.format("Тип: %s | Значение: %d\n", prize.getType(), prize.getValue()));

            if (result.isPerfectMatch()) {
                summary.append("\n✅ ВСЕ ПРОВЕРКИ ПРОЙДЕНЫ УСПЕШНО\n");
            } else {
                summary.append("\n⚠ НАЙДЕНЫ РАСХОЖДЕНИЯ:\n");
                if (!result.isNameMatches()) {
                    summary.append("• Название не совпадает\n");
                }
                if (!result.isTypeMatches()) {
                    summary.append("• Тип не совпадает\n");
                }
                if (!result.isValueMatches()) {
                    summary.append("• Значение вне диапазона\n");
                }
            }
        } else {
            summary.append("\n❓ ПРИЗ НЕ НАЙДЕН В ЭТАЛОННЫХ ДАННЫХ\n");
            summary.append("Требуется проверка и добавление в константы\n");
        }

        Allure.addAttachment("Краткий отчет", "text/plain", summary.toString());
    }

    // ================ ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ================

    private void generatePrizeValidationReport(PrizeValidationResult result, GameWheelStartGameResponse prize) {
        StringBuilder report = new StringBuilder();
        report.append("📋 ОТЧЕТ О ВАЛИДАЦИИ ПРИЗА\n");
        report.append("-".repeat(50)).append("\n");

        report.append(String.format("ID приза: %d\n", prize.getId()));
        report.append(String.format("Название: %s\n", prize.getName()));
        report.append(String.format("Тип: %s\n", prize.getType()));
        report.append(String.format("Значение: %d\n", prize.getValue()));
        report.append(String.format("userRewardId: %d\n", prize.getUserRewardId()));
        report.append(String.format("Статус: %s\n", result.getStatus().getDisplayName()));

        if (result.hasMessage()) {
            report.append(String.format("Сообщение: %s\n", result.getMessage()));
        }

        if (result.getValidationDetails() != null) {
            report.append("\nДетали валидации:\n");
            report.append(result.getValidationDetails());
        }

        Allure.addAttachment("Отчет о валидации приза", "text/plain", report.toString());
    }

    // ================ КЛАСС РЕЗУЛЬТАТА ВАЛИДАЦИИ ================

    public static class PrizeValidationResult {
        public enum Status {
            FULL_MATCH("Полное соответствие"),
            PARTIAL_MATCH("Частичное соответствие"),
            UNKNOWN("Неизвестный приз"),
            ERROR("Ошибка валидации");

            private final String displayName;

            Status(String displayName) {
                this.displayName = displayName;
            }

            public String getDisplayName() {
                return displayName;
            }
        }

        private final Long prizeId;
        private final String actualName;
        private boolean found;
        private String expectedName;
        private String expectedType;
        private Integer expectedMinValue;
        private Integer expectedMaxValue;
        private boolean nameMatches;
        private boolean typeMatches;
        private boolean valueMatches;
        private Status status;
        private String message;
        private String validationDetails;

        public PrizeValidationResult(Long prizeId, String actualName) {
            this.prizeId = prizeId;
            this.actualName = actualName;
            this.status = Status.UNKNOWN;
        }

        // Геттеры
        public Long getPrizeId() { return prizeId; }
        public String getActualName() { return actualName; }
        public boolean isFound() { return found; }
        public String getExpectedName() { return expectedName; }
        public String getExpectedType() { return expectedType; }
        public Integer getExpectedMinValue() { return expectedMinValue; }
        public Integer getExpectedMaxValue() { return expectedMaxValue; }
        public boolean isNameMatches() { return nameMatches; }
        public boolean isTypeMatches() { return typeMatches; }
        public boolean isValueMatches() { return valueMatches; }
        public Status getStatus() { return status; }
        public String getMessage() { return message; }
        public String getValidationDetails() { return validationDetails; }

        // Сеттеры
        public void setFound(boolean found) { this.found = found; }
        public void setExpectedName(String expectedName) { this.expectedName = expectedName; }
        public void setExpectedType(String expectedType) { this.expectedType = expectedType; }
        public void setExpectedMinValue(Integer expectedMinValue) { this.expectedMinValue = expectedMinValue; }
        public void setExpectedMaxValue(Integer expectedMaxValue) { this.expectedMaxValue = expectedMaxValue; }
        public void setNameMatches(boolean nameMatches) { this.nameMatches = nameMatches; }
        public void setTypeMatches(boolean typeMatches) { this.typeMatches = typeMatches; }
        public void setValueMatches(boolean valueMatches) { this.valueMatches = valueMatches; }
        public void setStatus(Status status) { this.status = status; }
        public void setMessage(String message) { this.message = message; }
        public void setValidationDetails(String validationDetails) { this.validationDetails = validationDetails; }

        public boolean hasMessage() {
            return message != null && !message.isEmpty();
        }

        public boolean isPerfectMatch() {
            return status == Status.FULL_MATCH || (nameMatches && typeMatches && valueMatches);
        }
    }

    // ================ МЕТОДЫ ДЛЯ МАССОВОГО ТЕСТИРОВАНИЯ ================

    @Step("Множественный запуск игры ({count} раз)")
    public List<GameWheelStartGameResponse> multipleStartGame(String authToken, int count) {
        List<GameWheelStartGameResponse> prizes = new ArrayList<>();
        Set<Long> userRewardIds = new HashSet<>();

        for (int i = 1; i <= count; i++) {
            Allure.addAttachment(String.format("Запуск #%d", i), "text/plain", "Начало запуска игры");

            Response response = startGame(authToken);
            GameWheelStartGameResponse prize = validateStartGameResponse(response);

            // Проверяем уникальность userRewardId
            assertFalse(userRewardIds.contains(prize.getUserRewardId()),
                    String.format("userRewardId %d уже встречался в предыдущих запусках",
                            prize.getUserRewardId()));

            userRewardIds.add(prize.getUserRewardId());
            prizes.add(prize);

            // Генерируем краткий отчет для каждого приза
            generateValidationSummary(validateAgainstExpectedValues(prize), prize);

            Allure.addAttachment(String.format("Приз #%d", i), "text/plain",
                    String.format("ID: %d, Название: %s, userRewardId: %d",
                            prize.getId(), prize.getName(), prize.getUserRewardId()));
        }

        // Сводный отчет по всем запускам
        generateMultipleGamesSummary(prizes);

        return prizes;
    }

    @Step("Генерация сводного отчета по множественным запускам")
    private void generateMultipleGamesSummary(List<GameWheelStartGameResponse> prizes) {
        if (prizes.isEmpty()) return;

        StringBuilder summary = new StringBuilder();
        summary.append("📊 СВОДНЫЙ ОТЧЕТ ПО ").append(prizes.size()).append(" ЗАПУСКАМ\n");
        summary.append("=".repeat(60)).append("\n\n");

        // Статистика по типам призов
        Map<String, Integer> typeStats = new HashMap<>();
        Map<Long, Integer> prizeIdStats = new HashMap<>();

        for (GameWheelStartGameResponse prize : prizes) {
            typeStats.merge(prize.getType(), 1, Integer::sum);
            prizeIdStats.merge(prize.getId(), 1, Integer::sum);
        }

        summary.append("СТАТИСТИКА ПО ТИПАМ ПРИЗОВ:\n");
        summary.append("-".repeat(40)).append("\n");
        typeStats.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry -> {
                    double percentage = (entry.getValue() * 100.0) / prizes.size();
                    summary.append(String.format("%-15s: %d раз (%.1f%%)\n",
                            entry.getKey(), entry.getValue(), percentage));
                });

        summary.append("\nСТАТИСТИКА ПО КОНКРЕТНЫМ ПРИЗАМ:\n");
        summary.append("-".repeat(40)).append("\n");
        prizeIdStats.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(10) // Топ-10 самых частых призов
                .forEach(entry -> {
                    // Получаем название приза
                    String prizeName = prizes.stream()
                            .filter(p -> p.getId().equals(entry.getKey()))
                            .findFirst()
                            .map(GameWheelStartGameResponse::getName)
                            .orElse("Неизвестно");

                    double percentage = (entry.getValue() * 100.0) / prizes.size();
                    summary.append(String.format("ID %-4d: %-30s - %d раз (%.1f%%)\n",
                            entry.getKey(),
                            prizeName.length() > 30 ? prizeName.substring(0, 27) + "..." : prizeName,
                            entry.getValue(), percentage));
                });

        // Проверка уникальности userRewardId
        Set<Long> uniqueUserRewardIds = prizes.stream()
                .map(GameWheelStartGameResponse::getUserRewardId)
                .collect(Collectors.toSet());

        summary.append("\nПРОВЕРКА УНИКАЛЬНОСТИ:\n");
        summary.append("-".repeat(40)).append("\n");
        summary.append(String.format("Всего userRewardId: %d\n", prizes.size()));
        summary.append(String.format("Уникальных: %d\n", uniqueUserRewardIds.size()));
        summary.append(String.format("Дубликатов: %d\n", prizes.size() - uniqueUserRewardIds.size()));

        if (uniqueUserRewardIds.size() == prizes.size()) {
            summary.append("✅ Все userRewardId уникальны\n");
        } else {
            summary.append("⚠ Обнаружены дубликаты userRewardId!\n");
        }

        Allure.addAttachment("Сводный отчет по запускам", "text/plain", summary.toString());
    }
}
