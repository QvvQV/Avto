package ru.tutor.api.club;

import io.restassured.response.Response;
import ru.tutor.base.BaseApi;
import ru.tutor.constants.EndpointsClub;

import java.util.Map;

public class ClubApi extends BaseApi {

    public ClubApi(String baseUrl) {
        super(baseUrl);
    }

    private static final Map<String, Object> DEFAULT_REFERRALS_QUERY = Map.of(
            "limit", 10, "page", 1, "sort", "incomePerMonth", "direction", "desc"
    );

    private static final Map<String, Object> DEFAULT_PAYOUTS_QUERY = Map.of(
            "page", 1, "per_page", 20
    );

    /**
     * GET /user/club/statistics - получение статистики клуба
     */
    public Response getClubStatistics(String authToken) {
        return getWithToken(EndpointsClub.CLUB_STATISTICS, authToken);
    }

    /**
     * GET /user/club/member - получение списка участников клуба
     */
    public Response getClubMembers(String authToken) {
        return getWithToken(EndpointsClub.CLUB_MEMBER, authToken);
    }

    /**
     * GET /user/club/payments
     */
    public Response getClubPayments(String authToken) {
        return getWithToken(EndpointsClub.CLUB_PAYMENTS, authToken);
    }

    /**
     * POST /user/club/request-withdrawal - запрос на вывод средств
     */
    public Response requestWithdrawal(String authToken, Map<String, Object> payload) {
        return postWithToken(EndpointsClub.CLUB_REQUEST_WITHDRAWAL, authToken, payload);
    }

    /**
     * POST /user/club/sz-details - сохранение деталей самозанятого
     */
    public Response saveSzDetails(String authToken, Map<String, Object> payload) {
        return postWithToken(EndpointsClub.CLUB_SZ_DETAILS, authToken, payload);
    }

    /**
     * POST /user/club/ip-details - сохранение деталей ИП
     */
    public Response saveIpDetails(String authToken, Map<String, Object> payload) {
        return postWithToken(EndpointsClub.CLUB_IP_DETAILS, authToken, payload);
    }

    /**
     * GET /user/club/ip-details - получение деталей ИП
     */
    public Response getIpDetails(String authToken) {
        return getWithToken(EndpointsClub.CLUB_IP_DETAILS, authToken);
    }

    /**
     * DELETE /user/club/details - получение деталей
     */
    public Response deleteClubDetails(String authToken) {
        return deleteWithToken(EndpointsClub.CLUB_DETAILS, authToken);
    }

    /**
     * GET /user/club/v2/payouts/summary - сводка выплат клуба
     */
    public Response getPayoutSummary(String authToken) {
        return getWithToken(EndpointsClub.CLUB_V2_PAYOUTS_SUMMARY, authToken);
    }

    /**
     * GET /user/club/v2/payouts - список заявок на вывод (page=1, per_page=20)
     */
    public Response getClubPayouts(String authToken) {
        return getWithQuery(EndpointsClub.USER_CLUB_V2_PAYOUTS, authToken, DEFAULT_PAYOUTS_QUERY);
    }

    /**
     * POST /user/club/v2/links - создание реферальной ссылки клуба
     */
    public Response createClubLink(String authToken, Map<String, Object> payload) {
        return postWithToken(EndpointsClub.USER_CLUB_V2_LINKS, authToken, payload);
    }

    /**
     * POST /user/club/share-reward - вознаграждение
     */
    public Response shareReward(String authToken) {
        return postWithToken(EndpointsClub.CLUB_SHARE_REWARD, authToken, "[]");
    }

    /**
     * PUT /user/club/v2/payouts/requisites - заполнение реквизитов
     */
    public Response savePayoutRequisites(String authToken, Object payload) {
        return putWithTokenObject(EndpointsClub.CLUB_V2_PAYOUTS_REQUISITES , authToken, payload);
    }
    public Response getPayoutRequisites(String authToken) {
        return getWithToken(EndpointsClub.CLUB_V2_PAYOUTS_REQUISITES, authToken);
    }
    public Response deletePayoutRequisites(String authToken) {
        return deleteWithToken(EndpointsClub.CLUB_V2_PAYOUTS_REQUISITES, authToken);
    }

    /**
     * POST /user/club/v2/payouts - создание заявки на выплату средств
     */
    public Response createPayout(String authToken,  Object payload) {
        return postWithToken(EndpointsClub.USER_CLUB_V2_PAYOUTS, authToken, payload);
    }

    /**
     * GET /user/club/v2/referrals - рефералы текущего пользователя (дефолтные параметры)
     */
    public Response getClubReferrals(String authToken) {
        return getWithQuery(EndpointsClub.USER_CLUB_V2_REFERRALS, authToken, DEFAULT_REFERRALS_QUERY);
    }

    /**
     * GET /user/club/v2/referrals - рефералы текущего пользователя с заданными параметрами
     */
    public Response getClubReferrals(String authToken, Map<String, Object> queryParams) {
        return getWithQuery(EndpointsClub.USER_CLUB_V2_REFERRALS, authToken, queryParams);
    }

    /**
     * GET /user/club/v2/referrals/{userId} - детальная информация реферала
     */
    public Response getClubReferralDetail(String authToken, String userId) {
        return getWithToken(EndpointsClub.USER_CLUB_V2_REFERRALS_ID.formatted(userId), authToken);
    }

    /**
     * GET /user/club/v2/referrals/{userId}/referrals - дерево рефералов конкретного реферала
     */
    public Response getClubReferralReferrals(String authToken, String userId) {
        return getWithToken(EndpointsClub.USER_CLUB_V2_REFERRALS_ID_REFERRALS.formatted(userId), authToken);
    }

    /**
     * GET /user/club/v2/home/analytics - аналитика главной клуба
     */
    public Response getClubHomeAnalytics(String authToken) {
        return getWithToken(EndpointsClub.USER_CLUB_V2_HOME_ANALYTICS, authToken);
    }

    /**
     * GET /user/club/v2/home/banners - баннеры клуба
     */
    public Response getClubHomeBanners(String authToken) {
        return getWithToken(EndpointsClub.USER_CLUB_V2_HOME_BANNERS, authToken);
    }

    /**
     * GET /user/club/v2/home/links - ссылки участника клуба
     */
    public Response getClubHomeLinks(String authToken) {
        return getWithToken(EndpointsClub.USER_CLUB_V2_HOME_LINKS, authToken);
    }

    /**
     * GET /user/club/v2/home/news - новости клуба
     */
    public Response getClubHomeNews(String authToken) {
        return getWithToken(EndpointsClub.USER_CLUB_V2_HOME_NEWS, authToken);
    }

    /**
     * GET /user/club/v2/home/news/{newsId} - детальная информация новости
     */
    public Response getClubNewsDetail(String authToken, String newsId) {
        return getWithToken(EndpointsClub.USER_CLUB_V2_HOME_NEWS_ID.formatted(newsId), authToken);
    }

    /**
     * GET /user/club/v2/analytics/links - аналитика по ссылкам клуба
     */
    public Response getClubAnalyticsLinks(String authToken) {
        return getWithToken(EndpointsClub.USER_CLUB_V2_ANALYTICS_LINKS, authToken);
    }

    /**
     * GET /user/club/v2/analytics/monthly-income - аналитика дохода по уровням
     */
    public Response getClubAnalyticsMonthlyIncome(String authToken) {
        return getWithToken(EndpointsClub.USER_CLUB_V2_ANALYTICS_MONTHLY_INCOME, authToken);
    }

    /**
     * GET /user/club/v2/links/summary - сводка по ссылкам клуба
     */
    public Response getClubLinksSummary(String authToken) {
        return getWithToken(EndpointsClub.USER_CLUB_V2_LINKS_SUMMARY, authToken);
    }
}