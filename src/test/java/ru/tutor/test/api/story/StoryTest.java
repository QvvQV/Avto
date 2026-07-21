package ru.tutor.test.api.story;


import io.qameta.allure.*;
import io.qameta.allure.junit5.AllureJunit5;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tutor.config.TestConfig;
import ru.tutor.model.fortune.GameWheelRewardResponse;
import ru.tutor.steps.StoriesSteps;
import ru.tutor.steps.UserSteps;


import java.util.List;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(AllureJunit5.class)
@Epic("API Тесты пользователя")
@Feature("Сторис/Баннер/Тутс")
@DisplayName("Тесты эндпоинта Сторис/Баннер/Тутс")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StoryTest {

    private final UserSteps userSteps = new UserSteps(TestConfig.getCurrentApiUrl());
    private final StoriesSteps storiesSteps = new StoriesSteps(TestConfig.getCurrentApiUrl());
    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();

    private static String token;
    private static String slug;
    private static List<GameWheelRewardResponse> rewards;

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

    //    @Test
    @Order(1)
    @DisplayName("Успешное получение списка наград игрового колеса (Проверка 200 OK)")
    @Story("Основной сценарий")
    @Severity(SeverityLevel.CRITICAL)
    void getGameWheelRewardsSuccess() {

        step("POST /user/stories/screen", () ->
                storiesSteps.showStories(token)
        );

//        rewards = step("Валидация ответа со списком наград", () ->
//                gameWheelRewardsSteps.validateGameWheelRewardsResponse(response)
//        );
//
//        step("Проверка структуры наград", () -> {
//            gameWheelRewardsSteps.validaterewardStructure(rewards);
//
//            Allure.addAttachment("Количество наград", "text/plain",
//                    "Получено наград: " + rewards.size());
//        });
    }

    @Test
    @Order(2)
    @DisplayName("Успешное получение списка доступных сторис/историй")
    void getStoriesSuccess() {
        Response response = step("GET /user/stories", () ->
                storiesSteps.getStories(token)
        );

        step("Валидация ответа", () ->
                storiesSteps.validateStoriesResponse(response)
        );
    }

    @Test
    @Order(3)
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
    @Order(4)
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
    @Order(5)
    @DisplayName("Интеграционный тест: список сторис → детали истории")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("integration")
    void getStoriesAndDetailsSuccess() {
        Response listResponse = step("GET /user/stories", () ->
                storiesSteps.getStories(token)
        );
        storiesSteps.validateStoriesResponse(listResponse);

        String ulid = step("Извлечение ulid из списка", () ->
                storiesSteps.extractFirstUlid(listResponse)
        );

        Response detailsResponse = step("GET /user/stories/{ulid} — детали истории", () ->
                storiesSteps.getStoriesUlid(token, ulid)
        );

        step("валидация ответа", () ->
                storiesSteps.validateStoriesResponse(detailsResponse)
        );
    }

    @Test
    @Order(6)
    @DisplayName("Список сторис без авторизации")
    @Severity(SeverityLevel.NORMAL)
    void getStoriesUlidWithoutAuth() {
        Response listResponse = step("GET /user/stories", () ->
                storiesSteps.getStories(token)
        );
        storiesSteps.validateStoriesResponse(listResponse);

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
    @Order(7)
    @DisplayName("Список сторис с невалидным токеном")
    @Severity(SeverityLevel.NORMAL)
    void getStoriesUlidInvalidToken() {
        Response listResponse = step("GET /user/stories", () ->
                storiesSteps.getStories(token)
        );
        storiesSteps.validateStoriesResponse(listResponse);

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
    @Order(8)
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
    @Order(9)
    @DisplayName("Детали баннера")
    @Severity(SeverityLevel.NORMAL)
    void getBannersSuccess() {
        Response bannerResponse = step("GET /user/banners/{slug} — детали баннера", () ->
                storiesSteps.getBannerDetails(token, "test")
        );
        step("Валидация ответа", () ->
                storiesSteps.validateStoriesResponse(bannerResponse)
        );
    }

    @Test
    @Order(10)
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
    @Order(11)
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
    @Order(12)
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
    @Order(13)
    @DisplayName("Получение списка категорий тутсов")
    @Severity(SeverityLevel.NORMAL)
    @Tag("positive")
    void getTutsCategoriesSuccess() {
        Response response = step("GET /user/tuts-categories", () ->
                storiesSteps.getTutsCategories(token)
        );
        step("Валидация ответа", () ->
                storiesSteps.validateStoriesResponse(response)
        );
    }

    @Test
    @Order(14)
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
    @Order(15)
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
    @Order(16)
    @DisplayName("Интеграция: категории тутсов → feed по первой категории")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("integration")
    void getTutsCategoriesAndFeedSuccess() {
        Response categoriesResponse = step("GET /user/tuts-categories", () ->
                storiesSteps.getTutsCategories(token)
        );
        storiesSteps.validateStoriesResponse(categoriesResponse);

        String categoryId = step("Извлечение id первой категории", () ->
                storiesSteps.extractFirstCategoryId(categoriesResponse)
        );

        Response feedResponse = step("GET /user/tuts/category/{id}/feed", () ->
                storiesSteps.getCategoryFeed(token, categoryId)
        );

        step("Валидация ответа ленты", () ->
                storiesSteps.validateStoriesResponse(feedResponse)
        );
    }

    @Test
    @Order(17)
    @DisplayName("Получение категорий тутсов без авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void getTutsCategoriesWithoutAuthAndFeed() {
        Response categoriesResponse = step("GET /user/tuts-categories", () ->
                storiesSteps.getTutsCategories(token)
        );
        storiesSteps.validateStoriesResponse(categoriesResponse);

        String categoryId = step("Извлечение id первой категории", () ->
                storiesSteps.extractFirstCategoryId(categoriesResponse)
        );
        Response feedResponse = step("GET /user/tuts/category/{id}/feed без токена", () ->
                storiesSteps.getCategoryFeed("", categoryId)
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, feedResponse.getStatusCode())
        );
    }

    @Test
    @Order(18)
    @DisplayName("Получение категорий тутсов с инвалидным токеном")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void getTutsCategoriesInvalidTokenAndFeed() {
        Response categoriesResponse = step("GET /user/tuts-categories", () ->
                storiesSteps.getTutsCategories(token)
        );
        storiesSteps.validateStoriesResponse(categoriesResponse);

        String categoryId = step("Извлечение id первой категории", () ->
                storiesSteps.extractFirstCategoryId(categoriesResponse)
        );
        Response feedResponse = step("GET /user/tuts/category/{id}/feed с инвалидным токеном", () ->
                storiesSteps.getCategoryFeed("invalid_token", categoryId)
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, feedResponse.getStatusCode())
        );
    }

    @Test
    @Order(19)
    @DisplayName("Получение категорий тутсов с пустой категорией")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void getTutsEmptyCategoriesAndFeed() {

        Response feedResponse = step("GET /user/tuts/category/{id}/feed с пустой категорией", () ->
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
    @Tag("positive")
    void getRandomListTutsSuccess() {
        Response response = step("GET /user/tuts/random успешное получени списка", () ->
                storiesSteps.getRandomListTuts(token)
        );
        step("Валидация ответа", () ->
                storiesSteps.validateStoriesResponse(response)
        );
    }

    @Test
    @Order(21)
    @DisplayName("Получение списка без токена авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void getRandomListTutsWithoutAuth() {
        Response response = step("GET /user/tuts/random получение списка без авторизации", () ->
                storiesSteps.getRandomListTuts("")
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode())
        );
    }

    @Test
    @Order(22)
    @DisplayName("Получение списка инвалидный токен")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void getRandomListTutsInvalidToken() {
        Response response = step("GET /user/tuts/random получени списка с инвалидным токеном", () ->
                storiesSteps.getRandomListTuts("invalid_token")
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode())
        );
    }
}