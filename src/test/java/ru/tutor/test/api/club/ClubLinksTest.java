package ru.tutor.test.api.club;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.tutor.config.TestConfig;
import ru.tutor.constants.Tags;
import ru.tutor.model.club.ClubLinksResponse;
import ru.tutor.steps.account.AuthSteps;
import ru.tutor.steps.club.ClubLinksSteps;

import java.util.Map;

import static io.qameta.allure.Allure.step;

@Epic("API Тесты пользователя клуба")
@Feature("Реферальные ссылки клуба")
@DisplayName("Тесты эндпоинта клуба: GET /user/club/v2/links")
@Tag(Tags.INTEGRATION)
@Tag(Tags.API)
public class ClubLinksTest {

    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private static final String TEST_EMAIL = TestConfig.getTestEmail();
    private static final String TEST_PASSWORD = TestConfig.getTestPassword();

    private static final int EXPECTED_LIMIT = 10;
    private static final int EXPECTED_PAGE = 1;

    private final ClubLinksSteps clubSteps = new ClubLinksSteps(BASE_URL);
    private final AuthSteps userSteps = new AuthSteps(BASE_URL);

    @BeforeAll
    public static void setupAll() {
        Allure.label("component", "API");
        Allure.label("feature", "User Club Links");
    }

    @Test
    @DisplayName("Контракт ответа ссылок: 200, items, pagination")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.CONTRACT)
    @Tag(Tags.POSITIVE)
    void getClubLinks() {
        String authToken = step("Логин тестового пользователя", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD));
        ClubLinksResponse links = step("GET /user/club/v2/links?limit=10&page=1", () ->
                clubSteps.getAndValidateClubLinksResponse(authToken, EXPECTED_LIMIT, EXPECTED_PAGE));
        step("Проверка структуры элементов списка", () ->
                clubSteps.validateClubLinkItems(links.safeItems()));
    }

    @Test
    @DisplayName("limit=1: per_page=1, count<=1")
    @Story("Пагинация")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.POSITIVE)
    @Tag(Tags.CONTRACT)
    void getClubLinksWithCustomLimit() {
        String authToken = step("Логин тестового пользователя", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD));

        int limit = 1;
        ClubLinksResponse links = step("GET /user/club/v2/links?limit=1&page=1", () ->
                clubSteps.validateClubLinksResponse(
                        clubSteps.getClubLinks(authToken, Map.of("limit", limit, "page", 1))));

        step("Проверка пагинации (limit=1)", () ->
                clubSteps.validateClubLinksPagination(links, limit, 1));
    }

    @Test
    @DisplayName("Невалидный токен: 401")
    @Story("Безопасность")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void getClubLinksWithInvalidToken() {
        Response response = step("GET /user/club/v2/links без токена", () ->
                clubSteps.getClubLinks(""));
        step("Проверка ошибки авторизации", () ->
                clubSteps.assertUnauthorizedWithMessage(response, "JWT Token not found"));
    }

    @Test
    @DisplayName("Пустой токен: 401")
    @Story("Безопасность")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void getClubLinksWithoutToken() {
        Response response = step("GET /user/club/v2/links?limit=1 с пустым токеном", () ->
                clubSteps.getClubLinks(""));
        step("Проверка ошибки авторизации", () ->
                clubSteps.assertUnauthorizedWithMessage(response, "JWT Token not found"));
    }
}
