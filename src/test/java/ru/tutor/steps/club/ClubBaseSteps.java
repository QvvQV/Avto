package ru.tutor.steps.club;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.api.club.ClubApi;
import ru.tutor.base.BaseApiSteps;
import ru.tutor.utils.AllureAttachments;

import static org.junit.jupiter.api.Assertions.*;

public class ClubBaseSteps extends BaseApiSteps {

    protected final ClubApi clubApi;

    public ClubBaseSteps(String baseUrl) {
        super(baseUrl);
        this.clubApi = new ClubApi(baseUrl);
    }

    @Step("Проверка 404 для {path}")
    public void validateNotFound(Response response, String path) {
        AllureAttachments.attachJson("404 Response [" +  path + "]", response.getBody().asString());

        assertAll("Ошибка 404 для " + path,

                () -> assertEquals(404, response.getStatusCode(),
                "Ожидался 404 для " + path + ", получен: " + response.getStatusCode()),
                () -> assertContentTypeJson(response),
                () -> {
            String body = response.getBody().asString();
            if (body.contains("\"message\"")) {
                assertJsonFieldNotEmpty(response, "message");
            } else if (body.contains("\"detail\"")) {
                assertJsonFieldNotEmpty(response, "detail");
            } else {
                fail("Тело 404 не содержит 'message' или 'detail'");
            }
                });
    }
}
