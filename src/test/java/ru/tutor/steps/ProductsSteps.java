package ru.tutor.steps;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.tutor.api.UserProductsApi;
import ru.tutor.base.BaseApiSteps;
import ru.tutor.model.ProductsResponse;
import ru.tutor.utils.AllureAttachments;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Collectors;

public class ProductsSteps extends BaseApiSteps {
    private final UserProductsApi productsApi;

    // Хранилище для сохранения ID продуктов
    private static final Map<String, Object> productCache = new HashMap<>();

    public ProductsSteps(String baseUrl) {
        super(baseUrl);
        this.productsApi = new UserProductsApi(baseUrl);
    }

    public static ProductsSteps withBaseUrl(String baseUrl) {
        return new ProductsSteps(baseUrl);
    }

    @Step("Получение списка продуктов")
    public Response getUserProducts(String authToken) {
        return this.productsApi.getUserProducts(authToken);
    }

    @Step("Получение списка достижений для продукта")
    public Response getUserAchievements(String authToken) {
        return this.productsApi.getUserAchievements(authToken);
    }

    @Step("Получение списка всех достижений")
    public Response getAchievementsProductsId(String authToken, String saveProductId) {
        return this.productsApi.getAchievementsProductById(authToken, saveProductId);
    }

    @Step("GET /user/achievements/last - последние достижения")
    public Response getAchievementsLast(String authToken) {
        Response response = productsApi.getAchievementsLast(authToken);
        AllureAttachments.attachJson("Attached files", response.getBody().asString());
        return response;
    }

    @Step("Получение всех продуктов")
    public Response getAllProducts(String authToken) {
        return this.productsApi.getAllProducts(authToken);
    }

    @Step("Валидация ответа списка продуктов")
    public List<ProductsResponse> validateProductsResponse(Response response) {
        assertSuccessStatusCode(response);
        assertContentTypeJson(response);

        return Arrays.asList(response.as(ProductsResponse[].class));
    }

    // ==================== МЕТОДЫ ДЛЯ ПОЛУЧЕНИЯ ID ====================

    /**
     * Получение ID четвёртого продукта в списке
     */
    @Step("Получение ID продукта")
    public String getProductId(List<ProductsResponse> products) {
        List<ProductsResponse> nonMini = products.stream()
                .filter(p -> !p.isMiniCourse())
                        .collect(Collectors.toList());
        assertFalse(products.isEmpty(), "Список продуктов не должен быть пустым");
        String productId = products.get(2).getProductId();
        Allure.addAttachment("Four Product ID", productId);
        return productId;
    }

    /**
     * Получение ID первого числового продукта (не onbording)
     */
    @Step("Получение ID первого числового продукта")
    public String getFirstNumericOrderNumber(List<ProductsResponse> products) {
        ProductsResponse product = products.stream()
                .filter(p -> !p.isOnboardingProduct())
                .filter(p -> !p.isPremiumProduct())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Нет числовых продуктов"));

        String productId = product.getId();
        Allure.addAttachment("First Numeric orderNumber", productId);
        return productId;
    }

    @Step("Получение деталей продукта по сохранённому ID")
    public Response getProductDetailsById(String authToken, String savedProductId) {
        Allure.addAttachment("Используем ID продукта", savedProductId);
        return this.productsApi.getProductById(authToken, savedProductId);
    }

    @Step("GET /user/products/{orderNumber}/attached-files - получение вложений курса")
    public Response getAttachedFiles(String authToken, String orderNumber) {
        Allure.addAttachment("Order Number", orderNumber);
        Response response = productsApi.getAttachedFiles(authToken, orderNumber);
        AllureAttachments.attachJson("Attached files", response.getBody().asString());
        return response;
    }

    @Step("Валидация ответа списка вложений")
    public void validateAttachedFilesResponse(Response response) {
        assertSuccessStatusCode(response);
        assertContentTypeJson(response);
        String body = response.getBody().asString();
        assertNotNull(body, "Тело не должно быть null");
    }

    @Step("Валидация ответа с деталями продукта")
    public void validateProductDetailsResponse(Response response) {
        assertSuccessStatusCode(response);
        assertContentTypeJson(response);
    }

    @Step("Валидация ответа списка достижений")
    public void validateAchievements(Response response) {
        assertSuccessStatusCode(response);
        assertContentTypeJson(response);
        String body = response.getBody().asString();
        assertNotNull(body, "Тело не должно быть null");
    }

    @Step("Получение модуля продукта по сохраненному ID продукта и номеру модуля")
    public Response getProductModuleById(String authToken, String savedProductValue, int moduleNumber) {
        Allure.addAttachment("Используемый ID продукта", savedProductValue);
        Allure.addAttachment("Номер модуля", String.valueOf(moduleNumber));
        return this.productsApi.getProductModule(authToken, savedProductValue, moduleNumber);
    }

    @Step("Отметка модуля как просмотренного")
    public Response markModuleWatched(String authToken, String productId, int moduleNumber, int timeline) {
        Allure.addAttachment("Order Number",  productId);
        Allure.addAttachment("Module Number", String.valueOf(moduleNumber));
        Allure.addAttachment("Timeline", String.valueOf(timeline));

        Response response = productsApi.postProductCreate(authToken, productId, moduleNumber, timeline);

        AllureAttachments.attachJson("Mark Watched Response", response.getBody().asString());
        return response;
    }

    @Step("Отметка модуля как завершенного (orderNumber: {orderNumber}, moduleNumber: {moduleNumber})")
    public Response completeModule(String authToken, String productId, int moduleNumber) {
        Allure.addAttachment("Order Number",  productId);
        Allure.addAttachment("Module Number", String.valueOf(moduleNumber));

        Response response = productsApi.putProductUpdate(authToken, productId, moduleNumber);

        AllureAttachments.attachJson("Complete Module Response", response.getBody().asString());
        return response;
    }

    @Step("Проверка базовой структуры продуктов")
    public void validateProductsStructure(List<ProductsResponse> products) {
        assertNotNull(products, "Список продуктов не должен быть null");

        for (ProductsResponse product : products) {

            if (!product.isMiniCourse()) {
                assertNotNull(product.getId(), "ID продукта не должен быть null");
                assertFalse(product.getId().trim().isEmpty(), "ID продукта не должен быть пустым");
            }
            // Обязательные поля

            assertNotNull(product.getName(), "Название продукта не должен быть null");
            assertNotNull(product.getAvailable(), "Available е должен быть null");
            assertNotNull(product.getCategoryAsString(), "Категория не должна быть null");

            // Валидация значений
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

        Allure.addAttachment("Product Report", report.toString());

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

            if (root.isArray() && !root.isEmpty()) {
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

    @Step("Список избранных продуктов")
    public Response favoriteProduct(String authToken) {
        return this.productsApi.getFavoriteProducts(authToken);
    }

    @Step("Добавление продуктов в избранное")
    public Response postFavoriteProducts(String authToken, String productId) {
        return this.productsApi.postFavoriteProducts(authToken, productId);
    }

    @Step("Премьеры")
    public Response getPremiersProducts(String authToken) {
        return this.productsApi.productsPremiers(authToken);
    }

    @Step("Топ 10")
    public Response getProductTop10(String authToken) {
        return this.productsApi.productTop10(authToken);
    }

    @Step("Сейчас смотрят")
    public Response getProductWatchingNow(String authToken) {
        return this.productsApi.productWatchingNow(authToken);
    }

    @Step("Категории платформы")
    public Response getCategories(String authToken) {
        return this.productsApi.getCategories(authToken);
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