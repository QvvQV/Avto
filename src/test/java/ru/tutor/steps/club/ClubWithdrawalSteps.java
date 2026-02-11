package ru.tutor.steps.club;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.tutor.api.ClubApi;
import ru.tutor.model.WithdrawalRequest;
import ru.tutor.model.WithdrawalResponse;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WithdrawalTestDataGenerator;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class ClubWithdrawalSteps {
    private final ClubApi withdrawalApi;

    public ClubWithdrawalSteps(String baseUrl) {
        this.withdrawalApi = new ClubApi(baseUrl);
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
                    .header("Access-Control-Allow-Origin", equalTo("https://client.ttplace.ru"))
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

//    @Step("Валидация ошибки валидации (422)")
//    public ErrorResponse validateValidationErrorResponse(Response response) {
//        int statusCode = response.getStatusCode();
//        assertEquals(422,  statusCode, "Ожидался статус 422");
//
//        // Проверяем структуру ошибки
//        response.then()
//                .contentType(ContentType.JSON)
//                .body("error", notNullValue())
//                .body("error.messages", notNullValue());
//
//        // Парсим ошибку
//        ErrorResponse errorResponse = response.as(ErrorResponse.class);
//        assertNotNull(errorResponse, "ErrorResponse should not be null");
//        assertTrue(errorResponse.hasValidationErrors(), "Should have validation errors");
//
////        // Добавляем информацию об ошибках в отчёт
////        StringBuilder errorInfo = new StringBuilder("Ошибки валидации вывода:\n");
////        if (errorResponse.getError() != null && errorResponse.getError().getMessage() != null) {
////            errorResponse.getError().getMessage().forEach((field, message) -> {
////                errorInfo.append("• ").append(field).append(": ").append(message).append("\n");
////            });
////        }
////
////        AllureAttachments.attachText("Валидационные ошибки вывода", errorInfo.toString());
////        return errorResponse;
//    }

//    @Step("Проверка лимитов вывода")
//    public void validateWithdrawalLimits(Response response, Integer requestedAmount) {
//        int statusCode = response.getStatusCode();
//
//        if (statusCode == 422) {
//            ErrorResponse errorResponse = response.as(ErrorResponse.class);
//            if (errorResponse.getError() != null && errorResponse.getError().getMessages() !=null) {
//                // Проверяем есть ли ошибки связанные с лимитами
//                boolean hasLimitError = errorResponse.getError().getMessages().entrySet().stream()
//                        .anyMatch(entry -> entry.getKey().contains("amount") &&
//                                (entry.getValue().contains("мало") ||
//                                        entry.getValue().contains("много") ||
//                                        entry.getValue().contains("limit") ||
//                                        entry.getValue().contains("лимит")));
//
//                if (hasLimitError) {
//                    AllureAttachments.attachText("Лимиты сработали",
//                            "✅ Валидация лимитов работает: " +
//                                    errorResponse.getError().getMessages());
//                }
//            }
//        } else if (statusCode == 201) {
//            WithdrawalResponse withdrawalResponse = response.as(WithdrawalResponse.class);
//            if (withdrawalResponse != null && withdrawalResponse.isSuccessful()) {
//                AllureAttachments.attachText("Лимиты пройдены",
//                        "✅ Запрос на вывод " + requestedAmount + " прошел лимиты");
//            }
//        }
//    }

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
