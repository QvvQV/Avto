package ru.tutor.test.api.gamewheel;

import io.qameta.allure.*;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tutor.config.TestConfig;
import ru.tutor.model.fortune.GameWheelRewardResponse;
import ru.tutor.model.fortune.UserRewardResponse;
import ru.tutor.steps.account.AuthSteps;
import ru.tutor.steps.fortune.GameWheelStartGameSteps;
import ru.tutor.steps.fortune.UserRewardSteps;

import java.util.*;

import static io.qameta.allure.Allure.step;


@Tag("API")
@ExtendWith(AllureJunit5.class)
@Epic("API Тесты пользователя")
@Feature("Игровое колесо")
@DisplayName("Тесты эндпоинта игрового колес. История получения призов")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserRewardHistoryTest {

    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private final AuthSteps userSteps = new AuthSteps(BASE_URL);
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
    @Order(2)
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
    @Order(3)
    @DisplayName("Проверка группы наград с невалидным токеном (Проверка 401)")
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
