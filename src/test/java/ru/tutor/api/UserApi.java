package ru.tutor.api;

import io.restassured.response.Response;
import ru.tutor.base.BaseApi;
import ru.tutor.constants.ApiEndpoints;
import ru.tutor.model.ApplyPromoCodeRequest;
import ru.tutor.model.RegistrationRequest;

import java.util.Map;

public class UserApi extends BaseApi {

    public UserApi(String baseUrl) {
        super(baseUrl);
    }

    public Response login(String email, String password) {
        return post(ApiEndpoints.LOGIN, Map.of("email", email, "password", password));
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
     * GET /user/game-leader/rating - рейтинг
     */
    public Response getLeaderRating(String authToken) {
        return getWithToken(ApiEndpoints.USER_GAME_LEADER_RATING, authToken);
    }

    /**
     * GET /user/tasks - список заданий
     */
    public Response getUserTasks(String authToken) {
        return getWithToken(ApiEndpoints.USER_TASKS, authToken);
    }

    /**
     * POST /user/tasks/change/{id} - заменить задание
     */
    public Response postUserTasksChange(String authToken, String id) {
        return postWithToken(ApiEndpoints.USER_TASKS_CHANGE, authToken, "", id);
    }

    /**
     * POST /user/tasks/finish/{id} - отметить задание завершенным
     */
    public Response postUserTasksFinish(String authToken, String id) {
        return postWithToken(ApiEndpoints.USER_TASKS_FINISH, authToken, "",id);
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
        return postWithToken(ApiEndpoints.PROMO_CODES_APPLY, authToken, request);
    }

    /**
     * GET /user - проверка авторизации
     */
    public Response checkAuth(String authToken) {
        return getWithToken(ApiEndpoints.USER_CHECK, authToken);
    }
}
