package ru.tutor.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.api.AdminApi;
import ru.tutor.base.BaseApiSteps;
import ru.tutor.utils.AllureAttachments;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
}
