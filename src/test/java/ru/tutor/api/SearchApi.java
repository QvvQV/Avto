package ru.tutor.api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.base.BaseApi;
import ru.tutor.constants.ApiEndpoints;

import java.util.List;
import java.util.Map;

public class SearchApi extends BaseApi {

    public SearchApi(String baseUrl) {
        super(baseUrl);
    }

    @Step("POST /user/search/history - сохранение истории поиска")
    public Response saveSearchHistory(String authToken, List<String> queryList) {
        return postWithToken(ApiEndpoints.SEARCH_HISTORY, authToken, Map.of("queryList", queryList));
    }

    @Step("GET /user/search/history - получение списка истории запроса")
    public Response getSearchHistory(String authToken) {
        return getWithToken(ApiEndpoints.SEARCH_HISTORY, authToken);
    }
}
