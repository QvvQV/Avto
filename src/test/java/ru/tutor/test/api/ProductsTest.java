package ru.tutor.test.api;

import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tutor.api.Steps.ProductsSteps;


@ExtendWith(AllureJunit5.class)
public class ProductsTest {
    private final ProductsSteps productSteps =
            new ProductsSteps("https://ttplace.ru", "admin", "JWwppsEA84B4BozJgE44sNiZZ");

    @Test
    @DisplayName("Все продукты из каталога должны быть доступны по коду 200")
    void allProductsAccessible() {
        productSteps.checkAllProductsAvailable();
        }
    }
