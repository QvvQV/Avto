package ru.tutor.test.api.search;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import ru.tutor.config.TestConfig;
import ru.tutor.steps.account.AuthSteps;
import ru.tutor.steps.catalog.SearchSteps;

import java.util.List;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;
import ru.tutor.constants.Tags;

@Epic("API Тесты пользователя")
@Feature("История поиска")
@DisplayName("Тесты эндпоинта POST /user/search/history")
@Tag(Tags.INTEGRATION)
@Tag(Tags.API)
public class SearchHistoryTest {

    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private final AuthSteps authSteps = new AuthSteps(BASE_URL);
    private final SearchSteps searchSteps = new SearchSteps(BASE_URL);
    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();

    private static String token;

    @Test
    @DisplayName("Успешное сохранение истории поиска с одним запросом")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    void saveSearchHistorySingleQuery() {
        token = step("Логин пользователя", () ->
                authSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );
        List<String> queries = List.of("Питание");
        Response response = step("POST /user/search/history - один запрос", () ->
                searchSteps.saveSearchHistory(token, queries)
        );

        step("Валидация: статус 201, тело []", () ->
                searchSteps.validateSearchHistoryResponse(response));
        step("GET возвращает сохранённый запрос", () -> {
            Response history = searchSteps.getSearchHistory(token);
            searchSteps.validateSearchHistoryContains(history, queries);
        });
    }

    @Test
    @DisplayName("Успешное сохранение поиска с несколькими запросами")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.POSITIVE)
    void saveSearchHistoryMultipleQueries() {
        token = step("Логин пользователя", () ->
                authSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );
        List<String> queries = List.of("Питание", "дневник питания", "ЗОЖ");
        Response response = step("POST /user/search/history - несколько запросов", () ->
                searchSteps.saveSearchHistory(token, queries)
        );

        step("Валидация: статус 201, тело []", ()->
                searchSteps.validateSearchHistoryResponse(response));
        step("GET возвращает сохранённые запросы", () -> {
            Response history = searchSteps.getSearchHistory(token);
            searchSteps.validateSearchHistoryContains(history, queries);
        });
    }

    @Test
    @DisplayName("Сохранение истории поиска без токена авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void saveSearchHistoryWithoutAuth() {
        Response response = step("POST /user/search/history - без токена", () ->
                searchSteps.saveSearchHistory("", List.of("Питание"))
        );

        step("Проверка статуса 401", () ->
                searchSteps.assertUnauthorizedWithMessage(response, "JWT Token not found"));
    }

    @Test
    @DisplayName("Сохранение истории поиска с невалидным токеном")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void saveSearchHistoryWithInvalidToken() {
        Response response = step("POST /user/search/history - без токена", () ->
                searchSteps.saveSearchHistory("invalid_token", List.of("Питание"))
        );

        step("Проверка статуса 401", () ->
                searchSteps.assertUnauthorizedWithMessage(response, "Invalid JWT Token"));
    }

    @Test
    @DisplayName("Сохранение истории поиска с пустым queryList")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.CONTRACT)
    void saveSearchHistoryEmptyQueryList() {
        token = step("Логин пользователя", () ->
                authSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );
        Response response = step("POST /user/search/history - пустой список", () ->
                searchSteps.saveSearchHistory(token, List.of())
        );

        step("Проверка допустимого статуса", () ->
                searchSteps.validateSearchHistoryResponse(response));
    }

    @Test
    @DisplayName("Сохранение истории поиска с несколькими пустыми queryList")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void saveSearchHistoryEmptyStrings() {
        token = step("Логин пользователя", () ->
                authSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );
        Response response = step("POST /user/search/history - пустой список", () ->
                searchSteps.saveSearchHistory(token, List.of("", "", ""))
        );

        step("Проверка: 422 + ошибка валидации", () ->
                searchSteps.validateSearchHistoryValidationError(response));
    }

    // ==================== GET TESTS ====================

    @Test
    @DisplayName("GET: успешное получение истории поиска")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    @Tag(Tags.CONTRACT)
    void getSearchHistorySuccess() {
        token = step("Логин пользователя", () ->
                authSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );
        Response response = step("GET /user/search/history", () ->
                searchSteps.getSearchHistory(token)
        );
        step("Валидация ответа", () ->
                searchSteps.validateGetSearchHistoryResponse(response)
        );
    }

    @Test
    @DisplayName("GET: без токена авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void getSearchHistoryWithoutAuth() {
        Response response = step("GET /user/search/history - без токена", () ->
                searchSteps.getSearchHistory("")
        );
        step("Проверка статуса 401", () ->
                searchSteps.assertUnauthorizedWithMessage(response, "JWT Token not found")
        );
    }

    @Test
    @DisplayName("GET: невалидный токен")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void getSearchHistoryInvalidToken() {
        Response response = step("GET /user/search/history - невалдиный токен", () ->
                searchSteps.getSearchHistory("invalid_token")
        );
        step("Проверка статуса 401", () ->
                searchSteps.assertUnauthorizedWithMessage(response, "Invalid JWT Token")
        );
    }
}
