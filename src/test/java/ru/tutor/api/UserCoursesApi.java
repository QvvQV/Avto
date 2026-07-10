package ru.tutor.api;

import io.restassured.response.Response;
import ru.tutor.base.BaseApi;
import ru.tutor.constants.ApiEndpoints;

public class UserCoursesApi extends BaseApi {
    public UserCoursesApi(String baseUrl) {
        super(baseUrl);
    }

    public Response getCourseById(String authToken, int productId) {
        return getWithToken(String.format(ApiEndpoints.USER_COURSES_ID, productId), authToken);
    }

    public Response getCourseReviews(String authToken, int productId) {
        return getWithToken(String.format(ApiEndpoints.USER_COURSES_REVIEWS, productId), authToken);
    }

    public Response getCourseRecommendations(String authToken, int productId) {
        return getWithToken(String.format(ApiEndpoints.USER_COURSES_RECOMMENDATIONS, productId), authToken);
    }
}
