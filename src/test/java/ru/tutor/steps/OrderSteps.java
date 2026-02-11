package ru.tutor.steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.tutor.api.PaymentApi;
import ru.tutor.model.order.OrderRequest;

public class OrderSteps {

    private final PaymentApi paymentApi;

    public OrderSteps(String baseUrl) {
        this.paymentApi = new PaymentApi(baseUrl);
    }

    @Step("Создать заказ и получить ответ")
    public Response createOrder(OrderRequest orderRequest) {
        validateOrderRequest(orderRequest);
        return paymentApi.createOrderWithoutAuth(orderRequest);
    }

    @Step("Проверить валидность запроса на заказ")
    public void validateOrderRequest(OrderRequest orderRequest) {
        if (!orderRequest.hasAccountId()){
            throw new IllegalArgumentException("AccountId обязателен");
        }
        if (!orderRequest.hasValidAmount()) {
            throw new IllegalArgumentException("Amount олжен быть положительным числом");
        }
        if (!orderRequest.hasEmail()) {
            throw new IllegalArgumentException("Email обязателен");
        }
    }

    @Step("Проверить успешное создание заказа")
    public void verifyOrderCreatedSuccessfully(Response response) {
        response.then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }
}
