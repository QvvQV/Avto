package ru.tutor;

import java.util.Map;

/**
 * Конфигурация продуктов для проведения рекуррентов и проверки дат по ним
 */

public class ProductConfiguration {
    private static final Map<String, ProductSettings> PRODUCTS_SETTINGS = Map.of(
            "231", new ProductSettings(14, 44, 74, "299"),
            "247", new ProductSettings(7, 37, 67, "399"),
            "258", new ProductSettings(7, 37, 67, "399"),
            "135", new ProductSettings(7,14,21, "294"),
            "180", new ProductSettings(7, 37, 67, "99"),
            "755", new ProductSettings(30, 37, 67, "399")
    );

    public static ProductSettings getSettings(String productId) {
        ProductSettings settings = PRODUCTS_SETTINGS.get(productId);
        if (settings == null) {
            throw new IllegalArgumentException("Unknown product ID: " + productId);
        }     return settings;
    }

    public static class ProductSettings {
        private final int initialExpiresDays;
        private final int afterFirstPaymentDays;
        private final int afterSecondPaymentDays;
        private final String price;

        public ProductSettings(int initialExpiresDays, int afterFirstPaymentDays, int afterSecondPaymentDays, String price) {
            this.initialExpiresDays = initialExpiresDays;
            this.afterFirstPaymentDays = afterFirstPaymentDays;
            this.afterSecondPaymentDays = afterSecondPaymentDays;
            this.price = price;
        }

        // Геттеры
        public int getInitialExpiresDays(){
            return initialExpiresDays;
        }

        public int getAfterFirstPaymentDays() {
            return afterFirstPaymentDays;
        }

        public int getAfterSecondPaymentDays() {
            return afterSecondPaymentDays;
        }

        public String getPrice() {
            return price;
        }
    }
}
