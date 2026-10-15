package ru.tutor.steps.club;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.model.club.*;
import ru.tutor.utils.AllureAttachments;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ClubPayoutsSteps extends ClubBaseSteps {

    public ClubPayoutsSteps(String baseUrl) {
        super(baseUrl);
    }

    @Step("GET /user/club/v2/payouts/summary - сводка выплат клуба")
    public Response getPayoutSummary(String authToken) {
        Response response = clubApi.getPayoutSummary(authToken);
        AllureAttachments.attachJson("Club Payout Summary Response", response.getBody().asString());
        AllureAttachments.attachJson("Club Payout Summary Headers", response.getHeaders().toString());
        return response;
    }

    @Step("Валидация ответа сводки выплат клуба")
    public ClubPayoutSummaryResponse validateClubPayoutSummaryResponse(Response response) {
        assertBasicResponse(response);

        assertJsonFieldExists(response, "balance");
        assertJsonFieldExists(response, "availableForWithdrawal");
        assertJsonFieldExists(response, "shareBonusExcludedAmount");
        assertJsonFieldExists(response, "minWithdrawalAmount");
        assertJsonKeyExists(response, "clubMembership");
        assertJsonKeyExists(response, "profileValid");
        assertJsonKeyExists(response, "canRequestWithdrawal");

        ClubPayoutSummaryResponse summary = response.as(ClubPayoutSummaryResponse.class);
        assertNotNull(summary, "Сводка выплат клуба не должна быть null");

        return summary;
    }

    @Step("Проверка значений сводки выплат клуба (ожидаемый баланс)")
    public void validateClubPayoutSummaryValues(ClubPayoutSummaryResponse summary, int expectedBalance, int expectedAvailable,
                                                boolean expectProfileValid, boolean expectCanRequestWithdrawal) {
        validateClubPayoutSummaryValues(summary, expectedBalance, expectedAvailable,
                expectProfileValid, expectCanRequestWithdrawal, "full");
    }

    @Step("Проверка значений сводки выплат клуба (членство {expectedClubMembership})")
    public void validateClubPayoutSummaryValues(ClubPayoutSummaryResponse summary, int expectedBalance, int expectedAvailable,
                                                boolean expectProfileValid, boolean expectCanRequestWithdrawal,
                                                String expectedClubMembership) {
        assertAll("Проверка значений сводки выплат клуба",
                () -> assertEquals(expectedBalance, summary.balance(),
                        "balance должен быть " + expectedBalance),
                () -> assertEquals(expectedAvailable, summary.availableForWithdrawal(),
                        "availableForWithdrawal должен быть " + expectedAvailable),
                () -> assertEquals(0, summary.shareBonusExcludedAmount(),
                        "shareBonusExcludedAmount должен быть 0"),
                () -> assertEquals(3000, summary.minWithdrawalAmount(),
                        "minWithdrawalAmount должен быть 3000"),
                () -> assertEquals(expectedClubMembership, summary.clubMembership(),
                        "clubMembership должен быть '" + expectedClubMembership + "'"),
                () -> assertEquals(expectProfileValid, Boolean.TRUE.equals(summary.profileValid()),
                        "profileValid должен быть " + expectProfileValid),
                () -> assertEquals(expectCanRequestWithdrawal, Boolean.TRUE.equals(summary.canRequestWithdrawal()),
                        "canRequestWithdrawal должен быть " + expectCanRequestWithdrawal)
        );
    }

    @Step("Проверка сводка выплат (GET /user/club/v2/payouts/summary)")
    public ClubPayoutSummaryResponse getAndValidatePayoutSummary(String authToken, int expectedBalance, int expectedAvailable,
                                                                 boolean expectProfileValid, boolean expectCanRequestWithdrawal) {
        ClubPayoutSummaryResponse summary = validateClubPayoutSummaryResponse(getPayoutSummary(authToken));
        validateClubPayoutSummaryValues(summary, expectedBalance, expectedAvailable, expectProfileValid, expectCanRequestWithdrawal);
        return summary;
    }

    @Step("Проверка сводки выплат (членство semi) (GET /user/club/v2/payouts/summary)")
    public ClubPayoutSummaryResponse getAndValidatePayoutSummarySemi(String authToken, int expectedBalance, int expectedAvailable,
                                                                     boolean expectProfileValid, boolean expectCanRequestWithdrawal) {
        ClubPayoutSummaryResponse summary = validateClubPayoutSummaryResponse(getPayoutSummary(authToken));
        validateClubPayoutSummaryValues(summary, expectedBalance, expectedAvailable,
                expectProfileValid, expectCanRequestWithdrawal, "semi");
        return summary;
    }

    @Step("POST /user/club/v2/payouts - создание заявки на вывод")
    public Response createPayout(String authToken, int amount) {
        Map<String, Object> payload = Map.of("amount", amount);
        Response response = clubApi.createPayout(authToken, payload);
        AllureAttachments.attachJson("Create Payout Response", response.getBody().asString());
        AllureAttachments.attachJson("Create Payout Headers", response.getHeaders().toString());
        AllureAttachments.attachJson("Create Payout Request", payload.toString());
        return response;
    }

    @Step("Валидация отказа в создании заяки на вывод (canRequestWithdrawal=false)")
    public void validatePayoutRejected(Response response) {
        assertEquals(500, response.getStatusCode(), "Ожидался статус 500 при canRequestWithdrawal=false, получен: " + response.getStatusCode());
        Allure.addAttachment("Payout rejected", "text/plain", response.getBody().asString());
    }

    @Step("Валидация ответа создания заявки на вывод (201)")
    public ClubWithdrawalResponse validatePayoutCreated(Response response) {
        assertStatusAnyOf(response, 201);

        ClubWithdrawalResponse withdrawal = response.as(ClubWithdrawalResponse.class);
        assertNotNull(withdrawal, "Заявка на вывод не должна быть null");
        assertAll("Проверка заявки на вывод",
                () -> assertNotNull(withdrawal.id(), "id не должен быть null"),
                () -> assertTrue(withdrawal.id() > 0, " id должен бы ть положительным"),
                () -> assertEquals("#WD-" + withdrawal.id(), withdrawal.number(),
                        "number должен быть '#WD-' + id"),
                () -> assertEquals(3000, withdrawal.amount(), "amount должен быть 3000"),
                () -> assertNotNull(withdrawal.createdAt(), "createdAt не должен быть null"),
                () -> assertEquals("new", withdrawal.status().code(), "status.code должен быть 'new'"),
                () -> assertEquals("Новая", withdrawal.status().label(), "status.label должен быть 'Новая'"),
                () -> assertEquals("bank_account_ip", withdrawal.method().code(),
                        "method.code должен быть 'bank_account_ip'"),
                () -> assertEquals("Банковский счет (ИП)", withdrawal.method().label(),
                        "method.label должен быть 'Банковский счет (ИП)'")
        );
        return withdrawal;
    }

    @Step("GET /user/club/v2/payouts - список заявок на вывод")
    public Response getPayouts(String authToken) {
        Response response = clubApi.getClubPayouts(authToken);
        AllureAttachments.attachJson("Club Payout Response", response.getBody().asString());
        AllureAttachments.attachJson("Club Payout Headers", response.getHeaders().toString());
        return response;
    }

    @Step("Валидация ответа списка заявок на вывод")
    public ClubPayoutsResponse validatePayoutsResponse(Response response) {
        assertBasicResponse(response);

        ClubPayoutsResponse payouts = response.as(ClubPayoutsResponse.class);
        assertNotNull(payouts, "Ответ списка заявок не должен быть null");
        assertNotNull(payouts.items(), "Поле 'items' должно присутствовать в ответе");

        Allure.addAttachment("Количество заявок",  "text/plain",
                String.valueOf(payouts.safeItems().size()));
        return payouts;
    }

    @Step("Наличие заявки в списке (GET /user/club/v2/payouts)")
    public void validatePayoutPresent(ClubPayoutsResponse payouts, ClubWithdrawalResponse created) {
        assertNotNull(created, "Созданная заявка не должна быть null");
        assertNotNull(payouts, "Ответ списка заявок не должен быть null");

        List<ClubWithdrawalResponse> items = payouts.safeItems();
        Allure.addAttachment("Заявок в списке", "text/plain", String.valueOf(items.size()));

        ClubWithdrawalResponse found = items.stream().filter(item -> created.id() != null && created.id().equals(item.id())).findFirst().orElseThrow(() -> new AssertionError(
                "Созданная заявка id=" + created.id() + " не найдена в GET /user/club/v2/payouts (page=1, per_page=20)"
        ));

        assertAll("Сверка созданной заявки со списком",
                () -> assertEquals(created.number(), found.number(), "number должен совпадать"),
                () -> assertEquals(created.amount(), found.amount(), "amount должен совпадать"),
                () -> assertEquals(created.status().code(), found.status().code(), "status.code должен совпадать"),
                () -> assertEquals(created.method().code(), found.method().code(), "method.code должен совпадать"));
    }
}
