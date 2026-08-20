package ru.tutor.test.api.products;

import io.qameta.allure.*;
import io.qameta.allure.junit5.AllureJunit5;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tutor.config.TestConfig;
import ru.tutor.steps.AuthSteps;
import ru.tutor.steps.ProductsSteps;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AllureJunit5.class)
@Epic("Список продуктов пользователя")
@Feature("Мини-курсы")
@DisplayName("Тесты эндпоинта '/user/mini-workouts'")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("products")
public class MiniWorkoutsTest {

    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private final AuthSteps userSteps = new AuthSteps(BASE_URL);
    private final ProductsSteps productsSteps = new ProductsSteps(BASE_URL);
    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();

    private String token;

    @BeforeAll
    public static void setupAll() {
        Allure.label("component", "API");
        Allure.label("feature", "Mini Workouts");
    }

    @BeforeEach
    void setUpAuth() {
        token = step("Логин пользователя", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD));
    }

    @Test
    @Order(1)
    @DisplayName("GET /user/mini-workouts - валидация элементов списка")
    @Story("Валидация данных")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("positive")
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
    @Tag("negative")
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
    @Tag("negative")
    void getMiniWorkoutsInvalidToken() {
        Response response = step("GET /user/mini-workouts невалидный токен", () ->
                productsSteps.getMiniWorkouts("invalid_token"));

        step("Проверка ошибки авторизации", () ->
                assertEquals(401, response.getStatusCode()));
    }
}
