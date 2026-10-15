package ru.tutor.test.api.user;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import ru.tutor.model.account.UserProfile;
import ru.tutor.steps.account.AuthSteps;
import ru.tutor.steps.account.ProfileSteps;
import ru.tutor.config.TestConfig;

import java.util.List;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

import ru.tutor.constants.Tags;


@Epic("API Тесты пользователя")
@Feature("Аутентификация и профиль")
@DisplayName("Тесты: Логин → Получение профиля → Проверка данных")
@Story("Полный сценарий аутентификации")
@Tag(Tags.INTEGRATION)
@Tag(Tags.API)
public class UserLoginProfileTest {

    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();
    private final ProfileSteps userSteps = new ProfileSteps(BASE_URL);
    private final AuthSteps authSteps = new AuthSteps(BASE_URL);

    private UserProfile getProfile() {
        String token = authSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD);
        Response response = userSteps.getProfile(token);
        return userSteps.validateProfileResponse(response, TEST_EMAIL);
    }

    @Test
    @DisplayName("Полный сценарий: Логин → Профиль → Проверка флагов")
    @Tag(Tags.POSITIVE)
    void fullLoginProfileScenario() {

        getProfile();
    }

    // ==================== Отрицательные тесты ====================

    @Test
    @DisplayName("Без токена -> 401")
    @Tag(Tags.CONTRACT)
    @Tag(Tags.NEGATIVE)
    void getProfileWithoutToken() {
        Response response = step("GET /user/profile без токена", () ->
                userSteps.getProfile("")
        );

        step("Проверка статуса 401", () ->
                userSteps.assertUnauthorizedWithMessage(response, "JWT Token not found")
        );
    }

    @Test
    @DisplayName("Невалидный токен -> 401")
    @Tag(Tags.CONTRACT)
    @Tag(Tags.NEGATIVE)
    void getProfileWithoutInvalidToken() {
        Response response = step("GET /user/profile невалидный токен", () ->
                userSteps.getProfile("invalid_token")
        );

        step("Проверка статуса 401", () ->
                userSteps.assertUnauthorizedWithMessage(response, "Invalid JWT Token")
        );
    }

    // ==================== Бизнес логика полей ====================

    @Test
    @DisplayName("id должен быть положительным числом")
    @Tag(Tags.CONTRACT)
    @Tag(Tags.POSITIVE)
    void profileIdPositive() {
        UserProfile profile = getProfile();
        assertTrue(profile.getId() > 0, "id должен быть > 0, получено: " + profile.getId());
    }

    @Test
    @DisplayName("email должен содержать @")
    @Tag(Tags.CONTRACT)
    @Tag(Tags.POSITIVE)
    void profileEmailFormat() {
        UserProfile profile = getProfile();
        assertTrue(profile.getEmail().contains("@"), "email должен содержать '@', получено: " + profile.getEmail());
    }

    @Test
    @DisplayName("clubMembership должен быть одним из: full, light, none")
    @Tag(Tags.CONTRACT)
    @Tag(Tags.POSITIVE)
    void clubMembershipValid() {
        UserProfile profile = getProfile();
        assertNotNull(profile.getClubMemberShip(), "clubMemberShip не должен быть null");
        assertTrue(List.of("full", "light", "none").contains(profile.getClubMemberShip()),
                "clubMembership должен быть 'full', 'light' или 'none', получено: " + profile.
                        getClubMemberShip());
    }
}