package ru.tutor.test.api.club;


import io.qameta.allure.*;
import io.qameta.allure.junit5.AllureJunit5;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tutor.config.TestConfig;
import ru.tutor.model.club.WithdrawalRequest;
import ru.tutor.model.club.WithdrawalResponse;
import ru.tutor.steps.AuthSteps;
import ru.tutor.steps.club.ClubWithdrawalSteps;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AllureJunit5.class)
@Epic("API Тесты пользователя")
@Feature("Запросы на вывод средств")
@DisplayName("Тесты эндпоинта Клуба: POST /user/club/request-withdrawal")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("club")
public class ClubWithdrawalTest {

    private final AuthSteps authSteps = AuthSteps.withBaseUrl(TestConfig.getCurrentApiUrl());
    private final ClubWithdrawalSteps withdrawalSteps = ClubWithdrawalSteps.withBaseUrl(TestConfig.getCurrentApiUrl());
    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();

    private static String authToken;

    @BeforeEach
    public void setup() {
        authToken = step("Логин", () ->
                authSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );
    }

    @Test
    @Order(1)
    @DisplayName("Запрос на вывод 100р")
    @Story("Валидация спецификации")
    @Severity(SeverityLevel.NORMAL)
    void requestWithdrawalExampleAmount() {
        WithdrawalRequest request = new WithdrawalRequest(100);

        Response response = step("POST с суммой 100р", () ->
                withdrawalSteps.requestWithdrawal(authToken, request)
        );

        step("Проверка ответа", () -> {
            int statusCode = response.getStatusCode();

            if (statusCode == 201) {
                WithdrawalResponse withdrawalResponse = response.as(WithdrawalResponse.class);
                if (withdrawalResponse != null && withdrawalResponse.isSuccessful()) {
                    assertEquals(100, withdrawalResponse.getWithdrawalAmount(),
                            "Сумма в отввете должна быть 100");
                    Allure.addAttachment("Успех", "text/plain",
                            "✅ Запрос на вывод 100 ₽ выполнен успешно");
                }
            } else if (statusCode == 422) {

            }
        });
    }

    @Test
    @Order(2)
    @DisplayName("Запрос на вывод средств без авторизации")
    @Severity(SeverityLevel.CRITICAL)
    void requestWithdrawalWithdrawalAmountWithoutAuth() {
        WithdrawalRequest request = new WithdrawalRequest(100);

        Response response = step("Вывод средст без авторизаии", () ->
                withdrawalSteps.requestWithdrawal("", request)
        );

        step("Валидация ответа", () ->
                assertTrue(response.getStatusCode() == 401));
    }
}