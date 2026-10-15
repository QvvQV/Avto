package ru.tutor.steps.club;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.utils.AllureAttachments;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ClubHomeSteps extends ClubBaseSteps {

    public ClubHomeSteps(String baseUrl) {
        super(baseUrl);
    }

    @Step("GET /user/club/v2/home/analytics - аналитика главной клуба")
    public Response getClubHomeAnalytics(String authToken) {
        Response response = clubApi.getClubHomeAnalytics(authToken);
        AllureAttachments.attachJson("Club Home Analytics Response", response.getBody().asString());
        AllureAttachments.attachJson("Club Home Analytics Headers", response.getHeaders().toString());
        return response;
    }

    @Step("Валидация структуры home/analytics")
    public void validateHomeAnalytics(Response response) {
        assertBasicResponse(response);
        assertAll("home/analytics - базовые ключи",
                () -> assertJsonKeyExists(response, "filters"),
                () -> assertJsonKeyExists(response, "totals"),
                () -> assertJsonKeyExists(response, "series"));

        assertAll("totals - ключи",
                () -> assertJsonFieldExists(response, "totals.clicks"),
                () -> assertJsonFieldExists(response, "totals.newSubscriptions"),
                () -> assertJsonFieldExists(response, "totals.subscriptions"),
                () -> assertJsonFieldExists(response, "totals.unsubscribed"),
                () -> assertJsonFieldExists(response, "totals.paymentError"),
                () -> assertJsonFieldExists(response, "totals.income"));

        List<Map<String, Object>> clicks = response.jsonPath().getList("series.clicks");
        assertNotNull(clicks, "series.clicks не должен быть null");
        for (int i = 0; i < clicks.size(); i++) {
            Map<String, Object> point = clicks.get(i);
            assertAll("series.clicks[" + i + "]",
                    () -> assertNotNull(point.get("date"), "date не должен быть null"),
                    () -> assertNotNull(point.get("value"), "value не должен быть null"));
        }
    }

    @Step("GET /user/club/v2/home/banners - баннеры клуба")
    public Response getClubHomeBanners(String authToken) {
        Response response = clubApi.getClubHomeBanners(authToken);
        AllureAttachments.attachJson("Club Home Banners Response", response.getBody().asString());
        return response;
    }

    @Step("Валидация структуры home/banners")
    public void validateHomeBanners(Response response) {
        assertBasicResponse(response);
        List<Map<String, Object>> items = response.jsonPath().getList("items");
        assertNotNull(items, "items не должен быть null");
        for (int i = 0; i < items.size(); i++) {
            Map<String, Object> banner = items.get(i);
            assertAll("banner[" + i + "]",
                    () -> assertNotNull(banner.get("id"), "id"),
                    () -> nonEmptyString(banner, "title"),
                    () -> nonEmptyString(banner, "description"),
                    () -> url(banner.get("image"), "image"),
                    () -> assertOptionalUrl(banner.get("mobileImage"), "mobileImage"),
                    () -> assertOptionalUrl(banner.get("link"), "link"));
        }
    }

    @Step("GET /user/club/v2/home/links - ссылки участника клуба")
    public Response getClubHomeLinks(String authToken) {
        Response response = clubApi.getClubHomeLinks(authToken);
        AllureAttachments.attachJson("Club Home Links Response", response.getBody().asString());
        return response;
    }

    @Step("Валидация структуры home/links")
    public void validateHomeLinks(Response response) {
        assertBasicResponse(response);
        assertJsonKeyExists(response, "filters");
        List<Map<String, Object>> items = response.jsonPath().getList("items");
        assertNotNull(items, "items не должен быть null");
        for (int i = 0; i < items.size(); i++) {
            Map<String, Object> link = items.get(i);
            assertAll("link[" + i + "]",
                    () -> positiveInt(link, "id"),
                    () -> nonEmptyString(link, "name"),
                    () -> url(link.get("url"), "url"),
                    () -> nonNegative(link, "clicks"),
                    () -> nonNegative(link, "trial"),
                    () -> nonNegative(link, "premium"),
                    () -> nonNegative(link, "paymentError"),
                    () -> nonNegative(link, "unsubscribed"),
                    () -> nonNegative(link, "income"));
        }
    }

    @Step("GET /user/club/v2/home/news - новости клуба")
    public Response getClubHomeNews(String authToken) {
        Response response = clubApi.getClubHomeNews(authToken);
        AllureAttachments.attachJson("Club Home News Response", response.getBody().asString());
        return response;
    }

    @Step("Валидация структуры home/news")
    public void validateHomeNews(Response response) {
        assertBasicResponse(response);
        assertAll("home/news - ключи",
                () -> assertJsonKeyExists(response, "items"),
                () -> assertJsonKeyExists(response, "pagination"),
                () -> assertJsonFieldExists(response, "pagination.count"),
                () -> assertJsonFieldExists(response, "pagination.total"),
                () -> assertJsonFieldExists(response, "pagination.per_page"),
                () -> assertJsonFieldExists(response, "pagination.page"),
                () -> assertJsonFieldExists(response, "pagination.pages"));
    }

    @Step("GET /user/club/v2/home/news/{newsId} - детальная информация новости")
    public Response getClubNewsDetail(String authToken, String newsId) {
        Response response = clubApi.getClubNewsDetail(authToken, newsId);
        AllureAttachments.attachJson("Club News Detail Response", response.getBody().asString());
        return response;
    }

    @Step("Валидация структуры home/news/{slug}")
    public void validateClubNewsDetail(Response response, String expectedSlug) {
        assertBasicResponse(response);
        assertAll("news detail - ключи",
                () -> assertJsonFieldExists(response, "id"),
                () -> assertEquals(expectedSlug, response.jsonPath().getString("slug"),
                        "slug должен совпадать с запрошенным"),
                () -> assertJsonFieldNotEmpty(response, "title"),
                () -> assertJsonFieldNotEmpty(response, "publishedAt"),
                () -> assertJsonFieldNotEmpty(response, "content"));
    }


}
