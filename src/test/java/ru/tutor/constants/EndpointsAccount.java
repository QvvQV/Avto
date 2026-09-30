package ru.tutor.constants;

/**
 * Эндпоинты аккаунта: аутентификация, профиль, задания, онбординг, соц.-интеграции.
 */
public final class EndpointsAccount {

    private EndpointsAccount() {
        throw new IllegalStateException("Utility class");
    }

    // ==================== Аутентификация ====================
    /**
     * вход в систему с email и паролем
     */
    public static final String LOGIN = "/user/login";

    /**
     * завершает сессию пользователя
     */
    public static final String LOGOUT = "/user/logout";

    /**
     * создание нового пользователя
     */
    public static final String REGISTER = "/register";

    public static final String LOGIN_TOKEN = "/login_token";

    /**
     * авторизация/переход по номеру заказа и опциональному коду
     */
    public static final String USER_AUTH = "/user/auth/%s/%s";

    public static final String ORDER_RESET = "/user/reset";

    /**
     * вход через Яндекс ID
     */
    public static final String YA_LOGIN = "/user/yalogin";

    /**
     * регистрация через Yandex ID
     */
    public static final String YA_REGISTER = "/user/ya-register";

    /**
     * проверка авторизации
     */
    public static final String USER_CHECK = "/user";

    /**
     * сброс пароля
     */
    public static final String RESET_PASSWORD = "/reset";

    // ==================== Профиль пользователя ====================
    /**
     * Получение профиля пользователя
     */
    public static final String USER_PROFILE = "/user/profile";

    /**
     * Смена пароля
     */
    public static final String USER_EDIT_PASSWORD = "/user/edit/pass";

    /**
     * Фото пользователя
     */
    public static final String USER_PHOTO = "/user/photo";

    /**
     * Отправляет click_id, utm-метки, ID устройства и прочие параметры аналитики
     */
    public static final String USER_HAS_SENT_TO_TRACKERS = "/user/has_sent_to_trackers";

    /**
     * Изменение пользовательских данных
     */
    public static final String USER_EDIT_DATA = "/user/edit/data";

    /**
     * изменение email
     */
    public static final String USER_EDIT_EMAIL = "/user/edit/email";

    /**
     * подтверждение email
     */
    public static final String USER_CONFIRM_EMAIL = "/user/confirm/email";

    /**
     * обновление пользовательских категорий
     */
    public static final String USER_EDIT_CATEGORIES = "/user/edit/categories";

    /**
     * деактивация профиля
     */
    public static final String USER_PROFILE_DEACTIVATE = "/user/profile/deactivate";

    /**
     * отправка пользовательской оценки NPS
     */
    public static final String USER_NPS = "/user/nps";

    // ==================== Задания пользователя ====================
    /**
     * Задания пользователя
     */
    public static final String USER_TASKS = "/user/tasks";

    /**
     * заменить задание
     */
    public static final String USER_TASKS_CHANGE = "/user/tasks/change/%s";

    /**
     * отметить задание завершенным
     */
    public static final String USER_TASKS_FINISH = "/user/tasks/finish/%s";

    /**
     * сбросить текущий список
     */
    public static final String USER_TASKS_RESET = "/user/tasks/reset";

    // ==================== ONBOARDING ENDPOINTS ====================
    /**
     * Сохранение прогресса онбординга
     */
    public static final String ONBOARDING_PROGRESS = "/user/onboarding/progress";

    /**
     * Получение награды за онбординг
     */
    public static final String ONBOARDING_REWARD = "/user/onboarding/reward";

    // ==================== SOCIAL INTEGRATIONS ENDPOINTS ====================
    /**
     * Связка с Telegram
     */
    public static final String PROFILE_LINK_WITH_TELEGRAM = "/user/profile/link-with-telegram";

    /**
     * Экспорт инвайт-ссылки YouTube
     */
    public static final String YT_EXPORT_INVITE_LINK = "/user/yt/export-inviteLink";

    /**
     * Ссылка на отзыв в Яндексе
     */
    public static final String YANDEX_REVIEW_LINK = "/user/yandex/review-link";

    /**
     * Экспорт чат-инвайт ссылки Telegram
     */
    public static final String TELEGRAM_EXPORT_CHAT_INVITE_LINK = "/user/telegram/export-chat-inviteLink";

    /**
     * Экспорт инвайт-ссылки VK
     */
    public static final String VK_EXPORT_INVITE_LINK = "/user/vk/export-inviteLink";
}