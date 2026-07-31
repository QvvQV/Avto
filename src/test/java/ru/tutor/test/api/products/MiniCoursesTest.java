package ru.tutor.test.api.products;

import io.qameta.allure.*;
import io.qameta.allure.junit5.AllureJunit5;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tutor.config.TestConfig;
import ru.tutor.steps.ProductsSteps;
import ru.tutor.steps.UserSteps;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AllureJunit5.class)
@Epic("Список продуктов пользователя")
@Feature("Мини-курсы")
@DisplayName("Тесты эндпоинта '/user/mini-courses'")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("products")

public class MiniCoursesTest {

    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private final UserSteps userSteps = new UserSteps(BASE_URL);
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
    @DisplayName("GET /user/mini-courses - успешное получение списка")
    @Story("Основной сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("positive")
    void getMiniCoursesSuccess() {
        Response response = step("GET /user/mini-courses", () ->
                productsSteps.getMiniCourses(token));

        step("Валидация ответа", () -> {
            productsSteps.validateMiniCoursesResponse(response);
        });
    }

    @Test
    @Order(2)
    @DisplayName("GET /user/mini-courses - валидация элементов списка")
    @Story("Валидация данных")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("positive")
    void validateMiniCoursesItems() {
        Response response = step("GET /user/mini-courses", () ->
                productsSteps.getMiniCourses(token));

        step("Проверка обязательных полей каждого элемента", () ->
                productsSteps.validateAllAreMiniCourses(response)
        );
    }

    @Test
    @Order(3)
    @DisplayName("GET /user/mini-courses - валидация пагинации")
    @Story("Валидация данных")
    @Severity(SeverityLevel.NORMAL)
    @Tag("positive")
    void validateMiniCoursesPagination() {
        Response response = step("GET /user/mini-courses", () ->
                productsSteps.getMiniCourses(token));

        step("Валдиация ответа", () ->
                productsSteps.validateMiniCoursesResponse(response));

        step("Проверка пагинации", () ->

            productsSteps.validateMiniCoursesPagination(response));

    }

    @Test
    @Order(4)
    @DisplayName("GET /user/mini-courses - без авторизации")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("negative")
    void getMiniCoursesWithoutAuth() {
        Response response = step("GET /user/mini-courses без токена", () ->
                productsSteps.getMiniCourses(""));

        step("Проверка ошибки авторизации", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(5)
    @DisplayName("GET /user/mini-courses - невалидный токен")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("negative")
    void getMiniCoursesInvalidToken() {
        Response response = step("GET /user/mini-courses невалидный токен", () ->
                productsSteps.getMiniCourses("invalid_token"));

        step("Проверка ошибки авторизации", () ->
                assertEquals(401, response.getStatusCode()));
    }
}
