package ru.tutor.test.api.user;

import io.qameta.allure.*;
import io.qameta.allure.junit5.AllureJunit5;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tutor.model.UserProfile;
import ru.tutor.steps.UserSteps;
import ru.tutor.config.TestConfig;

import java.util.List;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(AllureJunit5.class)
@Epic("API Тесты пользователя")
@Feature("Аутентификация и профиль")
@DisplayName("Тесты: Логин → Получение профиля → Проверка данных")
@Story("Полный сценарий аутентификации")
public class UserLoginProfileTest {

    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();
    private final UserSteps userSteps = new UserSteps(BASE_URL);

    private UserProfile getProfile() {
        String token = userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD);
        Response response = userSteps.getProfile(token);
        return userSteps.validateProfileResponse(response, TEST_EMAIL);
    }

    @Test
    @DisplayName("Полный сценарий: Логин → Профиль → Проверка флагов")
    void fullLoginProfileScenario() {

        UserProfile profile =  getProfile();

        step("Проверка ключевых полей профиля", () -> {
            assertNotNull(profile.getName(), "name не должен быть null");
            assertNotNull(profile.getPremium(), "premium не должен быть null");
            assertNotNull(profile.getDiamondBalance(), "diamondBalance не должен быть null");
            assertTrue(profile.getDiamondBalance() >= 0, "diamondBalance должен быть => 0");
        });
    }

    // ==================== Отрицательные тесты ====================

    @Test
    @DisplayName("Без токена -> 401")
    void getProfileWithoutToken() {
        Response response = step("GET /user/profile без токена", () ->
                userSteps.getProfile("")
        );

        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode())
        );
    }

    @Test
    @DisplayName("Невалидный токен -> 401")
    void getProfileWithoutInvalidToken() {
        Response response = step("GET /user/profile невалидный токен", () ->
                userSteps.getProfile("invalid_token")
        );

        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode())
        );
    }

    // ==================== Бизнес логика полей ====================

    @Test
    @DisplayName("id должен быть положительным числом")
    void profileIdPositive() {
        UserProfile profile = getProfile();
        assertTrue(profile.getId() > 0, "id должен быть > 0, получено: " + profile.getId());
    }

    @Test
    @DisplayName("email должен содержать @")
    void profileEmailFormat() {
        UserProfile profile = getProfile();
        assertTrue(profile.getEmail().contains("@"), "email должен содержать '@', получено: " + profile.getEmail());
    }

    @Test
    @DisplayName("clubMembership должен быть одним из: full, light, none")
    void clubMembershipValid() {
        UserProfile profile = getProfile();
        assertNotNull(profile.getClubMemberShip(), "clubMemberShip не должен быть null");
        assertTrue(List.of("full", "light", "none").contains(profile.getClubMemberShip()),
                "clubMembership должен быть 'full', 'light' или 'none', получено: " + profile.
                        getClubMemberShip());
    }

    @Test
    @DisplayName("isTariffLifetime, isYtSubscribed, isVkSubscribed, isTgSubscribed — boolean")
    void subscriptionFlagsAreBooleans() {
        UserProfile profile = getProfile();
        assertNotNull(profile.getIsTariffLifetime(), "isTariffLifeTime не долежн быть null");
        assertNotNull(profile.getYtSubscribed(), "isYtSubscribed не должен быть null");
        assertNotNull(profile.getVkSubscribed(), "isVkSubscribed не должен быть null");
        assertNotNull(profile.getTgSubscribed(), "isTgSubscribed не должен быть null");
    }
}