package ru.tutor.steps.club;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.tutor.api.ClubApi;
import ru.tutor.config.TestConfig;
import ru.tutor.model.ClubMemberResponse;
import ru.tutor.model.ClubStatisticsResponse;
import ru.tutor.utils.AllureAttachments;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public record ClubSteps(ClubApi clubApi){

    public static ClubSteps  withBaseUrl(String baseUrl){
        return new ClubSteps(new ClubApi(baseUrl));
    }

    @Step("GET /user/club/statistics - получить статистику клуба")
    public Response getClubStatictics(String authToken) {
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
}
