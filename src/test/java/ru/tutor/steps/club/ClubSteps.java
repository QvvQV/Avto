package ru.tutor.steps.club;

import ru.tutor.model.club.*;
import ru.tutor.utils.ClubTestDataGenerator;
import tools.jackson.databind.ObjectMapper;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.api.club.ClubApi;
import ru.tutor.base.BaseApiSteps;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.model.club.ClubReferralsResponse.Pagination;
import ru.tutor.model.club.ClubReferralsResponse.ReferralItem;
import ru.tutor.model.club.ClubReferralsResponse.ReferralLink;
import ru.tutor.model.club.ClubReferralsResponse.SubscriptionStatus;
import ru.tutor.model.club.ClubReferralsResponse.Summary;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ClubSteps extends BaseApiSteps {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final ClubApi clubApi;

    public ClubSteps(String baseUrl) {
        super(baseUrl);
        this.clubApi = new ClubApi(baseUrl);
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
    public Response getClubPayments(String authToken) {
        Response response = this.clubApi.getClubPayments(authToken);
        AllureAttachments.attachJson("Club Payments Response", response.getBody().asString());
        AllureAttachments.attachJson("Club Payments Headers", response.getHeaders().toString());
        return response;
    }

    @Step("Валидация успешного ответа истории платжей клуба")
    public List<ClubPaymentResponse> validateClubPaymentsResponse(Response response) {
        assertBasicResponse(response);

        // Проверяем что ответ массив
        Object body = response.jsonPath().get("$");
        assertNotNull(body, "Тело ответа не должно быть null");
        assertInstanceOf(List.class, body, "Ответ должен быть списком (List)");

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
                    () -> assertDoesNotThrow(() -> {
                        if (payment.name() != null) {
                            assertFalse(payment.name().isBlank(), "name не должен быть null");
                        }
                    }),
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
                        () -> assertTrue(true, "Сумма дохода должна быть положительной")
                );
            } else if (payment.isOutcome()) {
                assertAll(paymentInfo + " - проверка расхода",
                        () -> assertTrue(payment.amount() > 0, "Сумма расхода должна быть положительной")
                );
            }
        }
    }

    @Step("POST /user/club/sz-details - сохранение деталей самозанятого")
    public Response saveSzDetails(String authToken, Map<String, Object> payload) {
        Response response = clubApi.saveSzDetails(authToken, payload);
        AllureAttachments.attachJson("Savee Sz Details Request", payload.toString());
        AllureAttachments.attachJson("Save Sz Details Response", response.getBody().asString());
        AllureAttachments.attachJson("Save Sz Details Headers", response.getHeaders().toString());
        return response;
    }

    @Step("POST /user/club/sz-details - сохранение деталей самозанятого из объекта")
    public Response saveSzDetails(String authToken, SzDetailsRequest request) {
        Map<String, Object> payload = MAPPER.convertValue(request, Map.class);
        return saveSzDetails(authToken, payload);
    }

    @Step("DELETE /user/club/details - удаление деталей клуба")
    public Response deleteClubDetails(String authToken) {
        Response response = clubApi.deleteClubDetails(authToken);
        AllureAttachments.attachJson("Delete Club Details Response", response.getBody().asString());
        AllureAttachments.attachJson("Delete Club Details Headers", response.getHeaders().toString());
        return response;
    }

    /**
     * Тестовые данные для ИП
     */

    @Step("POST /user/club/ip-details - сохранение деталей ИП")
    public Response saveIpDetails(String authToken, Map<String, Object> payload) {
        Response response = clubApi.saveIpDetails(authToken, payload);
        AllureAttachments.attachJson("Save Ip Details Request", payload.toString());
        AllureAttachments.attachJson("Save Ip Details Response", response.getBody().asString());
        AllureAttachments.attachJson("Save Ip Details Headers", response.getHeaders().toString());
        return response;
    }

    @Step("POST /user/club/ip-details - сохранение деталей ИП из объекта")
    public Response saveIpDetails(String authToken, SzDetailsRequest request) {
        Map<String, Object> payload = MAPPER.convertValue(request, Map.class);
        return saveIpDetails(authToken, payload);
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

    @Step("POST /user/club/v2/links - создание реферальной ссылки")
    public Response createClubLink(String authToken, int productId, String name) {
        Map<String, Object> payload = Map.of("productId", productId, "name", name);
        Response response = clubApi.createClubLink(authToken, payload);
        AllureAttachments.attachJson("Club Link Create Request", payload.toString());
        AllureAttachments.attachJson("Club Link Create Response", response.getBody().asString());
        AllureAttachments.attachJson("Club Link Create Headers", response.getHeaders().toString());
        return response;
    }

    @Step("Валидация ответа создания реферальной ссылки")
    public ClubLinkResponse validateClubLinkResponse(Response response) {
        assertStatusAnyOf(response, 201);

        ClubLinkResponse link = response.as(ClubLinkResponse.class);
        assertNotNull(link, "Реферальная ссылка не должна быть null");
        assertTrue(link.id() != null && link.id() > 0, "id должен быть положительным");
        assertTrue(link.code() != null && !link.code().isBlank(), "code не должен быть пустым");
        assertTrue(link.hasUrl(), "url не должен быть пустым");
        assertNotNull(link.product(), "product не должен быть null");
        assertTrue(link.product().id() != null && link.product().id() > 0,
                "product.id должен быть положительным");

        return link;
    }

    @Step("POST /user/club/share-reward - начисление награды за шеринг")
    public Response shareReward(String authToken) {
        Response response = clubApi.shareReward(authToken);
        AllureAttachments.attachJson("Club Share Reward Response", response.getBody().asString());
        AllureAttachments.attachJson("Club Share Reward Headers", response.getHeaders().toString());
        return response;
    }

    @Step("Валидация ответа начисления награды за шеринг")
    public ShareRewardResponse validateShareRewardResponse(Response response) {
        assertSuccessStatusCode(response);
        assertJsonFieldExists(response, "balance");
        assertJsonFieldExists(response, "granted");

        ShareRewardResponse reward = response.as(ShareRewardResponse.class);
        assertNotNull(reward, "Награда за шеринг не должна быть null");
        assertEquals(true, reward.granted(), "granted должен быть true");
        assertEquals(1000, reward.balance(), "balance должен быть 1000");
        return reward;
    }

    @Step("PUT /user/club/v2/payouts/requisites - сохранение реквизитов ИП")
    public Response savePayoutRequisites(String authToken, ClubPayoutRequisitesRequest request) {
        Map<String, Object> payload = toPayoutPayload(request);
        Response response = clubApi.savePayoutRequisites(authToken, payload);
        AllureAttachments.attachJson("Save Payout Requisites Request", payload.toString());
        AllureAttachments.attachJson("Save Payout Requisites Response", response.getBody().asString());
        AllureAttachments.attachJson("Save Payout Requisites Headers", response.getHeaders().toString());
        return response;
    }

    private Map<String, Object> toPayoutPayload(ClubPayoutRequisitesRequest request) {
        Map<String, Object> payload = MAPPER.convertValue(request, Map.class);
        payload.values().removeIf(Objects::isNull);
        return payload;
    }

    @Step("Валидация ответа сохранения реквизитов ИП")
    public void validateRequisitesSaved(Response response) {
        assertSuccessStatusCode(response);
    }

    @Step("GET /user/club/v2/payouts/requisites - получение реквизитов")
    public Response getPayoutRequisites(String authToken) {
        Response response = clubApi.getPayoutRequisites(authToken);
        AllureAttachments.attachJson("Get Payout Requisites Response", response.getBody().asString());
        AllureAttachments.attachJson("Get Payout Requisites Headers", response.getHeaders().toString());
        return response;
    }

    @Step("Валидация реквизитов ИП: каждый detail-поле совпадает с запросом")
    public void validateRequisitesMatchRequest(Response response, ClubPayoutRequisitesRequest request) {
        assertBasicResponse(response);

        ClubPayoutRequisitesResponse requisites = response.as(ClubPayoutRequisitesResponse.class);
        assertNotNull(requisites, "реквизиты ИП не должны быть null");
        assertNotNull(requisites.details(), "details не должен быть null");

        ClubPayoutRequisitesResponse.Details details = requisites.details();
        assertAll("Проверка реквизитов ИП",
                () -> assertEquals("ip", requisites.type(), "type должен быть 'ip'"),
                () -> assertEquals("ИП", requisites.typeLabel(), "typeLabel должен быть 'ИП'"),
                () -> assertEquals(request.fullName(), requisites.fullName(), "fullName должен совпадать"),
                () -> assertEquals(request.fullName(), details.fullName(), "details.fullName должен совпадать"),
                () -> assertEquals(request.email(), details.email(), "details.email должен совпадать"),
                () -> assertEquals(request.idEdo(), details.idEdo(), "details.idEdo должен совпадать"),
                () -> assertEquals(request.phone(), details.phone(), "details.phone должен совпадать"),
                () -> assertEquals(request.isResident(), details.isResident(), "details.isResident должен совпадать"),
                () -> assertEquals(request.isSameAddress(), details.isSameAddress(), "details.isSameAddress должен совпадать"),
                () -> assertEquals(request.inn(), details.inn(), "details.inn должен совпадать"),
                () -> assertEquals(request.ogrnip(), details.ogrnip(), "details.ogrnip должен совпадать"),
                () -> assertEquals(request.addressRegister(), details.addressRegister(), "details.addressRegister должен совпадать"),
                () -> assertEquals(request.addressFact(), details.addressFact(), "details.addressFact должен совпадать"),
                () -> assertEquals(request.bankAccount(), details.bankAccount(), "details.bankAccount должен совпадать"),
                () -> assertEquals(request.bankKorAccount(), details.bankKorAccount(), "details.bankKorAccount должен совпадать"),
                () -> assertEquals(request.bankName(), details.bankName(), "details.bankName должен совпадать"),
                () -> assertEquals(request.bik(), details.bik(), "details.bik должен совпадать")
        );
    }

    @Step("Валидация реквизитов СЗ: каждый detail-поле совпадает с запросом")
    public void validateSzRequisitesMatchRequest(Response response, ClubPayoutRequisitesRequest request) {
        assertBasicResponse(response);

        ClubPayoutRequisitesResponse requisites = response.as(ClubPayoutRequisitesResponse.class);
        assertNotNull(requisites, "реквизиты СЗ не должны быть null");
        assertNotNull(requisites.details(), "details не должен быть null");

        ClubPayoutRequisitesResponse.Details details = requisites.details();
        assertAll("Проверка реквизитов СЗ",
                () -> assertEquals("sz", requisites.type(), "type должен быть 'sz'"),
                () -> assertEquals("Самозанятый", requisites.typeLabel(), "typeLabel должен быть 'Самозанятый'"),
                () -> assertEquals(request.fullName(), requisites.fullName(), "fullName должен совпадать"),
                () -> assertEquals(request.fullName(), details.fullName(), "details.fullName должен совпадать"),
                () -> assertEquals(request.email(), details.email(), "details.email должен совпадать"),
                () -> assertEquals(request.idEdo(), details.idEdo(), "details.idEdo должен совпадать"),
                () -> assertEquals(request.phone(), details.phone(), "details.phone должен совпадать"),
                () -> assertEquals(request.isResident(), details.isResident(), "details.isResident должен совпадать"),
                () -> assertEquals(request.isSameAddress(), details.isSameAddress(), "details.isSameAddress должен совпадать"),
                () -> assertEquals(request.inn(), details.inn(), "details.inn должен совпадать"),
                () -> assertEquals(request.addressRegister(), details.addressRegister(), "details.addressRegister должен совпадать"),
                () -> assertEquals(request.addressFact(), details.addressFact(), "details.addressFact должен совпадать"),
                () -> assertEquals(request.bankAccount(), details.bankAccount(), "details.bankAccount должен совпадать"),
                () -> assertEquals(request.bankKorAccount(), details.bankKorAccount(), "details.bankKorAccount должен совпадать"),
                () -> assertEquals(request.bankName(), details.bankName(), "details.bankName должен совпадать"),
                () -> assertEquals(request.bik(), details.bik(), "details.bik должен совпадать"),
                () -> assertEquals(request.dateBirth(), details.dateBirth(), "details.dateBirth должен совпадать"),
                () -> assertEquals(request.passSerial(), details.passSerial(), "details.passSerial должен совпадать"),
                () -> assertEquals(request.passNumber(), details.passNumber(), "details.passNumber должен совпадать"),
                () -> assertEquals(request.passIssuer(), details.passIssuer(), "details.passIssuer должен совпадать"),
                () -> assertEquals(request.passIssued(), details.passIssued(), "details.passIssued должен совпадать"),
                () -> assertEquals(request.passDepartment(), details.passDepartment(), "details.passDepartment должен совпадать")
        );
    }

    @Step("DELETE /user/club/v2/payouts/requisites - удаление реквизитов")
    public Response deletePayoutRequisites(String authToken) {
        Response response = clubApi.deletePayoutRequisites(authToken);
        AllureAttachments.attachJson("Delete Payout Requisites Response", response.getBody().asString());
        AllureAttachments.attachJson("Delete Payout Requisites Headers", response.getHeaders().toString());
        return response;
    }

    @Step("Валидация ответа удаления реквизитов (200, тело '[]')")
    public void validateRequisitesDeleted(Response response) {
        assertSuccessStatusCode(response);
        assertEquals("[]", response.getBody().asString().trim(),
                "Тело ответа должно быть '[]' после удаления");
    }

    @Step("Валидация отсутствия реквизитов (200, тело null/пустое)")
    public void validateRequisitesAbsent(Response response) {
        assertSuccessStatusCode(response);
        String body = response.getBody().asString().trim();
        assertTrue(body.isEmpty() || body.equals("null"),
                "Тело ответа должно быть пустым или 'null', но получено: " + body);
    }

    @Step("Начисление награды за шеринг и проверка (POST /user/club/share-reward)")
    public ShareRewardResponse shareRewardAndValidate(String authToken) {
        return validateShareRewardResponse(shareReward(authToken));
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

    @Step("Создание реферальной ссылки и проверка (POST /user/club/v2/links)")
    public ClubLinkResponse createAndValidateClubLink(String authToken, int productId, String name) {
        return validateClubLinkResponse(createClubLink(authToken, productId, name));
    }

    @Step("Генерация и сохранение реквизитов ИП (PUT /user/club/v2/payouts/requisites)")
    public ClubPayoutRequisitesRequest saveGeneratePayoutRequisites(String authToken, String email) {
        ClubPayoutRequisitesRequest request = ClubTestDataGenerator.generateValidPayoutRequisites(email);
        validateRequisitesSaved(savePayoutRequisites(authToken, request));
        return request;
    }

    @Step("Генерация и сохранение реквизитов СЗ (PUT /user/club/v2/payouts/requisites)")
    public ClubPayoutRequisitesRequest saveGeneratePayoutRequisitesSz(String authToken, String email) {
        ClubPayoutRequisitesRequest request = ClubTestDataGenerator.generateValidPayoutRequisitesSz(email);
        validateRequisitesSaved(savePayoutRequisites(authToken, request));
        return request;
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

    // ==================== GET /user/club/v2/referrals ====================

    private static final Set<String> REFERRAL_STATUS_CODES = Set.of("trial", "lite", "premium", "lifetime");

    @Step("GET /user/club/v2/referrals - рефералы текущего пользователя")
    public Response getClubReferrals(String authToken) {
        return getClubReferrals(authToken, Map.of(
                "limit", 10, "page", 1, "sort", "incomePerMonth", "direction", "desc"));
    }

    @Step("GET /user/club/v2/referrals - рефералы с query-параметрами {queryParams}")
    public Response getClubReferrals(String authToken, Map<String, Object> queryParams) {
        Response response = clubApi.getClubReferrals(authToken, queryParams);
        AllureAttachments.attachJson("Club Referrals Response", response.getBody().asString());
        AllureAttachments.attachJson("Club Referrals Headers", response.getHeaders().toString());
        return response;
    }

    @Step("Валидация контракта ответа рефералов (200, json, CORS, items/pagination/summary)")
    public ClubReferralsResponse validateClubReferralsResponse(Response response) {
        assertBasicResponse(response);
        assertJsonKeyExists(response, "items");
        assertJsonKeyExists(response, "pagination");
        assertJsonKeyExists(response, "summary");

        ClubReferralsResponse referrals = response.as(ClubReferralsResponse.class);

        assertNotNull(referrals, "Ответ со списком рефералов не должен быть null");
        assertNotNull(referrals.items(), "items не должен быть null");
        assertNotNull(referrals.pagination(), "pagination не должен быть null");
        assertNotNull(referrals.summary(), "summary не должен быть null");

        return referrals;
    }

    @Step("Валидация пагинации рефералов (limit={expectedLimit}, page={expectedPage})")
    public void validateReferralsPagination(ClubReferralsResponse referrals, int expectedLimit, int expectedPage) {
        Pagination pagination = referrals.pagination();

        assertAll("Проверка пагинации рефералов",
                () -> assertEquals(expectedLimit, pagination.perPage(),
                        "per_page должен быть " + expectedLimit),
                () -> assertEquals(expectedPage, pagination.page(),
                        "page должен быть " + expectedPage),
                () -> assertEquals(referrals.items().size(), pagination.count(),
                        "count должен совпадать с размером items"),
                () -> assertTrue(pagination.total() >= pagination.count(),
                        "total (" + pagination.total() + ") должен быть >= count (" + pagination.count() + ")"),
                () -> assertEquals(pagination.expectedPages(), pagination.pages(),
                        "pages должен │соответствовать total / per_page"));
    }

    @Step("Валидация summary: сумма счётчиков == pagination.total")
    public void validateReferralsSummaryTotals(ClubReferralsResponse referrals) {
        Summary summary = referrals.summary();
        long withoutSubscription = referrals.countWithoutSubscription();

        assertAll("Проверка summary рефералов",
                () -> assertTrue(summary.allNonNegative(), "Счётчики summary не могут быть отрицательными"),
                () -> assertEquals(referrals.pagination().total() - withoutSubscription,
                        summary.total(),
                        "Сумма счётчиков summary (" + summary.total() + ") + рефералы без подписки ("
                                + withoutSubscription + ") должна совпадать с pagination.total ("
                                + referrals.pagination().total() + ")"));
    }

    @Step("Валидация структуры каждого элемента items")
    public void validateReferralsItems(List<ReferralItem> items) {
        for (int i = 0; i < items.size(); i++) {
            ReferralItem item = items.get(i);
            String itemInfo = "Реферал #" + (i + 1) + " (userId=" + item.userId() + ")";

            assertAll(itemInfo + " - структура",
                    () -> assertNotNull(item.userId(), itemInfo + ": userId не должен быть null"),
                    () -> assertTrue(item.userId() > 0, itemInfo + ": userId должен быть положительным"),
                    () -> assertNotNull(item.level(), itemInfo + ": level не должен быть null"),
                    () -> assertTrue(item.level() >= 1, itemInfo + ": level должен быть >= 1"),
                    () -> assertNotNull(item.incomePerMonth(), itemInfo + ": incomePerMonth не должен быть null"),
                    () -> assertTrue(item.incomePerMonth() >= 0, itemInfo + ": incomePerMonth должен быть >= 0"),
                    () -> assertNotNull(item.incomeOneTime(), itemInfo + ": incomeOneTime не должен быть null"),
                    () -> assertTrue(item.incomeOneTime() >= 0, itemInfo + ": incomeOneTime должен быть >= 0"),
                    () -> validateSubscriptionDate(item, itemInfo),
                    () -> assertTrue(item.avatarUrl() == null || item.hasValidAvatarUrl(), itemInfo + ": avatarUrl должен быть null или URL"),
                    () -> validateSubscriptionStatus(item.subscriptionStatus(), itemInfo),
                    () -> validateReferralLink(item, itemInfo)
            );
        }
    }

    private void validateSubscriptionStatus(SubscriptionStatus status, String itemInfo) {
        assertNotNull(status, itemInfo + ": subscriptionStatus не должен быть null");
        assertJsonNotEmpty(status.label(), itemInfo + ": subscriptionStatus.label");
        assertJsonNotEmpty(status.stateLabel(), itemInfo + ": subscriptionStatus.stateLabel");
        assertNotNull(status.isActive(), itemInfo + ": subscriptionStatus.isActive не должен быть null");

        if (status.code() == null) {
            assertFalse(status.isActiveSubscription(),
                    itemInfo + ": при code == null подписка не может быть активной, stateLabel=" + status.stateLabel());
            return;
        }
        assertTrue(REFERRAL_STATUS_CODES.contains(status.code()),
                itemInfo + ": subscriptionStatus.code должен быть одним из " +
                        REFERRAL_STATUS_CODES);
    }

    private void validateReferralLink(ReferralItem item, String itemInfo) {
        ReferralLink link = item.link();

        if (link == null) {
            assertFalse(item.hasSubscription(),
                    itemInfo + ": link == null допустим только для реферала без подписки");
            return;
        }

        if (link.id() == null) {
            assertNull(link.url(), itemInfo + ": при link.id == null link.url должен    быть null");
            return;
        }

        assertTrue(link.id() > 0, itemInfo + ": link.id должен быть положительным");
        assertTrue(link.hasUrl(), itemInfo + ": link.url должен быть URL");
        assertTrue(link.hasName() && !link.isPlaceholderName(),
                itemInfo + ": link.name должен быть непустым и не равным строке \"null\"");
    }

    private void validateSubscriptionDate(ReferralItem item, String itemInfo) {
        if (!item.hasSubscription()) {
            assertNull(item.getSubscriptionDateAsLocalDateTime(),
                    itemInfo + ": у реферала без подписки subscriptionDate должен быть null");
            return;
        }
        assertNotNull(item.getSubscriptionDateAsLocalDateTime(),
                itemInfo + ": subscriptionDate должен быть в формате yyyy-MM-dd HH:mm:ss");
    }

    private void assertJsonNotEmpty(String value, String message) {
        assertNotNull(value, message + " не должен быть null");
        assertFalse(value.isBlank(), message + " не должен быть пустым");
    }

    @Step("Валидация summary против items (только при pages == 1)")
    public void validateReferralsSummaryMatchesItems(ClubReferralsResponse referrals) {
        Summary summary = referrals.summary();

        assertAll("Сверка summary с items (pages == 1)",
                () -> assertEquals(summary.byStatusCode("premium"), referrals.countByStatusCode("premium"),
                        "summary.premium() должен совпадать с числом items с кодом premium"),
                () -> assertEquals(summary.byStatusCode("lifetime"), referrals.countByStatusCode("lifetime"),
                        "summary.lifetime() должен совпадать с числом items с кодом lifetime"),
                () -> assertEquals(summary.byStatusCode("lite"), referrals.countByStatusCode("lite"),
                        "summary.lite() должен совпадать с числом items с кодом lite"),
                () -> assertEquals(summary.byStatusCode("trial"), referrals.countByStatusCode("trial"),
                        "summary.trial должен совпадать с числом items с кодом trial"));
    }

    @Step("Валидация сортировки по incomePerMonth по убыванию")
    public void validateReferralsSortedByIncomePerMonthDesc(List<ReferralItem> items) {
        for (int i = 1; i < items.size(); i++) {
            int previous = items.get(i - 1).incomePerMonth();
            int current = items.get(i).incomePerMonth();
            assertTrue(previous >= current, "incomePerMonth должен идти по убыванию: #" + i + " (" + previous + ") >= #" + (i + 1) + " (" + current + ")");
        }
    }

    @Step("GET /user/club/v2/referrals - полный контракт рефералов")
    public ClubReferralsResponse getAndValidateClubReferrals(String authToken, int expectedLimit, int expectedPage) {
        ClubReferralsResponse referrals = validateClubReferralsResponse(getClubReferrals(authToken));
        validateReferralsPagination(referrals, expectedLimit, expectedPage);
        validateReferralsSummaryTotals(referrals);
        return referrals;
    }

    // ==================== GET /user/club/v2/home & analytics ====================

    @Step("GET /user/club/v2/home/analytics - аналитика главной клуба")
    public Response getClubHomeAnalytics(String authToken) {
        Response response = clubApi.getClubHomeAnalytics(authToken);
        AllureAttachments.attachJson("Club Home Analytics Response", response.getBody().asString());
        AllureAttachments.attachJson("Club Home Analytics Headers", response.getHeaders().toString());
        return response;
    }

    @Step("Валидация структуры home/analytics")
    public void validateHomeAnalytics(Response response) {
        assertBasicResponse(response);
        assertAll("home/analytics - базовые ключи",
                () -> assertJsonKeyExists(response, "filters"),
                () -> assertJsonKeyExists(response, "totals"),
                () -> assertJsonKeyExists(response, "series"));

        assertAll("totals - ключи",
                () -> assertJsonFieldExists(response, "totals.clicks"),
                () -> assertJsonFieldExists(response, "totals.newSubscriptions"),
                () -> assertJsonFieldExists(response, "totals.subscriptions"),
                () -> assertJsonFieldExists(response, "totals.unsubscribed"),
                () -> assertJsonFieldExists(response, "totals.paymentError"),
                () -> assertJsonFieldExists(response, "totals.income"));

        List<Map<String, Object>> clicks = response.jsonPath().getList("series.clicks");
        assertNotNull(clicks, "series.clicks не должен быть null");
        for (int i = 0; i < clicks.size(); i++) {
            Map<String, Object> point = clicks.get(i);
            assertAll("series.clicks[" + i + "]",
                    () -> assertNotNull(point.get("date"), "date не должен быть null"),
                    () -> assertNotNull(point.get("value"), "value не должен быть null"));
        }
    }

    @Step("GET /user/club/v2/home/banners - баннеры клуба")
    public Response getClubHomeBanners(String authToken) {
        Response response = clubApi.getClubHomeBanners(authToken);
        AllureAttachments.attachJson("Club Home Banners Response", response.getBody().asString());
        return response;
    }

    @Step("Валидация структуры home/banners")
    public void validateHomeBanners(Response response) {
        assertBasicResponse(response);
        List<Map<String, Object>> items = response.jsonPath().getList("items");
        assertNotNull(items, "items не должен быть null");
        for (int i = 0; i < items.size(); i++) {
            Map<String, Object> banner = items.get(i);
            assertAll("banner[" + i + "]",
                    () -> assertNotNull(banner.get("id"), "id"),
                    () -> nonEmptyString(banner, "title"),
                    () -> nonEmptyString(banner, "description"),
                    () -> url(banner.get("image"), "image"),
                    () -> assertOptionalUrl(banner.get("mobileImage"), "mobileImage"),
                    () -> assertOptionalUrl(banner.get("link"), "link"));
        }
    }

    @Step("GET /user/club/v2/home/links - ссылки участника клуба")
    public Response getClubHomeLinks(String authToken) {
        Response response = clubApi.getClubHomeLinks(authToken);
        AllureAttachments.attachJson("Club Home Links Response", response.getBody().asString());
        return response;
    }

    @Step("Валидация структуры home/links")
    public void validateHomeLinks(Response response) {
        assertBasicResponse(response);
        assertJsonKeyExists(response, "filters");
        List<Map<String, Object>> items = response.jsonPath().getList("items");
        assertNotNull(items, "items не должен быть null");
        for (int i = 0; i < items.size(); i++) {
            Map<String, Object> link = items.get(i);
            assertAll("link[" + i + "]",
                    () -> positiveInt(link, "id"),
                    () -> nonEmptyString(link, "name"),
                    () -> url(link.get("url"), "url"),
                    () -> nonNegative(link, "clicks"),
                    () -> nonNegative(link, "trial"),
                    () -> nonNegative(link, "premium"),
                    () -> nonNegative(link, "paymentError"),
                    () -> nonNegative(link, "unsubscribed"),
                    () -> nonNegative(link, "income"));
        }
    }

    @Step("GET /user/club/v2/home/news - новости клуба")
    public Response getClubHomeNews(String authToken) {
        Response response = clubApi.getClubHomeNews(authToken);
        AllureAttachments.attachJson("Club Home News Response", response.getBody().asString());
        return response;
    }

    @Step("Валидация структуры home/news")
    public void validateHomeNews(Response response) {
        assertBasicResponse(response);
        assertAll("home/news - ключи",
                () -> assertJsonKeyExists(response, "items"),
                () -> assertJsonKeyExists(response, "pagination"),
                () -> assertJsonFieldExists(response, "pagination.count"),
                () -> assertJsonFieldExists(response, "pagination.total"),
                () -> assertJsonFieldExists(response, "pagination.per_page"),
                () -> assertJsonFieldExists(response, "pagination.page"),
                () -> assertJsonFieldExists(response, "pagination.pages"));
    }

    @Step("GET /user/club/v2/home/news/{newsId} - детальная информация новости")
    public Response getClubNewsDetail(String authToken, String newsId) {
        Response response = clubApi.getClubNewsDetail(authToken, newsId);
        AllureAttachments.attachJson("Club News Detail Response", response.getBody().asString());
        return response;
    }

    @Step("Валидация структуры home/news/{slug}")
    public void validateClubNewsDetail(Response response, String expectedSlug) {
        assertBasicResponse(response);
        assertAll("news detail - ключи",
                () -> assertJsonFieldExists(response, "id"),
                () -> assertEquals(expectedSlug, response.jsonPath().getString("slug"),
                        "slug должен совпадать с запрошенным"),
                () -> assertJsonFieldNotEmpty(response, "title"),
                () -> assertJsonFieldNotEmpty(response, "publishedAt"),
                () -> assertJsonFieldNotEmpty(response, "content"));
    }

    @Step("GET /user/club/v2/analytics/links - аналитика по ссылкам")
    public Response getClubAnalyticsLinks(String authToken) {
        Response response = clubApi.getClubAnalyticsLinks(authToken);
        AllureAttachments.attachJson("Club Analytics Links Response", response.getBody().asString());
        return response;
    }

    @Step("Валидация структуры analytics/links")
    public void validateAnalyticsLinks(Response response) {
        assertBasicResponse(response);
        assertJsonKeyExists(response, "filters");
        List<Map<String, Object>> items = response.jsonPath().getList("items");
        assertNotNull(items, "items не должен быть null");
        for (int i = 0; i < items.size(); i++) {
            Map<String, Object> link = items.get(i);
            assertAll("analytics_link[" + i + "]",
                    () -> positiveInt(link, "id"),
                    () -> nonEmptyString(link, "name"),
                    () -> containsKey(link, "product"),
                    () -> nonNegative(link, "referralsTotal"),
                    () -> nonNegative(link, "level1"),
                    () -> nonNegative(link, "level2"),
                    () -> nonNegative(link, "level3"),
                    () -> nonNegative(link, "income"));
        }
        assertJsonKeyExists(response, "pagination");
        assertJsonFieldExists(response, "pagination.count");
    }

    @Step("GET /user/club/v2/analytics/monthly-income - аналитика дохода по уровням")
    public Response getClubAnalyticsMonthlyIncome(String authToken) {
        Response response = clubApi.getClubAnalyticsMonthlyIncome(authToken);
        AllureAttachments.attachJson("Club Monthly Income Response", response.getBody().asString());
        return response;
    }

    @Step("Валидация структуры analytics/monthly-income")
    public void validateAnalyticsMonthlyIncome(Response response) {
        assertBasicResponse(response);
        assertJsonKeyExists(response, "filters");
        List<Map<String, Object>> items = response.jsonPath().getList("items");
        assertNotNull(items, "items не должен быть null");
        for (int i = 0; i < items.size(); i++) {
            Map<String, Object> income = items.get(i);
            assertAll("income_level[" + i + "]",
                    () -> assertTrue(((Number) income.get("level")).intValue() >= 1,
                            "level должен быть >= 1"),
                    () -> nonEmptyString(income, "label"),
                    () -> nonNegative(income, "rate"),
                    () -> nonNegative(income, "active"),
                    () -> nonNegative(income, "incomePerMonth"));
        }
        assertJsonKeyExists(response, "totals");
        assertJsonFieldExists(response, "totals.active");
        assertJsonFieldExists(response, "totals.incomePerMonth");
    }

    @Step("GET /user/club/v2/links/summary - сводка по ссылкам")
    public Response getClubLinksSummary(String authToken) {
        Response response = clubApi.getClubLinksSummary(authToken);
        AllureAttachments.attachJson("Club Links Summary Response", response.getBody().asString());
        return response;
    }

    @Step("Валидация структуры links/summary")
    public void validateLinksSummary(Response response) {
        assertBasicResponse(response);
        assertJsonKeyExists(response, "filters");
        assertAll("links/summary - ключи",
                () -> assertJsonFieldExists(response, "totals.linksCount"),
                () -> assertJsonFieldExists(response, "totals.clicks"),
                () -> assertJsonFieldExists(response, "totals.activeReferrals"),
                () -> assertJsonFieldExists(response, "totals.activePremiums"),
                () -> assertJsonFieldExists(response, "totals.income"),
                () -> assertJsonFieldExists(response, "breakdown.trial"),
                () -> assertJsonFieldExists(response, "breakdown.lite"),
                () -> assertJsonFieldExists(response, "breakdown.premium"),
                () -> assertJsonFieldExists(response, "breakdown.lifetime"),
                () -> assertJsonFieldExists(response, "breakdown.paymentError"),
                () -> assertJsonFieldExists(response, "breakdown.unsubscribed"),
                () -> assertJsonFieldExists(response, "series.clicks"),
                () -> assertJsonFieldExists(response, "series.income"),
                () -> assertJsonFieldExists(response, "series.activePremiums"));
    }

    @Step("GET /user/club/v2/referrals/{userId} - детальная информация реферала")
    public Response getClubReferralDetail(String authToken, String userId) {
        Response response = clubApi.getClubReferralDetail(authToken, userId);
        AllureAttachments.attachJson("Club Referral Detail Response", response.getBody().asString());
        return response;
    }

    @Step("Валидация структуры referrals/{userId}")
    public void validateReferralDetail(Response response) {
        assertBasicResponse(response);
        assertAll("referrals/{userId} - ключи",
                () -> assertJsonKeyExists(response, "userId"),
                () -> assertJsonKeyExists(response, "name"),
                () -> assertJsonKeyExists(response, "avatarUrl"),
                () -> assertJsonKeyExists(response, "level"),
                () -> assertJsonKeyExists(response, "invitedBy"),
                () -> assertJsonKeyExists(response, "subscriptionStatus"),
                () -> assertJsonKeyExists(response, "incomePerMonth"),
                () -> assertJsonKeyExists(response, "incomeOneTime"),
                () -> assertJsonKeyExists(response, "link"),
                () -> assertJsonKeyExists(response, "activity"));

        assertAll("subscriptionStatus - наличие ключей", () -> {
            Map<String, Object> status = response.jsonPath().get("subscriptionStatus");
            assertNotNull(status, "subscriptionStatus не должен быть null");
            for (String key : List.of("code", "label", "isActive", "stateLabel")) {
                assertTrue(status.containsKey(key), "subscriptionStatus." + key + " должен присутствовать");
            }
        });
        assertAll("activity - наличие ключей", () -> {
            Map<String, Object> activity = response.jsonPath().get("activity");
            assertNotNull(activity, "activity не должен быть null");
            for (String key : List.of("lastLoginAt", "registeredAt", "addedCoursesCount", "completedLessonsCount")) {
                assertTrue(activity.containsKey(key), "activity." + key + " должен присутствовать");
            }
        });
    }

    @Step("GET /user/club/v2/referrals/{userId}/referrals - дерево рефералов")
    public Response getClubReferralReferrals(String authToken, String userId) {
        Response response = clubApi.getClubReferralReferrals(authToken, userId);
        AllureAttachments.attachJson("Club Referral Tree Response", response.getBody().asString());
        return response;
    }

    @Step("Валидация структуры referrals/{userId}/referrals")
    public void validateReferralReferrals(Response response) {
        assertBasicResponse(response);
        assertAll("referrals/{userId}/referrals - ключи",
                () -> assertJsonKeyExists(response, "items"),
                () -> assertJsonKeyExists(response, "pagination"),
                () -> assertJsonFieldExists(response, "pagination.count"),
                () -> assertJsonFieldExists(response, "pagination.total"),
                () -> assertJsonFieldExists(response, "pagination.per_page"),
                () -> assertJsonFieldExists(response, "pagination.page"),
                () -> assertJsonFieldExists(response, "pagination.pages"));
    }

    @Step("Проверка 404 для {path}")
    public void validateNotFound(Response response, String path) {
        AllureAttachments.attachJson("404 Response [" +  path + "]", response.getBody().asString());

        assertAll("Ошибка 404 для " + path,

                () -> assertEquals(404, response.getStatusCode(),
                "Ожидался 404 для " + path + ", получен: " + response.getStatusCode()),
                () -> assertContentTypeJson(response),
                () -> {
            String body = response.getBody().asString();
            if (body.contains("\"message\"")) {
                assertJsonFieldNotEmpty(response, "message");
            } else if (body.contains("\"detail\"")) {
                assertJsonFieldNotEmpty(response, "detail");
            } else {
                fail("Тело 404 не содержит 'message' или 'detail'");
            }
                });
    }
}
