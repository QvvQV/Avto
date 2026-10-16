package ru.tutor.test.api.story;


import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import ru.tutor.config.TestConfig;
import ru.tutor.flow.account.FreshUserFixture;
import ru.tutor.steps.account.AuthSteps;
import ru.tutor.steps.content.StoriesSteps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;
import ru.tutor.constants.Tags;

@Epic("API Тесты пользователя")
@Feature("Сторис/Баннер/Тутс")
@DisplayName("Тесты эндпоинта Сторис/Баннер/Тутс")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag(Tags.INTEGRATION)
@Tag(Tags.API)
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

    @Test
    @Order(1)
    @DisplayName("Успешное получение списка доступных сторис/историй")
    @Tag(Tags.CONTRACT)
    void getStoriesSuccess() {
        token = step("Логин пользователя", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );

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
    @Tag(Tags.CONTRACT)
    void getStoriesWithoutAuth() {
        token = step("Логин пользователя", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );

        Response response = step("GET /user/stories", () ->
                storiesSteps.getStories("")
        );
        step("Проверка статуса 401", () ->
                storiesSteps.assertUnauthorizedWithMessage(response, "JWT Token not found")
        );
    }

    @Test
    @Order(3)
    @DisplayName("Список достпуных сторис с невалидным токеном")
    @Tag(Tags.CONTRACT)
    void getStoriesWithInvalidToken() {
        token = step("Логин пользователя", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );

        Response response = step("GET /user/stories", () ->
                storiesSteps.getStories("invalidToken")
        );
        step("Проверка статуса 401", () ->
                storiesSteps.assertUnauthorizedWithMessage(response, "Invalid JWT Token"));
    }

    @Test
    @Order(4)
    @DisplayName("Интеграционный тест: список сторис → детали истории")
    @Severity(SeverityLevel.CRITICAL)
    void getStoriesAndDetailsSuccess() {
        token = step("Логин пользователя", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );

        Response listResponse = step("GET /user/stories", () ->
                storiesSteps.getStories(token)
        );

        String ulid = step("Извлечение ulId из списка", () ->
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
    @Tag(Tags.CONTRACT)
    void getStoriesUlIdWithoutAuth() {
        token = step("Логин пользователя", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );

        Response listResponse = step("GET /user/stories", () ->
                storiesSteps.getStories(token)
        );

        String ulid = step("Извлечение ulId из списка", () ->
                storiesSteps.extractFirstUlid(listResponse)
        );
        Response response = step("GET /user/stories/{ulId} без токена", () ->
                storiesSteps.getStoriesUlid("", ulid)
        );
        step("Проверка статуса 401", () ->
                storiesSteps.assertUnauthorizedWithMessage(response, "JWT Token not found")
        );
    }

    @Test
    @Order(6)
    @DisplayName("Список сторис с невалидным токеном")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.CONTRACT)
    void getStoriesUlIdInvalidToken() {
        token = step("Логин пользователя", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );

        Response listResponse = step("GET /user/stories", () ->
                storiesSteps.getStories(token)
        );

        String ulid = step("Извлечение ulId из списка", () ->
                storiesSteps.extractFirstUlid(listResponse)
        );
        Response response = step("GET /user/stories/{ulid} инвалидного токена", () ->
                storiesSteps.getStoriesUlid("invalid_token", ulid)
        );
        step("Проверка статуса 401", () ->
                storiesSteps.assertUnauthorizedWithMessage(response, "Invalid JWT Token")
        );
    }

    @Test
    @Order(7)
    @DisplayName("Список сторис с невалидным UlId")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.CONTRACT)
    void getStoriesInvalidUlId() {

        token = step("Логин пользователя", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );

        Response response = step("GET /user/stories/{ulId} без токена", () ->
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
    @Tag(Tags.CONTRACT)
    void getBannersSuccess() {
        token = step("Логин пользователя", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );

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
    @Tag(Tags.CONTRACT)
    void getBannersInvalidToken() {
        token = step("Логин пользователя", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );

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
    @Tag(Tags.CONTRACT)
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
    @Tag(Tags.CONTRACT)
    void getBannersWithInvalidSlug() {
        token = step("Логин пользователя", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );

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
    @Tag(Tags.POSITIVE)
    @Tag(Tags.CONTRACT)
    void getTutsCategoriesSuccess() {
        String token = step("Регистрация нового пользователя", () ->
                new FreshUserFixture().register().token());

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
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void getTutsCategoriesWithoutAuth() {
        Response response = step("GET /user/tuts-categories без токена", () ->
                storiesSteps.getTutsCategories("")
        );

        step("Проверка статуса 401", () ->
                storiesSteps.assertUnauthorizedWithMessage(response, "JWT Token not found"));
    }

    @Test
    @Order(14)
    @DisplayName("Получение категорий тутсов с инвалидным токеном")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void getTutsCategoriesWithInvalid() {
        Response response = step("GET /user/tuts-categories с инвалидным токеном", () ->
                storiesSteps.getTutsCategories("invalid")
        );

        step("Проверка статуса 401", () ->
                storiesSteps.assertUnauthorizedWithMessage(response, "Invalid JWT Token")
        );
    }

    @Test
    @Order(15)
    @DisplayName("Интеграция: категории тутсов → feed по первой категории")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    void getTutsCategoriesAndFeedSuccess() {
        String token = step("Регистрация нового пользователя", () ->
                new FreshUserFixture().register().token());

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
    @DisplayName("Интеграция: рандомный список тутсов → детали тутса")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.POSITIVE)
    void getRandomTutAndDetailsSuccess() {
        String token = step("Регистрация нового пользователя", () ->
                new FreshUserFixture().register().token());

        Response randomResponse = step("GET /user/tuts/random", () ->
                storiesSteps.getRandomListTuts(token));
        String slug = step("Извлечение slug тутса", () ->
                storiesSteps.extractFirstTutSlug(randomResponse));
        Response detailsResponse = step("GET /user/tuts/{slug}", () ->
                storiesSteps.getTutDetails(slug, token));
        step("Валидация деталей тутса", () ->
                storiesSteps.validateTutDetailsResponse(detailsResponse));
    }

    @Test
    @Order(17)
    @DisplayName("Получение категорий тутсов без авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void getTutsCategoriesWithoutAuthAndFeed() {
        token = step("Логин пользователя", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );

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
                storiesSteps.assertUnauthorizedWithMessage(feedResponse, "JWT Token not found")
        );
    }

    @Test
    @Order(18)
    @DisplayName("Получение категорий тутсов с инвалидным токеном")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void getTutsCategoriesInvalidTokenAndFeed() {
        token = step("Логин пользователя", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );

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
                storiesSteps.assertUnauthorizedWithMessage(feedResponse, "Invalid JWT Token")
        );
    }

    @Test
    @Order(19)
    @DisplayName("Получение категорий тутсов с пустой категорией")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void getTutsEmptyCategoriesAndFeed() {

        token = step("Логин пользователя", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );

        Response feedResponse = step("GET /user/tuts/category/{id}/feed - с пустой категорией", () ->
                storiesSteps.getCategoryFeed(token, "")
        );
        step("Проверка статуса 404", () ->
                assertEquals(404, feedResponse.getStatusCode())
        );
    }

    @Test
    @Order(20)
    @DisplayName("Успешное получение рандомного списка тутсов")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.POSITIVE)
    @Tag(Tags.CONTRACT)
    void getRandomListTutsSuccess() {
        String token = step("Регистрация нового пользователя", () ->
                new FreshUserFixture().register().token());

        Response response = step("GET /user/tuts/random - успешное получени списка", () ->
                storiesSteps.getRandomListTuts(token)
        );
        step("Валидация ответа", () ->
                storiesSteps.validateRandomTutsResponse(response)
        );
    }

    @Test
    @Order(21)
    @DisplayName("Получение списка без токена авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void getRandomListTutsWithoutAuth() {
        Response response = step("GET /user/tuts/random - получение списка без авторизации", () ->
                storiesSteps.getRandomListTuts("")
        );
        step("Проверка статуса 401", () ->
                storiesSteps.assertUnauthorizedWithMessage(response, "JWT Token not found")
        );
    }

    @Test
    @Order(22)
    @DisplayName("Получение списка инвалидный токен")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void getRandomListTutsInvalidToken() {
        Response response = step("GET /user/tuts/random - получени списка с инвалидным токеном", () ->
                storiesSteps.getRandomListTuts("invalid_token")
        );
        step("Проверка статуса 401", () ->
                storiesSteps.assertUnauthorizedWithMessage(response, "Invalid JWT Token")
        );
    }

    @Test
    @Order(23)
    @DisplayName("Валидация полей списка тутсов")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    @Tag(Tags.CONTRACT)
    void getValidationListTutsSuccess() {
        String token = step("Регистрация нового пользователя", () ->
                new FreshUserFixture().register().token());

        Response response = step("GET /user/tuts/random-list", () ->
                storiesSteps.getRandomListTutsList(token)
        );
        step("Валидация ответа", () ->
                storiesSteps.validateTutsListResponse(response)
        );
    }

    @Test
    @Order(24)
    @DisplayName("Получение списка без авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void getListTutsWithoutAuth() {
        Response response = step("GET /user/tuts/random - получение списка без авторизации", () ->
                storiesSteps.getRandomListTutsList("")
        );
        step("Проверка статуса 401", () ->
                storiesSteps.assertUnauthorizedWithMessage(response, "JWT Token not found")
        );
    }

    @Test
    @Order(25)
    @DisplayName("Получение списка инвалидный токен")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void getListTutsInvalidToken() {
        Response response = step("GET /user/tuts/random - получени списка с инвалидным токеном", () ->
                storiesSteps.getRandomListTutsList("invalid_token")
        );
        step("Проверка статуса 401", () ->
                storiesSteps.assertUnauthorizedWithMessage(response, "Invalid JWT Token")
        );
    }

    @Test
    @Order(26)
    @DisplayName("Успешная фиксация просмотра сторис с одним ULID")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    @Tag(Tags.CONTRACT)
    void markStoriesViewSuccess() {
        String freshToken = step("Регистрация нового пользователя", () ->
                new FreshUserFixture().register().token());

        Response listResponse = step("GET /user/stories", () ->
                storiesSteps.getStories(freshToken));

        String ulid = step("Извлечение первого ULID", () ->
                storiesSteps.extractFirstUlid(listResponse));

        Response response = step("POST /user/stories/view", () ->
                storiesSteps.markStoriesViewed(freshToken, List.of(ulid)));

        step("Валидация: статус 201, тело []", () ->
                storiesSteps.validateStoriesViewResponse(response));
    }

    @Test
    @Order(27)
    @DisplayName("Фиксация просмотра сторис с несколькими ULID")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.POSITIVE)
    @Tag(Tags.CONTRACT)
    void markStoriesViewMultipleUlIds() {
        String freshToken = step("Регистрация нового пользователя", () ->
                new FreshUserFixture().register().token());

        Response listResponse = step("GET /user/stories", () ->
                storiesSteps.getStories(freshToken));

        List<String> ulids = step("Извлечение первых 3 ULIDs", () -> {
            List<Map<String, Object>> stories = listResponse.jsonPath().getList("$");
            assertNotNull(stories, "Список сторис не должен быть null");
            int count = Math.min(stories.size(), 3);
            List<String> result = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                String u = (String) stories.get(i).get("ulid");
                assertNotNull(u, "ulId не должен быть null");
                result.add(u);
            }
            assertFalse(result.isEmpty(), "Хотя бы один ULID должен быть извлечён");
            return result;
        });

        Response response = step("POST /user/stories/view - фиксация " + ulids.size() + " ULIDs", () ->
                storiesSteps.markStoriesViewed(freshToken, ulids));

        step("Валидация: статус 201, тело []", () ->
                storiesSteps.validateStoriesViewResponse(response));
    }

    @Test
    @Order(28)
    @DisplayName("Фиксация просмотра сторис без токена авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void markStoriesViewWithoutAuth() {
        Response response = step("POST /user/stories/view - без токена", () ->
                storiesSteps.markStoriesViewed("", List.of("019e8dad-7ac5-5d96-51ec-ac6dd64d4e2c"))
        );

        step("Проверка статуса 401", () ->
                storiesSteps.assertUnauthorizedWithMessage(response, "JWT Token not found"));
    }

    @Test
    @Order(29)
    @DisplayName("Фиксация просмотра сторис с невалидным токеном")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void markStoriesViewWithInvalidToken() {
        Response response = step("POST /user/stories/view - невалидный токен", () ->
                storiesSteps.markStoriesViewed("invalid_token", List.of("019e8dad-7ac5-5d96-51ec-ac6dd64d4e2c"))
        );

        step("Проверка статуса 401", () ->
                storiesSteps.assertUnauthorizedWithMessage(response, "Invalid JWT Token"));
    }

    @Test
    @Order(30)
    @DisplayName("Фиксация просмотра сторис c пустым массивом ULID")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void markStoriesViewEmptyArray() {
        token = step("Логин пользователя", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );

        Response response = step("POST /user/stories/view - пустой массив", () ->
                storiesSteps.markStoriesViewed(token, List.of())
        );

        step("Проверка статуса 201", () ->
                storiesSteps.validateStoriesViewResponse(response));
    }

    @Test
    @Order(31)
    @DisplayName("Фиксация показа экрана сторис с произвольным ULID (сервер не валидирует)")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void markStoriesViewInvalidUlIds() {
        token = step("Логин пользователя", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );

        Response response = step("POST /user/stories/view - невалидный Ulid", () ->
                storiesSteps.markStoriesViewed(token, List.of("not-a-valid-ulid"))
        );

        step("Проверка статуса 201", () ->
                storiesSteps.validateStoriesViewResponse(response));
    }

    @Test
    @Order(32)
    @DisplayName("Успешная фиксация показа экрана с payload fortuna")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    @Tag(Tags.CONTRACT)
    void markStoryScreenSuccess() {
        token = step("Логин пользователя", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );

        Response response = step("POST /user/stories/screen - с payload fortuna", () ->
                storiesSteps.markStoryScreen(token, List.of("fortuna"))
        );

        step("Валидация: статус 201, тело []", () ->
                storiesSteps.validateStoryScreenResponse(response)
        );
    }

    @Test
    @Order(33)
    @DisplayName("Фиксация показа экрана сторис с несколькими payload")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.POSITIVE)
    @Tag(Tags.CONTRACT)
    void markStoryScreenMultiplePayloads() {
        token = step("Логин пользователя", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );

        Response response = step("POST /user/stories/screen - с payload fortuna", () ->
                storiesSteps.markStoryScreen(token, List.of("fortuna", "bonus", "promo"))
        );

        step("Валидация: статус 201, тело []", () ->
                storiesSteps.validateStoryScreenResponse(response)
        );
    }

    @Test
    @Order(34)
    @DisplayName("Фиксация показа экрана сторис без токена авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void markStoryScreenWithoutAuth() {
        Response response = step("POST /user/stories/screen - без токена", () ->
                storiesSteps.markStoryScreen("", List.of("fortuna")));

        step("Проверка статуса 401", ()->
                storiesSteps.assertUnauthorizedWithMessage(response, "JWT Token not found"));
    }

    @Test
    @Order(35)
    @DisplayName("Фиксация показа экрана сторис с невалидным токеном")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void markStoryScreenInvalidToken() {
        Response response = step("POST /user/stories/screen - без токена", () ->
                storiesSteps.markStoryScreen("invalid_token", List.of("fortuna")));

        step("Проверка статуса 401", ()->
                storiesSteps.assertUnauthorizedWithMessage(response, "Invalid JWT Token"));
    }

    @Test
    @Order(36)
    @DisplayName("Фиксация показа экрана сторис с пустым массивом payload")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.CONTRACT)
    void markStoryScreenEmptyArray() {
        token = step("Логин пользователя", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );

        Response response = step("POST /user/stories/screen - пустой массив", () ->
                storiesSteps.markStoryScreen(token, List.of()));

        step("Проверка статуса 201", ()->
                storiesSteps.validateStoryScreenResponse(response));
    }

    @Test
    @Order(37)
    @DisplayName("Фиксация показа экрана сторис с произвольным payload (сервер не валидирует)")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.CONTRACT)
    void markStoryScreenInvalidPayload() {
        Response response = step("POST /user/stories/screen - невалидный payload", () ->
                storiesSteps.markStoryScreen(token, List.of("invalid_payload_value")));

        step("Проверка статуса 201", ()->
                storiesSteps.validateStoryScreenResponse(response));
    }

    @Test
    @Order(38)
    @DisplayName("Успешная фиксация просмотра тутса (204)")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    void markTutsViewSuccess() {
        String token = step("Регистрация нового пользователя", () ->
                new FreshUserFixture().register().token());
        Response categories = step("GET /user/tuts-categories", () ->
                storiesSteps.getTutsCategories(token));
        String categoryId = step("Извлечение id первой категории", () ->
                storiesSteps.extractFirstCategoryId(categories));

        Response feedBefore = step("GET /user/tuts/category/{id}/feed (до)", () ->
                storiesSteps.getCategoryFeed(token, categoryId));
        String slug = step("Извлечение непросмотренного тутса", () ->
                storiesSteps.extractFirstUnwatchedTutSlug(feedBefore));

        Response response = step("POST /user/tuts/{slug}/view", () ->
                storiesSteps.markTutsViewed(token, slug));
        step("Проверка статуса 204", () -> assertEquals(204, response.getStatusCode()));

        Response feedAfter = step("GET /user/tuts/cetegory/{id}/feed (после)", () ->
                storiesSteps.getCategoryFeed(token, categoryId));
        step("watched=true", () ->
                storiesSteps.validateTutWatched(feedAfter, slug, true));
    }

    @Test
    @Order(39)
    @DisplayName("Фиксация просмотра тутса без токена")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void markTutsViewWithoutAuth() {
        Response response = step("POST /user/tuts/{slug}/view - без токена", () ->
                storiesSteps.markTutsViewed("", "test-slug")
        );

        step("Проверка статуса 401", () ->
                storiesSteps.assertUnauthorizedWithMessage(response, "JWT Token not found")
        );
    }

    @Test
    @Order(40)
    @DisplayName("Фиксация просмотра тутса с невалидным токеном")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void markTutsViewWithInvalidToken() {
        Response response = step("POST /user/tuts/{slug}/view - невалдиный токен", () ->
                storiesSteps.markTutsViewed("invalid_token", "test-slug")
        );

        step("Проверка статуса 401", () ->
                storiesSteps.assertUnauthorizedWithMessage(response, "Invalid JWT Token")
        );
    }

    @Test
    @Order(41)
    @DisplayName("Фиксация просмотра тутса с несуществующим slug")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void markTutsViewWithInvalidSlug() {
        token = step("Логин пользователя", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );

        Response response = step("POST /user/tuts/{slug}/view - несуществующий slug", () ->
                storiesSteps.markTutsViewed(token, "test-slug-125")
        );

        step("Проверка статуса 404", () ->
                assertEquals(404, response.getStatusCode())
        );
    }
}
