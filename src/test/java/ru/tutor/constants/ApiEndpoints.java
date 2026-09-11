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

    /**
     * Статуса заказа после создания
     */
    public static final String ORDER_STATUS = "/order/status/%s";

    /**
     * Проверка успешного создания заказа бек
     */
    public static final String ORDER_SUCCESS = "/order/success/%s";

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


    public static final String ORDER = "/order";
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
     * Получение игровой статистики
     */
    public static final String USER_GAME_STATS = "/user/game-stats";

    /**
     * Страница достижений
     */
    public static final String USER_ACHIEVEMENTS = "/user/achievements";

    /**
     * Рейтинг
     */
    public static final String USER_GAME_LEADER_RATING = "/user/game-leader/rating";

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


    // ==================== PRODUCT ENDPOINTS ====================

    /**
     * Список продуктов
     */
    public static final String PRODUCTS_LIST = "/user/products";

    /**
     * Номер продукта
     */
    public static final String PRODUCTS_ID = "/user/products/%s";

    /**
     * Получение информации по модулю
     */
    public static final String PRODUCTS_MODULE = "/user/products/%s/modules/%d";

    /**
     * Завершение модуля (пройдено)
     */
    public static final String PRODUCTS_COMPLETED_MODULE = "/user/orders/%s/completed_module/%d";

    /**
     * Получение списка всех продуктов
     */
    public static final String ALL_PRODUCTS = "/user/products/all";

    /**
     * Список продуктов (app-версия)
     */
    public static final String PRODUCTS_LIST_APP =  "/user/products-app";

    /**
     * app-вариант списка всех продуктов
     */
    public static final String ALL_PRODUCTS_APP = "/user/products/all-app";

    /**
     * app-вариант списка премьер
     */
    public static final String PRODUCTS_PREMIERS_APP = "/user/products/premiers-app";

    /**
     *  продукт премиум-99
     */
    public static final String PRODUCTS_LIGHT_WEIGHT = "/user/products/light-weight";

    /**
     * продукт лайт-29
     */
    public static final String PRODUCTS_LITE_LIGHTWEIGHT = "/user/products/lite-lightweight";

    /**
     * список мини-курсов
     */
    public static final String MINI_COURSES =  "/user/mini-courses";

    /**
     * действие по мини-курсу
     */
    public static final String PRODUCTS_MINI_ID = "/user/products-mini/%s";

    /**
     * список доступных вложений у курса
     */
    public static final String PRODUCTS_ATTACHED_FILES = "/user/products/%s/attached-files";

    /**
     * доступ к конкретному вложению курса
     */
    public static final String PRODUCTS_FILES = "/user/products/%s/files/%s";

    /**
     * Получение информации о курсе
     */
    public static final String USER_COURSES_ID = "/user/courses/%s";

    /**
     * Отзывы курса
     */
    public static final String USER_COURSES_REVIEWS = "/user/courses/%s/reviews";

    /**
     * Рекомендации по курсу
     */
    public static final String USER_COURSES_RECOMMENDATIONS = "/user/courses/%s/recommendations";

    /**
     * список вебинаров пользователя
     */
    public static final String USER_WEBINARS = "/user/%s/webinars";

    /**
     * Данные конкретного вебинара
     */
    public static final String USER_WEBINARS_DETAILS = "/user/%s/webinars/%s/%s";

    /**
     * Чат вебинара
     */
    public static final String USER_WEBINARS_CHAT =  "/user/%s/webinars/%s/%s/chat";

    /**
     * b-content
     */
    public static final String PRODUCTS_MODULE_B_CONTENT = "/user/products/%s/module/%d/b-content";

    /**
     * Список квизов продукта
     */
    public static final String USER_PRODUCT_QUIZZES = "/user/%s/product/%s/quizzes";

    /**
     * Детали квиза
     */
    public static final String USER_PRODUCT_QUIZZES_DETAILS = "/user/%s/products/%s/quizzes/%s";

    /**
     * Вопросы квиза
     */
    public static final String USER_PRODUCT_QUIZZES_QUESTIONS = "/user/%s/products/%s/quizzes/%s/questions";

    /**
     * Ранее отправленные ответы по квизу
     */
    public static final String USER_QUIZ_ANSWERS = "/user/%s/quiz_answers/%s";

    /**
     * Отправка ответов на квиз
     */
    public static final String USER_QUIZ_ANSWERS_POST = "/user/%s/quiz_answers";

    /**
     * Получение списка категорий
     */
    public static final String CATEGORIES = "/categories";

    /**
     * Получение списка избранных продуктов
     */
    public static final String USER_FAVORITE_PRODUCTS = "/user/favorite-products";

    /**
     * Добавление/Удаление продукта в избранные
     */
    public static final String USER_FAVORITE_PRODUCTS_ID = "/user/favorite-products/%s";

    /**
     * Список достижений по продукту
     */
    public static final String ACHIEVEMENTS_PRODUCT_ID = "/user/achievements/%s";

    /**
     * Она отдает в ответе 6 ачивок по курсам
     */
    public static final String USER_ACHIEVEMENTS_LAST =  "/user/achievements/last";

    /**
     * История поиска
     */
    public static final String SEARCH_HISTORY = "/user/search/history";

    /**
     * Уведомление/заявка по поиску
     */
    public static final String SEARCH_NOTIFY = "/user/search/notify";

    /**
     * удаляет продукт из добавленных по айдишнику заказа
     */
    public static final String PRODUCT_HIDE =  "/user/orders/product/%s/hide";

    /**
     * закрепить добавленный курс
     */
    public static final String PRODUCT_PINNED =  "/user/orders/pinned/%s/1";



    // ==================== Сертификаты и Refund ====================

    /**
     * Генерация сертификата (Query: name, orderNumber)
     */
    public static final String CERTIFICATE_GENERATE = "/user/certificate/generate";

    /**
     * Получение сертификата в PDF
     */
    public static final String CERTIFICATE_PDF = "/user/certificate/pdf";

    /**
     * Получение сертификата в PNG
     */
    public static final String CERTIFICATE_PNG = "/user/certificate/png";


    // ==================== REFUND TICKETS ENDPOINTS ====================

    /**
     * Список заявок на возврат
     */
    public static final String REFUND_TICKETS = "/user/refund-tickets";

    /**
     * Создание заявки на возврат
     */
    public static final String REFUND_TICKETS_CREATE = "/user/refund-tickets/create";


    // ==================== ONBOARDING ENDPOINTS ====================

    /**
     * Сохранение прогресса онбординга
     */
    public static final String ONBOARDING_PROGRESS = "/user/onboarding/progress";

    /**
     * Получение награды за онбординг
     */
    public static final String ONBOARDING_REWARD = "/user/onboarding/reward";

    /**
     * Подписка на уведомление о продукте
     */
    public static final String PRODUCTS_NOTIFY = "/user/products-notify";

    /**
     * Отмена уведомления
     */
    public static final String PRODUCTS_NOTIFY_DELETE = "/user/products-notify/%s";


    // ==================== AB TEST ENDPOINTS ====================
    /**
     * Фиксация статуса A/B теста
     */
    public static final String ORDERS_AB_TEST_STATUS = "/user/orders/%s/ab-test-status";

    /**
     * Фиксация показа кнопки
     */
    public static final String ORDERS_AB_TEST_BUTTON_SHOWN = "/user/orders/%s/ab-test-button-shown";

    /**
     * Фиксация прогресса видео
     */
    public static final String ORDERS_AB_TEST_VIDEO_PROGRESS = "/user/orders/%s/ab-test-video-progress";

    /**
     * Фиксация фокуса
     */
    public static final String ORDERS_AB_TEST_FOCUS = "/user/orders/%s/ab-test-focus";



    // ==================== SUBSCRIPTIONS ENDPOINTS ====================
    /**
     * Отписка пользователя до Лайт
     */
    public static final String ORDERS_TOGGLE_SUBSCRIPTION = "/user/orders/toggle_subscription/%d";

    /**
     * Реактивация пользователя Лайт по внутренней почте
     */
    public static final String ORDERS_REACTIVATE_PREMIUM = "/user/orders/reactivate-premium";

    /**
     * Восстановление премиума по ссылке из email-рассылки
     */
    public static final String ORDERS_REACTIVATE_PREMIUM_BY_EMAIL = "/user/orders/reactivate-premium-by-email";

    /**
     * Тариф Подарок
     */
    public static final String ORDERS_GET_PREMIUM_GIFT = "/user/orders/get-premium-gift";

    /**
     * оформление подписки
     */
    public static final String ORDERS_SUBSCRIBE = "/user/orders/subscribe/%s";

    /**
     * возобновление подписки
     */
    public static final String ORDERS_RESUBSCRIBE = "/user/orders/resubscribe";

    /**
     * отписка
     */
    public static final String ORDERS_UNSUBSCRIBE = "/user/orders/unsubscribe";

    /**
     * История платежей
     */
    public static final String ORDERS_PAYMENTS = "/user/orders/payments";

    /**
     * Доступные платёжные системы
     */
    public static final String AVAILABLE_PAYMENT_SYSTEMS = "/available-payment-systems";

    /**
     * Переходы между тарифами
     */
    public static final String ORDERS_SWITCH_PREMIUM = "/user/orders/switch-premium";

    public static final String ORDERS_SWITCH_LITE_LIGHTWEIGHT = "/user/orders/switch-lite-lightweight";

    /**
     * Восстановление подписок
     */
    public static final String ORDERS_RESTORE_PREMIUM = "/user/orders/restore-premium";

    public static final String ORDERS_RESTORE_LITE = "/user/orders/restore-lite";

    /**
     * Ребилл
     */
    public static final String ORDERS_REBILL = "/user/orders/rebill/%s";

    public static final String ORDERS_REBILL_PRODUCT = "/user/orders/rebill_product/%s";

    public static final String ORDERS_PAUSE =  "/user/orders/pause/%d";

    public static final String ORDERS_UNPAUSE =  "/user/orders/unpause";

    /**
     * Апсейл за деньги
     */
    public static final String ORDERS_UPSALE_CONTENT_INVOICE = "/user/orders/upsale-content-invoice/%s";

    /**
     * Вариант модального окна апсейлов
     */
    public static final String ORDERS_UPSELL_VARIANT = "/user/orders/%s/upsell-variant";

    /**
     * Открытие модального окна апсейлов
     */
     public static final String ORDERS_UPSELL_MODAL_OPENED = "/user/orders/%s/upsell-modal-opened";

    /**
     * Апсейл за алмазы
     */
    public static final String ORDERS_UPSALE_CONTENT_DIAMOND = "/user/orders/upsale-content-diamond/%s";

    /**
     * информация по бонусному доступу
     */
    public static final String ORDERS_BONUS_ACCESS_INFO = "/user/orders/bonus-access-info";

    /**
     * списание кэшбека
     */
    public static final String ORDERS_SPEND_CASHBACK = "/user/orders/spend-cashback";

    /**
     * оплата lifetime-тарифа из модалки при Неактивном заказе
     */

    public static final String ORDERS_PAY_LIFETIME_TARIFF = "/user/orders/pay-lifetime-tariff";

    /**
     * оплата lifetime-тарифа через сплит платежа
     */

    public static final String ORDERS_PAY_LIFETIME_TARIFF_SPLIT = "/user/orders/pay-lifetime-tariff-split";

    /**
     * сохранение отзыва при отписке
     */

    public static final String ORDERS_STORE_FEEDBACK_UNSUBSCRIBE = "/user/orders/store-feedback-unsubscribe/%s";

    /**
     * загрузка скриншота отзыва
     */

    public static final String ORDERS_REVIEW_SCREENSHOT = "/user/orders/%s/review/screenshot";

    /**
     * состояние подписки
     */

    public static final String ORDERS_SUBSCRIPTION_STATE = "/user/orders/subscription/state";

    /**
     * реактивация премиума из внутренней почты
     */

    public static final String ORDERS_REACTIVATE_PREMIUM_BY_INTERNAL_MAIL = "/user/orders/reactivate-premium-by-internal-mail";


    // ==================== VIDEO ENDPOINTS ====================

    /**
     *  Список доступных видео в группе "Подкасты"
     */

    public static final String USER_VIDEOS = "/user/videos";

    /**
     * Список Категорий по шоу
     */
    public static final String SHOW_CATEGORIES = "/user/show-categories";

    /**
     * Информация по конкретному видео
     */
    public static final String USER_VIDEO_DETAILS_BY_ID = "/user/videos/%s";


    /**
     * Если просмотр каких-то видео был оборван до завершения модуля
     */
    public static final String VIDEOS_WATCHED_LAST =  "/user/videos/watched/last";

    /**
     * Тайминг видео
     */
    public static final String VIDEOS_WATCHED = "/user/videos/watched/create";

    /**
     * Фиксация показа экрана сторис
     */
    public static final String USER_STORIES_SCREEN_POST = "/user/stories/screen";

    /**
     * Фиксация просмотра сторис
     */
    public static final String USER_STORIES_VIEW = "/user/stories/view";


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
     * Экран колеса
     */
    public static final String USER_STORIES_SCREEN = "/user/stories-screen";

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


    // ==================== PROMO CODE ENDPOINTS ====================
    /**
     * применение промокода
     */
    public static final String PROMO_CODES_APPLY = "/user/promo-codes/apply";

    /**
     * активация подписки по промокоду
     */
    public static final String PROMO_CODES_ACTIVATE_SUBSCRIPTION = "/user/promo-codes/activate-subscription";


    // ==================== Клуб ====================
    /**
     * Сохранение деталей самозанятого
     */
    public static final String CLUB_SZ_DETAILS = "/user/club/sz-details";

    /**
     * Сохранение деталей ИП
     */
    public static final String CLUB_IP_DETAILS = "/user/club/ip-details";

    /**
     * Получение деталей
     */
    public static final String CLUB_DETAILS = "/user/club/details";

    /**
     * Получение статистики клуба
     */
    public static final String CLUB_STATISTICS = "/user/club/statistics";

    /**
     * Получение списка участников клуба
     */
    public static final String CLUB_MEMBER = "/user/club/member";

    /**
     * Запрос на вывод средств
     */
    public static final String CLUB_REQUEST_WITHDRAWAL = "/user/club/request-withdrawal";

    /**
     * Запрос на историю оплат
     */
    public static final String CLUB_PAYMENTS = "/user/club/payments";

    /**
     * рефералы пользователя
     */
    public static final String USER_CLUB_REFERRALS = "/user/%s/club/referrals";

    /**
     * конкретный реферал пользователя
     */
    public static final String USER_CLUB_REFERRALS_ID = "/user/%s/club/referrals/%s";

    /**
     * рефералы этого реферала
     */
    public static final String USER_CLUB_REFERRALS_ID_REFERRALS = "/user/%s/club/referrals/%s/referrals";



    // ==================== DASHBOARD ENDPOINTS (ПРИЛОЖЕНИЯ) ====================

    /**
     * Информация о всех приложениях
     */
    public static final String USER_DASHBOARD_INFO = "/user/dashboard/info";


    // ==================== ENDPOINTS ГЛАВНОЙ СТРАНИЦЫ ====================

    /**
     * Список историй
     */
    public static final String USER_STORIES = "/user/stories";

    /**
     * детали конкретной истории
     */
    public static final String USER_STORIES_ULID = "/user/stories/%s";

    /**
     * детали конкретного баннера
     */
    public static final String USER_BANNERS = "/user/banners/%s";

    /**
     * список тутсов с пагинацией
     */
    public static final String USER_TUTS = "/user/tuts";

    /**
     * Рандомный список категорий тутсов
     */
    public static final String USER_TUTS_RANDOM_LIST = "/user/tuts/random";

    /**
     * Рандомный список всех доступных тутсов
     */
    public static final String TUTS_RANDOM_LIST= "/user/tuts/random-list";

    /**
     * Категории тутсов
     */
    public static final String USER_TUTS_CATEGORIES = "/user/tuts-categories";

    /**
     * Запуск категории тутсов
     */
    public static final String USER_TUTS_CATEGORY_FEED =  "/user/tuts/category/%s/feed";

    /**
     * Запись факта просмотра тутса
     */
    public static final String USER_TUTS_VIEW =  "/user/tuts/%s/view";

    /**
     * Добавить лайк к тутсу
     */
    public static final String USER_TUTS_LIKES = "/user/tuts/-/likes";

    /**
     * Получить лайки тутса с пагинацией
     */
    public static final String USER_TUTS_LIKES_GET = "/user/tuts/%s/likes/";

    /**
     * Удалить реакции текущего пользователя у тутса
     */
    public static final String USER_TUTS_LIKES_DELETE = "/user/tuts/%s/likes";
    /**
     * Премьеры
     */
    public static final String USER_PRODUCTS_PREMIERS = "/user/products/premiers";

    /**
     * Топ 10 курсов
     */
    public static final String USER_PRODUCT_POSITIONS_TOP10 = "/user/product-positions/top10";

    /**
     * Сейчас смотрят
     */
    public static final String USER_PRODUCTS_WATCHING_NOW = "/user/products/watching/now";

    /**
     * Мини-тренировки
     */
    public static final String MINI_WORKOUTS= "/user/mini-workouts";

    /**
     * Список Шоу
     */
    public static final String USER_SHOWS= "/user/shows";

    // ==================== AUTHORS ENDPOINTS ====================
    /**
     * Информация об авторе
     */
    public static final String USER_AUTHOR_ID = "/user/author/%s";

    /**
     * Активация автора
     */
    public static final String USER_AUTHOR_ACTIVATE = "/user/author/activate";

    // ==================== SOCIAL INTEGRATIONS ENDPOINTS ====================

    /**
     * Экспорт инвайт-ссылки YouTube
     */
    public static final String YT_EXPORT_INVITE_LINK = "/user/yt/export-inviteLink";

    /**
     * Ссылка на отзыв в Яндексе
     */
    public static final String YANDEX_REVIEW_LINK =  "/user/yandex/review-link";

    /**
     * Связка с Telegram
     */
    public static final String PROFILE_LINK_WITH_TELEGRAM = "/user/profile/link-with-telegram";

    /**
     * Экспорт чат-инвайт ссылки Telegram
     */
    public static final String TELEGRAM_EXPORT_CHAT_INVITE_LINK = "/user/telegram/export-chat-inviteLink";

    /**
     * Экспорт инвайт-ссылки VK
     */
    public static final String VK_EXPORT_INVITE_LINK = "/user/vk/export-inviteLink";

    // ==================== DASHBOARD APPS ENDPOINTS ====================
    /**
     * Анализ блюда по фото
     */
    public static final String DASHBOARD_ANALYZE_DISH =  "/user/dashboard/analyze-dish";

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

//    === Admin ===
    /**
     * Логин
     */
    public static final String ADMIN_LOGIN = "/admin/login";

    /**
     * Заказ
     */
    public static final String ADMIN_ORDERS = "/admin/orders";

    /**
     * ID заказа
     */
    public static final String ADMIN_ORDERS_ID = "/admin/orders/%s";

    /**
     * Продление подписки (days)
     */
    public static final String ADMIN_ORDERS_EXTEND_ACCESS = "/admin/orders/extend-access";
}
