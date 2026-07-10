package ru.tutor.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.api.UserOrderApi;
import ru.tutor.base.BaseApiSteps;
import ru.tutor.utils.AllureAttachments;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class OrderSteps extends BaseApiSteps {
    private final UserOrderApi orderApi;

    public OrderSteps (String baseUrl) {
        super(baseUrl);
        this.orderApi = new UserOrderApi(baseUrl);
    }

    public static OrderSteps withBaseUrl(String baseUrl) {
        return new OrderSteps(baseUrl);
    }

    @Step("GET /user/orders/payments - история платежей")
    public Response getOrdersPayments(String authToken) {
        Response response = orderApi.getOrdersPayments(authToken);

        AllureAttachments.attachJson("Orders Payments Response", response.getBody().asString());
        return response;
    }

    @Step("Валидация ответа истории платежей")
    public void validateOrdersPaymentsResponse(Response response) {
        assertSuccessStatusCode(response);
        assertContentTypeJson(response);
        assertCorsHeaders(response);
    }

    @Step("GET /user/orders/bonus-access-info - информация по бонусному доступу")
    public Response getOrdersBonusAccessInfo(String authToken) {
        Response response = orderApi.getBonusAccessInfo(authToken);
        AllureAttachments.attachJson("Bonus Access Info Response", response.getBody().asString());
        return response;
    }

    @Step("GET /user/orders/subscription/state - состояние подписки")
    public Response getOrdersSubscriptionState(String authToken) {
        Response response = orderApi.getSubscriptionState(authToken);
        AllureAttachments.attachJson("Subscription State Response", response.getBody().asString());
        return response;
    }

    @Step("PUT /user/orders/pause/{days} - пауза подписки")
    public Response pauseOrder(String authToken, int days) {
        Response response = orderApi.pauseOrder(authToken, 7);
        AllureAttachments.attachJson("Orders Pause Response", response.getBody().asString());
        return response;
    }

    @Step("PUT /user/orders/unpause - снфтие паузы")
    public Response unpauseOrder(String authToken) {
        Response response = orderApi.unpauseOrder(authToken);
        AllureAttachments.attachJson("Orders UnpauseResponse", response.getBody().asString());
        return response;
    }

    @Step("Валидация пустого []")
    public void validateEmptyArrayResponse(Response response) {
        int code = response.getStatusCode();
        if (code == 200) {
            assertContentTypeJson(response);
            assertCorsHeaders(response);
            assertEquals("[]", response.getBody().asString().trim(),
                    "Тело ответа должно быть пустым массивом");
        } else if (code == 400) {
            assertEquals("ERR_CANT_FIND_SUBSCRIPTION_ORDER", response.jsonPath().getString("error")
            );
        }  else {
            fail("Неожиданный статус: " + code);
        }


    }
}
