package ru.tutor.test.api.dashboard;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import ru.tutor.config.TestConfig;
import ru.tutor.steps.account.AuthSteps;
import ru.tutor.steps.dashboard.DashboardSteps;

import java.util.LinkedHashSet;
import java.util.Set;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import ru.tutor.constants.Tags;

@Epic("API Тесты пользователя")
@Feature("Дашборды")
@DisplayName("Тесты эндпоинта dashboard/info")
@Tag(Tags.INTEGRATION)
@Tag(Tags.API)
public class DashboardInfoTest {

    private final AuthSteps userSteps = new AuthSteps(TestConfig.getCurrentApiUrl());
    private final DashboardSteps dashboardSteps = new DashboardSteps(TestConfig.getCurrentApiUrl());
    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();

    private static final Set<String> EXPECTED_DASHBOARDS = Set.of(
            "tutorDiet", "iq", "beauty", "telegram", "avitorg",
            "astrology", "matrix", "taro", "hiromant"
    );

    @BeforeAll
    public static void setUp() {
        Allure.label("component", "API");
        Allure.label("feature", "Dashboard");
    }

    @Test
    @DisplayName("GET /user/dashboard/info - успешное получение")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.POSITIVE)
    @Tag(Tags.CONTRACT)
    void getDashboardInfoSuccess() {
        String token = step("Логин пользователя", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD));

        Response response = step("GET /user/dashboard/info", () ->
                dashboardSteps.getDashboardInfo(token)
        );
        step("Валидация ответа", () ->
                dashboardSteps.validateDashboardInfoResponse(response)
        );
        step("Набор дашбордов совпадает с ожидаемым", () -> {
            Set<String> actual = new LinkedHashSet<>(response.jsonPath().getList("$", String.class));
            assertEquals(EXPECTED_DASHBOARDS, actual, "Набор дашбордов не совпадает");
        });
    }

    @Test
    @DisplayName("GET /user/dashboard/info без авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void getDashboardInfoWithoutAuth() {
        Response response = step("GET /user/dashboard/info без токена", () ->
                dashboardSteps.getDashboardInfo("")
        );
        step("Проверка статуса 401", () ->
                dashboardSteps.assertUnauthorizedWithMessage(response, "JWT Token not found"));
    }

    @Test
    @DisplayName("GET /user/dashboard/info с невалидным токеном")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void getDashboardInfoInvalidToken() {
        Response response = step("GET /user/dashboard/info с невалидным токеном", () ->
                dashboardSteps.getDashboardInfo("invalid")
        );
        step("Проверка статуса 401 + тело", () ->
                dashboardSteps.assertUnauthorizedWithMessage(response, "Invalid JWT Token")
        );
    }
}
