package ru.tutor.test.api.user;

import io.qameta.allure.*;
import io.qameta.allure.junit5.AllureJunit5;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tutor.steps.account.AuthSteps;
import ru.tutor.steps.fortune.GameSteps;
import ru.tutor.steps.account.ProfileSteps;
import ru.tutor.config.TestConfig;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;
import ru.tutor.constants.Tags;

@ExtendWith(AllureJunit5.class)
@Epic("API Тесты пользователя")
@Feature("Игровая статистика")
@DisplayName("Тесты эндпоинта /user/game-stats")
public class UserGameStatsTest {

    private final GameSteps userSteps = new GameSteps(TestConfig.getCurrentApiUrl());
    private final AuthSteps authSteps = new AuthSteps(TestConfig.getCurrentApiUrl());
    private final ProfileSteps profileSteps = new ProfileSteps(TestConfig.getCurrentApiUrl());
    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();

    private static String token;

    @BeforeEach
        public void setup() {
            token = step("Логин", () ->
                    authSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
            );
    }

    @Test
    @DisplayName("Детальная проверка данных игровой статистики")
    @Story("Валидация данных")
    void verifyGameStatsDetails() {
        Response response = userSteps.getGameStats(token);

        step("Валидация ответа", () ->
                userSteps.validateGameStatsResponse(response)
        );
    }

    @Test
    @DisplayName("Проверка связи с профилем пользователя")
    @Story("Интеграционный тест")
    void verifyConnectionWithUserProfile() {
        // 1. Получаем профиль

        Response profileResponse = profileSteps.getProfile(token);
        Boolean hasLoginTodayProfile = profileResponse.jsonPath().getBoolean("hasLoginToday");

        // 2. Получаем игровую статистику
        Response gameStatsResponse = userSteps.getGameStats(token);
        Boolean hasGameStatsResponse = gameStatsResponse.jsonPath().getBoolean("hasLoginToday");

        // 3. Проверяем согласованность
        assertNotNull(hasLoginTodayProfile, "Профиль должен содержать hasLoginToday");
        assertNotNull(hasGameStatsResponse, "Игровая статистика должна содержать hasLoginToday");

        // Обычно эти значения должны совпадать
        assertEquals(hasLoginTodayProfile, hasGameStatsResponse,
                "hasLoginTodayProfile должен быть согласован между профилем и игровой статистикой");

        Allure.addAttachment("Согласованность данных", "text/plain",
                "Профиль - hasLoginToday: " + hasLoginTodayProfile + "\n" +
                        "Game Stats - hasGameStatsResponse: " + hasGameStatsResponse + "\n" +
                        "Совпадают: " + (hasLoginTodayProfile.equals(hasGameStatsResponse) ? "✅" : "❌")
        );
    }

    @Test
    @DisplayName("Успешное получение рейтинг лидеров")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.POSITIVE)
    void gameLeaderRatingSuccess()
    {
        Response response = step("Получение списка лидеров", () ->
                userSteps.getLeaderRating(token)
        );
        step("Валидация ответа", () ->
                userSteps.validateLeaderRatingResponse(response)
        );
    }

    @Test
    @DisplayName("Получение списка лидеров без авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    void gameLeaderRatingWithoutAuth()
    {
        Response response = step("Получение списка лидеров", () ->
                userSteps.getLeaderRating("")
        );
        step("Валидация ответа", () ->
                assertEquals(401, response.getStatusCode())
        );
    }
}
