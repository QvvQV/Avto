package ru.tutor.test.api;

import io.qameta.allure.*;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tutor.config.TestConfig;
import ru.tutor.model.ProductsResponse;
import ru.tutor.steps.AuthSteps;
import ru.tutor.steps.ProductsSteps;


import java.util.*;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AllureJunit5.class)
@Epic("Список продуктов пользователя")
@Feature("Продукты пользователя")
@DisplayName("Тесты эндпоинта '/user/products'")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductsProfileTest {


    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private final AuthSteps authSteps = AuthSteps.withBaseUrl(BASE_URL);
    private final ProductsSteps productsSteps = ProductsSteps.withBaseUrl(BASE_URL);
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
                authSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );

        Response response = step("GET /user/products", () ->
                productsSteps.getUserProducts(token)
        );

        List<ProductsResponse> products = step("Валидация ответа", () ->
                productsSteps.validateProductsResponse(response)
        );

        productsSteps.validateProductsStructure(products);

        // Пример первого продукта для анализа
        savedOrderNumber = productsSteps.getFirstNumericOrderNumber(products);
        savedProductValue = productsSteps.getProductId(products);
        Allure.addAttachment("Product Report", savedOrderNumber);
    }

    @Test
    @Order(1)
    @DisplayName("Успешное получение списка продуктов - 200 ОК")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("smoke")
    @Tag("positive")
    @Tag("products")
    void getProductsSuccess() {

        // Детали продукта
        Response response1 = step("Получение информации о продукте", () ->
                productsSteps.getProductDetailsById(token, savedOrderNumber));
        step("Валидация деталей продукта", () ->
                productsSteps.validateProductDetailsResponse(response1));

        // Модуль продукта
        Response response2 = step("GET /user/products/{productValue}/modules/{n}", () ->
                productsSteps.getProductModuleById(token, savedProductValue, MODULE_NUMBER));

        step("Валидация модуля продукта", () ->
                assertEquals(200, response2.getStatusCode()));

        // Отметка просмотра модуля
        Response response3 = step("Отметка модулю отсмотренного времени", () ->
                productsSteps.markModuleWatched(token, savedOrderNumber, MODULE_NUMBER, TIMELINE));

        step("Валидация отметки тайминга продукта", () ->
                assertEquals(200, response3.getStatusCode()));

        // Отметка о завершении модуля
        Response response4 = step("Отметка модуля как завершенного", () ->
                productsSteps.completeModule(token, savedOrderNumber, MODULE_NUMBER));

        step("Валидация отметка о завершении", () ->
                assertEquals(200, response4.getStatusCode()));

        Response response5 = step("Получение списка достижений продукта", () ->
                productsSteps.getAchievementsProductsId(token, savedOrderNumber));
        step("Валидация деталей списка достижений продукта", () ->
                productsSteps.validateAchievementsProductsId(response5));

        Response response6 = step("Страница достижений", () ->
                productsSteps.getUserAchievements(token));

        step("Валидация страницы достижений", () ->
                productsSteps.validateAchievements(response6));
    }

    // ==================== Отрицательные тесты ====================

    @Test
    @Order(2)
    @DisplayName("Эндпоинты получение информации о продукте без авторизации")
    void getProductsWithoutAuth() {

        // Детали продукта
        Response response = step("Получение информации о продукте", () ->
                productsSteps.getProductDetailsById("", savedOrderNumber));
        step("Валидация деталей продукта", () ->
                assertTrue(response.getStatusCode() == 401));
    }

    @Test
    @Order(3)
    @DisplayName("Эндпоит modules без авторизации")
    void getModulesWithoutAuth() {

        // Модуль продукта
        Response response = step("GET /user/products/{productValue}/modules/{n}", () ->
                productsSteps.getProductModuleById("", savedProductValue, MODULE_NUMBER));

        step("Валидация модуля продукта", () ->
                assertTrue(response.getStatusCode() == 401));
    }

    @Test
    @Order(4)
    @DisplayName("Эндпоинт просмотр модуля без авторизации")
    void getModuleWatchedProductsWithoutAuth() {

        Response response = step("Отметка модулю отсмотренного времени", () ->
                productsSteps.markModuleWatched("", savedOrderNumber, MODULE_NUMBER, TIMELINE));

        step("Валидация отметки тайминга продукта", () ->
                assertTrue(response.getStatusCode() == 401));
    }

    @Test
    @Order(5)
    @DisplayName("Эндпоинт completeModule без авторизации")
    void putCompleteModuleWithoutAuth() {

        Response response = step("Отметка модуля как завершенного", () ->
                productsSteps.completeModule("", savedOrderNumber, MODULE_NUMBER));

        step("Валидация отметка о завершении", () ->
                assertTrue(response.getStatusCode() == 401));
    }

    @Test
    @Order(6)
    @DisplayName("Эндпоинт список достижений продукта без авторизации")
    void getAchievementsProductsWithoutAuth() {

        Response response = step("Получение списка достижений продукта", () ->
                productsSteps.getAchievementsProductsId("", savedOrderNumber));
        step("Валидация деталей списка достижений продукта", () ->
                assertTrue(response.getStatusCode() == 401));
    }

    @Test
    @Order(7)
    @DisplayName("Эндпоинт страница достижений без авторизации")
    void getAchievementsWithoutAuth() {
        Response response = step("Страница достижений", () ->
                productsSteps.getUserAchievements(""));

        step("Валидация страницы достижений", () ->
                assertTrue(response.getStatusCode() == 401));
    }

    @Test
    @Order(8)
    @DisplayName("Эндпоинт modules без номера продукта")
    void getUserModulesWithoutProductValue() {
        Response response = step("Пустое значение продукта в модуле просомтра", ()->
                productsSteps.getProductModuleById(token, "", MODULE_NUMBER));

        step("Валидация ответа при пустом продукте", () ->
                assertTrue(response.getStatusCode() == 404));
//        step("Валидация отсутствия stack trace для Production/Прод", () ->
//                assertFalse(response.getBody().asString().contains("trace")));
    }

    @Test
    @Order(9)
    @DisplayName("Невалидный модуль")
    void getUserModulesInvalidModule() {

        Response response = step("Невалидный модуль", ()->
                productsSteps.getProductModuleById(token, savedProductValue, INVALID_MODULE_NUMBER));

        step("Валидация ответа при невалидном модуле", () ->
                assertTrue(response.getStatusCode() == 404));
    }

    @Test
    @Order(10)
    @DisplayName("Эндпоит modules с невалидным номером продукта")
    void getModulesInvalidModuleProduct() {

        // Модуль продукта
        Response response = step("GET /user/products/{productValue}/modules/{n}", () ->
                productsSteps.getProductModuleById(token, INVALID_PRODUCT_VALUE, MODULE_NUMBER));

        step("Валидация модуля продукта", () ->
                assertTrue(response.getStatusCode() == 404));
    }
}