package ru.tutor.test.api.dashboard;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import ru.tutor.config.TestConfig;
import ru.tutor.steps.account.AuthSteps;
import ru.tutor.steps.dashboard.DashboardSteps;

import java.util.List;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import ru.tutor.constants.Tags;

@Epic("API Тесты пользователя")
@Feature("Дашборды")
@DisplayName("Тесты эндпоинта dashboard/info")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag(Tags.INTEGRATION)
public class DashboardInfoTest {

    private final AuthSteps userSteps = new AuthSteps(TestConfig.getCurrentApiUrl());
    private final DashboardSteps dashboardSteps = new DashboardSteps(TestConfig.getCurrentApiUrl());
    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();

    private static String token;

    @BeforeAll
    public static void setUp() {
        Allure.label("component", "API");
        Allure.label("feature", "Dashboard");
    }

    @BeforeEach
    void setUpAuth() {
        token = step("Логин пользователя", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD));
    }

    @Test
    @Order(1)
    @DisplayName("GET /user/dashboard/info - успешное получение")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.POSITIVE)
    @Tag(Tags.CONTRACT)
    void getDashboardInfoSuccess() {
        Response response = step("GET /user/dashboard/info", () ->
                dashboardSteps.getDashboardInfo(token)
        );
        step("Валидация ответа", () ->
                dashboardSteps.validateDashboardInfoResponse(response)
        );
    }

    @Test
    @Order(2)
    @DisplayName("GET /user/dashboard/info без авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void getDashboardInfoWithoutAuth() {
        Response response = step("GET /user/dashboard/info без токена", () ->
                dashboardSteps.getDashboardInfo("")
        );
        step("проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode())
        );
    }

    @Test
    @Order(3)
    @DisplayName("GET /user/dashboard/info с невалидным токеном")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void getDashboardInfoInvalidToken() {
        Response response = step("GET /user/dashboard/info без токена", () ->
                dashboardSteps.getDashboardInfo("invalid")
        );
        step("проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode())
        );
    }

    @Test
    @Order(4)
    @DisplayName("Проверка известных дашбордов")
    @Severity(SeverityLevel.MINOR)
    @Tag(Tags.POSITIVE)
    @Tag(Tags.CONTRACT)
    void verifyKnownDashboardsExist() {
        Response response = dashboardSteps.getDashboardInfo(token);
        step("Проверка информации об известных бордах", () -> {
            List<String> dashboards = response.jsonPath().getList("$");
            assertTrue(dashboards.contains("tutorDiet"), "Должен быть tutorDiet");
            assertTrue(dashboards.contains("iq"), "Должен быть iq");
            assertTrue(dashboards.contains("beauty"), "Должен быть beauty");
            assertTrue(dashboards.contains("telegram"), "Должен быть telegram");
            assertTrue(dashboards.contains("avitorg"), "Должен быть avitorg");
            assertTrue(dashboards.contains("astrology"), "Должен быть astrology");
            assertTrue(dashboards.contains("matrix"), "Должен быть matrix");
            assertTrue(dashboards.contains("taro"), "Должен быть taro");
            assertTrue(dashboards.contains("hiromant"), "Должен быть hiromant");
        });
    }
}
