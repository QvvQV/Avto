package ru.tutor.steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.tutor.api.ReactivatePremiumApi;
import ru.tutor.model.ReactivatePremiumResponse;
import ru.tutor.utils.AllureAttachments;

/*
 * Шаги по Ручкам для работы восстановления Премиума из письма внутренней почты / по e-mail рассылке
 */

public record ReactivatePremiumSteps(ReactivatePremiumApi reactivatePremiumApi) {

    public static ReactivatePremiumSteps withBaseUrl(String baseUrl) {
        return new ReactivatePremiumSteps(new ReactivatePremiumApi(baseUrl));
    }

    @Step("Переключение подписки c Лайта на Премиум по внутренней почте")
    public Response reactivatePremium(String authToken) {
        return this.reactivatePremiumApi.reactivatePremium(authToken, null);
    }

    @Step("Реактивация подписки по внутренней почте с aff_rid: {affRid}")
    public Response reactivatePremium( String authToken, String affRid) {
        Response response = reactivatePremiumApi.reactivatePremium(authToken, affRid);

        int statusCode = response.getStatusCode();
        AllureAttachments.attachText("Результат реактивации",
                String.format("Статус код: %d\nТело ответа: %s",
                        statusCode, response.getBody().asString()));
        return response;
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