package ru.tutor.test.api.search;

import io.qameta.allure.*;
import io.qameta.allure.junit5.AllureJunit5;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tutor.config.TestConfig;
import ru.tutor.steps.AuthSteps;
import ru.tutor.steps.SearchSteps;

import java.util.List;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AllureJunit5.class)
@Epic("API Тесты пользователя")
@Feature("История поиска")
@DisplayName("Тесты эндпоинта POST /user/search/history")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SearchHistoryTest {

    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private final AuthSteps authSteps = new AuthSteps(BASE_URL);
    private final SearchSteps searchSteps = new SearchSteps(BASE_URL);
    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();

    private static String token;

    @BeforeEach
    void setUpAuth() {
        token = step("Логин пользователя", () ->
                authSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );
    }

    @Test
    @Order(1)
    @DisplayName("Успешное сохранение истории поиска с одним запросом")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("positive")
    void saveSearchHistorySingleQuery() {
        Response response = step("POST /user/search/history - один запрос", () ->
                searchSteps.saveSearchHistory(token, List.of("Питание"))
        );

        step("Валидация: статус 201, тело []", () ->
                searchSteps.validateSearchHistoryResponse(response));
    }

    @Test
    @Order(2)
    @DisplayName("Успешное сохранение поиска с несколькими запросами")
    @Severity(SeverityLevel.NORMAL)
    @Tag("positive")
    void saveSearchHistoryMultipleQueries() {
        Response response = step("POST /user/search/history - несколько запросов", () ->
                searchSteps.saveSearchHistory(token, List.of("Питание", "дневник питания", "ЗОЖ"))
        );

        step("Валидация: статус 201, тело []", ()->
                searchSteps.validateSearchHistoryResponse(response));
    }

    @Test
    @Order(3)
    @DisplayName("Сохранение истории поиска без токена авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void saveSearchHistoryWithoutAuth() {
        Response response = step("POST /user/search/history - без токена", () ->
                searchSteps.saveSearchHistory("", List.of("Питание"))
        );

        step("Проверка статуса 401", () ->
                assertEquals(401,  response.getStatusCode())
        );
    }

    @Test
    @Order(4)
    @DisplayName("Сохранение истории поиска с невалидным токеном")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void saveSearchHistoryWithInvalidToken() {
        Response response = step("POST /user/search/history - без токена", () ->
                searchSteps.saveSearchHistory("invalid_token", List.of("Питание"))
        );

        step("Проверка статуса 401", () ->
                assertEquals(401,  response.getStatusCode())
        );
    }

    @Test
    @Order(5)
    @DisplayName("Сохранение истории поиска с пустым queryList")
    @Severity(SeverityLevel.NORMAL)
    @Tag("edge-case")
    void saveSearchHistoryEmptyQueryList() {
        Response response = step("POST /user/search/history - пустой список", () ->
                searchSteps.saveSearchHistory(token, List.of())
        );

        step("Проверка допустимого статуса", () ->
                searchSteps.validateSearchHistoryResponse(response));
    }

    @Test
    @Order(6)
    @DisplayName("Сохранение истории поиска с несколькими пустыми queryList")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void saveSearchHistoryEmptyStrings() {
        Response response = step("POST /user/search/history - пустой список", () ->
                searchSteps.saveSearchHistory(token, List.of("", "", ""))
        );

        step("Проверка: 422 + ошибка валидации", () ->
                searchSteps.validateSearchHistoryValidationError(response));
    }

    // ==================== GET TESTS ====================

    @Test
    @Order(7)
    @DisplayName("GET: успешное получение истории поиска")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("positive")
    void getSearchHistorySuccess() {
        Response response = step("GET / user/search/history", () ->
                searchSteps.getSearchHistory(token)
        );
        step("Валидация ответа", () ->
                searchSteps.validateGetSearchHistoryResponse(response)
        );
    }

    @Test
    @Order(8)
    @DisplayName("GET: без токена авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void getSearchHistoryWithoutAuth() {
        Response response = step("GET /user/search/history - без токена", () ->
                searchSteps.getSearchHistory("")
        );
        step("Проверка статуса 401", () ->
                assertEquals(401,  response.getStatusCode())
        );
    }

    @Test
    @Order(9)
    @DisplayName("GET: невалидный токен")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void getSearchHistoryInvalidToken() {
        Response response = step("GET /user/search/history - невалдиный токен", () ->
                searchSteps.getSearchHistory("invalid_token")
        );
        step("Проверка статуса 401", () ->
                assertEquals(401,  response.getStatusCode())
        );
    }

    // ==================== INTEGRATION ====================

    @Test
    @Order(10)
    @DisplayName("Интеграция: POST сохранение -> GET проверка содержимого")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("positive")
    @Tag("integration")
    void postThenGetSearchHistory() {
        List<String> queries = List.of("Питание", "дневник питания");

        step("POST /user/search/history - сохранение", () ->
                searchSteps.saveSearchHistory(token, queries)
        );

        Response response = step("GET /user/search/history - получение", () ->
                searchSteps.getSearchHistory(token)
        );

        step("Проверка: GET возвращает сохранённые запросы", () ->
                searchSteps.validateSearchHistoryContains(response, queries)
        );
    }
}
