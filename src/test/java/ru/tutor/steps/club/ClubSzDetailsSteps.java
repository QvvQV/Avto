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

public class ClubSzDetailsSteps {
    private final ClubSzDetailsApi szDetailsApi;

    public ClubSzDetailsSteps(String baseUrl) {
        this.szDetailsApi = new ClubSzDetailsApi(baseUrl);
    }

    @Step("POST /user/club/sz-details - сохранение деталей самозанятого")
    public Response saveSzDetails(String authToken, Map<String, Object> payload) {
        Response response = szDetailsApi.saveSzDetails(authToken, payload);
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
                "✅ Статус 201 Created получен\n" +
                        "✅ Тело ответа: пустой массив []\n" +
                        "✅ Заголовки корректны");
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

//    @Step("Валидация ответа получения деталей")
//    public SzDetailsResponse validateGetResponse(Response response) {
//        // Проверяем статус код
//        int statusCode = response.getStatusCode();
//
//        if (statusCode != 201) {
//            AllureAttachments.attachText("Детали не найдены",
//                    "Эндпоинт возвращает " + statusCode + " Возможно данные ещё не сохранены.");
//            return null;
//        }
//
//        if (statusCode == 204) {
//            AllureAttachments.attachText("Нет содержимого",
//                    "Статус 204. Данных нетю");
//            return null;
//        }
//
//        response.then()
//                .statusCode(200)
//                .contentType(ContentType.JSON);
//
//        // Преобразуем в модель
//        SzDetailsResponse szResponse = response.as(SzDetailsResponse.class);
//
//        if (szResponse != null && szResponse.getData() != null) {
//            AllureAttachments.attachJson("Полученные данные",
//                    "ID: " + szResponse.getData().getId() + "\n" +
//                            "ФИО: " + szResponse.getData().getFullName() + "\n" +
//                            "ИНН: " + szResponse.getData().getInn());
//        }
//
//        return szResponse;
//    }

//    @Step("Проверка соответствия сохраненных данных")
//    public void validateSavedDataMatches(SzDetailsRequest request, SzDetailsResponse response) {
//        if (response == null || response.getData() == null) {
//            AllureAttachments.attachText("Проверка данных пропущена",
//                    "Нет данных для проверки соответствия");
//            return;
//        }
//
//        SzDetailsResponse.SzDetailsData data = response.getData();
//
//        assertAll("Saved data validation",
//                () -> assertEquals(request.getFullName(), data.getFullName(),
//                        "Full name should match"),
//                () -> assertEquals(request.getEmail(), data.getEmail(),
//                        "Email should match"),
//                () -> assertEquals(request.getPhone(), data.getPhone(),
//                        "Phone should match"),
//                () -> assertEquals(request.getDateBirth(), data.getDateBirth(),
//                        "Birth date should match"),
//                () -> assertEquals(request.getInn(), data.getInn(),
//                        "INN should match"),
//                () -> assertEquals(request.getIdEdo(), data.getIdEdo(),
//                        "ID EDO should match"),
//                () -> assertEquals(request.getAddressRegister(), data.getAddressRegister(),
//                        "Register address should match"),
//                () -> assertEquals(request.getAddressFact(), data.getAddressFact(),
//                        "Fact address should match"),
//                () -> assertEquals(request.getSameAddress(), data.getSameAddress(),
//                        "Same address flag should match"),
//                () -> assertEquals(request.getPassSerial(), data.getPassSerial(),
//                        "Passport serial should match"),
//                () -> assertEquals(request.getPassNumber(), data.getPassNumber(),
//                        "Passport number should match"),
//                () -> assertEquals(request.getPassIssued(), data.getPassIssued(),
//                        "Passport issue date should match"),
//                () -> assertEquals(request.getPassIssuer(), data.getPassIssuer(),
//                        "Passport issuer should match"),
//                () -> assertEquals(request.getPassDepartment(), data.getPassDepartment(),
//                        "Passport department should match"),
//                () -> assertEquals(request.getBankName(), data.getBankName(),
//                        "Bank name should match"),
//                () -> assertEquals(request.getBankAccount(), data.getBankAccount(),
//                        "Bank account should match"),
//                () -> assertEquals(request.getBankKorAccount(), data.getBankKorAccount(),
//                        "Bank kor account should match"),
//                () -> assertEquals(request.getBik(), data.getBik(),
//                        "BIK should match"),
//                () -> assertEquals(request.getIsResident(), data.getResident(),
//                        "Resident flag should match"),
//                () -> assertEquals(request.getOgrnip(), data.getOgrnip(),
//                        "OGRNIP should match")
//        );
//    }

//    @Step("Проверка бизнес-логики адресов")
//    public void validateAddressBusinessLogic(SzDetailsRequest request, SzDetailsResponse response) {
//        if (response == null || response.getData() == null) return;
//
//        SzDetailsResponse.SzDetailsData data = response.getData();
//
//        // Проверка логики одинаковых адресов
//        if (Boolean.TRUE.equals(request.getSameAddress())) {
//            assertEquals(data.getAddressRegister(), data.getAddressFact(),
//                    "When sameAddress=true, addresses shoul be equal");
//        }
//
//        // Проверка что адреса не пустые
//        assertNotNull(data.getAddressRegister(), "Address register should not be null");
//        assertNotNull(data.getAddressFact(), "Address fact should not be null");
//    }

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
        } else if (statusCode == 201) {
            AllureAttachments.attachText("Успешное создание",
                    "Данные сохранены успешно, статус 201");
        } else if (statusCode == 500) {
            // Внутренняя ошибка сервера
            AllureAttachments.attachText("Ошибка сервера",
                    "Внутренняя ошибка сервера: " + response.getBody().asString());
        }
    }

//    @Step("Проверка различных статус-кодов")
//    public void validateResponseStatus(Response response, Integer expectedStatusCode) {
//        int actualStatusCode = response.getStatusCode();
//
//        AllureAttachments.attachText("Проверка статуса",
//                "Ожидаемый статус: " + expectedStatusCode + "\n" +
//                        "Фактический статус: " + actualStatusCode + "\n" +
//                        "Соответствует: " + (actualStatusCode == expectedStatusCode ? "✅" : "❌"));
//
//        if (expectedStatusCode != null) {
//            assertEquals(expectedStatusCode, actualStatusCode,
//                    "Статус код должен быть " + expectedStatusCode);
//        }
//    }

//    @Step("Полный цикл: сохранение → получение → проверка")
//    public void fullSzDetailsCycle(String authToken, SzDetailsRequest request) {
//        // Шаг 1: Сохранение данных
//        Response saveResponse = saveSzDetails(authToken, request);
//        validateSuccessfulSave(saveResponse);
//
//        // Шаг 2: Получение данных (через некоторое время)
//        try {
//            Thread.sleep(1000); // Небольшая задержка для обработки данных
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
//
//        Response getResponse = getSzDetails(authToken);
//        SzDetailsResponse getResult = validateGetResponse(getResponse);
//
//        // Шаг 3: Проверка соответствия
//        if (getResult != null) {
//            validateSavedDataMatches(request, getResult);
//            validateAddressBusinessLogic(request, getResult);
//        }
//    }

    @Step("DELETE /user/club/details - удаление деталей клуба")
    public Response deleteClubDetails(String authToken) {
        Response response = szDetailsApi.deleteClubDetails(authToken);
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

//    @Step("Валидация успешного удаления деталей клуба")
//    public void validateMethodNotAllowe(Response response) {
//        // Проверяем что возвращается 405 для недопустимых методов
//        response.then()
//                .statusCode(405)
//                .contentType(ContentType.JSON)
//                .header("Allow", containsString("DELETE"));
//
//        AllureAttachments.attachText("Method Not Allowed",
//                "✅ Статус 405 Method Not Allowed\n" +
//                        "✅ Заголовок Allow: " + response.getHeader("Allow") + "\n" +
//                        "✅ Тело ответа содержит информацию об ошибке");
//    }

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
        Response response = szDetailsApi.saveIpDetails(authToken, payload);
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

//    @Step("GET /user/club/ip-details - получение деталей ИП")
//    public Response getIpDetails(String authToken) {
//        Response response = szDetailsApi.getIpDetails(authToken);
//        AllureAttachments.attachJson("Get Ip Details", response.getBody().asString());
//        return response;
//    }

//    @Step("Валидация ошибки валидации (422)")
//    public ErrorResponse validateValidationError(Response response) {
//        int  statusCode = response.getStatusCode();
//        assertEquals (422,  statusCode, "Ожидался статус 422 Unprocessable Entity");
//        // Проверяем структуру ошибки
//        response.then()
//                .contentType(ContentType.JSON)
//                .body("error", notNullValue())
//                .body("error.messages", notNullValue());
//
//        // Парсим ошибку
//        ErrorResponse errorResponse = response.as(ErrorResponse.class);
//        assertNotNull(errorResponse, "Error response should not be null");
//        assertTrue(errorResponse.hasValidationErrors(), "Should have validation errors");
//
//        // Добавляем информацию об ошибках в отчёт
//        StringBuilder errorInfo = new StringBuilder("Ошибка валидации ИП:\n");
//        if (errorResponse.getError() != null)
//        {
//            errorInfo.append(errorResponse.getError());
//        }
//    }

//    @Step("Валидация ответа получения деталей")
//    public SzDetailsResponse validateGetResponseIp(Response response) {
//        // Проверяем статус код
//        int statusCode = response.getStatusCode();
//
//        if (statusCode != 201) {
//            AllureAttachments.attachText("Детали не найдены",
//                    "Эндпоинт возвращает " + statusCode + " Возможно данные ещё не сохранены.");
//            return null;
//        }
//
//        if (statusCode == 204) {
//            AllureAttachments.attachText("Нет содержимого",
//                    "Статус 204. Данных нетю");
//            return null;
//        }
//
//        response.then()
//                .statusCode(200)
//                .contentType(ContentType.JSON);
//
//        // Преобразуем в модель
//        SzDetailsResponse ipResponse = response.as(SzDetailsResponse.class);
//
//        if (ipResponse != null && ipResponse.getData() != null) {
//            AllureAttachments.attachJson("Полученные данные ИП",
//                    "ID: " + ipResponse.getData().getId() + "\n" +
//                            "ФИО: " + ipResponse.getData().getFullName() + "\n" +
//                            "ИНН: " + ipResponse.getData().getInn()+ "\n" +
//                            "ОГРНИП: " + ipResponse.getData().getOgrnip());
//
//        }
//
//        return ipResponse;
//    }
}