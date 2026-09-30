package ru.tutor.test.api.order;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import ru.tutor.config.TestConfig;
import ru.tutor.steps.account.AuthSteps;
import ru.tutor.steps.orders.OrderSteps;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;
import ru.tutor.constants.Tags;

@Epic("API Тесты пользователя")
@Feature("История платежей")
@DisplayName("Тесты эндпоинта GET /user/orders/payments")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderPaymentsTest {

    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();

    private final AuthSteps userSteps = new AuthSteps(BASE_URL);
    private final OrderSteps orderSteps = new OrderSteps(BASE_URL);

    private String token;

    @BeforeEach
    void setup() {
        token = step("Логин пользователя", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );
    }

    @Test
    @Order(1)
    @DisplayName("Успешное получение истории платежей")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.POSITIVE)
    @Tag(Tags.SMOKE)
    void getOrderPaymentsSuccess() {
        Response response = step("GET /user/orders/payments", () ->
                orderSteps.getOrdersPayments(token)
        );

        step("Валидация ответа", () ->
            orderSteps.validateOrdersPaymentsResponse(response)
        );
    }

    @Test
    @Order(2)
    @DisplayName("Получение истории платежей без авторизации")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.NEGATIVE)
    void getOrdersPaymentsWithoutAuth() {
        Response response = step("GET /user/orders/payments без токена", () ->
                orderSteps.getOrdersPayments("")
        );
        step("Проверка статуса 401", () ->
            assertEquals(401, response.getStatusCode())
        );
    }

    @Test
    @Order(3)
    @DisplayName("Получение истории платежей с невалидным токеном")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    void getOrdersPaymentsWithInvalidToken() {
        Response response = step("с невалидным токеном", () ->
                orderSteps.getOrdersPayments("invalid_token")
        );

        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode())
        );
    }

    @Test
    @Order(4)
    @DisplayName("Успешное получение информации по бонусному доступу")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.POSITIVE)
    @Tag(Tags.SMOKE)
    void getOrderBonusPaymentsSuccess() {
        Response response = step("GET /user/orders/bonus-access-info", () ->
                orderSteps.getOrdersBonusAccessInfo(token)
        );

        step("Валидация ответа", () ->
                orderSteps.validateBonusAccessInfoResponse(response)
        );
    }

    @Test
    @Order(5)
    @DisplayName("Получение информации по бонусному доступу без авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    void getOrdersBonusPaymentsWithoutAuth() {
        Response response = step("GET /user/orders/bonus-access-info без токена", () ->
                orderSteps.getOrdersBonusAccessInfo("")
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode())
        );
    }

    @Test
    @Order(6)
    @DisplayName("Получение истории платежей с невалидным токеном")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    void getOrdersBonusPaymentsWithInvalidToken() {
        Response response = step("с невалидным токеном", () ->
                orderSteps.getOrdersBonusAccessInfo("invalid_token")
        );

        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode())
        );
    }

    @Test
    @Order(7)
    @DisplayName("Успешное получение информации по состоянию подписки")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.POSITIVE)
    @Tag(Tags.SMOKE)
    void getOrderSubscriptionStateSuccess() {
        Response response = step("GET /user/orders/subscription/state", () ->
                orderSteps.getOrdersSubscriptionState(token)
        );

        step("Валидация ответа", () ->
                orderSteps.validateOrderSubscriptionStateResponse(response)
        );
    }

    @Test
    @Order(8)
    @DisplayName("Получение информации по бонусному доступу без авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    void getOrdersSubscriptionStateWithoutAuth() {
        Response response = step("GET /user/orders/subscription/state без токена", () ->
                orderSteps.getOrdersSubscriptionState("")
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode())
        );
    }

    @Test
    @Order(9)
    @DisplayName("Получение истории платежей с невалидным токеном")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    void getOrdersSubscriptionStateWithInvalidToken() {
        Response response = step("с невалидным токеном", () ->
                orderSteps.getOrdersSubscriptionState("invalid_token")
        );

        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode())
        );
    }


}
