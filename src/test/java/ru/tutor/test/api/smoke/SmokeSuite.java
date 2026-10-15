package ru.tutor.test.api.smoke;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.tutor.config.TestConfig;
import ru.tutor.constants.Tags;
import ru.tutor.model.account.LoginResponse;
import ru.tutor.model.account.RegistrationRequest;
import ru.tutor.model.account.RegistrationResponse;
import ru.tutor.steps.account.AuthSteps;
import ru.tutor.steps.account.ProfileSteps;
import ru.tutor.steps.account.RegistrationSteps;
import ru.tutor.steps.club.ClubHomeSteps;
import ru.tutor.steps.club.ClubPayoutsSteps;
import ru.tutor.steps.fortune.GameWheelMiscSteps;
import ru.tutor.steps.orders.OrderSteps;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

@Epic("API Тесты пользователя")
@Feature("Smoke-гейт")
@DisplayName("Smoke: быстрые проверки ключевых путей ")
@Tag(Tags.SMOKE)
@Tag(Tags.API)
public class SmokeSuite {

    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private static final String TEST_EMAIL = TestConfig.getTestEmail();
    private static final String TEST_PASSWORD = TestConfig.getTestPassword();

    private final AuthSteps authSteps = new AuthSteps(BASE_URL);
    private final ProfileSteps profileSteps = new ProfileSteps(BASE_URL);
    private final RegistrationSteps registrationSteps = new RegistrationSteps(BASE_URL);
    private final ClubHomeSteps clubHomeSteps = new ClubHomeSteps(BASE_URL);
    private final ClubPayoutsSteps clubPayoutsSteps = new ClubPayoutsSteps(BASE_URL);
    private final GameWheelMiscSteps gameWheelMiscSteps = new GameWheelMiscSteps(BASE_URL);
    private final OrderSteps orderSteps = new OrderSteps(BASE_URL);

    private String authToken;

    @BeforeEach
    public void setup() {
        authToken = step("Логин тестового пользователя", () ->
                authSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD));
    }

    @Test
    @DisplayName("✅ Логин: POST /user/login -> токен")
    @Story("Авторизация")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    void loginSmoke() {
        LoginResponse login = step("POST /user/login", () ->
                authSteps.loginAndGetTokens(TEST_EMAIL, TEST_PASSWORD));
        step("Валидация ответа логина", () ->
                authSteps.validateLoginResponse(login));
    }

    @Test
    @DisplayName("✅ Логин + GET /user/profile: email совпадает")
    @Story("Авторизация")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    void profileSmoke() {
        Response response = step("GET /user/profile", () ->
                profileSteps.getProfile(authToken));
        step("Валидация профиля", () ->
                profileSteps.validateProfileResponse(response, TEST_EMAIL));
    }

    @Test
    @DisplayName("✅ Регистрация нового пользователя: message == ok")
    @Story("регистрация")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    void registrationSmoke() {
        RegistrationRequest request = RegistrationRequest.generateRandom();
        RegistrationResponse registration = step("POST /user/registration", () ->
                registrationSteps.validateRegistrationResponse(registrationSteps.register(request)));
        step("Проверка результата", () -> {
            assertEquals("ok", registration.getMessage(), "Сообщение должно быть 'ok'");
            assertTrue(registration.isSuccessful(), "Регистрация должна быть успешной");
        });
    }

    @Test
    @DisplayName("✅ Клуб: GET /user/club/v2/home/analytics")
    @Story("Клуб")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    void clubHomeAnalyticsSmoke() {
        Response response = step("GET /user/club/v2/home/analytics", () ->
                clubHomeSteps.getClubHomeAnalytics(authToken));
        step("Валидация аналитики", () ->
                clubHomeSteps.validateHomeAnalytics(response));
    }

    @Test
    @DisplayName("✅ Клуб: GET /user/club/v2/payouts/summary")
    @Story("Клуб")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    void clubPayoutSummarySmoke() {
        Response response = step("GET /user/club/v2/payouts/summary", () ->
                clubPayoutsSteps.getPayoutSummary(authToken));
        step("Валидация сводки выплат", () ->
                clubPayoutsSteps.validateClubPayoutSummaryResponse(response));
    }

    @Test
    @DisplayName("✅ Колесо: GET /user/game-wheel/timer-left-seconds")
    @Story("Игровое колесо")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    void gameWheelTimerSmoke() {
        Response response = step("GET /user/game-wheel/timer-left-seconds", () ->
                gameWheelMiscSteps.getUserGameWheelTimerLeft(authToken));
        step("Валидация таймера", () ->
                gameWheelMiscSteps.validateTimerLeftSeconds(response));
    }

    @Test
    @DisplayName("✅ Платежи: GET /user/orders/payments")
    @Story("Платежи")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    void ordersPaymentsSmoke() {
        Response response = step("GET /user/orders/payments", () ->
                orderSteps.getOrdersPayments(authToken));
        step("Валидация истории платежей", () ->
                orderSteps.validateOrdersPaymentsResponse(response));
    }
}
