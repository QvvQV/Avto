package ru.tutor.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.api.UserApi;
import ru.tutor.base.BaseApiSteps;
import ru.tutor.model.fortune.GameStatsResponse;
import ru.tutor.utils.AllureAttachments;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GameSteps extends BaseApiSteps {

    private final UserApi userApi;

    public GameSteps(String baseUrl) {
        super(baseUrl);
        this.userApi = new UserApi(baseUrl);
    }

    @Step("GET /user/game-stats - получить игровую статистику")
    public Response getGameStats(String authToken) {
        Response response = userApi.getGameStats(authToken);
        AllureAttachments.attachJson("Game Stats Response", response.getBody().asString());
        AllureAttachments.attachJson("Game Stats Headers", response.getHeaders().toString());
        return response;
    }

    @Step("GET /user/game-leader/rating")
    public Response getLeaderRating(String authToken) {
        return this.userApi.getLeaderRating(authToken);
    }

    @Step("Валидация ответа игровой статистики")
    public GameStatsResponse validateGameStatsResponse(Response response) {
        // Проверяем статус код и заголовки
        assertBasicResponse(response);

        Map<String, Object> root = response.jsonPath().getMap("$");
        assertNotNull(root, "Тело должно быть JSON-объектом");

        assertAll("Поля игровой статистики",
                positiveInt(root, "level"),
                nonNegative(root, "experience") ,
                nonNegative(root, "experienceLevel"),
                positiveInt(root, "levelUpLimit"),
                nonNegative(root, "diamond"),
                nonNegative(root, "countNewRewards"),
                positiveInt(root, "season"),
                nonEmptyString(root, "seasonEnd"),
                notNull(root, "hasLoginToday"),
                notNull(root, "showModal"),
                () -> assertNotNull(root.get("days"), "days не долежн быть null")
        );

        List<Map<String, Object>> days = response.jsonPath().getList("days");
        assertNotNull(days, "days не должен быть null");
        assertFalse(days.isEmpty(), "days не должен быть пустым");

        for (int i= 0; i < days.size(); i++) {
            Map<String, Object> day = days.get(i);
            assertAll("День [" + i + "]",
                    positiveInt(day, "day"),
                    nonEmptyString(day, "item"),
                    nonNegative(day, "score"),
                    notNull(day, "done")
            );
        }

        GameStatsResponse stats = response.as(GameStatsResponse.class);
        assertNotNull(stats, "Игровая статистика не должна быть null");

        return stats;
    }
}