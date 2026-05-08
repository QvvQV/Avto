package ru.tutor.api.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.api.ProductApi;
import ru.tutor.config.ProductConfig;
import ru.tutor.model.ProductResponse;
import ru.tutor.utils.AllureAttachments;

import java.util.List;

import static io.qameta.allure.Allure.step;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class ProductSteps {
    private final ProductApi productApi;
    
    public ProductSteps() {
        this.productApi = new ProductApi();
    }
    
    @Step("Получить доменную информацию для продукта: {productId}")
    public Response getProductResponse(String productId) {
        Response response = productApi.getProductInfo(productId);
        AllureAttachments.attachJson("Domain Response for: " + productId, response.getBody().asString());
        return response;
    }
    
    @Step("Проверить успешный ответ домена для продукта: {productId}")
    public ProductResponse validateSuccessfulDomainResponse(String productId) {
        Response response = getProductResponse(productId);
        
        response.then()
                .statusCode(200)
                .contentType("application/json")
                .header("Access-Control-Allow-Origin", equalTo("https://psykatalog.tutorplace.ru"))
                .header("X-Powered-By", equalTo("PHP/7.4.30"));
        
        ProductResponse domainResponse = response.as(ProductResponse.class);
        assertNotNull(domainResponse, "Доменный ответ не должен быть null");
        
        return domainResponse;
    }
    
    @Step("Проверить поле name в products для продукта: {productId}")
    public void validateProductsName(String productId, ProductResponse domainResponse) {
        assertNotNull(domainResponse.getProducts(), 
            "Поле 'products' не должно быть null для продукта: " + productId);
        assertFalse(domainResponse.getProducts().isEmpty(),
            "Массив 'products' не должен быть пустым для продукта: " + productId);
        
        List<ProductResponse.Product> products = domainResponse.getProducts();
        
        step("Проверить name для каждого продукта в массиве", () -> {
            for (int i = 0; i < products.size(); i++) {
                ProductResponse.Product product = products.get(i);
                assertNotNull(product.getName(), 
                    String.format("Поле 'name' не должно быть null для продукта [%d] в массиве products для: %s", i, productId));
                assertFalse(product.getName().trim().isEmpty(),
                    String.format("Поле 'name' не должно быть пустым для продукта [%d] в массиве products для: %s", i, productId));
                assertTrue(product.getName().length() >= 2,
                    String.format("Название продукта [%d] должно быть достаточно длинным для: %s", i, productId));
            }
        });
    }
    
    @Step("Проверить ожидаемое название продукта для: {productId}")
    public void validateExpectedProductName(String productId, ProductResponse domainResponse) {
        String expectedName = ProductConfig.getExpectedProductName(productId);
        
        if (expectedName != null) {
            List<ProductResponse.Product> products = domainResponse.getProducts();
            boolean foundExpectedProduct = products.stream()
                    .anyMatch(product -> expectedName.equals(product.getName()));
            
            assertTrue(foundExpectedProduct,
                "В массиве products должен присутствовать продукт с названием: '" + expectedName + "' для: " + productId);
            
            AllureAttachments.attachProductComparison(productId, expectedName, 
                products.stream().map(ProductResponse.Product::getName).reduce((a, b) -> a + ", " + b).orElse("No products"));
        }
    }
    
    @Step("Проверить структуру первого продукта для: {productId}")
    public void validateFirstProductStructure(String productId, ProductResponse domainResponse) {
        ProductResponse.Product firstProduct = domainResponse.getProducts().getFirst();
        
        step("Проверить основные поля первого продукта", () -> {
            assertNotNull(firstProduct.getId(), "ID продукта не должен быть null");
            assertNotNull(firstProduct.getPrice(), "Price продукта не должен быть null");
            assertNotNull(firstProduct.getCurrency(), "Currency продукта не должен быть null");
            
            // Проверка опциональных полей если они присутствуют
            if (firstProduct.getDescription() != null) {
                assertFalse(firstProduct.getDescription().trim().isEmpty(),
                    "Description не должен быть пустым если присутствует");
            }
            
            if (firstProduct.getCover() != null) {
                assertTrue(firstProduct.getCover().startsWith("http"),
                    "Cover должен быть валидным URL если присутствует");
            }
            
            if (firstProduct.getAuthors() != null && !firstProduct.getAuthors().isEmpty()) {
                ProductResponse.Author firstAuthor = firstProduct.getAuthors().getFirst();
                assertNotNull(firstAuthor.getName(), "Author name не должен быть null");
                assertNotNull(firstAuthor.getProfession(), "Author profession не должен быть null");
            }
        });
    }
    
    @Step("Проанализировать products для продукта: {productId}")
    public void analyzeProducts(String productId, ProductResponse domainResponse) {
        List<ProductResponse.Product> products = domainResponse.getProducts();
        
        StringBuilder analysis = new StringBuilder();
        analysis.append("=== АНАЛИЗ PRODUCTS ДЛЯ ").append(productId).append(" ===\n\n");
        analysis.append("Количество продуктов: ").append(products.size()).append("\n\n");
        
        for (int i = 0; i < products.size(); i++) {
            ProductResponse.Product product = products.get(i);
            analysis.append("ПРОДУКТ [").append(i).append("]:\n");
            analysis.append("  ID: ").append(product.getId()).append("\n");
            analysis.append("  Name: ").append(product.getName()).append("\n");
            analysis.append("  Price: ").append(product.getPrice()).append(" ").append(product.getCurrency()).append("\n");
            analysis.append("  Authors: ").append(product.getAuthors() != null ? product.getAuthors().size() : 0).append("\n");
            
            if (product.getAuthors() != null && !product.getAuthors().isEmpty()) {
                analysis.append("  First Author: ").append(product.getAuthors().getFirst().getName()).append("\n");
            }
            
            analysis.append("  Description Length: ").append(
                product.getDescription() != null ? product.getDescription().length() : "null").append(" chars\n");
            analysis.append("\n");
        }
        
        AllureAttachments.attachText("Products Analysis - " + productId, analysis.toString());
    }
}
