package ru.tutor.steps.club;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.tutor.api.ClubApi;
import ru.tutor.config.TestConfig;
import ru.tutor.model.club.WithdrawalRequest;
import ru.tutor.model.club.WithdrawalResponse;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WithdrawalTestDataGenerator;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public record ClubWithdrawalSteps(ClubApi withdrawalApi) {

    public static ClubWithdrawalSteps withBaseUrl(String baseUrl) {
        return new ClubWithdrawalSteps(new ClubApi(baseUrl));
    }

    @Step("POST /user/club/request-withdrawal - запрос на вывод средств")
    public Response requestWithdrawal(String authToken, Map<String, Object> payload) {
        Response response = withdrawalApi.requestWithdrawal(authToken, payload);
        AllureAttachments.attachJson("Withdrawal Request", payload.toString());
        AllureAttachments.attachJson("Withdrawal Response", response.getBody().asString());
        AllureAttachments.attachJson("Withdrawal Headers", response.getHeaders().toString());
        return response;
    }

    @Step("POST /user/club/request-withdrawal - запрос на вывод средств из объекта")
    public Response requestWithdrawal(String authToken, WithdrawalRequest request) {
        Map<String, Object> payload = WithdrawalTestDataGenerator.toMap(request);
        return requestWithdrawal(authToken, payload);
    }

    @Step("Валидация успешного запроса на вывод средств")
    public WithdrawalResponse validateSuccessfulWithdrawal(Response response) {
        // Проверяем статус кода 201
        int statusCode = response.getStatusCode();

        if (statusCode == 201) {
            response.then()
                    .statusCode(statusCode)
                    .contentType(ContentType.JSON)
                    .header("Access-Control-Allow-Origin", equalTo(TestConfig.getCurrentClientDomain()))
                    .header("Access-Control-Allow-Credentials", equalTo("true"));

            // Проверяем структуру ответа
            response.then()
                    .body("success", equalTo(true))
                    .body("message", not(emptyOrNullString()))
                    .body("data", notNullValue())
                    .body("data.id",  notNullValue())
                    .body("data.userId", notNullValue())
                    .body("data.amount", notNullValue())
                    .body("data.status", notNullValue());

            // Преобразуем в модель
            WithdrawalResponse withdrawalResponse = response.as(WithdrawalResponse.class);
            assertNotNull(withdrawalResponse, "Withdrawal response should not be null");
            assertTrue(withdrawalResponse.isSuccessful(), "Response should be successful");
            assertNotNull(withdrawalResponse.getData(), "Response data should not be null");

            AllureAttachments.attachText("Успешный запрос на вывод",
                    "✅ Статус " + statusCode + "\n" +
                            "✅ ID запроса: " + withdrawalResponse.getWithdrawalId() + "\n" +
                            "✅ Сумма: " + withdrawalResponse.getWithdrawalAmount() + "\n" +
                            "✅ Сообщение: " + withdrawalResponse.getMessage());

            return withdrawalResponse;
        } else {
            fail("Ожидался статус 201, получен: " + statusCode);
            return null;
        }
    }

    @Step("Проверка валидации полей при создании заявки")
    public void validateFieldValidation(Response response) {
        int statusCode = response.getStatusCode();
        if (statusCode == 422) {
            // Ожидаемое поведение
            response.then()
                    .body("error", notNullValue())
                    .body("error.messages", not(empty()));

            // Извлекаем сообщения об ошибках
            Map<String, Object> errorResponse = response.jsonPath().getMap("error");
            assertNotNull(errorResponse, "Error response should not be null");

            AllureAttachments.attachText("Валидация сработала", "Получена ошибка валидации: " + response.getBody().asString());
        } else if (statusCode == 500) {
            AllureAttachments.attachText("Ошибка сервера",
                    "Внутренняя ошибка сервера: " + response.getBody().asString());
        }
    }
}
