package ru.tutor.api.dashboards;

import io.restassured.response.Response;
import ru.tutor.base.BaseApi;
import ru.tutor.constants.ApiEndpoints;

import java.util.HashMap;
import java.util.Map;

/** Работа с курсом/продуктом */

public class DashboardsAllApi extends BaseApi {

    public DashboardsAllApi(String baseUrl) {
        super(baseUrl);
    }

    /**
     * GET /user/dashboard/ingo - получение списка приложений
     */
    public Response dashboardInfo(String authToken) {
        return getWithToken(ApiEndpoints.USER_DASHBOARD_INFO , authToken);
    }

}
