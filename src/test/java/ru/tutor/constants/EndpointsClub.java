package ru.tutor.constants;

/**
 * Эндпоинты клуба.
 */
public final class EndpointsClub {

    private EndpointsClub() {
        throw new IllegalStateException("Utility class");
    }

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

    /**
     * Создание реферальных ссылок
     */
    public static final String USER_CLUB_V2_LINKS = "/user/club/v2/links";

    /**
     * Сводка пользователя
     */
    public static final String CLUB_V2_PAYOUTS_SUMMARY = "/user/club/v2/payouts/summary";

    /**
     * Реквизиты
     */
    public static final String CLUB_V2_PAYOUTS_REQUISITES = "/user/club/v2/payouts/requisites";

    /**
     * Вознаграждение за рекомендацию
     */
    public static final String CLUB_SHARE_REWARD = "/user/club/share-reward";

    /**
     * Создание заявки на выплату
     */
    public static final String USER_CLUB_V2_PAYOUTS = "/user/club/v2/payouts";

    /**
     * Рефералы текущего пользователя с пагинацией, сортировкой и summary
     */
    public static final String USER_CLUB_V2_REFERRALS = "/user/club/v2/referrals";

    /**
     * Аналитика главной клуба v2
     */
    public static final String USER_CLUB_V2_HOME_ANALYTICS = "/user/club/v2/home/analytics";

    /**
     * Баннеры клуба v2
     */
    public static final String USER_CLUB_V2_HOME_BANNERS = "/user/club/v2/home/banners";

    /**
     * Ссылки участника клуба v2
     */
    public static final String USER_CLUB_V2_HOME_LINKS = "/user/club/v2/home/links";

    /**
     * Новости клуба v2
     */
    public static final String USER_CLUB_V2_HOME_NEWS = "/user/club/v2/home/news";

    /**
     * Детальная информация новости клуба v2
     */
    public static final String USER_CLUB_V2_HOME_NEWS_ID = "/user/club/v2/home/news/%s";

    /**
     * Аналитика ссылок клуба v2
     */
    public static final String USER_CLUB_V2_ANALYTICS_LINKS = "/user/club/v2/analytics/links";

    /**
     * Аналитика дохода по уровням клуба v2
     */
    public static final String USER_CLUB_V2_ANALYTICS_MONTHLY_INCOME = "/user/club/v2/analytics/monthly-income";

    /**
     * Сводка по ссылкам клуба v2
     */
    public static final String USER_CLUB_V2_LINKS_SUMMARY = "/user/club/v2/links/summary";

    /**
     * Детальная информация реферала клуба v2
     */
    public static final String USER_CLUB_V2_REFERRALS_ID = "/user/club/v2/referrals/%s";

    /**
     * Дерево рефералов конкретного реферала клуба v2
     */
    public static final String USER_CLUB_V2_REFERRALS_ID_REFERRALS = "/user/club/v2/referrals/%s/referrals";
}