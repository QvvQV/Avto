package ru.tutor.test.api.gamewheel;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import io.restassured.response.Response;
import ru.tutor.config.TestConfig;
import ru.tutor.model.fortune.GameWheelRewardResponse;
import ru.tutor.steps.account.AuthSteps;
import ru.tutor.steps.fortune.GameWheelRewardsSteps;


import java.util.*;

import static io.qameta.allure.Allure.step;
import ru.tutor.constants.Tags;

@Tag(Tags.API)
@Epic("API Тесты пользователя")
@Feature("Игровое колесо")
@DisplayName("Тесты эндпоинта игрового колеса")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag(Tags.INTEGRATION)
public class GameWheelRewardsTest {

    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private final AuthSteps userSteps = new AuthSteps(BASE_URL);
    private final GameWheelRewardsSteps gameWheelRewardsSteps = new GameWheelRewardsSteps(BASE_URL);

    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();

    private static String token;

    private static List<GameWheelRewardResponse> rewards;

    @BeforeAll
    public static void setUp() {
        Allure.label("component", "API");
        Allure.label("feature", "Game Wheel");
    }

    @Test
    @Order(1)
    @DisplayName("Успешное получение списка наград игрового колеса (Проверка 200 OK)")
    @Story("Основной сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    @Tag(Tags.CONTRACT)
    void getGameWheelRewardsSuccess() {

        token = step("Логин", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );

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
    @Order(2)
    @DisplayName("Проверка 'Наград колеса' без авторизации (Проверка 401)")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void shouldFailWithoutAuthorization() {
        Response response = step("Отправка Get запроса без токена", () ->
                gameWheelRewardsSteps.getGameWheelRewards("")
        );

        step("Проверка ошибки авторизации", () ->
                gameWheelRewardsSteps.assertUnauthorizedWithMessage(response, "JWT Token not found")
        );
    }

    @Test
    @Order(3)
    @DisplayName("Проверка 'Наград колеса' с невалидным токеном (Проверка 401)")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void shouldFailWithInvalidToken() {
        Response response = step("Отправка GET запроса с невалидным токеном", () ->
                gameWheelRewardsSteps.getGameWheelRewards("invalid_token_12345")
        );

        step("Проверка ошибки авторизации", () ->
                gameWheelRewardsSteps.assertUnauthorizedWithMessage(response, "Invalid JWT Token")
        );
    }
}
