package ru.tutor.api.account;

import io.restassured.response.Response;
import ru.tutor.base.BaseApi;
import ru.tutor.constants.EndpointsAccount;
import ru.tutor.constants.EndpointsFortune;
import ru.tutor.constants.EndpointsPromoCode;
import ru.tutor.model.promoCode.ApplyPromoCodeRequest;
import ru.tutor.model.account.RegistrationRequest;

import java.util.Map;

import ru.tutor.model.account.UserEditDataRequest;

public class UserApi extends BaseApi {

    public UserApi(String baseUrl) {
        super(baseUrl);
    }

    public Response login(String email, String password) {
        return post(EndpointsAccount.LOGIN, Map.of("email", email, "password", password));
    }

    /**
     * GET /user/profile - получение профиля пользователя
     */
    public Response getProfile(String authToken) {
        return getWithToken(EndpointsAccount.USER_PROFILE, authToken);
    }

    /**
     * POST /user/logout - выход пользователя из системы
     */

    public Response logout(String authToken) {
        return postWithToken(EndpointsAccount.LOGOUT,  authToken, "[]");
    }

    /**
     * GET /user/game-stats - получение игровой статистики
     */

    public Response getGameStats(String authToken) {
        return getWithToken(EndpointsFortune.USER_GAME_STATS, authToken);
    }

    /**
     * GET /user/game-leader/rating - рейтинг
     */
    public Response getLeaderRating(String authToken) {
        return getWithToken(EndpointsFortune.USER_GAME_LEADER_RATING, authToken);
    }

    /**
     * GET /user/tasks - список заданий
     */
    public Response getUserTasks(String authToken) {
        return getWithToken(EndpointsAccount.USER_TASKS, authToken);
    }

    /**
     * POST /user/tasks/change/{id} - заменить задание
     */
    public Response postUserTasksChange(String authToken, String id) {
        return postWithToken(EndpointsAccount.USER_TASKS_CHANGE, authToken, "", id);
    }

    /**
     * POST /user/tasks/finish/{id} - отметить задание завершенным
     */
    public Response postUserTasksFinish(String authToken, String id) {
        return postWithToken(EndpointsAccount.USER_TASKS_FINISH, authToken, "",id);
    }

    /**
     * POST /register - регистрация пользователя
     */
    public Response register(RegistrationRequest request) {
        return post(EndpointsAccount.REGISTER, request);
    }

    /**
     * POST /user/promo-codes/apply - применение промокода
     */
    public Response applyPromoCode(String authToken, ApplyPromoCodeRequest request) {
        return postWithToken(EndpointsPromoCode.PROMO_CODES_APPLY, authToken, request);
    }

    /**
     * GET /user - проверка авторизации
     */
    public Response checkAuth(String authToken) {
        return getWithToken(EndpointsAccount.USER_CHECK, authToken);
    }

    /**
     * PUT /user/edit/pass` - изменение пароля
     */
    public Response editPassword(String authToken, Map<String, String> password) {
        return putWithTokenObject(EndpointsAccount.USER_EDIT_PASSWORD, authToken, password);
    }

    /**
     * PUT /user/edit/data - изменение данных пользователя
     */
    public Response editData(String authToken, UserEditDataRequest body) {
        return putWithTokenObject(EndpointsAccount.USER_EDIT_DATA, authToken, body);
    }
}
