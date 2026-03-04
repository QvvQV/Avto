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

    private static String token;

    @BeforeAll
    public static void setUp() {
        Allure.label("component", "API");
        Allure.label("feature", "Game Wheel");
    }

    @BeforeEach
    void setUpAuth() {
        // Логин перед каждым тестом
        token = step("Логин пользователя", () ->
                authSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );
    }

    @Test
    @Order(1)
    @DisplayName("Успешное получение списка продуктов - 200 ОК")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("positive")
    @Tag("products")
    void getProductsSuccess() {
        Response response = step("GET /user/products", () ->
                productsSteps.addProduct(token)
        );

        List<ProductsResponse> products = step("Валидация ответа", () ->
                productsSteps.validateProductsResponse(response)
        );

        productsSteps.validateProductsStructure(products);
        productsSteps.generateProductsReport(products);
        // Пример первого продукта для анализа
        ProductsResponse firstProduct = products.getFirst();
    }
}
