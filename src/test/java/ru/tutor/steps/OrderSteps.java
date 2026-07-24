package ru.tutor.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.api.UserOrderApi;
import ru.tutor.base.BaseApiSteps;
import ru.tutor.model.RefundTicketRequest;
import ru.tutor.utils.AllureAttachments;

import java.util.HashMap;

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
        Response response = orderApi.pauseOrder(authToken, days);
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

    @Step("POST /user/refund-tickets/create - создание заявки на возврат")
    public Response createRefundTicket(String authToken, RefundTicketRequest request) {
        Response response = orderApi.createRefundTicket(authToken, request);
        AllureAttachments.attachJson("Create Refund Ticket Response", response.getBody().asString());
        return response;
    }

    @Step("Валидация ответа создания заявки на возврат")
    public void validateRefundTicketResponse(Response response) {
        assertSuccessStatusCode(response);
        assertContentTypeJson(response);
        assertCorsHeaders(response);
        String body = response.getBody().asString();
        assertNotNull(body, "Тело ответа не должно быть null");
        assertFalse(body.isEmpty(), "Тело ответа не должно быть пустым");
    }

    @Step("Валидация ошибок валидации полей")
    public void validateRefundValidationError(Response response) {
        assertEquals(422, response.getStatusCode());
        assertContentTypeJson(response);

        assertEquals("Значение не должно быть пустым.",
                response.jsonPath().getString("error.messages.firstname"));
        assertEquals("Значение не должно быть пустым.",
                response.jsonPath().getString("error.messages.lastname"));
        assertEquals("Значение не должно быть пустым.",
                response.jsonPath().getString("error.messages.patronymic"));
        assertEquals("Значение не должно быть пустым.",
                response.jsonPath().getString("error.messages.email"));
        assertEquals("Пустое поле card",
                response.jsonPath().getString("error.messages.card"));
        assertEquals("Значение не должно быть пустым.",
                response.jsonPath().getString("error.messages.bank"));
        assertEquals("Значение не должно быть пустым.",
                response.jsonPath().getString("error.messages.paymentDate"));
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
        assertSuccessStatusCode(response);
        assertContentTypeJson(response);
        assertEquals("OK", response.jsonPath().getString("status"));
    }

    @Step("Валидация ошибки при недостатке средств")
    public void validateSpendCashbackValidationError(Response response) {
        assertEquals(400, response.getStatusCode());
        String error = response.jsonPath().getString("error");
        assertTrue(error !=null && error.contains("Not enough diamond balance"),
                "Ожидалась ошибка о недостатке средств, получено: " + error);
    }
}
