package ru.tutor.api.club;

import io.restassured.response.Response;
import ru.tutor.base.BaseApi;
import ru.tutor.constants.ApiEndpoints;

import java.util.Map;

public class ClubApi extends BaseApi {

    public ClubApi(String baseUrl) {
        super(baseUrl);
    }

    /**
     * GET /user/club/statistics - получение статистики клуба
     */
    public Response getClubStatistics(String authToken) {
        return getWithToken(ApiEndpoints.CLUB_STATISTICS, authToken);
    }

    /**
     * GET /user/club/member - получение списка участников клуба
     */
    public Response getClubMembers(String authToken) {
        return getWithToken(ApiEndpoints.CLUB_MEMBER, authToken);
    }

    /**
     * GET /user/club/payments
     */
    public Response getClubPayments(String authToken) {
        return getWithToken(ApiEndpoints.CLUB_PAYMENTS, authToken);
    }

    /**
     * POST /user/club/request-withdrawal - запрос на вывод средств
     */
    public Response requestWithdrawal(String authToken, Map<String, Object> payload) {
        return postWithToken(ApiEndpoints.CLUB_REQUEST_WITHDRAWAL, authToken, payload);
    }

    /**
     * POST /user/club/sz-details - сохранение деталей самозанятого
     */
    public Response saveSzDetails(String authToken, Map<String, Object> payload) {
        return postWithToken(ApiEndpoints.CLUB_SZ_DETAILS, authToken, payload);
    }

    /**
     * POST /user/club/ip-details - сохранение деталей ИП
     */
    public Response saveIpDetails(String authToken, Map<String, Object> payload) {
        return postWithToken(ApiEndpoints.CLUB_IP_DETAILS, authToken, payload);
    }

    /**
     * GET /user/club/ip-details - получение деталей ИП
     */
    public Response getIpDetails(String authToken) {
        return getWithToken(ApiEndpoints.CLUB_IP_DETAILS, authToken);
    }

    /**
     * DELETE /user/club/details - получение деталей
     */
    public Response deleteClubDetails(String authToken) {
        return deleteWithToken(ApiEndpoints.CLUB_DETAILS, authToken);
    }

    /**
     * GET /user/{userId}/club/referrals - список рефералов пользователя"
     */
    public Response getUserReferrals(String authToken, int userId) {
        return getWithToken(String.format(ApiEndpoints.USER_CLUB_REFERRALS, userId), authToken);
    }

    /**
     * GET /user/{userId}/club/referrals/{referralId} - детали рефферала
     */
    public Response getUserReferralById(String authToken, int userId, int referralId) {
        return getWithToken(
                String.format(ApiEndpoints.USER_CLUB_REFERRALS_ID, userId, referralId), authToken
        );
    }

    /**
     * GET /user/{userId}/club/referrals/{referralId}/referrals - рефералы реферала
     */
    public Response getUserReferralRefferrals(String authToken, int userId, int referralId) {
        return getWithToken(
                String.format(ApiEndpoints.USER_CLUB_REFERRALS_ID_REFERRALS,  userId, referralId), authToken
        );
    }
}