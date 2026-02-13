package ru.tutor.steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.tutor.api.ReactivatePremiumApi;
import ru.tutor.model.ReactivatePremiumResponse;

/*
 * Шаги по Ручкам для работы восстановления Премиума из письма внутренней почты / по e-mail рассылке
 */

public record ReactivatePremiumSteps(ReactivatePremiumApi reactivatePremiumApi) {

    public static ReactivatePremiumSteps withBaseUrl(String baseUrl) {
        return new ReactivatePremiumSteps(new ReactivatePremiumApi(baseUrl));
    }

    @Step("Переключение подписки c Лайта на Премиум по внутренней почте")
    public Response reactivatePremium(String authToken) {
        return this.reactivatePremiumApi.reactivatePremium(authToken);
    }

    @Step("Переключение подписки c Лайта на Премиум по по ссылке из email-рассылки")
    public Response reactivatePremiumByEmail(String authToken) {
        return this.reactivatePremiumApi.reactivatePremiumByEmail(authToken);
    }

    @Step("Валидация успешного ответа при реактивации")
    public ReactivatePremiumResponse reactivatePremiumSuccessfully(Response response) {
        response.then()
                .statusCode(200)
                .contentType(ContentType.JSON);

        response.then();

        return response.as(ReactivatePremiumResponse.class);
    }
}