package ru.tutor.test.api.club;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import ru.tutor.config.TestConfig;
import ru.tutor.constants.Tags;
import ru.tutor.model.club.ClubReferralsResponse;
import ru.tutor.steps.account.AuthSteps;
import ru.tutor.steps.club.ClubAnalyticsSteps;
import ru.tutor.steps.club.ClubHomeSteps;
import ru.tutor.steps.club.ClubReferralsSteps;

import java.util.List;
import java.util.Map;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@Epic("API Тесты пользователя клуба")
@Feature("Клуб: главная и аналитика v2")
@DisplayName("Контрактные тесты клуба v2: home / analytics / links / referrals")
@Tag(Tags.INTEGRATION)
@Tag(Tags.API)
public class ClubHomeContractTest {

    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private static final String TEST_EMAIL = TestConfig.getTestEmail();
    private static final String TEST_PASSWORD = TestConfig.getTestPassword();

    private static final String NONEXISTENT_USER_ID = "999999999";
    private static final String NONEXISTENT_NEWS_ID = "999999";

    private final ClubHomeSteps clubHomeSteps = new ClubHomeSteps(BASE_URL);
    private final ClubAnalyticsSteps clubAnalyticsSteps = new ClubAnalyticsSteps(BASE_URL);
    private final ClubReferralsSteps clubReferralsSteps = new ClubReferralsSteps(BASE_URL);
    private final AuthSteps userSteps = new AuthSteps(BASE_URL);

    private String authToken;

    private String firstReferralUserId() {
        return step("Получение userId первого реферала", () -> {
            ClubReferralsResponse referrals = clubReferralsSteps.validateClubReferralsResponse(
                    clubReferralsSteps.getClubReferrals(authToken)
            );
            return referrals.safeItems().isEmpty() ? null : String.valueOf(referrals.safeItems().getFirst().userId());
        });
    }

    private String firstNewsSlug() {
        return step("Получение slug первой новости", () -> {
            Response news = clubHomeSteps.getClubHomeNews(authToken);
            List<Map<String, Object>> items = news.jsonPath().getList("items");
            return items.isEmpty() ? null : String.valueOf(items.getFirst().get("slug"));
        });
    }

    @BeforeAll
    public static void setupAll() {
        Allure.label("component", "API");
        Allure.label("feature", "User Club Home v2");
    }

    @BeforeEach
    public void setup() {
        authToken = step("Логин тестового пользователя", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD));
    }

    @Test
    @DisplayName("GET /user/club/v2/home/analytics: 200, totals, series")
    @Story("Главная клуба")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.POSITIVE)
    @Tag(Tags.CONTRACT)
    void homeAnalytics() {
        Response response = step("GET /user/club/v2/home/analytics", () ->
                clubHomeSteps.getClubHomeAnalytics(authToken));
        step("Проверка структуры аналитики", () ->
                clubHomeSteps.validateHomeAnalytics(response));
    }

    @Test
    @DisplayName("GET /user/club/v2/home/banners: 200, структура баннеров")
    @Story("Главная клуба")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.POSITIVE)
    @Tag(Tags.CONTRACT)
    void homeBanners() {
        Response response = step("GET /user/club/v2/home/banners", () ->
                clubHomeSteps.getClubHomeBanners(authToken));
        step("Проверка структуры баннеров", () ->
                clubHomeSteps.validateHomeBanners(response));
    }

    @Test
    @DisplayName("GET /user/club/v2/home/links: 200, структура ссылок")
    @Story("Главная клуба")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.POSITIVE)
    @Tag(Tags.CONTRACT)
    void homeLinks() {
        Response response = step("GET /user/club/v2/home/links", () ->
                clubHomeSteps.getClubHomeLinks(authToken));
        step("Проверка структуры ссылок", () ->
                clubHomeSteps.validateHomeLinks(response));
    }

    @Test
    @DisplayName("GET /user/club/v2/home/news: 200, items + pagination")
    @Story("Главная клуба")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.POSITIVE)
    @Tag(Tags.CONTRACT)
    void homeNews() {
        Response response = step("GET /user/club/v2/home/news", () ->
                clubHomeSteps.getClubHomeNews(authToken));
        step("Проверка структуры новостей", () ->
                clubHomeSteps.validateHomeNews(response));
    }

    @Test
    @DisplayName("GET /user/club/v2/home/news/{id}: 404 для отсутствующей новости")
    @Story("Главная клуба")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void homeNewsDetailWhenNotFound() {
        Response response = step("GET /user/club/v2/home/news/" + NONEXISTENT_NEWS_ID, () ->
                clubHomeSteps.getClubNewsDetail(authToken, NONEXISTENT_NEWS_ID));
        step("Проверка 404", () ->
                clubHomeSteps.validateNotFound(response, "/user/club/v2/home/news/{id}"));
    }

    @Test
    @DisplayName("GET /user/club/v2/analytics/links: 200, структура")
    @Story("Аналитика клуба")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.POSITIVE)
    @Tag(Tags.CONTRACT)
    void analyticsLinks() {
        Response response = step("GET /user/club/v2/analytics/links", () ->
                clubAnalyticsSteps.getClubAnalyticsLinks(authToken));
        step("Проверка структуры аналитики ссылок", () ->
                clubAnalyticsSteps.validateAnalyticsLinks(response));
    }

    @Test
    @DisplayName("GET /user/club/v2/analytics/monthly-income: 200, уровни + totals")
    @Story("Аналитика клуба")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.POSITIVE)
    @Tag(Tags.CONTRACT)
    void analyticsMonthlyIncome() {
        Response response = step("GET /user/club/v2/analytics/monthly-income", () ->
                clubAnalyticsSteps.getClubAnalyticsMonthlyIncome(authToken));
        step("Проверка структуры дохода по уровням", () ->
                clubAnalyticsSteps.validateAnalyticsMonthlyIncome(response));
    }

    @Test
    @DisplayName("GET /user/club/v2/links/summary: 200, totals + breakdown")
    @Story("Аналитика клуба")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.POSITIVE)
    @Tag(Tags.CONTRACT)
    void linksSummary() {
        Response response = step("GET /user/club/v2/links/summary", () ->
                clubAnalyticsSteps.getClubLinksSummary(authToken));
        step("Проверка структуры сводки", () ->
                clubAnalyticsSteps.validateLinksSummary(response));
    }

    @Test
    @DisplayName("GET /user/club/v2/referrals/{userId}: 200, детальная информация")
    @Story("Рефералы клуба")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    void referralDetail() {
        String userId = firstReferralUserId();
        assumeTrue(userId != null, "Нет рефералов у пользователя — пропуск детальной ручки");

        Response response = step("GET /user/club/v2/referrals/{userId}", () ->
                clubReferralsSteps.getClubReferralDetail(authToken, userId));
        step("Проверка структуры реферала", () ->
                clubReferralsSteps.validateReferralDetail(response));
    }

    @Test
    @DisplayName("GET /user/club/v2/referrals/{userId}/referrals: 200, дерево рефералов")
    @Story("Рефералы клуба")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    void referralReferrals() {
        String userId = firstReferralUserId();
        assumeTrue(userId != null, "Нет рефералов у пользователя — пропуск дерева рефералов");

        Response response = step("GET /user/club/v2/referrals/{userId}/referrals", () ->
                clubReferralsSteps.getClubReferralReferrals(authToken, userId));
        step("Проверка структуры дерева рефералов", () ->
                clubReferralsSteps.validateReferralReferrals(response));
    }

    @Test
    @DisplayName("GET /user/club/v2/referrals/{userId}: 404 для несуществующего реферала")
    @Story("Рефералы клуба")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void referralDetailWhenNotFound() {
        Response response = step("GET /user/club/v2/referrals/" + NONEXISTENT_USER_ID, () ->
                clubReferralsSteps.getClubReferralDetail(authToken, NONEXISTENT_USER_ID));
        step("Проверка 404", () ->
                clubReferralsSteps.validateNotFound(response, "/user/club/v2/referrals/{userId}"));
    }

    @Test
    @DisplayName("GET /user/club/v2/home/analytics без токена: 401")
    @Story("Безопасность")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void homeAnalyticsWithoutToken() {
        Response response = step("GET /user/club/v2/home/analytics без токена", () ->
                clubHomeSteps.getClubHomeAnalytics(""));
        step("Проверка ошибки авторизации", () ->
                clubHomeSteps.assertUnauthorizedWithMessage(response, "JWT Token not found") );
    }

    @Test
    @DisplayName("GET /user/club/v2/home/banners без токена: 401")
    @Story("Безопасность")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void homeBannersWithoutToken() {
        Response response = step("GET /user/club/v2/home/banners без токена", () ->
                clubHomeSteps.getClubHomeBanners(""));
        step("Проверка ошибки авторизации", () ->
                clubHomeSteps.assertUnauthorizedWithMessage(response, "JWT Token not found") );
    }

    @Test
    @DisplayName("GET /user/club/v2/home/links без токена: 401")
    @Story("Безопасность")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void homeLinksWithoutToken() {
        Response response = step("GET /user/club/v2/home/links без токена", () ->
                clubHomeSteps.getClubHomeLinks(""));
        step("Проверка ошибки авторизации", () -> clubHomeSteps.assertUnauthorizedWithMessage(response, "JWT Token not found") );
    }

    @Test
    @DisplayName("GET /user/club/v2/home/news без токена: 401")
    @Story("Безопасность")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void homeNewsWithoutToken() {
        Response response = step("GET /user/club/v2/home/news без токена", () ->
                clubHomeSteps.getClubHomeNews(""));
        step("Проверка ошибки авторизации", () -> clubHomeSteps.assertUnauthorizedWithMessage(response,  "JWT Token not found") );
    }

    @Test
    @DisplayName("GET /user/club/v2/analytics/links без токена: 401")
    @Story("Безопасность")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void analyticsLinksWithoutToken() {
        Response response = step("GET /user/club/v2/analytics/links без токена", () ->
                clubAnalyticsSteps.getClubAnalyticsLinks(""));
        step("Проверка ошибки авторизации", () -> clubAnalyticsSteps.assertUnauthorizedWithMessage(response,  "JWT Token not found") );
    }

    @Test
    @DisplayName("GET /user/club/v2/analytics/monthly-income без токена: 401")
    @Story("Безопасность")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void analyticsMonthlyIncomeWithoutToken() {
        Response response = step("GET /user/club/v2/analytics/monthly-income без токена", () ->
                clubAnalyticsSteps.getClubAnalyticsMonthlyIncome("") );
        step("Проверка ошибки авторизации", () -> clubAnalyticsSteps.assertUnauthorizedWithMessage(response, "JWT Token not found") );
    }

    @Test
    @DisplayName("GET /user/club/v2/links/summary без токена: 401")
    @Story("Безопасность")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void linksSummaryWithoutToken() {
        Response response = step("GET /user/club/v2/links/summary без токена", () ->
                clubAnalyticsSteps.getClubLinksSummary(""));
        step("Проверка ошибки авторизации", () -> clubAnalyticsSteps.assertUnauthorizedWithMessage(response,   "JWT Token not found") );
    }

    @Test
    @DisplayName("GET /user/club/v2/home/news/{slug}: 200, детальная информация")
    @Story("Главная клуба")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.POSITIVE)
    void homeNewsDetail() {
        String slug = firstNewsSlug();
        assumeTrue(slug != null, "Список новостей пуст - пропуск детальной ручки");
        Response response = step("GET /user/club/v2/home/news/" + slug, () ->
                clubHomeSteps.getClubNewsDetail(authToken, slug));
        step("Проверка структуры новостей", () ->
                clubHomeSteps.validateClubNewsDetail(response, slug));
    }
}
