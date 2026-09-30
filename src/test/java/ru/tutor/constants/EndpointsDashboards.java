package ru.tutor.constants;

/**
 * Эндпоинты дашбордов (приложения).
 */
public final class EndpointsDashboards {

    private EndpointsDashboards() {
        throw new IllegalStateException("Utility class");
    }

    // ==================== DASHBOARD ENDPOINTS (ПРИЛОЖЕНИЯ) ====================
    /**
     * Информация о всех приложениях
     */
    public static final String USER_DASHBOARD_INFO = "/user/dashboard/info";

    // ==================== DASHBOARD APPS ENDPOINTS ====================
    /**
     * Анализ блюда по фото
     */
    public static final String DASHBOARD_ANALYZE_DISH = "/user/dashboard/analyze-dish";

    /**
     * Загрузка фото для хиромантии
     */
    public static final String DASHBOARD_PALMISTRY_PHOTO = "/user/dashboard/palmistry/photo";

    /**
     * Получение дашборда (GET)
     */
    public static final String USER_ENTITIES_GET = "/user/entities/%s/%s";

    /**
     * Создание/обновление дашборда (POST)
     */
    public static final String USER_ENTITIES_POST = "/user/entities/%s/%s";

    /**
     * Полная замена значения (PUT)
     */
    public static final String USER_ENTITIES_PUT = "/user/entities/%s/%s";
}