package ru.tutor.steps.catalog;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.api.catalog.SearchApi;
import ru.tutor.base.BaseApiSteps;
import ru.tutor.utils.AllureAttachments;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SearchSteps extends BaseApiSteps {
    private final SearchApi searchApi;

    public SearchSteps(String baseUrl) {
        super(baseUrl);
        this.searchApi = new SearchApi(baseUrl);
    }

    // ==================== POST /user/search/history ====================

    @Step("POST /user/search/history - сохранение истории поиска")
    public Response saveSearchHistory(String authToken, List<String> queryList) {
        Response response = searchApi.saveSearchHistory(authToken, queryList);
        AllureAttachments.attachJson("Search History Response", response.getBody().asString());
        return response;
    }

    @Step("Валидация успешного ответа сохранения истории поиска (201)")
    public void validateSearchHistoryResponse(Response response) {
        assertEquals(201, response.getStatusCode(),
                "Ожидался статус 201, но получен: " + response.getStatusCode());
        assertContentTypeJson(response);
        List<?> body = response.jsonPath().getList("$");
        assertNotNull(body, "Тело ответа не должно быть null");
    }

    @Step("Валидация ошибки queryList (422)")
    public void validateSearchHistoryValidationError(Response response) {
        assertEquals(422, response.getStatusCode(),
                "Ожидался статус 422, но получен: " +  response.getStatusCode());
        Map<String, Object> messages = response.jsonPath().getMap("error.messages");
        assertEquals("Значение не должно быть пустым.", messages.get("queryList[0]"));
        assertEquals("Значение не должно быть пустым.", messages.get("queryList[1]"));
        assertEquals("Значение не должно быть пустым.", messages.get("queryList[2]"));
    }

    // ==================== GET /user/search/history ====================

    @Step("GET /user/search/history - получение данных поиска")
    public Response getSearchHistory(String authToken) {
        Response response = searchApi.getSearchHistory(authToken);
        AllureAttachments.attachJson("Search History GET Response", response.getBody().asString());
        return response;
    }

    @Step("Валидация успешного ответа получения истории (200)")
    public void validateGetSearchHistoryResponse(Response response) {
        assertBasicResponse(response);
        List<String> actual = response.jsonPath().getList("$",  String.class);
        assertNotNull(actual, "Список истории поиска не должен быть null");
    }

    @Step("Валидация что GET возвращает сохранённые запросы")
    public void validateSearchHistoryContains(Response response, List<String> expectedQueries) {
        List<String> actual = response.jsonPath().getList("$",  String.class);
        assertNotNull(actual, "Список истории поиска не должен быть null");
        for(String query : expectedQueries) {
            assertTrue(actual.contains(query),
                    "История поиска должна содержать: " + query);
        }
    }
}
