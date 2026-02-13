package ru.tutor.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.page.api.ResetApi;
import ru.tutor.utils.ResponseAnalyzer;

public record ResetSteps(ResetApi resetApi) {

    public static ResetSteps  withBaseUrl(String baseUrl) {
        return new ResetSteps(new  ResetApi(baseUrl));
    }

    @Step("Отправка запроса на сброс пароля для email: {email}")
    public Response requestPasswordReset(String email) {
        Response response = resetApi.resetPassword(email);
        ResponseAnalyzer.analyzeAndAttachResponse(response, "Password Reset");
        return response;
    }
}
