package ru.tutor.api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.base.BaseApi;
import ru.tutor.constants.ApiEndpoints;

public class PaymentSuccessApi extends BaseApi {

    public PaymentSuccessApi(String baseUrl) {
        super(baseUrl);
    }

    @Step("GET /order/status/{orderNumber}")
    public Response getOrderStatus(String orderNumber) {
        return getWithoutToken(ApiEndpoints.ORDER_STATUS.formatted(orderNumber));
    }

    @Step("GET /order/success/{orderNumber}")
    public Response getOrderSuccess(String orderNumber) {
        return getWithoutToken(ApiEndpoints.ORDER_SUCCESS.formatted(orderNumber));
    }
}
