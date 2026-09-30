package ru.tutor.constants;

/**
 * Эндпоинты промокодов.
 */
public final class EndpointsPromoCode {

    private EndpointsPromoCode() {
        throw new IllegalStateException("Utility class");
    }

    // ==================== PROMO CODE ENDPOINTS ====================
    /**
     * применение промокода
     */
    public static final String PROMO_CODES_APPLY = "/user/promo-codes/apply";

    /**
     * активация подписки по промокоду
     */
    public static final String PROMO_CODES_ACTIVATE_SUBSCRIPTION = "/user/promo-codes/activate-subscription";
}