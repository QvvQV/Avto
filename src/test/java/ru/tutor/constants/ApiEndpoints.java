package ru.tutor.constants;

import ru.tutor.config.TestConfig;

/**
 * Общие константы API. Доменные эндпоинты разнесены по классам:
 * EndpointsAccount, EndpointsOrders, EndpointsCatalog, EndpointsContent,
 * EndpointsFortune, EndpointsClub, EndpointsPromoCode, EndpointsDashboards, EndpointsAdmin.
 * Содержит только константы путей, без логики
 */
public final class ApiEndpoints {

    private ApiEndpoints() {
        throw new IllegalStateException("Utility class");
    }

    // ==================== API URL ====================
    public static final String API_BASE_URL = TestConfig.getCurrentApiUrl();
}