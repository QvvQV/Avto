package ru.tutor.test.api.user;

import io.qameta.allure.*;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.junit.jupiter.api.*;
import ru.tutor.config.TestConfig;
import ru.tutor.model.order.RefundTicketRequest;
import ru.tutor.steps.account.AuthSteps;
import ru.tutor.steps.orders.OrderSteps;

import java.util.Locale;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import ru.tutor.constants.Tags;

@Epic("API Тесты пользователя")
@Feature("Заявки на возврат")
@DisplayName("Тесты эндпоинта создания заявки на возврат")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RefundTicketTest {

    private final AuthSteps userSteps = new AuthSteps(TestConfig.getCurrentApiUrl());
    private final OrderSteps orderSteps = new OrderSteps(TestConfig.getCurrentApiUrl());
    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();
    private static final Faker faker = new Faker(Locale.ENGLISH);

    private static String token;

    @BeforeAll
    public static void setUp() {
        Allure.label("component", "API");
        Allure.label("feature", "Refund");
    }

    @BeforeEach
    void setUpAuth() {
        token = step("Логин пользователя", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );
    }

    private RefundTicketRequest createRefundRequest() {
        return new RefundTicketRequest(
                faker.name().firstName(),
                faker.name().lastName(),
                "no data",
                faker.name().nameWithMiddle(),
                faker.internet().emailAddress(),
                "424242******4242",
                "Скрабби",
                "13-07-2026 00:00:00 +03:00",
                5000,
                "это не имеет значение",
                "Неудобный формат обучения"
        );
    }

    private RefundTicketRequest createEmptyRefundRequest() {
        return new RefundTicketRequest(
                "",
                "",
                "no data",
                "",
                "",
                "",
                "",
                "",
                0,
                "",
                "Неудобный формат обучения"
        );
    }

    @Test
    @Order(1)
    @DisplayName("POST /user/refund-tickets/create - создание заявки на возврат")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.POSITIVE)
    void createRefundTicketSuccess() {
        RefundTicketRequest request =step("Подготовка тела запроса", this::createRefundRequest
        );

        Response response = step("POST /user/refund-tickets/create", () ->
                orderSteps.createRefundTicket(token, request)
        );
        step("Валидация ответа", () ->
                orderSteps.validateRefundTicketResponse(response));
    }

    @Test
    @Order(2)
    @DisplayName("POST /user/refund-tickets/create - без авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    void createRefundTicketWithoutAuth() {
        RefundTicketRequest request = createRefundRequest();

        Response response = step("POST /user/refund-tickets/create - без токена", () ->
                orderSteps.createRefundTicket("", request)
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(3)
    @DisplayName("POST /user/refund-tickets/create - невалидный токен")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    void createRefundTicketWithInvalidToken() {
        RefundTicketRequest request = createRefundRequest();

        Response response = step("POST /user/refund-tickets/create - без токена", () ->
                orderSteps.createRefundTicket("invalid", request)
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(4)
    @DisplayName("POST /user/refund-tickets/create - пустое тело")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    void createRefundTicketWithEmptyBody() {
        RefundTicketRequest request = createEmptyRefundRequest();

        Response response = step("POST /user/refund-tickets/create - без тела", () ->
                orderSteps.createRefundTicket(token, request)
        );
        step("Проверка статуса 422", () ->
                orderSteps.validateRefundValidationError(response));
    }

    @Test
    @Order(5)
    @DisplayName("GET /user/refund-tickets - список заявок")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.POSITIVE)
    void getRefundTicketsSuccess() {
        Response response = step("GET /user/refund-tickets", () ->
                orderSteps.getRefundTickets(token)
        );
        step("Валидация ответа", () ->
                orderSteps.validateRefundTicketResponse(response) // эндпоинт после создания /user/refund-tickets/create POST
        );
    }

    @Test
    @Order(6)
    @DisplayName("GET /user/refund-tickets - без авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    void getRefundTicketsWithoutAuth() {
        Response response = step("GET /user/refund-tickets - без токена", () ->
                orderSteps.getRefundTickets("")
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode())
        );
    }

    @Test
    @Order(7)
    @DisplayName("GET /user/refund-tickets - невалидный токен")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    void getRefundTicketsInvalidToken() {
        Response response = step("GET /user/refund-tickets - без токена", () ->
                orderSteps.getRefundTickets("invalid")
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode())
        );
    }
}
