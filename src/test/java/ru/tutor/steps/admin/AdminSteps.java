package ru.tutor.steps.admin;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.api.admin.AdminApi;
import ru.tutor.base.BaseApiSteps;
import ru.tutor.model.admin.PaymentType;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.DateValidator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.codeborne.selenide.Selenide.sleep;
import static org.junit.jupiter.api.Assertions.*;

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

    @Step("Проверка через админ-API: заказ с тарифом «{expectedKeyword}» для email: {email}")
    public void assertOrderTariffForEmail(String email, String expectedKeyword) {
        login();
        String orderId = findOrderId(email);
        Response order = adminApi.getOrder(adminToken, orderId);
        AllureAttachments.attachJson("Заказ (админка)", order.getBody().asString());
        String productName = order.jsonPath().getString("product.name");
        assertNotNull(productName, "В ответе админ-API нет поля product.name");
        assertTrue(productName.toLowerCase().contains(expectedKeyword.toLowerCase()),
                "Ожидался тариф «" + expectedKeyword + "», получен продукт: " + productName);
        AllureAttachments.attachText("Тариф заказа", productName);
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
        int status = order.jsonPath().getInt("status");

        assertNotNull(deactivatedAt, "После отмены заказа deactivatedAt должен быть заполнен");
        assertEquals("admin", deactivatedBy, "deactivatedBy должен быть 'admin'");
        assertEquals("Деактивация сотрудником ", deactivatedType,
                "deactivatedType должен быть 'Деактивация сотрудником'");
        assertEquals(7, status, "status после отмены должен быть 7 ('Оплаченный')");
        AllureAttachments.attachText("Флаги деактивации",
                "deactivatedAt=" + deactivatedAt + ", deactivatedBy=" + deactivatedBy
                        + ", deactivatedType=" + deactivatedType + ", status=" + status);
    }

    @Step("Поиск refundable платежа заказа {orderId} (GET /admin/order/{id} -> payments)")
    public Integer findRefundablePaymentId(String orderId) {
        Response order = adminApi.getOrder(adminToken, orderId);
        AllureAttachments.attachJson("Заказ перед refund", order.getBody().asString());

        List<Map<String, Object>> payments = order.jsonPath().getList("payments");
        assertNotNull(payments, "Список payments не должен быть null");

        Map<String, Object> target = null;
        int targetAmount = Integer.MIN_VALUE;
        for (Map<String, Object> p : payments) {
            if (!Boolean.TRUE.equals(p.get("isRefundable"))) continue;
            if (!Boolean.FALSE.equals(p.get("hasRefund"))) continue;
            Number amount = (Number) p.get("amount");
            Number status = (Number) p.get("status");
            if (amount == null || amount.intValue() <= 0) continue;
            if (status == null || status.intValue() != 1) continue;
            if (amount.intValue() > targetAmount) {
                targetAmount = amount.intValue();
                target = p;
            }
        }
        assertNotNull(target, "Refundable платеж не найден в payments заказа " + orderId);
        Integer paymentId = ((Number) target.get("id")).intValue();
        AllureAttachments.attachText("Refundable платеж", "id=" + paymentId + ", amount=" + target.get("amount"));
        return paymentId;
    }

    @Step("Возврат платежа {paymentId} через админку (POST /admin/payments/refund)")
    public Response refundPayment(int paymentId) {
        Response resp = adminApi.refundPayment(adminToken, paymentId);
        assertSuccessStatusCode(resp);
        AllureAttachments.attachJson("Admin refund", resp.getBody().asString());
        Object body = resp.jsonPath().get("$");
        assertTrue(body instanceof List,
                "Ответ должен быть список ([]), но тело: " + resp.getBody().asString());
        return resp;
    }

    @Step("Проверка записи о возврате в платежах заказа {orderId} (amount=-{refundAmount}, type=5, status=1)")
    public void assertRefundApplied(String orderId, int refundAmount) {
        Response order = adminApi.getOrder(adminToken, orderId);
        AllureAttachments.attachJson("Заказ после refund", order.getBody().asString());

        List<Map<String, Object>> payments = order.jsonPath().getList("payments");
        assertNotNull(payments, "Список payments не должен быть null");

        boolean found = payments.stream().anyMatch(
                p -> p.get("amount") != null && ((Number) p.get("amount")).intValue() == -refundAmount
                && p.get("status") != null && ((Number) p.get("status")).intValue() == 1
                && p.get("type") != null && ((Number) p.get("type")).intValue() == 5);

        assertTrue(found, "Не найдена запись refund (amount=-" + refundAmount + ", type=5, status=1)" +
                " в payments заказа " + orderId);
        AllureAttachments.attachText("Refund проверка",
                "Найдена запись: amount=-" + refundAmount + ", type=5, status=1");
    }

    @Step("Проверка через админ-API: заказы переведены в status=2")
    public void assertOrdersDeactivated(String... orderIds) {
        login();
        for (String orderId : orderIds) {
            Response order = adminApi.getOrder(adminToken, orderId);
            int status = order.jsonPath().getInt("status");
            assertEquals(2, status, "Заказ " + orderId + " должен стать status=2");
            AllureAttachments.attachText("Заказ (status=2)", "id=" + orderId + ", status=" + status);
        }
    }

    @Step("GET /admin/user/club/withdrawal/{id} - заявка в админке")
    public Response getClubWithdrawal(long withdrawalId) {
        Response resp = adminApi.getClubWithdrawal(adminToken, withdrawalId);
        AllureAttachments.attachJson("Admin Club Withdrawal", resp.getBody().asString());
        return resp;
    }

    @Step("POST /admin/user/club/withdrawal/{id}/reject - отклонение заявки (204)")
    public Response rejectClubWithdrawal(long withdrawalId, boolean returnBonus) {
        Response resp = adminApi.rejectClubWithdrawal(adminToken, withdrawalId, returnBonus);
assertEquals(204, resp.getStatusCode(),
                "Ожидался статус 204, получен: " + resp.getStatusCode());
        AllureAttachments.attachJson("admin Club Withdrawal Reject", resp.getBody().asString());
        return resp;
    }

    @Step("Проверка статуса заявки на вывод: {expectedCode} / {expectedLabel}")
    public void validateWithdrawalStatus(Response response, String expectedCode, String expectedLabel) {
        assertSuccessStatusCode(response);
        String code = response.jsonPath().getString("status.code");
        String label = response.jsonPath().getString("status.label");
        assertEquals(expectedCode, code,
                "status.code должен быть '" + expectedCode + "'");
        assertEquals(expectedLabel, label,
                "status.label должен быть '" + expectedLabel + "'");
    }

    @Step("DELETE /admin/user/club/withdrawal/{id} - удаление заявки на вывод (204)")
    public Response deleteClubWithdrawal(long withdrawalId, boolean returnBonus) {
        Response resp = adminApi.deleteClubWithdrawal(adminToken, withdrawalId, returnBonus);
        assertEquals(204, resp.getStatusCode(), "Ожидался статус 204, получен: " + resp.getStatusCode());
        AllureAttachments.attachJson("Admin Club Withdrawal Delete", resp.getBody().asString());
        return resp;
    }

    @Step("Согласование платежа через админку (POST /admin/user/club/withdrawal)")
    public Response withdrawalPayment(int paymentId, boolean isPaid) {
        Response resp = adminApi.withdrawalPayment(adminToken, paymentId, isPaid);
        assertEquals(201, resp.getStatusCode(), "Ожидался статус 201, получен: " + resp.getStatusCode());
        AllureAttachments.attachJson("Admin withdrawal", resp.getBody().asString());
        Object body = resp.jsonPath().get("$");
        assertTrue(body instanceof List,
                "Ответ должен быть список ([]), но тело: " + resp.getBody().asString());
        return resp;
    }

    @Step("Проверка типа платежа на сумму {expectedAmount} в заказе {orderId}: все status=1 плватежи должны быть {expectedType}")
    public void validatePaymentType(String orderId, int expectedAmount, PaymentType expectedType) {
        Response order = adminApi.getOrder(adminToken, orderId);
        AllureAttachments.attachJson("Заказ (админка)", order.getBody().asString());

        List<Map<String, Object>> payments = order.jsonPath().getList("payments");
        assertNotNull(payments, "Список payments не должен быть null");

        List<Integer> types = payments.stream()
                .filter(p -> p.get("amount") != null && ((Number) p.get("amount")).intValue() == expectedAmount)
                .filter(p -> p.get("status") != null && ((Number) p.get("status")).intValue() == 1)
                .map(p -> ((Number) p.get("type")).intValue())
                .toList();

        assertFalse(types.isEmpty(),
                "Платёж на сумму " + expectedAmount + " не найден в payments заказа " + orderId);

        Set<Integer> actual = new TreeSet<>(types);
        assertTrue(actual.size() == 1 && actual.contains(expectedType.code()),
                "Все платежи на сумму " + expectedAmount + " должны иметь type=" + expectedType.code()
                + " (" + expectedType + "), фактические типы: " + actual);

        AllureAttachments.attachText("Тип платежа",
                expectedAmount + " ₽ -> type=" + expectedType.code() + " (" + expectedType + ")");
    }

    @Step("Поиск родительского id заказа по email: {email}")
    public String findFirstOrderId(String email) {
        String date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        Response resp = adminApi.searchOrdersByEmail(adminToken, email, date);
        AllureAttachments.attachJson("Поиск заказов админа (родитель)", resp.getBody().asString());

        List<Map<String, Object>> items = resp.jsonPath().getList("items");
        assertNotNull(items, "Список items не должен быть null");
        assertFalse(items.isEmpty(), "Заказ для email " + email + " не найден в админке");

        for (Map<String, Object> item : items) {
            String candidateId = String.valueOf(item.get("id"));
            String productName = adminApi.getOrder(adminToken, candidateId).jsonPath().getString("product.name");
            if (productName != null && productName.toLowerCase().contains("премиум")) {
                AllureAttachments.attachText("Премиум-заказ (родитель)", "ID: " +  candidateId);
                return candidateId;
            }
        }
        fail("Премиум-заказ не найден для email " + email + ", заказы: " + items);
        return null;
    }
}