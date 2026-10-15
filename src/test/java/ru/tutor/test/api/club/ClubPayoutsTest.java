package ru.tutor.test.api.club;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import ru.tutor.config.TestConfig;
import ru.tutor.constants.Tags;
import ru.tutor.model.club.ClubPayoutsResponse;
import ru.tutor.steps.account.AuthSteps;
import ru.tutor.steps.club.ClubSteps;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@Epic("API Тесты пользователя")
@Feature("Выплаты клуба")
@DisplayName("Тесты эндпоинта клуба: GET /user/club/v2/payouts")
@Tag(Tags.INTEGRATION)
@Tag(Tags.API)
public class ClubPayoutsTest {

    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private static final String TEST_EMAIL = TestConfig.getTestEmail();
    private static final String TEST_PASSWORD = TestConfig.getTestPassword();

    private final ClubSteps clubSteps = new ClubSteps(BASE_URL);
    private final AuthSteps authSteps = new AuthSteps(BASE_URL);

    @BeforeAll
    public static void setupAll() {
        Allure.label("component", "API");
        Allure.label("feature", "User Club Payouts");
    }

    @Test
    @DisplayName("Контракт списка заявок: 200, items")
    @Story("Основной сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    @Tag(Tags.CONTRACT)
    void getClubPayouts() {
        String authToken = step("Логин тестового пользователя", () ->
                authSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD));

        ClubPayoutsResponse payouts = step("GET /user/club/v2/payouts?page=1&per_page=20", () ->
                clubSteps.validatePayoutsResponse(clubSteps.getPayouts(authToken)));

        step("Проверка пагинации (если присутсвует в ответе)", () -> {
            assumeTrue(payouts.hasPagination(), "Ответ не содержит блок pagination");
            Assertions.assertAll("Проверка пагинации",
                    () -> Assertions.assertEquals(1, payouts.pagination().page(), "page должен быть 1"),
                    () -> Assertions.assertEquals(20, payouts.pagination().perPage(), "per_page должен быть 20"));
        });
    }

    @Test
    @DisplayName("Невалидный токен: 401")
    @Story("Безопасность")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void getClubPayoutsWithInvalidToken() {
        Response response = step("GET /user/club/v2/payouts с невалидным токеном", () ->
                clubSteps.getPayouts("invalid_token"));
        step("Проверка ошибки авторизации", () ->
                clubSteps.validateUnauthorizedResponse(response));
    }

    @Test
    @DisplayName("Пустой токен: 401")
    @Story("Безопасность")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void getClubPayoutsWithoutToken() {
        Response response = step("GET /user/club/v2/payouts без токена", () ->
                clubSteps.getPayouts(""));
        step("Проверка ошибки авторизации", () ->
                clubSteps.validateUnauthorizedResponse(response));
    }
}
