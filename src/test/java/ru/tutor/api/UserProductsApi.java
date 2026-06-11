package ru.tutor.api;

import io.restassured.response.Response;
import ru.tutor.base.BaseApi;
import ru.tutor.constants.ApiEndpoints;
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
        return getWithToken(ApiEndpoints.PRODUCTS_LIST, authToken);
    }

    /**
     * GET /user/achievements - получение списка продуктов пользователя
     */
    public Response getUserAchievements(String authToken) {
        return getWithToken(ApiEndpoints.USER_ACHIEVEMENTS, authToken);
    }

    /**
     * GET user/achievements/{productId} - получение списка достижений конкретного продукта
     */

    public Response getAchievementsProductById(String authToken, String productId) {

        return getWithToken(String.format(ApiEndpoints.ACHIEVEMENTS_PRODUCT_ID, productId), authToken);
    }

    /**
     * GET /user/products/all - получение всех доступных продуктов
     */
    public Response getAllProducts(String authToken) {
        return getWithToken(ApiEndpoints.ALL_PRODUCTS, authToken);
    }

    /**
     * GET /user/products/{productId} - получение конкретного продукта
     */

    public Response getProductById(String authToken, String productId) {

        return getWithToken(String.format(ApiEndpoints.PRODUCTS_ID, productId), authToken);
    }

    /**
     * GET /user/products/{productId} - получение конкретного продукта
     */

    public Response getProductModule(String authToken, String productValue, int moduleNumber) {

        return getWithToken(String.format(ApiEndpoints.PRODUCTS_MODULE, productValue, moduleNumber), authToken);
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

        return postWithToken(ApiEndpoints.VIDEOS_WATCHED, authToken, payload);
    }

    /**
     * PUT user/orders/{productId}/completed_module/{int moduleNumber}
     */
    public Response putProductUpdate(String authToken, String productId, int moduleNumber) {

        return putWithToken(String.format(ApiEndpoints.PRODUCTS_COMPLETED_MODULE, productId, moduleNumber), authToken);
    }
}
