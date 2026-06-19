package ru.tutor.api.club;

import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

import ru.tutor.base.BaseApi;
import ru.tutor.constants.ApiEndpoints;

public class ClubSzDetailsApi extends BaseApi {

    public ClubSzDetailsApi(String baseUrl) {
        super(baseUrl);
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
//        return given()
//                .filter(new AllureRestAssured())
//                .header("Authorization", "Bearer " + authToken)
//                .header("Accept", "application/json, text/plain, */*")
//                .header("Origin", "https://client.ttplace.ru")
//                .header("Referer", "https://client.ttplace.ru/")
//                .log().all()
//                .when()
//                .delete(baseUrl + ApiEndpoints.CLUB_DETAILS)
//                .then()
//                .log().all()
//                .extract().response();
        return deleteWithToken(ApiEndpoints.CLUB_DETAILS, authToken);
    }
}
