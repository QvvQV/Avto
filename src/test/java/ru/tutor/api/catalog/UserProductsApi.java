package ru.tutor.api.catalog;

import io.restassured.response.Response;
import ru.tutor.base.BaseApi;
import ru.tutor.constants.EndpointsCatalog;
import ru.tutor.constants.EndpointsContent;
import ru.tutor.constants.EndpointsFortune;
import java.util.HashMap;
import java.util.Map;

/** Работа с курсом/продуктом */

public class UserProductsApi extends BaseApi {

    public UserProductsApi(String baseUrl) {
        super(baseUrl);
    }

    /**
     * GET /user/products - получение списка продуктов пользователя
     */
    public Response getUserProducts(String authToken) {
        return getWithToken(EndpointsCatalog.PRODUCTS_LIST, authToken);
    }

    /**
     * GET /user/achievements - получение списка всех достижений продуктов пользователя
     */
    public Response getUserAchievements(String authToken) {
        return getWithToken(EndpointsFortune.USER_ACHIEVEMENTS, authToken);
    }

    /**
     * GET user/achievements/{productId} - получение списка достижений конкретного продукта
     */

    public Response getAchievementsProductById(String authToken, String productId) {

        return getWithToken(String.format(EndpointsFortune.ACHIEVEMENTS_PRODUCT_ID, productId), authToken);
    }

    /**
     * GET /user/achievements/last
     */
    public Response getAchievementsLast(String authToken) {
        return getWithToken(EndpointsFortune.USER_ACHIEVEMENTS_LAST, authToken);
    }

    /**
     * GET /user/products/all - получение всех доступных продуктов
     */
    public Response getAllProducts(String authToken) {
        return getWithToken(EndpointsCatalog.ALL_PRODUCTS, authToken);
    }

    /**
     * GET /user/products/{productId} - получение конкретного продукта
     */

    public Response getProductById(String authToken, String productId) {

        return getWithToken(String.format(EndpointsCatalog.PRODUCTS_ID, productId), authToken);
    }

    /**
     * GET /user/products/{productId} - получение конкретного продукта
     */

    public Response getProductModule(String authToken, String productValue, int moduleNumber) {

        return getWithToken(String.format(EndpointsCatalog.PRODUCTS_MODULE, productValue, moduleNumber), authToken);
    }

    /**
     * GET /user/favorite-products - список избранных продуктов
     */

    public Response getFavoriteProducts(String authToken) {
        return getWithToken(EndpointsCatalog.USER_FAVORITE_PRODUCTS, authToken);
    }

    /**
     * POST /user/favorite-products/{productId} - добавление продукта в избранное
     */
    public Response postFavoriteProducts(String authToken, String productId) {
        return postWithToken(EndpointsCatalog.USER_FAVORITE_PRODUCTS_ID, authToken, "", productId);
    }

    /**
     * DELETE /user/favorite-products/{productId} - удаление продукта из избранных
     */
    public Response deleteFavoriteProducts(String authToken, String productId) {
        return deleteWithTokenObject(EndpointsCatalog.USER_FAVORITE_PRODUCTS_ID, authToken, "", productId);
    }

    /**
     * POST /user/videos/watched/create - тайминг просмотра
     */

    public Response postProductCreate(String authToken, String productId, int moduleNumber, int timeline) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "module_watched");
        payload.put("orderNumber", productId);
        payload.put("moduleNumber", moduleNumber);
        payload.put("timeline", timeline);

        return postWithToken(EndpointsContent.VIDEOS_WATCHED, authToken, payload);
    }

    /**
     * PUT user/orders/{productId}/completed_module/{int moduleNumber}
     */
    public Response putProductUpdate(String authToken, String productId, int moduleNumber) {

        return putWithToken(String.format(EndpointsCatalog.PRODUCTS_COMPLETED_MODULE, productId, moduleNumber), authToken);
    }

    /**
     * GET user/products/premiers - премьеры
     */
    public Response productsPremiers(String authToken) {
        return getWithToken(EndpointsContent.USER_PRODUCTS_PREMIERS, authToken);
    }

    /**
     * GET user/product-positions/top10 - список топ 10
     */
    public Response productTop10(String authToken) {
        return getWithToken(EndpointsContent.USER_PRODUCT_POSITIONS_TOP10, authToken);
    }

    /**
     * GET user/products/watching/now сейчас смотрят
     */
    public Response productWatchingNow(String authToken) {
        return getWithToken(EndpointsContent.USER_PRODUCTS_WATCHING_NOW , authToken);
    }

    /**
     * GET /categories - категории платформы
     */
    public Response getCategories(String authToken) {
        return getWithToken(EndpointsCatalog.CATEGORIES, authToken);
    }

    /**
     * GET /user/products/{orderNumber}/attached-files - список доступных вложений у курса
     */
    public Response getAttachedFiles(String authToken, String productId) {
        return getWithToken(String.format(EndpointsCatalog.PRODUCTS_ATTACHED_FILES, productId), authToken);
    }

    /**
     * GET /user/mini-courses — список мини-курсов
     */
    public Response getMiniCourses(String authToken) {
        return getWithToken(EndpointsCatalog.MINI_COURSES, authToken);
    }

    /**
     * GET /user/mini-workouts
     */
    public Response getMiniWorkouts(String authToken) {
        return getWithToken(EndpointsContent.MINI_WORKOUTS, authToken);
    }

    /**
     * GET /user/show-categories
     */
    public Response getShowCategories(String authToken) {
        return getWithToken(EndpointsContent.SHOW_CATEGORIES, authToken);
    }

    /**
     * GET /user/shows
     */
    public Response getShows(String authToken) {
        return getWithToken(EndpointsContent.USER_SHOWS, authToken);
    }
}
