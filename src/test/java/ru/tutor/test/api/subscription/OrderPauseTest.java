package ru.tutor.test.api.subscription;

import io.qameta.allure.*;
import io.qameta.allure.junit5.AllureJunit5;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tutor.config.TestConfig;
import ru.tutor.steps.OrderSteps;
import ru.tutor.steps.UserSteps;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(AllureJunit5.class)
@Epic("API Тесты пользователя")
@Feature("Управление подпиской")
@DisplayName("Тесты паузы подписки")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderPauseTest {

    private final UserSteps userSteps = new UserSteps(TestConfig.getCurrentApiUrl());
    private final OrderSteps orderSteps = new OrderSteps(TestConfig.getCurrentApiUrl());
    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();

    private static String token;

    @BeforeAll
    public static void setUp() {
        Allure.label("component", "API");
        Allure.label("feature", "Orders");
    }

    @BeforeEach
    void setUpAuth() {
        token = step("Логин пользователя", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD));
    }

    @Test
    @Order(1)
    @DisplayName("PUT /user/orders/pause/7 - поставить паузу на 7 дней")
    @Severity(SeverityLevel.NORMAL)
    @Tag("positive")
    void pauseOrderSuccess() {
        Response response = step("PUT /user/orders/pause/7", () ->
                orderSteps.pauseOrder(token, 7)
        );
        step("Валдиация ответа", () ->
                orderSteps.validateEmptyArrayResponse(response));
    }

    @Test
    @Order(2)
    @DisplayName("PUT /user/orders/unpause - снять паузу")
    @Severity(SeverityLevel.NORMAL)
    @Tag("positive")
    void unpauseOrderSuccess() {
        Response response = step("PUT /user/orders/unpause", () ->
                orderSteps.unpauseOrder(token)
        );
        step("Валидация ответа", () ->
                orderSteps.validateEmptyArrayResponse(response));
    }

    @Test
    @Order(3)
    @DisplayName("PUT /user/orders/pause/7 - без авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void pauseOrderWithoutAuth() {
        Response response = step("PUT /user/orders/pause/7 без токена", () ->
                orderSteps.pauseOrder("", 7)
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode())
        );
    }

    @Test
    @Order(4)
    @DisplayName("PUT /user/orders/unpause - без авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void unpauseOrderWithoutAuth() {
        Response response = step("PUT /user/orders/unpause без токена", () ->
                orderSteps.unpauseOrder("")
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(5)
    @DisplayName("PUT /user/orders/pause/7 с инвалидным токеном")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void pauseOrderWithInvalidToken() {
        Response response = step("PUT /user/orders/pause/7 с невалидным токеном", () ->
                orderSteps.pauseOrder("invalid_token", 7)
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode())
        );
    }

    @Test
    @Order(6)
    @DisplayName("PUT /user/orders/unpause с невалидным токеном")
    void  unpauseOrderWithInvalidToken() {
        Response response = step ("POST /user/orders/unpause с инвалидным токеном", () ->
                orderSteps.unpauseOrder("invalid")
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode())
        );
    }
}