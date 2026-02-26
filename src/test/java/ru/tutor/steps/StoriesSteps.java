package ru.tutor.steps;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.tutor.api.GameWheelApi;
import ru.tutor.api.StoriesApi;
import ru.tutor.constants.GameWheelRewardsConstants;
import ru.tutor.model.StoryScreenResponse;
import ru.tutor.model.fortune.GameWheelStartGameResponse;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Шаги для сторис
 */

public record StoriesSteps(StoriesApi storiesApi) {

    public static StoriesSteps withBaseUrl(String baseUrl) {
        return new StoriesSteps(new StoriesApi(baseUrl));
    }


    @Step("видимость сторис")
    public Response showStories(String authToken) {
        return this.storiesApi.storyScreen(authToken);
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



    // ================ ВАЛИДАЦИЯ ОШИБОК ================


}