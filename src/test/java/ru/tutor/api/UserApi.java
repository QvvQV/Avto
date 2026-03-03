package ru.tutor.api;

import io.restassured.response.Response;
import ru.tutor.base.BaseApi;
import ru.tutor.constants.ApiEndpoints;

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
     *
     * @param authToken Bearer токен пользователя
     * @return Response с результатом логаута
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
}
