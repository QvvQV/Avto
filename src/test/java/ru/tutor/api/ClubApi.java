package ru.tutor.api;

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
        return getWithToken("/user/club/ip-details", authToken);
    }

    /**
     * DELETE /user/club/details - получение деталей
     */
    public Response deleteClubDetails(String authToken) {
        return deleteWithToken(ApiEndpoints.CLUB_DETAILS, authToken);
    }
}