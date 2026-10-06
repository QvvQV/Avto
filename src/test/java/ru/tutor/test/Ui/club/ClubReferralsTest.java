package ru.tutor.test.Ui.club;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import ru.tutor.config.TestConfig;
import ru.tutor.model.club.ClubReferralsResponse;
import ru.tutor.steps.account.AuthSteps;
import ru.tutor.steps.club.ClubSteps;
import ru.tutor.constants.Tags;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@Epic("API Тесты пользователя")
@Feature("Рефералы клуба")
@DisplayName("Тесты эндпоинта клуба: GET /user/club/v2/referrals")
public class ClubReferralsTest {

    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private static final String TEST_EMAIL = TestConfig.getTestEmail();
    private static final String TEST_PASSWORD = TestConfig.getTestPassword();

    private static final int EXPECTED_LIMIT = 10;
    private static final int EXPECTED_PAGE = 1;
    private static final int SMALLEST_REFERALS_COUNT = 2;

    private final ClubSteps clubSteps = new ClubSteps(BASE_URL);
    private final AuthSteps userSteps = new AuthSteps(BASE_URL);

    private String authToken;

    @BeforeAll
    public static void setupAll() {
        Allure.label("component", "API");
        Allure.label("feature", "User Club Referrals");
    }

    @BeforeEach
    public void setup() {
        authToken = step("Логин тестового пользователя", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD));
    }

    @Test
    @DisplayName("✅ Контракт ответа рефералов: 200, items, pagination, summary")
    @Story("Основной сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.API)
    @Tag(Tags.POSITIVE)
    @Tag(Tags.SMOKE)
    void getClubReferrals_Contract() {
        ClubReferralsResponse referrals = step("GET /user/club/v2/referrals", () ->
                clubSteps.getAndValidateClubReferrals(authToken, EXPECTED_LIMIT, EXPECTED_PAGE));
        step("Проверка структуры элементов списка", () ->
                clubSteps.validateReferralsItems(referrals.safeItems()));
        step("Проверка сортировки по incomePerMonth (desc)", () -> {
            assumeTrue(referrals.items().size() >= SMALLEST_REFERALS_COUNT,
                    "Для проверки сортировки нужно минимум " + SMALLEST_REFERALS_COUNT + " реферала, получено: " + referrals.items().size());
            clubSteps.validateReferralsSortedByIncomePerMonthDesc(referrals.items());

            step("Сверка summary с элементами списка (pages == 1)", () -> {
                assumeTrue(referrals.pagination().pages() == 1,
                        "summary глобальный: сверка с items корректна только при pages == 1, получено pages="
                + referrals.pagination().pages());
                clubSteps.validateReferralsSummaryMatchesItems(referrals);
            });
        });
    }

    @Test
    @DisplayName("🔒 Невалидный токен: 401")
    @Story("Безопасность")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.API)
    @Tag(Tags.NEGATIVE)
    void getClubReferrals_InvalidToken_ShouldFail() {
        Response response = step("GET /user/club/v2/referrals невалидным токеном", () ->
                clubSteps.getClubReferrals("invalid_token"));
        step("Проверка ошибки авторизации", () ->
                clubSteps.validateUnauthorizedResponse(response));
    }

    @Test
    @DisplayName("🔒 Пустой токен: 401")
    @Story("Безопасность")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.API)
    @Tag(Tags.NEGATIVE)
    void getClubReferrals_WithoutToken_ShouldFail() {
        Response response = step("GET /user/club/v2/referrals без токена", () ->
                clubSteps.getClubReferrals(""));
        step("Проверка ошибки авторизации", () ->
                clubSteps.validateUnauthorizedResponse(response));
    }
}
