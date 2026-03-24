package ru.tutor.test.api.club;


import io.qameta.allure.*;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tutor.config.TestConfig;

import ru.tutor.model.club.ClubPaymentResponse;

import ru.tutor.steps.AuthSteps;
import ru.tutor.steps.club.ClubSteps;
import io.restassured.response.Response;


import java.util.List;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AllureJunit5.class)
@Epic("API Тесты пользователя")
@Feature("Клуб пользователя")
@DisplayName("Тесты эндпоинтов клуба")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)


public class ClubPaymentTest {

    // Общие константы
    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private final String TEST_EMAIL = TestConfig.getTestEmailFortune();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();

    private final ClubSteps clubSteps = ClubSteps.withBaseUrl(BASE_URL);
    private final AuthSteps authSteps = AuthSteps.withBaseUrl(BASE_URL);

    private String authToken;
    private static List<ClubPaymentResponse> payments;

    @BeforeAll
    public static void setupAll() {
        Allure.label("component", "API");
        Allure.label("feature", "User Club");
    }

    @BeforeEach
    public void setup() {
        authToken = step("Логин", () ->
                authSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );
    }

    @Test
    @Order(1)
    @DisplayName("Успешное получение истории платежей (Проверка 200 ОК)")
    @Story("Основной сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("positive")
    @Tag("smoke")
    void getClubPaymentsSuccess() {
        Response response = step("GET /user/club/payments", () ->
                clubSteps.getClubPayments(authToken)
        );

        payments = step("Валидация ответа с платежами", ()->
                clubSteps.validateClubPaymentsResponse(response)
        );

        step("Проверка структуры платежей", () -> {
            clubSteps.validatePaymentsStructure(payments);

            Allure.addAttachment("Количество платежей", "text/plain",
                    "Получено платежей: " + payments.size());
        });
    }

    @Test
    @Order(2)
    @DisplayName("Проверка направления платежей (income/outcome)")
    @Story("Классификация платежей")
    @Severity(SeverityLevel.NORMAL)
    @Tag("validation")
    @Tag("positive")
    void shouldHaveCorrectPaymentDirections(){
        if (payments == null) {
            Response response = step("Получение истории платежей", () ->
                    clubSteps.getClubPayments(authToken)
            );
            payments = clubSteps.validateClubPaymentsResponse(response);
        }

        step("Проверка направлений платежей", () -> {
            for  (ClubPaymentResponse payment : payments) {
                assertTrue(payment.isIncome() || payment.isOutcome(),
                        "аправление платежей должно быть 'income' или 'outcome'");

                String direction = payment.isIncome() ? "💰 Доход" : "💸 Расход";
                Allure.addAttachment("Направление платежа", "text/plain",
                        String.format("%s: %s (%d руб.)",
                                direction, payment.getName() , payment.getAmount())
                );
            }
        });
    }

    @Test
    @Order(3)
    @DisplayName("Проверка получения истории платежей с невалидным токеном (Проверка 401)")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("negative")
    @Tag("security")
    void shouldFailWithoutAuthorization() {
        Response response = step("Попытка получения истории платежей с невалидным токеном", () ->
                clubSteps.getClubPayments("invalid")
        );

        step("Проверка ошибки авторизации", () ->
                clubSteps.validateUnauthorizedResponse(response)
        );
    }

    @Test
    @Order(4)
    @DisplayName("Проверка получения истории платежей без авторизации (Проверка 401)")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("negative")
    @Tag("security")
    void shouldFailWithoutAuthorization1() {
        Response response = step("Попытка получения платежей без токена", () ->
                clubSteps.getClubPayments("")
        );

        step("Проверка ошибки авторизации", () ->
                clubSteps.validateUnauthorizedResponse(response)
        );
    }

    @Test
    @Order(5)
    @DisplayName("Проверка корректности дат платежей")
    @Story("Валидация данных")
    @Severity(SeverityLevel.NORMAL)
    @Tag("validation")
    @Tag("positive")
    void shouldHaveValidPaymentDates() {
        if (payments == null) {
            Response response = step("Получение истории платежей", () ->
                    clubSteps.getClubPayments(authToken)
            );
            payments = clubSteps.validateClubPaymentsResponse(response);
        }

        step("Проверка дат платежей", () -> {
            for (ClubPaymentResponse payment : payments) {
                assertNotNull(payment.getDateAsLocalDateTime(),
                        "Дата платежа должна быть в корректном формате ISO");

                Allure.addAttachment("Проверка даты", "text/plain",
                        String.format("Платеж: %s\nДата: %s\nФормат: ✓",
                                payment.getName(), payment.getDate()));
            }
        });
    }
}
