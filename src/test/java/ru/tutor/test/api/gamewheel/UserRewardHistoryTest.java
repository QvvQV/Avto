package ru.tutor.test.api.gamewheel;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import io.restassured.response.Response;
import ru.tutor.config.TestConfig;
import ru.tutor.model.fortune.UserRewardResponse;
import ru.tutor.steps.account.AuthSteps;
import ru.tutor.steps.fortune.GameWheelStartGameSteps;
import ru.tutor.steps.fortune.UserRewardSteps;

import java.util.*;

import static io.qameta.allure.Allure.step;
import ru.tutor.constants.Tags;


@Tag(Tags.API)
@Epic("API Тесты пользователя")
@Feature("Игровое колесо")
@DisplayName("Тесты эндпоинта игрового колеса. История получения призов")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag(Tags.INTEGRATION)
public class UserRewardHistoryTest {

    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private final AuthSteps userSteps = new AuthSteps(BASE_URL);
    private final GameWheelStartGameSteps gameWheelStartGameSteps = new GameWheelStartGameSteps(BASE_URL);
    private final UserRewardSteps userRewardSteps = new UserRewardSteps(BASE_URL);

    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();

    private static String token;

    @BeforeAll
    public static void setUp() {
        Allure.label("component", "API");
        Allure.label("feature", "Game Wheel");
    }

    @BeforeEach
    void setUpAuth() {
        token = step("Логин", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );
    }

    @Test
    @Order(1)
    @DisplayName("Успешное получение истории наград (Проверка 200)")
    @Story("История наград")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    @Tag(Tags.CONTRACT)
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
    @Order(2)
    @DisplayName("Проверка 'История наград' без авторизации (Проверка 401)")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void shouldFailToGetHistoryWithoutAuth() {
        Response response = step("Отправка GET запроса без токена", () ->
                userRewardSteps.getUserRewardsHistory("")
        );

        step("Проверка ошибки авторизации", () -> {
            gameWheelStartGameSteps.assertUnauthorizedWithMessage(response, "JWT Token not found");
        });
    }

    @Test
    @Order(3)
    @DisplayName("Проверка группы наград с невалидным токеном (Проверка 401)")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void shouldFailWithInvalidTokenHistory() {
        Response response = step("Отправка GET запроса без токена", () ->
                userRewardSteps.getUserRewardsHistory("invalid_token_12345")
        );

        step("Проверка ошибки авторизации", () ->
                gameWheelStartGameSteps.assertUnauthorizedWithMessage(response, "Invalid JWT Token")
        );
    }
}
