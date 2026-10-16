package ru.tutor.test.api.gamewheel;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import io.restassured.response.Response;
import ru.tutor.config.TestConfig;
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
@Tag(Tags.INTEGRATION)
public class GameWheelMiscTest {

    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private final AuthSteps userSteps = new AuthSteps(BASE_URL);
    private final GameWheelStartGameSteps gameWheelStartGameSteps = new GameWheelStartGameSteps(BASE_URL);
    private final UserRewardSteps userRewardSteps = new UserRewardSteps(BASE_URL);
    private final GameWheelMiscSteps gameWheelMiscSteps = new GameWheelMiscSteps(BASE_URL);

    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();

    private static String token;

    @BeforeAll
    public static void setUp() {
        Allure.label("component", "API");
        Allure.label("feature", "Game Wheel");
    }

    @Test
    @Order(1)
    @DisplayName("Интеграционный тест: 'Запуск игры' и проверка 'Добавления награды'")
    @Story("Интеграция с колесом")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    void integrationTestWithGameWheel() {

        token = step("Логин", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );

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
    @Tag(Tags.POSITIVE)
    void fullRewardFlowIntegrationTest() {

        token = step("Логин", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );

        List<UserRewardResponse> initialRewards = step("Получаем начальный список наград", () ->
                userRewardSteps.validateUserRewardsListComplete(userRewardSteps.getUserRewards(token))
        );

        step("Крутим колесо", () -> {
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

        step("Открытая награда удалена из списка", () ->
                assertTrue(finalRewards.stream().noneMatch(r -> r.id().equals(newReward.id())),
                        "Открытая награда " + newReward.id() + " не должна оставаться в списке"));
        step("Количество совпадает (после спина +1, после открытия -1)", () ->
                assertEquals(afterSpinRewards.size() - 1, finalRewards.size()));

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
    @Tag(Tags.CONTRACT)
    void getRewardsGroupsSuccess() {

        token = step("Логин", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );

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
    @Tag(Tags.CONTRACT)
    void getRewardsGroupsWithoutAuth() {
        Response response = step("GET /user/game-wheel/rewards_groups без токена", () ->
                gameWheelMiscSteps.getUserGameWheelRewardsGroups("")
        );

        step("Проверка ошибки авторизации", () ->
                gameWheelMiscSteps.assertUnauthorizedWithMessage(response,   "JWT Token not found") );
    }

    @Test
    @Order(5)
    @DisplayName("GET /user/game-wheel/rewards_groups — с невалидным токеном")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void getRewardsGroupsInvalidToken() {
        Response response = step("GET /user/game-wheel/rewards_groups с невалидным токеном", () ->
                gameWheelMiscSteps.getUserGameWheelRewardsGroups("invalid_token")
        );

        step("Проверка ошибки авторизации", () ->
                gameWheelMiscSteps.assertUnauthorizedWithMessage(response,   "Invalid JWT Token") );
    }

    @Test
    @Order(6)
    @DisplayName("GET /user/game-wheel/timer-left-seconds — успешное получение оставшихся секунд")
    @Story("Основной сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    @Tag(Tags.CONTRACT)
    void getTimerLeftSuccess() {

        token = step("Логин", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );

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
    @Tag(Tags.CONTRACT)
    void getTimerLeftWithoutAuth() {
        Response response = step("GET /user/game-wheel/timer-left-seconds без токена", () ->
                gameWheelMiscSteps.getUserGameWheelTimerLeft("")
        );

        step("Проверка ошибки авторизации", () ->
                gameWheelMiscSteps.assertUnauthorizedWithMessage(response,   "JWT Token not found") );
    }

    @Test
    @Order(8)
    @DisplayName("GET /user/game-wheel/timer-left-seconds — с невалидным токеном")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void getTimerLeftInvalidToken() {
        Response response = step("GET /user/game-wheel/timer-left-seconds с невалидным токеном", () ->
                gameWheelMiscSteps.getUserGameWheelTimerLeft("invalid_token")
        );

        step("Проверка ошибки авторизации", () ->
                gameWheelMiscSteps.assertUnauthorizedWithMessage(response,   "Invalid JWT Token") );
    }

    @Test
    @Order(9)
    @DisplayName("GET /user/game-wheel/rewards-recently-won-all — успешное получение списка победителей")
    @Story("Основной сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    @Tag(Tags.CONTRACT)
    void getGameWheelWonAllSuccess() {

        token = step("Логин", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );

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
    @Tag(Tags.CONTRACT)
    void getGameWheelWonAllWithoutAuth() {
        Response response = step("GET /user/game-wheel/rewards-recently-won-all без токена", () ->
                gameWheelMiscSteps.getUserGameWheelRewardsRecentlyWonAll("")
        );

        step("Проверка ошибки авторизации", () ->
                gameWheelMiscSteps.assertUnauthorizedWithMessage(response,   "JWT Token not found") );
    }

    @Test
    @Order(11)
    @DisplayName("GET /user/game-wheel/rewards-recently-won-all — с невалидным токеном")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void getGameWheelWonAllInvalidToken() {
        Response response = step("GET /user/game-wheel/rewards-recently-won-all с невалидным токеном", () ->
                gameWheelMiscSteps.getUserGameWheelRewardsRecentlyWonAll("invalid_token")
        );

        step("Проверка ошибки авторизации", () ->
                gameWheelMiscSteps.assertUnauthorizedWithMessage(response,   "Invalid JWT Token") );
    }
}
