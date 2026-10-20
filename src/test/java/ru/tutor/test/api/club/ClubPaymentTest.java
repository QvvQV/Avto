package ru.tutor.test.api.club;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import ru.tutor.config.TestConfig;

import ru.tutor.model.club.ClubPaymentResponse;

import ru.tutor.steps.account.AuthSteps;
import ru.tutor.steps.club.ClubPaymentsSteps;
import io.restassured.response.Response;


import java.util.List;

import static io.qameta.allure.Allure.step;
import ru.tutor.constants.Tags;

@Epic("API Тесты пользователя клуба")
@Feature("Клуб пользователя")
@DisplayName("Тесты эндпоинтов клуба")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag(Tags.INTEGRATION)
@Tag(Tags.API)
public class ClubPaymentTest {

    // Общие константы
    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();

    private final ClubPaymentsSteps clubSteps = new ClubPaymentsSteps(BASE_URL);
    private final AuthSteps userSteps = new AuthSteps(BASE_URL);

    @BeforeAll
    public static void setupAll() {
        Allure.label("component", "API");
        Allure.label("feature", "User Club");
    }

    @Test
    @DisplayName("Успешное получение истории платежей (Проверка 200 ОК)")
    @Story("Основной сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    @Tag(Tags.CONTRACT)
    void getClubPaymentsSuccess() {
        String authToken = step("Логин тестового пользователя", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD));

        Response response = step("GET /user/club/payments", () ->
                clubSteps.getClubPayments(authToken)
        );

        List<ClubPaymentResponse> payments = step("Валидация ответа с платежами", ()->
                clubSteps.validateClubPaymentsResponse(response)
        );

        step("Проверка структуры платежей", () -> {
            clubSteps.validatePaymentsStructure(payments);

            Allure.addAttachment("Количество платежей", "text/plain",
                    "Получено платежей: " + payments.size());
        });
    }

    @Test
    @DisplayName("Невалидный токен (401)")
    @Story("Безопасность")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void getClubPaymentsWithInvalidToken(){
       Response response = step("GET /user/club/payments с невалидным токеном", () ->
               clubSteps.getClubPayments("invalid"));

       step("Проверка ошибки авторизации", () ->
               clubSteps.assertUnauthorizedWithMessage(response, "Invalid JWT Token") );
    }

    @Test
    @DisplayName("Пустой токен (Проверка 401)")
    @Story("Безопасность")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void getClubPaymentWithoutToken() {
        Response response = step("GET /user/club/payments без токена", () ->
                clubSteps.getClubPayments("")
        );

        step("Проверка ошибки авторизации", () ->
                clubSteps.assertUnauthorizedWithMessage(response, "JWT Token not found")
        );
    }
}