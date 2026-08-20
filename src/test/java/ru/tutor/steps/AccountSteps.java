package ru.tutor.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.api.UserApi;
import ru.tutor.base.BaseApiSteps;
import ru.tutor.model.UserEditDataRequest;
import ru.tutor.utils.AllureAttachments;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AccountSteps extends BaseApiSteps {

    private final UserApi userApi;

    public AccountSteps(String baseUrl) {
        super(baseUrl);
        this.userApi = new UserApi(baseUrl);
    }

    @Step("PUT /user/edit/data - изменение данных пользователя")
    public Response editUserData(String authToken, UserEditDataRequest body) {
        Response response = userApi.editData(authToken, body);
        AllureAttachments.attachJson("Edit Data Response", response.getBody().asString());
        return response;
    }

    @Step("Валидация успешного изменения данных пользователя")
    public void validateEditDataSuccess(Response response) {
        assertBasicResponse(response);
        assertJsonFieldEquals(response, "message", "ok");
    }

    @Step("PUT /user/edit/pass - изменение пароля")
    public Response editPassword(String authToken, String oldPassword, String newPassword) {
        Map<String, String> body = new HashMap<>();
        body.put("oldPassword", oldPassword);
        body.put("newPassword", newPassword);
        Response response =userApi.editPassword(authToken, body);
        AllureAttachments.attachJson("Edit Password Response",  response.getBody().asString());
        return response;
    }

    @Step("Валидация успешной смены пароля")
    public void validateEditPasswordSuccess(Response response) {
        assertBasicResponse(response);
        assertJsonFieldEquals(response, "message", "ok");
    }

    @Step("Валидация ошибки смены пароля")
    public void validateEditPasswordError(Response response, String expectedMessage) {
        assertEquals(400, response.getStatusCode());
        String body = response.getBody().asString();
        assertNotNull(body, "Тело ответа не должно быть null");
        if (expectedMessage != null && !expectedMessage.isEmpty()) {
            assertTrue(body.contains(expectedMessage),
                    "Ответ должен содержать: " + expectedMessage);
        }
    }
}