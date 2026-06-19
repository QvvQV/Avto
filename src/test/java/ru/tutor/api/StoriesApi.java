package ru.tutor.api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.base.BaseApi;
import ru.tutor.constants.ApiEndpoints;

public class StoriesApi extends BaseApi {

    public StoriesApi(String baseUrl) {
        super(baseUrl);
    }

    /**
     * Ручка для колеса фортуны
     */
    @Step("POST /user/stories/screen - видимость колеса для пользователя сторис")
    public Response storyScreen(String authToken) {

        return postWithToken(ApiEndpoints.USER_STORIES_SCREEN, authToken, "[fortuna]");
    }

    /**
     * GET /user/stories
     */
    @Step("GET /user/stories - истории на платформе")
    public Response showStories(String authToken) {
        return getWithToken(ApiEndpoints.USER_STORIES, authToken);
    }

    /**
     * GET /user/tuts-categories
     */
    @Step("GET /user/tuts-categories - список категорий тутсов")
    public Response showTutsCategories(String authToken) {
        return getWithToken(ApiEndpoints.USER_TUTS_CATEGORIES, authToken);
    }

    /**
     * GET /user/tuts/random-list
     */
    @Step("GET /user/tuts/random-list - андомный список тутсов")
    public Response showTutsRandomList(String authToken) {
        return getWithToken(ApiEndpoints.USER_TUTS_RANDOM_LIST, authToken);
    }

    /**
     * GET /user/tuts/category/feed
     */
    @Step("GET /user/tuts/category/{id}/feed - список тутсов определённой категории")
    public Response showCategoryFeed(String authToken, String id) {
        return getWithToken(String.format(ApiEndpoints.USER_TUTS_CATEGORY_FEED, id), authToken);
    }

    /**
     * GET /user/videos
     */
    @Step("GET /user/videos - список видео")
    public Response showVideos(String authToken) {
        return getWithToken(ApiEndpoints.USER_VIDEOS, authToken);
    }

    /**
     * GET /user/videos/{id} - детали видео
     */
    public Response detailsUserVideos(String authToken, String id) {
        return getWithToken(String.format(ApiEndpoints.USER_VIDEO_DETAILS_BY_ID, id), authToken);
    }
}
