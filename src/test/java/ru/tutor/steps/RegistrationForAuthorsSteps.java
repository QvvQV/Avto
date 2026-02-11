package ru.tutor.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.api.RegistrationForAuthorsApi;

public class RegistrationForAuthorsSteps {

    private RegistrationForAuthorsApi registrationForAuthorsApi;

    RegistrationForAuthorsSteps (String baseUrl) {
        this.registrationForAuthorsApi = new RegistrationForAuthorsApi(baseUrl);
    }

    @Step("Переключение подписки c Лайта на Премиум по внутренней почте")
    public Response registrationAutohor(String authToken) {
        Response response = this.registrationForAuthorsApi.registrationAuthor();
        return response;
    }
}
