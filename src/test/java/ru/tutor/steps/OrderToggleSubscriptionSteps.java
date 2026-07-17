package ru.tutor.steps;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import ru.tutor.api.UserOrderApi;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.tutor.base.BaseApiSteps;
import ru.tutor.model.OrderToggleSubscriptionResponse;
import ru.tutor.utils.AllureAttachments;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Класс для переключения на Лайт по API
 */

public class OrderToggleSubscriptionSteps extends BaseApiSteps {
    private final UserOrderApi orderApi;

    public OrderToggleSubscriptionSteps(String baseUrl) {
        super(baseUrl);
        this.orderApi = new UserOrderApi(baseUrl);
    }

    @Step("Переключение подписки на заказ ID: {orderId}")
    public Response toggleOrderSubscription(String authToken, Integer orderId) {
        return this.orderApi.toggleOrderSubscription(authToken, orderId);
    }

    @Step("Валидация успешного ответа при переключении подписки")
    public List<OrderToggleSubscriptionResponse> validateToggleSubscriptionResponse(Response response) {
        response.then()
                .statusCode(200)
                .contentType(ContentType.JSON);

        // Проверяем что ответ - это список
        Object body = response.jsonPath().get("$");
        assertNotNull(body, "Тело ответа не должно быть null");
        assertTrue(body instanceof List, "Ответ должен быть списком (List)");

        // Преобразуем в список
        List<OrderToggleSubscriptionResponse> items = response.jsonPath().getList("$", OrderToggleSubscriptionResponse.class);

        assertNotNull(items, "Ответ не должен быть null");

        return items;
    }

    @Step("Детальная валидация полей подписки")
    public void validateSubscriptionFields(OrderToggleSubscriptionResponse subscription) {
        assertAll(
                () -> assertNotNull(subscription.getId(), "orderId должен быть не null"),
                () -> assertTrue(subscription.getId() > 0, "orderId должен быть положительным числом"),
                () ->
                {
                    Boolean isSubscribed = subscription.getIsSubscribed();
                    assertNotNull(isSubscribed, "isSubscribed должен быть не null");
                },
                () -> assertTrue(subscription.isValid(), "Ответ должен быть валидным")
        );
    }

    @Step("Анализ формата ответа")
    public void analyzeResponseFormat(Response response) {
        String responseBody = response.getBody().asString();
        String contentType = response.getContentType();

        StringBuilder analysis = new StringBuilder();
        analysis.append("🔍 АНАЛИЗ ФОРМАТА ОТВЕТА\n");
        analysis.append("=".repeat(50)).append("\n\n");

        analysis.append(String.format("Content-Type: %s\n", contentType));
        analysis.append(String.format("Длина ответа: %d символов\n\n", responseBody.length()));

        if (responseBody.equals("[]")) {
            analysis.append("⚠ Массив пустой\n");
        } else {
            analysis.append("✅ Массив содержит элементы\n");
        }

        Allure.addAttachment("Анализ формата", "text/plain", analysis.toString());
    }

    @Step("POST /user/orders/restore-premium - восстановление премиум")
    public Response restorePremium(String authToken) {
        Response response = orderApi.restorePremium(authToken);
        AllureAttachments.attachJson("Restore Premium Response", response.getBody().asString());
        return response;
    }

    @Step("Валидация полей")
    public void validateRestorePremiumResponse(Response response) {
        assertSuccessStatusCode(response);
        assertContentTypeJson(response);
        assertCorsHeaders(response);
    }
}