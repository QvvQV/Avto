package ru.tutor.test.api.subscription;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import io.restassured.response.Response;
import ru.tutor.config.TestConfig;
import ru.tutor.flow.account.RenewalModalFlow;
import ru.tutor.steps.account.AuthSteps;
import ru.tutor.steps.orders.OrderToggleSubscriptionSteps;
import ru.tutor.steps.fortune.GameWheelRewardsSteps;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import ru.tutor.constants.Tags;

@Epic("API Тесты пользователя")
@Feature("Управление заказами")
@DisplayName("Тесты переключения подписки")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class OrderToggleSubscriptionTest {

    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();

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
    @Tag(Tags.POSITIVE)
    void toggleOrderSubscriptionSuccess() {
        Response response = step("POST /user/orders/toggle_subscription/" + RenewalModalFlow.LITE_PRODUCT_ID, () ->
                orderSteps.toggleOrderSubscription(token, RenewalModalFlow.LITE_PRODUCT_ID)
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
    @Tag(Tags.NEGATIVE)
    void shouldFailWithoutAuthorization() {
        Response response = step("Отправка POST запроса без токена", () ->
                orderSteps.toggleOrderSubscription("", RenewalModalFlow.LITE_PRODUCT_ID)
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
    @Tag(Tags.NEGATIVE)
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
    @Tag(Tags.NEGATIVE)
    void shouldFailInvalidTokenRestorePremium() {
        Response response = step("POST /user/orders/restore-premium - восстановление премиум без авторизации", () ->
                orderSteps.restorePremium("invalid")
        );
        step("Валидация ответа", () ->
                assertEquals(401, response.getStatusCode()));
    }
}
