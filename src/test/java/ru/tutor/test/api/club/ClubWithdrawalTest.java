package ru.tutor.test.api.club;


import io.qameta.allure.*;
import io.qameta.allure.junit5.AllureJunit5;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tutor.config.TestConfig;
import ru.tutor.model.*;
import ru.tutor.steps.AuthSteps;
import ru.tutor.steps.club.ClubWithdrawalSteps;
import ru.tutor.steps.UserSteps;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AllureJunit5.class)
@Epic("API Тесты пользователя")
@Feature("Запросы на вывод средств")
@DisplayName("Тесты эндпоинта Клуба: POST /user/club/request-withdrawal")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("club")
public class ClubWithdrawalTest {

    private final AuthSteps authSteps = new AuthSteps(TestConfig.getApiBaseUrl());
    private final ClubWithdrawalSteps withdrawalSteps = new ClubWithdrawalSteps(TestConfig.getApiBaseUrl());
    private UserSteps userSteps;
    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();

    private static String authToken;
    private static Integer currentUserId;
    private static SzDetailsRequest testRequest;
    private static Integer testUserId = 42225;
    private static Integer lastWithdrawalId;
    private static Integer savedSzDetailsId;

    @BeforeEach
    public void setup() {
        authToken = step("Логин", () ->
                authSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );

        // Получаем реальный ID пользователя из профиля
//       step("Получение ID пользователя", () -> {
//             Response profileResponse = userSteps.getProfile(authToken);
//             currentUserId = profileResponse.jsonPath().getInt("id");
//             Allure.addAttachment("ID пользователя", "text/plain",
//                     "Текущий userId: " + currentUserId);
//        });
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
}