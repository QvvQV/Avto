package ru.tutor.constants;

/**
 * Эндпоинты заказов: создание/оплата, подписки, refund, A/B тесты.
 */
public final class EndpointsOrders {

    private EndpointsOrders() {
        throw new IllegalStateException("Utility class");
    }

    // ==================== Заказы и оплата ====================
    public static final String ORDER = "/order";

    /**
     * Статуса заказа после создания
     */
    public static final String ORDER_STATUS = "/order/status/%s";

    /**
     * Проверка успешного создания заказа бек
     */
    public static final String ORDER_SUCCESS = "/order/success/%s";

    /**
     * Доступные платёжные системы
     */
    public static final String AVAILABLE_PAYMENT_SYSTEMS = "/available-payment-systems";

    // ==================== REFUND TICKETS ENDPOINTS ====================
    /**
     * Список заявок на возврат
     */
    public static final String REFUND_TICKETS = "/user/refund-tickets";

    /**
     * Создание заявки на возврат
     */
    public static final String REFUND_TICKETS_CREATE = "/user/refund-tickets/create";

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

    public static final String ORDERS_PAUSE = "/user/orders/pause/%d";

    public static final String ORDERS_UNPAUSE = "/user/orders/unpause";

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
}