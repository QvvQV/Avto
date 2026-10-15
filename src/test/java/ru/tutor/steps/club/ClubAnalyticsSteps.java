package ru.tutor.steps.club;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.utils.AllureAttachments;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ClubAnalyticsSteps extends ClubBaseSteps {

    public ClubAnalyticsSteps(String baseUrl) {
        super(baseUrl);
    }

    @Step("GET /user/club/v2/analytics/links - аналитика по ссылкам")
    public Response getClubAnalyticsLinks(String authToken) {
        Response response = clubApi.getClubAnalyticsLinks(authToken);
        AllureAttachments.attachJson("Club Analytics Links Response", response.getBody().asString());
        return response;
    }

    @Step("Валидация структуры analytics/links")
    public void validateAnalyticsLinks(Response response) {
        assertBasicResponse(response);
        assertJsonKeyExists(response, "filters");
        List<Map<String, Object>> items = response.jsonPath().getList("items");
        assertNotNull(items, "items не должен быть null");
        for (int i = 0; i < items.size(); i++) {
            Map<String, Object> link = items.get(i);
            assertAll("analytics_link[" + i + "]",
                    () -> positiveInt(link, "id"),
                    () -> nonEmptyString(link, "name"),
                    () -> containsKey(link, "product"),
                    () -> nonNegative(link, "referralsTotal"),
                    () -> nonNegative(link, "level1"),
                    () -> nonNegative(link, "level2"),
                    () -> nonNegative(link, "level3"),
                    () -> nonNegative(link, "income"));
        }
        assertJsonKeyExists(response, "pagination");
        assertJsonFieldExists(response, "pagination.count");
    }

    @Step("GET /user/club/v2/analytics/monthly-income - аналитика дохода по уровням")
    public Response getClubAnalyticsMonthlyIncome(String authToken) {
        Response response = clubApi.getClubAnalyticsMonthlyIncome(authToken);
        AllureAttachments.attachJson("Club Monthly Income Response", response.getBody().asString());
        return response;
    }

    @Step("Валидация структуры analytics/monthly-income")
    public void validateAnalyticsMonthlyIncome(Response response) {
        assertBasicResponse(response);
        assertJsonKeyExists(response, "filters");
        List<Map<String, Object>> items = response.jsonPath().getList("items");
        assertNotNull(items, "items не должен быть null");
        for (int i = 0; i < items.size(); i++) {
            Map<String, Object> income = items.get(i);
            assertAll("income_level[" + i + "]",
                    () -> assertTrue(((Number) income.get("level")).intValue() >= 1,
                            "level должен быть >= 1"),
                    () -> nonEmptyString(income, "label"),
                    () -> nonNegative(income, "rate"),
                    () -> nonNegative(income, "active"),
                    () -> nonNegative(income, "incomePerMonth"));
        }
        assertJsonKeyExists(response, "totals");
        assertJsonFieldExists(response, "totals.active");
        assertJsonFieldExists(response, "totals.incomePerMonth");
    }

    @Step("GET /user/club/v2/links/summary - сводка по ссылкам")
    public Response getClubLinksSummary(String authToken) {
        Response response = clubApi.getClubLinksSummary(authToken);
        AllureAttachments.attachJson("Club Links Summary Response", response.getBody().asString());
        return response;
    }

    @Step("Валидация структуры links/summary")
    public void validateLinksSummary(Response response) {
        assertBasicResponse(response);
        assertJsonKeyExists(response, "filters");
        assertAll("links/summary - ключи",
                () -> assertJsonFieldExists(response, "totals.linksCount"),
                () -> assertJsonFieldExists(response, "totals.clicks"),
                () -> assertJsonFieldExists(response, "totals.activeReferrals"),
                () -> assertJsonFieldExists(response, "totals.activePremiums"),
                () -> assertJsonFieldExists(response, "totals.income"),
                () -> assertJsonFieldExists(response, "breakdown.trial"),
                () -> assertJsonFieldExists(response, "breakdown.lite"),
                () -> assertJsonFieldExists(response, "breakdown.premium"),
                () -> assertJsonFieldExists(response, "breakdown.lifetime"),
                () -> assertJsonFieldExists(response, "breakdown.paymentError"),
                () -> assertJsonFieldExists(response, "breakdown.unsubscribed"),
                () -> assertJsonFieldExists(response, "series.clicks"),
                () -> assertJsonFieldExists(response, "series.income"),
                () -> assertJsonFieldExists(response, "series.activePremiums"));
    }
}
