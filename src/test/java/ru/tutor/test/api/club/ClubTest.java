package ru.tutor.test.api.club;


import io.qameta.allure.*;
import io.qameta.allure.junit5.AllureJunit5;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tutor.config.TestConfig;
import ru.tutor.model.ClubMemberResponse;
import ru.tutor.model.ClubStatisticsResponse;
import ru.tutor.steps.AuthSteps;
import ru.tutor.steps.club.ClubSteps;

import static io.qameta.allure.Allure.step;
import static org.hamcrest.Matchers.*;

@ExtendWith(AllureJunit5.class)
@Epic("API Тесты пользователя")
@Feature("Клуб пользователя")
@DisplayName("Тесты эндпоинтов клуба")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClubTest {

    private final AuthSteps authSteps = AuthSteps.withBaseUrl(TestConfig.getCurrentApiUrl());
    private final ClubSteps clubSteps = ClubSteps.withBaseUrl(TestConfig.getCurrentApiUrl());
    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();

    private static ClubMemberResponse memberInfo;
    private static ClubStatisticsResponse statisticsInfo;

    @BeforeAll
    public static void setupAll() {
        Allure.label("component", "API");
        Allure.label("feature", "User Club");
    }

    @Test
    @Order(1)
    @DisplayName("Получение статистики клуба")
    @Story("Основной сценарий")
    @Severity(SeverityLevel.CRITICAL)
    void getClubStatisticsSuccess() {
        String token = step("Логин", () ->
                authSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );

        Response response = step("GET /user/club/statistics", () ->
                clubSteps.getClubStatictics(token)
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
        String token = step("Логин", () ->
                authSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );

        Response response = step("GET /user/club/member", () ->
                clubSteps.getClubMember(token)
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
    @DisplayName("Проверка конкретных значений из примера")
    @Story("Валидация данных")
    @Severity(SeverityLevel.NORMAL)
    void verifySpecificValuesFromExample() {

//        Response memberResponse = clubSteps.getClubMember(token);
        String token = step("Логин", () ->
                authSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );

        Response response = step("GET /user/club/member", () ->
                clubSteps.getClubMember(token)
        );


        step("Проверка членства из примера", () -> {
            response.then()
                    .body("id", equalTo(3338))
                    .body("isActive",  equalTo(true))
                    .body("memberType", nullValue())
                    .body("membershipDate", equalTo("2025-10-17"))
                    .body("referralLink", equalTo("68f1fe239caf76.05384387"))
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
}
