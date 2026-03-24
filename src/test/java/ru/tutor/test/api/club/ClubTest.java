package ru.tutor.test.api.club;


import io.qameta.allure.*;
import io.qameta.allure.junit5.AllureJunit5;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tutor.config.TestConfig;
import ru.tutor.model.club.ClubMemberResponse;
import ru.tutor.model.club.ClubStatisticsResponse;
import ru.tutor.model.club.WithdrawalRequest;
import ru.tutor.model.club.WithdrawalResponse;
import ru.tutor.steps.AuthSteps;
import ru.tutor.steps.club.ClubSteps;
import ru.tutor.steps.club.ClubWithdrawalSteps;

import static io.qameta.allure.Allure.step;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AllureJunit5.class)
@Epic("API Тесты пользователя")
@Feature("Клуб пользователя")
@DisplayName("Тесты эндпоинтов клуба")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClubTest {

    // Общие константы
    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();

    private final ClubSteps clubSteps = ClubSteps.withBaseUrl(BASE_URL);
    private final ClubWithdrawalSteps withdrawalSteps = ClubWithdrawalSteps.withBaseUrl(BASE_URL);
    private final AuthSteps authSteps = AuthSteps.withBaseUrl(BASE_URL);

    private static ClubMemberResponse memberInfo;
    private static ClubStatisticsResponse statisticsInfo;
    private String authToken;

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
    @DisplayName("Получение статистики клуба")
    @Story("Основной сценарий")
    @Severity(SeverityLevel.CRITICAL)
    void getClubStatisticsSuccess() {

        Response response = step("GET /user/club/statistics", () ->
                clubSteps.getClubStatictics(authToken)
        );

        statisticsInfo = step("Валидация ответа", () ->
                clubSteps.validateClubStatisticsResponse(response)
        );

        step("Проверка значений статистики", () -> {
            clubSteps.validateClubStatisticsValues(statisticsInfo);

            Allure.addAttachment("Статистика клуба", "text/plain",
                    "Баланс: " + statisticsInfo.getBalance() + "\n" +
                    "Собственные рефералы: " + statisticsInfo.getReferrals().getOwn() + "\n" +
                    "Всего рефералов: " + statisticsInfo.getReferrals().getAll());
        });
    }

    @Test
    @Order(2)
    @DisplayName("Получение информации о членстве в клубе")
    @Story("Основной сценарий")
    @Severity(SeverityLevel.CRITICAL)
    void getClubMemberSuccess() {

        Response response = step("GET /user/club/member", () ->
                clubSteps.getClubMember(authToken)
        );

        memberInfo = step("Валидация ответа", () ->
                clubSteps.validateClubMemberResponse(response)
        );

        step("Проверка значений членства", () -> {
            clubSteps.validateClubMemberValues(memberInfo);

            Allure.addAttachment("Информация о членстве", "text/plain",
                    "ID: " + memberInfo.getId() + "\n" +
                    "Активен: " + memberInfo.getActive() + "\n" +
                    "Тип членства: " + memberInfo.getMemberType() + "\n" +
                    "Дата членства: " + memberInfo.getMembershipDate() + "\n" +
                    "Реферальная ссылка: " + memberInfo.getReferralLink() + "\n" +
                    "Детали: " + memberInfo.getDetails());
        });
    }

    @Test
    @Order(3)
    @DisplayName("Проверка конкретных значений")
    @Story("Валидация данных")
    @Severity(SeverityLevel.NORMAL)
    void verifySpecificValuesFromExample() {

        Response response = step("GET /user/club/member", () ->
                clubSteps.getClubMember(authToken)
        );

        step("Проверка членства", () -> {
            response.then()
                    .body("id", notNullValue())
                    .body("isActive",  equalTo(true))
                    .body("memberType", nullValue())
                    .body("membershipDate", notNullValue())
                    .body("referralLink", notNullValue())
                    .body("details", nullValue());
        });
    }

    @Test
    @Order(4)
    @DisplayName("Проверка статистики без авторизации")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.CRITICAL)
    void getClubStatisticsWithoutAuth() {
        Response response = step("GET /user/club/statistics без токена", () ->
                clubSteps.getClubStatictics("invalid")
        );

        step("Проверка ошибки авторизации", () -> clubSteps.validateUnauthorizedResponse(response));
    }

    @Test
    @Order(5)
    @DisplayName("Проверка информации о членстве без авторизации")
    @Story("негативный сценарий")
    @Severity(SeverityLevel.CRITICAL)
    void getClubMemberWithoutAuth() {
        Response response = step("GET /user/club/member без токена", () ->
                clubSteps.getClubMember("invalid")
        );

        step("Проверка ошибки авторизации", () ->
            clubSteps.validateUnauthorizedResponse(response)
        );
    }

    @Test
    @Order(6)
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

            String responseBody = response.getBody().asString();

            if (statusCode == 201) {
                WithdrawalResponse withdrawalResponse = response.as(WithdrawalResponse.class);
                if (withdrawalResponse != null && withdrawalResponse.isSuccessful()) {
                    assertEquals(3000, withdrawalResponse.getWithdrawalAmount(),
                            "Сумма в ответе должна быть 3000");
                    Allure.addAttachment("Успех", "text/plain",
                            "✅ Запрос на вывод 3000 ₽ выполнен успешно");
                }
            } else if (statusCode == 400) {
                // Недостаточно средств
                assertAll("Проверка ошибки недостаточно средств",
                        () -> assertTrue(responseBody.contains("Not enough credits"),
                                "Сообщение должно содержать 'Not enough credits'"),
                        () -> assertTrue(responseBody.contains("1000"),
                                "Сообщение должно содержать доступный баланс")
                );
            } else {
                // Неожиданный статус код
                Allure.addAttachment("Неожиданный ответ", "text/plain",
                        String.format("⚠ Получен неожиданный статус код: %d\nТело ответа: %s",
                                statusCode, responseBody));
                fail(String.format("Неожиданный статус код: %d. Ожидался 201, 400", statusCode));
            }
        });
    }
}