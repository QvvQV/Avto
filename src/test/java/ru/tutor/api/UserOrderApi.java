package ru.tutor.api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.base.BaseApi;
import ru.tutor.constants.ApiEndpoints;
import ru.tutor.model.RefundTicketRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Ручка для отписки до Лайта. Ручка для тарифа Подарок
 */

public class UserOrderApi extends BaseApi {

    public UserOrderApi(String baseUrl) {
        super(baseUrl);
    }

    // Лайт подписка

    @Step("POST /user/orders/toggle_subscription/{orderId} - Отписка пользователя до Лайт")
    public Response toggleOrderSubscription(String authToken, Integer orderId) {
        return postWithToken(ApiEndpoints.ORDERS_TOGGLE_SUBSCRIPTION, authToken, "[]", orderId);
    }

    // Подарок

    @Step(" POST user/orders/get-premium-gift - Получение Тариф Подарок")
    public Response getPremiumGift(String authToken) {
        return postWithToken(ApiEndpoints.ORDERS_GET_PREMIUM_GIFT, authToken, "[]");
    }

    @Step("`GET /user/orders/payments` - История платежей")
    public Response getOrdersPayments(String authToken) {
        return getWithToken(ApiEndpoints.ORDERS_PAYMENTS, authToken);
    }

    @Step("GET /user/orders/bonus-access-info")
    public Response getBonusAccessInfo(String authToken) {
        return getWithToken(ApiEndpoints.ORDERS_BONUS_ACCESS_INFO, authToken);
    }

    @Step("GET /user/orders/subscription/state")
    public Response getSubscriptionState(String authToken) {
        return getWithToken(ApiEndpoints.ORDERS_SUBSCRIPTION_STATE, authToken);
    }

    @Step("PUT /user/orders/pause/{days} - поставить паузу")
    public Response pauseOrder(String authToken, int days) {
        return putWithToken(String.format(ApiEndpoints.ORDERS_PAUSE, days), authToken);
    }

    @Step("PUT /user/orders/unpause - снять паузу")
    public Response unpauseOrder(String authToken) {
        return putWithTokenObject(ApiEndpoints.ORDERS_UNPAUSE, authToken, "[]");
    }

    @Step("POST /user/refund-tickets/create - создание заявки на возврат")
    public Response createRefundTicket(String authToken, RefundTicketRequest request) {
        return postWithToken(ApiEndpoints.REFUND_TICKETS_CREATE, authToken, request);
    }

    @Step("GET /user/refund-tickets")
    public Response getRefundTickets(String authToken) {
        return getWithToken(ApiEndpoints.REFUND_TICKETS, authToken);
    }

    @Step("POST /user/orders/restore-premium - воссатновление премиум подписки")
    public Response restorePremium(String authToken) {
        return postWithToken(ApiEndpoints.ORDERS_RESTORE_PREMIUM, authToken, "[]");
    }

    @Step("POST /user/orders/spend-cashback - списание кэшбека")
    public Response spendCashback(String authToken) {
        return postWithToken(ApiEndpoints.ORDERS_SPEND_CASHBACK, authToken, "[]");
    }

    // Отзыв

    @Step("POST /user/orders/store-feedback-unsubscribe/{orderNumber} - сохранение отзыва")
    public Response storeFeedBackUnsubscribe(String authToken, String orderNumber, Map<String, String> fields) {
        return postFormData(
                String.format(ApiEndpoints.ORDERS_STORE_FEEDBACK_UNSUBSCRIBE, orderNumber),
                authToken,
                fields
        );
    }

    @Step("GET /user/orders/upsale-content-diamond/{orderNumber} - апсейл за алмазы")
    public Response getUpsaleContentDiamond(String authToken, String orderNumber) {
        return getWithToken(String.format(ApiEndpoints.ORDERS_UPSALE_CONTENT_DIAMOND, orderNumber), authToken);
    }

    @Step("GET /user/orders/upsale-content-invoice/{productOrderNumber} - апсейл за деньги")
    public Response getUpsaleContentMoney(String authToken, String orderNumber) {
        return getWithToken(String.format(ApiEndpoints.ORDERS_UPSALE_CONTENT_INVOICE, orderNumber), authToken);
    }

    @Step("GET /user/orders/{productOrderNumber}/upsell-variant - вариант модального окна")
    public Response getUpsaleVariant(String authToken, String orderNumber) {
        return getWithToken(String.format(ApiEndpoints.ORDERS_UPSELL_VARIANT, orderNumber), authToken);
    }

    @Step("POST /user/orders/{productOrderNumber}/upsell-modal-opened - открытие модального окна апсейлов")
    public Response upsellModalOpened(String authToken, String orderNumber) {
        return postWithToken(String.format(ApiEndpoints.ORDERS_UPSELL_MODAL_OPENED, orderNumber), authToken, "[]");
    }
}