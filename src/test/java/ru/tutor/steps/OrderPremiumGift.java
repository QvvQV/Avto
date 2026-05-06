package ru.tutor.steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.tutor.api.UserOrderApi;
import ru.tutor.model.OrderToggleSubscriptionResponse;
import ru.tutor.utils.AllureAttachments;

import java.util.List;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.*;

/*
 * Класс для переключения на Тариф Подарок по API
 */

public record OrderPremiumGift(UserOrderApi orderApi) {

    public static OrderPremiumGift withBaseUrl(String baseUrl) {
        return new OrderPremiumGift(new UserOrderApi(baseUrl));
    }

    @Step("Активация Премиум подписки подарок")
    public Response getPremiumGift(String authToken) {
        Response response = this.orderApi.getPremiumGift(authToken);
        AllureAttachments.attachJson("Premium Gift Response", response.getBody().asString());
        AllureAttachments.attachJson("Premium Gift Headers", response.getHeaders().toString());
        return response;
//        return this.orderApi.getPremiumGift(authToken);
    }

    @Step("Валидация успешного ответа при переключении подписки")
    public List<OrderToggleSubscriptionResponse> validateGetPremiumGiftResponse(Response response) {
        response.then()
                .statusCode(200)
                .contentType(ContentType.JSON);

        response.then()
                .body("$", instanceOf(List.class));

        // Преобразуем в список
        List<OrderToggleSubscriptionResponse> items = response.jsonPath().getList("$", OrderToggleSubscriptionResponse.class);

        assertNotNull(items, "Ответ не должен быть null");

        return items;
    }

    @Step("Детальная валидация полей подписки")
    public void validateOrderPremiumGiftFields(OrderToggleSubscriptionResponse subscription) {
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

    @Step("Проверка ошибки при получении премиум подарка")
    public void validatePremiumGiftError(Response response, int expectedStatusCode, String expectedMessagePart) {
        int statusCode = response.getStatusCode();
        String responseBody = response.getBody().asString();

        AllureAttachments.attachText("Ошибка получения подарка",
                String.format("""
                                ❌ Получение премиум подарка не удалось
                                📊 Статус код: %d (ожидался: %d)
                                📝 Тело ответа: %s
                                """,
                        statusCode,
                        expectedStatusCode,
                        responseBody));

        assertTrue(statusCode == expectedStatusCode || statusCode == 400 || statusCode == 422,
                String.format("Should return error status code. Got: %d", statusCode));

        if (expectedMessagePart != null && !expectedMessagePart.isEmpty()) {
            assertTrue(responseBody.toLowerCase().contains(expectedMessagePart.toLowerCase()),
                    String.format("Response should contain: %s", expectedMessagePart));
        }
    }
}