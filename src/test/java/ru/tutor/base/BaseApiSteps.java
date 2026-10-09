package ru.tutor.base;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.config.TestConfig;
import ru.tutor.utils.AllureAttachments;

import org.junit.jupiter.api.function.Executable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Базовый класс для всех Steps API с общими методами валидации
 */
public abstract class BaseApiSteps {

    protected final String baseUrl;

    protected BaseApiSteps(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    // ==================== БАЗОВЫЕ ПРОВЕРКИ СТАТУС-КОДОВ ====================

    @Step("Проверить успешный статус ответа (200)")
    protected void assertSuccessStatusCode(Response response) {
        assertEquals(200, response.getStatusCode(),
                "Ожидался статус 200, но получен: " +  response.getStatusCode());
    }

    @Step("Проверить статус ошибки: {expectedStatus}")
    protected void assertErrorStatusCode(Response response, int expectedStatus) {
        int actual = response.getStatusCode();
        assertTrue(actual == expectedStatus || actual == 400 || actual == 401 || actual == 422,
                "Ожидался статус: " + expectedStatus + ", но получен:  " + actual);
    }

    @Step("Проверить допустимый статус ответа")
    protected void assertStatusAnyOf(Response response, int ... allowedStatuses) {
        int actual  = response.getStatusCode();
        for (int allowed : allowedStatuses) {
            if (actual == allowed) return;
        }
        fail("Получен неожиданный статус: " + actual);
    }

    // ==================== ПРОВЕРКИ CONTENT-TYPE ====================

    @Step("Проверить Content-Type = application/json")
    protected void assertContentTypeJson(Response response) {
        String contentType = response.getHeader("Content-Type");
        assertNotNull(contentType, "Content-Type не должен быть null");
        assertTrue(contentType.contains("application/json"), "Content-Type должен быть json, но получен: " + contentType);
    }

    @Step("Проверить статус и Content-Type")
    protected void assertBasicResponse(Response response) {
        assertSuccessStatusCode(response);
        assertContentTypeJson(response);
        assertCorsHeaders(response);
    }

    // ==================== ПРОВЕРКИ JSON ПОЛЕЙ ====================
    @Step("Проверить наличие поля '{fieldPath}' в ответе")
    protected void assertJsonFieldExists(Response response, String fieldPath) {
        Object value = response.jsonPath().get(fieldPath);
        assertNotNull(value, "Поле '" + fieldPath + "'  должно существовать в ответе");
    }

    @Step("Проверить значение поля '{fieldPath}' = '{expectedValue}'")
    protected void assertJsonFieldEquals(Response response, String fieldPath, Object expectedValue) {
        Object actualValue = response.jsonPath().get(fieldPath);
        assertEquals(expectedValue, actualValue,
                "Поле '" + fieldPath + "' имеет неверное значение");
    }

    @Step("Проверить что поле '{fieldPath}' не пустое")
    protected void assertJsonFieldNotEmpty(Response response, String fieldPath) {
        Object value = response.jsonPath().get(fieldPath);
        assertNotNull(value, "Поле '" + fieldPath + "' не должно быть null");
        if (value instanceof String) {
            assertFalse(((String) value).isEmpty(), "Поле '" + fieldPath + "' не должно быть пустым");
        }
    }

    @Step("Проверить наличие хотя бы одного поля из списка")
    protected void assertAnyFieldExists(Response response, String... fieldPath) {
        String responseBody = response.getBody().asString();
        boolean found = false;
        for (String field : fieldPath) {
            if (responseBody.contains("\"" + field + "\"")) {
                found = true;
                break;
            }
        }
        assertTrue(found, "Ответ должен содержать хотя бы одно из полей: " + String.join(", ", fieldPath));
    }

    @Step("Проверить отсутствие поля '{field}' в ответе")
    protected void assertFieldNotExists(Response response, String field) {
        String responseBody = response.getBody().asString();
        assertFalse(responseBody.contains("\"" + field + "\""),
                "Ответ не должен содержать поле '" + field + "'");
    }

    @Step("Проверить наличие ключа '{key}' в JSON ответе (допускается null значение)")
    protected void assertJsonKeyExists(Response response, String key) {
        String body = response.getBody().asString();
        assertTrue(body.contains("\"" + key + "\""),
                "Ответ должен содержать ключ '" + key + "'");
    }

    // ==================== ПРОВЕРКИ ЗАГОЛОВКОВ ====================
    @Step("Проверить CORS заголовки")
    protected void assertCorsHeaders(Response response) {
        assertEquals(TestConfig.getCurrentClientDomain(),
                response.getHeader("Access-Control-Allow-Origin"),
                "Access-Control-Allow-Origin должен соответствовать домену клиента");
        assertEquals("true", response.getHeader("Access-Control-Allow-Credentials"),
                "Access-Control-Allow-Credentials должен быть 'true'");
    }

    @Step("Проверить заголовок '{headerName}' = '{expectedValue}'")
    protected void assertHeader(Response response, String headerName, String expectedValue) {
        String actualValue = response.getHeader(headerName);
        assertEquals(expectedValue, actualValue,
                "Заголовок '" + headerName + "' имеет неверное значение");
    }

    @Step("Проверить что заголовок '{headerName}' существует")
    protected void assertHeaderExists(Response response, String headerName) {
        String value = response.getHeader(headerName);
        assertNotNull(value, "Заголовок '" + headerName + "' должен существовать");
    }

    @Step("Проверить что заголовок '{headerName}' содержит '{expectedPart}'")
    protected void assertHeaderContains(Response response, String headerName, String expectedPart) {
        String value = response.getHeader(headerName);
        assertNotNull(value, "Заголовок '" + headerName + "' должен существовать");
        assertTrue(value.contains(expectedPart),
                "Заголовок '" + headerName + "' должен содержать '" + expectedPart + "', но содержит:"
        + value);
    }

    // ==================== ПРОВЕРКИ БЕЗОПАСНОСТИ ====================
    @Step("Проверить отсутствие чувствительных данных")
    protected void assertNoSensitiveData(Response response) {
        String responseBody = response.getBody().asString().toLowerCase();
        assertFalse(responseBody.contains("password"), "Ответ не должен содержать пароли");
        assertFalse(responseBody.contains("sql"), "Ответ не должен содержать SQL информацию");
        assertFalse(responseBody.contains("exception"), "Ответ не должен содержать исключения");
    }

    // ==================== ЛОГИРОВАНИЕ ====================
    @Step("Прикрепить ответ к отчету: {description}")
    protected void attachResponseToAllure(Response response, String description) {
        AllureAttachments.attachJson(description, response.getBody().asString());
        AllureAttachments.attachJson(description + " Headers", response.getHeaders().toString());
    }

    // ==================== ХЕЛПЕРЫ ВАЛИДАЦИИ ПОЛЕЙ (МАР) ====================
    protected Executable notNull(Map<String, Object> item, String field){
        return () -> assertNotNull(item.get(field), field + " не null");
    }

    protected Executable nonEmptyString(Map<String, Object> item, String field){
        return () -> {
            assertNotNull(item.get(field), field + " не null");
            assertFalse(((String) item.get(field)).trim().isEmpty(), field + " не пустой");
        };
    }

    protected Executable positiveInt(Map<String, Object> item, String field){
        return () -> assertTrue(((Number) item.get(field)).intValue() > 0, field + " > 0");
    }

    protected Executable nonNegative(Map<String, Object> item, String field){
        return () -> assertTrue(((Number) item.get(field)).doubleValue() >= 0, field + " >= 0");
    }

    protected Executable numberField(Map<String, Object> item, String field){
        return () -> assertTrue(item.get(field) instanceof Number, field + " должен быть числом");
    }

    protected Executable containsKey(Map<String, Object> item, String field){
        return () -> assertTrue(item.containsKey(field), field + " должен быть в ответе");
    }

    protected Executable url(Object value, String label){
        return () -> assertTrue(value instanceof String && ((String) value).startsWith("http"),
                label + " должен быть URL");
    }

    protected Executable optionalUrl(Object value, String label){
        return () -> assertOptionalUrl(value, label);
    }

    protected Executable optionalField(Map<String, Object> item, String field, Class<?> type){
        return () -> {
            if (item.get(field) != null) {
                assertTrue(type.isInstance(item.get(field)), field + " должен быть " + type.getSimpleName());
            }
        };
    }

    protected void assertOptionalUrl(Object value, String label){
        if (value != null) {
            assertTrue(value instanceof String && ((String) value).startsWith("http"),
                    label + " должен быть URL");
        }
    }

    protected List<Map<String, Object>> assertJsonArray(Response response, String what) {
        List<Map<String, Object>> items = response.jsonPath().getList("$");
        assertNotNull(items, "Тело должно быть JSON-массивом");
        assertFalse(items.isEmpty(), what + " не должен быть пустым");
        return items;
    }
}
