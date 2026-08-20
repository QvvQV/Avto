package ru.tutor.steps;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
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

    @Step("Валидация ответа категорий тутсов")
    public void validateTutsCategoriesResponse(Response response) {
        assertBasicResponse(response);
        List<Map<String, Object>> categories = response.jsonPath().getList("$");
        assertNotNull(categories, "Список категорий не должен быть null");
        assertFalse(categories.isEmpty(), "Список категорий не должен быть пуст");

        for (int i = 0; i < categories.size(); i++) {
            Map<String, Object> category = categories.get(i);
            assertAll("Категория [" + i + "]",
                    positiveInt(category,"id"),
                    notNull(category,"name"),
                    notNull(category,"slug"),
                    nonNegative(category,"totalCount"),
                    nonNegative(category,"unwatchedCount")
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
                    notNull(item,"slug"),
                    notNull(item,"watched")
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
                    notNull(story,"ulid"),
                    notNull(story,"title"),
                    notNull(story,"imageUri"),
                    containsKey(story,"imageWideUri"),
                    containsKey(story,"link")
            );
        }
    }

    @Step("Валидация ответа деталей истории")
    public void validateStoriesDetailsResponse(Response response) {
        assertBasicResponse(response);

        Map<String, Object> story = response.jsonPath().getMap("$");
        assertNotNull(story, "Список сторис не должен быть null");

            assertAll("Детали истории",
                    notNull(story,"ulid"),
                    notNull(story,"title"),
                    notNull(story,"imageUri"),
                    containsKey(story,"imageWideUri"),
                    containsKey(story,"link")
            );

        // Проверяем слайды
        List<Map<String, Object>> slides = response.jsonPath().getList("slides");
        assertNotNull(slides, "slides не должен быть null");
        if (slides.isEmpty()) return;

        for (int i = 0; i < slides.size(); i++) {
            Map<String, Object> slide = slides.get(i);
            assertAll("Слайд [" + i + "]",
                    notNull(slide,"ulid"),
                    notNull(slide,"imageUri"),
                    containsKey(slide,"imageWideUri"),
                    containsKey(slide,"kinescopeId"),
                    containsKey(slide,"link"),
                    notNull(slide,"customSort")
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
        assertBasicResponse(response);
        assertAll("Проверка полей тутса",
                () -> assertJsonFieldExists(response,"slug"),
                () -> assertJsonFieldExists(response,"title"),
                () -> assertJsonFieldExists(response,"link"),
                () -> assertJsonFieldExists(response,"authorName"),
                () -> assertJsonFieldExists(response,"authorImage"),
                () -> assertJsonFieldExists(response,"poster.original")
        );
    }

    @Step("Валидация ответа рандомного списка тутсов /user/tuts/random")
    public void validateRandomTutsResponse(Response response) {
        assertBasicResponse(response);
        List<String> slugs = response.jsonPath().getList("$", String.class);
        assertNotNull(slugs, "Список тутсов не должен быть null");
        assertFalse(slugs.isEmpty(), "Список тутсов не должен быть пустым");
        for (int i =0; i < slugs.size(); i++) {
            assertNotNull(slugs.get(i), "slug [" + i + "] не должен быть null");
            assertFalse(slugs.get(i).isBlank(), "slug [" + i + "] не должен быть пустым");
        }
    }

    @Step("Валидация ответа баннера /user/banners/{slug}")
    public void validateBannerResponse(Response response) {
        assertBasicResponse(response);
        Map<String, Object> root = response.jsonPath().getMap("$");
        assertNotNull(root, "Тело должно быть JSON-объектом");

        assertAll("Баннер",
                nonEmptyString(root, "slug"),
                nonEmptyString(root, "link")
        );
    }

    @Step("POST /user/stories/view - фиксация просмотра сторис")
    public Response markStoriesViewed(String authToken, List<String> ulids) {
        Response response = storiesApi.markStoriesViewed(authToken, ulids);
        AllureAttachments.attachJson("Stories View Response", response.getBody().asString());
        return response;
    }

    @Step("Валидация успешного ответа фиксации просмотра")
    public void validateStoriesViewResponse(Response response) {
        assertEquals(201, response.getStatusCode(),
                "Ожидался статус 201, но полдучен: " + response.getStatusCode());
        assertContentTypeJson(response);
        List<?> body = response.jsonPath().getList("$");
        assertNotNull(body, "Тело ответа не должно быть null");
    }

    @Step("POST /user/stories/screen - фиксация показа экрана сторис")
    public Response markStoryScreen(String authToken, List<String> payloads) {
        Response response = storiesApi.storyScreenPost(authToken, payloads);
        AllureAttachments.attachJson("Story Screen Response", response.getBody().asString());
        return response;
    }

    @Step("Валидация цспешного ответа фиксации показа экрана (201)")
    public void validateStoryScreenResponse(Response response) {
        assertEquals(201, response.getStatusCode(),
                "Ожидался статус 201, но получен: " + response.getStatusCode());
        assertContentTypeJson(response);
        List<?> body = response.jsonPath().getList("$");
        assertNotNull(body, "Тело ответа не должно быть null");
    }

    @Step("POST /user/tuts/{slug}/view - фиксация просмотра тутса")
    public Response markTutsViewed(String authToken, String slug) {
        Response response = storiesApi.markTutsViewed(authToken, slug);
        AllureAttachments.attachJson("Tuts View Response", response.getBody().asString());
        return response;
    }
}