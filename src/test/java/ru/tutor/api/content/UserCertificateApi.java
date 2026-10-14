package ru.tutor.api.content;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.base.BaseApi;
import ru.tutor.constants.EndpointsContent;

import java.util.Map;

public class UserCertificateApi extends BaseApi {
    public UserCertificateApi(String baseUrl) {
        super(baseUrl);
    }

    @Step("GET /user/certificate/generate?name={name}&orderNumber={orderNumber}")
    public Response generateCertificate(String authToken, String name, String orderNumber) {
        return getWithToken(
                EndpointsContent.CERTIFICATE_GENERATE + "?name=" + name + "&orderNumber=" + orderNumber,
                authToken
        );
    }

    @Step("GET {path} - файл сертификата")
    // можно скачать pdf и png файл, но тест займёт 30 секунд
    public Response getCertificateFile(String authToken, String path) {
        return getWithToken(path, authToken);
    }
}
