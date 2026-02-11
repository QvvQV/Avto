package ru.tutor.test.api;


import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.tutor.config.TestConfig;
import ru.tutor.model.order.OrderRequest;
import ru.tutor.model.order.OrderResponse;
import ru.tutor.steps.AuthSteps;
import ru.tutor.steps.OrderSteps;
import ru.tutor.steps.UserSteps;

@Epic("Заказы и подписки")
@Feature("API управления заказами")
@DisplayName("Тесты API заказов и подписок")
public class OrderApiTest {

    private ru.tutor.steps.OrderSteps orderSteps;
    private final AuthSteps authSteps = new AuthSteps(TestConfig.getApiBaseUrl());

    @BeforeEach
    void setUp() {
        orderSteps = new OrderSteps(TestConfig.getApiBaseUrl());
    }

    @Test
    @DisplayName("Создание заказа с email")
    void shouldCreateOrderForSpecificUser() {


    }
}
