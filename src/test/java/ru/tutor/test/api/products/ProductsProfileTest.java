package ru.tutor.test.api.products;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import io.restassured.response.Response;
import ru.tutor.config.TestConfig;
import ru.tutor.model.catalog.ProductsResponse;
import ru.tutor.steps.account.AuthSteps;
import ru.tutor.steps.catalog.ProductsSteps;

import java.util.*;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;
import ru.tutor.constants.Tags;

@Epic("Список продуктов пользователя")
@Feature("Продукты пользователя")
@DisplayName("Тесты эндпоинта '/user/products'")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductsProfileTest {


    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private final AuthSteps userSteps = new AuthSteps(BASE_URL);
    private final ProductsSteps productsSteps = new ProductsSteps(BASE_URL);
    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();

    private String token;
    private String savedOrderNumber;
    private String savedProductValue;
    private final int TIMELINE = 9999;
    private final int MODULE_NUMBER = 5;
    private final int INVALID_MODULE_NUMBER = -1;
    private final String INVALID_PRODUCT_VALUE = "999999";

    @BeforeEach
    void setUpAuth() {
        // Логин перед каждым тестом
        token = step("Логин пользователя", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );
    }

    @Test
    @Order(1)
    @DisplayName("Успешное получение списка продуктов - 200 ОК")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.SMOKE)
    @Tag(Tags.POSITIVE)
    void getProductsSuccess() {
        ensureProductsLoaded();

        // Детали продукта
        Response response1 = step("Получение информации о продукте", () ->
                productsSteps.getProductDetailsById(token, savedOrderNumber));
        step("Валидация деталей продукта", () ->
                productsSteps.validateProductDetailsResponse(response1));

        // Модуль продукта
        Response response2 = step("GET /user/products/{productValue}/modules/{n}", () ->
                productsSteps.getProductModuleById(token, savedProductValue, MODULE_NUMBER));

        step("Валидация модуля продукта", () ->
                productsSteps.validateProductModuleResponse(response2));

        // Отметка просмотра модуля
        Response response3 = step("Отметка модулю отсмотренного времени", () ->
                productsSteps.markModuleWatched(token, savedOrderNumber, MODULE_NUMBER, TIMELINE));

        step("Валидация отметки тайминга продукта", () ->
                productsSteps.validateUniversity(response3));

        // Отметка о завершении модуля
        Response response4 = step("Отметка модуля как завершенного", () ->
                productsSteps.completeModule(token, savedOrderNumber, MODULE_NUMBER));

        step("Валидация отметка о завершении", () ->
                productsSteps.validateUniversity(response4));

        Response response5 = step("Получение списка достижений продукта", () ->
                productsSteps.getAchievementsProductsId(token, savedOrderNumber));
        step("Валидация деталей списка достижений продукта", () ->
                productsSteps.validateAchievements(response5));

        Response response6 = step("Страница достижений", () ->
                productsSteps.getUserAchievements(token));

        step("Валидация страницы достижений", () ->
                productsSteps.validateUserAchievementsResponse(response6));
    }

    @Test
    @Order(2)
    @DisplayName("Получение списка всех продуктов платформы")
    @Tag(Tags.POSITIVE)
    void getAllProductsSuccess() {
        Response response = productsSteps.getAllProducts(token);
        step("Валидация ответа", () -> {
            List<ProductsResponse> products = productsSteps.validateProductsResponse(response);
            productsSteps.validateProductsStructure(products);
            assertNotNull(response.getBody().asString());
            assertFalse(response.getBody().asString().isEmpty(), "Тело запроса не должно быть пустым");
        });
    }

    @Test
    @Order(3)
    @DisplayName("Эндпоинт Премьеры")
    @Tag(Tags.POSITIVE)
    void getPremiersProducts() {
        Response response = productsSteps.getPremiersProducts(token);
        step("Валидация ответа", () -> {
            productsSteps.validatePremiersResponse(response);
            assertNotNull(response.getBody().asString());
            assertFalse(response.getBody().asString().isEmpty(), "Тело запроса не должно быть пустым");
        });
    }

    @Test
    @Order(4)
    @DisplayName("Эндпоинт Топ10")
    @Tag(Tags.POSITIVE)
    void getProductsTop10() {
        Response response = productsSteps.getProductTop10(token);
        step("Валидация ответа", () -> {
            productsSteps.validateTop10Response(response);
            assertNotNull(response.getBody().asString());
            assertFalse(response.getBody().asString().isEmpty(), "Тело запроса не должно быть пустым");
        });
    }

    @Test
    @Order(5)
    @DisplayName("Эндпоинт Сейчас смотрят")
    @Tag(Tags.POSITIVE)
    void getProductWatchingNow() {
        Response response = productsSteps.getProductWatchingNow(token);
        step("Валидация ответа", () -> {
            productsSteps.validateWatchingNowResponse(response);
            assertNotNull(response.getBody().asString());
            assertFalse(response.getBody().asString().isEmpty(), "Тело запроса не должно быть пустым");
        });
    }

    @Test
    @Order(6)
    @DisplayName("Эндпоинт список Избранных продуктов")
    @Tag(Tags.POSITIVE)
    void getFavoriteProducts() {
        Response response = productsSteps.favoriteProduct(token);
        step("Валидация ответа", () ->
                productsSteps.validateFavoriteProductsResponse(response));
    }

    @Test
    @Order(7)
    @DisplayName("Эндпоинт добавления в Избранное")
    @Tag(Tags.POSITIVE)
    void postFavoriteProducts() {
        ensureProductsLoaded();
        Response response = productsSteps.postFavoriteProducts(token, savedProductValue);
        step("Валидация ответа", () -> {
            assertEquals(201, response.getStatusCode());
            assertNotNull(response.getBody().asString());
        });
    }

    @Test
    @Order(8)
    @DisplayName("Эндпоинт удаления продукта из избранных")
    @Tag(Tags.POSITIVE)
    void deleteProducts() {
        ensureProductsLoaded();
        Response response = productsSteps.deleteFavoriteProducts(token, savedProductValue);
        step("Валидация ответа", () -> {
            assertEquals(204, response.getStatusCode());
            assertNotNull(response.getBody().asString());
        });
    }

    @Test
    @Order(9)
    @DisplayName("Эндпоинт категории платформы")
    void getCategories() {
        Response response = productsSteps.getCategories(token);
        step("Валидация ответа", () -> {
            productsSteps.validateCategoriesResponse(response);
            assertNotNull(response.getBody().asString());
            assertFalse(response.getBody().asString().isEmpty(), "Тело запроса не должно быть пустым");
        });
    }

    @Test
    @Order(10)
    @DisplayName("Успешное получение Последних достижений GET /user/achievements/last")
    void getAchievementsLastSuccess() {
        Response response = productsSteps.getAchievementsLast(token);
        step("Валидация ответа", () ->
                productsSteps.validateProductsResponse(response)
        );
    }

    // ==================== Отрицательные тесты ====================

    @Test
    @Order(11)
    @DisplayName("Эндпоинты получение информации о продукте без авторизации")
    @Tag(Tags.NEGATIVE)
    void getProductsWithoutAuth() {
        ensureProductsLoaded();

        // Детали продукта
        Response response = step("Получение информации о продукте", () ->
                productsSteps.getProductDetailsById("", savedOrderNumber));
        step("Валидация деталей продукта", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(12)
    @DisplayName("Эндпоит modules без авторизации")
    @Tag(Tags.NEGATIVE)
    void getModulesWithoutAuth() {
        ensureProductsLoaded();

        // Модуль продукта
        Response response = step("GET /user/products/{productValue}/modules/{n}", () ->
                productsSteps.getProductModuleById("", savedProductValue, MODULE_NUMBER));

        step("Валидация модуля продукта", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(13)
    @DisplayName("Эндпоинт просмотр модуля без авторизации")
    @Tag(Tags.NEGATIVE)
    void getModuleWatchedProductsWithoutAuth() {
        ensureProductsLoaded();

        Response response = step("Отметка модулю отсмотренного времени", () ->
                productsSteps.markModuleWatched("", savedOrderNumber, MODULE_NUMBER, TIMELINE));

        step("Валидация отметки тайминга продукта", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(14)
    @DisplayName("Эндпоинт completeModule без авторизации")
    @Tag(Tags.NEGATIVE)
    void putCompleteModuleWithoutAuth() {
        ensureProductsLoaded();

        Response response = step("Отметка модуля как завершенного", () ->
                productsSteps.completeModule("", savedOrderNumber, MODULE_NUMBER));

        step("Валидация отметка о завершении", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(15)
    @DisplayName("Эндпоинт список достижений продукта без авторизации")
    @Tag(Tags.NEGATIVE)
    void getAchievementsProductsWithoutAuth() {
        ensureProductsLoaded();

        Response response = step("Получение списка достижений продукта", () ->
                productsSteps.getAchievementsProductsId("", savedOrderNumber));
        step("Валидация деталей списка достижений продукта", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(16)
    @DisplayName("Эндпоинт страница достижений без авторизации")
    @Tag(Tags.NEGATIVE)
    void getAchievementsWithoutAuth() {
        Response response = step("Страница достижений", () ->
                productsSteps.getUserAchievements(""));

        step("Валидация страницы достижений", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(17)
    @DisplayName("Эндпоинт modules без номера продукта")
    @Tag(Tags.NEGATIVE)
    void getUserModulesWithoutProductValue() {
        Response response = step("Пустое значение продукта в модуле просомтра", () ->
                productsSteps.getProductModuleById(token, "", MODULE_NUMBER));

        step("Валидация ответа при пустом продукте", () ->
                assertEquals(404, response.getStatusCode()));
//        step("Валидация отсутствия stack trace для Production/Прод", () ->
//                assertFalse(response.getBody().asString().contains("trace")));
    }

    @Test
    @Order(18)
    @DisplayName("Невалидный модуль")
    @Tag(Tags.NEGATIVE)
    void getUserModulesInvalidModule() {
        ensureProductsLoaded();

        Response response = step("Невалидный модуль", () ->
                productsSteps.getProductModuleById(token, savedProductValue, INVALID_MODULE_NUMBER));

        step("Валидация ответа при невалидном модуле", () ->
                assertEquals(404, response.getStatusCode()));
    }

    @Test
    @Order(19)
    @DisplayName("Эндпоит modules с невалидным номером продукта")
    @Tag(Tags.NEGATIVE)
    void getModulesInvalidModuleProduct() {

        // Модуль продукта
        Response response = step("GET /user/products/{productValue}/modules/{n}", () ->
                productsSteps.getProductModuleById(token, INVALID_PRODUCT_VALUE, MODULE_NUMBER));

        step("Валидация модуля продукта", () ->
                assertEquals(404, response.getStatusCode()));
    }

    @Test
    @Order(20)
    @DisplayName("Список всех продуктов платформы без авторизации")
    @Tag(Tags.NEGATIVE)
    void getAllProductsWithoutAuth() {
        Response response = productsSteps.getAllProducts("");
        step("Валидация ответа", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(21)
    @DisplayName("Премьеры без авторизации")
    @Tag(Tags.NEGATIVE)
    void getPremiersWithoutAuth() {
        Response response = productsSteps.getPremiersProducts("");
        step("Проверка код ответа", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(22)
    @DisplayName("Топ10 без авторизации")
    @Tag(Tags.NEGATIVE)
    void getTop10WithoutAuth() {
        Response response = productsSteps.getProductTop10("");
        step("Валидация ответа", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(23)
    @DisplayName("Сейчас смотрят без авторизации")
    @Tag(Tags.NEGATIVE)
    void getWatchingNowWithoutAuth() {
        Response response = productsSteps.getProductWatchingNow("");
        step("Валидация ответа", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(24)
    @DisplayName("Получение избранных продуктов без авторизации")
    @Tag(Tags.NEGATIVE)
    void getFavoriteProductsWithoutAuth() {
        Response response = productsSteps.favoriteProduct("");
        step("Валидация продукта", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(25)
    @DisplayName("Список продуктов без авторизации - 401")
    @Tag(Tags.NEGATIVE)
    void getUserProductsWithoutAuth() {
        Response response = productsSteps.getUserProducts("");
        step("Валидация ответа", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(26)
    @DisplayName("Детали продукта с несуществующим ID - 404")
    void getProductDetailsByIdNotFound() {
        Response response = productsSteps.getProductDetailsById(token, INVALID_PRODUCT_VALUE);
        step("Валидация ответа", () -> {
            assertEquals(400, response.getStatusCode());
            String body = response.getBody().asString();
            assertTrue(body.contains("error"), "Должно быть сообщение об ошибке. Тело: " + body);
        });
    }

    @Test
    @Order(27)
    @DisplayName("Достижения с несуществующим ID продукта - 404")
    @Tag(Tags.NEGATIVE)
    void getAchievementsProductByIdNotFound() {
        Response response = productsSteps.getAchievementsProductsId(token, INVALID_PRODUCT_VALUE);
        step("Валидация ответа", () -> {
            assertEquals(404, response.getStatusCode());
            String body = response.getBody().asString();
            assertTrue(body.contains("message"), "Должно быть сообщение об ошибке. Тело: " + body);
        });
    }

    @Test
    @Order(28)
    @DisplayName("Отметка просмотра с пустым orderNumber")
    @Tag(Tags.NEGATIVE)
    void getAchievementsProductById() {
        ensureProductsLoaded();
        Response response = productsSteps.markModuleWatched(token, "", MODULE_NUMBER, TIMELINE);
        step("Валидация ответа", () -> {
            assertEquals(422, response.getStatusCode());
            String body = response.getBody().asString();
            assertTrue(body.contains("orderNumber"), "Должна быть ошибка о номере курса. Тело: " + body);
        });
    }

    @Test
    @Order(29)
    @DisplayName("Завершение модуля с пустым orderNumber")
    @Tag(Tags.NEGATIVE)
    void completeModuleEmptyOrder() {
        ensureProductsLoaded();
        Response response = productsSteps.completeModule(token, "", MODULE_NUMBER);
        step("Валидация ответа", () -> {
            assertEquals(404, response.getStatusCode());
            String body = response.getBody().asString();
            assertTrue(body.contains("title"), "Должна быть ошибка о номере курса. Тело: " + body);
        });
    }

    @Test
    @Order(30)
    @DisplayName("Успешный ответ вложенные файлы")
    @Tag(Tags.POSITIVE)
    void getAttachedFilesSuccess() {
        ensureProductsLoaded();
        Response response = step("GET /user/products/{orderNumber}/attached-files", () ->
                productsSteps.getAttachedFiles(token, savedOrderNumber)
        );
        step("Валидация ответа", () -> {
            productsSteps.validateAttachedFilesResponse(response);
            assertNotNull(response.getBody().asString());
        });
    }

    @Test
    @Order(31)
    @DisplayName("Эндпоинт attached-files пустой продукт")
    @Tag(Tags.NEGATIVE)
    void getAttachedFilesWithoutProduct() {
        Response response = step("GET /user/products/{orderNumber}/attached-files без токена", () ->
                productsSteps.getAttachedFiles(token, "")
        );
        step("Проверка статуса 404", () ->
                assertEquals(404, response.getStatusCode()));
    }

    @Test
    @Order(32)
    @DisplayName("Эндпоинт achievements/last без авторизации")
    @Tag(Tags.NEGATIVE)
    void getAchievementsLastWithoutAuth() {
        Response response = step("GET user/achievements/last без авторизации", () ->
                productsSteps.getAchievementsLast("")
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(33)
    @DisplayName("Эндпоинт achievements/last невалидный токен")
    @Tag(Tags.NEGATIVE)
    void getAchievementsLastInvalidToken() {
        Response response = step("GET user/achievements/last с невалидным токеном", () ->
                productsSteps.getAchievementsLast("invalidToken")
        );
        step("Проверка 401", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(34)
    @DisplayName("Эндпоинт удаления продукта из избранных - без токена")
    @Tag(Tags.NEGATIVE)
    void deleteProductsWithoutAuth() {
        ensureProductsLoaded();
        Response response = productsSteps.deleteFavoriteProducts("", savedProductValue);
        step("Валидация ответа", () -> {
            assertEquals(401, response.getStatusCode());
            assertNotNull(response.getBody().asString());
        });
    }

    @Test
    @Order(35)
    @DisplayName("Эндпоинт удаления без указания продукта из избранных")
    @Tag(Tags.NEGATIVE)
    void deleteWithoutProducts() {
        ensureProductsLoaded();
        Response response = productsSteps.deleteFavoriteProducts(token, "");
        step("Валидация ответа", () -> {
            assertEquals(404, response.getStatusCode());
            assertNotNull(response.getBody().asString());
        });
    }

    private void ensureProductsLoaded() {
        if (savedProductValue != null) return;

        savedOrderNumber = productsSteps.fetchFirstOrderNumber(token);

        Response response = productsSteps.getUserProducts(token);
        List<ProductsResponse> products = productsSteps.validateProductsResponse(response);
        step("Проверка структуры продуктов", () -> productsSteps.validateProductsStructure(products));
        savedProductValue = step("Получение productId", () -> productsSteps.getProductId(products));
        Allure.addAttachment("Product Report", savedProductValue);
    }
}