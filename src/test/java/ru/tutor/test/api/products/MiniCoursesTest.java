package ru.tutor.test.api.products;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import ru.tutor.config.TestConfig;
import ru.tutor.steps.account.AuthSteps;
import ru.tutor.steps.catalog.ProductsSteps;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;
import ru.tutor.constants.Tags;

@Epic("Список продуктов пользователя")
@Feature("Мини-курсы")
@DisplayName("Тесты эндпоинта '/user/mini-courses'")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class MiniCoursesTest {

    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private final AuthSteps userSteps = new AuthSteps(BASE_URL);
    private final ProductsSteps productsSteps = new ProductsSteps(BASE_URL);
    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();

    private String token;

    @BeforeAll
    public static void setupAll() {
        Allure.label("component", "API");
        Allure.label("feature", "Mini Courses");
    }

    @BeforeEach
    void setUpAuth() {
        token = step("Логин пользователя", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD));
    }

    @Test
    @Order(1)
    @DisplayName("GET /user/mini-courses - валидация элементов списка")
    @Story("Валидация данных")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    void validateMiniCoursesItems() {
        Response response = step("GET /user/mini-courses", () ->
                productsSteps.getMiniCourses(token));

        step("Проверка обязательных полей каждого элемента", () ->
                productsSteps.validateAllAreMiniCourses(response)
        );
    }

    @Test
    @Order(2)
    @DisplayName("GET /user/mini-courses - валидация пагинации")
    @Story("Валидация данных")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.POSITIVE)
    void validateMiniCoursesPagination() {
        Response response = step("GET /user/mini-courses", () ->
                productsSteps.getMiniCourses(token));

        step("Проверка пагинации", () ->

            productsSteps.validateMiniCoursesPagination(response));

    }

    @Test
    @Order(3)
    @DisplayName("GET /user/mini-courses - без авторизации")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.NEGATIVE)
    void getMiniCoursesWithoutAuth() {
        Response response = step("GET /user/mini-courses без токена", () ->
                productsSteps.getMiniCourses(""));

        step("Проверка ошибки авторизации", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(4)
    @DisplayName("GET /user/mini-courses - невалидный токен")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.NEGATIVE)
    void getMiniCoursesInvalidToken() {
        Response response = step("GET /user/mini-courses невалидный токен", () ->
                productsSteps.getMiniCourses("invalid_token"));

        step("Проверка ошибки авторизации", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(1)
    @DisplayName("GET /user/mini-workouts - валидация элементов списка")
    @Story("Валидация данных")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    void validateMiniWorkoutsItems() {
        Response response = step("GET /user/mini-workouts", () ->
                productsSteps.getMiniWorkouts(token));

        step("Проверка обязательных полей каждого элемента", () ->
                productsSteps.validateAllAreMiniCourses(response)
        );
    }

    @Test
    @Order(2)
    @DisplayName("GET /user/mini-workouts - без авторизации")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.NEGATIVE)
    void getMiniWorkoutsWithoutAuth() {
        Response response = step("GET /user/mini-workouts без токена", () ->
                productsSteps.getMiniWorkouts(""));

        step("Проверка ошибки авторизации", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(3)
    @DisplayName("GET /user/mini-workouts - невалидный токен")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.NEGATIVE)
    void getMiniWorkoutsInvalidToken() {
        Response response = step("GET /user/mini-workouts невалидный токен", () ->
                productsSteps.getMiniWorkouts("invalid_token"));

        step("Проверка ошибки авторизации", () ->
                assertEquals(401, response.getStatusCode()));
    }
}
