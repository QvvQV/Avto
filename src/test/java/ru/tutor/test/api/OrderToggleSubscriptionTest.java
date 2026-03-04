package ru.tutor.test.api;


import io.qameta.allure.*;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tutor.config.TestConfig;
import ru.tutor.steps.AuthSteps;
import ru.tutor.steps.OrderToggleSubscriptionSteps;
import ru.tutor.steps.fortune.GameWheelRewardsSteps;

import java.util.*;

import static io.qameta.allure.Allure.step;

@ExtendWith(AllureJunit5.class)
@Epic("API Тесты пользователя")
@Feature("Управление заказами")
@DisplayName("Тесты переключения подписки")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class OrderToggleSubscriptionTest {

    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();
    private final Integer TEST_ORDER_ID = 180;


    private final AuthSteps authSteps = AuthSteps.withBaseUrl(BASE_URL);
    private final GameWheelRewardsSteps gameWheelRewardsSteps = GameWheelRewardsSteps.withBaseUrl(BASE_URL);
    private final OrderToggleSubscriptionSteps orderSteps = OrderToggleSubscriptionSteps.withBaseUrl(BASE_URL);


    private static String token;

    @BeforeAll
    public static void setUp() {
        Allure.label("component", "API");
        Allure.label("feature", "Order Subscription");
    }

    @BeforeEach
    void setUpAuth() {
        token = step("Логин Пользователя", () ->
                authSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );
    }

    @Test
    @Order(1)
    @DisplayName("Успешное переключение подписки (Проверка 200 ОК)")
    @Story("Основной сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("positive")
    void toggleOrderSubscriptionSuccess() {
        Response response = step("POST /user/orders/toggle_subscription/" + TEST_ORDER_ID, () ->
                orderSteps.toggleOrderSubscription(token, TEST_ORDER_ID)
        );

        step("Анализ формата ответа", () ->
                orderSteps.analyzeResponseFormat(response));
    }

    @Test
    @Order(2)
    @DisplayName("Проверка без авторизации (401)")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("negative")
    @Tag("security")
    void shouldFailWithoutAuthorization() {
        Response response = step("Отправка POST запроса без токена", () ->
                orderSteps.toggleOrderSubscription("", TEST_ORDER_ID)
        );

        step("Проверка ошибки авторизации", () ->
                gameWheelRewardsSteps.validateUnauthorizedResponse(response)
        );
    }
}
