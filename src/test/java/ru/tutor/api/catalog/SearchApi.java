package ru.tutor.api.catalog;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.base.BaseApi;
import ru.tutor.constants.EndpointsCatalog;

import java.util.List;
import java.util.Map;

public class SearchApi extends BaseApi {

    public SearchApi(String baseUrl) {
        super(baseUrl);
    }

    @Step("POST /user/search/history - сохранение истории поиска")
    public Response saveSearchHistory(String authToken, List<String> queryList) {
        return postWithToken(EndpointsCatalog.SEARCH_HISTORY, authToken, Map.of("queryList", queryList));
    }

    @Step("GET /user/search/history - получение списка истории запроса")
    public Response getSearchHistory(String authToken) {
        return getWithToken(EndpointsCatalog.SEARCH_HISTORY, authToken);
    }
}
