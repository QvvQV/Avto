package ru.tutor.steps.orders;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.api.orders.ReactivatePremiumApi;
import ru.tutor.base.BaseApiSteps;
import ru.tutor.model.order.ReactivatePremiumResponse;
import ru.tutor.utils.AllureAttachments;

/*
 * Шаги по Ручкам для работы восстановления Премиума из письма внутренней почты / по e-mail рассылке
 */

public class ReactivatePremiumSteps extends BaseApiSteps {
    private final ReactivatePremiumApi reactivatePremiumApi;

    public ReactivatePremiumSteps(String baseUrl) {
        super(baseUrl);
        this.reactivatePremiumApi = new ReactivatePremiumApi(baseUrl);
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
        assertSuccessStatusCode(response);

        response.then();

        return response.as(ReactivatePremiumResponse.class);
    }
}