package ru.tutor.test.api.user;


import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import ru.tutor.config.TestConfig;
import ru.tutor.steps.account.AuthSteps;
import ru.tutor.steps.content.CoursesSteps;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import ru.tutor.constants.Tags;

@Epic("API Тесты пользователя")
@Feature("Курсы")
@DisplayName("Тесты эндпоинтов курсов")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag(Tags.INTEGRATION)
@Tag(Tags.API)
public class CoursesTest {

    private final AuthSteps userSteps = new AuthSteps(TestConfig.getCurrentApiUrl());
    private final CoursesSteps coursesSteps = new CoursesSteps(TestConfig.getCurrentApiUrl());
    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();

    private static String token;

    @BeforeAll
    public static void setUp() {
        Allure.label("component", "API");
        Allure.label("feature", "Courses");
    }

    @BeforeEach
    void setUpAuth() {
        token = step("Логин пользователя", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );
    }

    @Test
    @Order(1)
    @DisplayName("GET /user/courses/{productId} - информация о курсе")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.POSITIVE)
    @Tag(Tags.CONTRACT)
    void getCoursesByIdSuccess() {
        Response allProducts = step("GET /user/products/all", () ->
                coursesSteps.getAllProducts(token)
        );
        step("Валидация ответа продуктов", () ->
                coursesSteps.validateProductsResponse(allProducts)
        );

        int productId = step("Извлечение случайного productId", () ->
                coursesSteps.extractRandomProductId(allProducts)
        );
        Response courseResponse = step("GET /user/courses/" +  productId, () ->
                coursesSteps.getCourseById(token, productId)
        );

        step("Валидация ответа курса", () ->
                coursesSteps.validateCourses(courseResponse)
        );
    }

    @Test
    @Order(2)
    @DisplayName("GET /user/courses/{productId}/reviews - отзывы курса")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.POSITIVE)
    @Tag(Tags.CONTRACT)
    void getCourseReviewsSuccess() {
        Response allProducts = step("GET /user/products/all", () ->
                coursesSteps.getAllProducts(token)
        );
        int productId = step("Извлечение случайного productId", () ->
                coursesSteps.extractRandomProductId(allProducts)
        );

        Response reviewsResponse = step("GET /user/courses/" +  productId + "/reviews", () ->
                coursesSteps.getCourseReviews(token, productId)
        );
        step("Валидация ответа отзывов", () ->
                coursesSteps.validateReviewsResponse(reviewsResponse)
        );
    }

    @Test
    @Order(3)
    @DisplayName("GET /user/courses/{productId}/recommendations - рекомендации")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.POSITIVE)
    @Tag(Tags.CONTRACT)
    void getCourseRecommendationsSuccess() {
        Response allProducts = step("GET /user/products/all", () ->
                coursesSteps.getAllProducts(token)
        );
        int productId = step("Извлечение случайного productId", () ->
                coursesSteps.extractRandomProductId(allProducts)
        );

        Response recResponse = step("GET /user/courses/" +  productId + "/recommendations", () ->
                coursesSteps.getCourseRecommendations(token, productId)
        );
        step("Валидация ответа рекомендаций", () ->
                coursesSteps.validateRecommendationsResponse(recResponse)
        );
    }

    @Test
    @Order(4)
    @DisplayName("GET /user/courses/{productId} - без авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void getCoursesByIdWithoutAuth() {
        Response response = step ("GET /user/courses/666", () ->
                coursesSteps.getCourseById("", 666)
        );
        step("Проверка статуса 401", ()->
                coursesSteps.assertUnauthorizedWithMessage(response, "JWT Token not found"));
    }

    @Test
    @Order(5)
    @DisplayName("GET /user/courses/{productId}/reviews - без авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void getCourseReviewsWithoutAuth() {
        Response response = step("GET /user/courses/666/reviews без токена", () ->
                coursesSteps.getCourseReviews("", 666)
        );
        step("Проверка статуса 401", () ->
                coursesSteps.assertUnauthorizedWithMessage(response, "JWT Token not found"));
    }

    @Test
    @Order(6)
    @DisplayName("GET /user/courses/{productId}/recommendations - без авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void getCourseRecommendationsWithoutAuth() {
        Response response = step("GET /user/courses/666/recommendations без токена", () ->
                coursesSteps.getCourseRecommendations("", 666)
        );
        step("Проверка статуса 401", () ->
                coursesSteps.assertUnauthorizedWithMessage(response, "JWT Token not found"));
    }

    @Test
    @Order(7)
    @DisplayName("GET /user/courses/{productId} - с инвалидным токеном")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void getCoursesByIdWithInvalidToken() {
        Response response = step ("GET /user/courses/666", () ->
                coursesSteps.getCourseById("invalid_token", 666)
        );
        step("Проверка статуса 401", ()->
                coursesSteps.assertUnauthorizedWithMessage(response, "Invalid JWT Token"));
    }

}
