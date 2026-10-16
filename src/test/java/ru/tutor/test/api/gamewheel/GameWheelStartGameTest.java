package ru.tutor.test.api.gamewheel;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import io.restassured.response.Response;
import ru.tutor.config.TestConfig;
import ru.tutor.model.fortune.GameWheelStartGameResponse;
import ru.tutor.model.fortune.UserRewardResponse;
import ru.tutor.steps.account.AuthSteps;
import ru.tutor.steps.fortune.GameWheelStartGameSteps;

import java.util.*;

import static io.qameta.allure.Allure.step;
import ru.tutor.constants.Tags;
import ru.tutor.steps.fortune.UserRewardSteps;

@Tag(Tags.API)
@Epic("API Тесты пользователя")
@Feature("Игровое колесо")
@DisplayName("Тесты эндпоинта игрового колеса")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag(Tags.INTEGRATION)
public class GameWheelStartGameTest {

    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private final AuthSteps userSteps = new AuthSteps(BASE_URL);
    private final UserRewardSteps userRewardSteps = new UserRewardSteps(BASE_URL);
    private final GameWheelStartGameSteps gameWheelStartGameSteps = new GameWheelStartGameSteps(BASE_URL);

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
    @DisplayName("Успешный 'Запуск игры' на колесе (Проверка 200 OK)")
    @Story("Основной сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    void startGameSuccess() {

        token = step("Логин", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );

        Response response = step("POST /user/game-wheel/start-game", () ->
                gameWheelStartGameSteps.startGame(token)
        );

        GameWheelStartGameResponse prize = step("Валидация ответа с призом", () ->
                gameWheelStartGameSteps.validateStartGameResponse(response)
        );

        step("Награда появилась в списке наград пользователя", () ->
                userRewardSteps.verifyRewardAppearedInUserList(prize,
                        userRewardSteps.validateUserRewardsResponse(
                                userRewardSteps.getUserRewards(token)
                        )));

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
    @Order(2)
    @DisplayName("Проверка уникальности userRewardId при множественых запусках")
    @Story("Функциональность")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.POSITIVE)
    void shouldGenerateUniqueUserRewardId() {

        token = step("Логин", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );

        List<GameWheelStartGameResponse> multiplePrizes = step("Множественный запуск игры (10 раза)",
                () ->
                        gameWheelStartGameSteps.multipleStartGame(token, 10)
        );

        step("Валидация результатов", () ->
                gameWheelStartGameSteps.validateMultipleStartGameResults(multiplePrizes));
        step("Все призы появились в списке наград пользователя", () -> {
            List<UserRewardResponse> userRewards = userRewardSteps.validateUserRewardsResponse(
                    userRewardSteps.getUserRewards(token)
            );
            multiplePrizes.forEach(prize ->
                    userRewardSteps.verifyRewardAppearedInUserList(prize, userRewards));
        });
    }

    @Test
    @Order(3)
    @DisplayName("Проверка 'Запуска игры' без авторизации (Проверка 401)")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void shouldFailWithoutAuthorizationGame() {
        Response response = step("Отправка POST запроса без токена", () ->
                gameWheelStartGameSteps.startGame("")
        );

        step("Проверка ошибки авторизации", () -> {
            gameWheelStartGameSteps.assertUnauthorizedWithMessage(response, "JWT Token not found");
        });
    }

    @Test
    @Order(4)
    @DisplayName("Проверка 'Запуска игры' с невалидным токеном (Проверка 401)")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void shouldFailWithInvalidTokenGame() {
        Response response = step("Отправка POST запроса с невалидным токеном", () ->
                gameWheelStartGameSteps.startGame("invalid_token_12345")
        );

        step("Проверка ошибки авторизации", () ->
                gameWheelStartGameSteps.assertUnauthorizedWithMessage(response, "Invalid JWT Token")
        );
    }
}