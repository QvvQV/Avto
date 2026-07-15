package ru.tutor.steps.club;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.api.club.ClubApi;
import ru.tutor.base.BaseApiSteps;
import ru.tutor.model.club.ClubMemberResponse;
import ru.tutor.model.club.ClubPaymentResponse;
import ru.tutor.model.club.ClubStatisticsResponse;
import ru.tutor.utils.AllureAttachments;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ClubSteps extends BaseApiSteps {

    private final ClubApi clubApi;

    public ClubSteps(String baseUrl){
        super(baseUrl);
        this.clubApi = new ClubApi(baseUrl);
    }

    public static ClubSteps withBaseUrl(String baseUrl){
        return new ClubSteps(baseUrl);
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
        assertSuccessStatusCode(response);
        assertContentTypeJson(response);
        assertCorsHeaders(response);

        assertJsonFieldExists(response, "balance");
        assertJsonFieldExists(response, "referrals");

        // Преобразуем в модель
        ClubStatisticsResponse statistics = response.as(ClubStatisticsResponse.class);
        assertNotNull(statistics, "Статистика клуба не должна быть null");

        return statistics;
    }

    @Step("Валидация ответа информации о членстве")
    public ClubMemberResponse validateClubMemberResponse(Response response) {
        assertSuccessStatusCode(response);
        assertContentTypeJson(response);
        assertCorsHeaders(response);

        assertJsonFieldExists(response, "id");
        assertJsonFieldExists(response, "isActive");
        assertJsonKeyExists(response, "memberType");
        assertJsonFieldExists(response, "membershipDate");
        assertJsonFieldExists(response, "referralLink");
        assertJsonKeyExists(response, "details");

        ClubMemberResponse member = response.as(ClubMemberResponse.class);
        assertNotNull(member, "Участник клуба не должен быть null");

        return member;
    }

    @Step("Проверка знаний статистики клуба")
    public void validateClubStatisticsValues(ClubStatisticsResponse statistics) {
        assertAll("Проверка значений статистики клуба",
                () -> assertNotNull(statistics.balance(), "balance не должен быть null"),
                () -> assertNotNull(statistics.referrals(), "referrals не должен быть null"),
                () -> assertNotNull(statistics.referrals().own(), "referrals.own не должен быть null"),
                () -> assertNotNull(statistics.referrals().all(), "referrals.all не должен быть null"),
                () -> assertTrue(statistics.balance() >= 0, "balance должен быть >= 0"),
                () -> assertTrue(statistics.referrals().own() >= 0, "referrals.own должен быть >= 0"),
                () -> assertTrue(statistics.referrals().all() >= 0, "referrals.all должен быть >= 0")
        );

        Integer own = statistics.referrals().own();
        Integer all = statistics.referrals().all();
        if (own != null && all != null) {
            assertTrue(all >= own, "Всего рефералов должно быть >= собственных рефералов");
        }
    }

    @Step("Проверка значений информации о членстве")
    public void validateClubMemberValues(ClubMemberResponse member) {
        assertAll("Проверка значений участника клуба",
                () -> assertNotNull(member.id(), "id не должен быть null"),
                () -> assertTrue(member.id() > 0, "id должен быть положительным"),
                () -> assertNotNull(member.isActive(), "isActive не должен быть null"),
                () -> assertNotNull(member.membershipDate(), "membershipDate не должен быть null"),
                () -> assertNotNull(member.referralLink(), "referralLink не должен быть null")
        );

        assertTrue(member.hasReferralLink(), "referralLink не должен быть пустым");
        assertTrue(member.referralLink().length() >= 10, "referralLink должен быть минимум 10 символов");

        assertTrue(member.isMembershipActive(), "membershipDate должен быть валидной датой");

        LocalDate membershipDate = member.getMembershipDateAsLocalDate();
        if (membershipDate != null) {
        assertFalse(membershipDate.isAfter(LocalDate.now()), "membershipDate не должен быть в будущем");}
    }

    @Step("Проверка ответа без авторизации")
    public void validateUnauthorizedResponse(Response response) {
        int statusCode = response.getStatusCode();
        Allure.addAttachment("Unauthorized response", "text/plain",
                "Статус код: " + statusCode + "\n" +
                "Ожидается: 401 или 403");

        assertTrue(statusCode == 401 || statusCode == 403,
                "Должен вернуть 401 или 403 для отсутствующего/невалидного токена. Получен: " + statusCode);
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
        assertSuccessStatusCode(response);
        assertContentTypeJson(response);
        assertCorsHeaders(response);

        // Проверяем что ответ массив
        Object body = response.jsonPath().get("$");
        assertNotNull(body, "Тело ответа не должно быть null");
        assertTrue(body instanceof List, "Ответ должен быть списком (List)");

        // Преобразуем в список моделей
        List<ClubPaymentResponse> payments = response.jsonPath()
                .getList("$", ClubPaymentResponse.class);

        assertNotNull(payments, "Список платежей не должен быть null");

        Allure.addAttachment("Количество платежей", "text/plain", String.valueOf(payments.size()));

        return payments;
    }

    @Step("Валидация структуры каждого платежа")
    public void validatePaymentsStructure(List<ClubPaymentResponse> payments) {
        for (int i = 0; i < payments.size(); i++) {
            ClubPaymentResponse payment = payments.get(i);
            String paymentInfo = "Платеж #" + (i + 1);

            assertAll(paymentInfo + " - проверка структуры",
                    () -> assertNotNull(payment.name(), "name не должен быть null"),
                    () -> assertNotNull(payment.direction(), "direction не должен быть null"),
                    () -> assertTrue(
                            payment.isIncome() || payment.isOutcome(),
                            paymentInfo + " - direction должен быть 'income' или 'outcome'"
                    ),
                    () -> assertNotNull(payment.amount(), "amount не должен быть null"),
                    () -> assertTrue(payment.amount() > 0,
                            paymentInfo + " - amount должен быть положительным"),
                    () -> assertNotNull(payment.date(), "date не должен быть null"),
                    () -> assertNotNull(payment.getDateAsLocalDateTime(),
                            paymentInfo + " - date должен быть валидным ISO форматом")
            );

            // Дополнительные проверки для income/outcome
            if (payment.isIncome()) {
                assertAll(paymentInfo + " - проверка дохода",
                        () -> assertTrue(payment.amount() > 0, "Сумма дохода должна быть положительной")
                );
            } else if (payment.isOutcome()) {
                assertAll(paymentInfo + " - проверка расхода",
                        () -> assertTrue(payment.amount() > 0, "Сумма расхода должна быть положительной")
                );
            }
        }
    }

    @Step("GET /user/{userId}/club/referrals - список рефералов")
    public Response getUserReferrals(String authToken, int userId) {
        Response response = this.clubApi.getUserReferrals(authToken, userId);
        AllureAttachments.attachJson("User Referrals Response", response.getBody().asString());
        return response;
    }

    @Step("Валидация ответа списка рефералов")
    public void validateUserReferralsResponse(Response response) {
        assertSuccessStatusCode(response);
        assertContentTypeJson(response);
        assertCorsHeaders(response);
    }

    @Step("GET /user/{userId}/club/referrals/{referralId} - детали реферала")
    public Response getUserReferralById(String authToken, int userId, int referralId) {
        Response response = this.clubApi.getUserReferralById(authToken, userId, referralId);
        AllureAttachments.attachJson("Club Referral Detail Response", response.getBody().asString());
        return response;
    }

    @Step("Извлечение id первого реферала из списка")
    public Integer extractReferralId(Response referralsResponse) {
        Integer referralId = referralsResponse.jsonPath().getInt("[0].userId");
        assertNotNull(referralId, "id реферала не должен быть null");
        Allure.addAttachment("Extracted referralId",  String.valueOf(referralId));
        return referralId;
    }

    @Step("GET /user/{userId}/club/referrals/{referralId}/referrals")
    public Response getUserReferralsReferrals(String authToken, int userId, int referralId) {
        Response response = this.clubApi.getUserReferralRefferrals(authToken, userId, referralId);
        AllureAttachments.attachJson("Club Referral Detail Response", response.getBody().asString());
        return response;
    }

}
