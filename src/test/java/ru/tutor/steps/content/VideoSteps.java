package ru.tutor.steps.content;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.function.Executable;
import ru.tutor.api.content.StoriesApi;
import ru.tutor.base.BaseApiSteps;
import ru.tutor.utils.AllureAttachments;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class VideoSteps extends BaseApiSteps {
    private final StoriesApi storiesApi;

    public VideoSteps(String baseUrl) {
        super(baseUrl);
        this.storiesApi = new StoriesApi(baseUrl);
    }

    @Step("GET /user/videos - список видео")
    public Response getVideos(String authToken) {
        Response response = storiesApi.showVideos(authToken);
        AllureAttachments.attachJson("Videos Response", response.getBody().asString());
        return response;
    }

    @Step("GET /user/videos/{id} - детали видео")
    public Response getVideosDetails(String authToken, String id) {
        Response response = storiesApi.detailsUserVideos(authToken, id);
        AllureAttachments.attachJson("Videos Details Response", response.getBody().asString());
        return response;
    }

    @Step("GET /user/videos/wathed/last - последние просмотры")
    public Response getWatchedLast(String authToken) {
        Response response = storiesApi.getWatchedLast(authToken);
        AllureAttachments.attachJson("Watched Last Response", response.getBody().asString());
        return response;
    }

    private void validateVideoItem(Map<String, Object> item, String label) {
        assertAll(label,
                positiveInt(item, "id"),
                notNull (item, "name"),
                notNull(item,"type"),
                notNull(item,"isPaidContent"),
                notNull(item,"description"),
                notNull(item,"kinescopeId"),
                notNull(item,"poster"),
                containsKey(item,"productId"),
                containsKey(item,"showCategoryId"),
                containsKey(item,"showCategory")
        );
    }

    @Step("Валидация ответа /user/videos/watched/last")
    public void validateVideosWatchedLastResponse(Response response) {
        assertBasicResponse(response);
        List<Map<String, Object>> items = assertJsonArray(response, "Массив");

        for (int i =0; i < items.size(); i++) {
            Map<String, Object> item = items.get(i);
            assertAll("Просмотр [" + i + "]",
                    notNull(item,"type"),
                    notNull(item,"orderNumber"),
                    notNull(item,"productName"),
                    () -> assertFalse(item.get("orderNumber").toString().isEmpty(), "orderNumber не пустой"),
                    () -> assertFalse(item.get("productName").toString().isEmpty(), "productName не пустой")
            );
        }
    }

    @Step("Валидация деталей видео")
    public void validateVideosDetailsResponse(Response response) {
        assertBasicResponse(response);
        Map<String, Object> root = response.jsonPath().getMap("$");
        assertNotNull(root, "Тело должно быть JSON-объектом");
        validateVideoItem(root, "Детали видео");
        }

    @Step("Валидация ответа списка видео /user/videos")
    public void validateVideosListResponse(Response response) {
        assertBasicResponse(response);
        Map<String, Object> root = response.jsonPath().getMap("$");
        assertNotNull(root, "Тело должно быть JSON-объектом");

        List<Map<String, Object>> items = response.jsonPath().getList("items");
        assertNotNull(items, "items не должен быть null");
        assertFalse(items.isEmpty(), "items не должен быть пустым");

        for (int i = 0; i < items.size(); i++) {
            validateVideoItem(items.get(i), "Видео [" + i + "]");
            assertKeyExists(items.get(i),"createdAt");
        }
    }

    @Step("Извлечение id первого видео из списка")
    public String extractFirstVideoId(Response videosResponse) {
        String videoId = videosResponse.jsonPath().getString("items.[0].id");
        assertNotNull(videoId, "id видео не должен быть null");
        assertFalse(videoId.isEmpty(), "id видео не должен быть пустым");
        Allure.addAttachment("Extracted videoId", videoId);
        return videoId;
    }

    protected void assertKeyExists(Map<String, Object> map, String field) {
        assertTrue(map.containsKey(field), field + " должен быть в ответе");
    }

    protected Executable containsKey(Map<String, Object> item, String field) {
        return () -> assertKeyExists(item, field);
    }
}