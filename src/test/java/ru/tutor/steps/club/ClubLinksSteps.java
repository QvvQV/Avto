package ru.tutor.steps.club;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.model.club.*;
import ru.tutor.utils.AllureAttachments;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ClubLinksSteps extends ClubBaseSteps {

    public ClubLinksSteps(String baseUrl) {
        super(baseUrl);
    }

    @Step("POST /user/club/v2/links - создание реферальной ссылки")
    public Response createClubLink(String authToken, int productId, String name) {
        Map<String, Object> payload = Map.of("productId", productId, "name", name);
        Response response = clubApi.createClubLink(authToken, payload);
        AllureAttachments.attachJson("Club Link Create Request", payload.toString());
        AllureAttachments.attachJson("Club Link Create Response", response.getBody().asString());
        AllureAttachments.attachJson("Club Link Create Headers", response.getHeaders().toString());
        return response;
    }

    @Step("Валидация ответа создания реферальной ссылки")
    public ClubLinkResponse validateClubLinkResponse(Response response) {
        assertStatusAnyOf(response, 201);

        ClubLinkResponse link = response.as(ClubLinkResponse.class);
        assertNotNull(link, "Реферальная ссылка не должна быть null");
        assertTrue(link.id() != null && link.id() > 0, "id должен быть положительным");
        assertTrue(link.code() != null && !link.code().isBlank(), "code не должен быть пустым");
        assertTrue(link.hasUrl(), "url не должен быть пустым");
        assertNotNull(link.product(), "product не должен быть null");
        assertTrue(link.product().id() != null && link.product().id() > 0,
                "product.id должен быть положительным");

        return link;
    }

    @Step("POST /user/club/share-reward - начисление награды за шеринг")
    public Response shareReward(String authToken) {
        Response response = clubApi.shareReward(authToken);
        AllureAttachments.attachJson("Club Share Reward Response", response.getBody().asString());
        AllureAttachments.attachJson("Club Share Reward Headers", response.getHeaders().toString());
        return response;
    }

    @Step("Валидация ответа начисления награды за шеринг")
    public ShareRewardResponse validateShareRewardResponse(Response response) {
        assertSuccessStatusCode(response);
        assertJsonFieldExists(response, "balance");
        assertJsonFieldExists(response, "granted");

        ShareRewardResponse reward = response.as(ShareRewardResponse.class);
        assertNotNull(reward, "Награда за шеринг не должна быть null");
        assertEquals(true, reward.granted(), "granted должен быть true");
        assertEquals(1000, reward.balance(), "balance должен быть 1000");
        return reward;
    }

    @Step("Начисление награды за шеринг и проверка (POST /user/club/share-reward)")
    public ShareRewardResponse shareRewardAndValidate(String authToken) {
        return validateShareRewardResponse(shareReward(authToken));
    }

    @Step("Создание реферальной ссылки и проверка (POST /user/club/v2/links)")
    public ClubLinkResponse createAndValidateClubLink(String authToken, int productId, String name) {
        return validateClubLinkResponse(createClubLink(authToken, productId, name));
    }

    @Step("GET /user/club/v2/links -список реферальных ссылок")
    public Response getClubLinks(String authToken) {
        Response response = clubApi.getClubLinks(authToken);
        AllureAttachments.attachJson("Club Links Response", response.getBody().asString());
        return response;
    }

    @Step("GET /user/club/v2/links - список реферальных ссылок с query {queryParams}")
    public Response getClubLinks(String authToken, Map<String, Object> queryParams) {
        Response response = clubApi.getClubLinks(authToken, queryParams);
        AllureAttachments.attachJson("Club Links Response", response.getBody().asString());
        return response;
    }

    @Step("Валдиация контракта ответа ссылок (200, json, CORS, items/pagination)")
    public ClubLinksResponse validateClubLinksResponse(Response response) {
        assertBasicResponse(response);
        assertJsonKeyExists(response, "items");
        assertJsonKeyExists(response, "pagination");
        ClubLinksResponse links = response.as(ClubLinksResponse.class);
        assertNotNull(links, "Ответ со списком ссылок не должен быть null");
        assertNotNull(links.items(), "items не долежн быть null");
        assertNotNull(links.pagination(), "pagination не долежн быть null");
        return links;
    }

    @Step("Валдиация пагинации ссылок (limit={expectedLimit}, page={expectedPage})")
    public void validateClubLinksPagination(ClubLinksResponse links, int expectedLimit, int expectedPage) {
        ClubReferralsResponse.Pagination pagination = links.pagination();
        assertAll("Проверка пагинации",
                () -> assertEquals(expectedLimit, pagination.perPage(),
                        "per_page должен быть " + expectedLimit),
                () -> assertEquals(expectedPage, pagination.page(),
                        "page должен быть " + expectedPage),
                () -> assertEquals(links.safeItems().size(), pagination.count(),
                        "count должен совпадать с размером items"),
                () -> assertTrue(pagination.count() <= pagination.perPage(),
                        "count (" + pagination.count() + ") не должен превышать per_page (" + pagination.perPage() + ")"),
                () -> assertTrue(pagination.total() >= pagination.count(),
                        "total (" + pagination.total() + ") должен быть >= count (" + pagination.count() + ")"),
                () -> assertEquals(pagination.expectedPages(), pagination.pages(),
                        "pages должен соответствовать total / per_page"));
    }

    @Step("Валидация структуры каждого элемента items (ссылки)")
    public void validateClubLinkItems(List<ClubLinkResponse> items) {
        for (int i = 0; i < items.size(); i++) {
            ClubLinkResponse link = items.get(i);
            String itemInfo = "Ссылка #" + (i + 1) + " (id=" + link.id() + ")";

            assertAll(itemInfo + " - структура",
                    () -> assertNotNull(link.id(), itemInfo + ": id не должен быть null"),
                    () -> assertTrue(link.id() != null && link.id() > 0,
                            itemInfo + ": id должен быть положительным"),
                    () -> assertNotNull(link.code(), itemInfo + ": code не должен быть null"),
                    () -> assertFalse(link.code() == null || link.code().isBlank(),
                            itemInfo + ": code не должен быть пустым"),
                    () -> assertTrue(link.hasUrl(), itemInfo + ": url не должен быть пустым"),
                    () -> assertNotNull(link.name(), itemInfo + ": name не должен быть null"),
                    () -> assertFalse(link.name() == null || link.name().isBlank(),
                            itemInfo + ": name не должен быть пустым"),
                    () -> assertNotNull(link.createdAt(), itemInfo + ": createdAt не должен быть null"),
                    () -> assertNotNull(link.clicks(), itemInfo + ": clicks не должен быть null"),
                    () -> assertTrue(link.clicks() != null && link.clicks() >= 0,
                            itemInfo + ": clicks должен быть >= 0"),
                    () -> assertNotNull(link.product(), itemInfo + ": product не должен быть null"),
                    () -> assertTrue(link.product() != null && link.product().id() != null
                                    && link.product().id() > 0,
                            itemInfo + ": product.id должен быть положительным")
            );
        }
    }

    @Step("GET /user/club/v2/links - полный котракт ссылок")
    public ClubLinksResponse getAndValidateClubLinksResponse(String authToken, int expectedLimit, int expectedPage) {
        ClubLinksResponse links = validateClubLinksResponse(getClubLinks(authToken));
        validateClubLinksPagination(links, expectedLimit, expectedPage);
        return links;
    }
}