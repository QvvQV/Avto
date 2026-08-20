package ru.tutor.test.api.subscription;

import io.qameta.allure.*;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tutor.config.TestConfig;
import ru.tutor.steps.AuthSteps;
import ru.tutor.steps.OrderToggleSubscriptionSteps;
import ru.tutor.steps.fortune.GameWheelRewardsSteps;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

    private final AuthSteps userSteps = new AuthSteps(BASE_URL);
    private final GameWheelRewardsSteps gameWheelRewardsSteps = new GameWheelRewardsSteps(BASE_URL);
    private final OrderToggleSubscriptionSteps orderSteps = new OrderToggleSubscriptionSteps(BASE_URL);


    private static String token;

    @BeforeAll
    public static void setUp() {
        Allure.label("component", "API");
        Allure.label("feature", "Order Subscription");
    }

    @BeforeEach
    void setUpAuth() {
        token = step("Логин Пользователя", () ->

                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );
    }

    @Test
    @Order(1)
    @DisplayName("Успешное переключение Премиум подписки (Проверка 200 ОК)")
    @Story("Основной сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("positive")
    void toggleOrderSubscriptionSuccess() {
        Response response = step("POST /user/orders/toggle_subscription/" + TEST_ORDER_ID, () ->
                orderSteps.toggleOrderSubscription(token, TEST_ORDER_ID)
        );

        step("Анализ формата ответа", () ->
                orderSteps.analyzeResponseFormat(response)
        );
        Response restorePremium = step("POST /user/orders/restore-premium", () ->
                orderSteps.restorePremium(token)
        );
        step("Валидация ответа", () ->
                orderSteps.validateRestorePremiumResponse(restorePremium));
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

    @Test
    @Order(3)
    @DisplayName("Восстановление подписки без авторизации")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void shouldFailWithoutTokenRestorePremium() {
        Response response = step("POST /user/orders/restore-premium - восстановление премиум без авторизации", () ->
                orderSteps.restorePremium("")
        );
        step("Валидация ответа", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(4)
    @DisplayName("Восстановление подписки с невалидным токеном")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void shouldFailInvalidTokenRestorePremium() {
        Response response = step("POST /user/orders/restore-premium - восстановление премиум без авторизации", () ->
                orderSteps.restorePremium("invalid")
        );
        step("Валидация ответа", () ->
                assertEquals(401, response.getStatusCode()));
    }
}
