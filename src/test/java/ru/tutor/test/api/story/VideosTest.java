package ru.tutor.test.api.story;

import io.qameta.allure.*;
import io.qameta.allure.junit5.AllureJunit5;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tutor.config.TestConfig;
import ru.tutor.steps.account.AuthSteps;
import ru.tutor.steps.content.VideoSteps;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(AllureJunit5.class)
@Epic("API Тесты пользователя")
@Feature("Видео-подкасты")
@DisplayName("Тесты эндпоинтов видео")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VideosTest {

    private final AuthSteps userSteps = new AuthSteps(TestConfig.getCurrentApiUrl());
    private final VideoSteps videoSteps = new VideoSteps(TestConfig.getCurrentApiUrl());
    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();

    private static String token;

    @BeforeAll
    public static void setUp() {
        Allure.label("component", "API");
        Allure.label("feature", "Videos");
    }

    @BeforeEach
    public void setUpAuth() {
        token = step("Логин пользователя", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );
    }

    @Test
    @Order(1)
    @DisplayName("GET /user/videos - успешное получение списка видео")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("positive")
    void getVideosSuccess() {
        Response response = step("GET /user/videos", () ->
                videoSteps.getVideos(token)
        );
        step("Валидация ответа", () ->
                videoSteps.validateVideosListResponse(response)
        );
    }

    @Test
    @Order(2)
    @DisplayName("GET /user/videos - без авторизации")
    void getVideosWithoutAuth() {
        Response response = step("GET /user/videos без токена", () ->
                videoSteps.getVideos("")
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode()));
    }
    @Test
    @Order(3)
    @DisplayName("GET /user/videos - инвалид токен")
    void getVideosInvalidToken() {
        Response response = step("GET /user/videos инвалид токен", () ->
                videoSteps.getVideos("invalid token")
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(4)
    @DisplayName("GET /user/videos/{id} - детали видео")
    @Severity(SeverityLevel.NORMAL)
    @Tag("positive")
    void getVideosByIdSuccess() {
        Response response = step("GET /user/videos", () ->
                videoSteps.getVideos(token)
        );

        String videoId = step("Извлечение id первого видео", () ->
                videoSteps.extractFirstVideoId(response)
        );

        Response detailsResponse = step("GET /user/videos/" +  videoId, () ->
                videoSteps.getVideosDetails(token, videoId)
        );
        step("Валидация ответа", () ->
                videoSteps.validateVideosDetailsResponse(detailsResponse)
        );
    }

    @Test
    @Order(5)
    @DisplayName("GET /user/videos/{id} без авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void getVideoDetailsWithoutAuth() {
        Response response = step("GET /user/videos/{id} без токена", () ->
                videoSteps.getVideosDetails("","16")
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(6)
    @DisplayName("GET /user/videos/watched/last - последний просмотр")
    @Severity(SeverityLevel.NORMAL)
    @Tag("positive")
    void getWatchedLastSuccess() {
        Response response = step ("GET /user/videos/watched/last", () ->
                videoSteps.getWatchedLast(token)
        );
        step("Валидация ответа", () ->
                videoSteps.validateVideosWatchedLastResponse(response));
    }

    @Test
    @Order(7)
    @DisplayName("GET /user/videos/watches/last без авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void getWatchedLastWithoutAuth() {
        Response response = step("GET /user/videos/watched/last - без токена", () ->
                videoSteps.getWatchedLast("")
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode()));
    }
}
