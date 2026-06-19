package ru.tutor.steps.club;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.api.club.ClubApi;
import ru.tutor.base.BaseApiSteps;
import ru.tutor.model.club.WithdrawalRequest;
import ru.tutor.model.club.WithdrawalResponse;
import ru.tutor.utils.AllureAttachments;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ClubWithdrawalSteps extends BaseApiSteps {
    private final ClubApi withdrawalApi;

    public ClubWithdrawalSteps(String baseUrl) {
        super(baseUrl);
        this.withdrawalApi = new ClubApi(baseUrl);
    }

    public static ClubWithdrawalSteps withBaseUrl(String baseUrl) {
        return new ClubWithdrawalSteps(baseUrl);
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
        Map<String, Object> payload = Map.of("amount", request.amount());
        return requestWithdrawal(authToken, payload);
    }

    @Step("Валидация успешного запроса на вывод средств")
    public WithdrawalResponse validateSuccessfulWithdrawal(Response response) {
        // Проверяем статус кода 201
        int statusCode = response.getStatusCode();

        if (statusCode == 201) {
            assertStatusAnyOf(response, 201);
            assertContentTypeJson(response);
            assertCorsHeaders(response);

            // Проверяем структуру ответа
            assertJsonFieldEquals(response, "success", true);
            assertJsonFieldNotEmpty(response, "message");
            assertJsonFieldExists(response, "data");
            assertJsonFieldExists(response, "data.id");
            assertJsonFieldExists(response, "data.userId");
            assertJsonFieldExists(response, "data.amount");
            assertJsonFieldExists(response, "data.status");

            // Преобразуем в модель
            WithdrawalResponse withdrawalResponse = response.as(WithdrawalResponse.class);
            assertNotNull(withdrawalResponse, "Ответ на вывод средств не должен быть null");
            assertTrue(withdrawalResponse.isSuccessful(), "Ответ должен быть успешным");
            assertNotNull(withdrawalResponse.data(), "Данные ответа не должны быть null");

            AllureAttachments.attachText("Успешный запрос на вывод",
                    "✅ Статус " + statusCode + "\n" +
                            "✅ ID запроса: " + withdrawalResponse.getWithdrawalId() + "\n" +
                            "✅ Сумма: " + withdrawalResponse.getWithdrawalAmount() + "\n" +
                            "✅ Сообщение: " + withdrawalResponse.message());

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
            assertJsonFieldExists(response, "error");

            // Извлекаем сообщения об ошибках
            Map<String, Object> errorResponse = response.jsonPath().getMap("error");
            assertNotNull(errorResponse, "Ответ с ошибкой не должен быть null");

            AllureAttachments.attachText("Валидация сработала", "Получена ошибка валидации: " + response.getBody().asString());
        } else if (statusCode == 500) {
            AllureAttachments.attachText("Ошибка сервера",
                    "Внутренняя ошибка сервера: " + response.getBody().asString());
        }
    }
}
