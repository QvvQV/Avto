package ru.tutor.steps.fortune;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import ru.tutor.api.fortune.GameWheelApi;
import io.restassured.response.Response;
import ru.tutor.base.BaseApiSteps;
import ru.tutor.constants.GameWheelRewardsConstants;
import ru.tutor.model.fortune.GameWheelStartGameResponse;
import ru.tutor.model.fortune.UserRewardResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.stream.Collectors;

public class UserRewardSteps extends BaseApiSteps {
    private final GameWheelApi gameWheelApi;

    public UserRewardSteps(String baseUrl) {
        super(baseUrl);
        this.gameWheelApi = new GameWheelApi(baseUrl);
    }

    private static final DateTimeFormatter ISO_FORMATTER =
            DateTimeFormatter.ISO_DATE;

    @Step("GET /user/reward/list - получение списка наград пользователя")
    public Response getUserRewards(String authToken) {
        return this.gameWheelApi.getUserRewards(authToken);
    }

    @Step("GET /user/reward/history - получение истории забранных наград пользователя")
    public Response getUserRewardsHistory(String authToken) {
        return this.gameWheelApi.getUserRewardHistory(authToken);
    }

    @Step("Валидация успешного ответа со списком наград пользователя")
    public List<UserRewardResponse> validateUserRewardsResponse(Response response) {
        // Базовая валидация ответа
        assertSuccessStatusCode(response);
        assertContentTypeJson(response);

        // Проверяем что ответ массив
        Object body = response.jsonPath().get("$");
        assertNotNull(body, "Тело ответа не должно быть null");
        assertInstanceOf(List.class, body, "Ответ должен быть списком (List)");

        // Преобразуем в список модели
        List<UserRewardResponse> userRewards = response.jsonPath()
                .getList("$", UserRewardResponse.class);

        assertNotNull(userRewards, "Список наград пользователя не должен быть null");

        // Может быть пустым, если у пользователя нет наград
        Allure.addAttachment("Количество наград пользователя", "text/plain",
                String.valueOf(userRewards.size()));

        return userRewards;
    }
    @Step("POST /user/reward/opеn/{id} - удаление подарка из списка призов")
    public Response openReward(String authToken, Long rewardId) {
                return this.gameWheelApi.openReward(authToken, rewardId);
            }

    @Step("Валидация структуры каждой награды")
    public void validateUserRewardStructure(List<UserRewardResponse> userRewards) {
        for (int i = 0; i < userRewards.size(); i++) {
            UserRewardResponse reward = userRewards.get(i);
            String rewardInfo = "Награда пользователя #" + (i + 1) + " (ID: " + reward.id() + ")";

            assertAll(rewardInfo + " - проверка обязательных полей",
                    () -> assertNotNull(reward.id(), "id не должен быть null"),
                    () -> assertNotNull(reward.name(), "name не должен быть null"),
                    () -> assertFalse(reward.name().trim().isEmpty(), "name не должен быть пустым"),
                    () -> assertNotNull(reward.rewardValue(), "rewardValue не должен быть null"),
                    () -> assertNotNull(reward.rewardType(), "rewardType не должен быть null"),
                    () -> assertNotNull(reward.expiresAt(), "expiresAt не должен быть null"),
                    () -> assertNotNull(reward.isShowValue(), "isShowValue не должен быть null")
            );

            // Дополнительные проверки
            assertTrue(reward.id() > 0, "id должен быть больше 0");
            assertTrue(reward.rewardValue() > 0, "rewardValue должен быть больше 0");

            // Проверка типа наград
            assertTrue(Arrays.asList(GameWheelRewardsConstants.ALLOWED_TYPES).contains(reward.rewardType()),
                    rewardInfo + " - rewardType should be allowed: " +
                            Arrays.toString(GameWheelRewardsConstants.ALLOWED_TYPES));

            // Проверка даты истечения (формат YYYY-MM-DD)
            validateDate(reward.expiresAt(), rewardInfo);
        }
    }

    @Step("Проверка формата даты")
    private void validateDate(String dateStr, String rewardInfo) {
        try {
            LocalDate expiresAt = LocalDate.parse(dateStr);
            assertTrue(expiresAt.isAfter(LocalDate.now().minusDays(1)) ||
                            expiresAt.isEqual(LocalDate.now()),
                    rewardInfo + " - expiresAt должна быть сегодня или в будущем: " + dateStr);
        } catch (DateTimeParseException e) {
            fail(rewardInfo + " - Неверный формат даты: " + dateStr + ". Ожидается YYYY-MM-DD");
        }
    }

    @Step("Проверка, что полученная награда появилась в списке пользователя")
    public void verifyRewardAppearedInUserList(GameWheelStartGameResponse startGamePrize,
                                               List<UserRewardResponse> userRewards) {
        if (!startGamePrize.isSuccess()) {
            Allure.addAttachment("Пропуск проверки", "text/plain",
                    "Нельзя проверять неуспешный запуск игры");
            return;
        }

        if (userRewards == null) {
            fail("список наград пользователя не должен быть null");
            return;
        }

        Long expectedUserRewardId = startGamePrize.getUserRewardId();
        String expectedName = startGamePrize.getName();
        String expectedType = startGamePrize.getType();
        Integer expectedValue = startGamePrize.getValue();

        // Ищем награду в списке
        Optional<UserRewardResponse> foundReward = userRewards.stream()
                .filter(reward -> reward.id().equals(expectedUserRewardId))
                .findFirst();

        if (foundReward.isPresent()) {
            UserRewardResponse actualReward = foundReward.get();

            assertAll("Проверка соответсвия награды в списке пользователя",
                    () -> assertEquals(expectedUserRewardId, actualReward.id(),
                            "ID награды должно совпадать"),
                    () -> assertEquals(expectedName, actualReward.name(),
                            "Name награды должно совпадать"),
                    () -> assertEquals(expectedType, actualReward.rewardType(),
                            "Тип награды должен совпадать"),
                    () -> assertEquals(expectedValue, actualReward.rewardValue(),
                            "Значение награды должно совпадать")
            );

            Allure.addAttachment("Награда найдена в списке пользователя", "text/plain," +
                    String.format("""
                                    ✅ Награда ID %d успешно добавлена в список пользователя
                                    Название: %s
                                    Тип: %s
                                    Значение: %d""",
                            expectedUserRewardId, expectedName, expectedType, expectedValue));
        } else {
            fail(String.format("Награда с ID %d не найдена в списке пользователя. \" +\n" +
                            "                              \"Получено в start-game: %s, Тип: %s, Значение: %d",
                    expectedUserRewardId, expectedName, expectedType, expectedValue));
        }
    }

    @Step("Проверка уникальности ID наград в списке")
    public void verifyUniqueRewards(List<UserRewardResponse> userRewards) {
        if (userRewards == null) {
            Allure.addAttachment("Проверка уникальности", "text/plain",
                    "Список наград null, проверка пропущена");
            return;
        }

        Set<Long> uniqueIds = new HashSet<>();
        List<Long> duplicatedIds = new ArrayList<>();
        for (UserRewardResponse reward : userRewards) {
            if (!uniqueIds.add(reward.id())) {
                duplicatedIds.add(reward.id());
            }
        }

        assertTrue(duplicatedIds.isEmpty(),
                "Найдены дубликаты ID наград: " + duplicatedIds);

        Allure.addAttachment("Уникальность ID", "text/plain",
                String.format("Всего наград: %d\nУникальных ID: %d\nДубликатов: %d",
                        userRewards.size(), uniqueIds.size(), 0));
    }

    @Step("Генерация отчёта о наградах")
    public void generateUserRewardsReport(List<UserRewardResponse> userRewards) {
        if (userRewards.isEmpty()) {
            Allure.addAttachment("Отчёт о наградах", "text/plain",
                    "У пользователя нет наград");
            return;
        }

        StringBuilder report = new StringBuilder();

        report.append("🎁 ОТЧЕТ О НАГРАДАХ ПОЛЬЗОВАТЕЛЯ\n");
        report.repeat("=", 70).append("\n\n");

        // Статистика по типам наград
        Map<String, Long> typeStatistics = userRewards.stream()
                .collect(Collectors.groupingBy(UserRewardResponse::rewardType, Collectors.counting()));

        Map<String, Integer> valueStatistics = userRewards.stream()
                .collect(Collectors.groupingBy(UserRewardResponse::rewardType,
                        Collectors.summingInt(UserRewardResponse::rewardValue)));

        report.append("📊 СТАТИСТИКА ПО ТИПАМ:\n");
        report.repeat("-", 40).append("\n");
        typeStatistics.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(entry -> {
                    String type = entry.getKey();
                    Long count = entry.getValue();
                    Integer totalValue = valueStatistics.getOrDefault(type, 0);
                    report.append(String.format("%-15s: %d шт., всего: %d\n",
                            type, count, totalValue));
                });

        // Сводная таблица всех наград
        report.append("\n📋 СПИСОК ВСЕХ НАГРАД:\n");
        report.repeat("-", 90).append("\n");
        report.append(String.format("%-8s | %-30s | %-12s | %-10s | %-12s | %s\n",
                "ID", "Название", "Тип", "Значение", "Истекает", "Показ. знач."));
        report.repeat("-", 90).append("\n");

        userRewards.stream()
                .sorted(Comparator.comparing(UserRewardResponse::id).reversed())
                .forEach(reward -> {
                    String name = reward.name();
                    if (name != null && name.length() > 30) {
                        name = name.substring(0, 27) + "...";
                    }

                    String type = reward.rewardType() != null ? reward.rewardType() : "";
                    String expiresAt = reward.expiresAt() != null ? reward.expiresAt() : "";
                    String showValue = reward.isShowValue() != null ?
                            (reward.isShowValue() ? "Да" : "Нет") : "";

                    report.append(String.format("%-8d | %-30s | %-12s | %-10d | %-12s | %s\n",
                            reward.id(),
                            name != null ? name : "",
                            type,
                            reward.rewardValue() != null ? reward.rewardValue() : 0,
                            expiresAt,
                            showValue));
                });

        report.repeat("-", 90).append("\n");
        report.append(String.format("Всего наград: %d\n", userRewards.size()));

        // Информация о ближайших истечениях
        List<UserRewardResponse> sortedByExpiry = userRewards.stream()
                .filter(r -> r.expiresAt() != null)
                .sorted((r1, r2) -> {
                    try {
                        LocalDate date1 = LocalDate.parse(r1.expiresAt());
                        LocalDate date2 = LocalDate.parse(r2.expiresAt());
                        return date1.compareTo(date2);
                    } catch (Exception e) {
                        return 0;
                    }
                })
                .limit(5)
                .toList();

        Allure.addAttachment("Детальный отчет о наградах", "text/plain", report.toString());
    }

    @Step("Проверка целостности данных (после запуска игры)")
    public IntegrationTestResult performIntegrationTest(String authToken, GameWheelStartGameSteps startGameSteps) {
        IntegrationTestResult result = new IntegrationTestResult();

        // 1. Получаем текущий список наград пользователя (до запуска)
        Response beforeResponse = getUserRewards(authToken);
        List<UserRewardResponse> beforeRewards = validateUserRewardsResponse(beforeResponse);
        result.setRewardsBefore(beforeRewards);

        // 2. Запускаем игру
        Response startGameResponse = startGameSteps.startGame(authToken);
        GameWheelStartGameResponse prize = startGameSteps.validateStartGameResponse(startGameResponse);
        result.setStartGamePrize(prize); // Сохраняем приз, а не steps

        if (prize.isSuccess()) {
            // 3. Получаем обновленный список наград пользователя (после запуска)
            Response afterResponse = getUserRewards(authToken);
            List<UserRewardResponse> afterRewards = validateUserRewardsResponse(afterResponse);
            result.setRewardsAfter(afterRewards);

            // 4. Проверяем, что награда добавилась
            verifyRewardAppearedInUserList(prize, afterRewards);

            // 5. Сравниваем количество наград
            assertEquals(beforeRewards.size() + 1, afterRewards.size(),
                    "Количество наград должно увеличиться на 1 после успешного запуска");

            result.setSuccess(true);
            result.setMessage("Интеграционный тест пройден успешно");
        } else {
            result.setSuccess(false);
            result.setMessage("Не удалось получить приз: " + prize.getErrorMessage());
        }

        return result;
    }

    // ================ ВСПОМОГАТЕЛЬНЫЕ КЛАССЫ ================

    public static class IntegrationTestResult {
        private boolean success;
        private String message;
        private GameWheelStartGameResponse startGamePrize; // Исправлено: GameWheelStartGameResponse
        private List<UserRewardResponse> rewardsBefore;
        private List<UserRewardResponse> rewardsAfter;

        // Геттеры и сеттеры
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public GameWheelStartGameResponse getStartGamePrize() { return startGamePrize; }
        public void setStartGamePrize(GameWheelStartGameResponse startGamePrize) { this.startGamePrize = startGamePrize; } // Исправлено

        public List<UserRewardResponse> getRewardsBefore() { return rewardsBefore; }
        public void setRewardsBefore(List<UserRewardResponse> rewardsBefore) { this.rewardsBefore = rewardsBefore; }

        public List<UserRewardResponse> getRewardsAfter() { return rewardsAfter; }
        public void setRewardsAfter(List<UserRewardResponse> rewardsAfter) { this.rewardsAfter = rewardsAfter; }
    }

    @Step("Найти подходящую награду для открытия")
    public Optional<UserRewardResponse> findSuitableRewardToOpen(List<UserRewardResponse> rewards) {
        return rewards.stream()
                .filter(Objects::nonNull)
                .filter(reward -> reward.id() != null)
                .filter(reward -> !isValuationReward(reward))
                .filter(this::isRewardNotExpired)
                .findFirst();
    }

    @Step("Найти награду типа valuation")
    public Optional<UserRewardResponse> findValuationReward(List<UserRewardResponse> rewards) {
        return rewards.stream()
                .filter(Objects::nonNull)
                .filter(this::isValuationReward)
                .findFirst();
    }

    private boolean isValuationReward(UserRewardResponse reward) {
        return "valuation".equals(reward.rewardType());
    }

    private boolean isRewardNotExpired(UserRewardResponse reward) {
        if (reward.expiresAt() == null || reward.expiresAt().isEmpty()) {
            return true;
        }

        try {
            LocalDateTime expiryDate = LocalDateTime.parse(reward.expiresAt(), ISO_FORMATTER);
            LocalDateTime now = LocalDateTime.now();
            return expiryDate.isAfter(now);
        } catch (DateTimeParseException e) {
            return true;
        }
    }

    private boolean isRewardExpired(UserRewardResponse reward) {
        String expiredAt = reward.expiresAt();

        if (expiredAt == null || expiredAt.isEmpty()) {
            return false;
        }

        try {
            LocalDateTime expiryDate = LocalDateTime.parse(expiredAt, ISO_FORMATTER);
            LocalDateTime now = LocalDateTime.now();
            return expiryDate.isBefore(now);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    @Step("Валидация ошибки `valuation`")
    public void validateOpenRewardRejected(Response response, long rewardId) {
        int statusCode = response.statusCode();
        assertTrue(statusCode !=200,
                "Для valuation награды должен возвращаться 4хх статус код. Получен: " + statusCode);

        Allure.addAttachment("Результат", "text/plain",
                String.format("""
                        ✅ Сервер корректно отклонил запрос
                        🎁 ID valuation награды: %d
                        📊 Статус код: %d
                        📝 Ответ сервера: %s,
                        """,
                        rewardId,
                        statusCode,
                        response.getBody().asString()));
    }

    @Step("Найти или создать награду для открытия")
    public Long findOrCreateRewardToOpen(String authToken) {
        Response response = getUserRewards(authToken);
        List<UserRewardResponse> userRewards = validateUserRewardsResponse(response);

        Optional<UserRewardResponse> rewardToOpen = findSuitableRewardToOpen(userRewards);
        if (rewardToOpen.isPresent()) {
            return rewardToOpen.get().id();
        }

        // Нет подходящего - крутим колесо
        GameWheelStartGameSteps startGameSteps = new GameWheelStartGameSteps(baseUrl);
        Response spinResponse = startGameSteps.startGame(authToken);
        startGameSteps.validateStartGameResponse(spinResponse);

        Response updatedResponse = getUserRewards(authToken);
        List<UserRewardResponse> updatedRewards = validateUserRewardsResponse(updatedResponse);

        return updatedRewards.stream()
                .filter(reward -> !"valuation".equals(reward.rewardType()))
                .filter(reward -> reward.id() != null)
                .max(Comparator.comparingLong(UserRewardResponse::id))
                .map(UserRewardResponse::id)
                .orElseThrow(() -> new RuntimeException("Не удалось получить новую награду"));
    }

    @Step("Полная валидация списка наград пользователя")
    public List<UserRewardResponse> validateUserRewardsListComplete(Response response) {
        List<UserRewardResponse> userRewards = validateUserRewardsResponse(response);

        if (!userRewards.isEmpty()) {
            validateUserRewardStructure(userRewards);
            verifyUniqueRewards(userRewards);
            generateUserRewardsReport(userRewards);
        }
        return userRewards;
    }

    @Step("Найти новую награду в обновлённом списке")
    public UserRewardResponse findNewReward(List<UserRewardResponse> before, List<UserRewardResponse> after) {
        return after.stream()
                .filter(reward -> before.stream()
                        .noneMatch(initial -> initial.id().equals(reward.id())))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Новая награда не найдена в списке"));
    }

    @Step("Валдиация успешного открытия награды")
    public void validateOpenRewardSuccess(Response response, long rewardId) {
        assertEquals(200, response.getStatusCode(),
                "Статус код должен быть 200");

        Allure.addAttachment("Награда открыта", "text/plain",
                String.format("ID награды: %d, статус: 200 ОК", rewardId));
    }

    @Step("Генерация итогового отчёта Е2Е")
    public void generateE2EReport(int initialCount, int afterSpinCount, int finalCount, UserRewardResponse newReward) {
        Allure.addAttachment("ИТОГИ Е2Е ТЕСТА", "text/plain",
                String.format("""
                        Статистика:
                        • До теста: %d                                                                                                             \s
                        • После спина: %d (+1)                                                                                                     \s
                        • После открытия: %d
                        
                        Награда:
                        • ID: %d                                                                                                                   \s
                        • Название: %s                                                                                                             \s
                        • Тип: %s
                        • Значение: %d
                        """,
                        initialCount, afterSpinCount, finalCount,
                        newReward.id(), newReward.name(),
                        newReward.rewardType(), newReward.rewardValue()));
    }

    @Step("Валидация результата интеграционного теста")
    public void validateIntegrationTestResult(IntegrationTestResult result) {
        if (result.isSuccess()) {
            GameWheelStartGameResponse prize = result.getStartGamePrize();
            List<UserRewardResponse> before = result.getRewardsBefore();
            List<UserRewardResponse> after = result.getRewardsAfter();

            Allure.addAttachment("Интеграционный тест пройден", "text/plain",
                    String.format("""
                            Приз: %s (ID: %d)
                            Наград до: %d, после: %d
                            """,
                            prize.getName(), prize.getUserRewardId(),
                            before.size(), after.size()));

            assertEquals(before.size() + 1, after.size(),
                    "Количество наград должно увеличиться на 1");
            return;
        }

        // Не удалось получить приз - допустимо, если нет средств
        if (result.getStartGamePrize() != null && !result.getStartGamePrize().isSuccess()) {
            Allure.addAttachment("Причина пропуска", "text/plain",
                    "Не удалось получить приз: " + result.getStartGamePrize().getErrorMessage());
            return;
        }

        fail("интеграционный тест не пройден: " + result.getMessage());
    }
}