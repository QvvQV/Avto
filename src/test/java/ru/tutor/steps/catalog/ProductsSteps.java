package ru.tutor.steps.catalog;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.api.catalog.UserProductsApi;
import ru.tutor.base.BaseApiSteps;
import ru.tutor.model.catalog.ProductsResponse;
import ru.tutor.model.catalog.TotalCoursesResponse;
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

    private void validateAuthors(List<Map<String, Object>> authors, boolean withShowOtherCoursesButton) {
        assertNotNull(authors, "authors не null");
        for (int i = 0; i < authors.size(); i++) {
            Map<String, Object> a = authors.get(i);
            assertAll("Автор [" + i + "]",
                    positiveInt(a, "id"),
                    notNull(a, "name"),
                    notNull(a, "profession"),
                    url(a.get("photoUrl"), "photoUrl"),
                    () -> {
                        if (withShowOtherCoursesButton) notNull(a, "showOtherCoursesButton");
                    });
        }
    }

    private void validateReviews(List<Map<String, Object>> reviews, boolean withPhotoName) {
        assertNotNull(reviews, "reviews не null");
        for (int i = 0; i < reviews.size(); i++) {
            Map<String, Object> r = reviews.get(i);
            assertAll("Отзыв [" + i + "]",
                    positiveInt(r, "id"),
                    notNull(r, "reviewerName"),
                    notNull(r, "reviewerProfession"),
                    url(r.get("reviewerPhotoUrl"), "reviewerPhotoUrl"),
                    notNull(r, "text"),
                    () -> {
                        if (withPhotoName) url(r.get("reviewerPhotoName"), "reviewerPhotoName");
                    });
        }
    }

    private void validateModulesSummary(Map<String, Object> modules, String label) {
        assertNotNull(modules, label + " не null");
        assertTrue(((Number) modules.get("total")).intValue() > 0, label + ".total > 0");
        assertTrue(modules.containsKey("progress"), label + ".progress должен быть в ответе");
    }

    private void validateCategoryItem(Map<String, Object> cat, int index) {
        assertAll("Категория [" + index + "]",
                positiveInt(cat, "id"),
                notNull(cat, "name"),
                notNull(cat, "slug"),
                notNull(cat, "is18Plus"));
    }

    @Step("Валидация ответа премьер /user/products/premiers")
    public void validatePremiersResponse(Response response) {
        assertBasicResponse(response);
        List<Map<String, Object>> items = assertJsonArray(response, "Список премьер");

        for (int i = 0; i < items.size(); i++) {
            Map<String, Object> p = items.get(i);
            String entity = (String) p.get("entity");
            String info = "Премьера [" + i + "]";
            assertAll(info,
                    () -> assertTrue("video".equals(entity) || "product".equals(entity) || "category".equals(entity),
                            info + ": entity должен быть video или product"),
                    nonEmptyString(p, "name"),
                    optionalUrl(p.get("cover"), "cover"),
                    notNull(p, "publicAt"));

            if ("video".equals(entity)) {
                validateVideoPremierItem(p, info);
            } else if ("product".equals(entity)) {
                validateProductPremiersItem(p, info);
            }
        }
    }

    private void validateVideoPremierItem(Map<String, Object> p, String info) {
        assertAll(info + " (video)",
                positiveInt(p, "videoId"),
                () -> assertInstanceOf(Number.class, p.get("type"), info + ": type должен быть числом"));
        Map<String, Object> author = (Map<String, Object>) p.get("author");
        if (author != null) {
            assertAll(info + " - author",
                    positiveInt(author, "id"),
                    nonEmptyString(author, "name"));
        }

        Map<String, Object> showCategory = (Map<String, Object>) p.get("showCategory");
        if (showCategory != null) {
            assertAll(info + " - showCategory",
                    positiveInt(showCategory, "id"),
                    nonEmptyString(showCategory, "name"),
                    optionalUrl(showCategory.get("cover"), "showCategory.cover"),
                    optionalUrl(showCategory.get("icon"), "showCategory.icon"));
        }
    }

    private void validateProductPremiersItem(Map<String, Object> p, String info) {
        assertAll(info + " (product)",
                nonEmptyString(p, "name"),
                notNull(p, "slug"),
                positiveInt(p, "productId"),
                notNull(p, "description"),
                notNull(p, "shortDescription"),
                optionalUrl(p.get("cover"), "cover"),
                optionalUrl(p.get("coverMobile"), "coverMobile"),
                optionalUrl(p.get("background"), "background"),
                optionalUrl(p.get("backgroundMini"), "backgroundMini"),
                notNull(p, "isPremium"),
                notNull(p, "isLite"),
                notNull(p, "notifyMe"),
                nonNegative(p, "rating"),
                nonNegative(p, "price"),
                nonNegative(p, "oldPrice"),
                notNull(p, "createdAt"),
                optionalField(p, "previewKinescopeId", String.class),
                nonNegative(p, "courseNovelty"),
                notNull(p, "publicAt"),
                notNull(p, "isCourseOfWeek"),
                notNull(p, "isRecommendedHidden"),
                notNull(p, "isPopular"),
                containsKey(p, "modal"),
                containsKey(p, "modalMobile"),
                containsKey(p, "link")
        );
        validateModulesSummary((Map<String, Object>) p.get("modules"), "modules");

        assertNotNull(p.get("skills"), "skills не null");
        assertFalse(((List<?>) p.get("skills")).isEmpty(), "skills не должен быть пустым");

        List<Map<String, Object>> categories = (List<Map<String, Object>>) p.get("categories");
        assertNotNull(categories, "categories не null");
        for (int c = 0; c < categories.size(); c++) validateCategoryItem(categories.get(c), c);

        validateAuthors((List<Map<String, Object>>) p.get("authors"), true);

        assertNotNull(p.get("reviews"), "reviews не null");
        assertNotNull(p.get("previews"), "previews не null");
    }

    @Step("Валидация ответа Топ-10 /user/product-positions/top10")
    public void validateTop10Response(Response response) {
        assertBasicResponse(response);
        List<Map<String, Object>> items = assertJsonArray(response, "Топ-10");

        for (int i = 0; i < items.size(); i++) {
            Map<String, Object> entry = items.get(i);
            assertTrue(((Number) entry.get("position")).intValue() >= 1, "position должен быть >= 1");

            Map<String, Object> product = (Map<String, Object>) entry.get("product");
            assertNotNull(product, "product не null");
            assertAll("Продукт [" + i + "]",
                    nonEmptyString(product, "name"),
                    notNull(product, "shortDescription"),
                    notNull(product, "description"),
                    notNull(product, "top10description"),
                    positiveInt(product, "productId"),
                    nonNegative(product, "rating"),
                    nonNegative(product, "price"),
                    nonNegative(product, "renewalProductPrice"),
                    notNull(product, "subscribed"),
                    notNull(product, "isPremium"),
                    notNull(product, "isLite"),
                    notNull(product, "isActive"),
                    notNull(product, "isAvailable"),
                    optionalUrl(product.get("background"), "background"),
                    optionalUrl(product.get("backgroundMini"), "backgroundMini"),
                    optionalUrl(product.get("coverMobile"), "coverMobile"),
                    optionalField(product, "previewKinescopeId", String.class),
                    optionalField(product, "isVideoPreviewEnabled", Boolean.class),
                    notNull(product, "createdAt")
            );
            validateModulesSummary((Map<String, Object>) product.get("modules"), "product.modules");
            validateReviews((List<Map<String, Object>>) product.get("reviews"), false);
            validateAuthors((List<Map<String, Object>>) product.get("authors"), false);
        }
        assertEquals(items.size(),
                items.stream().map(m -> m.get("position")).distinct().count(),
                "position не должен повторяться");
    }

    @Step("Валидация ответа «Сейчас смотрят» /user/products/watching/now")
    public void validateWatchingNowResponse(Response response) {
        assertBasicResponse(response);
        List<Map<String, Object>> items = assertJsonArray(response, "Список «Сейчас смотрят»");

        for (int i = 0; i < items.size(); i++) {
            Map<String, Object> item = items.get(i);
            String info = "Просмотр [" + i + "]";
            String type = (String) item.get("type");
            assertAll(info,
                    nonEmptyString(item, "lessonTitle"),
                    positiveInt(item, "lessonNumber"),
                    nonEmptyString(item, "productTitle"),
                    () -> assertTrue("course".equals(type) || "podcast".equals(type),
                            info + ": type должен быть course или podcast"),
                    () -> assertTrue(((Number) item.get("duration")).doubleValue()
                                    > 0,
                            info + ": duration > 0"),
                    containsKey(item, "userProductNumber")
            );

            if ("course".equals(type)) {
                assertAll(info + " (course)",
                        positiveInt(item, "productId"),
                        nonEmptyString(item, "productSlug"));
            } else {
                assertAll(info + " (podcast)",
                        () -> assertInstanceOf(Number.class, item.get("videoId"), info + ": videoId должен быть числом"),
                        optionalPositiveInt(item, "productId"));
            }

            Map<String, Object> poster = (Map<String, Object>) item.get("poster");
            assertNotNull(poster, "poster не null");
            assertAll(info + " - Poster",
                    nonEmptyString(poster, "id"),
                    nonEmptyString(poster, "type"),
                    nonEmptyString(poster, "media_id"),
                    nonEmptyString(poster, "status"),
                    () -> assertInstanceOf(Boolean.class, poster.get("active"), "active должен быть boolean"),
                    url(poster.get("original"), "original"),
                    url(poster.get("md"), "md"),
                    url(poster.get("sm"), "sm"),
                    url(poster.get("xs"), "xs"),
                    nonNegative(poster, "from_time"),
                    nonNegative(poster, "to_time"),
                    nonEmptyString(poster, "download_filename")
            );
        }
    }

    @Step("Валидация ответа избранных продуктов /user/favorite-products")
    public void validateFavoriteProductsResponse(Response response) {
        assertBasicResponse(response);
        assertNotNull(response.jsonPath().getList("$"), "Тело должно быть JSON-массивом");
    }

    @Step("Валидация ответа категорий /categories")
    public void validateCategoriesResponse(Response response) {
        assertBasicResponse(response);
        List<Map<String, Object>> items = assertJsonArray(response, "Список категорий");

        for (int i = 0; i < items.size(); i++) {
            Map<String, Object> c = items.get(i);
            assertAll("Категория [" + i + "]",
                    positiveInt(c, "id"),
                    nonEmptyString(c, "name"),
                    nonEmptyString(c, "slug"),
                    url(c.get("background"), "background"),
                    () -> assertInstanceOf(Boolean.class, c.get("is18Plus"), "is18Plus должен быть boolean"),
                    containsKey(c, "icon"),
                    containsKey(c, "iconMini"),
                    optionalUrl(c.get("icon"), "icon"),
                    optionalUrl(c.get("iconMini"), "iconMini")
            );
        }
        assertEquals(items.size(), items.stream().map(m -> m.get("id")).distinct().count(), "id категорий не должны повторяться");
        assertEquals(items.size(), items.stream().map(m -> m.get("slug")).distinct().count(), "slug категорий не должны повторяться");
    }

    // ==================== МЕТОДЫ ДЛЯ ПОЛУЧЕНИЯ ID ====================

    /**
     * Получение ID четвёртого продукта в списке
     */
    @Step("Получение ID продукта")
    public String getProductId(List<ProductsResponse> products) {
        List<ProductsResponse> nonMini = products.stream()
                .filter(p -> !p.isMiniCourse())
                .toList();
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

    @Step("Валидация ответа вложений /user/products/{orderNumber}/attached-files")
    public void validateAttachedFilesResponse(Response response) {
        assertBasicResponse(response);
        String body = response.getBody().asString();
        assertNotNull(body, "Тело не должно быть null");
        assertFalse(body.isEmpty(), "Тело не должно быть пустым");
    }

    @Step("Валидация ответа с деталями продукта")
    public void validateProductDetailsResponse(Response response) {
        assertBasicResponse(response);
        Map<String, Object> root = response.jsonPath().getMap("$");
        assertNotNull(root, "Тело должно быть JSON-объектом");

        assertAll("Детали продукта",
                nonEmptyString(root, "id"),
                nonEmptyString(root, "name"),
                notNull(root, "slug"),
                nonNegative(root, "rating"),
                positiveInt(root, "productId"),
                notNull(root, "shortDescription"),
                notNull(root, "language"),
                notNull(root, "firstCheckbox"),
                notNull(root, "secondCheckbox"),
                optionalUrl(root.get("background"), "background"),
                optionalUrl(root.get("backgroundMini"), "backgroundMini"),
                optionalUrl(root.get("cover"), "cover"),
                optionalUrl(root.get("coverMobile"), "coverMobile"),
                optionalField(root, "isVideoPreviewEnabled", Boolean.class),
                notNull(root, "miniCourse"),
                notNull(root, "previewKinescopeId"),
                notNull(root, "reviewsCount"),
                nonNegative(root, "reviewsCount"),
                notNull(root, "isActive"),
                notNull(root, "contentAvailable"),
                nonNegative(root, "countFiles"),
                () -> assertTrue(((Number) root.get("duration")).intValue() > 0, "duration > 0"),
                () -> assertTrue(((Number) root.get("activeDays")).intValue() > 0, "activeDays > 0"),
                nonNegative(root, "updatePrice"),
                notNull(root, "isAvailable"),
                notNull(root, "isTariffFreeRegistration")
        );

        validateAuthors(response.jsonPath().getList("authors"), true);
        validateReviews(response.jsonPath().getList("reviews"), true);

        Map<String, Object> lastModule = response.jsonPath().getMap("lastModuleShowInfo");
        if (lastModule != null) {
            assertTrue(((Number) lastModule.get("module")).intValue() > 0, "lastModule.module > 0");
            notNull(lastModule, "timeline");
        }

        List<Map<String, Object>> modules = response.jsonPath().getList("modules");
        assertNotNull(modules, "modules не должен быть null");
        for (int i = 0; i < modules.size(); i++) {
            Map<String, Object> m = modules.get(i);
            assertAll("Модуль [" + i + "]",
                    positiveInt(m, "module"),
                    notNull(m, "isDone"),
                    containsKey(m, "viewedAt")
            );
        }
    }

    private void validateAchievementItem(Map<String, Object> item, int index, boolean idNullable) {
        assertAll("Достижение [" + index + "]",
                () -> {
                    if (idNullable) {
                        assertTrue(item.containsKey("id"),
                                "ключ id должен присутствовать");
                    } else {
                        assertTrue(((Number) item.get("id")).intValue() > 0, "id > 0");
                    }
                },
                positiveInt(item, "productId"),
                notNull(item, "name"),
                notNull(item, "description"),
                nonNegative(item, "score"),
                notNull(item, "isNew"),
                notNull(item, "progress"),
                positiveInt(item, "daysNeeded"),
                () -> assertTrue(List.of(1, 2, 3, 4).contains(item.get("type"))),
                positiveInt(item, "category"),
                nonNegative(item, "peoplePercent"),
                url(item.get("pic"), "pic"),
                url(item.get("picAchievement"),
                        "picAchievement"),
                containsKey(item, "dateReceived"),
                () -> {
                    List<?> progress = (List<?>) item.get("progress");
                    assertNotNull(progress, "progress не null");
                    assertTrue(progress.size() >= 2, "progress содержит минимум 2 элемента");
                    assertTrue(((Number) progress.get(0)).intValue() <= ((Number) progress.get(1)).intValue(),
                            "progress: текущее значение <= целевого");
                }
        );
    }

    @Step("Валидация ответа модуля продукта /user/products/{productValue}/modules/{n}")
    public void validateProductModuleResponse(Response response) {
        assertBasicResponse(response);
        Map<String, Object> root = response.jsonPath().getMap("$");
        assertNotNull(root, "Тело должно быть JSON-объектом");
        assertAll("Модуль продукта",
                containsKey(root, "videoAds"),
                notNull(root, "videoAds")
        );
    }

    @Step("Валидация ответа отметки тайминга/зевершение модуля")
    public void validateUniversity(Response response) {
        assertBasicResponse(response);
        assertNotNull(response.jsonPath().getMap("$"), "Тело должно быть JSON-объектом");
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
        assertAll("game_task",
                containsKey(task, "day"),
                containsKey(task, "week"),
                containsKey(task, "special")
        );
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
        assertNotNull(response.jsonPath().getObject("professions", Integer.class));
        assertNotNull(response.jsonPath().getObject("professionsLimit", Integer.class));

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
        Allure.addAttachment("Order Number", productId);
        Allure.addAttachment("Module Number", String.valueOf(moduleNumber));
        Allure.addAttachment("Timeline", String.valueOf(timeline));

        Response response = productsApi.postProductCreate(authToken, productId, moduleNumber, timeline);

        AllureAttachments.attachJson("Mark Watched Response", response.getBody().asString());
        return response;
    }

    @Step("Отметка модуля как завершенного (orderNumber: {orderNumber}, moduleNumber: {moduleNumber})")
    public Response completeModule(String authToken, String productId, int moduleNumber) {
        Allure.addAttachment("Order Number", productId);
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

    @Step("Получение первого orderNumber из /user/products")
    public String fetchFirstOrderNumber(String authToken) {
        Response response = getUserProducts(authToken);
        List<ProductsResponse> products = validateProductsResponse(response);
        return getFirstNumericOrderNumber(products);
    }

    @Step("Получение orderNumber премиум-продукта из /user/products")
    public String fetchPremiumOrderNumber(String authToken) {
        Response response = getUserProducts(authToken);
        List<ProductsResponse> products = validateProductsResponse(response);

        ProductsResponse premium = products.stream()
                .filter(ProductsResponse::isPremiumProduct)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Премиум-продукт не найден в профиле"));

        String orderNumber = premium.getId();
        Allure.addAttachment("Premium orderNumber", orderNumber);
        return orderNumber;
    }

    @Step("GET user/mini-courses")
    public Response getMiniCourses(String authToken) {
        return productsApi.getMiniCourses(authToken);
    }

    @Step("Проверка что все элементы - мини-курсы")
    public void validateAllAreMiniCourses(Response response) {
        assertBasicResponse(response);
        List<Map<String, Object>> items = response.jsonPath().getList("items");
        assertNotNull(items, "Список мини-курсов не должен быть null");
        assertFalse(items.isEmpty(), "Список мини-курсов не должен быть пуст");

        for (int i = 0; i < items.size(); i++) {
            Map<String, Object> item = items.get(i);
            assertAll("Элемент [" + i + "] «" + item.get("name") + "»",
                    positiveInt(item, "id"),
                    nonEmptyString(item, "name"),
                    positiveInt(item, "duration"),
                    notNull(item, "kinescopeId"),
                    notNull(item, "createdAt")
            );
        }

        validatePagination(response);
    }

    @Step("Проверка обязательных полей шоу")
    public void validateShowsItems(Response response) {
        assertBasicResponse(response);

        List<Map<String, Object>> items = response.jsonPath().getList("items");
        assertNotNull(items, "Список шоу не должен быть null");
        assertFalse(items.isEmpty(), "Список шоу не должен быть пуст");

        for (int i = 0; i < items.size(); i++) {
            Map<String, Object> item = items.get(i);
            assertAll("Элемент [" + i + "] «" + item.get("name") + "»",
                    positiveInt(item, "id"),
                    nonEmptyString(item, "name"),
                    positiveInt(item, "duration"),
                    notNull(item, "kinescopeId"),
                    notNull(item, "createdAt"),
                    notNull(item, "category")
            );

            // Валидация вложенного category
            Map<String, Object> category = (Map<String, Object>) item.get("category");
            if (category != null) {
                assertAll("category элемента [" + i + "]",
                        positiveInt(category, "id"),
                        notNull(category, "name")
                );
            }
        }
    }

    @Step("Валидация пагинации мини-курсов")
    public void validatePagination(Response response) {
        assertBasicResponse(response);
        Map<String, Object> pagination = response.jsonPath().getMap("pagination");
        assertNotNull(pagination, "pagination не должен быть null");

        assertAll("Проверка пагинации",

                positiveInt(pagination, "count"),
                positiveInt(pagination, "total"),
                positiveInt(pagination, "per_page"),
                positiveInt(pagination, "page"),
                positiveInt(pagination, "pages"),
                () -> assertTrue(((Number) pagination.get("count")).intValue()
                                <= ((Number) pagination.get("per_page")).intValue(),
                        "count не должен превышать per_page")
        );

        Allure.addAttachment("Пагинация", "text/plain",
                String.format("count: %d, total: %d, per_page: %d, page: %d, pages: %d",
                        pagination.get("count"), pagination.get("total"), pagination.get("per_page"),
                        pagination.get("page"), pagination.get("pages")));
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
        assertBasicResponse(response);
        List<Map<String, Object>> items = assertJsonArray(response, "Список шоу-категорий");

        for (int i = 0; i < items.size(); i++) {
            Map<String, Object> item = items.get(i);
            assertAll("Элемент [" + i + "] «" + item.get("name") + "»",
                    positiveInt(item, "id"),
                    nonEmptyString(item, "name"),
                    url(item.get("icon"),
                            "Иконка"),
                    containsKey(item, "cover")
            );
        }
    }

    @Step("Логин и список продуктов пользователя")
    public List<ProductsResponse> getUserProductsList(String authToken) {
        return validateProductsResponse(getUserProducts(authToken));
    }

    @Step("Проверка: продукт productId={productId} присутствует в списке")
    public void assertProductPresent(String authToken, String productId) {
        List<ProductsResponse> products = getUserProductsList(authToken);
        assertTrue(products.stream().anyMatch(p -> productId.equals(p.getProductId())),
                "Продукт (productId " + productId + ") должен присутствовать в списке");
    }

    @Step("Проверка: продукт productId={productId} отсутвует в списке")
    public void assertProductAbsent(String authToken, String productId) {
        List<ProductsResponse> products = getUserProductsList(authToken);
        assertFalse(products.stream().anyMatch(p -> productId.equals(p.getProductId())),
                "Продукт (productId " + productId + ") не должен присутствовать в спике");
    }

    @Step("Read-back: lastModuleShowInfo продукта {orderNumber}")
    @SuppressWarnings("unchecked")
    public Map<String, Object> fetchLastModuleShowInfo(String authToken, String
            orderNumber) {
        Response response = getUserProducts(authToken);
        assertBasicResponse(response);
        List<Map<String, Object>> products = response.jsonPath().getList("$");
        return products.stream()
                .filter(p -> orderNumber.equals(String.valueOf(p.get("id"))))
                .findFirst()
                .map(p -> (Map<String, Object>) p.get("lastModuleShowInfo"))
                .orElseThrow(() -> new AssertionError("Продукт " + orderNumber + " не найден в /user/products"));
    }

    @Step("GET /total-courses - общий счётчик каталога")
    public Response getTotalCourses() {
        Response response = productsApi.getTotalCourses();
        AllureAttachments.attachJson("Total Courses Response", response.getBody().asString());
        return response;
    }

    @Step("Валидация ответа /total-courses")
    public TotalCoursesResponse validateTotalCoursesResponse(Response response) {
        assertBasicResponse(response);
        Map<String, Object> root = response.jsonPath().getMap("$");
        assertNotNull(root, "Тело должно быть JSON-объектом");

        assertAll("Счётчики /total-courses",
                containsKey(root, "products"),
                containsKey(root, "miniCourses"),
                containsKey(root, "shows"),
                containsKey(root, "miniWorkouts"),
                notNull(root, "products"),
                notNull(root, "miniCourses"),
                notNull(root, "shows"),
                notNull(root, "miniWorkouts"),
                nonNegative(root, "products"),
                nonNegative(root, "miniCourses"),
                nonNegative(root, "shows"),
                nonNegative(root, "miniWorkouts"));

        return response.as(TotalCoursesResponse.class);
    }
}