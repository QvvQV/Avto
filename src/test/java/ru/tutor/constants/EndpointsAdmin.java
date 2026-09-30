package ru.tutor.constants;

/**
 * Эндпоинты админки.
 */
public final class EndpointsAdmin {

    private EndpointsAdmin() {
        throw new IllegalStateException("Utility class");
    }

    // ==================== Admin ====================
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

    /**
     * Оплата заказа
     */
    public static final String ADMIN_PAYMENTS_CREATE = "/admin/payments/create";

    /**
     * Отмена подписки заказа в админке
     */
    public static final String ADMIN_ORDERS_UNSUBSCRIBE = "/admin/orders/%s/unsubscribe";

    /**
     * Возврат (refund) платежа админка
     */
    public static final String ADMIN_PAYMENTS_REFUND = "/admin/payments/refund";

    /**
     * Отклонение заявки на выплату средств по клубу
     */
    public static final String ADMIN_USER_CLUB_WITHDRAWAL_REJECT = "/admin/user/club/withdrawal/%s/reject";

    /**
     * Заявка на выплату средств
     */
    public static final String ADMIN_USER_CLUB_WITHDRAWAL = "/admin/user/club/withdrawal/%s";

    /**
     * Перевод в статус Оплачено
     */
    public static final String ADMIN_USER_WITHDRAWAL_CLUB = "/admin/user/club/withdrawal";
}