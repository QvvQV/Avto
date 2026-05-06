package ru.tutor.page.admin;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.tutor.utils.AllureAttachments;

import java.time.Duration;
import java.util.List;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;

public class AdminOrderDetails {

    // Таймаут
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    // Селекторы
    private SelenideElement getDateElement(OrderDateType dateType) {
        return $$("div.order-info-entry")
                .findBy(text(dateType.getDisplayName()))
                .$("div", 1)
                .shouldBe(visible, DEFAULT_TIMEOUT);
    }

    private SelenideElement getProductElement() {
        return $$("div.order-info-entry")
                .findBy(text("Продукт"))
                .$(".order-info-entry__children")
                .shouldBe(visible, DEFAULT_TIMEOUT);
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
                .shouldBe(visible, DEFAULT_TIMEOUT)
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
        getDateElement(OrderDateType.CREATED).shouldBe(visible, DEFAULT_TIMEOUT);
        getDateElement(OrderDateType.EXPIRES).shouldBe(visible, DEFAULT_TIMEOUT);
        getDateElement(OrderDateType.NEXT_PAYMENT).shouldBe(visible, DEFAULT_TIMEOUT);
    }

    @Step("Обновление страницы для получения актуальных данных")
    public void refreshPage() {
        step("Обновление страницы для получения актуальных данных", ()-> {
            sleep(10000);
            Selenide.refresh();
            verifyAllDatesAreDisplayed();
        });
    }

    @Step("Отображение всех дат заказа")
    public OrderDates displayAllDates() {
        OrderDates dates = getAllOrderDates();
        AllureAttachments.attachText("Все даты заказа", dates.toString());
        return dates;
    }

    @Step("Получение ID первого продукта")
    public String getFirstProductId() {
        return step("Получение ID первого продукта", () -> {
            String productText = getProductElement()
                    .$$("a[href^='/product/edit/']")
                    .first()
                    .shouldBe(visible, DEFAULT_TIMEOUT)
                    .getText()
                    .trim();

            // Извлекаем ID из текста "247 - Премиум подписка"
            String productId = extractProductId(productText);
            AllureAttachments.attachText("ID первого продукта", productId);
            return productId;
        });
    }

    @Step("Получение названия первого продукта")
    public String getFirstProductName() {
        String productText = getProductElement()
                .$$("a")
                .first()
                .shouldBe(visible, DEFAULT_TIMEOUT)
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
        public record OrderDates(String created, String expires, String nextPayment) {

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
        // Извлекаем название из текста "247 - Премиум подписка"
        if (productText != null && productText.contains(" - ")) {
            String[] parts = productText.split(" - ", 2);
            return parts.length > 1 ? parts[1].trim() : productText;
        }
        return productText;
    }

    //    DTO класс для хранения информации о продукте
        public record ProductInfo(String id, String name) {

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
        public record OrderFullInfo(OrderDates dates, ProductInfo firstProduct, List<ProductInfo> allProducts) {

        @Override
            public String toString() {
                return String.format(
                        "Даты: %s, Первый продукт: %s, Всего продуктов: %d",
                        dates.toString(), firstProduct.toString(), allProducts.size()
                );
            }
        }
}