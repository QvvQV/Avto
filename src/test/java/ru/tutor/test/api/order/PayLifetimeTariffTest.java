package ru.tutor.test.api.order;

import io.qameta.allure.*;
import io.qameta.allure.junit5.AllureJunit5;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tutor.config.TestConfig;
import ru.tutor.steps.account.AuthSteps;
import ru.tutor.steps.orders.OrderSteps;

import static io.qameta.allure.Allure.step;
import ru.tutor.constants.Tags;

@ExtendWith(AllureJunit5.class)
@Epic("API Тесты пользователя")
@Feature("Оплата подписки")
@DisplayName("Тесты оплата lifetime-тарифа")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PayLifetimeTariffTest {

    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();

    private final AuthSteps userSteps = new AuthSteps(BASE_URL);
    private final OrderSteps orderSteps = new OrderSteps(BASE_URL);

    private static String token;

    @BeforeEach
    void setUpAuth() {
        token = step("Логин Пользователя", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD));
    }

    @Test
    @Order(1)
    @DisplayName("POST /user/orders/pay-lifetime-tariff - успешная оплата (200, тело не пустое)")
    @Story("Основной сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    void payLifetimeTariffSuccess() {
        Response response = step("POST /user/orders/pay-lifetime-tariff", () ->
                orderSteps.payLifetimeTariff(token));
        step("Валидация ответа (статус 200, тело не пустое и не null, нет ошибки 400)", () ->
                orderSteps.validatePayLifetimeTariffResponse(response));
    }
}
