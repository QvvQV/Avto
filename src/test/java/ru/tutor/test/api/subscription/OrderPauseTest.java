package ru.tutor.test.api.subscription;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import ru.tutor.config.TestConfig;
import ru.tutor.steps.account.AuthSteps;
import ru.tutor.steps.account.ProfileSteps;
import ru.tutor.steps.orders.OrderSteps;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

import ru.tutor.constants.Tags;


@Epic("API Тесты пользователя")
@Feature("Управление подпиской")
@DisplayName("Тесты паузы подписки")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag(Tags.INTEGRATION)
@Tag(Tags.API)
public class OrderPauseTest {

    private final AuthSteps userSteps = new AuthSteps(TestConfig.getCurrentApiUrl());
    private final OrderSteps orderSteps = new OrderSteps(TestConfig.getCurrentApiUrl());
    private final ProfileSteps profileSteps = new ProfileSteps(TestConfig.getCurrentApiUrl());
    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();

    private static String token;
    private static String basePausedTill;

    @BeforeAll
    public static void setUp() {
        Allure.label("component", "API");
        Allure.label("feature", "Orders");
    }

    @BeforeEach
    void setUpAuth() {
        token = step("Логин пользователя", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD));
    }

    @Test
    @Order(1)
    @DisplayName("PUT /user/orders/pause/7 - поставить паузу на 7 дней")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.POSITIVE)
    void pauseOrderSuccess() {
        basePausedTill = step("Профиль до паузы (pausedTill Ожидается null)", () ->
                profileSteps.getProfile(token).jsonPath().getString("pausedTill"));
        Response response = step("PUT /user/orders/pause/7", () ->
                orderSteps.pauseOrder(token, 7)
        );
        step("Валидация ответа", () ->
                orderSteps.validateEmptyArrayResponse(response));
        step("Пауза выставила pausedTill = now + days", () -> {
            String pausedTill = profileSteps.getProfile(token).jsonPath().getString("pausedTill");
            assertNotEquals(basePausedTill, pausedTill, "pausedTill должен измениться. Было: " +
                    basePausedTill + " стало: " +  pausedTill + ")");
            assertNotNull(pausedTill, "pausedTill не должен быть null после паузы");
        });
    }

    @Test
    @Order(2)
    @DisplayName("PUT /user/orders/unpause - снять паузу")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.POSITIVE)
    void unpauseOrderSuccess() {
        Response response = step("PUT /user/orders/unpause", () ->
                orderSteps.unpauseOrder(token)
        );
        step("Валидация ответа", () ->
                orderSteps.validateEmptyArrayResponse(response));
        step("После unpause состояние вернулось к исходному pausedTill=null", () -> {
            String restored = profileSteps.getProfile(token).jsonPath().getString("pausedTill");
            assertNull(restored, "Состояние pausedTill должно вернуться к исходному (было: " +
                    basePausedTill + ", стало: " +  restored + ")");
        });
    }

    @Test
    @Order(3)
    @DisplayName("PUT /user/orders/pause/7 - без авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void pauseOrderWithoutAuth() {
        Response response = step("PUT /user/orders/pause/7 без токена", () ->
                orderSteps.pauseOrder("", 7)
        );
        step("Проверка статуса 401", () ->
                orderSteps.assertUnauthorizedWithMessage(response, "JWT Token not found")
        );
    }

    @Test
    @Order(4)
    @DisplayName("PUT /user/orders/unpause - без авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void unpauseOrderWithoutAuth() {
        Response response = step("PUT /user/orders/unpause без токена", () ->
                orderSteps.unpauseOrder("")
        );
        step("Проверка статуса 401", () ->
                orderSteps.assertUnauthorizedWithMessage(response, "JWT Token not found"));
    }

    @Test
    @Order(5)
    @DisplayName("PUT /user/orders/pause/7 с инвалидным токеном")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void pauseOrderWithInvalidToken() {
        Response response = step("PUT /user/orders/pause/7 с невалидным токеном", () ->
                orderSteps.pauseOrder("invalid_token", 7)
        );
        step("Проверка статуса 401", () ->
                orderSteps.assertUnauthorizedWithMessage(response, "Invalid JWT Token")
        );
    }

    @Test
    @Order(6)
    @DisplayName("PUT /user/orders/unpause с невалидным токеном")
    @Tag(Tags.CONTRACT)
    void  unpauseOrderWithInvalidToken() {
        Response response = step ("POST /user/orders/unpause с инвалидным токеном", () ->
                orderSteps.unpauseOrder("invalid")
        );
        step("Проверка статуса 401", () ->
                orderSteps.assertUnauthorizedWithMessage(response, "Invalid JWT Token")
        );
    }
}