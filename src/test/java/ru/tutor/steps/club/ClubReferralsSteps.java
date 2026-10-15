package ru.tutor.steps.club;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.model.club.ClubReferralsResponse;
import ru.tutor.model.club.ClubReferralsResponse.Pagination;
import ru.tutor.model.club.ClubReferralsResponse.ReferralItem;
import ru.tutor.model.club.ClubReferralsResponse.ReferralLink;
import ru.tutor.model.club.ClubReferralsResponse.SubscriptionStatus;
import ru.tutor.model.club.ClubReferralsResponse.Summary;
import ru.tutor.utils.AllureAttachments;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ClubReferralsSteps extends ClubBaseSteps {

    private static final Set<String> REFERRAL_STATUS_CODES = Set.of("trial", "lite", "premium", "lifetime");

    public ClubReferralsSteps(String baseUrl) {
        super(baseUrl);
    }

    @Step("GET /user/club/v2/referrals - рефералы текущего пользователя")
    public Response getClubReferrals(String authToken) {
        return getClubReferrals(authToken, Map.of(
                "limit", 10, "page", 1, "sort", "incomePerMonth", "direction", "desc"));
    }

    @Step("GET /user/club/v2/referrals - рефералы с query-параметрами {queryParams}")
    public Response getClubReferrals(String authToken, Map<String, Object> queryParams) {
        Response response = clubApi.getClubReferrals(authToken, queryParams);
        AllureAttachments.attachJson("Club Referrals Response", response.getBody().asString());
        AllureAttachments.attachJson("Club Referrals Headers", response.getHeaders().toString());
        return response;
    }

    @Step("Валидация контракта ответа рефералов (200, json, CORS, items/pagination/summary)")
    public ClubReferralsResponse validateClubReferralsResponse(Response response) {
        assertBasicResponse(response);
        assertJsonKeyExists(response, "items");
        assertJsonKeyExists(response, "pagination");
        assertJsonKeyExists(response, "summary");

        ClubReferralsResponse referrals = response.as(ClubReferralsResponse.class);

        assertNotNull(referrals, "Ответ со списком рефералов не должен быть null");
        assertNotNull(referrals.items(), "items не должен быть null");
        assertNotNull(referrals.pagination(), "pagination не должен быть null");
        assertNotNull(referrals.summary(), "summary не должен быть null");

        return referrals;
    }

    @Step("Валидация пагинации рефералов (limit={expectedLimit}, page={expectedPage})")
    public void validateReferralsPagination(ClubReferralsResponse referrals, int expectedLimit, int expectedPage) {
        Pagination pagination = referrals.pagination();

        assertAll("Проверка пагинации рефералов",
                () -> assertEquals(expectedLimit, pagination.perPage(),
                        "per_page должен быть " + expectedLimit),
                () -> assertEquals(expectedPage, pagination.page(),
                        "page должен быть " + expectedPage),
                () -> assertEquals(referrals.items().size(), pagination.count(),
                        "count должен совпадать с размером items"),
                () -> assertTrue(pagination.total() >= pagination.count(),
                        "total (" + pagination.total() + ") должен быть >= count (" + pagination.count() + ")"),
                () -> assertEquals(pagination.expectedPages(), pagination.pages(),
                        "pages должен │соответствовать total / per_page"));
    }

    @Step("Валидация summary: сумма счётчиков == pagination.total")
    public void validateReferralsSummaryTotals(ClubReferralsResponse referrals) {
        Summary summary = referrals.summary();
        long withoutSubscription = referrals.countWithoutSubscription();

        assertAll("Проверка summary рефералов",
                () -> assertTrue(summary.allNonNegative(), "Счётчики summary не могут быть отрицательными"),
                () -> assertEquals(referrals.pagination().total() - withoutSubscription,
                        summary.total(),
                        "Сумма счётчиков summary (" + summary.total() + ") + рефералы без подписки ("
                                + withoutSubscription + ") должна совпадать с pagination.total ("
                                + referrals.pagination().total() + ")"));
    }

    @Step("Валидация структуры каждого элемента items")
    public void validateReferralsItems(List<ReferralItem> items) {
        for (int i = 0; i < items.size(); i++) {
            ReferralItem item = items.get(i);
            String itemInfo = "Реферал #" + (i + 1) + " (userId=" + item.userId() + ")";

            assertAll(itemInfo + " - структура",
                    () -> assertNotNull(item.userId(), itemInfo + ": userId не должен быть null"),
                    () -> assertTrue(item.userId() > 0, itemInfo + ": userId должен быть положительным"),
                    () -> assertNotNull(item.level(), itemInfo + ": level не должен быть null"),
                    () -> assertTrue(item.level() >= 1, itemInfo + ": level должен быть >= 1"),
                    () -> assertNotNull(item.incomePerMonth(), itemInfo + ": incomePerMonth не должен быть null"),
                    () -> assertTrue(item.incomePerMonth() >= 0, itemInfo + ": incomePerMonth должен быть >= 0"),
                    () -> assertNotNull(item.incomeOneTime(), itemInfo + ": incomeOneTime не должен быть null"),
                    () -> assertTrue(item.incomeOneTime() >= 0, itemInfo + ": incomeOneTime должен быть >= 0"),
                    () -> validateSubscriptionDate(item, itemInfo),
                    () -> assertTrue(item.avatarUrl() == null || item.hasValidAvatarUrl(), itemInfo + ": avatarUrl должен быть null или URL"),
                    () -> validateSubscriptionStatus(item.subscriptionStatus(), itemInfo),
                    () -> validateReferralLink(item, itemInfo)
            );
        }
    }

    private void validateSubscriptionStatus(SubscriptionStatus status, String itemInfo) {
        assertNotNull(status, itemInfo + ": subscriptionStatus не должен быть null");
        assertJsonNotEmpty(status.label(), itemInfo + ": subscriptionStatus.label");
        assertJsonNotEmpty(status.stateLabel(), itemInfo + ": subscriptionStatus.stateLabel");
        assertNotNull(status.isActive(), itemInfo + ": subscriptionStatus.isActive не должен быть null");

        if (status.code() == null) {
            assertFalse(status.isActiveSubscription(),
                    itemInfo + ": при code == null подписка не может быть активной, stateLabel=" + status.stateLabel());
            return;
        }
        assertTrue(REFERRAL_STATUS_CODES.contains(status.code()),
                itemInfo + ": subscriptionStatus.code должен быть одним из " +
                        REFERRAL_STATUS_CODES);
    }

    private void validateReferralLink(ReferralItem item, String itemInfo) {
        ReferralLink link = item.link();

        if (link == null) {
            assertFalse(item.hasSubscription(),
                    itemInfo + ": link == null допустим только для реферала без подписки");
            return;
        }

        if (link.id() == null) {
            assertNull(link.url(), itemInfo + ": при link.id == null link.url должен    быть null");
            return;
        }

        assertTrue(link.id() > 0, itemInfo + ": link.id должен быть положительным");
        assertTrue(link.hasUrl(), itemInfo + ": link.url должен быть URL");
        assertTrue(link.hasName() && !link.isPlaceholderName(),
                itemInfo + ": link.name должен быть непустым и не равным строке \"null\"");
    }

    private void validateSubscriptionDate(ReferralItem item, String itemInfo) {
        if (!item.hasSubscription()) {
            assertNull(item.getSubscriptionDateAsLocalDateTime(),
                    itemInfo + ": у реферала без подписки subscriptionDate должен быть null");
            return;
        }
        assertNotNull(item.getSubscriptionDateAsLocalDateTime(),
                itemInfo + ": subscriptionDate должен быть в формате yyyy-MM-dd HH:mm:ss");
    }

    private void assertJsonNotEmpty(String value, String message) {
        assertNotNull(value, message + " не должен быть null");
        assertFalse(value.isBlank(), message + " не должен быть пустым");
    }

    @Step("Валидация summary против items (только при pages == 1)")
    public void validateReferralsSummaryMatchesItems(ClubReferralsResponse referrals) {
        Summary summary = referrals.summary();

        assertAll("Сверка summary с items (pages == 1)",
                () -> assertEquals(summary.byStatusCode("premium"), referrals.countByStatusCode("premium"),
                        "summary.premium() должен совпадать с числом items с кодом premium"),
                () -> assertEquals(summary.byStatusCode("lifetime"), referrals.countByStatusCode("lifetime"),
                        "summary.lifetime() должен совпадать с числом items с кодом lifetime"),
                () -> assertEquals(summary.byStatusCode("lite"), referrals.countByStatusCode("lite"),
                        "summary.lite() должен совпадать с числом items с кодом lite"),
                () -> assertEquals(summary.byStatusCode("trial"), referrals.countByStatusCode("trial"),
                        "summary.trial должен совпадать с числом items с кодом trial"));
    }

    @Step("Валидация сортировки по incomePerMonth по убыванию")
    public void validateReferralsSortedByIncomePerMonthDesc(List<ReferralItem> items) {
        for (int i = 1; i < items.size(); i++) {
            int previous = items.get(i - 1).incomePerMonth();
            int current = items.get(i).incomePerMonth();
            assertTrue(previous >= current, "incomePerMonth должен идти по убыванию: #" + i + " (" + previous + ") >= #" + (i + 1) + " (" + current + ")");
        }
    }

    @Step("GET /user/club/v2/referrals - полный контракт рефералов")
    public ClubReferralsResponse getAndValidateClubReferrals(String authToken, int expectedLimit, int expectedPage) {
        ClubReferralsResponse referrals = validateClubReferralsResponse(getClubReferrals(authToken));
        validateReferralsPagination(referrals, expectedLimit, expectedPage);
        validateReferralsSummaryTotals(referrals);
        return referrals;
    }

    @Step("GET /user/club/v2/referrals/{userId} - детальная информация реферала")
    public Response getClubReferralDetail(String authToken, String userId) {
        Response response = clubApi.getClubReferralDetail(authToken, userId);
        AllureAttachments.attachJson("Club Referral Detail Response", response.getBody().asString());
        return response;
    }

    @Step("Валидация структуры referrals/{userId}")
    public void validateReferralDetail(Response response) {
        assertBasicResponse(response);
        assertAll("referrals/{userId} - ключи",
                () -> assertJsonKeyExists(response, "userId"),
                () -> assertJsonKeyExists(response, "name"),
                () -> assertJsonKeyExists(response, "avatarUrl"),
                () -> assertJsonKeyExists(response, "level"),
                () -> assertJsonKeyExists(response, "invitedBy"),
                () -> assertJsonKeyExists(response, "subscriptionStatus"),
                () -> assertJsonKeyExists(response, "incomePerMonth"),
                () -> assertJsonKeyExists(response, "incomeOneTime"),
                () -> assertJsonKeyExists(response, "link"),
                () -> assertJsonKeyExists(response, "activity"));

        assertAll("subscriptionStatus - наличие ключей", () -> {
            Map<String, Object> status = response.jsonPath().get("subscriptionStatus");
            assertNotNull(status, "subscriptionStatus не должен быть null");
            for (String key : List.of("code", "label", "isActive", "stateLabel")) {
                assertTrue(status.containsKey(key), "subscriptionStatus." + key + " должен присутствовать");
            }
        });
        assertAll("activity - наличие ключей", () -> {
            Map<String, Object> activity = response.jsonPath().get("activity");
            assertNotNull(activity, "activity не должен быть null");
            for (String key : List.of("lastLoginAt", "registeredAt", "addedCoursesCount", "completedLessonsCount")) {
                assertTrue(activity.containsKey(key), "activity." + key + " должен присутствовать");
            }
        });
    }

    @Step("GET /user/club/v2/referrals/{userId}/referrals - дерево рефералов")
    public Response getClubReferralReferrals(String authToken, String userId) {
        Response response = clubApi.getClubReferralReferrals(authToken, userId);
        AllureAttachments.attachJson("Club Referral Tree Response", response.getBody().asString());
        return response;
    }

    @Step("Валидация структуры referrals/{userId}/referrals")
    public void validateReferralReferrals(Response response) {
        assertBasicResponse(response);
        assertAll("referrals/{userId}/referrals - ключи",
                () -> assertJsonKeyExists(response, "items"),
                () -> assertJsonKeyExists(response, "pagination"),
                () -> assertJsonFieldExists(response, "pagination.count"),
                () -> assertJsonFieldExists(response, "pagination.total"),
                () -> assertJsonFieldExists(response, "pagination.per_page"),
                () -> assertJsonFieldExists(response, "pagination.page"),
                () -> assertJsonFieldExists(response, "pagination.pages"));
    }
}
