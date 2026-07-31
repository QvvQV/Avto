package ru.tutor.steps;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
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
        assertBasicResponse(response);

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
     * Получение ID первого числового продукта (не onboarding)
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
        assertBasicResponse(response);
        String body = response.getBody().asString();
        assertNotNull(body, "Тело не должно быть null");
    }

    @Step("Валидация ответа с деталями продукта")
    public void validateProductDetailsResponse(Response response) {
        assertBasicResponse(response);
    }

    private void validateAchievementItem(Map<String, Object> item, int index, boolean idNullable) {
        assertAll("Достижение [" + index + "]",
                () -> {if (idNullable) {
                    assertTrue(item.containsKey("id"),
                            "ключ id должен присутствовать");
                } else {
                    assertTrue(((Number) item.get("id")).intValue() > 0, "id > 0");
                }
        },
                () -> assertTrue(((Number) item.get("productId")).intValue() > 0, "productId > 0"),
                () -> assertNotNull(item.get("name"), "name не null"),
                () -> assertNotNull(item.get("description"), "description не null"),
                () -> assertTrue(((Number) item.get("score")).intValue() >= 0, "score >= 0"),
                () -> assertNotNull(item.get("isNew"), "isNew не null"),
                () -> assertNotNull(item.get("progress"), "progress не null"),
                () -> assertTrue(((Number) item.get("daysNeeded")).intValue() > 0, "daysNeeded > 0"),
                () -> assertTrue(List.of(1, 2, 3, 4).contains(item.get("type"))),
                () -> assertTrue(((Number) item.get("category")).intValue() > 0, "category > 0")
                );
    }

    @Step("Валидация ответа списка достижений")
    public void validateAchievements(Response response) {
        assertBasicResponse(response);

        List<Map<String, Object>> progress = response.jsonPath().getList("game_progress");
        assertNotNull(progress, "game_progress не должен быть null");
        assertFalse(progress.isEmpty(), "game_progress не должен быть пуст");

        for (int i = 0; i < progress.size(); i++)
            validateAchievementItem(progress.get(i), i, false);

        Map<String, Object> task = response.jsonPath().getMap("game_task");
        assertNotNull(task, "game_task не должен быть null");
        assertTrue(task.containsKey("day"), "game_task должен содержать day");
        assertTrue(task.containsKey("week"), "game_task должен содержать week");
        assertTrue(task.containsKey("special"), "game_task должен содержать special");
    }

    @Step("Валидация ответа списка достижений пользователя")
    public void validateUserAchievementsResponse(Response response) {
        assertBasicResponse(response);

        //Summary поля
        assertTrue(response.jsonPath().getInt("total") >= 0);
        assertTrue(response.jsonPath().getInt("totalLimit") >= 0);
        assertTrue(response.jsonPath().getInt("common") >= 0);
        assertTrue(response.jsonPath().getInt("commonLimit") >= 0);
        // professions могут быть 0
        assertTrue(response.jsonPath().getObject("professions", Integer.class) != null);
        assertTrue(response.jsonPath().getObject("professionsLimit", Integer.class) != null);

        // Массив достижений
        List<Map<String, Object>> achievements = response.jsonPath().getList("achievements");
        assertNotNull(achievements, "achievements не должен быть null");
        if (achievements.isEmpty()) return;

        for (int i = 0; i < achievements.size(); i++) {
            validateAchievementItem(achievements.get(i), i, true);
        }
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

            if (!product.isMiniCourse() && product.getId() != null) {
                assertNotNull(product.getId(), "ID продукта не должен быть null");
                assertFalse(product.getId().trim().isEmpty(), "ID продукта не должен быть пустым");
            }
            // Обязательные поля

            assertNotNull(product.getName(), "Название продукта не должен быть null");
            assertNotNull(product.getAvailable(), "Available не должен быть null");
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

    @Step("Список избранных продуктов")
    public Response favoriteProduct(String authToken) {
        return this.productsApi.getFavoriteProducts(authToken);
    }

    @Step("Добавление продуктов в избранное")
    public Response postFavoriteProducts(String authToken, String productId) {
        return this.productsApi.postFavoriteProducts(authToken, productId);
    }

    @Step("Удаление продуктов из избранных")
    public Response deleteFavoriteProducts(String authToken, String productId) {
        return this.productsApi.deleteFavoriteProducts(authToken, productId);
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

    private String getJsonNodeType(tools.jackson.databind.JsonNode node) {
        if (node.isObject()) return "OBJECT";
        if (node.isArray()) return "ARRAY";
        if (node.isTextual()) return "STRING";
        if (node.isNumber()) return "NUMBER";
        if (node.isBoolean()) return "BOOLEAN";
        if (node.isNull()) return "NULL";
        return "UNKNOWN";
    }

    @Step("Получение первого orderNumber из /user/products")
    public String fetchFirstOrderNumber(String authToken) {
        Response response = getUserProducts(authToken);
        List<ProductsResponse> products = validateProductsResponse(response);
        return getFirstNumericOrderNumber(products);
    }

    @Step("GET user/mini-courses")
    public Response getMiniCourses(String authToken) {
        return productsApi.getMiniCourses(authToken);
    }

    @Step("Валидация ответа мини-курсов")
    public void validateMiniCoursesResponse(Response response) {
        assertBasicResponse(response);
    }

    @Step("Проверка что все элементы - мини-курсы")
    public void validateAllAreMiniCourses(Response response) {
        List<Map <String, Object>> items = response.jsonPath().getList("items");
        assertNotNull(items, "Список мини-курсов не должен быть null");
        assertFalse(items.isEmpty(), "Список мини-курсов не должен быть пуст");

        for (int i = 0; i < items.size(); i++) {
            Map <String, Object> item = items.get(i);
            assertAll("Элемент [" + i + "] «" + item.get("name") + "»",
                    () -> assertTrue(((Number) item.get("id")).intValue() > 0,
                            "ID должен быть > 0"),
                    () -> assertNotNull(item.get("name"),
                            "Название не должно быть null"),
                    () -> assertFalse(((String) item.get("name")).trim().isEmpty(),
                            "Название не должно быть пустым"),
                    () ->  assertTrue(((Number) item.get("duration")).doubleValue() > 0,
                            "Длительность должна быть > 0"),
                    () -> assertNotNull(item.get("kinescopeId"),
                            "kinescopeId не должен быть null"),
                    () -> assertNotNull(item.get("createdAt"),
                            "createdAt не должен быть null")
            );
        }

        validateMiniCoursesPagination(response);
    }

    @Step("Проверка обязательных полей шоу")
    public void validateShowsItems(Response response) {
        assertBasicResponse(response);

        List<Map <String, Object>> items = response.jsonPath().getList("items");
        assertNotNull(items, "Список шоу не должен быть null");
        assertFalse(items.isEmpty(), "Список шоу не должен быть пуст");

        for (int i = 0; i < items.size(); i++) {
            Map <String, Object> item = items.get(i);
            assertAll("Элемент [" + i + "] «" + item.get("name") + "»",
                    () -> assertTrue(((Number) item.get("id")).intValue() > 0,
                            "ID должен быть > 0"),
                    () -> assertNotNull(item.get("name"),
                            "Название не должно быть null"),
                    () -> assertFalse(((String) item.get("name")).trim().isEmpty(),
                            "Название не должно быть пустым"),
                    () ->  assertTrue(((Number) item.get("duration")).doubleValue() > 0,
                            "Длительность должна быть > 0"),
                    () -> assertNotNull(item.get("kinescopeId"),
                            "kinescopeId не должен быть null"),
                    () -> assertNotNull(item.get("createdAt"),
                            "createdAt не должен быть null"),
                    () -> assertNotNull(item.get("category"),
                            "category не должен быть null")
            );

            // Валидация вложенного category
            Map<String, Object> category = (Map<String, Object>) item.get("category");
            if (category != null) {
                assertAll("category элемента [" + i + "]",
                        () -> assertTrue(((Number) category.get("id")).intValue() > 0,
                                "category.id должен быть > 0"),
                        () -> assertNotNull(category.get("name"),
                                "category.name не должен быть null")
                );
            }
        }
    }

    @Step("Валидация пагинации мини-курсов")
    public void validateMiniCoursesPagination(Response response) {
        int count = response.jsonPath().getInt("pagination.count");
        int total = response.jsonPath().getInt("pagination.total");
        int perPage = response.jsonPath().getInt("pagination.per_page");
        int page = response.jsonPath().getInt("pagination.page");
        int pages = response.jsonPath().getInt("pagination.pages");

        assertAll("Проверка пагинации",

                () -> assertTrue(count > 0, "count должен быть > 0"),
                () -> assertTrue(total > 0, "total должен быть > 0"),
                () -> assertTrue(perPage > 0, "per_page должен быть > 0"),
                () -> assertTrue(page >= 1, "page должен быть >= 1"),
                () -> assertTrue(pages >= 1, "pages должен быть >= 1"),
                () -> assertTrue(count <= perPage, "count не должен превышать per_page")
        );

        Allure.addAttachment("Пагинация", "text/plain",
                String.format("count: %d, total: %d, per_page: %d, page: %d, pages: %d",
                        count, total,
                        perPage, page, pages));
    }

    @Step("GET user/mini-workouts")
    public Response getMiniWorkouts(String authToken) {
        return productsApi.getMiniWorkouts(authToken);
    }

    @Step("GET /user/show-categories")
    public Response getShowCategories(String authToken) {
        return productsApi.getShowCategories(authToken);
    }

    @Step("GET /user/shows")
    public Response getShows(String authToken) {
        return productsApi.getShows(authToken);
    }

    @Step("Проверка обязательных полей шоу-категорий")
    public void validateShowCategoriesItems(Response response) {
        List<Map <String, Object>> items = response.jsonPath().getList("$");
        assertNotNull(items, "Список шоу-категорий не должен быть null");
        assertFalse(items.isEmpty(), "Список шоу-категорий не должен быть пуст");

        for (int i = 0; i < items.size(); i++) {
            Map <String, Object> item = items.get(i);
            assertAll("Элемент [" + i + "] «" + item.get("name") + "»",
                    () -> assertTrue(((Number) item.get("id")).intValue() > 0,
                            "ID должен быть > 0"),
                    () -> assertNotNull(item.get("name"),
                            "Название не должно быть null"),
                    () -> assertFalse(((String) item.get("name")).trim().isEmpty(),
                            "Название не должно быть пустым"),
                    () -> assertNotNull(item.get("icon"),
                            "Иконка не должна быть null"),
                    () -> assertTrue(((String) item.get("icon")).startsWith("http"),
                            "Иконка должна быть URL")
                    );
        }
    }
}