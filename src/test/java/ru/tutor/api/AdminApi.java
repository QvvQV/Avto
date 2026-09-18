package ru.tutor.api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import ru.tutor.base.BaseApi;
import ru.tutor.constants.ApiEndpoints;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class AdminApi extends BaseApi {

    public AdminApi(String baseUrl) {
        super(baseUrl);
    }

    @Step("POST /admin/login - авторизация админа")
    public Response login(String username, String password) {
        return post(ApiEndpoints.ADMIN_LOGIN, Map.of("username", username, "password", password));
    }

    @Step("Извлечение cookie AUTH_ADMIN из ответа login")
    public String extractAdminToken(Response loginResponse) {
        return loginResponse.getCookie("AUTH_ADMIN");
    }

    @Step("GET /admin/orders?filter[email] - поиск заказов по email")
    public Response searchOrdersByEmail(String adminToken, String email, String date) {
        String query = "filter%5Bemail%5D=" + URLEncoder.encode(email, StandardCharsets.UTF_8)
                + "&filter%5BdateFrom%5D=" + date
                + "&filter%5BdateTo%5D=" + date
                + "&filter%5BclickHouse%5D=true&limit=50&page=1";
        return withAdminCookie(adminToken)
                .when().get(ApiEndpoints.ADMIN_ORDERS + "?" + query)
                .then().log().ifError().extract().response();
    }

    @Step("GET /admin/orders/{id}")
    public Response getOrder(String adminToken, String orderId) {
        return withAdminCookie(adminToken)
                .when().get(ApiEndpoints.ADMIN_ORDERS_ID.formatted(orderId))
                .then().log().ifError().extract().response();
    }

    @Step("PUT /admin/orders/extend-access - установка дней доступа")
    public Response putExtendAccess(String adminToken, String subscriptionOrderId, int amountDays) {
        return withAdminCookie(adminToken)
                .body(Map.of("subscriptionOrderId", Long.parseLong(subscriptionOrderId),
                        "amountDays", amountDays))
                .when().put(ApiEndpoints.ADMIN_ORDERS_EXTEND_ACCESS)
                .then().log().ifError().extract().response();
    }

    private RequestSpecification withAdminCookie(String adminToken) {
        return given(baseSpec).header("Cookie", "AUTH_ADMIN=" + adminToken);
    }

    @Step("POST /admin/payments/create - оплата заказа в админке")
    public Response createPayment(String adminToken, long orderId, int amount, boolean moveDates) {
        Map<String, Object> body = new HashMap<>();
        body.put("orderId", orderId);
        body.put("amount", amount);
        body.put("moveDates", moveDates);
        body.put("createAt", null);
        return withAdminCookie(adminToken)
                .body(body)
                .when().post(ApiEndpoints.ADMIN_PAYMENTS_CREATE)
                .then().log().ifError().extract().response();
    }

    @Step("POST /admin/orders/{id}/unsubscribe - отмена подписки заказа в админке")
    public Response unsubscribeOrder(String adminToken, String orderId) {
        return withAdminCookie(adminToken)
                .when().post(ApiEndpoints.ADMIN_ORDERS_UNSUBSCRIBE.formatted(orderId))
                .then().log().ifError().extract().response();
    }

    @Step("POST /admin/payments/refund - возврат платежа {paymentId} в админке")
    public Response refundPayment(String adminToken, int paymentId) {
        return withAdminCookie(adminToken)
                .body(Map.of("id", paymentId))
                .when().post(ApiEndpoints.ADMIN_PAYMENTS_REFUND)
                .then().log().ifError().extract().response();
    }
}