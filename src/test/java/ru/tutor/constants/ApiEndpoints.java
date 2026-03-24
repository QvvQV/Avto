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
    public static final String apiBaseUrl = TestConfig.getCurrentApiUrl();

    // ==================== Аутентификация ====================
    public static final String LOGIN = "/user/login";
    public static final String LOGOUT = "/user/logout";


    // ==================== Профиль пользователя ====================
    /** Получение профиля пользователя */
    public static final String USER_PROFILE =  "/user/profile";


    /** Получение игровой статистики */
    public static final String USER_GAME_STATS = "user/game-stats";



    // ==================== Отписка ====================
    /** Отписка пользователя до Лайт */
    public static final String ORDERS_TOGGLE_SUBSCRIPTION =  "/user/orders/toggle_subscription/%d";

    /** Реактивация пользователя Лайт по внутренней почте */
    public static final String ORDERS_REACTIVATE_PREMIUM =  "/user/orders/reactivate-premium";

    /** Восстановление премиума по ссылке из email-рассылки */
    public static final String ORDERS_REACTIVATE_PREMIUM_BY_EMAIL = "/user/orders/reactivate-premium-by-email";



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
