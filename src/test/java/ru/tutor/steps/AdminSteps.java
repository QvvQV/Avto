package ru.tutor.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.api.AdminApi;
import ru.tutor.base.BaseApiSteps;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.DateValidator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.codeborne.selenide.Selenide.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdminSteps extends BaseApiSteps {
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";

    private final AdminApi adminApi;
    private String adminToken;

    public AdminSteps(String baseUrl) {
        super(baseUrl);
        this.adminApi = new AdminApi(baseUrl);
    }

    @Step("Авторизация админа (AUTH_ADMIN cookie)")
    public AdminSteps login() {
        Response resp = adminApi.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        adminToken = adminApi.extractAdminToken(resp);
        assertNotNull(adminToken, "Cookie AUTH_ADMIN не получена в ответе /admin/login");
        return this;
    }

    @Step("Поиcк id заказа по email: {email}")
    public String findOrderId(String email) {
        String date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        Response resp = adminApi.searchOrdersByEmail(adminToken, email, date);
        AllureAttachments.attachJson("Поиск заказов админа", resp.getBody().asString());
        String id = resp.jsonPath().getString("items[0].id");
        assertNotNull(id, "Заказ для email " + email + " не найден в админке");
        return id;
    }

    @Step("Деактивация доступа: PUT extend-access {days} дня + проверка статуса")
    public void extendAccessDays(String orderId, int days) {
        Response put = adminApi.putExtendAccess(adminToken, orderId, days);
        assertSuccessStatusCode(put);
        Response order = adminApi.getOrder(adminToken, orderId);
        String status = order.jsonPath().getString("status");
        assertFalse("Активный".equals(status),
                "После extend-access(" + days + ") заказ " + orderId + " должен стать неактивным, но status=" + status);
        AllureAttachments.attachText("Статус заказа после деактивации", status);
    }

    @Step("Проверка существования заказа в админке по email: {email}")
    public String assertOrderExists(String email) {
        login();
        String orderId = findOrderId(email);
        AllureAttachments.attachText("Заказ найден", "ID заказа: " +  orderId);
        return orderId;
    }

@Step("Оплата заказа {orderId} в админке ({amount}₽) и проверка сдвига дат на {days} дней")
    public void createPaymentAndValidateDates(String orderId, int amount, int days) {
        Response payment = adminApi.createPayment(adminToken, Long.parseLong(orderId), amount, true);
        assertSuccessStatusCode(payment);
        AllureAttachments.attachJson("Admin createPayment", payment.getBody().asString());

        sleep(7000);

        Response order = adminApi.getOrder(adminToken, orderId);
        String createdAt  = order.jsonPath().getString("createdAt");
        String expiresAt  = order.jsonPath().getString("expiresAt");
        String nextPaymentAt = order.jsonPath().getString("nextPaymentAt");
        AllureAttachments.attachJson("Admin order после оплаты", order.getBody().asString());

        DateValidator validator = new DateValidator(3);
        validator.verifyDateDifference(LocalDateTime.parse(createdAt), LocalDateTime.parse(expiresAt),
                days, "expiresAt от createdAt");
        validator.verifyTimeOfDayDifference(LocalDateTime.parse(expiresAt), LocalDateTime.parse(nextPaymentAt),
                "nextPaymentAt/expiresAt");
    }

    @Step("Поиск дочернего заказа в parent-заказе: {parentOrderId}")
    public String findChildOrderId(String parentOrderId) {
        Response order = adminApi.getOrder(adminToken, parentOrderId);
        AllureAttachments.attachJson("Parent заказ", order.getBody().asString());
        String childId = order.jsonPath().getString("childrenOrdersIds[0]");
        assertNotNull(childId, "Дочерний заказ не найден в parent " + parentOrderId);
        return childId;
    }

    @Step("Отмена подписки заказа {orderId} через админку (POST /admin/orders/{id}/unsubscribe)")
    public Response unsubscribeOrder(String orderId) {
        Response resp = adminApi.unsubscribeOrder(adminToken, orderId);
        assertSuccessStatusCode(resp);
        AllureAttachments.attachJson("Admin unsubscribe", resp.getBody().asString());
        Object body = resp.jsonPath().get("$");
        assertTrue(body instanceof List,
                "Ответ должен быть списком ([]), но тело: " + resp.getBody().asString());
        return resp;
    }

    @Step("Проверка флагов деактивации заказа {orderId} (GET /admin/orders/{id})")
    public void assertOrderDeactivated(String orderId) {
        Response order = adminApi.getOrder(adminToken, orderId);
        AllureAttachments.attachJson("Admin order после отмены", order.getBody().asString());

        String deactivatedAt = order.jsonPath().getString("deactivatedAt");
        String deactivatedBy = order.jsonPath().getString("deactivatedBy");
        String deactivatedType = order.jsonPath().getString("deactivatedType");

        assertNotNull(deactivatedAt, "После отмены заказа deactivatedAt должен быть заполнен");
        assertEquals("admin", deactivatedBy, "deactivatedBy должен быть 'admin'");
        assertEquals("Деактивация сотрудником ", deactivatedType,
                "deactivatedType должен быть 'Деактивация сотрудником'");
        AllureAttachments.attachText("Флаги деактивации",
                "deactivatedAt=" + deactivatedAt + ", deactivatedBy=" + deactivatedBy
                        + ", deactivatedType=" + deactivatedType);
    }
}
