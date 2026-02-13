package ru.tutor.api.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.page.api.OrderApi;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderSteps {
    private final OrderApi orderApi;

    public OrderSteps(String baseUrl) {
        this.orderApi = new OrderApi(baseUrl);
    }

    @Step("Создать заказ для email: {email}")
    public String createOrder(String email, List<String> products) {
        Response response = orderApi.createOrder(email, products);
        assertEquals(200, response.statusCode(), "Ошибка при создании заказа");
        return response.jsonPath().getString("orderId");
    }

    @Step("Создать intent для заказа {orderId}")
    public void createIntent(String orderId) {
        Response response = orderApi.createIntent(orderId);
        assertEquals(200, response.statusCode(), "Ошибка при создании intent");
    }

    @Step("Проверить BIN: {bin}")
    public void checkBin(String bin) {
        Response response = orderApi.checkBin(bin);
        assertEquals(200, response.statusCode(), "Ошибка при проверке BIN");
    }

    @Step("Оплатить заказ {orderId}")
    public void payOrder(String orderId, String cardNumber, String exp, String cvc) {
        Response response = orderApi.payOrder(orderId, cardNumber, exp, cvc);
        assertEquals(200, response.statusCode(), "Ошибка при оплате заказа");
    }
}
