package ru.tutor.steps.fortune;

import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import ru.tutor.api.account.UserApi;
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
        Response response = this.userApi.getLeaderRating(authToken);
        AllureAttachments.attachJson("Game Leader Rating", response.getBody().asString());
        AllureAttachments.attachJson("Game Leader Rating", response.getHeaders().toString());
        return response;
    }

    @Step("Валидация ответа рейтинга лидеров")
    public void validateLeaderRatingResponse(Response response) {
        assertBasicResponse(response);

        JsonPath json = response.jsonPath();
        Map<String, Object> root = json.getMap("$");
        assertNotNull(root, "Root response should not be null");

        assertAll("Rating table version",
                () -> assertNotNull(json.getString("ratingTableVersion"), "ratingTableVersion should exist"));

        assertAll("User object",
                () -> assertNotNull(json.getMap("user"), "user object should exist"),
                () -> assertNotNull(json.getMap("user.today"), "user.today should exist"),
                () -> assertNotNull(json.getMap("user.week"), "user.week should exist"),
                () -> assertNotNull(json.getMap("user.month"), "user.month should exist"),
                () -> assertNotNull(json.getList("user.prize"), "user.prize should exist")
        );

        assertAll("Leaders object",
                () -> assertNotNull(json.getMap("leaders"), "leaders object should exist"),
                () -> assertNotNull(json.getList("leaders.today"), "leaders.today should exist"),
                () -> assertNotNull(json.getList("leaders.week"), "leaders.week should exist"),
                () -> assertNotNull(json.getList("leaders.month"), "leaders.month should exist")
        );

        List<Map<String, Object>> leadersMonth = json.getList("leaders.month");
        if (leadersMonth != null && !leadersMonth.isEmpty()) {
            for (int i =0 ; i < leadersMonth.size(); i++) {
                Map<String, Object> leader = leadersMonth.get(i);
                assertAll("Leader [" + i + "]",
                        nonEmptyString(leader, "name"),
                        nonEmptyString(leader, "level"),
                        nonEmptyString(leader, "progress"),
                        nonEmptyString(leader, "score")
                );
            }
        }
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
                () -> assertNotNull(root.get("days"), "days не должен быть null")
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