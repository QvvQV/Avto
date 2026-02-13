package ru.tutor.test.api;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.tutor.config.ProductConfig;
import ru.tutor.model.ProductResponse;
import ru.tutor.api.steps.ProductSteps;
import ru.tutor.utils.AllureAttachments;

import java.util.List;
import java.util.stream.Stream;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

@Epic("Products Domain API")
@Feature("Product Names in Domain Response")
@Story("Validate product names in products array for different domains")
@DisplayName("🏷️ Тесты названий продуктов в доменных ответах")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductNameTest {
    
    private final ProductSteps productSteps = new ProductSteps();
    
    static Stream<String> productIdsProvider() {
        return ProductConfig.PRODUCT_DOMAINS.keySet().stream();
    }
    
    @ParameterizedTest
    @MethodSource("productIdsProvider")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("✅ Проверка name в products для: {0}")
    @Order(1)
    void validateProductsName_ForAllProducts(String productId) {
        ProductResponse domainResponse = step("Получить доменный ответ для " + productId, () ->
            productSteps.validateSuccessfulDomainResponse(productId)
        );
        
        step("Проверить поле name в массиве products", () ->
            productSteps.validateProductsName(productId, domainResponse)
        );
        
        step("Проверить ожидаемое название продукта", () ->
            productSteps.validateExpectedProductName(productId, domainResponse)
        );
        
        step("Проанализировать структуру products", () ->
            productSteps.analyzeProducts(productId, domainResponse)
        );
    }
    
    @Test
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("✅ Детальная проверка Burnout продукта")
    @Order(3)
    void validateBurnoutProduct_Detailed() {
        String productId = "burnout";
        
        ProductResponse domainResponse = productSteps.validateSuccessfulDomainResponse(productId);
        
        step("Проверить что products содержит Burnout продукт", () -> {
            List<ProductResponse.Product> products = domainResponse.getProducts();
            assertFalse(products.isEmpty(), "Products array should not be empty");
            
            ProductResponse.Product mainProduct = products.getFirst();
            assertEquals("Как бороться с выгоранием", mainProduct.getName(), 
                "Основной продукт должен называться 'Как бороться с выгоранием'");
        });
        
        step("Проверить авторов Burnout продукта", () -> {
            ProductResponse.Product mainProduct = domainResponse.getProducts().getFirst();
            assertNotNull(mainProduct.getAuthors(), "Authors should not be null");
            assertFalse(mainProduct.getAuthors().isEmpty(), "Authors should not be empty");
            
            ProductResponse.Author firstAuthor = mainProduct.getAuthors().getFirst();
            assertEquals("Игорь Свенторжецкий", firstAuthor.getName(), 
                "First author should be 'Игорь Свенторжецкий'");
            assertEquals("Психолог", firstAuthor.getProfession(),
                "Author profession should be 'Психолог'");
        });
        
        step("Зафиксировать результат Burnout", () -> {
            ProductResponse.Product mainProduct = domainResponse.getProducts().getFirst();
            ProductResponse.Author firstAuthor = mainProduct.getAuthors().getFirst();
            
            AllureAttachments.attachText("Burnout Product Details",
                "Name: " + mainProduct.getName() + "\n" +
                "ID: " + mainProduct.getId() + "\n" +
                "Price: " + mainProduct.getPrice() + " " + mainProduct.getCurrency() + "\n" +
                "Author: " + firstAuthor.getName() + " (" + firstAuthor.getProfession() + ")\n" +
                "Description: " + firstAuthor.getDescription());
        });
    }
    
    @ParameterizedTest
    @MethodSource("productIdsProvider")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("🔍 Проверка структуры products для: {0}")
    @Order(4)
    void validateProductsStructure_ForAllProducts(String productId) {
        ProductResponse domainResponse = productSteps.validateSuccessfulDomainResponse(productId);
        
        step("Проверить структуру первого продукта", () ->
            productSteps.validateFirstProductStructure(productId, domainResponse)
        );
        
        step("Проанализировать products", () ->
            productSteps.analyzeProducts(productId, domainResponse)
        );
    }
    
    @Nested
    @DisplayName("⚡ Performance тесты")
    class PerformanceTests {
        
        @Test
        @Severity(SeverityLevel.MINOR)
        @DisplayName("⏱️  Проверка времени ответа для основных продуктов")
        void validateResponseTime_ForMainProducts() {
            String[] mainProducts = {"cdvg", "burnout", "panicatt", "stress"};
            
            for (String productId : mainProducts) {
                step("Измерить время ответа для " + productId, () -> {
                    long startTime = System.currentTimeMillis();
                    ProductResponse domainResponse = productSteps.validateSuccessfulDomainResponse(productId);
                    long responseTime = System.currentTimeMillis() - startTime;
                    
                    assertTrue(responseTime < 10000, 
                        "Время ответа для " + productId + " должно быть меньше 10 секунд. Фактическое: " + responseTime + "ms");
                    
                    Allure.step("Время ответа " + productId + ": " + responseTime + "ms");
                });
            }
        }
    }
    
    @Test
    @Severity(SeverityLevel.MINOR)
    @DisplayName("📊 Сводка по всем продуктам")
    @Order(99)
    void generateProductsSummary() {
        step("Собрать статистику по всем продуктам", () -> {
            StringBuilder summary = new StringBuilder();
            summary.append("=== СВОДКА ПО ПРОДУКТАМ ===\n\n");
            
            int totalProducts = ProductConfig.PRODUCT_DOMAINS.size();
            summary.append("Всего продуктов: ").append(totalProducts).append("\n\n");
            
            summary.append("СПИСОК ПРОДУКТОВ:\n");
            ProductConfig.PRODUCT_DOMAINS.forEach((productId, info) ->
                summary.append("  - ").append(productId)
                       .append(" -> ").append(info.expectedProductName())
                       .append(" (").append(info.subdomain()).append(".tutorplace.ru)")
                       .append("\n")
            );
            
            AllureAttachments.attachText("Products Domain Summary", summary.toString());
        });
    }
}
