package ru.tutor.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.page.Api.ResetApi;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.ResponseAnalyzer;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResetSteps {
    private final ResetApi resetApi;

    public ResetSteps(String baseUrl) {
        this.resetApi = new ResetApi(baseUrl);
    }

    @Step("Отправка запроса на сброс пароля для email: {email}")
    public Response requestPasswordReset(String email) {
        Response response = resetApi.resetPassword(email);
        ResponseAnalyzer.analyzeAndAttachResponse(response, "Password Reset");
        return response;
    }

    @Step("Проверка, что код ответа = {expectedStatus}")
    public void assertStatus(Response response, int expectedStatus) {
        response.then().statusCode(expectedStatus);
    }

    @Step("Проверка, что сообщение об ошибке равно '{expectedMessage}'")
    public void assertErrorMessage(Response response, String expectedMessage) {
        response.then()
                .body("message", equalTo(expectedMessage))
                .body("$", hasKey("message"));
    }

    @Step("Проверка ответа с ошибкой валидации")
    public void assertValidationErrorResponse(Response response) {
        response.then()
                .statusCode(422)
                .contentType("application/json")
                .body("$", anyOf(
                        hasKey("errors"),
                        hasKey("error"),
                        hasKey("message")
                ));
    }

    @Step("Проверка структуры тела ответа")
    public void assertResponseBodyStructure(Response response, String... expectedFields) {
        for (String field : expectedFields) {
            response.then().body("$", hasKey(field));
        }
    }

    @Step("Проверка времени ответа (макс. {maxTimeMs} мс)")
    public void assertResponseTime(Response response, long maxTimeMs) {
        long responseTime = response.getTime();
        assertTrue(responseTime <= maxTimeMs,
                String.format("Время ответа превышает лимит: %d мс > %d мс", responseTime, maxTimeMs));
    }

    @Step("Проверка допустимых статусов ответа: {expectedStatuses}")
    public void verifyAcceptableStatusCodes(Response response, Integer... expectedStatuses) {
        int actualStatusCode = response.getStatusCode();
        boolean isValid = false;
        for (int expected : expectedStatuses) {
            if (actualStatusCode == expected) {
                isValid = true;
                break;
            }
        }
        assertTrue(isValid, "Expected one of " + Arrays.toString(expectedStatuses) +
                ", but got: " + actualStatusCode);
    }

    @Step("Проверка статуса в диапазоне: {expectedStatuses}")
    public void verifyStatusCodeInRange(int actualStatusCode, Integer... expectedStatuses) {
        boolean isValid = false;
        for (int expected : expectedStatuses) {
            if (actualStatusCode == expected) {
                isValid = true;
                break;
            }
        }
        assertTrue(isValid, "Expected one of " + Arrays.toString(expectedStatuses) +
                ", but got: " + actualStatusCode);
    }

    @Step("Прикрепление тестовых данных: {name} = {value}")
    public void attachTestData(String name, String value) {
        AllureAttachments.attachText("Тестовые данные - " + name, value);
    }

    @Step("Проверка отсутствия ошибок в ответе")
    public void verifyNoErrorsInResponse(Response response) {
        String responseBody = response.getBody().asString();
        if (!responseBody.trim().isEmpty() &&
                response.getContentType().contains("json") &&
                !responseBody.equals("[]")) {
            response.then().body("$", not(hasKey("error")));
        }
    }
}
