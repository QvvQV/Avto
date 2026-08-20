package ru.tutor.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.api.UserOrderApi;
import ru.tutor.base.BaseApiSteps;
import ru.tutor.model.RefundTicketRequest;
import ru.tutor.utils.AllureAttachments;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class OrderSteps extends BaseApiSteps {
    private final UserOrderApi orderApi;

    public OrderSteps (String baseUrl) {
        super(baseUrl);
        this.orderApi = new UserOrderApi(baseUrl);
    }

    @Step("GET /user/orders/payments - история платежей")
    public Response getOrdersPayments(String authToken) {
        Response response = orderApi.getOrdersPayments(authToken);

        AllureAttachments.attachJson("Orders Payments Response", response.getBody().asString());
        return response;
    }

    @Step("Валидация ответа истории платежей")
    public void validateOrdersPaymentsResponse(Response response) {
        assertBasicResponse(response);

        List<Map<String, Object>> payments = response.jsonPath().getList("payments");
        assertNotNull(payments, "payments не должен быть null");

        if (payments.isEmpty()) return;

        for (int i = 0; i < payments.size(); i++) {
            Map<String, Object> p = payments.get(i);
            assertAll("Платёж [" + i + "]",
                    notNull(p,"createdAt"),
                    notNull(p,"amount"),
                    () -> assertTrue(Double.parseDouble((String) p.get("amount")) > 0,
                            "amount > 0"),
                    nonNegative(p,"status"),
                    nonNegative(p,"type"),
                    nonNegative(p,"method"),
                    notNull(p,"typeName"),
                    notNull(p,"source"),
                    notNull(p,"sourceName")
            );
        }
    }

    @Step("Валидация ответа истории платежей")
    public void validateBonusAccessInfoResponse(Response response) {
        assertBasicResponse(response);

        Boolean hasBonusAccess = response.jsonPath().getObject("hasBonusAccess", Boolean.class);
        assertNotNull(hasBonusAccess, "hasBonusAccess не должен быть null");
    }

    @Step("Валидация ответа Информации о состоянии подписки")
    public void validateOrderSubscriptionStateResponse(Response response) {
        assertBasicResponse(response);

        Map<String, Object> root = response.jsonPath().getMap("$");
        assertNotNull(root, "Тело должно быть JSON-объектом");
        assertAll("Состояние подписки", nonNegative(root, "state"));
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
        Response response = orderApi.pauseOrder(authToken, days);
        AllureAttachments.attachJson("Orders Pause Response", response.getBody().asString());
        return response;
    }

    @Step("PUT /user/orders/unpause - снятие паузы")
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
            assertJsonFieldEquals(response, "error", "ERR_CANT_FIND_SUBSCRIPTION_ORDER");
        }  else {
            fail("Неожиданный статус: " + code);
        }
    }

    @Step("POST /user/refund-tickets/create - создание заявки на возврат")
    public Response createRefundTicket(String authToken, RefundTicketRequest request) {
        Response response = orderApi.createRefundTicket(authToken, request);
        AllureAttachments.attachJson("Create Refund Ticket Response", response.getBody().asString());
        return response;
    }

    @Step("Валидация ответа создания заявки на возврат")
    public void validateRefundTicketResponse(Response response) {
        assertBasicResponse(response);
        assertJsonFieldEquals(response, "success", "OK");
    }

    @Step("Валидация ошибок валидации полей")
    public void validateRefundValidationError(Response response) {
        assertEquals(422, response.getStatusCode());
        assertContentTypeJson(response);

        assertJsonFieldEquals(response, "error.messages.firstname", "Значение не должно быть пустым.");
        assertJsonFieldEquals(response, "error.messages.lastname", "Значение не должно быть пустым.");
        assertJsonFieldEquals(response, "error.messages.patronymic", "Значение не должно быть пустым.");
        assertJsonFieldEquals(response, "error.messages.email", "Значение не должно быть пустым.");
        assertJsonFieldEquals(response, "error.messages.card", "Пустое поле card");
        assertJsonFieldEquals(response, "error.messages.bank", "Значение не должно быть пустым.");
        assertJsonFieldEquals(response, "error.messages.paymentDate", "Значение не должно быть пустым.");
    }

    @Step("GET /user/refund-tickets - список заявок на возврат")
    public Response getRefundTickets(String authToken) {
        Response response = orderApi.getRefundTickets(authToken);
        AllureAttachments.attachJson("Refund Tickets Response", response.getBody().asString());
        return response;
    }

    @Step("POST /user/orders/spend-cashback - списание кэшбека")
    public Response spendCashback(String authToken) {
        Response response = orderApi.spendCashback(authToken);
        AllureAttachments.attachJson("Spend Cashback Response", response.getBody().asString());
        return response;
    }

    @Step("Валидация успешного списания кэшбека")
    public void validateSpendCashbackResponse(Response response) {
        assertBasicResponse(response);
        assertEquals("OK", response.jsonPath().getString("status"));
    }

    @Step("Валидация ошибки при недостатке средств")
    public void validateSpendCashbackValidationError(Response response) {
        assertEquals(400, response.getStatusCode());
        String error = response.jsonPath().getString("error");
        assertTrue(error !=null && error.contains("Not enough diamond balance"),
                "Ожидалась ошибка о недостатке средств, получено: " + error);
    }

    @Step("POST /user/orders/store-feedback-unsubscribe/{orderNumber} - сохранение отзыва")
    public Response storeFeedbackUnsubscribe(String authToken, String orderNumber, String message, int rating) {
        Map<String, String> fields = new HashMap<>();
        fields.put("message", message);
        fields.put("rating", String.valueOf(rating));

        Response response = orderApi.storeFeedBackUnsubscribe(authToken, orderNumber, fields);
        AllureAttachments.attachJson("Feedback Unsubscribe Response", response.getBody().asString());
        return response;
    }

    @Step("Валидация успешного ответа сохранения отзыва")
    public void validateStoreFeedbackSuccess(Response response) {
        assertEquals(200, response.getStatusCode(),
                "Ожидался статус 200, но получен: " + response.getStatusCode());
        assertContentTypeJson(response);
        assertJsonFieldEquals(response, "message", "Feedback stored successfully");
        assertJsonFieldExists(response, "order_id");
    }

    @Step("Валидация ошибки 'Order not found'")
    public void validateOrderNotFound(Response response) {
        assertEquals(400, response.getStatusCode(),
                "Ожидался статус 400, но получен: " + response.getStatusCode());
        assertJsonFieldEquals(response, "error.message", "Order not found");
    }
}
