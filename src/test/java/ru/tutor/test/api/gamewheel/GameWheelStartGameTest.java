package ru.tutor.test.api.gamewheel;

import io.qameta.allure.*;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tutor.config.TestConfig;
import ru.tutor.model.fortune.GameWheelRewardResponse;
import ru.tutor.model.fortune.GameWheelStartGameResponse;
import ru.tutor.steps.account.AuthSteps;
import ru.tutor.steps.fortune.GameWheelStartGameSteps;

import java.util.*;

import static io.qameta.allure.Allure.step;
import ru.tutor.constants.Tags;

@Tag(Tags.API)
@ExtendWith(AllureJunit5.class)
@Epic("API Тесты пользователя")
@Feature("Игровое колесо")
@DisplayName("Тесты эндпоинта игрового колеса")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameWheelStartGameTest {

    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private final AuthSteps userSteps = new AuthSteps(BASE_URL);
    private final GameWheelStartGameSteps gameWheelStartGameSteps = new GameWheelStartGameSteps(BASE_URL);

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
    @DisplayName("Успешный 'Запуск игры' на колесе (Проверка 200 OK)")
    @Story("Основной сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    @Tag(Tags.SMOKE)
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
    @Order(2)
    @DisplayName("Проверка уникальности userRewardId при множественых запусках")
    @Story("Фунциональность")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.POSITIVE)
    void shouldGenerateUniqueUserRewardId() {

        List<GameWheelStartGameResponse> multiplePrizes = step("Множественный запуск игры (10 раза)",
                () ->
                        gameWheelStartGameSteps.multipleStartGame(token, 5)
        );

        step("Валидация результатов", () ->
                gameWheelStartGameSteps.validateMultipleStartGameResults(multiplePrizes));
    }

    @Test
    @Order(3)
    @DisplayName("Проверка 'Запуска игры' без авторизации (Проверка 401)")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.NEGATIVE)
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
    @Order(4)
    @DisplayName("Проверка 'Запуска игры' с невалидным токеном (Проверка 401)")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    void shouldFailWithInvalidTokenGame() {
        Response response = step("Отправка POST запроса с невалидным токеном", () ->
                gameWheelStartGameSteps.startGame("invalid_token_12345")
        );

        step("Проверка ошибки авторизации", () ->
                gameWheelStartGameSteps.validateUnauthorizedResponse(response)
        );
    }
}