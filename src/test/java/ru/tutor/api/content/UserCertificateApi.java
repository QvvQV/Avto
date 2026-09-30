package ru.tutor.api.content;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.base.BaseApi;
import ru.tutor.constants.EndpointsContent;

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
}
