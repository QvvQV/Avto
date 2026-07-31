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

    @Step("Видимость сторис")
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
        assertBasicResponse(response);
    }

    @Step("Валидация ответа категорий тутсов")
    public void validateTutsCategoriesResponse(Response response) {
        assertBasicResponse(response);

        List<Map<String, Object>> categories = response.jsonPath().getList("$");
        assertNotNull(categories, "Список категорий не должен быть null");
        assertFalse(categories.isEmpty(), "Список категорий не должен быть пуст");

        for (int i = 0; i < categories.size(); i++) {
            Map<String, Object> category = categories.get(i);
            assertAll("Категория [" + i + "]",
                    () -> assertTrue(((Number) category.get("id")).intValue() > 0),
                    () -> assertNotNull(category.get("name")),
                    () -> assertNotNull(category.get("slug")),
                    () -> assertTrue(((Number) category.get("totalCount")).intValue() >= 0),
                    () -> assertTrue(((Number) category.get("unwatchedCount")).intValue() >= 0)
            );
        }
    }

    @Step("Валидация ответа ленты категории")
    public void validateCategoryFeedResponse(Response response) {
        assertBasicResponse(response);

        List<Map<String, Object>> items = response.jsonPath().getList("items");
        assertNotNull(items, "items не должен быть null");

        if (items.isEmpty()) return;

        for (int i = 0; i < items.size(); i++) {
            Map<String, Object> item = items.get(i);
            assertAll("Элемент ленты [" +i + "]",
                    () -> assertNotNull(item.get("slug"), "slug не null"),
                    () -> assertNotNull(item.get("watched"), "watched не null")
            );
        }
    }

    @Step("Валидация ответа списка сторис")
    public void validateStoriesListResponse(Response response) {
        assertBasicResponse(response);

        List<Map<String, Object>> stories = response.jsonPath().getList("$");
        assertNotNull(stories, "Список сторис не должен быть null");
        if (stories.isEmpty()) return;

        for (int i = 0; i < stories.size(); i++) {
            Map<String, Object> story = stories.get(i);
            assertAll("Сторис [" + i + "]",
                    () -> assertNotNull(story.get("ulid")),
                    () -> assertNotNull(story.get("title")),
                    () -> assertNotNull(story.get("imageUri")),
                    () -> assertTrue(story.containsKey("imageWideUri")),
                    () -> assertTrue(story.containsKey("link"))
            );
        }
    }

    @Step("Валидация ответа деталей истории")
    public void validateStoriesDetailsResponse(Response response) {
        assertBasicResponse(response);

        Map<String, Object> story = response.jsonPath().getMap("$");
        assertNotNull(story, "Список сторис не должен быть null");

            assertAll("Детали истории",
                    () -> assertNotNull(story.get("ulid")),
                    () -> assertNotNull(story.get("title")),
                    () -> assertNotNull(story.get("imageUri")),
                    () -> assertTrue(story.containsKey("imageWideUri")),
                    () -> assertTrue(story.containsKey("link"))
            );

        // Проверяем слайды
        List<Map<String, Object>> slides = response.jsonPath().getList("slides");
        assertNotNull(slides, "slides не должен быть null");
        if (slides.isEmpty()) return;

        for (int i = 0; i < slides.size(); i++) {
            Map<String, Object> slide = slides.get(i);
            assertAll("Слайд [" + i + "]",
                    () -> assertNotNull(slide.get("ulid")),
                    () -> assertNotNull(slide.get("imageUri")),
                    () -> assertTrue(slide.containsKey("imageWideUri")),
                    () -> assertTrue(slide.containsKey("kinescopeId")),
                    () -> assertTrue(slide.containsKey("link")),
                    () -> assertNotNull(slide.get("customSort"))
                    );
        }
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

    @Step("GET /user/tuts/random-list - возвращает список всех тутсов")
    public Response getRandomListTutsList(String authToken) {
        return storiesApi.showTutsList(authToken);
    }

    @Step("Валидация структуры тутса")
    public void validateRandomTutsItem(Response response) {
        assertAll("Поверка полей тутса",
                () -> assertNotNull(response.jsonPath().getString("slug"), "slug не должен быть null"),
                () -> assertNotNull(response.jsonPath().getString("title"), "title не должен быть null"),
                () -> assertNotNull(response.jsonPath().getString("link"), "link не должен быть null"),
                () -> assertNotNull(response.jsonPath().getString("authorName"), "authorName не должен быть null"),
                () -> assertNotNull(response.jsonPath().getString("authorImage"), "authorImage не должен быть null"),
                () -> assertNotNull(response.jsonPath().getString("poster.original"), "poster.original не должен быть null"));
    }
}