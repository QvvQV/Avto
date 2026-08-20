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

    private final ClubSteps clubSteps = new ClubSteps(BASE_URL);
    private final ClubWithdrawalSteps withdrawalSteps = new ClubWithdrawalSteps(BASE_URL);
    private final AuthSteps userSteps = new AuthSteps(BASE_URL);

    private ClubMemberResponse memberInfo;
    private ClubStatisticsResponse statisticsInfo;
    private String token;
    private int userId;
//     = 47782

    @BeforeAll
    public static void setupAll() {
        Allure.label("component", "API");
        Allure.label("feature", "User Club");
    }

    @BeforeEach
    public void setup() {
        ru.tutor.model.UserProfile profile = step("Логин", () ->
                userSteps.loginAndGetUserProfile(TEST_EMAIL, TEST_PASSWORD)
        );
        token = userSteps.getCurrentAuthToken();
        userId = profile.getId();

    }

    @Test
    @Order(1)
    @DisplayName("Получение статистики клуба")
    @Story("Основной сценарий")
    @Severity(SeverityLevel.CRITICAL)
    void getClubStatisticsSuccess() {

        Response response = step("GET /user/club/statistics", () ->
                clubSteps.getClubStatistics(token)
        );

        statisticsInfo = step("Валидация ответа", () ->
                clubSteps.validateClubStatisticsResponse(response)
        );

        step("Проверка значений статистики", () -> {
            clubSteps.validateClubStatisticsValues(statisticsInfo);

            Allure.addAttachment("Статистика клуба", "text/plain",
                    "Баланс: " + statisticsInfo.balance() + "\n" +
                    "Собственные рефералы: " + statisticsInfo.referrals().own() + "\n" +
                    "Всего рефералов: " + statisticsInfo.referrals().all());
        });
    }

    @Test
    @Order(2)
    @DisplayName("Проверка информации о членстве в клубе")
    @Story("Валидация данных")
    @Severity(SeverityLevel.NORMAL)
    void verifySpecificValuesFromExample() {

        Response response = step("GET /user/club/member", () ->
                clubSteps.getClubMember(token)
        );

        memberInfo = step("Валидация ответа", () ->
                clubSteps.validateClubMemberResponse(response)
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

        step("Проверка значений членства", () -> {
            clubSteps.validateClubMemberValues(memberInfo);

            Allure.addAttachment("Информация о членстве", "text/plain",
                    "ID: " + memberInfo.id() + "\n" +
                            "Активен: " + memberInfo.isActive() + "\n" +
                            "Тип членства: " + memberInfo.memberType() + "\n" +
                            "Дата членства: " + memberInfo.membershipDate() + "\n" +
                            "Реферальная ссылка: " + memberInfo.referralLink() + "\n" +
                            "Детали: " + memberInfo.details());
        });
    }

    @Test
    @Order(3)
    @DisplayName("Проверка статистики без авторизации")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.CRITICAL)
    void getClubStatisticsWithoutAuth() {
        Response response = step("GET /user/club/statistics без токена", () ->
                clubSteps.getClubStatistics("invalid")
        );

        step("Проверка ошибки авторизации", () -> clubSteps.validateUnauthorizedResponse(response));
    }

    @Test
    @Order(4)
    @DisplayName("Проверка информации о членстве без авторизации")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.CRITICAL)
    void getClubMemberWithoutAuth() {
        Response response = step("GET /user/club/member без токена", () ->
                clubSteps.getClubMember("invalid")
        );

        step("Проверка ошибки авторизации", () ->
            clubSteps.validateUnauthorizedResponse(response)
        );
    }

//    @Test
    @Order(5)
    @DisplayName("Запрос на вывод 3000р")
    @Story("Валидация спецификации")
    @Severity(SeverityLevel.NORMAL)
    void requestWithdrawalExampleAmount() {
        WithdrawalRequest request = new WithdrawalRequest(3000);

        Response response = step("POST с суммой 3000р", () ->
                withdrawalSteps.requestWithdrawal(token, request)
        );

        step("Проверка ответа", () -> {
            int statusCode = response.getStatusCode();

            String responseBody = response.getBody().asString();

            if (statusCode == 201) {
                assertTrue(responseBody.equals("[]") || responseBody.equals("[\n\n]"),
                        "Тело ответа при успехе должно быть пустым массивом");
                Allure.addAttachment("Успех", "text/plain",
                        "✅ Запрос на вывод 3000 ₽ выполнен успешно");
            } else if (statusCode == 400) {
                // Недостаточно средств
                String balanceStr = responseBody.replaceAll("[^0-9]", "");
                assertAll("Проверка ошибки недостаточно средств",
                        () -> assertTrue(responseBody.contains("Not enough credits"),
                                "Сообщение должно содержать 'Not enough credits'"),
                        () -> assertTrue(!balanceStr.isEmpty() && Integer.parseInt(balanceStr) >= 0,
                                "Сообщение должно содержать число (доступный баланс)")
                );
            } else {
                // Неожиданный статус код
                Allure.addAttachment("Неожиданный ответ", "text/plain",
                        String.format("⚠ Получен неожиданный статус код: %d\nТело ответа: %s",
                                statusCode, responseBody));
                fail(String.format("Неожиданный статус код: %d. Ожидался 201 или 400", statusCode));
            }
        });
    }

    @Test
    @Order(6)
    @DisplayName("Успешное получение списка реферралов")
    @Story("Основной сценарий")
    @Tag("positive")
    void getReferralsSuccess() {
        Response response = step("Список реферралдов пользователя", () ->
                clubSteps.getUserReferrals(token, userId)
        );
        step("Проверка ответа", () ->
               clubSteps.validateUserReferralsResponse(response)
        );
    }

    @Test
    @Order(7)
    @DisplayName("Получение списка реферралов - без авторизации")
    @Story("Негативный сценарий")
    @Tag("negative")
    void getReferralsWithoutAuth() {
        Response response = step("Список реферралов без токена", () ->
                clubSteps.getUserReferrals("", userId)
        );
        step("Проверка статуса ответа", () ->
                assertEquals(401,  response.getStatusCode()));
    }

    @Test
    @Order(8)
    @DisplayName("Получение списка реферралов с невалидным токеном")
    @Story("Негативный сценарий")
    @Tag("negative")
    void getReferralsInvalidToken() {
        Response response = step("Список реферралов с невалидным токеном", () ->
                clubSteps.getUserReferrals("invalid", userId)
        );
        step("Проверка статуса ответа", () ->
                assertEquals(401,  response.getStatusCode()));
    }

    @Test
    @Order(9)
    @DisplayName("GET /user/{userId}/club/referrals/{referralId} - детали реферала")
    @Severity(SeverityLevel.NORMAL)
    @Story("Основной сценарий")
    @Tag("positive")
    void getReferralDetailSuccess() {
        Response referralsResponse = step("GET /user/" +  userId + "/club/referrals", () ->
                clubSteps.getUserReferrals(token, userId)
        );
        clubSteps.validateUserReferralsResponse(referralsResponse);
        Integer referralId = step("Извлечение id реферала", () ->
                clubSteps.extractReferralId(referralsResponse)
        );
        Response detailResponse = step("GET /user/" + userId + "/club/referrals/" + referralId, () ->
                clubSteps.getUserReferralById(token, userId, referralId)
        );
        step("Валидация ответа", () ->
                clubSteps.validateUserReferralsResponse(detailResponse)
        );
    }

    @Test
    @Order(10)
    @DisplayName("GET /user/{userId}/club/referrals/{referralId} - детали реферала без авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Story("Негативный сценарий")
    @Tag("negative")
    void getReferralWithoutAuth() {
        Response response = step("GET /user/" + userId + "/club/referrals - без токена", () ->
                clubSteps.getUserReferralById("", userId, 0)
        );
        step("Проверка ошибки авторизации", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(11)
    @DisplayName("GET /user/{userId}/club/referrals/{referralId} - с невалидным токеном")
    @Severity(SeverityLevel.NORMAL)
    @Story("Негативный сценарий")
    @Tag("negative")
    void getReferralInvalidToken() {
        Response response = step("GET /user/" + userId + "/club/referrals - невалидный токен", () ->
                clubSteps.getUserReferralById("invalid", userId, 0)
        );
        step("Проверк ошибки", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(12)
    @DisplayName("GET /user/{userId}/club/referrals/{referralId}/referrals - список реферала")
    @Severity(SeverityLevel.NORMAL)
    @Story("Основной сценарий")
    @Tag("positive")
    void getReferralReferralsSuccess() {
        Response referralsResponse = step("GET /user/" +  userId + "/club/referrals", () ->
                clubSteps.getUserReferrals(token, userId)
        );
        clubSteps.validateUserReferralsResponse(referralsResponse);
        Integer referralId = step("Извлечение id реферала", () ->
                clubSteps.extractReferralId(referralsResponse)
        );
        Response detailResponse = step("GET /user/" + userId + "/club/referrals/" + referralId, () ->
                clubSteps.getUserReferralById(token, userId, referralId)
        );
        step("Валидация ответа", () ->
                clubSteps.validateUserReferralsResponse(detailResponse)
        );

        Response nestedResponse = step ("GET /user/" + userId + "/club/referrals/" + referralId + "/referrals", () ->
                clubSteps.getUserReferralsReferrals(token, userId, referralId)
        );
        step("Валидация ответа", () ->
                clubSteps.validateUserReferralsResponse(nestedResponse)
        );
    }

    @Test
    @Order(13)
    @DisplayName("GET /user/{userId}/club/referrals/{referralId}/referrals - без авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Story("Негативный сценарий")
    @Tag("negative")
    void getReferralReferralsWithoutAuth() {
        Response response = step("GET /user/" + userId + "/club/referrals - без токена", () ->
                clubSteps.getUserReferralById("", userId, 0)
        );
        step("Проверка ошибки авторизации", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(14)
    @DisplayName("GET /user/{userId}/club/referrals/{referralId}/referrals  - с невалидным токеном")
    @Severity(SeverityLevel.NORMAL)
    @Story("Негативный сценарий")
    @Tag("negative")
    void getReferralReferralsInvalidToken() {
        Response response = step("GET /user/" + userId + "/club/referrals - невалидный токен", () ->
                clubSteps.getUserReferralById("invalid", userId, 0)
        );
        step("Проверк ошибки", () ->
                assertEquals(401, response.getStatusCode()));
    }
}