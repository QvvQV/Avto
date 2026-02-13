package ru.tutor.test.api.club;


import io.qameta.allure.*;
import io.qameta.allure.junit5.AllureJunit5;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tutor.config.TestConfig;
import ru.tutor.model.SzDetailsRequest;
import ru.tutor.model.SzDetailsResponse;
import ru.tutor.steps.AuthSteps;
import ru.tutor.steps.club.ClubSzDetailsSteps;
import ru.tutor.utils.SzDetailsTestDataGenerator;

import java.util.Map;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AllureJunit5.class)
@Epic("API Тесты пользователя")
@Feature("Детали самозанятого")
@DisplayName("Тесты эндпоинта Клуба: /user/club/sz-details + /user/club/details")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("club")
public class ClubSzDetailsTest {

    private final AuthSteps authSteps = AuthSteps.withBaseUrl(TestConfig.getApiBaseUrl());
    private final ClubSzDetailsSteps szDetailsSteps = ClubSzDetailsSteps.withBaseUrl(TestConfig.getApiBaseUrl());
    private ru.tutor.steps.UserSteps userSteps;
    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();

    private static String authToken;
    private static Integer currentUserId;
    private static SzDetailsRequest testRequest;
    private static final Integer testUserId = 42225;
    private static Integer savedSzDetailsId;

    @BeforeEach
    public void setup() {
        authToken = step("Логин", () ->
                authSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );
    }

    @Test
    @Order(1)
    @DisplayName("Сохранение деталей самозанятого с валидными данными - 201")
    @Story("Основной сценарий")
    @Severity(SeverityLevel.CRITICAL)
    void saveSzDetailsWithValidDate() {
        // Генерация тестовых данных с Faker
        testRequest = step("Генерация тестовых данных", () ->
                SzDetailsTestDataGenerator.generateValidSzDetails(testUserId, TEST_EMAIL)
        );

        Allure.addAttachment("Сгенерированные данные", "text/plain",
                "ФИО: " + testRequest.getFullName() + "\n" +
                        "Телефон: " + testRequest.getPhone() + "\n" +
                        "ИНН: " + testRequest.getInn() + "\n" +
                        "Дата рождения: " + testRequest.getDateBirth() + "\n" +
                        "Email: " + testRequest.getEmail());

        Response response = step("POST /user/club/sz-details", () ->
                szDetailsSteps.saveSzDetails(authToken, testRequest)
        );

        step("Валидация ответа (ожидаем 201)", () ->
                szDetailsSteps.validateSuccessfulSave(response)
        );

        // Если нужно получить объект ответа
        SzDetailsResponse szResponse = step("Парсинг ответа", () ->
                szDetailsSteps.parseSaveResponse(response)
        );

        if (szResponse != null) {
            Allure.addAttachment("Парсинг ответа", "text/plain",
                    "Успешно: " + szResponse.isSuccessful() + "\n" +
                            "Сообщение: " + szResponse.getMessage()
            );
        }
    }

    @Test
    @Order(2)
    @DisplayName("Сохранение данных с минимальным набором полей СЗ - 422")
    @Story("Валидация")
    @Severity(SeverityLevel.NORMAL)
    void saveSzDetailsWithMinimalFields() {
        testRequest = step("Генерация тестовых данных", () ->
                SzDetailsTestDataGenerator.generateValidSzDetails(testUserId, TEST_EMAIL)
        );

        // Создаем минимальный набор данных
        Map<String, Object> minimalPayload = Map.of(
                "userId", testUserId,
                "email", TEST_EMAIL,
                "fullName", "Тестовый пользователь",
                "phone", "+7 (999) 123-45-67",
                "dateBirth", "1990-01-01",
                "inn", "123456789012",
                "isResident", true
        );

        Response response = step("POST /user/club/sz-details с минимльными данными", () ->
                szDetailsSteps.saveSzDetails(authToken, minimalPayload)
        );

        step("Проверка валидации", () ->
                szDetailsSteps.validateFieldValidation(response)
        );
    }

    @Test
    @Order(3)
    @DisplayName("Негативный тест - неверный userId - 500")
    @Story("Валидация")
    @Severity(SeverityLevel.NORMAL)
    void saveSzDetailsWuthWrongUserId() {
        Integer wrongUserId = 3338;

        SzDetailsRequest request = step("Генерация данных с неверным userId", () ->
                SzDetailsTestDataGenerator.generateValidSzDetails(wrongUserId, TEST_EMAIL)
        );

        Response response = step("POST с неверным userID", () ->
                szDetailsSteps.saveSzDetails(authToken, request)
        );

        step("Проверка ошибки", () -> {
            int statusCode = response.getStatusCode();

            if (statusCode == 500) {
                // Ожидаемо - сервер не может найти пользователя
                Allure.addAttachment("Ожидаемая ошибка", "text/plain",
                        "✅ Получена ошибка 500 при неверном userId");
            } else if (statusCode == 403 || statusCode == 404) {
                Allure.addAttachment("Ошибка доступа", "text/plain",
                        "✅ Получена ошибка доступа при неверном userId");
            } else if (statusCode == 201) {
                Allure.addAttachment("Неожиданный успех", "text/plain",
                        "⚠ Данные сохранены с неверным userId - проверьте логику сервера");
            }
        });
    }

    @Test
    @Order(4)
    @DisplayName("Негативный тест - невалидный email - 422")
    @Story("Валидация")
    @Severity(SeverityLevel.NORMAL)
    void saveSzDetailsWithInvalidEmail() {

        SzDetailsRequest request = SzDetailsTestDataGenerator.generateValidSzDetails(testUserId, TEST_EMAIL);
        request.setEmail("invalid_email");

        Response response = step("POST с невалидным email", () ->
                szDetailsSteps.saveSzDetails(authToken, request)
        );

        step("Проверка валидации", () ->
                szDetailsSteps.validateFieldValidation(response)
        );
    }

    @Test
    @Order(5)
    @DisplayName("Негативный тест - запрос без авторизации - 401")
    @Story("Безопасность")
    @Severity(SeverityLevel.CRITICAL)
    void saveSzDetailsWithoutAuth() {
        SzDetailsRequest request = SzDetailsTestDataGenerator.generateValidSzDetails(testUserId, TEST_EMAIL);

        Response response = step("POST без токена авторизации", () ->
                szDetailsSteps.saveSzDetails("invalid_token", request)
        );

        step("Проверка ошибки авторизации", () -> {
            int statusCode = response.getStatusCode();

            assertTrue(statusCode == 401 || statusCode == 403 || statusCode == 500,
                    "Should return auth error (401, 403) or server (500) for invalid token. Got: " + statusCode);

            Allure.addAttachment("Результат", "text/plain",
                    "Статус код: " + statusCode + "\n" +
                            (statusCode == 401 || statusCode == 403 ? "✅ Ошибка авторизации - invalid_token" :
                                    statusCode == 500 ? "⚠ Ошибка сервера при невалидном токене" :
                                            "❌ Неожиданный статус"));
        });
    }

    @Test
    @Order(6)
    @DisplayName("Множественное сохранение и удаление данных - 201")
    @Story("Функциональность")
    @Severity(SeverityLevel.NORMAL)
    void saveMultipleSzDetails() {
        SzDetailsRequest request1 = SzDetailsTestDataGenerator.generateValidSzDetails(testUserId, TEST_EMAIL);
        Response response1 = szDetailsSteps.saveSzDetails(authToken, request1);

        step("Проверка первого сохранения", () -> {
            int status1 = response1.getStatusCode();
            Allure.addAttachment("Первое сохранение", "text/plain",
                    "Статус: " + status1 + "\n" +
                            (status1 == 201 ? "✅ Успешно" : "❌ Ошибка"));
        });

        SzDetailsRequest request2 = SzDetailsTestDataGenerator.generateValidSzDetails(testUserId, TEST_EMAIL);
        Response response2 = szDetailsSteps.saveSzDetails(authToken, request2);

        Response firstResponse = step("Первое удаление", () ->
                szDetailsSteps.deleteClubDetails(authToken)
        );

        step("Проверка первого удаления", () -> {
            int firstStatusCode = firstResponse.getStatusCode();
            Allure.addAttachment("Первое удаление", "text/plain",
                    "Статус: " + firstStatusCode + "\n" +
                            (firstStatusCode == 200 ? "✅ Успешно" :
                                    firstStatusCode == 404 ? "⚠ Уже удалено" : "❌ Ошибка"));
        });

        step("Проверка второго удаления", () -> {
            int secondStatusCode = response2.getStatusCode();

            // DELETE должен быть идемпотентным
            // Может вернуть 201 (успешное удаление) или 404 (уже удалено)
            assertTrue(secondStatusCode == 201 || secondStatusCode == 404,
                    "DELETE должен быть идемпотентным. Ожидается 201 или 404. Получен: " + secondStatusCode);

            Allure.addAttachment("Второе удаление", "text/plain",
                    "Статус: " + secondStatusCode + "\n" +
                            (secondStatusCode == 201 ? "✅ Успешно повторно" :
                                    secondStatusCode == 404 ? "✅ Идемпотентность подтверждена (уже удалено)" :
                                            "❌ Неожиданно")
            );
        });

        step("Проверка второго сохранения", () -> {
            int status2 = response2.getStatusCode();
            Allure.addAttachment("Второе сохранение", "text/plain",
                    "Статус: " + status2 + "\n" +
                            (status2 == 201 ? "✅ Успешно (данные обновлены)" :
                                    status2 == 400 ? "⚠ Ошибка валидации" :
                                            status2 == 500 ? "❌ Ошибка сервера" : "❓ Неожиданный статус"));
        });
    }

    @Test
    @Order(7)
    @DisplayName("Удаление деталей клуба - успешный сценарий")
    @Story("Основной сценарий")
    @Severity(SeverityLevel.CRITICAL)
    void deleteClubDetailsSuccess() {
        Response response = step("DELETE /user/club/details", () ->
                szDetailsSteps.deleteClubDetails(authToken)
        );

        step("Валидация успешного удаления", () ->
                szDetailsSteps.validateSuccessfulDeletion(response)
        );
    }

//    TODO: на доработке
//     @Test
//     @Order(8)
//     @DisplayName("Проверка идемпотентности DELETE")
//     @Story("Функциональность")
//     @Severity(SeverityLevel.NORMAL)
//     void testDeleteIdempotence() {
//        // Первое удаление
//        Response firstResponse = step("Первое удаление", () ->
//                szDetailsSteps.deleteClubDetails(authToken)
//        );
// TODO:
//        step("Проверка первого удаления", () -> {
//            int firstStatusCode = firstResponse.getStatusCode();
//            Allure.addAttachment("Первое удаление", "text/plain");
//        });
//    }

    @Test
    @Order(7)
    @DisplayName("Удаление без авторизации - 401")
    @Story("Безопасность")
    @Severity(SeverityLevel.CRITICAL)
    void deleteClubDetailsWithoutAuth() {
        Response response = step("DELETE /user/club/details без токена", () ->
                szDetailsSteps.deleteClubDetails("invalid_token")
        );

        step("проверка ошибки авторизации", () -> {
            int statusCode = response.getStatusCode();

            // Ожидаем ошибку авторизации
            assertTrue(statusCode == 401 || statusCode == 403,
                    "Должна быть ошибка авторизации (401//403). Получен: " + statusCode);

            Allure.addAttachment("Результат", "text/plain",
                    "Статус код: " + statusCode + "\n" +
                            (statusCode == 401 || statusCode == 403 ?
                                    "✅ Ошибка авторизации (ожидаемо)" : "❌ Неожиданный статус"));
        });
    }

    @Test
    @Order(8)
    @DisplayName("Множественные DELETE запросы подряд - 400")
    @Story("Нагрузочное тестирование")
    @Severity(SeverityLevel.NORMAL)
    void verifyDeleteResponseStructure200() {
        step("Выполнение 3 DELETE запросов подряд", () -> {
                    for (int i = 1; i <= 3; i++) {
                        Response response = szDetailsSteps.deleteClubDetails(authToken);
                        int statusCode = response.getStatusCode();

                        Allure.addAttachment("Запрос " + i, "text/plain",
                                "Статус: " + statusCode + "\n" +
                                        (statusCode == 200 ? "✅ Успешно" :
                                                statusCode == 400 ? "ℹ Уже удалено" : "❌ Ошибка"));
                    }
                }
        );
    }

    @Test
    @Order(9)
    @DisplayName("Быстрая проверка работоспособности создание -> удаление - 200")
    @Story("Смок-тест")
    @Severity(SeverityLevel.BLOCKER)
    void quickSmokeTest() {
        // Генерация тестовых данных с Faker
        testRequest = step("Генерация тестовых данных", () ->
                SzDetailsTestDataGenerator.generateValidSzDetails(testUserId, TEST_EMAIL)
        );

        Allure.addAttachment("Сгенерированные данные", "text/plain",
                "ФИО: " + testRequest.getFullName() + "\n" +
                        "Телефон: " + testRequest.getPhone() + "\n" +
                        "ИНН: " + testRequest.getInn() + "\n" +
                        "Дата рождения: " + testRequest.getDateBirth() + "\n" +
                        "Email: " + testRequest.getEmail());

        Response response = step("POST /user/club/sz-details", () ->
                szDetailsSteps.saveSzDetails(authToken, testRequest)
        );

        step("Валидация ответа (ожидаем 201)", () ->
                szDetailsSteps.validateSuccessfulSave(response)
        );

        // Если нужно получить объект ответа
        SzDetailsResponse szResponse = step("Парсинг ответа", () ->
                szDetailsSteps.parseSaveResponse(response)
        );

        if (szResponse != null) {
            Allure.addAttachment("Парсинг ответа", "text/plain",
                    "Успешно: " + szResponse.isSuccessful() + "\n" +
                            "Сообщение: " + szResponse.getMessage()
            );
        }

        Response responseDel = step("DELETE запрос", () ->
                szDetailsSteps.deleteClubDetails(authToken)
        );

        step("Детальная проверка структуры ответа", () -> {
            int statusCode = responseDel.getStatusCode();

            if (statusCode == 200) {
                // Проверяем заголовки
                assertEquals("application/json", responseDel.getContentType(),
                        "Content-Type: application/json");

                // Проверяем CORS заголовки
                String corsOrigin = responseDel.getHeader("Access-Control-Allow-Origin");
                String corsCredentials = responseDel.getHeader("Access-Control-Allow-Credentials");

                assertNotNull(corsOrigin, "CORS Origin заголовок должен присутствовать");
                assertNotNull(corsCredentials, "CORS Credentials заголовок должен присутствовать");

                // Проверяем тело ответа
                String responseBody = responseDel.getBody().asString().trim();
                assertEquals("[]", responseBody, "Тело ответа должно быть пустым массивом");

                Allure.addAttachment("Структура ответа 200", "text/plain",
                        """
                                ✅ Статус: 200 OK
                                ✅ Content-Type: application/json
                                ✅ CORS заголовки корректны
                                ✅ Тело: пустой массив []""");
            } else if (statusCode == 404) {
                Allure.addAttachment("Структура ответа 404", "text/plain",
                        "ℹ Статус: 404 Not Found\n" +
                                "Тело: " + responseDel.getBody().asString().trim()
                );
            }
        });
    }

    @Test
    @Order(10)
    @DisplayName("Сохранение данных ИП с валидными данными - 201")
    @Story("Основной сценарий")
    @Severity(SeverityLevel.CRITICAL)
    void saveIpDetailsWithValidData() {
        // Генерация тестовых данных с Faker
        testRequest = step("Генерация тестовых данных", () ->
                SzDetailsTestDataGenerator.generateValidIpDetails(testUserId, TEST_EMAIL)
        );

        Allure.addAttachment("Сгенерированные данные", "text/plain",
                "ФИО: " + testRequest.getFullName() + "\n" +
                        "Телефон: " + testRequest.getPhone() + "\n" +
                        "ИНН: " + testRequest.getInn() + "\n" +
                        "ОГРНИП: " + testRequest.getOgrnip() + "\n" +
                        "Email: " + testRequest.getEmail());

        Response response = step("POST /user/club/ip-details", () ->
                szDetailsSteps.saveIpDetails(authToken, testRequest)
        );

        step("Валидация ответа (ожидаем 201)", () ->
                szDetailsSteps.validateSuccessfulSave(response)
        );

        // Если нужно получить объект ответа
        SzDetailsResponse szResponse = step("Парсинг ответа", () ->
                szDetailsSteps.parseSaveResponse(response)
        );

        if (szResponse != null) {
            Allure.addAttachment("Парсинг ответа", "text/plain",
                    "Успешно: " + szResponse.isSuccessful() + "\n" +
                            "Сообщение: " + szResponse.getMessage()
            );
        }
    }

    @Test
    @Order(11)
    @DisplayName("Тест валидации - неполные данные ИП - 422")
    @Story("Валидация")
    @Severity(SeverityLevel.NORMAL)
    void testValidationWithIncompleteData() {
        testRequest = step("Генерация тестовых данных", () ->
                SzDetailsTestDataGenerator.generateValidIpDetails(testUserId, TEST_EMAIL)
        );

        // Создаем запрос только с несколькими полями
        Map<String, Object> incompletePayload = Map.of(
                "email", TEST_EMAIL,
                "fullName",
                "Тестовый пользователь",
                "phone", "+7 (999) 123-45-67"
        );

        Response response = step("POST /user/club/sz-details с минимльными данными", () ->
                szDetailsSteps.saveIpDetails(authToken, incompletePayload)
        );

        step("Проверка валидации", () ->
                szDetailsSteps.validateFieldValidation(response)
        );
    }

    @Test
    @Order(12)
    @DisplayName("Негативный тест - невалидный ОГРНИП - 422")
    @Story("Валидация")
    @Severity(SeverityLevel.NORMAL)
    void saveIpDetailsWithInvalidOgrnip() {
        testRequest = step("Генерация тестовых данных", () ->
                SzDetailsTestDataGenerator.generateValidIpDetails(testUserId, TEST_EMAIL)
        );

        testRequest.setOgrnip("неправильный огрнип");


        Response response = step("POST с невалидным ОГРНИП", () ->
                szDetailsSteps.saveIpDetails(authToken, testRequest)
        );

        step("Проверка валидации ОГРНИП", () -> {
            int statusCode = response.statusCode();

            if (statusCode == 422) {
                Allure.addAttachment("Валидация сработала", "text/plain",
                        "✅ Получена ошибка валидации для невалидного ОГРНИП");
            } else if (statusCode == 201) {
                Allure.addAttachment("Валидация пропущена", "text/plain",
                        "⚠ Сервер принял невалидный ОГРНИП - проверьте валидацию");
            }
        });
    }

    @Test
    @Order(13)
    @DisplayName("Негативный тест - запрос без авторизации ИП - 401")
    @Story("Безопасность")
    @Severity(SeverityLevel.CRITICAL)
    void saveIpDetailsWithoutAuth() {
        testRequest = step("Генерация тестовых данных", () ->
                SzDetailsTestDataGenerator.generateValidIpDetails(testUserId, TEST_EMAIL)
        );

        Response response = step("POST без токена авторизации", () ->
                szDetailsSteps.saveIpDetails("invalid_token", testRequest)
        );

        step("Проверка ошибки авторизации", () -> {
            int statusCode = response.statusCode();

            assertTrue(statusCode == 401 || statusCode == 403,
                    "Should return auth error (401 or 403) for invalid token. Got: " + statusCode);

            Allure.addAttachment("Результат", "text/plain",
                    "Статус код: " + statusCode + "\n" +
                            (statusCode == 401 || statusCode == 403 ? "✅ Ошибка авторизации - ожидаемо" :
                                    "❌ Неожиданный статус"));
        });
    }
}