package ru.tutor.test.api.order;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tutor.config.TestConfig;
import ru.tutor.steps.account.AuthSteps;
import ru.tutor.steps.orders.OrderSteps;
import ru.tutor.steps.catalog.ProductsSteps;
import io.restassured.response.Response;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import ru.tutor.constants.Tags;

@ExtendWith(AllureJunit5.class)
@Epic("API Тесты пользователя")
@Feature("Модальное окно апсейлов")
@DisplayName("Тесты эндпоинта POST /user/orders/upsale")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UpsaleContentDiamondTest {

    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private final AuthSteps authSteps = new AuthSteps(BASE_URL);
    private final OrderSteps orderSteps = new OrderSteps(BASE_URL);
    private final ProductsSteps  productsSteps = new ProductsSteps(BASE_URL);
    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();

    private static String token;
    private static String orderNumber;

    @BeforeEach
    void setupOrderNumber() {
        token = step("Логин пользователя", () ->
                authSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );
        orderNumber = step("Получение orderNumber премиума", () ->
                productsSteps.fetchFirstOrderNumber(token)
        );
    }

    @Test
    @Order(1)
    @DisplayName("Успешное получение апсейла за алмазы")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    void getUpsaleContentDiamondSuccess() {
        Response response = step("GET /user/orders/upsale-content-diamond/{orderNumber}", () ->
                orderSteps.getUpsaleContentDiamond(token, orderNumber)
        );
        step("Валидация: статус 200, тело []", () ->
                orderSteps.validateUpsaleContentDiamondResponse(response));
    }

    @Test
    @Order(2)
    @DisplayName("Получение апсейла за алмазы с невалидным токеном")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    void getUpsaleContentDiamondInvalidToken() {
        Response response = step("GET /user/orders/upsale-content-diamond - невалдиный токен", () ->
                orderSteps.getUpsaleContentDiamond("invalid_token", orderNumber)
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(3)
    @DisplayName("Получение апсейла за алмазы с невалидным токеном")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    void getUpsaleContentDiamondWithoutAuth() {
        Response response = step("GET /user/orders/upsale-content-diamond - без токена", () ->
                orderSteps.getUpsaleContentDiamond("", orderNumber)
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(4)
    @DisplayName("Получение апсейла за алмазы с несуществующим orderNumber")
    @Severity(SeverityLevel.NORMAL)
    void getUpsaleContentDiamondInvalidOrderNumber() {
        Response response = step("GET /user/orders/upsale-content-diamond - невалидный orderNumber", () ->
                orderSteps.getUpsaleContentDiamond(token, "999999")
        );
        step("Проверка 404 + message = 'Product order not found' ", () ->
                orderSteps.validateProductOrderNotFound(response));
    }

    @Test
    @Order(5)
    @DisplayName("Успешное получение апсейла за деньги")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    void getUpsaleContentMoneySuccess() {
        Response response = step("GET /user/orders/upsale-content-invoice/{productOrderNumber}", () ->
                orderSteps.getUpsaleContentMoney(token, orderNumber)
        );
        step("Валидация структуры ответа", () ->
                orderSteps.validateUpsaleContentInvoiceResponse(response));
    }

    @Test
    @Order(6)
    @DisplayName("Получение апсейла за деньги с невалидным токеном")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    void getUpsaleContentMoneyInvalidToken() {
        Response response = step("GET /user/orders/upsale-content-invoice/{productOrderNumber} - невалдиный токен", () ->
                orderSteps.getUpsaleContentMoney("invalid_token", orderNumber)
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(7)
    @DisplayName("Получение апсейла за деньги с невалидным токеном")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    void getUpsaleContentMoneyWithoutAuth() {
        Response response = step("GET /user/orders/upsale-content-invoice/{productOrderNumber} - без токена", () ->
                orderSteps.getUpsaleContentMoney("", orderNumber)
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(8)
    @DisplayName("Получение апсейла за деньги с несуществующим orderNumber")
    @Severity(SeverityLevel.NORMAL)
    void getUpsaleContentMoneyInvalidOrderNumber() {
        Response response = step("GET /user/orders/upsale-content-invoice/{productOrderNumber} - невалидный orderNumber", () ->
                orderSteps.getUpsaleContentMoney(token, "999999")
        );
        step("Проверка 404 + message = 'Product order not found' ", () ->
                orderSteps.validateProductOrderNotFound(response));
    }

    @Test
    @Order(9)
    @DisplayName("Успешное получение варианта модального окна апсейлов")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    void getUpsaleVariantSuccess() {
        Response response = step("GET /user/orders/{productOrderNumber}/upsell-variant", () ->
                orderSteps.getUpsaleVariant(token, orderNumber)
        );
        step("Валидация структуры ответа", () ->
                orderSteps.validateUpsellVariantResponse(response) );
    }

    @Test
    @Order(10)
    @DisplayName("Получение варианта модального окна апсейлов с невалидным токеном")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    void getUpsaleVariantInvalidToken() {
        Response response = step("GET /user/orders/{productOrderNumber}/upsell-variant - невалидный токен", () ->
                orderSteps.getUpsaleVariant("invalid_token", orderNumber)
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(11)
    @DisplayName("Получение варианта модального окна апсейла с пустым токеном")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    void getUpsaleVariantWithoutAuth() {
        Response response = step("GET /user/orders/{productOrderNumber}/upsell-variant - без токена", () ->
                orderSteps.getUpsaleVariant("", orderNumber)
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(12)
    @DisplayName("Получение варианта модального окна апсейла с несуществующим orderNumber")
    @Severity(SeverityLevel.NORMAL)
    void getUpsaleVariantInvalidOrderNumber() {
        Response response = step("GET /user/orders/{productOrderNumber}/upsell-variant - невалидный orderNumber", () ->
                orderSteps.getUpsaleVariant(token, "999999")
        );
        step("Проверка 404 + message = 'Product order not found' ", () ->
                orderSteps.validateProductOrderNotFound(response));
    }

    @Test
    @Order(13)
    @DisplayName("Успешное открытие модального окна апсейлов")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    void postUpsellModalOpenedSuccess() {
        Response response = step("POST /user/orders/{productOrderNumber}/upsell-modal-opened", () ->
                orderSteps.postUpsellModalOpened(token, orderNumber)
        );
        step("Проверка статуса 200", () ->
                orderSteps.validateUpsellModalOpenedResponse(response));
    }

    @Test
    @Order(14)
    @DisplayName("Открытие модального окна апсейлов с невалидным токеном")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    void getUpsaleModalOpenedInvalidToken() {
        Response response = step("POST /user/orders/{productOrderNumber}/upsell-modal-opened - невалдиный токен", () ->
                orderSteps.postUpsellModalOpened("invalid_token", orderNumber)
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(15)
    @DisplayName("Получение варианта модального окна апсейла с пустым токеном")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    void getUpsaleModalOpenedWithoutAuth() {
        Response response = step("POST /user/orders/{productOrderNumber}/upsell-modal-opened - без токена", () ->
                orderSteps.postUpsellModalOpened("", orderNumber)
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode()));
    }
}