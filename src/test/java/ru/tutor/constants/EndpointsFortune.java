package ru.tutor.constants;

/**
 * Эндпоинты игрового колеса и достижений.
 */
public final class EndpointsFortune {

    private EndpointsFortune() {
        throw new IllegalStateException("Utility class");
    }

    // ==================== Игровое колесо ====================
    /**
     * Запуск игры на колесе
     */
    public static final String GAME_WHEEL_START = "/user/game-wheel/start-game";

    /**
     * Получение списка наград игрового колеса
     */
    public static final String GAME_WHEEL_REWARDS = "/user/game-wheel/rewards";

    /**
     * Получение списка наград пользователя
     */
    public static final String GAME_REWARD_LIST = "/user/reward/list";

    /**
     * Удаление подарка из списка призов
     */
    public static final String GAME_REWARD_OPEN_ID = "/user/reward/open/%d";

    /**
     * Получение истории забранных наград пользователя
     */
    public static final String GAME_REWARD_HISTORY = "/user/reward/history";

    /**
     * Получение недавних победителей
     */
    public static final String GAME_WHEEL_REWARDS_RECENTLY_WON_ALL = "/user/game-wheel/rewards-recently-won-all";

    /**
     * Таймер до бесплатной прокрутки
     */
    public static final String GAME_WHEEL_TIMER_LEFT_SECONDS = "/user/game-wheel/timer-left-seconds";

    /**
     * Награды колеса по группам
     */
    public static final String GAME_WHEEL_REWARDS_GROUPS = "/user/game-wheel/rewards_groups";

    // ==================== Игровая статистика и достижения ====================
    /**
     * Получение игровой статистики
     */
    public static final String USER_GAME_STATS = "/user/game-stats";

    /**
     * Рейтинг
     */
    public static final String USER_GAME_LEADER_RATING = "/user/game-leader/rating";

    /**
     * Получение достижений
     */
    public static final String USER_ACHIEVEMENTS = "/user/achievements";

    /**
     * Список достижений по продукту
     */
    public static final String ACHIEVEMENTS_PRODUCT_ID = "/user/achievements/%s";

    /**
     * Она отдает в ответе 6 ачивок по курсам
     */
    public static final String USER_ACHIEVEMENTS_LAST = "/user/achievements/last";
}