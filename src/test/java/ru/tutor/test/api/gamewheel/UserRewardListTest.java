package ru.tutor.test.api.gamewheel;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import io.restassured.response.Response;
import ru.tutor.config.TestConfig;
import ru.tutor.model.fortune.GameWheelRewardResponse;
import ru.tutor.steps.account.AuthSteps;
import ru.tutor.steps.fortune.GameWheelStartGameSteps;
import ru.tutor.steps.fortune.UserRewardSteps;

import java.util.*;

import static io.qameta.allure.Allure.step;
import ru.tutor.constants.Tags;


@Tag(Tags.API)
@Epic("API Тесты пользователя")
@Feature("Игровое колесо")
@DisplayName("Тесты эндпоинта игрового колесаю Список неполученных призов")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag(Tags.INTEGRATION)
@Tag(Tags.CONTRACT)
public class UserRewardListTest {

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
    @DisplayName("Успешное получение 'Списка наград' пользователя (Проверка 200 OK)")
    @Story("Основной сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    void getUserRewardSuccess() {

        Response response = step("GET /user/reward/list", () ->
                userRewardSteps.getUserRewards(token)
        );

        step("Полная валидация ответа", () ->
                userRewardSteps.validateUserRewardsListComplete(response)
        );
    }

    @Test
    @Order(2)
    @DisplayName("Проверка 'Списка призов' без авторизации (Проверка 401)")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.NEGATIVE)
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
    @Order(3)
    @DisplayName("Проверка 'Списка призов' с невалидным токеном (Проверка 401)")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    void shouldFailWithInvalidTokenList() {
        Response response = step("Отправка GET запроса с невалидным токеном", () ->
                userRewardSteps.getUserRewards("invalid_token_12345")
        );

        step("Проверка ошибки авторизации", () ->
                gameWheelStartGameSteps.validateUnauthorizedResponse(response)
        );
    }
}
