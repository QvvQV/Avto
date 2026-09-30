package ru.tutor.steps.content;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.api.content.UserCertificateApi;
import ru.tutor.base.BaseApiSteps;
import ru.tutor.utils.AllureAttachments;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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
        assertBasicResponse(response);
        Map<String, Object> root = response.jsonPath().getMap("$");
        assertNotNull(root, "Тело должно быть JSON-объектом");
        assertAll("Сертификат",
                nonEmptyString(root, "pdf"),
                nonEmptyString(root, "png"),
                positiveInt(root, "number")
    );
    }
}
