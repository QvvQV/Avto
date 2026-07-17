package ru.tutor.steps;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.tutor.api.StoriesApi;
import ru.tutor.base.BaseApiSteps;
import ru.tutor.model.StoryScreenResponse;
import ru.tutor.utils.AllureAttachments;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Шаги для сторис
 */

public class StoriesSteps extends BaseApiSteps {
    private final StoriesApi storiesApi;

    public StoriesSteps(String baseUrl) {
        super(baseUrl);
        this.storiesApi = new StoriesApi(baseUrl);
    }

    @Step("видимость сторис")
    public Response showStories(String authToken) {
        Response response = storiesApi.storyScreen(authToken);
        AllureAttachments.attachJson("Story Screen Response", response.getBody().asString());
        return response;
    }

    @Step("Валидация успешного ответа при запуске игры")
    public StoryScreenResponse validateSuccessResponse(Response response) {
        // Базовая валидация ответа
        response.then()
                .statusCode(201)
                .contentType(ContentType.JSON);

        // Валидация структуры JSON
        StoryScreenResponse responseBody = response.as(StoryScreenResponse.class);
        assertNotNull(responseBody, "Пустое тело ответа");
        return responseBody;


        // Преобразуем в модель
    }


    @Step("GET /user/stories - список историй")
    public Response getStories(String authToken) {
        Response response = storiesApi.showStories(authToken);
        AllureAttachments.attachJson("Stories Response", response.getBody().asString());
        return response;
    }

    @Step("GET /user/stories/{ulid} - детали истории")
    public Response getStoriesUlid(String authToken, String ulid) {
        Response response = storiesApi.showStoriesId(authToken, ulid);
        AllureAttachments.attachJson("Stories Response", response.getBody().asString());
        return response;
    }

    @Step("Извлечние первого ulid из списка сторис")
    public String extractFirstUlid(Response response) {
        String ulid = response.jsonPath().getString("[0].ulid");
        assertNotNull(ulid, "ulid не должен быть null");
        assertFalse(ulid.isEmpty(), "ulid не должен быть null");
        Allure.addAttachment("Extracted ulid", ulid);
        return ulid;
    }

    @Step("Валидация ответа списка историй")
    public void validateStoriesResponse(Response response) {
        assertSuccessStatusCode(response);
        assertContentTypeJson(response);
        assertCorsHeaders(response);
    }

    @Step("Детали баннера")
    public Response getBannerDetails(String authToken, String slug) {
        Response response = storiesApi.showBanners(authToken, slug);
        AllureAttachments.attachJson("Banner Response", response.getBody().asString());
        return response;
    }

    @Step("GET /user/tuts-categories - список категорий тутсов")
    public Response getTutsCategories(String authToken) {
        Response response = storiesApi.showTutsCategories(authToken);
        AllureAttachments.attachJson("Tuts Categories Response", response.getBody().asString());
        return response;
    }

    @Step("Извлечение id первой категории тутсов")
    public String extractFirstCategoryId(Response response) {
        String categoryId = response.jsonPath().getString("[0].id");
        assertNotNull(categoryId, "id категории не должен быть null");
        assertFalse(categoryId.isEmpty(), "id категории не должен быть пустым");
        Allure.addAttachment("Extracted id", categoryId);
        return categoryId;
    }

    @Step("GET /user/tuts/category/{id}/feed - список тутсов категории")
    public Response getCategoryFeed(String authToken, String categoryId) {
        Response response = storiesApi.showCategoryFeed(authToken, categoryId);
        AllureAttachments.attachJson("Category Feed", response.getBody().asString());
        return response;
    }

    @Step("GET /user/tuts/random — возвращает случайно перемешанный список доступных тутсов")
    public Response getRandomListTuts(String authToken) {
        Response response = storiesApi.showTutsRandomList(authToken);
        AllureAttachments.attachJson("Random List Tuts", response.getBody().asString());
        return response;
    }

}