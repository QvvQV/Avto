package ru.tutor.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.api.ResetApi;
import ru.tutor.base.BaseApiSteps;
import ru.tutor.utils.ResponseAnalyzer;

public class ResetSteps extends BaseApiSteps {
    private final ResetApi resetApi;

    public ResetSteps(String baseUrl) {
        super(baseUrl);
        this.resetApi = new ResetApi(baseUrl);
    }

    public static ResetSteps  withBaseUrl(String baseUrl) {
        return new ResetSteps(baseUrl);
    }

    @Step("Отправка запроса на сброс пароля для email: {email}")
    public Response requestPasswordReset(String email) {
        Response response = resetApi.resetPassword(email);
        ResponseAnalyzer.analyzeAndAttachResponse(response, "Password Reset");
        return response;
    }
}
