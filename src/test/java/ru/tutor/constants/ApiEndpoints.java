package ru.tutor.constants;

import ru.tutor.config.TestConfig;

/**
 * Централизованное хранение всех API эндпоинтов
 * Содержит только константы путей, без логики
 */
public final class ApiEndpoints {

    private ApiEndpoints() {
        throw new IllegalStateException("Utility class");
    }

    // ==================== API URL ====================
    public static final String API_BASE_URL = TestConfig.getCurrentApiUrl();

    // ==================== Аутентификация ====================
    public static final String LOGIN = "/user/login";
    public static final String LOGOUT = "/user/logout";
    public static final String REGISTER = "/register";
    public static final String ORDER = "/order";


    // ==================== Профиль пользователя ====================
    /** Получение профиля пользователя */
    public static final String USER_PROFILE =  "/user/profile";

    /** Получение игровой статистики */
    public static final String USER_GAME_STATS = "user/game-stats";

    /** Страница достижений */
    public static final String USER_ACHIEVEMENTS = "/user/achievements";


    // ==================== PRODUCT ENDPOINTS ====================
    /** Отписка пользователя до Лайт */
    public static final String ORDERS_TOGGLE_SUBSCRIPTION =  "/user/orders/toggle_subscription/%d";

    /** Реактивация пользователя Лайт по внутренней почте */
    public static final String ORDERS_REACTIVATE_PREMIUM =  "/user/orders/reactivate-premium";

    /** Восстановление премиума по ссылке из email-рассылки */
    public static final String ORDERS_REACTIVATE_PREMIUM_BY_EMAIL = "/user/orders/reactivate-premium-by-email";

    /** Тариф Подарок */
    public static final String ORDERS_GET_PREMIUM_GIFT = "/user/orders/get-premium-gift";

    /** Список продуктов */
    public static final String PRODUCTS_LIST = "/user/products";

    /** Номер продукта */
    public static final String PRODUCTS_ID = "/user/products/%s";

    /** Получение информации по модулю */
    public static final String PRODUCTS_MODULE = "/user/products/%s/modules/%d";

    /** Тайминг видео */
    public static final String VIDEOS_WATCHED = "/user/videos/watched/create";

    /** Завершение модуля (пройдено) */
    public static final String PRODUCTS_COMPLETED_MODULE = "/user/orders/%s/completed_module/%d";

    /** Получение списка всех продуктов */
    public static final String ALL_PRODUCTS =  "/user/products/all";

    /** Список достижений по продукту*/
    public static final String ACHIEVEMENTS_PRODUCT_ID = "/user/achievements/%s";

    // ==================== Игровое колесо ====================
    /** Запуск игры на колесе */
    public static final String GAME_WHEEL_START = "/user/game-wheel/start-game";

    /** Получение списка наград игрового колеса */
    public static final String GAME_WHEEL_REWARDS = "/user/game-wheel/rewards";

    /** Получение списка наград пользователя */
    public static final String GAME_REWARD_LIST = "/user/reward/list";

    /** Удаление подарка из списка призов */
    public static final String GAME_REWARD_OPEN_ID = "/user/reward/open/%d";

    /** Получение истории забранных наград пользователя */
    public static final String GAME_REWARD_HISTORY = "/user/reward/history";

    // ==================== PROMO CODE ENDPOINTS ====================
    public static final String PROMO_CODES_APPLY = "/user/promo-codes/apply";


    // ==================== Клуб ====================
    /** Сохранение деталей самозанятого */
    public static final String CLUB_SZ_DETAILS = "/user/club/sz-details";

    /** Сохранение деталей ИП */
    public static final String CLUB_IP_DETAILS = "/user/club/ip-details";

    /** Получение деталей */
    public static final String CLUB_DETAILS = "/user/club/details";

    /** Получение статистики клуба */
    public static final String CLUB_STATISTICS = "/user/club/statistics";

    /** Получение списка участников клуба */
    public static final String CLUB_MEMBER = "/user/club/member";

    /** Запрос на вывод средств */
    public static final String CLUB_REQUEST_WITHDRAWAL = "/user/club/request-withdrawal";

    /** Запрос на историю оплат */
    public static final String CLUB_PAYMENTS = "/user/club/payments";

}
