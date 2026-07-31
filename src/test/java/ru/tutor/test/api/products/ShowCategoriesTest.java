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
@Feature("Шоу-категории")
@DisplayName("Тесты эндпоинта '/user/show-categories'")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("products")

public class ShowCategoriesTest {

    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private final UserSteps userSteps = new UserSteps(BASE_URL);
    private final ProductsSteps productsSteps = new ProductsSteps(BASE_URL);
    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();

    private String token;

    @BeforeAll
    public static void setupAll() {
        Allure.label("component", "API");
        Allure.label("feature", "Show Categories");
    }

    @BeforeEach
    void setUpAuth() {
        token = step("Логин пользователя", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD));
    }

    @Test
    @Order(1)
    @DisplayName("GET /user/show-categories - успешное получение списка")
    @Story("Основной сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("positive")
    void getShowCategoriesSuccess() {
        Response response = step("GET /user/show-categories", () ->
                productsSteps.getShowCategories(token));

        step("Валидация", () ->
                productsSteps.validateMiniCoursesResponse(response));
    }

    @Test
    @Order(2)
    @DisplayName("GET /user/show-categories - валидация элементов списка")
    @Story("Валидация данных")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("positive")
    void validateShowCategoriesItems() {
        Response response = step("GET /user/show-categories", () ->
                productsSteps.getShowCategories(token));

        step("Проверка обязательных полей каждого элемента", () ->
                productsSteps.validateShowCategoriesItems(response));
    }

    @Test
    @Order(3)
    @DisplayName("GET /user/show-categories - без авторизации")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("negative")
    void getShowCategoriesWithoutAuth() {
        Response response = step("GET /user/show-categories без токена", () ->
                productsSteps.getShowCategories(""));

        step("Проверка ошибки авторизации", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(4)
    @DisplayName("GET /user/show-categories - невалидный токен")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("negative")
    void getShowCategoriesInvalidToken() {
        Response response = step("GET /user/show-categories невалидный токен", () ->
                productsSteps.getShowCategories("invalid"));

        step("Проверка ошибки авторизации", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(5)
    @DisplayName("GET /user/shows - успешное получение списка всех шоу, валидация")
    @Story("Основной сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("positive")
    void getShowsSuccess() {
        Response response = step("GET /user/shows", () ->
                productsSteps.getShows(token));

        step("Валидация, проверка обязательных полей каждого элемента", () ->
                productsSteps.validateShowsItems(response));
    }

    @Test
    @Order(6)
    @DisplayName("GET /user/shows - без авторизации")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("negative")
    void getShowsWithoutAuth() {
        Response response = step("GET /user/shows без токена", () ->
                productsSteps.getShows(""));

        step("Проверка ошибки авторизации", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(7)
    @DisplayName("GET /user/shows - невалидный токен")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("negative")
    void getShowsInvalidToken() {
        Response response = step("GET /user/show-categories невалидный токен", () ->
                productsSteps.getShows("invalid"));

        step("Проверка ошибки авторизации", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(8)
    @DisplayName("GET /user/shows - валидация пагинации")
    @Story("Валидация данных")
    @Severity(SeverityLevel.NORMAL)
    @Tag("positive")
    void validateShowsPagination() {
        Response response = step("GET /user/shows", () ->
                productsSteps.getShows(token));

        step("Валдиация ответа", () ->
                productsSteps.validateMiniCoursesResponse(response));

        step("Проверка пагинации", () ->

                productsSteps.validateMiniCoursesPagination(response));

    }

}
