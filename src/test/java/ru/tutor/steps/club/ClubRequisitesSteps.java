package ru.tutor.steps.club;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.model.club.*;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.ClubTestDataGenerator;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class ClubRequisitesSteps extends ClubBaseSteps {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public ClubRequisitesSteps(String baseUrl) {
        super(baseUrl);
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
}
