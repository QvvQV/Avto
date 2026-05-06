package ru.tutor.api;

import io.restassured.response.Response;
import ru.tutor.base.BaseApi;
import ru.tutor.constants.ApiEndpoints;
import ru.tutor.model.ApplyPromoCodeRequest;
import ru.tutor.model.RegistrationRequest;

public class UserApi extends BaseApi {

    public UserApi(String baseUrl) {
        super(baseUrl);
    }

    /**
     * POST /user/login - вход пользователя
     */

    public Response login(String email, String password) {
        String body = String.format(
                "{\"email\":\"%s\",\"password\":\"%s\"}",
                email, password
        );

        return post(ApiEndpoints.LOGIN, body);
    }

    /**
     * GET /user/profile - получение профиля пользователя
     */
    public Response getProfile(String authToken) {
        return getWithToken(ApiEndpoints.USER_PROFILE, authToken);
    }

    /**
     * POST /user/logout - выход пользователя из системы
     */

    public Response logout(String authToken) {
        return postWithToken(ApiEndpoints.LOGOUT,  authToken, "[]");
    }

    /**
     * GET /user/game-stats - получение игровой статистики
     */

    public Response getGameStats(String authToken) {
        return getWithToken(ApiEndpoints.USER_GAME_STATS, authToken);
    }

    /**
     * POST /register - регистрация пользователя
     */
    public Response register(RegistrationRequest request) {
        return post(ApiEndpoints.REGISTER, request);
    }

    /**
     * POST /user/promo-codes/apply - применение промокода
     */
    public Response applyPromoCode(String authToken, ApplyPromoCodeRequest request) {
        return postWithToken(ApiEndpoints.PROMO_CODES_APPLY , authToken, request);
    }
}
