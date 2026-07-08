package ru.tutor.api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.base.BaseApi;
import ru.tutor.constants.ApiEndpoints;
import ru.tutor.utils.AllureAttachments;

import static io.restassured.RestAssured.given;

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
}