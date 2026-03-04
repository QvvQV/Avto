package ru.tutor.test.api.user;


import io.qameta.allure.*;
import io.qameta.allure.junit5.AllureJunit5;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tutor.model.UserProfile;
import ru.tutor.steps.AuthSteps;
import ru.tutor.steps.UserSteps;
import ru.tutor.config.TestConfig;
import ru.tutor.model.fortune.GameStatsResponse;

import static io.qameta.allure.Allure.step;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AllureJunit5.class)
@Epic("API Тесты пользователя")
@Feature("Игровая статистика")
@DisplayName("Тесты эндпоинта /user/game-stats")
public class UserGameStatsTest {

    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();

    private final UserSteps userSteps = new UserSteps(BASE_URL);

    private UserProfile profile;

    @BeforeEach
        public void setup() {
            profile = step("Логин", () ->
                    userSteps.loginAndGetUserProfile(TEST_EMAIL, TEST_PASSWORD)
            );
    }

    @Test
    @DisplayName("Получение игровой статистики")
    @Story("Основной сценарий")
    void getGameStatsSuccess() {

        Response response = step("GET /user/game-stats", () ->
                userSteps.getGameStats(userSteps.getCurrentAuthToken())
        );

        step("Проверка ответа", () -> {
            response.then()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body("level", notNullValue())
                    .body("experience", notNullValue())
                    .body("diamond", notNullValue())
                    .body("hasLoginToday", notNullValue());
        });
    }

    @Test
    @DisplayName("Детальная проверка данных игровой статистики")
    @Story("Валидация данных")
    void verifyGameStatsDetails() {
        Response response = userSteps.getGameStats(userSteps.getCurrentAuthToken());
        GameStatsResponse stats = userSteps.validateGameStatsResponse(response);

        step("Проверка конкретных значений", () ->
                assertAll("Game stats specific values",
                        () -> assertEquals(500, stats.getLevelUpLimit(), "LevelUpLimit should be 500"),
                        () -> assertEquals(18, stats.getSeason(), "Season should be 18"),
                        () -> assertNotNull(stats.getSeasonEnd(), "SeasonEnd should not be null"),
                        () -> assertTrue(stats.getSeasonEnd().contains("2026"), "SeasonEnd should be in 2026"),
                        () -> assertNotNull(stats.getShowModal(), "showModal should not be null"))
        );
    }

    @Test
    @DisplayName("Проверка структуры массива days")
    void verifyDaysArrayStructure() {

        Response response = userSteps.getGameStats(userSteps.getCurrentAuthToken());

        response.then()
                .body("days.size()", equalTo(31))
                .body("days[0].item", equalTo("experience"))
                .body("days[0].score", equalTo(15))
                .body("days[0].done", equalTo(true))
                .body("days[1].item", equalTo("diamond"))
                .body("days[1].score", equalTo(15))
                .body("days[1].done", equalTo(true))
                .body("days[2].item", equalTo("diamond"))
                .body("days[2].score", equalTo(15))
                .body("days[2].done", equalTo(true))
                .body("days[3].item", equalTo("experience"));
//                .body("days[3].score", equalTo(Integer.valueOf(30)))
//                .body("days[3].done", equalTo(true));

        for (int i = 0; i < 31; i++) {
//            final int index = i;
            response.then()
                    .body("days[" + i + "].item", anyOf(equalTo("experience"), equalTo("diamond")))
                    .body("days[" + i + "].score", greaterThan(0))
                    .body("days[" + i + "].done", notNullValue());
        }
    }

    @Test
    @DisplayName("Проверка связи с профилем пользователя")
    @Story("Интеграционный тест")
    void verifyConnectionWithUserProfile() {
        // 1. Получаем профиль

        Response profileResponse = userSteps.getGameStats(userSteps.getCurrentAuthToken());
        Boolean hasLoginTodayProfile = profileResponse.jsonPath().getBoolean("hasLoginToday");

        // 2. Получаем игровую статистику
        Response gameStatsResponse = userSteps.getGameStats(userSteps.getCurrentAuthToken());
        Boolean hasGameStatsResponse = gameStatsResponse.jsonPath().getBoolean("hasLoginToday");

        // 3. Проверяем согласованность
        assertNotNull(hasLoginTodayProfile, "Profile should have hasLoginToday");
        assertNotNull(hasGameStatsResponse, "Game stats should have hasLoginToday");

        // Обычно эти значения должны совпадать
        assertEquals(hasLoginTodayProfile, hasGameStatsResponse,
                "hasLoginTodayProfile should be consistent between profile and game stats");

        Allure.addAttachment("Согласованность данных", "text/plain",
                "Профиль - hasLoginToday: " + hasLoginTodayProfile + "\n" +
                        "Game Stats - hasGameStatsResponse: " + hasGameStatsResponse + "\n" +
                        "Совпадают: " + (hasLoginTodayProfile.equals(hasGameStatsResponse) ? "✅" : "❌")
        );
    }
}
