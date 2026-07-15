package ru.tutor.steps;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.api.StoriesApi;
import ru.tutor.base.BaseApiSteps;
import ru.tutor.utils.AllureAttachments;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

    @Step("Валидация ответа списка видео")
    public void validateVideosResponse(Response response) {
        assertSuccessStatusCode(response);
        assertContentTypeJson(response);
        assertCorsHeaders(response);
        String body = response.getBody().asString();
        assertNotNull(body, "Тело ответа не должно быть null");
        assertFalse(body.isEmpty(), "Тело ответа не должно быть пустым");
    }

    @Step("Извлечение id первого видео из списка")
    public String extractFirstVideoId(Response videosResponse) {
        String videoId = videosResponse.jsonPath().getString("items.[0].id");
        assertNotNull(videoId, "id видео не должен быть null");
        assertFalse(videoId.isEmpty(), "id видео не должен быть пустым");
        Allure.addAttachment("Extracted videoId", videoId);
        return videoId;
    }
}