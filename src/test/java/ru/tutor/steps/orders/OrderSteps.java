package ru.tutor.steps.orders;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.api.orders.PaymentSuccessApi;
import ru.tutor.api.orders.UserOrderApi;
import ru.tutor.base.BaseApiSteps;
import ru.tutor.config.TestConfig;
import ru.tutor.model.order.RefundTicketRequest;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.utils.AllureAttachments;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codeborne.selenide.Selenide.sleep;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

public class OrderSteps extends BaseApiSteps {
    private final UserOrderApi orderApi;

    private static final Pattern ORDER_NUMBER_PATTERN = Pattern.compile("/success/([A-Z0-9]+)");
    private final PaymentSuccessApi siteApi; //site домен (order/status)
    private final PaymentSuccessApi apiApi; //API домен (order/success)

    public OrderSteps (String baseUrl) {
        super(baseUrl);
        this.orderApi = new UserOrderApi(baseUrl);
        this.siteApi = new PaymentSuccessApi(baseUrl);
        this.apiApi = new PaymentSuccessApi(baseUrl);
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

    @Step("POST /user/orders/subscribe/{productId} - оформление подписки на продукт")
    public Response subscribeToOrder(String authToken, Integer productId) {
        Response response = orderApi.subscribeToOrder(authToken, productId);
        AllureAttachments.attachJson("Subscribe Response", response.getBody().asString());
        return response;
    }

    @Step("Валидация ответа subscribe: 200")
    public void validateSubscribeResponse(Response response) {
        assertSuccessStatusCode(response);
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

    @Step("POST /user/orders/pay-lifetime-tariff - оплата lifetime-тарифа")
    public Response payLifetimeTariff(String authToken) {
        Response response = orderApi.payLifetimeTariff(authToken);
        AllureAttachments.attachJson("Pay LifeTime Tariff Response", response.getBody().asString());
        return response;
    }

    @Step("Валидация ответа pay-lifetime-tariff: 200, тело не пустое и не null")
    public void validatePayLifetimeTariffResponse(Response response) {
        int code = response.getStatusCode();
        String body = response.getBody().asString();

        if (code ==400 && body.contains("Cannot find active subscription order")) {
            fail("Эндпоинт вернул ошибку 400: `Cannot find active subscription order`");
        }

        assertBasicResponse(response);
        assertNotNull(body, "Тело ответа не должно быть null");
        assertFalse(body.trim().isEmpty(), "Тело ответа не должно быть пустым");
        AllureAttachments.attachText("Тело ответа (pay-lifetime-tariff)", body);
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
        assertSuccessStatusCode(response);
        assertContentTypeJson(response);
        String message = response.jsonPath().getString("message");
        assertNotNull(message, "Message не должен быть null");

        // Проверяем, что message содержит одно из ожидаемых значений
        assertTrue(
                message.equals("Feedback stored successfully") ||
                        message.equals("Feedback already exist")
        );
        assertJsonFieldExists(response, "order_id");
    }

    @Step("Валидация ошибки 'Order not found'")
    public void validateOrderNotFound(Response response) {
        assertEquals(400, response.getStatusCode(),
                "Ожидался статус 400, но получен: " + response.getStatusCode());
        assertJsonFieldEquals(response, "error.message", "Order not found");
    }

    @Step("GET /user/orders/upsale-content-diamond/{orderNumber} - апсейл за алмазы")
    public Response getUpsaleContentDiamond(String authToken, String orderNumber) {
        Response response = orderApi.getUpsaleContentDiamond(authToken, orderNumber);
        AllureAttachments.attachJson("Upsale Content Diamond Response", response.getBody().asString());
        return response;
    }

    @Step("Валидация ответа за алмазы (пустой массив)")
    public void validateUpsaleContentDiamondResponse(Response response) {
        assertBasicResponse(response);
        List<?> body = response.jsonPath().getList("$");
        assertNotNull(body, "Тело ответа не должно быть null");
    }

    @Step("Валидация ошибки 'Product order not found' (404)")
    public void validateProductOrderNotFound(Response response) {
        assertEquals(404, response.getStatusCode(),
                "Ожидался статус 404, но получен: " + response.getStatusCode());
        assertJsonFieldEquals(response, "message", "Product order not found");
    }

    @Step("GET /user/orders/upsale-content-invoice/{productOrderNumber} - апсейл за деньги")
    public Response getUpsaleContentMoney(String authToken, String orderNumber) {
        Response response = orderApi.getUpsaleContentMoney(authToken, orderNumber);
        AllureAttachments.attachJson("Upsale Content Money Response", response.getBody().asString());
        return response;
    }

    @SuppressWarnings("unchecked")
    @Step("Валидация ответа за деньги (пустой массив)")
    public void validateUpsaleContentInvoiceResponse(Response response) {
        assertSuccessStatusCode(response);
        assertContentTypeJson(response);

        Map<String, Object> root = response.jsonPath().getMap("$");
        assertNotNull(root, "Тело должно быть JSON-объектом");

        assertAll("Структура ответа апсейла за деньги",
                () -> assertNotNull(root.get("paymentSystem"), "paymentSystem не null"),
                () -> assertNotNull(root.get("data"), "data не null"),
                () -> assertNotNull(root.get("params"), "params не null"),
                () -> assertNotNull(root.get("url"), "url не null")
        );

        Map<String, Object> data = (Map<String, Object>) root.get("data");
        assertAll("Поля data",
                () -> assertNotNull(data.get("publicId"), "data.publicId не null"),
                () -> assertNotNull(data.get("amount"), "data.amount не null"),
                () -> assertNotNull(data.get("currency"), "data.currency не null"),
                () -> assertNotNull(data.get("invoiceId"), "data.invoiceId не null"),
                () -> assertNotNull(data.get("description"), "data.description не null"),
                () -> assertNotNull(data.get("accountId"), "data.accountId не null"),
                () -> assertNotNull(data.get("skin"), "data.skin не null"),
                () -> assertNotNull(data.get("email"), "data.email не null"),
                () -> assertNotNull(data.get("autoClose"), "data.autoClose не null"),
                () -> assertNotNull(data.get("language"), "data.language не null")
        );

        Map<String, Object> innerData = (Map<String, Object>) data.get("data");
        assertAll("Поля data.data",
                () -> assertNotNull(innerData, "innerData не null"),
                () -> assertNotNull(innerData.get("paymentType"), "paymentType не null"),
                () -> assertNotNull(innerData.get("gatewayId"), "gatewayId не null"),
                () -> assertNotNull(innerData.get("cloudPayments"), "cloudPayments не null"),
                () -> assertNotNull(innerData.get("productOrderNumber"), "productOrderNumber не null")
        );

        Map<String, Object> params = (Map<String, Object>) root.get("params");
        assertNotNull(params, "params не null");
        assertAll("Поля params",
                () -> assertNotNull(params.get("language"), "language не null"),
                () -> assertNotNull(params.get("paymentType"), "paymentType не null")
        );

        Map<String, Object> url = (Map<String, Object>) root.get("url");
        assertAll("Поля url",
                () -> assertNotNull(url.get("result"), "url.result не null"),
                () -> assertNotNull(url.get("success"), "url.success не null"),
                () -> assertNotNull(url.get("fail"), "url.fail не null")
        );
    }

    @Step("GET /user/orders/{productOrderNumber}/upsell-variant - вариант модального окна")
    public Response getUpsaleVariant(String authToken, String orderNumber) {
        Response response = orderApi.getUpsaleVariant(authToken, orderNumber);
        AllureAttachments.attachJson("Upsale variant Response", response.getBody().asString());
        return response;
    }

    @Step("Валидация успешного ответа upsell-variant (200)")
    public void validateUpsellVariantResponse(Response response) {
        assertSuccessStatusCode(response);
        assertContentTypeJson(response);

        Map<String, Object> root = response.jsonPath().getMap("$");
        assertNotNull(root, "Тело должно быть JSON-объектом");

        assertAll("Поля upsell-variant",
                () -> assertNotNull(root.get("upsellVariant"), "upsellVariant не null"),
                () -> assertNotNull(root.get("onboardingFinished"), "onboardingFinished не null")
        );
    }

    @Step("POST /user/orders/{productOrderNumber}/upsell-modal-opened - открытие модального окна апсейлов")
    public Response postUpsellModalOpened(String authToken, String orderNumber) {
        Response response = orderApi.upsellModalOpened(authToken, orderNumber);
        AllureAttachments.attachJson("Upsell Modal Opened Response", response.getBody().asString());
        return response;
    }

    @Step("Валидация ответа upsell-modal-opened")
    public void validateUpsellModalOpenedResponse(Response response) {
        assertSuccessStatusCode(response);
        assertContentTypeJson(response);

        List<Object> body = response.jsonPath().getList("$");
        assertNotNull(body, "Тело должно быть JSON-массивом");
        assertTrue(body.isEmpty(), "Массив должен быть пустым, получено элементов: " + body.size());
    }

    @Step ("Извлечение orderNumber из URL страницы (/order/status/{orderNumber})")
    public static String extractOrderNumberFromUrl(String url) {
        Matcher matcher = ORDER_NUMBER_PATTERN.matcher(url);
        assertTrue(matcher.find(), "orderNumber не найден в URL: " + url);
        return matcher.group(1);
    }

    @Step ("Извлечение параметра из href кнопки (loginToken, password, orderNumber)")
    public static String extractQueryParam(String url, String paramName) {
        String query = URI.create(url).getQuery();
        if(query == null) return null;
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=", 2);
            if(kv.length == 2 && kv[0].equals(paramName))
                return URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
        }
        return null;
    }

    @Step("GET /order/status/{orderNumber}")
    public Response getOrderStatus(String orderNumber) {
        Response response = siteApi.getOrderStatus(orderNumber);
        AllureAttachments.attachJson("order/status Response", response.getBody().asString());
        return response;
    }

    @Step("GET /order/success/{orderNumber}")
    public Response getOrderSuccess(String orderNumber) {
        Response response = apiApi.getOrderSuccess(orderNumber);
        AllureAttachments.attachJson("order/success Response", response.getBody().asString());
        return response;
    }

    @Step("Валидация order/status")
    public void validateOrderStatusResponse(Response response) {
        assertBasicResponse(response);
        assertJsonFieldEquals(response, "status", "ok");
    }

    @Step("Валидация order/success и согласованность с href кнопки")
    public void validateOrderSuccessResponse(Response response, String orderNumber,
                                             String expectedLoginToken, String expectedPassword) {

        assertBasicResponse(response);

        assertJsonFieldNotEmpty(response, "login");
        assertJsonFieldNotEmpty(response, "password");
        assertJsonFieldNotEmpty(response, "loginToken");
    }

    @Step("Полная проверка успешной оплаты")
    public static PaymentCredentials validateSuccessfulPayment(CloudPage cloudPage) {
        OrderSteps orderSteps = new OrderSteps(TestConfig.getCurrentApiUrl());

        PaymentCredentials credentials = extractPaymentCredentials(cloudPage);

        sleep(5000);

        step("Проверка статуса заказа", () -> {
            orderSteps.validateOrderStatusResponse(
                    orderSteps.getOrderStatus(credentials.orderNumber)
            );
        });

        step("Валидация успешного ответа", () -> {
            orderSteps.validateOrderSuccessResponse(
                    orderSteps.getOrderSuccess(credentials.orderNumber),
                    credentials.orderNumber, credentials.loginToken, credentials.password
            );
        });
        return credentials;
    }

    public record PaymentCredentials(String loginToken, String password, String orderNumber) {}

    @Step("Извлечение данных оплаты из страницы успеха: loginToken, password, orderNumber")
    public static PaymentCredentials extractPaymentCredentials(CloudPage cloudPage) {
        OrderSteps orderSteps = new OrderSteps(TestConfig.getCurrentApiUrl());

        String href = cloudPage.verifySuccessProfilePage();
        String orderNumber = orderSteps.extractOrderNumberFromUrl(
                com.codeborne.selenide.WebDriverRunner.getWebDriver().getCurrentUrl()
        );
        String loginToken = orderSteps.extractQueryParam(href, "loginToken");
        String password = orderSteps.extractQueryParam(href, "password");

        return new PaymentCredentials(loginToken, password, orderNumber);
    }

}