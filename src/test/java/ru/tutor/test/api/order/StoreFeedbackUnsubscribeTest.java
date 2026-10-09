package ru.tutor.test.api.order;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import ru.tutor.config.TestConfig;
import ru.tutor.model.account.RegistrationRequest;
import ru.tutor.steps.account.AuthSteps;
import ru.tutor.steps.account.RegistrationSteps;
import ru.tutor.steps.catalog.ProductsSteps;
import ru.tutor.steps.orders.OrderSteps;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;
import ru.tutor.constants.Tags;

@Epic("Сохранение отзыва при отписке")
@Feature("Сохранение отзыва при отписке")
@DisplayName("Тесты POST /user/orders/store-feedback-unsubscribe/{orderNumber}")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag(Tags.INTEGRATION)
public class StoreFeedbackUnsubscribeTest {

    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();

    private final AuthSteps userSteps = new AuthSteps(BASE_URL);
    private final OrderSteps orderSteps = new OrderSteps(BASE_URL);
    private final ProductsSteps productsSteps = new ProductsSteps(BASE_URL);
    private final RegistrationSteps registrationSteps = new RegistrationSteps(BASE_URL);

    private String token;
    private String orderNumber;

    @BeforeEach
    void setUp() {
        token = step("Логин пользователя", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );
    }

    @Test
    @Order(1)
    @DisplayName("Успешное сохранение отзыва при отписке")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    void storeFeedbackSuccess() {
        String orderNumber = step("Получение orderNumber премиума", () ->
                productsSteps.fetchPremiumOrderNumber(token)
        );

        Response response = step("POST /user/orders/store-feedback-unsubscribe/{orderNumber}", () ->
                orderSteps.storeFeedbackUnsubscribe(token, orderNumber, "Тестовое сообщение", 4)
        );

        step("Валидация: message + order_id", () ->
                orderSteps.validateStoreFeedbackSuccess(response)
        );
    }

    @Test
    @Order(2)
    @DisplayName("Сохранение отзыва без токена авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void storeFeedbackWithoutAuth() {
        Response response = step("POST /user/orders/store-feedback-unsubscribe - без токена", () ->
                orderSteps.storeFeedbackUnsubscribe("", orderNumber, "Test", 4)
        );

        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(3)
    @DisplayName("Сохранение отзыва с невалидным токеном")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void storeFeedbackInvalidToken() {
        Response response = step("POST /user/orders/store-feedback-unsubscribe - без токена", () ->
                orderSteps.storeFeedbackUnsubscribe("invalid_token", orderNumber, "Test", 4)
        );

        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(4)
    @DisplayName("Сохранение отзыва с несуществующим orderNumber")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.CONTRACT)
    void storeFeedbackInvalidOrderNumber() {
        Response response = step("POST /user/orders/store-feedback-unsubscribe - невалидный orderNumber", () ->
                orderSteps.storeFeedbackUnsubscribe(token, "258", "Test", 4)
        );

        step("Проверка допустимого статуса (400)", () ->
                assertEquals(400, response.getStatusCode()));
    }

    @Test
    @Order(5)
    @DisplayName("Сохранение отзыва с рейтингом за пределами допустимого")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.POSITIVE)
    void storeFeedbackInvalidRating() {
        String orderNumber = step("Получение orderNumber премиума", () ->
                productsSteps.fetchPremiumOrderNumber(token)
        );

        Response response = step("POST /user/orders/store-feedback-unsubscribe - rating=0", () ->
                orderSteps.storeFeedbackUnsubscribe(token, orderNumber, "Test", 10)
        );

        step("Проверка допустимого статуса (200)", () ->
                orderSteps.validateStoreFeedbackSuccess(response));
    }

    @Test
    @Order(6)
    @DisplayName("Сохранение отзыва пользователем без автивной подписки")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void storeFeedbackWithoutActiveSubscription() {
        String newUserToken = step("Регистрация нового пользователя без подписки", () -> {
            RegistrationRequest request = RegistrationRequest.generateRandom();
            return registrationSteps.registerAndLogin(request);
        });

        Response response = step("POST /user/orders/store-feedback-unsubscribe - с несуществующим orderNumber", () ->
                orderSteps.storeFeedbackUnsubscribe(newUserToken, orderNumber, "Test", 4)
        );

        step("Проверка: сервер отклоняет 400", () -> {
            int statusCode = response.getStatusCode();
            String responseBody = response.getBody().asString();

            Allure.addAttachment("Ответ без подписки", "text/plain",
                    String.format("Статус код: %d\nТело ответа: %s", statusCode, responseBody));

            step("Проверка: 400 + error.message = 'Order not found'", () ->
                    orderSteps.validateOrderNotFound(response)
            );
        });
        }
}