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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class ClubSteps extends BaseApiSteps {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final ClubApi clubApi;

    public ClubSteps(String baseUrl){
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
    public Response getClubPayments(String authToken){
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
                        () -> assertTrue(payment.amount() > 0, "Сумма дохода должна быть положительной")
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
    public ClubPayoutRequisitesResponse validateRequisitesMatchRequest(Response response, ClubPayoutRequisitesRequest request) {
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

        return requisites;
    }

    @Step("Валидация реквизитов СЗ: каждый detail-поле совпадает с запросом")
    public ClubPayoutRequisitesResponse validateSzRequisitesMatchRequest(Response response, ClubPayoutRequisitesRequest request) {
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

        return requisites;
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
        validateClubPayoutSummaryValues(summary,  expectedBalance, expectedAvailable, expectProfileValid, expectCanRequestWithdrawal);
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
        assertEquals(500, response.getStatusCode(), "Ожидался статус 500 при canRequestWithdrawal=false, получен: " +  response.getStatusCode());
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
}
