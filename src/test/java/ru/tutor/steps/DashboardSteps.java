package ru.tutor.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.api.dashboards.DashboardsAllApi;
import ru.tutor.base.BaseApiSteps;
import ru.tutor.utils.AllureAttachments;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DashboardSteps extends BaseApiSteps  {
    private final DashboardsAllApi  dashboardsAllApi;

    public DashboardSteps(String baseUrl) {
        super(baseUrl);
        dashboardsAllApi = new DashboardsAllApi(baseUrl);
    }

    public static DashboardSteps withBaseUrl(String baseUrl) {
        return new DashboardSteps(baseUrl);
    }

    @Step("GET /user/dashboard/info - список дашбордов")
    public Response getDashboardInfo(String authToken) {
        Response response = dashboardsAllApi.dashboardInfo(authToken);
        AllureAttachments.attachJson("Dashboard Info Response", response.getBody().asString());
        return response;
    }

    @Step("Валидация ответа списка дашбордов")
    public void validateDashboardInfoResponse(Response response) {
        assertSuccessStatusCode(response);
        assertContentTypeJson(response);
        assertCorsHeaders(response);
        String body = response.getBody().asString();
        assertNotNull(body, "Тело ответа не должно быть null");
        assertFalse(body.isEmpty(), "Тело ответа не должно быть пустым");
        List<String> dashboars = response.jsonPath().getList("$", String.class);
        assertNotNull(dashboars, "Список дашбордов не должен быть null");
        assertFalse(dashboars.isEmpty(), "Список дашбордов не должен быть пустым");
        for (String dashboard : dashboars) {
            assertNotNull(dashboard, "Элемент дашборда не должен быть null");
            assertFalse(dashboard.trim().isEmpty(), "Элемент дашборда не должен быть пустым");
        }
    }
}