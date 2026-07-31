package ru.tutor.test.api.gamewheel;

import io.qameta.allure.*;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tutor.config.TestConfig;
import ru.tutor.model.fortune.GameWheelRewardResponse;
import ru.tutor.model.fortune.UserRewardResponse;
import ru.tutor.steps.UserSteps;
import ru.tutor.steps.fortune.GameWheelStartGameSteps;
import ru.tutor.steps.fortune.UserRewardSteps;

import java.util.*;

import static io.qameta.allure.Allure.step;


@Tag("API")
@ExtendWith(AllureJunit5.class)
@Epic("API Тесты пользователя")
@Feature("Игровое колесо")
@DisplayName("Тесты эндпоинта игрового колеса")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserRewardListTest {

    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private final UserSteps userSteps = new UserSteps(BASE_URL);
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
    @Tag("positive")
    @Tag("smoke")
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
    @Order(3)
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
}
