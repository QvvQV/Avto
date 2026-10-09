package ru.tutor.steps.account;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.api.account.UserApi;
import ru.tutor.base.BaseApiSteps;
import ru.tutor.model.account.UserProfile;
import ru.tutor.utils.AllureAttachments;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ProfileSteps extends BaseApiSteps {

    private final UserApi userApi;

    public ProfileSteps(String baseUrl) {
        super(baseUrl);
        this.userApi = new UserApi(baseUrl);
    }

    @Step("GET /user/profile - получить профиль пользователя")
    public Response getProfile(String authToken) {
        Response response = userApi.getProfile(authToken);
        AllureAttachments.attachJson("Profile Response", response.getBody().asString());
        AllureAttachments.attachJson("Profile Headers", response.getHeaders().toString());
        return response;
    }

    @Step("Проверить ответ профиля")
    public UserProfile validateProfileResponse(Response response, String expectedEmail) {
        assertBasicResponse(response);

        Map<String, Object> root = response.jsonPath().getMap("$");
        assertNotNull(root, "Тело должно быть JSON-объектом");

        String email = response.jsonPath().getString("email");
        assertEquals(expectedEmail, email, "Email должен соответствовать ожидаемому значению");

        assertAll("Поля профиля",
                //Числовой
                positiveInt(root, "id"),
                nonNegative(root, "diamondBalance"),
                numberField(root, "daysSincePasswordUpdate"),
                nonNegative(root, "deactivatedQuiz"),
                nonNegative(root, "recurrencyCashBackAmount"),
                nonNegative(root, "gender"),
                nonNegative(root, "isClubParticipatorAndNotified"),
                //Строковые
                nonEmptyString(root, "name"),
                nonEmptyString(root, "email"),
                nonEmptyString(root, "phone"),
                nonEmptyString(root, "orderLanding"),
                nonEmptyString(root, "telegram"),
                nonEmptyString(root, "emailAdditional"),
                nonEmptyString(root, "about"),
                nonEmptyString(root, "clubMembership"),
                nonEmptyString(root, "registrationPlatform"),
                //Булевые
                notNull(root, "premium"),
                notNull(root, "hasLite"),
                notNull(root, "lite"),
                notNull(root, "policyAgreement"),
                notNull(root, "isLightweight"),
                notNull(root, "isFirstAuth"),
                notNull(root, "isTest"),
                notNull(root, "isTariffFreeRegistration"),
                notNull(root, "hasRebill"),
                notNull(root, "isEnabledGameWheel"),
                notNull(root, "isTariffPresent"),
                notNull(root, "hasReceivedPremiumGift"),
                notNull(root, "isYtSubscribed"),
                notNull(root, "isVkSubscribed"),
                notNull(root, "isTgSubscribed"),
                notNull(root, "isMaxSubscribed"),
                notNull(root, "isYaReviewed"),
                notNull(root, "coinsToDiamondsConversion"),
                notNull(root, "hasLoginToday"),
                notNull(root, "isTariffLifetime"),
                notNull(root, "subscriptionNotBePayed"),
                notNull(root, "paymentGatewayVerify"),
                notNull(root, "isAuthor"),
                //Коллекции
                containsKey(root, "days"),
                containsKey(root, "categories"),
                containsKey(root, "letters"),
                containsKey(root, "trackers"),
                containsKey(root, "notificationTimeFrom"),
                containsKey(root, "notificationTimeTo"),
                containsKey(root, "pausedTill"),
                containsKey(root, "fullAccessPremium"),
                containsKey(root, "fullAccessLite"),
                containsKey(root, "diamondBalanceAddition"),
                containsKey(root, "latestPremiumOrderNumber")
        );

        UserProfile userProfile = response.as(UserProfile.class);
        assertNotNull(userProfile, "UserProfile не должен быть null");

        return userProfile;
    }
}