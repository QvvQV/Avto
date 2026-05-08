package ru.tutor;

import java.util.Map;

/*
 * Конфигурация продуктов для проведения рекуррентов и проверки дат по ним
 */

public class ProductConfiguration {
    private static final Map<String, ProductSettings> PRODUCTS_SETTINGS = Map.of(
            "231", new ProductSettings(14, 44, 74, "299"),
            "247", new ProductSettings(7, 30, 60, "399"),
            "258", new ProductSettings(7, 30, 60, "399"),
            "135", new ProductSettings(7,14,21, "294"),
            "180", new ProductSettings(7, 30, 60, "99"),
            "755", new ProductSettings(30, 30, 60, "399")
    );

    public static ProductSettings getSettings(String productId) {
        ProductSettings settings = PRODUCTS_SETTINGS.get(productId);
        if (settings == null) {
            throw new IllegalArgumentException("Unknown product ID: " + productId);
        }     return settings;
    }

    public record ProductSettings(int initialExpiresDays, int afterFirstPaymentDays, int afterSecondPaymentDays,
                                  String price) {
    }
}
