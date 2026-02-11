package ru.tutor.steps;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.tutor.api.ProductsApi;
import ru.tutor.model.ProductsResponse;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Collectors;

public class ProductsSteps {
    private final ProductsApi productsApi;

    public ProductsSteps(String baseUrl) {
        this.productsApi = new ProductsApi(baseUrl);
    }

    @Step("Получение списка продуктов")
    public Response addProduct(String authToken) {
        Response response = this.productsApi.getUserProducts(authToken);
        return response;
    }

    @Step("Валидация ответа списка продуктов")
    public List<ProductsResponse> validateProductsResponse(Response response) {
        response.then()
                .statusCode(200)
                .contentType(ContentType.JSON);

        return Arrays.asList(response.as(ProductsResponse[].class));
    }

    @Step("Проверка базовой структуры продуктов")
    public void validateProductsStructure(List<ProductsResponse> products) {
        assertNotNull(products, "Список продуктов не должен быть null");

        for (ProductsResponse product : products) {
            // Обязательные поля
            assertNotNull(product.getId(), "ID родукта не должен быть null");
            assertNotNull(product.getName(), "Название продукта не должен быть null");
            assertNotNull(product.getAvailable(), "Available е должен быть null");
            assertNotNull(product.getCategoryAsString(), "Категория не должна быть null");

            // Валидация значений
            assertFalse(product.getId().trim().isEmpty(), "ID продукта не должен быть пустым");
            assertFalse(product.getName().trim().isEmpty(), "Название не должно быть пустым");
        }
    }

    @Step("Фильтрация продуктов по категории")
    public List<ProductsResponse> filterByCategory(List<ProductsResponse> products, String category) {
        return products.stream()
                .filter(product -> category.equals(product.getCategoryAsString()))
                .collect(Collectors.toList());
    }

    @Step("Поиск продукта по ID")
    public ProductsResponse findProductById(List<ProductsResponse> products, String id) {
        return products.stream()
                .filter(product -> id.equals(product.getId()))
                .findFirst()
                .orElse(null);
    }

    @Step("Генерация отчёта по продуктам")
    public void generateProductsReport(List<ProductsResponse> products) {
        if (products.isEmpty()) {
            Allure.addAttachment("Отчет", "text/plain", "Список продуктов пуст");
            return;
        }

        StringBuilder report = new StringBuilder();
        report.append("🛍️ ОТЧЕТ ПО ПРОДУКТАМ\n");
        report.append("==================\n\n");

        long totalProducts = products.size();
        long availableProducts = products.stream()
                .filter(p -> Boolean.TRUE.equals(p.getAvailable()))
                .count();

        report.append("📊 ОБЩАЯ СТАТИСТИКА:\n");
        report.append(String.format("• Всего продуктов: %d\n", totalProducts));
        report.append(String.format("• Доступно: %d (%.1f%%)\n",
                availableProducts, (availableProducts * 100.0) / totalProducts));

        // Статистика по категориям

    }

    @Step("Отладочный вывод структуры ответа")
    public void debugResponseStructure(Response response) {
        String responseBody = response.getBody().asString();

        // Берем только начало ответа для анализа
        int previewLength = Math.min(1000, responseBody.length());
        String preview = responseBody.substring(0, previewLength);

        Allure.addAttachment("Сырой ответ (первые 1000 символов)", "text/plain", preview);

        // Пытаемся распарсить как JSON для структуры
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(responseBody);

            if (root.isArray() && root.size() > 0) {
                com.fasterxml.jackson.databind.JsonNode firstProduct = root.get(0);

                StringBuilder structure = new StringBuilder();
                structure.append("📋 СТРУКТУРА ПЕРВОГО ПРОДУКТА:\n");
                structure.append("=============================\n\n");

                firstProduct.fieldNames().forEachRemaining(fieldName -> {
                    com.fasterxml.jackson.databind.JsonNode field = firstProduct.get(fieldName);
                    String fieldType = getJsonNodeType(field);
                    structure.append(String.format("%s: %s", fieldName, fieldType));

                    // Добавляем пример значения для простых полей
                    if (field.isValueNode()) {
                        structure.append(String.format(" = %s", field.asText()));
                    }
                    structure.append("\n");
                });

                Allure.addAttachment("Структура JSON", "text/plain", structure.toString());
            }
        } catch (Exception e) {
            Allure.addAttachment("Ошибка парсинга JSON", "text/plain",
                    "Не удалось распарсить JSON: " + e.getMessage());
        }
    }

    private String getJsonNodeType(com.fasterxml.jackson.databind.JsonNode node) {
        if (node.isObject()) return "OBJECT";
        if (node.isArray()) return "ARRAY";
        if (node.isTextual()) return "STRING";
        if (node.isNumber()) return "NUMBER";
        if (node.isBoolean()) return "BOOLEAN";
        if (node.isNull()) return "NULL";
        return "UNKNOWN";
    }
}