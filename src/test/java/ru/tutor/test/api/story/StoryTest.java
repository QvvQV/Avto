package ru.tutor.test.api.story;


import io.qameta.allure.*;
import io.qameta.allure.junit5.AllureJunit5;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tutor.config.TestConfig;
import ru.tutor.steps.AuthSteps;
import ru.tutor.steps.StoriesSteps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AllureJunit5.class)
@Epic("API Тесты пользователя")
@Feature("Сторис/Баннер/Тутс")
@DisplayName("Тесты эндпоинта Сторис/Баннер/Тутс")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StoryTest {

    private final AuthSteps userSteps = new AuthSteps(TestConfig.getCurrentApiUrl());
    private final StoriesSteps storiesSteps = new StoriesSteps(TestConfig.getCurrentApiUrl());
    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();

    private static String token;

    @BeforeAll
    public static void setUp() {
        Allure.label("component", "API");
        Allure.label("feature", "Stories/banners");
    }

    @BeforeEach
    void setUpAuth() {
        // Логин перед каждым тестом
        token = step("Логин пользователя", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );
    }

    @Test
    @Order(1)
    @DisplayName("Успешное получение списка доступных сторис/историй")
    void getStoriesSuccess() {
        Response response = step("GET /user/stories", () ->
                storiesSteps.getStories(token)
        );

        step("Валидация ответа", () ->
                storiesSteps.validateStoriesListResponse(response)
        );
    }

    @Test
    @Order(2)
    @DisplayName("Список доступных сторис без токена")
    void getStoriesWithoutAuth() {
        Response response = step("GET /user/stories", () ->
                storiesSteps.getStories("")
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode())
        );
    }

    @Test
    @Order(3)
    @DisplayName("Список достпуных сторис с невалидным токеном")
    void getStoriesWithInvalidToken() {
        Response response = step("GET /user/stories", () ->
                storiesSteps.getStories("invalidToken")
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode())
        );
    }

    @Test
    @Order(4)
    @DisplayName("Интеграционный тест: список сторис → детали истории")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("integration")
    void getStoriesAndDetailsSuccess() {
        Response listResponse = step("GET /user/stories", () ->
                storiesSteps.getStories(token)
        );

        String ulid = step("Извлечение ulid из списка", () ->
                storiesSteps.extractFirstUlid(listResponse)
        );

        Response detailsResponse = step("GET /user/stories/{ulid} — детали истории", () ->
                storiesSteps.getStoriesUlid(token, ulid)
        );

        step("Валидация ответа", () ->
                storiesSteps.validateStoriesDetailsResponse(detailsResponse)
        );
    }

    @Test
    @Order(5)
    @DisplayName("Список сторис без авторизации")
    @Severity(SeverityLevel.NORMAL)
    void getStoriesUlidWithoutAuth() {
        Response listResponse = step("GET /user/stories", () ->
                storiesSteps.getStories(token)
        );

        String ulid = step("Извлечение ulid из списка", () ->
                storiesSteps.extractFirstUlid(listResponse)
        );
        Response response = step("GET /user/stories/{ulid} без токена", () ->
                storiesSteps.getStoriesUlid("", ulid)
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode())
        );
    }

    @Test
    @Order(6)
    @DisplayName("Список сторис с невалидным токеном")
    @Severity(SeverityLevel.NORMAL)
    void getStoriesUlidInvalidToken() {
        Response listResponse = step("GET /user/stories", () ->
                storiesSteps.getStories(token)
        );

        String ulid = step("Извлечение ulid из списка", () ->
                storiesSteps.extractFirstUlid(listResponse)
        );
        Response response = step("GET /user/stories/{ulid} инвалидного токена", () ->
                storiesSteps.getStoriesUlid("invalid_token", ulid)
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode())
        );
    }

    @Test
    @Order(7)
    @DisplayName("Список сторис с невалидным Ulid")
    @Severity(SeverityLevel.NORMAL)
    void getStoriesInvalidUlid() {

        Response response = step("GET /user/stories/{ulid} без токена", () ->
                storiesSteps.getStoriesUlid(token, "invalid")
        );
        step("Проверка статуса 404", () ->
                assertEquals(404, response.getStatusCode())
        );
    }

    @Test
    @Order(8)
    @DisplayName("Детали баннера")
    @Severity(SeverityLevel.NORMAL)
    void getBannersSuccess() {
        Response bannerResponse = step("GET /user/banners/{slug} — детали баннера", () ->
                storiesSteps.getBannerDetails(token, "youtube")
        );
        step("Валидация ответа", () ->
                storiesSteps.validateBannerResponse(bannerResponse)
        );
    }

    @Test
    @Order(9)
    @DisplayName("Детали баннера невалидным токеном")
    @Severity(SeverityLevel.NORMAL)
    void getBannersInvalidToken() {
        Response bannerResponse = step("GET /user/banners/{slug} - невалидным токеном", () ->
                storiesSteps.getBannerDetails("invalid_token", "test")
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, bannerResponse.getStatusCode()));
    }

    @Test
    @Order(10)
    @DisplayName("Детали баннера без авторизации")
    @Severity(SeverityLevel.NORMAL)
    void getBannersWithoutAuth() {
        Response bannerResponse = step("GET /user/banners - без авторизации", () ->
                storiesSteps.getBannerDetails("", "test")
        );
        step("Проверка 401", () ->
                assertEquals(401, bannerResponse.getStatusCode())
        );
    }

    @Test
    @Order(11)
    @DisplayName("Детали невалидного баннера")
    @Severity(SeverityLevel.NORMAL)
    void getBannersWithInvalidSlug() {
        Response bannerResponse = step("GET /user/banners - невалидный slug", () ->
                storiesSteps.getBannerDetails(token, "invalid_slug")
        );
        step("Проверка 404", () ->
                assertEquals(404, bannerResponse.getStatusCode())
        );
    }

    @Test
    @Order(12)
    @DisplayName("Получение списка категорий тутсов")
    @Severity(SeverityLevel.NORMAL)
    @Tag("positive")
    void getTutsCategoriesSuccess() {
        Response response = step("GET /user/tuts-categories", () ->
                storiesSteps.getTutsCategories(token)
        );
        step("Валидация ответа", () ->
                storiesSteps.validateTutsCategoriesResponse(response)
        );
    }

    @Test
    @Order(13)
    @DisplayName("Получение категорий тутсов без авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void getTutsCategoriesWithoutAuth() {
        Response response = step("GET /user/tuts-categories без токена", () ->
                storiesSteps.getTutsCategories("")
        );

        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(14)
    @DisplayName("Получение категорий тутсов с инвалидным токеном")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void getTutsCategoriesWithInvalid() {
        Response response = step("GET /user/tuts-categories с инвалидным токеном", () ->
                storiesSteps.getTutsCategories("invalid")
        );

        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode())
        );
    }

    @Test
    @Order(15)
    @DisplayName("Интеграция: категории тутсов → feed по первой категории")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("integration")
    void getTutsCategoriesAndFeedSuccess() {
        Response categoriesResponse = step("GET /user/tuts-categories", () ->
                storiesSteps.getTutsCategories(token)
        );

        String categoryId = step("Извлечение id первой категории", () ->
                storiesSteps.extractFirstCategoryId(categoriesResponse)
        );

        Response feedResponse = step("GET /user/tuts/category/{id}/feed", () ->
                storiesSteps.getCategoryFeed(token, categoryId)
        );

        step("Валидация ответа ленты", () ->
                storiesSteps.validateCategoryFeedResponse(feedResponse)
        );
    }

    @Test
    @Order(16)
    @DisplayName("Получение категорий тутсов без авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void getTutsCategoriesWithoutAuthAndFeed() {
        Response categoriesResponse = step("GET /user/tuts-categories", () ->
                storiesSteps.getTutsCategories(token)
        );

        String categoryId = step("Извлечение id первой категории", () ->
                storiesSteps.extractFirstCategoryId(categoriesResponse)
        );
        Response feedResponse = step("GET /user/tuts/category/{id}/feed - без токена", () ->
                storiesSteps.getCategoryFeed("", categoryId)
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, feedResponse.getStatusCode())
        );
    }

    @Test
    @Order(17)
    @DisplayName("Получение категорий тутсов с инвалидным токеном")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void getTutsCategoriesInvalidTokenAndFeed() {
        Response categoriesResponse = step("GET /user/tuts-categories", () ->
                storiesSteps.getTutsCategories(token)
        );

        String categoryId = step("Извлечение id первой категории", () ->
                storiesSteps.extractFirstCategoryId(categoriesResponse)
        );
        Response feedResponse = step("GET /user/tuts/category/{id}/feed - с инвалидным токеном", () ->
                storiesSteps.getCategoryFeed("invalid_token", categoryId)
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, feedResponse.getStatusCode())
        );
    }

    @Test
    @Order(18)
    @DisplayName("Получение категорий тутсов с пустой категорией")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void getTutsEmptyCategoriesAndFeed() {

        Response feedResponse = step("GET /user/tuts/category/{id}/feed - с пустой категорией", () ->
                storiesSteps.getCategoryFeed(token, "")
        );
        step("Проверка статуса 404", () ->
                assertEquals(404, feedResponse.getStatusCode())
        );
    }

    @Test
    @Order(19)
    @DisplayName("Успешное получение рандомного списка тутсов")
    @Severity(SeverityLevel.NORMAL)
    @Tag("positive")
    void getRandomListTutsSuccess() {
        Response response = step("GET /user/tuts/random - успешное получени списка", () ->
                storiesSteps.getRandomListTuts(token)
        );
        step("Валидация ответа", () ->
                storiesSteps.validateRandomTutsResponse(response)
        );
    }

    @Test
    @Order(20)
    @DisplayName("Получение списка без токена авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void getRandomListTutsWithoutAuth() {
        Response response = step("GET /user/tuts/random - получение списка без авторизации", () ->
                storiesSteps.getRandomListTuts("")
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode())
        );
    }

    @Test
    @Order(21)
    @DisplayName("Получение списка инвалидный токен")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void getRandomListTutsInvalidToken() {
        Response response = step("GET /user/tuts/random - получени списка с инвалидным токеном", () ->
                storiesSteps.getRandomListTuts("invalid_token")
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode())
        );
    }

    @Test
    @Order(22)
    @DisplayName("Валидация полей списка тутсов")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("positive")
    void getValidationListTutsSuccess() {
        Response response = step("GET /user/tuts/random-list", () ->
                storiesSteps.getRandomListTutsList(token)
        );
        step("Валидация ответа", () ->
                storiesSteps.validateRandomTutsItem(response)
        );
    }

    @Test
    @Order(23)
    @DisplayName("Получение списка без авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void getListTutsWithoutAuth() {
        Response response = step("GET /user/tuts/random - получение списка без авторизации", () ->
                storiesSteps.getRandomListTutsList("")
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode())
        );
    }

    @Test
    @Order(24)
    @DisplayName("Получение списка инвалидный токен")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void getListTutsInvalidToken() {
        Response response = step("GET /user/tuts/random - получени списка с инвалидным токеном", () ->
                storiesSteps.getRandomListTutsList("invalid_token")
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode())
        );
    }

    @Test
    @Order(25)
    @DisplayName("Успешная фиксация просмотра сторис с одним ULID")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("positive")
    void markStoriesViewSuccess() {
        Response listResponse = step("GET /user/stories - получить список", () ->
                storiesSteps.getStories(token)
        );

        String ulid = step("Извлечение первого ULID", () ->
                storiesSteps.extractFirstUlid(listResponse)
        );

        Response response = step("POST /user/stories/view - фиксация просмотра", () ->
                storiesSteps.markStoriesViewed(token, List.of(ulid))
        );

        step("Валидация: статус 201 тело []", () ->
                storiesSteps.validateStoriesViewResponse(response));
    }

    @Test
    @Order(26)
    @DisplayName("Фиксация просмотра сторис с несколькими ULID")
    @Severity(SeverityLevel.NORMAL)
    @Tag("positive")
    void markStoriesViewMultipleUlids() {
        Response listResponse = step("GET /user/stories - получить список", () ->
                storiesSteps.getStories(token)
        );

        List<String> ulids = step("Извлечение первых 3 ULIDs", () -> {
            List<Map<String, Object>> stories = listResponse.jsonPath().getList("$");
            assertNotNull(stories, "Список сторис не должен быть null");
            int count = Math.min(stories.size(), 3);
            List<String> result = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                String u = (String) stories.get(i).get("ulid");
                assertNotNull(u, "ulid не должен быть null");
                result.add(u);
            }
            assertFalse(result.isEmpty(), "Хотя бы один ULID должен быть извлечён");
            return result;
        });

        Response response = step("POST /user/stories/view - фиксация " + ulids.size() + " ULIDs", () ->
                storiesSteps.markStoriesViewed(token, ulids)
        );

        step("Валидация: статус 201б тело []", () ->
                storiesSteps.validateStoriesViewResponse(response)
        );
    }

    @Test
    @Order(27)
    @DisplayName("Фиксация просмотра сторис без токена авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void markStoriesViewWithoutAuth() {
        Response response = step("POST /user/stories/view - без токена", () ->
                storiesSteps.markStoriesViewed("", List.of("019e8dad-7ac5-5d96-51ec-ac6dd64d4e2c"))
        );

        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(28)
    @DisplayName("Фиксация просмотра сторис с невалидным токеном")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void markStoriesViewWithInvalidToken() {
        Response response = step("POST /user/stories/view - невалидный токен", () ->
                storiesSteps.markStoriesViewed("invalid_token", List.of("019e8dad-7ac5-5d96-51ec-ac6dd64d4e2c"))
        );

        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(29)
    @DisplayName("Фиксация просмотра сторис c пустым массивом ULID")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void markStoriesViewEmptyArray() {
        Response response = step("POST /user/stories/view - пустой массив", () ->
                storiesSteps.markStoriesViewed(token, List.of())
        );

        step("Проверка статуса 201", () ->
                storiesSteps.validateStoriesViewResponse(response));
    }

    @Test
    @Order(30)
    @DisplayName("Фиксация показа экрана сторис с произвольным ULID (сервер не валидирует)")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void markStoriesViewInvalidUlids() {
        Response response = step("POST /user/stories/view - невалидный Ulid", () ->
                storiesSteps.markStoriesViewed(token, List.of("not-a-valid-ulid"))
        );

        step("Проверка статуса 201", () ->
                storiesSteps.validateStoriesViewResponse(response));
    }

    @Test
    @Order(31)
    @DisplayName("Успешная фиксация показа экрана с payload fortuna")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("positive")
    void markStoryScreenSuccess() {
        Response response = step("POST /user/stories/screen - с payload fortuna", () ->
                storiesSteps.markStoryScreen(token, List.of("fortuna"))
        );

        step("Валидация: статус 201, тело []", () ->
                storiesSteps.validateStoryScreenResponse(response)
        );
    }

    @Test
    @Order(32)
    @DisplayName("Фиксация показа экрана сторис с несколькими payload")
    @Severity(SeverityLevel.NORMAL)
    @Tag("positive")
    void markStoryScreenMultiplePayloads() {
        Response response = step("POST /user/stories/screen - с payload fortuna", () ->
                storiesSteps.markStoryScreen(token, List.of("fortuna", "bonus", "promo"))
        );

        step("Валидация: статус 201, тело []", () ->
                storiesSteps.validateStoryScreenResponse(response)
        );
    }

    @Test
    @Order(33)
    @DisplayName("Фиксация показа экрана сторис без токена авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void markStoryScreenWithoutAuth() {
        Response response = step("POST /user/stories/screen - без токена", () ->
                storiesSteps.markStoryScreen("", List.of("fortuna")));

        step("Проверка статуса 401", ()->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(34)
    @DisplayName("Фиксация показа экрана сторис с невалидным токеном")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void markStoryScreenInvalidToken() {
        Response response = step("POST /user/stories/screen - без токена", () ->
                storiesSteps.markStoryScreen("invalid_token", List.of("fortuna")));

        step("Проверка статуса 401", ()->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(35)
    @DisplayName("Фиксация показа экрана сторис с пустым массивом payload")
    @Severity(SeverityLevel.NORMAL)
    @Tag("edge-case")
    void markStoryScreenEmptyArray() {
        Response response = step("POST /user/stories/screen - пустой массив", () ->
                storiesSteps.markStoryScreen(token, List.of()));

        step("Проверка статуса 201", ()->
                storiesSteps.validateStoryScreenResponse(response));
    }

    @Test
    @Order(36)
    @DisplayName("Фиксация показа экрана сторис с произвольным payload (сервер не валидирует)")
    @Severity(SeverityLevel.NORMAL)
    @Tag("edge-case")
    void markStoryScreenInvalidPayload() {
        Response response = step("POST /user/stories/screen - невалидный payload", () ->
                storiesSteps.markStoryScreen(token, List.of("invalid_payload_value")));

        step("Проверка статуса 201", ()->
                storiesSteps.validateStoryScreenResponse(response));
    }

    @Test
    @Order(37)
    @DisplayName("Успешная фиксация просмотра тутса")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("positive")
    @Tag("integration")
    void markTutsViewSuccess() {
        Response categoriesResponse = step("GET /user/tuts-categories", () ->
                storiesSteps.getTutsCategories(token)
        );

        String categoryId = step("Извлечение id первой категории", () ->
                storiesSteps.extractFirstCategoryId(categoriesResponse)
        );

        Response feedResponse = step ("GET /user/tuts/category/{id}/feed", () ->
                storiesSteps.getCategoryFeed(token, categoryId)
        );

        String slug = step("Извлечение slug первого тутса", () -> {
            List<Map<String, Object>> items = feedResponse.jsonPath().getList("items");
            assertNotNull(items, "Список items не должен быть null");
            assertFalse(items.isEmpty(), "Список items не должен быть пуст");
            String s =(String) items.get(0).get("slug");
            assertNotNull(s, "slug не должен быть null");
            return s;
        });

        Response response = step("POST /user/tuts/{slug}/view - фиксация просмотра", () ->
                storiesSteps.markTutsViewed(token, slug)
        );

        step("проверка статуса 204", () ->
                assertEquals(204, response.getStatusCode()));
    }

    @Test
    @Order(38)
    @DisplayName("Фиксация просмотра тутса без токена")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void markTutsViewWithoutAuth() {
        Response response = step("POST /user/tuts/{slug}/view - без токена", () ->
                storiesSteps.markTutsViewed("", "test-slug")
        );

        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode())
        );
    }

    @Test
    @Order(39)
    @DisplayName("Фиксация просмотра тутса с невалидным токеном")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void markTutsViewWithInvalidToken() {
        Response response = step("POST /user/tuts/{slug}/view - невалдиный токен", () ->
                storiesSteps.markTutsViewed("invalid_token", "test-slug")
        );

        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode())
        );
    }

    @Test
    @Order(40)
    @DisplayName("Фиксация просмотра тутса с несуществующим slug")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void markTutsViewWithInvalidSlug() {
        Response response = step("POST /user/tuts/{slug}/view - несуществующий slug", () ->
                storiesSteps.markTutsViewed(token, "test-slug-125")
        );

        step("Проверка статуса 404", () ->
                assertEquals(404, response.getStatusCode())
        );
    }
}