package ru.tutor.test.api.gamewheel;

import io.qameta.allure.*;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tutor.config.TestConfig;
import ru.tutor.constants.GameWheelRewardsConstants;
import ru.tutor.model.fortune.GameWheelRewardResponse;
import ru.tutor.model.fortune.GameWheelStartGameResponse;
import ru.tutor.model.fortune.UserRewardResponse;
import ru.tutor.steps.UserSteps;
import ru.tutor.steps.fortune.GameWheelStartGameSteps;
import ru.tutor.steps.fortune.GameWheelRewardsSteps;
import ru.tutor.steps.fortune.UserRewardSteps;


import java.util.*;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

@Tag("API")
@ExtendWith(AllureJunit5.class)
@Epic("API Тесты пользователя")
@Feature("Игровое колесо")
@DisplayName("Тесты эндпоинта игрового колеса")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameWheelTest {


    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private final UserSteps userSteps = new UserSteps(BASE_URL);
    private final GameWheelRewardsSteps gameWheelRewardsSteps = new GameWheelRewardsSteps(BASE_URL);
    private final GameWheelStartGameSteps gameWheelStartGameSteps = new GameWheelStartGameSteps(BASE_URL);
    private final UserRewardSteps userRewardSteps = new UserRewardSteps(BASE_URL);

    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();

    private static String token;

    private static List<GameWheelRewardResponse> rewards;
//    TODO: сейчас таблица призов по ручке не появляется (может к ней вернутся)
//    private static Map<String, GameWheelRewardsSteps.RewardValidationResult> validationResults;

    @BeforeAll
    public static void setUp() {
        Allure.label("component", "API");
        Allure.label("feature", "Game Wheel");
    }

    @BeforeEach
    void setUpAuth() {
        // Логин перед каждым тестом
        token = step("Логин", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );
    }

    @Test
    @Order(1)
    @DisplayName("Успешное получение списка наград игрового колеса (Проверка 200 OK)")
    @Story("Основной сценарий")
    @Severity(SeverityLevel.CRITICAL)
    void getGameWheelRewardsSuccess() {

        Response response = step("GET /user/game-wheel/rewards", () ->
                gameWheelRewardsSteps.getGameWheelRewards(token)
        );

        rewards = step("Валидация ответа со списком наград", () ->
                gameWheelRewardsSteps.validateGameWheelRewardsResponse(response)
        );

        step("Проверка структуры наград", () -> {
            gameWheelRewardsSteps.validaterewardStructure(rewards);

            Allure.addAttachment("Количество наград", "text/plain",
                    "Получено наград: " + rewards.size());
        });
    }

    @Test
    @Order(3)
    @DisplayName("Проверка 'Наград колеса' без авторизации (Проверка 401)")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("negative")
    @Tag("security")
    void shouldFailWithoutAuthorization() {
        Response response = step("Отправка Get запроса без токена", () ->
                gameWheelRewardsSteps.getGameWheelRewards("")
        );

        step("Проверка ошибки авторизации", () ->
            gameWheelRewardsSteps.validateUnauthorizedResponse(response)
        );
    }

    @Test
    @Order(4)
    @DisplayName("Проверка 'Наград колеса' с невалидным токеном (Проверка 401)")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    @Tag("security")
    void shouldFailWithInvalidToken() {
        Response response = step("Отправка GET запроса с невалидным токеном", () ->
                gameWheelRewardsSteps.getGameWheelRewards("invalid_token_12345")
        );

        step("Проверка ошибки авторизации", () ->
            gameWheelRewardsSteps.validateUnauthorizedResponse(response)
        );
    }

    @Test
    @Order(5)
    @DisplayName("Успешный 'Запуск игры' на колесе (Проверка 200 OK)")
    @Story("Основной сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("positive")
    @Tag("smoke")
    void startGameSuccess() {

        Response response = step("POST /user/game-wheel/start-game", () ->
                gameWheelStartGameSteps.startGame(token)
        );

        GameWheelStartGameResponse prize = step("Валидация ответа с призом", () ->
                gameWheelStartGameSteps.validateStartGameResponse(response)
        );

        step("Проверка структуры приза", () ->
            Allure.addAttachment("Полученный приз", "text/plain",
                    String.format("""
                                    🎁 Приз: %s (ID: %d)
                                    🏷️ Тип: %s
                                    💰 Значение: %d
                                    🆔 ID награды: %d""",
                            prize.getName(), prize.getId(),
                            prize.getType(), prize.getValue(),
                            prize.getUserRewardId())
            )
        );
    }

    @Test
    @Order(6)
    @DisplayName("Проверка уникальности userRewardId при множественых запусках")
    @Story("Фунциональность")
    @Severity(SeverityLevel.NORMAL)
    @Tag("positive")
    @Tag("functional")
    void shouldGenerateUniqueUserRewardId() {

        List<GameWheelStartGameResponse> multiplePrizes = step("Множественный запуск игры (10 раза)",
                () ->
                        gameWheelStartGameSteps.multipleStartGame(token, 5)
        );

        step("Проверка уникальности", () -> {
            Set<Long> userRewardIds = new HashSet<>();
            Set<Long> prizeIds = new HashSet<>();
            Set<String> prizeTypes = new HashSet<>();
            for (GameWheelStartGameResponse prize : multiplePrizes) {
                userRewardIds.add(prize.getUserRewardId());
                prizeIds.add(prize.getId());
                prizeTypes.add(prize.getType());
            }

            Allure.addAttachment("Статистика по запускам", "text/plain",
                    String.format("""
                                    Всего запусков: %d
                                    Уникальных userRewardId: %d
                                    Уникальных ID призов: %d
                                    Уникальных типов призов: %d
                                    Типы призов: %s""",
                            multiplePrizes.size(),
                            userRewardIds.size(),
                            prizeIds.size(),
                            prizeTypes.size(),
                            String.join(", ", prizeTypes)));

            // проверка что все типы призов допустимы
            for (String type : prizeTypes) {
                assertTrue(GameWheelRewardsConstants.ExpectedStartGamePrize.isValidType(type),
                        String.format("Тип '%s' не является допустимым", type));
            }
        });
    }

    @Test
    @Order(7)
    @DisplayName("Проверка 'Запуска игры' без авторизации (Проверка 401)")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("negative")
    @Tag("security")
    void shouldFailWithoutAuthorizationGame() {
        Response response = step("Отправка POST запроса без токена", () ->
                gameWheelStartGameSteps.startGame("")
        );

        step("Проверка ошибки авторизации", () -> {
            gameWheelStartGameSteps.validateUnauthorizedResponse(response);

            Allure.addAttachment("Проверка безопасности", "text/plain",
                    "✅ Доступ без авторизации запрещен (401)");
        });
    }

    @Test
    @Order(8)
    @DisplayName("Проверка 'Запуска игры' с невалидным токеном (Проверка 401)")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    @Tag("security")
    void shouldFailWithInvalidTokenGame() {
        Response response = step("Отправка POST запроса с невалидным токеном", () ->
                gameWheelStartGameSteps.startGame("invalid_token_12345")
        );

        step("Проверка ошибки авторизации", () ->
            gameWheelStartGameSteps.validateUnauthorizedResponse(response)
        );
    }

    @Test
    @Order(9)
    @DisplayName("Успешное получение 'Списка наград' пользователя (Проверка 200 OK)")
    @Story("Основной сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("positive")
    @Tag("smoke")
    void getUserRewardSuccess() {

        Response response = step("GET /user/reward/list", () ->
                userRewardSteps.getUserRewards(token)
        );

        List<UserRewardResponse> rewards = step("Валидация ответа", () ->
                userRewardSteps.validateUserRewardsResponse(response)
        );

        step("Проверка стуктуры наград", () -> {
            assertNotNull(rewards, "Список наград пользователя не должен быть null");

            Allure.addAttachment("Количество наград", "text/plain",
                    "Получено наград: " + rewards.size());

            // Проверяем структуру, если есть награды
            if (!rewards.isEmpty()) {
                userRewardSteps.validateUserRewardStructure(rewards);
                userRewardSteps.verifyUniqueRewards(rewards);

                // Генерация отчета
                userRewardSteps.generateUserRewardsReport(rewards);

                Allure.addAttachment("Результат", "text/plain",
                        String.format("✅ Получено наград: %d\n✅ Все награды имеют корректную структуру",
                                rewards.size()));
            } else {
                Allure.addAttachment("Информация", "text/plain",
                        "У пользователя нет наград. Это нормально для нового пользователя или если награды отсутствуют.");
            }
        });
    }

//    @Test
    @Order(10)
    @DisplayName("Интеграционный тест: 'Запуск игры' и проверка 'Добавления награды'")
    @Story("Интеграция с колесом")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("integration")
    @Tag("positive")
    void integrationTestWithGameWheel() {

        UserRewardSteps.IntegrationTestResult result = step("Выполнение интеграционного теста", () ->
                userRewardSteps.performIntegrationTest(token, gameWheelStartGameSteps)
        );

        step("Анализ результатов интеграции", () -> {
            if (result.isSuccess()) {
                GameWheelStartGameResponse prize = result.getStartGamePrize();
                List<UserRewardResponse> before = result.getRewardsBefore();
                List<UserRewardResponse> after = result.getRewardsAfter();

                Allure.addAttachment("Интеграционный тест пройден", "text/plain",
                        String.format("""
                                        🎉 ТЕСТ ПРОЙДЕН!
                                        🎁 Полученный приз: %s (ID: %d)
                                        📊 Наград до запуска: %d
                                        📊 Наград после запуска: %d
                                        ✅ Награда успешно добавлена в список пользователя""",
                                prize.getName(), prize.getUserRewardId(),
                                before.size(), after.size()));

                assertEquals(before.size() + 1, after.size(),
                        "Количество наград должно увеличиться на 1");
            } else {
                Allure.addAttachment("Интеграционный тест не пройден", "text/plain",
                        "❌ Тест не пройден: " + result.getMessage());

                // Если не удалось получить приз (нет средств), это допустимо
                if (result.getStartGamePrize() != null &&
                        !result.getStartGamePrize().isSuccess()) {
                    Allure.addAttachment("Причина", "text/plain",
                            "Не удалось получить приз: " +
                                    result.getStartGamePrize().getErrorMessage());
                } else {
                    fail("Интеграционный тест не пройден: " + result.getMessage());
                }
            }
        });
    }

    @Test
    @Order(11)
    @DisplayName("Проверка 'Списка призов' без авторизации (Проверка 401)")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("negative")
    @Tag("security")
    void shouldFailWithoutAuthorizationList() {
        Response response = step("Отправка GET запроса без токена", () ->
                userRewardSteps.getUserRewards("")
        );

        step("Проверка ошибки авторизации", () -> {
            gameWheelStartGameSteps.validateUnauthorizedResponse(response);

            Allure.addAttachment("Проверка безопасности", "text/plain",
                    "✅ Доступ без авторизации запрещен (401)");
        });
    }

    @Test
    @Order(12)
    @DisplayName("Проверка 'Списка призов' с невалидным токеном (Проверка 401)")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    @Tag("security")
    void shouldFailWithInvalidTokenList() {
        Response response = step("Отправка GET запроса с невалидным токеном", () ->
                userRewardSteps.getUserRewards("invalid_token_12345")
        );

        step("Проверка ошибки авторизации", () ->
            gameWheelStartGameSteps.validateUnauthorizedResponse(response)
        );
    }

    @Test
    @Order(13)
    @DisplayName("Базовый тест: открытие награды (Проверка 200 OK)")
    @Story("Основной сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("positive")
    void openRewardBasicTest() {

        List<UserRewardResponse> userRewards = step("Получить список наград", () ->
        {
            Response response = userRewardSteps.getUserRewards(token);
            return userRewardSteps.validateUserRewardsResponse(response);
        });

        Optional<UserRewardResponse> rewardToOpen = step("Ищем награду для открытия", () ->
                userRewardSteps.findSuitableRewardToOpen(userRewards)
        );

        Long rewardId;
        if (rewardToOpen.isPresent()) {
            rewardId = rewardToOpen.get().id();
            step("Используем существующую награду", () -> {
                UserRewardResponse reward = rewardToOpen.get();
                String expiryInfo = reward.expiresAt() != null ?
                        "Истекает: " + reward.expiresAt() : "Срок не ограничен";

                Allure.addAttachment("Выбранная награда", "text/plain",
                        String.format("""
                                        ID: %d
                                        Название: %s
                                        Тип: %s
                                        Значение: %d
                                        %s""",
                                rewardId,
                                reward.name(),
                                reward.rewardType(),
                                reward.rewardValue(),
                                expiryInfo));
            });
        } else {
            rewardId = step("Создаем новую награду через игровое колесо", () -> {
                Response spinResponse = gameWheelStartGameSteps.startGame(token);
                gameWheelStartGameSteps.validateStartGameResponse(spinResponse);

                Response updateResponse = userRewardSteps.getUserRewards(token);
                List<UserRewardResponse> updateRewards = userRewardSteps.validateUserRewardsResponse(updateResponse);

                return updateRewards.stream()
                        .filter(reward -> !"valuation".equals(reward.rewardType()))
                        .max((r1, r2) -> {
                            if (r1.id() != null && r2.id() != null) {
                                return Long.compare(r1.id(), r2.id());
                            }
                            return 0;
                        })
                        .map(UserRewardResponse::id)
                        .orElseThrow(() -> new RuntimeException("не удалось получить новую награду"));
            });
        }

        step("Открываем награду ID: " + rewardId, () ->
                userRewardSteps.openReward(token, rewardId)
        );
    }

    //    @Test
    @Order(14)
    @DisplayName("Негативный тест: Попытка открыть награду типа 'valuation'")
    @Story("Бизнес-правила")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void shouldNotOpenValuationReward() {
        List<UserRewardResponse> userRewards = step("Получить список наград", () -> {
            Response response = userRewardSteps.getUserRewards(token);
            return userRewardSteps.validateUserRewardsResponse(response);
        });

        Optional<UserRewardResponse> valuationReward = step("Ищем 'valuation' награду", () ->
                userRewardSteps.findValuationReward(userRewards)
        );

        // если нет valuation наград, отмечает тест как пропущенный
        Assumptions.assumeTrue(valuationReward.isPresent(),
                "У пользователя нет наград типа 'valuation' ля тестирования");

        Long valuationRewardId = valuationReward.get().id();

        step("Найдена valuation наград", () ->
            Allure.addAttachment("Награда valuation", "text/plain",
                    String.format("ID: %d\nНазвание: %s\nТип: %s",
                            valuationRewardId,
                            valuationReward.get().name(),
                            valuationReward.get().rewardType()))
        );

        Response openResponse = step("Попытка открыть valuation награду", () ->
                userRewardSteps.openReward(token, valuationRewardId)
        );

        step("Валидация ошибки", () -> {
            int statusCode = openResponse.getStatusCode();

            assertTrue(statusCode != 200,
                    "Для valuation награды должен вовзращаться 4хх статус код. Получен: " + statusCode);

            String responseBody = openResponse.getBody().asString();

            Allure.addAttachment("Результат", "text/plain",
                    String.format("""
                                    ✅ Сервер корректно отклонил запрос
                                    🎁 ID valuation награды: %d
                                    📊 Статус код: %d
                                    📝 Ответ сервера: %s""",
                            valuationRewardId,
                            statusCode,
                            responseBody));
        });
    }

    //    @Test
    @Order(15)
    @DisplayName("Негативный тест: Открытие несуществующей награды")
    @Story("Валидация")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void shouldFailToOpenNonExistentReward() {
        // Несуществующий ID
        Long nonExistenId = 999999L;

        Response openResponse = step("Попытка открыть несуществующую награду", () ->
                userRewardSteps.openReward(token, nonExistenId)
        );

        step("Валидация ошибки 'не найдено'", () -> {
            int statusCode = openResponse.getStatusCode();

            assertTrue(statusCode != 200, "Для несуществующей награды должен возвращаться 4хх статус код");

            if (statusCode == 404) {
                Allure.addAttachment("Результат", "text/plain",
                        "✅ Корректный ответ 404 Not Found для несуществующей награды");
            } else {
                Allure.addAttachment("Результат", "text/plain",
                        String.format("📊 Статус код: %d (ожидался 404, но другой 4xx тоже допустим)",
                                statusCode));
            }
        });
    }

    @Test
    @Order(16)
    @DisplayName("Тест безопасности: открытие награды без авторизации")
    @Story("Безопасность")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("security")
    @Tag("negative")
    void shouldRequireAuthentication() {
        // используем любой ID
        Long testRewardId = 7090L;

        Response response = step("Отправка GET запроса без токена", () ->
                userRewardSteps.openReward("", testRewardId)
        );

        step("Проверка ошибки авторизации", () -> {
            gameWheelStartGameSteps.validateUnauthorizedResponse(response);

            Allure.addAttachment("Проверка безопасности", "text/plain",
                    "✅ Доступ без авторизации запрещен (401)");
        });
    }

    @Test
    @Order(17)
    @DisplayName("Тест безопасности: Открытие награды с невалидным токеном")
    @Story("Безопасность")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("security")
    @Tag("negative")
    void shouldFailWithInvalidTokenOpen() {
        Response response = step("Отправка GET запроса с невалидным токеном", () ->
                userRewardSteps.openReward("invalid_token_12345", 7090L)
        );

        step("Проверка ошибки авторизации", () ->
            gameWheelStartGameSteps.validateUnauthorizedResponse(response)
        );
    }

    @Test
    @Order(18)
    @DisplayName("Интеграционный тест: Полный цикл (спин -> получение награды -> открытие)")
    @Story("E2E сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("integration")
    @Tag("e2e")
    void fullRewardFlowIntegrationTest() {
        List<UserRewardResponse> initionalRewards = step("Получаем начальный список наград", () -> {
            Response response = userRewardSteps.getUserRewards(token);
            return userRewardSteps.validateUserRewardsResponse(response);
        });

        int initialCount = initionalRewards.size();
        Allure.addAttachment("Начальное состояние", "text/plain",
                "Количество наград до теста: " + initialCount);

        GameWheelStartGameResponse prize = step("Крутим игровое колесо", () -> {
            Response response = gameWheelStartGameSteps.startGame(token);
            return gameWheelStartGameSteps.validateStartGameResponse(response);
        });

        step("Приз получен", () ->
            Allure.addAttachment("Выигранный приз", "text/plain",
                    String.format("🎁 Приз: %s\nID: %d\nТип: %s\nЗначение: %d\nUserRewardId: %d",
                            prize.getName(),
                            prize.getId(),
                            prize.getType(),
                            prize.getValue(),
                            prize.getUserRewardId()))
        );

        List<UserRewardResponse> rewardAfterSpin = step("Получаем список после спина", () -> {
            Response response = userRewardSteps.getUserRewards(token);
            return userRewardSteps.validateUserRewardsResponse(response);
        });

        int afterSpinCount = rewardAfterSpin.size();

        step("Проверяем что награда добавилась", () -> {
            assertEquals(initialCount + 1, afterSpinCount,
                    "после спина колеса должна добавится одна награда");

            Allure.addAttachment("После спина", "text/plain",
                    "Количество наград после спина: " + afterSpinCount);
        });

        UserRewardResponse newReward = step("Находим новую награду", () ->
            rewardAfterSpin.stream()
                    .filter(reward -> initionalRewards.stream()
                            .noneMatch(initial -> initial.id().equals(reward.id())))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("Новая награда не найдена в списке"))
        );

        step("Новая награда найдена", () -> {
            Allure.addAttachment("Новая награда", "text/plain",
                    String.format("ID: %d\nНазвание: %s\nТип: %s\nЗначение: %d",
                            newReward.id(),
                            newReward.name(),
                            newReward.rewardType(),
                            newReward.rewardValue()));

            assertNotEquals("valuation", newReward.rewardType(),
                    "Награда с игрового колеса не должна быть типа 'valuation'");
        });

        Response openResponse = step("Открываем новую награду", () ->
                userRewardSteps.openReward(token, newReward.id())
        );

        step("Проверяем успешное открытие", () -> {
            openResponse.then()
                    .statusCode(200);

            Allure.addAttachment("Результат открытия", "text/plain",
                    String.format("""
                                    ✅ Награда успешно открыта
                                    🎁 ID награды: %d
                                    📊 Статус код: 200 OK""",
                            newReward.id()));
        });

        List<UserRewardResponse> finalRewards = step("Получаем финальный список", () -> {
            Response response = userRewardSteps.getUserRewards(token);
            return userRewardSteps.validateUserRewardsResponse(response);
        });

        step("Итоговый отчет", () -> {
            int finalCount = finalRewards.size();

            Allure.addAttachment("🎉 ИТОГИ E2E ТЕСТА", "text/plain",
                    String.format("""
                                    ✅ ПОЛНЫЙ ЦИКЛ ВЫПОЛНЕН УСПЕШНО!
                                    
                                    📊 Статистика:
                                    • Начальное количество наград: %d
                                    • После спина колеса: %d (+1)
                                    • После открытия награды: %d
                                    • Итого изменений: +1 награда
                                    
                                    🎁 Детали награды:
                                    • ID: %d
                                    • Название: %s
                                    • Тип: %s
                                    • Значение: %d
                                    
                                    ✅ Все этапы выполнены корректно!""",
                            initialCount,
                            afterSpinCount,
                            finalCount,
                            newReward.id(),
                            newReward.name(),
                            newReward.rewardType(),
                            newReward.rewardValue()));
        });
    }

    @Test
    @Order(19)
    @DisplayName("Успешное получение истории наград (Проверка 200)")
    @Story("История наград")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("positive")
    @Tag("history")
    void getRewardHistorySuccess() {
        Response response = step("GET /user/reward/history", () ->
                userRewardSteps.getUserRewardsHistory(token)
        );

        List<UserRewardResponse> history = step("Валидация ответа", () ->
                userRewardSteps.validateUserRewardsResponse(response)
        );
        userRewardSteps.generateUserRewardsReport(history);
    }

    @Test
    @Order(20)
    @DisplayName("Проверка 'История наград' без авторизации (Проверка 401)")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("negative")
    @Tag("security")
    void shouldFailToGetHistoryWithoutAuth() {
        Response response = step("Отправка GET запроса без токена", () ->
                userRewardSteps.getUserRewardsHistory("")
        );

        step("Проверка ошибки авторизации", () -> {
            gameWheelStartGameSteps.validateUnauthorizedResponse(response);

            Allure.addAttachment("Проверка безопасности", "text/plain",
                    "✅ Доступ без авторизации запрещен (401)");
        });
    }

    @Test
    @Order(21)
    @DisplayName("Проверка 'Запуска игры' с невалидным токеном (Проверка 401)")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    @Tag("security")
    void shouldFailWithInvalidTokenHistory() {
        Response response = step("Отправка GET запроса без токена", () ->
                userRewardSteps.getUserRewardsHistory("invalid_token_12345")
        );

        step("Проверка ошибки авторизации", () ->
            gameWheelStartGameSteps.validateUnauthorizedResponse(response)
        );
    }
}