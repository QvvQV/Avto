package ru.tutor.steps;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.api.UserCoursesApi;
import ru.tutor.api.UserProductsApi;
import ru.tutor.base.BaseApiSteps;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        String body = response.getBody().asString();
        assertNotNull(body, "Тело не должно быть null");
    }
}
