package ru.tutor.api.content;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.base.BaseApi;
import ru.tutor.constants.EndpointsContent;

import java.util.List;

public class StoriesApi extends BaseApi {

    public StoriesApi(String baseUrl) {
        super(baseUrl);
    }

    /**
     * GET /user/stories
     */
    @Step("GET /user/stories - истории на платформе")
    public Response showStories(String authToken) {
        return getWithToken(EndpointsContent.USER_STORIES, authToken);
    }

    /**
     * user/stories/{ulid}
     */
    @Step("GET /user/stories/{ulid} - детали конкретной истории")
    public Response showStoriesId(String authToken, String ulid) {
        return getWithToken(String.format(EndpointsContent.USER_STORIES_ULID, ulid), authToken);
    }

    /**
     * /user/banners/{slug}
     */
    @Step("/user/banners/{slug} - детали конкретного баннера")
    public Response showBanners(String authToken, String slug) {
        return getWithToken(String.format(EndpointsContent.USER_BANNERS, slug), authToken);
    }

    /**
     * GET /user/tuts-categories
     */
    @Step("GET /user/tuts-categories - список категорий тутсов")
    public Response showTutsCategories(String authToken) {
        return getWithToken(EndpointsContent.USER_TUTS_CATEGORIES, authToken);
    }

    /**
     * GET /user/tuts/random
     */
    @Step("GET /user/tuts/random - рандомный список тутсов")
    public Response showTutsRandomList(String authToken) {
        return getWithToken(EndpointsContent.USER_TUTS_RANDOM_LIST, authToken);
    }

    /**
     * GET /user/tuts/random-list
     */
    @Step("GET /user/tuts/random-list - список всех тутсов")
    public Response showTutsList(String authToken) {
        return getWithToken(EndpointsContent.TUTS_RANDOM_LIST, authToken);
    }

    /**
     * GET /user/tuts/category/feed
     */
    @Step("GET /user/tuts/category/{id}/feed - список тутсов определённой категории")
    public Response showCategoryFeed(String authToken, String id) {
        return getWithToken(String.format(EndpointsContent.USER_TUTS_CATEGORY_FEED, id), authToken);
    }

    /**
     * GET /user/tuts/{slug} - детали тутса
     */
    @Step("GET /user/tuts/{slug} - детали тутса")
    public Response showTutDetails(String authToken, String slug) {
        return getWithToken(String.format(EndpointsContent.USER_TUTS_DETAILS, slug), authToken);
    }

    /**
     * GET /user/videos
     */
    @Step("GET /user/videos - список видео")
    public Response showVideos(String authToken) {
        return getWithToken(EndpointsContent.USER_VIDEOS, authToken);
    }

    /**
     * GET /user/videos/{id} - детали видео
     */
    @Step("GET /user/videos/{id} - детали видео")
    public Response detailsUserVideos(String authToken, String id) {
        return getWithToken(String.format(EndpointsContent.USER_VIDEO_DETAILS_BY_ID, id), authToken);
    }

    /**
     * GET /user/videos/watched/last` - если просмотр каких-то видео был оборван
     */
    @Step("GET /user/videos/watched/last` - если просмотр каких-то видео был оборван")
    public Response getWatchedLast(String authToken) {
        return getWithToken(EndpointsContent.VIDEOS_WATCHED_LAST,  authToken);
    }

    /**
     * POST /user/stories/view - фиксация просмотра сторис
     */
    public Response markStoriesViewed(String authToken, List<String> ulids) {
        return postWithToken(EndpointsContent.USER_STORIES_VIEW, authToken, ulids);
    }

    /**
     * POST /user/stories/screen - фиксация показа экрана сторис
     */
    public Response storyScreenPost(String authToken, List<String> payloads) {
        return postWithToken(EndpointsContent.USER_STORIES_SCREEN_POST, authToken, payloads);
    }

    /**
     * POST /user/tuts/{slug}/view - фиксация просмотра тутса
     */
    public Response markTutsViewed(String authToken, String slug) {
        return postWithToken(String.format(EndpointsContent.USER_TUTS_VIEW, slug), authToken, "");
    }
}