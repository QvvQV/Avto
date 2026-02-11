package ru.tutor.steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.tutor.api.ReactivatePremiumApi;
import ru.tutor.model.ReactivatePremiumResponse;

/**
 * Шаги по Ручкам для работы восстановления Премиума из письма внутренней почты / по e-mail рассылке
 */

public class ReactivatePremiumSteps {

    private final ReactivatePremiumApi reactivatePremiumApi;

    public ReactivatePremiumSteps(String baseUrl) {
        this.reactivatePremiumApi = new ReactivatePremiumApi(baseUrl);
    }

    @Step("Переключение подписки c Лайта на Премиум по внутренней почте")
    public Response reactivatePremium(String authToken) {
        Response response = this.reactivatePremiumApi.reactivatePremium(authToken);
        return response;
    }

    @Step("Переключение подписки c Лайта на Премиум по по ссылке из email-рассылки")
    public Response reactivatePremiumByEmail(String authToken) {
        Response response = this.reactivatePremiumApi.reactivatePremiumByEmail(authToken);
        return response;
    }

//    @Step("Переключение подписки c Лайта на Премиум по внутренней почте")
//    public Response reactivatePremiumLetter(String authToken) {
//        Response response = this.reactivatePremiumApi.reactivatePremiumLetter(authToken);
//        return response;
//    }

    @Step("Валидация успешного ответа при реактивации")
    public ReactivatePremiumResponse reactivatePremiumSuccessfully(Response response) {
        response.then()
                .statusCode(200)
                .contentType(ContentType.JSON);

        response.then();
//                .body("$", instanceOf(List.class));
//
//        // Преобразуем в список
//        List<OrderToggleSubscriptionResponse> items = response.jsonPath().getList("$", OrderToggleSubscriptionResponse.class);
//
//        assertNotNull(items, "Ответ не должен быть null");

        return response.as(ReactivatePremiumResponse.class);
    }
}