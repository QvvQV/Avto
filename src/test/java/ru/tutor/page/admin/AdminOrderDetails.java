package ru.tutor.page.admin;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.tutor.model.ProductResponse;
import ru.tutor.utils.AllureAttachments;

import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.sleep;

public class AdminOrderDetails {

    // Селекторы
    private static SelenideElement getDateElement(OrderDateType dateType) {
        return $$("div.order-info-entry")
                .findBy(text(dateType.getDisplayName()))
                .$("div", 1)
                .shouldBe(visible, Duration.ofSeconds(10));
    }

    private static SelenideElement getProductElement() {
        return $$("div.order-info-entry")
                .findBy(text("Продукт"))
                .$(".order-info-entry__children")
                .shouldBe(visible, Duration.ofSeconds(10));
    }

    public enum OrderDateType {
        CREATED("Создан"),
        EXPIRES("Истекает"),
        NEXT_PAYMENT("След. платеж"),
        PRODUCT("Продукт");

        private final String displayName;

        OrderDateType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Универсальный метод для получения даты любого типа
    @Step("Получение даты '{dateType}'")
    private String getDate(OrderDateType dateType) {
        return getDateElement(dateType)
                .getText()
                .trim();
    }

    // Универсальный метод для отображения даты любого типа
    @Step("Поиск и отображения даты '{dateType}'")
    public String displayDate(OrderDateType dateType) {
        String dateValue = getDate(dateType);
        String message = String.format("Дата '%s' : %s", dateType.getDisplayName(), dateValue);
        AllureAttachments.attachText(message, dateValue);
        return dateValue;
    }

    // Специфичные методы для обратной совместимости
    @Step("Получение даты 'Создан'")
    public String getCreationDate() {
        return getDate(OrderDateType.CREATED);
    }

    @Step("Поиск и отображение даты создания заказа")
    public String displayCreationDate() {
        return displayDate(OrderDateType.CREATED);
    }

    @Step("Получение даты 'Истекает'")
    public String getExpirationDate() { // Исправлено опечатку в названии метода
        return getDate(OrderDateType.EXPIRES);
    }

    @Step("Поиск и отображение даты 'Истекает'")
    public String displayExpirationDate() {
        return displayDate(OrderDateType.EXPIRES);
    }

    @Step("Получение даты 'Следующий платёж'")
    public String getNextPayment() {
        return getDate(OrderDateType.NEXT_PAYMENT);
    }

    @Step("Поиск и отображение даты 'Следующий платёж'") // Исправлено название шага
    public String displayNextPayment() {
        return displayDate(OrderDateType.NEXT_PAYMENT);
    }

    @Step("Получение всех дат заказа")
    public OrderDates getAllOrderDates() {
        return new OrderDates(
                getCreationDate(),
                getExpirationDate(),
                getNextPayment()
        );
    }

    @Step("Проверка что все даты заказа отображаются")
    public void verifyAllDatesAreDisplayed() {
        getDateElement(OrderDateType.CREATED).shouldBe(visible, Duration.ofSeconds(10));
        getDateElement(OrderDateType.EXPIRES).shouldBe(visible, Duration.ofSeconds(10));
        getDateElement(OrderDateType.NEXT_PAYMENT).shouldBe(visible, Duration.ofSeconds(10));
    }

    @Step("Обновление страницы для получения актуальных данных")
    public void refreshPage() {
        sleep(7000);
        Selenide.refresh();
        verifyAllDatesAreDisplayed();
    }

    @Step("Отображение всех дат заказа")
    public OrderDates displayAllDates() {
        OrderDates dates = getAllOrderDates();
        AllureAttachments.attachText("Все даты заказа", dates.toString());
        return dates;
    }

    @Step("Получение ID первого продукта")
    public String getFirstProductId() {
        String productText = getProductElement()
                .$$("a")
                .first()
                .shouldBe(visible, Duration.ofSeconds(10))
                .getText()
                .trim();

        // Извлекаем ID из текста "135 - Премиум подписка"
        String productId = extractProductId(productText);
        AllureAttachments.attachText("ID первого продукта", productId);
        return productId;
    }

    @Step("Получение названия первого продукта")
    public String getFirstProductName(){
        String productText = getProductElement()
                .$$("a")
                .first()
                .shouldBe(visible, Duration.ofSeconds(10))
                .getText()
                .trim();

        // Извлекаем название из текста
        String productName = extractProductName(productText);
        AllureAttachments.attachText("название первого продукта", productName);
        return productName;
    }

    @Step("Получение информации о первом продукте")
    public ProductInfo getFirstProductInfo() {
        String productText = getProductElement()
                .$$("a")
                .first()
                .getText()
                .trim();

        String productId = extractProductId(productText);
        String productName = extractProductName(productText);

        ProductInfo productInfo = new ProductInfo(productId, productName);
        AllureAttachments.attachText("Информация о первом продукте", productInfo.toString());
        return productInfo;
    }

    @Step("Проверка что первый продукт имеет ID {expectedProductId}")
    public void verifyFirstProductId(String expectedProductId) {
        String actualProductId = getFirstProductId();
        if (!actualProductId.equals(expectedProductId)) {
            throw new AssertionError(String.format(
                    "Неверный ID родукта. Ожидалось: %s, Фактически: %s",
                    expectedProductId, actualProductId
            ));
        }
        AllureAttachments.attachText("Проверка ID продукта", String.format("ID продукта корректен: %s", actualProductId));
    }

    @Step("Проверка что первый продукт является Premium подпиской")
    public void verifyFirstProductIsPremium() {
        String productName = getFirstProductName().toLowerCase();
        boolean isPremium = productName.contains("премиум") || productName.contains("premium");

        if (!isPremium) {
            throw new AssertionError(String.format("Продукт не является Premium подпиской. Название: %s",
                    getFirstProductName()
            ));
        }

        AllureAttachments.attachText("Проверка типа продукта", String.format("Продукт является Premium подпиской: %s", getFirstProductName()
        ));
    }

    @Step("Получение всех продуктов")
    public java.util.List<ProductInfo> getAllProducts() {
        return getProductElement()
                .$$("a")
                .stream()
                .map(element -> {
                    String productText = element.getText().trim();
                    return new ProductInfo(
                        extractProductId(productText),
                        extractProductName(productText)
                    );
                })
                .collect(java.util.stream.Collectors.toList());
    }

    @Step("Получение полной информации о заказе")
    public OrderFullInfo getOrderFullInfo() {
        return new OrderFullInfo(
                getAllOrderDates(),
                getFirstProductInfo(),
                getAllProducts()
        );
    }

    // DTO класс для хранения всех дат заказа
    public static class OrderDates {
        private final String created;
        private final String expires;
        private final String nextPayment;

        public OrderDates(String created, String expires, String nextPayment) {
            this.created = created;
            this.expires = expires;
            this.nextPayment = nextPayment;
        }

        public String getCreated() {
            return created;
        }

        public String getExpires() {
            return expires;
        }

        public String getNextPayment() {
            return nextPayment;
        }

        @Override
        public String toString() {
            return String.format(
                    "Создан: %s, Истекает: %s, След. платеж: %s",
                    created, expires, nextPayment
            );
        }
    }

    // Вспомогательные методы для извлечения ID и названия продукта
    private String extractProductId(String productText) {
        // Извлекаем ID из текста
        if (productText != null && productText.contains(" - ")) {
            return productText.split(" - ")[0].trim();
        }
        return "unknown";
    }

    private String extractProductName(String productText) {
        // Извлекаем название из текста "135 - Премиум подписка"
        if (productText != null && productText.contains(" - ")){
            String[] parts = productText.split(" - ", 2);
            return parts.length > 1 ? parts[1].trim() : productText;
        }
        return productText;
    }

//    DTO класс для хранения информации о продукте
    public static class ProductInfo {
        private final String id;
        private final String name;

        public ProductInfo(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public boolean isPremium() {
            return name.toLowerCase().contains("премиум") || name.toLowerCase().contains("premium");
        }

        public boolean isLite() {
            return name.toLowerCase().contains("лайт") || name.toLowerCase().contains("lite");
        }

        public boolean isLifetime() {
            return name.toLowerCase().contains("пожизненный") || name.toLowerCase().contains("lifetime");
        }

        @Override
    public String toString() {
            return String.format("%s - %s", id, name);
        }
}

// DTO класс для полной информации о заказе
    public static class OrderFullInfo {
        private final OrderDates dates;
        private final ProductInfo firstProduct;
        private final java.util.List<ProductInfo> allProducts;

        public OrderFullInfo(OrderDates dates, ProductInfo firstProduct, java.util.List<ProductInfo> allProducts) {
            this.dates = dates;
            this.firstProduct = firstProduct;
            this.allProducts = allProducts;
        }

        public OrderDates getDates() {
            return dates;
        }

        public ProductInfo getFirstProduct() {
            return firstProduct;
        }

        public java.util.List<ProductInfo> getAllProducts() {
            return allProducts;
        }

        @Override
    public String toString() {
            return String.format(
                    "Даты: %s, Первый продукт: %s, Всего продуктов: %d",
                    dates.toString(), firstProduct.toString(), allProducts.size()
            );
        }
}
}
