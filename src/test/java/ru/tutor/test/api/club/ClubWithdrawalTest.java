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

    private final ClubWithdrawalSteps withdrawalSteps = new ClubWithdrawalSteps(TestConfig.getCurrentApiUrl());
    private final AuthSteps userSteps = new AuthSteps(TestConfig.getCurrentApiUrl());
    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();

    private static String authToken;

    @BeforeEach
    public void setup() {
        authToken = step("Логин", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );
    }

    @Test
    @Order(1)
    @DisplayName("Запрос на вывод 3000р")
    @Story("Валидация спецификации")
    @Severity(SeverityLevel.NORMAL)
    void requestWithdrawalExampleAmount() {
        WithdrawalRequest request = new WithdrawalRequest(3000);

        Response response = step("POST с суммой 3000р", () ->
                withdrawalSteps.requestWithdrawal(authToken, request)
        );

        step("Проверка ответа", () -> {
            int statusCode = response.getStatusCode();

            if (statusCode == 201) {
                WithdrawalResponse withdrawalResponse = response.as(WithdrawalResponse.class);
                if (withdrawalResponse != null && withdrawalResponse.isSuccessful()) {
                    assertEquals(3000, withdrawalResponse.getWithdrawalAmount(),
                            "Сумма в ответе должна быть 3000");
                    Allure.addAttachment("Успех", "text/plain",
                            "✅ Запрос на вывод 3000 ₽ выполнен успешно");
                }
            } else if (statusCode == 422) {
                Allure.addAttachment("Недостаточно средств для вывода", "text/plain");
            } else  if (statusCode == 400) {
                String error = response.jsonPath().getString("$");
                assertEquals("[Withdrawal details not found]", error);
                Allure.addAttachment("Реквизиты не заполнены",
                        "❌ Не заполнены реквизиты для вывода средств " + error);
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