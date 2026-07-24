package ru.tutor.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.api.UserCertificateApi;
import ru.tutor.base.BaseApi;
import ru.tutor.base.BaseApiSteps;
import ru.tutor.utils.AllureAttachments;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CertificateSteps extends BaseApiSteps {
    private final UserCertificateApi certApi;

    public CertificateSteps(String baseUrl) {
        super(baseUrl);
        certApi = new UserCertificateApi(baseUrl);
    }

    @Step("GET /user/certificate/generate?name={name}&orderNumber={orderNumber}")
    public Response generateCertificate(String authToken, String name, String orderNumber) {
        Response response = certApi.generateCertificate(authToken, name, orderNumber);
        AllureAttachments.attachJson("Certificate Generate Response", response.getBody().asString());
        return response;
    }

    @Step("Валидация успешной генерации сертификата")
    public void validateCertificateGenerateResponse(Response response) {
        assertSuccessStatusCode(response);
        assertContentTypeJson(response);
        assertCorsHeaders(response);
        assertNotNull(response.jsonPath().getString("pdf"), "pdf не должен быть null");
        assertNotNull(response.jsonPath().getString("png"), "png не должен быть null");
        assertTrue(response.jsonPath().getLong("number") > 0, "number должен быть > 0");
    }
}
