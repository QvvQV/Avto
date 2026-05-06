package ru.tutor.steps.club;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.tutor.api.ClubApi;
import ru.tutor.config.TestConfig;
import ru.tutor.model.club.ClubMemberResponse;
import ru.tutor.model.club.ClubPaymentResponse;
import ru.tutor.model.club.ClubStatisticsResponse;
import ru.tutor.utils.AllureAttachments;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public record ClubSteps(ClubApi clubApi){

    public static ClubSteps  withBaseUrl(String baseUrl){
        return new ClubSteps(new ClubApi(baseUrl));
    }

    @Step("GET /user/club/statistics - получить статистику клуба")
    public Response getClubStatistics(String authToken) {
        Response response = this.clubApi.getClubStatistics(authToken);
        AllureAttachments.attachJson("Club Statics Response", response.getBody().asString());
        AllureAttachments.attachJson("Club Statistics Headers", response.getHeaders().toString());
        return response;
    }

    @Step("GET /user/club/member - получить информацию о членстве в клубе")
    public Response getClubMember(String authToken) {
        Response response = this.clubApi.getClubMembers(authToken);
        AllureAttachments.attachJson("Club Members", response.getBody().asString());
        AllureAttachments.attachJson("Club Members Headers", response.getHeaders().toString());
        return response;
    }

    @Step("Валидация ответа статистики клуба")
    public ClubStatisticsResponse validateClubStatisticsResponse(Response response) {
        response.then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .header("Access-Control-Allow-Origin", equalTo(TestConfig.getCurrentClientDomain()))
                .header("Access-Control-Allow-Credentials", equalTo("true"));

        response.then()
                .body("$", hasKey("balance"))
                .body("$", hasKey("referrals"))
                .body("referrals", hasKey("own"))
                .body("referrals", hasKey("all"));

        response.then()
                .body("balance", instanceOf(Integer.class))
                .body("referrals.own", instanceOf(Integer.class))
                .body("referrals.all", instanceOf(Integer.class));

        // Преобразуем в модель
        ClubStatisticsResponse statistics = response.as(ClubStatisticsResponse.class);
        assertNotNull(statistics, "Club statistics should not be null");

        return statistics;
    }

    @Step("Валидация ответа информации о членстве")
    public ClubMemberResponse validateClubMemberResponse(Response response) {
        response.then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .header("Access-Control-Allow-Origin", equalTo(TestConfig.getCurrentClientDomain()))
                .header("Access-Control-Allow-Credentials", equalTo("true"));

        response.then()
                .body("$", hasKey("id"))
                .body("$", hasKey("isActive"))
                .body("$", hasKey("memberType"))
                .body("$", hasKey("membershipDate"))
                .body("$", hasKey("referralLink"))
                .body("$", hasKey("details"));

        ClubMemberResponse member = response.as(ClubMemberResponse.class);
        assertNotNull(member, "Club member should not be null");

        return member;
    }

    @Step("Проверка знаний статистики клуба")
    public void validateClubStatisticsValues(ClubStatisticsResponse statistics) {
        assertAll("Club statistics values",
                () -> assertNotNull(statistics.getBalance(), "balance should not be null"),
                () -> assertNotNull(statistics.getReferrals(), "referrals object should not be null"),
                () -> assertNotNull(statistics.getReferrals().getOwn(), "referrals.own should not be null"),
                () -> assertNotNull(statistics.getReferrals().getAll(), "referrals.all should not be null"),
                () -> assertTrue(statistics.getBalance() >= 0, "balance should be >= 0"),
                () -> assertTrue(statistics.getReferrals().getOwn() >= 0, "referrals.own should be >= 0"),
                () -> assertTrue(statistics.getReferrals().getAll() >= 0, "referrals.all should be >= 0")
        );

        Integer own = statistics.getReferrals().getOwn();
        Integer all = statistics.getReferrals().getAll();
        if (own != null && all != null) {
            assertTrue(all >= own, "Total referrals should be >= own referrals");
        }
    }

    @Step("Проверка значений информации о членстве")
    public void validateClubMemberValues(ClubMemberResponse member) {
        assertAll("Club member values",
                () -> assertNotNull(member.getId(), "id should not be null"),
                () -> assertTrue(member.getId() > 0, "id should be positive"),
                () -> assertNotNull(member.getActive(), "isActive should not be null"),
                () -> assertNotNull(member.getMembershipDate(), "membershipDate should not be null"),
                () -> assertNotNull(member.getReferralLink(), "referralLink should not be null")
        );

        assertTrue(member.hasReferralLink(), "referralLink should not be empty");
        assertTrue(member.getReferralLink().length() >= 10, "referralLink should be at least 10 characters");

        assertTrue(member.isMembershipActive(), "membershipDate should be valid date");

        LocalDate membershipDate = member.getMembershipDateAsLocalDate();
        if (membershipDate != null) {
        assertFalse(membershipDate.isAfter(LocalDate.now()), "membershipDate should not be in the future");}
    }

    @Step("Проверка ответа без авторизации")
    public void validateUnauthorizedResponse(Response response) {
        int statusCode = response.getStatusCode();
        Allure.addAttachment("Unauthorized response", "text/plain",
                "Статус код: " + statusCode + "\n" +
                "Ожидается: 401 или 403");

        assertTrue(statusCode == 401 || statusCode == 403,
                "Should return 401 or 403 for missing/invalid token. Got: " + statusCode);
    }

    @Step("GET /user/club/payments - получить историю платежей клуба")
    public Response getClubPayments(String authToken){
        Response response = this.clubApi.getClubPayments(authToken);
        AllureAttachments.attachJson("Club Payments Response", response.getBody().asString());
        AllureAttachments.attachJson("Club Payments Headers", response.getHeaders().toString());
        return response;
    }

    @Step("Валидация успешного ответа истории платжей клуба")
    public List<ClubPaymentResponse> validateClubPaymentsResponse(Response response) {
        response.then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .header("Access-Control-Allow-Origin", equalTo(TestConfig.getCurrentClientDomain()))
                .header("Access-Control-Allow-Credentials", equalTo("true"));

        // Проверяем что ответ массив
        response.then()
                .body("$", instanceOf(List.class));

        // Преобразуем в список моделей
        List<ClubPaymentResponse> payments = response.jsonPath()
                .getList("$", ClubPaymentResponse.class);

        assertNotNull(payments, "Payments list should not be null");

        Allure.addAttachment("Количество платежей", "text/plain", String.valueOf(payments.size()));

        return payments;
    }

    @Step("Валидация структуры каждого платежа")
    public void validatePaymentsStructure(List<ClubPaymentResponse> payments) {
        for (int i = 0; i < payments.size(); i++) {
            ClubPaymentResponse payment = payments.get(i);
            String paymentInfo = "Платеж #" + (i + 1);

            assertAll(paymentInfo + " - проверка структуры",
                    () -> assertNotNull(payment.getName(), "name should not be null"),
                    () -> assertNotNull(payment.getDirection(), "direction should not be null"),
                    () -> assertTrue(
                            payment.isIncome() || payment.isOutcome(),
                            paymentInfo + " - direction should be 'income' or 'outcome'"
                    ),
                    () -> assertNotNull(payment.getAmount(), "amount should not be null"),
                    () -> assertTrue(payment.getAmount() > 0,
                            paymentInfo + " - amount should be positive"),
                    () -> assertNotNull(payment.getDate(), "date should not be null"),
                    () -> assertNotNull(payment.getDateAsLocalDateTime(),
                            paymentInfo + " - date should be valid ISO format")
            );

            // Дополнительные проверки для income/outcome
            if (payment.isIncome()) {
                assertAll(paymentInfo + " - income validation",
                        () -> assertTrue(payment.getAmount() > 0, "Income amount should be positive")
                );
            } else if (payment.isOutcome()) {
                assertAll(paymentInfo + " - outcome validation",
                        () -> assertTrue(payment.getAmount() > 0, "Outcome amount should be positive")
                );
            }
        }
    }

}
