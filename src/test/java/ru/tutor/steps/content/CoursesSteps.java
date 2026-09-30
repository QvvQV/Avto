package ru.tutor.steps.content;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import ru.tutor.api.content.UserCoursesApi;
import ru.tutor.api.catalog.UserProductsApi;
import ru.tutor.base.BaseApiSteps;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

public class CoursesSteps extends BaseApiSteps {
    private final UserCoursesApi coursesApi;
    private final UserProductsApi productsApi;

    public CoursesSteps(String baseUrl) {
        super(baseUrl);
        this.coursesApi = new UserCoursesApi(baseUrl);
        this.productsApi = new UserProductsApi(baseUrl);
    }

    @Step("GET /user/products/all - получение всех продуктов")
    public Response getAllProducts(String authToken) {
        return productsApi.getAllProducts(authToken);
    }

    @Step("GET /user/courses/{productId} - информация о курсе")
    public Response getCourseById(String authToken, int productId){
        return coursesApi.getCourseById(authToken, productId);
    };

    @Step("GET /user/courses/{productId}/reviews - отзывы курса")
    public Response getCourseReviews(String authToken, int productId){
        return coursesApi.getCourseReviews(authToken, productId);
    }

    @Step("GET /user/courses/{productId}/recommendations - рекомендации")
    public Response getCourseRecommendations(String authToken, int productId){
        return coursesApi.getCourseRecommendations(authToken, productId);
    }

    @Step("Извлечение случайного productId из /user/products/all")
    public int extractRandomProductId(Response allProductsResponse)
    {
        List<Integer> productIds = allProductsResponse.jsonPath().getList("productId", Integer.class);
        assertNotNull(productIds, "Список productId не должен быть null");
        assertFalse(productIds.isEmpty(), "Список продуктов пуст");

        int randomIndex = ThreadLocalRandom.current().nextInt(productIds.size());
        int productId = productIds.get(randomIndex);

        Allure.addAttachment("Random productId", String.valueOf(productId));
        return productId;
    }

    @Step("Валидация ответа списка достижений")
    public void validateCourses(Response response) {
        assertBasicResponse(response);
        JsonPath json = response.jsonPath();

        String slug = json.getString("slug");
        assertNotNull(slug, "slug не должен быть null");
        assertFalse(slug.isBlank(), "slug не должен быть пустым");

        String name = json.getString("name");
        assertNotNull(name, "name не должен быть null");
        assertFalse(name.isBlank(), "name не должен быть пустым");

        assertNotNull(json.getObject("rating", Number.class), "rating не должен быть null");

        List<String> skills = json.getList("skills", String.class);
        assertNotNull(skills, "skills не должен быть null");
        assertFalse(skills.isEmpty(), "skills не должен быть пустым");

        String createdAt = json.getString("createdAt");
        assertNotNull(createdAt, "createdAt не должен быть null");
        OffsetDateTime.parse(createdAt);

        assertNotNull(json.getMap("productInformation"), "productInformation не должен быть null");
        Integer reviewsCount = json.getInt("productInformation.reviewsCount");
        assertNotNull(reviewsCount, "reviewsCount не должен быть null");
        assertTrue(reviewsCount >= 0, "reviewsCount должен быть >= 0");

        assertNotNull(json.getInt("productInformation.duration"), "duration не должен быть null");

        assertNotNull(json.getMap("promoPage"), "promoPage не должен быть null");
        List<Map<String, Object>> descriptions = json.getList("promoPage.descriptions");
        assertNotNull(descriptions, "descriptions не должен быть null");
        assertFalse(descriptions.isEmpty(), "descriptions не должен быть пустым");

        for (Map<String, Object> description : descriptions) {
            assertNotNull(description.get("title"), "title не должен быть null");
            assertNotNull(description.get("text"), "text не должен быть null");
            String type = (String)  description.get("type");
            assertTrue(type != null && Set.of("courseFor", "afterCourse", "callToAction").contains(type),
                    "type должен быть courseFor/afterCourse/callToAction, а был: " + type);
        }
    }

    @Step("Валидация ответа /user/products/all")
    public void validateProductsResponse(Response response) {
        assertBasicResponse(response);
        List<Map<String, Object>> products = response.jsonPath().getList("$");
        assertNotNull(products, "Список продуктов не должен быть null");
        assertFalse(products.isEmpty(), "Список продуктов не должен быть пустым");
    }

    @Step("Валидация ответа отзывов /user/courses/{productId}/reviews")
    public void validateReviewsResponse(Response response) {
        assertBasicResponse(response);
        JsonPath json = response.jsonPath();
        Map<String, Object> root = json.getMap("$");
        assertNotNull(root, "Тело должно быть JSON-объектом");

        List<Map<String, Object>> reviews = json.getList("reviews");
        assertNotNull(reviews, "reviews не должен быть null");
        assertFalse(reviews.isEmpty(), "reviews не должен быть пустым");

        for (Map<String, Object> review : reviews) {
            assertNotNull(review.get("reviewerName"), "reviewerName не должен быть null");
            assertNotNull(review.get("text"), "text не должен быть null");
            if (review.containsKey("reviewerPhotoName")) {
                assertNotNull(review.get("reviewerPhotoName"), "reviewerPhotoName не должен быть null");
            }
            if (review.containsKey("reviewerPhotoUrl")) {
                assertNotNull(review.get("reviewerPhotoUrl"), "reviewerPhotoUrl не должен быть null");
            }
        }
    }

    @Step("Валидация ответа рекомендаций /user/courses/{productId}/recommendations")
    public void validateRecommendationsResponse(Response response) {
        assertBasicResponse(response);
        JsonPath json = response.jsonPath();
        Map<String, Object> root = json.getMap("$");
        assertNotNull(root, "Тело должно быть JSON-объектом");

        for (String key : List.of("next_step", "similar", "alternative", "also_watching", "another")) {
            assertTrue(root.containsKey(key), key + " должен присутствовать в ответе");
            assertNotNull(json.getList(key, Integer.class), key + " не должен быть null");
        }

    }
}
