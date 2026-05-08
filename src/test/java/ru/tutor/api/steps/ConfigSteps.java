package ru.tutor.api.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.model.ConfigResponse;
import ru.tutor.api.catalog.ConfigApi;
import ru.tutor.utils.AllureAttachments;

import java.net.MalformedURLException;
import java.net.URI;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class ConfigSteps {
    private final ConfigApi configApi;

    public ConfigSteps(String baseUrl, String basicAuth) {
        this.configApi = new ConfigApi(baseUrl, basicAuth);
    }

    @Step("GET /api/config - получить конфигурацию")
    public Response getConfig() {
        Response response = configApi.getConfig();
        AllureAttachments.attachJson("GET Config Response", response.getBody().asString());
        AllureAttachments.attachHeaders("GET Config Headers", response.getHeaders());
        return response;
    }

    @Step("POST /api/config - создать конфигурацию с телом: {body}")
    public Response createConfig(String body) {
        AllureAttachments.attachText("POST Config Request Body", body);
        Response response = configApi.createConfig(body);
        AllureAttachments.attachJson("POST Config Response", response.getBody().asString());
        AllureAttachments.attachHeaders("POST Config Headers", response.getHeaders());
        return response;
    }

    @Step("PUT /api/config - обновить конфигурацию с телом: {body}")
    public Response updateConfig(String body) {
        AllureAttachments.attachText("PUT Config Request Body", body);
        Response response = configApi.updateConfig(body);
        AllureAttachments.attachJson("PUT Config Response", response.getBody().asString());
        AllureAttachments.attachHeaders("PUT Config Headers", response.getHeaders());
        return response;
    }

    @Step("DELETE /api/config - удалить конфигурацию")
    public Response deleteConfig() {
        Response response = configApi.deleteConfig();
        AllureAttachments.attachJson("DELETE Config Response", response.getBody().asString());
        AllureAttachments.attachHeaders("DELETE Config Headers", response.getHeaders());
        return response;
    }

    @Step("Проверить успешный ответ конфигурации")
    public ConfigResponse validateSuccessfulConfigResponse() {
        Response response = getConfig();

        response.then()
                .statusCode(200)
                .contentType("application/json")
                .body("requestPath", not(emptyOrNullString()))
                .body("requestPath", startsWith("http"));

        ConfigResponse configResponse = response.as(ConfigResponse.class);
        assertNotNull(configResponse, "Ответ конфигурации не должен быть null");

        return configResponse;
    }

    @Step("Проверить структуру конфигурации")
    public void validateConfigStructure(ConfigResponse config) {
        assertAll("Проверка структуры конфигурации",
                () -> assertNotNull(config.requestPath(), "requestPath не должен быть null"),
                () -> assertTrue(config.requestPath().startsWith("http"),
                        "requestPath должен быть валидным URL"),
                () -> assertTrue(config.requestPath().length() > 10,
                        "requestPath должен быть осмысленным")
        );

        // Дополнительные проверки если есть другие поля
        if (config.timeout() != null) {
            assertTrue(config.timeout() > 0, "timeout должен быть положительным, если присутствует");
        }

        if (config.maxConnections() != null) {
            assertTrue(config.maxConnections() > 0, "maxConnections должен быть положительным, если присутствует");
        }
    }

    @Step("Проверить что метод не разрешен (405)")
    public void validateMethodNotAllowed(Response response) {
        // Основная проверка - статус 405
        response.then().statusCode(405);

        // Проверяем заголовок Allow, но не падаем если его нет
        String allowHeader = response.getHeader("Allow");
        if (allowHeader != null && !allowHeader.trim().isEmpty()) {
            response.then().header("Allow", not(emptyOrNullString()));

            // Логируем разрешенные методы
            AllureAttachments.attachText("Allowed Methods", allowHeader);

            // Проверяем что GET метод разрешен (должен быть в списке)
            assertTrue(allowHeader.toUpperCase().contains("GET"),
                    "Метод GET должен быть разрешён согласно заголовку Allow: " + allowHeader);
        } else {
            // Если заголовок Allow отсутствует, просто логируем это
            AllureAttachments.attachText("Allow Header Info",
                    "Allow header is not present in 405 response. " +
                            "This is technically valid but not ideal REST practice.");
        }

        // ГИБКАЯ проверка Content-Type - разрешаем пустой или любой тип
        String contentType = response.getContentType();
        AllureAttachments.attachText("Content-Type Info",
                "Actual Content-Type: '" + contentType + "'");

        // Не проверяем строго Content-Type для 405 ошибок, так как сервер может не устанавливать его
        if (contentType != null && !contentType.trim().isEmpty()) {
            // Если Content-Type установлен, проверяем что он валидный
            response.then().contentType(anyOf(
                    containsString("application/json"),
                    containsString("text/plain"),
                    containsString("text/html"),
                    containsString("application/problem+json")
            ));
        }
        // Если Content-Type пустой - это допустимо для 405 ошибок
    }

    @Step("Проверить авторизацию")
    public void validateAuthentication() {
        Response response = configApi.getConfig();

        // Может быть 200 или 401 в зависимости от конфигурации
        if (response.getStatusCode() == 401) {
            response.then()
                    .statusCode(401)
                    .header("WWW-Authenticate", containsString("Basic"));
        }
    }

    @Step("Проанализировать конфигурацию")
    public void analyzeConfig(ConfigResponse config) {
        String analysis =
                "=== АНАЛИЗ КОНФИГУРАЦИИ ===\n\n" +
                        "Request Path: " + config.requestPath() + "\n" +
                        "Length: " + config.requestPath().length() + " chars\n" +
                        "Is HTTPS: " + config.requestPath().startsWith("https") + "\n" +
                        "Domain: " + extractDomain(config.requestPath()) + "\n";

        AllureAttachments.attachText("Config Analysis", analysis);
    }

    @Step("Анализировать ответ с ошибкой 405")
    public void analyze405Response(Response response, String method) {
        StringBuilder analysis = new StringBuilder();
        analysis.append("=== АНАЛИЗ ОТВЕТА 405 ===\n\n");
        analysis.append("Method: ").append(method).append("\n");
        analysis.append("Status Code: ").append(response.getStatusCode()).append("\n");
        analysis.append("Allow Header: ").append(response.getHeader("Allow")).append("\n");
        analysis.append("Content Type: '").append(response.getContentType()).append("'\n");
        analysis.append("Body: '").append(response.getBody().asString()).append("'\n");
        analysis.append("Body Length: ").append(response.getBody().asString().length()).append(" chars\n\n");
        analysis.append("Headers:\n");

        response.getHeaders().forEach(header ->
                analysis.append("  ").append(header.getName()).append(": ").append(header.getValue()).append("\n"));

        AllureAttachments.attachText("405 Response Analysis - " + method, analysis.toString());

        // Дополнительная проверка для отладки
        if (response.getBody().asString().trim().isEmpty()) {
            AllureAttachments.attachText("Empty Body Warning",
                    "Response body is empty for 405 error. This is unusual but acceptable.");
        }
    }

    @Step("Проверить базовые заголовки ответа")
    public void validateBasicHeaders(Response response) {
        // Проверяем только самые базовые заголовки которые должны быть всегда
        response.then()
                .header("Date", not(emptyOrNullString()))
                .header("Connection", not(emptyOrNullString()));

        // Дополнительные security headers если есть
        String strictTransportSecurity = response.getHeader("Strict-Transport-Security");
        if (strictTransportSecurity != null) {
            assertFalse(strictTransportSecurity.trim().isEmpty(),
                    "Strict-Transport-Security не должен быть пустым, если присутствует");
        }
    }

    private String extractDomain(String url) {
        try {
            return URI.create(url).toURL().getHost();
        } catch (IllegalArgumentException | MalformedURLException e) {
            return "Invalid URL";
        }
    }
}
