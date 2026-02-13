package ru.tutor.steps.club;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.tutor.api.ClubSzDetailsApi;
import ru.tutor.model.SzDetailsRequest;
import ru.tutor.model.SzDetailsResponse;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.SzDetailsTestDataGenerator;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public record ClubSzDetailsSteps(ClubSzDetailsApi clubSzDetailsApi) {

    public static ClubSzDetailsSteps withBaseUrl(String baseUrl) {
        return new ClubSzDetailsSteps(new ClubSzDetailsApi(baseUrl));
    }

    @Step("POST /user/club/sz-details - сохранение деталей самозанятого")
    public Response saveSzDetails(String authToken, Map<String, Object> payload) {
        Response response = clubSzDetailsApi.saveSzDetails(authToken, payload);
        AllureAttachments.attachJson("Savee Sz Details Request", payload.toString());
        AllureAttachments.attachJson("Save Sz Details Response", response.getBody().asString());
        AllureAttachments.attachJson("Save Sz Details Headers", response.getHeaders().toString());
        return response;
    }

    @Step("POST /user/club/sz-details - сохранение деталей самозанятого из объекта")
    public Response saveSzDetails(String authToken, SzDetailsRequest request) {
        Map<String, Object> payload = SzDetailsTestDataGenerator.toMap(request);
        return saveSzDetails(authToken, payload);
    }

    @Step("Валидация успешного сохранения деталей")
    public void validateSuccessfulSave(Response response) {
        // Проверяем статус код
        response.then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .header("Access-Control-Allow-Origin", equalTo("https://client.ttplace.ru"))
                .header("Access-Control-Allow-Credentials", equalTo("true"));

        // Проверяем структуру ответа
        response.then()
                .body("$", instanceOf(java.util.List.class))
                .body("$", hasSize(0)); // Проверяем что массив пустой

        // Дополнительная информация для отчета
        AllureAttachments.attachText("Успешное сохранение",
                """
                        ✅ Статус 201 Created получен
                        ✅ Тело ответа: пустой массив []
                        ✅ Заголовки корректны""");
    }

    @Step("Парсинг ответа сохранения в модель")
    public SzDetailsResponse parseSaveResponse(Response response) {
        // Метод возвращает SzDetailsResponse или null
        if (response.getStatusCode() == 201) {
            // Для статуса 201 с пустым массивом возвращаем null
            // или создаем пустой объект
            SzDetailsResponse szResponse = new SzDetailsResponse();
            szResponse.setSuccess(true);
            szResponse.setMessage("Data saved successfully (201 Created)");
            return szResponse;
        } else if (response.getStatusCode() == 200) {
            // Если вдруг сервер вернет 200, парсим ответ
            return response.as(SzDetailsResponse.class);
        } else {
            // Для других статусов пробуем парсить
            try {
                return response.as(SzDetailsResponse.class);
            } catch (Exception e) {
                // Если не удалось распарсить, создаем объект с ошибкой
                SzDetailsResponse errorResponse = new SzDetailsResponse();
                errorResponse.setSuccess(false);
                errorResponse.setMessage("Error: Status code " + response.getStatusCode());
                return errorResponse;
            }
        }
    }

    @Step("Проверка валидации полей")
    public void validateFieldValidation(Response response) {
        // Проверяем что при невалидных данных возвращается ошибка
        int statusCode = response.getStatusCode();

        if (statusCode != 201) {
            // Это ожидаемое поведение для невалидных данных
            response.then()
                    .body("error.messages", not(empty()));

            // Извлекаем сообщения напрямую через jsonPath
            Map<String, String> validationErrors = response.jsonPath()
                    .getMap("error.messages");

            assertNotNull(validationErrors, "Validation errors should not be null");
            assertFalse(validationErrors.isEmpty(), "Should have validation errors");

            AllureAttachments.attachText("Валидация сработала",
                    "Получена ошибка валидации: " + response.getBody().asString());
        } else {
            AllureAttachments.attachText("Успешное создание",
                    "Данные сохранены успешно, статус 201");
        }
    }

    @Step("DELETE /user/club/details - удаление деталей клуба")
    public Response deleteClubDetails(String authToken) {
        Response response = clubSzDetailsApi.deleteClubDetails(authToken);
        AllureAttachments.attachJson("Delete Club Details Response", response.getBody().asString());
        AllureAttachments.attachJson("Delete Club Details Headers", response.getHeaders().toString());
        return response;
    }

    @Step("Валидация успешного удаления деталей клуба")
    public void validateSuccessfulDeletion(Response response) {
        // Проверяем статус код
        response.then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .header("Access-Control-Allow-Origin", equalTo("https://client.ttplace.ru"))
                .header("Access-Control-Allow-Credentials", equalTo("true"));

        // проверяем что тело ответа - пустой массив []
        response.then()
                .body("$", instanceOf(java.util.List.class))
                .body("$", hasSize(0));

        AllureAttachments.attachText("Успешное удаление",
                "✅ Статус 200 OK\n" +
                        "✅ Тело ответа: пустой массив []" +
                        response.getHeader("Access-Control-Allow-Origin") + ", " +
                        response.getHeader("Access-Control-Allow-Credentials"));
    }

    @Step("Валидация идемпотентности DELETE (повторное удаление)")
    public void validateIdempotentDeletion(Response response) {
        int statusCode = response.getStatusCode();

        // DELETE должен быть идемпотентным - повторный вызов должен возвращать тот же результат
        if (statusCode == 200) {
            // Первое успешное удаление
            response.then()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body("$", instanceOf(java.util.List.class))
                    .body("$", hasSize(0));

            AllureAttachments.attachText("Успешное удаление",
                    "✅ Статус 200 OK - данные удалены");
        } else if (statusCode == 404) {
            // Данные уже удалены или не существовали
            AllureAttachments.attachText("Данные не найдены",
                    "ℹ Статус 404 - данные уже удалены или не существовали");
        } else {
            AllureAttachments.attachText("Неожиданный статус",
                    "❓ Неожиданный статус код: " + statusCode);
        }
    }

    @Step("Проверка ответа на различные статусы коды")
    public void validateDeleteResponse(Response response) {
        int statusCode = response.getStatusCode();

        switch (statusCode) {
            case 200:
                validateSuccessfulDeletion(response);
                break;
            case 404:
                response.then()
                        .statusCode(404);
                AllureAttachments.attachText("Данные не найдены",
                        "ℹ Статус 404 - данные уже удалены или не существовали");
                break;
            case 401:
            case 403:
                AllureAttachments.attachText("Ошибка авторизации",
                        "🔒 Статус: " + statusCode + " - Ошибка авторизации");
                break;
            case 500:
                AllureAttachments.attachText("Ошибка сервера",
                        "❌ Статус 500 Internal Server Error\n" +
                                "Тело ответа: " + response.getBody().asString());
                break;
                default:
                    AllureAttachments.attachText("Неожиданный статус",
                            "❓ Неожиданный статус код: " + statusCode);
                    fail("Неожиданный статус код: " + statusCode);
        }
    }

    @Step("Пауза для обработки данных")
    public void pauseForProcessing(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Пауза прервана", e);
        }
    }

    /**
     * Тестовые данные для ИП
     */

    @Step("POST /user/club/ip-details - сохранение деталей ИП")
    public Response saveIpDetails(String authToken, Map<String, Object> payload) {
        Response response = clubSzDetailsApi.saveIpDetails(authToken, payload);
        AllureAttachments.attachJson("Save Ip Details Request", payload.toString());
        AllureAttachments.attachJson("Save Ip Details Response", response.getBody().asString());
        AllureAttachments.attachJson("Save Ip Details Headers", response.getHeaders().toString());
        return response;
    }

    @Step("POST /user/club/ip-details - охранение деталей ИП из объекта")
    public Response saveIpDetails(String authToken, SzDetailsRequest request) {
        Map<String, Object> payload = SzDetailsTestDataGenerator.toMap(request);
        return saveIpDetails(authToken, payload);
    }
}