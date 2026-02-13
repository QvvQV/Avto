package ru.tutor.test.api;

import io.qameta.allure.AllureId;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tutor.api.steps.ProductsSteps;


@ExtendWith(AllureJunit5.class)
@Epic("API Тесты продуктов")
@Feature("Каталог продуктов")
@DisplayName("тесты API для работы с продуктами")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductsTest {
    private final ProductsSteps productSteps =
            new ProductsSteps("https://ttplace.ru", "admin", "JWwppsEA84B4BozJgE44sNiZZ");

    @Test
    @AllureId("50")
    @DisplayName("Все продукты из каталога должны быть доступны по коду 200")
    void allProductsAccessible() {
        productSteps.checkAllProductsAvailable();
        }
    }
