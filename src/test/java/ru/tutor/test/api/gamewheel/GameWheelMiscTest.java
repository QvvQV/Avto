package ru.tutor.test.api.gamewheel;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import io.restassured.response.Response;
import ru.tutor.config.TestConfig;
import ru.tutor.model.fortune.GameWheelRewardResponse;
import ru.tutor.model.fortune.GameWheelStartGameResponse;
import ru.tutor.model.fortune.UserRewardResponse;
import ru.tutor.steps.account.AuthSteps;
import ru.tutor.steps.fortune.GameWheelMiscSteps;
import ru.tutor.steps.fortune.GameWheelStartGameSteps;
import ru.tutor.steps.fortune.UserRewardSteps;

import java.util.*;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;
import ru.tutor.constants.Tags;
@Tag(Tags.API)
@Epic("API Тесты пользователя")
@Feature("Игровое колесо")
@DisplayName("Тесты эндпоинта игрового колеса победители, таймер, группы")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameWheelMiscTest {

    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private final AuthSteps userSteps = new AuthSteps(BASE_URL);
    private final GameWheelStartGameSteps gameWheelStartGameSteps = new GameWheelStartGameSteps(BASE_URL);
    private final UserRewardSteps userRewardSteps = new UserRewardSteps(BASE_URL);
    private final GameWheelMiscSteps gameWheelMiscSteps = new GameWheelMiscSteps(BASE_URL);

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
    @DisplayName("Интеграционный тест: 'Запуск игры' и проверка 'Добавления награды'")
    @Story("Интеграция с колесом")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.INTEGRATION)
    @Tag(Tags.POSITIVE)
    void integrationTestWithGameWheel() {

        UserRewardSteps.IntegrationTestResult result = step("Выполнение интеграционного теста", () ->
                userRewardSteps.performIntegrationTest(token, gameWheelStartGameSteps)
        );

        step("Валидация результата", () ->
                userRewardSteps.validateIntegrationTestResult(result));
    }

    @Test
    @Order(2)
    @DisplayName("Интеграционный тест: Полный цикл (спин -> получение награды -> открытие)")
    @Story("E2E сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.INTEGRATION)
    @Tag(Tags.E2E)
    void fullRewardFlowIntegrationTest() {
        List<UserRewardResponse> initialRewards = step("Получаем начальный список наград", () ->
                userRewardSteps.validateUserRewardsListComplete(userRewardSteps.getUserRewards(token))
        );

        GameWheelStartGameResponse prize = step("Крутим колесо", () -> {
            Response response = gameWheelStartGameSteps.startGame(token);
            return gameWheelStartGameSteps.validateStartGameResponse(response);
        });

        List<UserRewardResponse> afterSpinRewards = step("Получаем список после спина", () ->
                userRewardSteps.validateUserRewardsListComplete(userRewardSteps.getUserRewards(token))
        );

        assertEquals(initialRewards.size() + 1, afterSpinRewards.size(),
                "После спина должна добавиться одна награда");

        UserRewardResponse newReward = step("Находим новую награду", () ->
                userRewardSteps.findNewReward(initialRewards, afterSpinRewards)
        );

        Response openResponse = step("Открываем награду", () ->
                userRewardSteps.openReward(token, newReward.id())
        );

        step("Валидация открытия", () ->
                userRewardSteps.validateOpenRewardSuccess(openResponse, newReward.id())
        );

        List<UserRewardResponse> finalRewards = step("Финальный список", () ->
                userRewardSteps.validateUserRewardsListComplete(
                        userRewardSteps.getUserRewards(token)
                )
        );

        step("Итоговый отчёт", () ->
                userRewardSteps.generateE2EReport(
                        initialRewards.size(), afterSpinRewards.size(),
                        finalRewards.size(), newReward)
        );
    }

    @Test
    @Order(3)
    @DisplayName("GET /user/game-wheel/rewards_groups — успешное получение списка")
    @Story("Основной сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    void getRewardsGroupsSuccess() {
        Response response = step("GET /user/game-wheel/rewards_groups", () ->
                gameWheelMiscSteps.getUserGameWheelRewardsGroups(token)
        );

        step("Валидация ответа", () ->
                gameWheelMiscSteps.validateRewardsGroupsItems(response)
        );
    }

    @Test
    @Order(4)
    @DisplayName("GET /user/game-wheel/rewards_groups — без авторизации")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.NEGATIVE)
    void getRewardsGroupsWithoutAuth() {
        Response response = step("GET /user/game-wheel/rewards_groups без токена", () ->
                gameWheelMiscSteps.getUserGameWheelRewardsGroups("")
        );

        step("Проверка ошибки авторизации", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(5)
    @DisplayName("GET /user/game-wheel/rewards_groups — с невалидным токеном")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.NEGATIVE)
    void getRewardsGroupsInvalidToken() {
        Response response = step("GET /user/game-wheel/rewards_groups с невалидным токена", () ->
                gameWheelMiscSteps.getUserGameWheelRewardsGroups("invalid_token")
        );

        step("Проверка ошибки авторизации", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(6)
    @DisplayName("GET /user/game-wheel/timer-left-seconds — успешное получение оставшихся секунд")
    @Story("Основной сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    void getTimerLeftSuccess() {
        Response response = step("GET /user/game-wheel/timer-left-seconds", () ->
                gameWheelMiscSteps.getUserGameWheelTimerLeft(token)
        );

        step("Валидация ответа", () ->
                gameWheelMiscSteps.validateTimerLeftSeconds(response)
        );
    }

    @Test
    @Order(7)
    @DisplayName("GET /user/game-wheel/timer-left-seconds — без авторизации")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.NEGATIVE)
    void getTimerLeftWithoutAuth() {
        Response response = step("GET /user/game-wheel/timer-left-seconds без токена", () ->
                gameWheelMiscSteps.getUserGameWheelTimerLeft("")
        );

        step("Проверка ошибки авторизации", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(8)
    @DisplayName("GET /user/game-wheel/timer-left-seconds — с невалидным токеном")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.NEGATIVE)
    void getTimerLeftInvalidToken() {
        Response response = step("GET /user/game-wheel/timer-left-seconds с невалидным токена", () ->
                gameWheelMiscSteps.getUserGameWheelTimerLeft("invalid_token")
        );

        step("Проверка ошибки авторизации", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(9)
    @DisplayName("GET /user/game-wheel/rewards-recently-won-all — успешное получение списка победителей")
    @Story("Основной сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    void getGameWheelWonAllSuccess() {
        Response response = step("GET /user/game-wheel/rewards-recently-won-all", () ->
                gameWheelMiscSteps.getUserGameWheelRewardsRecentlyWonAll(token)
        );

        step("Валидация ответа", () ->
                gameWheelMiscSteps.validateRecentlyWonItem(response)
        );
    }

    @Test
    @Order(10)
    @DisplayName("GET /user/game-wheel/rewards-recently-won-all — без авторизации")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.NEGATIVE)
    void getGameWheelWonAllWithoutAuth() {
        Response response = step("GET /user/game-wheel/rewards-recently-won-all без токена", () ->
                gameWheelMiscSteps.getUserGameWheelRewardsRecentlyWonAll("")
        );

        step("Проверка ошибки авторизации", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(11)
    @DisplayName("GET /user/game-wheel/rewards-recently-won-all — с невалидным токеном")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.NEGATIVE)
    void getGameWheelWonAllInvalidToken() {
        Response response = step("GET /user/game-wheel/rewards-recently-won-all с невалидным токена", () ->
                gameWheelMiscSteps.getUserGameWheelRewardsRecentlyWonAll("invalid_token")
        );

        step("Проверка ошибки авторизации", () ->
                assertEquals(401, response.getStatusCode()));
    }
}
