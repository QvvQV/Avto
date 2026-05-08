package ru.tutor.test.api.user;

import io.qameta.allure.Allure;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.tutor.base.RecurrentPaymentTestBase;
import ru.tutor.config.TestConfig;
import ru.tutor.constants.PromoCodeTestData;
import ru.tutor.flow.ReactivatePremiumFlow;
import ru.tutor.flow.SubscriptionTestFlow;
import ru.tutor.model.ApplyPromoCodeRequest;
import ru.tutor.model.ApplyPromoCodeResponse;
import ru.tutor.model.UserProfile;
import ru.tutor.steps.UserSteps;
import ru.tutor.steps.fortune.GameWheelRewardsSteps;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PromoCodeTest extends RecurrentPaymentTestBase {
    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private ReactivatePremiumFlow reactivateFlow;
    private final SubscriptionTestFlow flow = new SubscriptionTestFlow(ttPage, cloudPage, sberPage, adminOrdersPage);

    private final UserSteps userSteps = new UserSteps(BASE_URL);

    private static final String VALID_PROMO_CODE = "новый";
    private static final String INACTIVE_PROMO_CODE = "333CDRS444GKETSHDL";
    private static final String NON_EXISTENT_PROMO_CODE = "CDRSGKETSHDL";


    @Override
    protected void initializePaymentCycleExecutor() {
        super.initializePaymentCycleExecutor();

        this.reactivateFlow = new ReactivatePremiumFlow(
                ttPage, cloudPage, sberPage, adminOrdersPage, adminOrderDetails,
                orderDatesPage, paymentCycleExecutor, context
        );
    }

    @BeforeEach
    void setup() {
        reactivateFlow
                .createUserAndRecurrent()
                .goToUserPersonalAccount();
    }

    @Test
    @Tag("UI" + "API")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("✅ Применение промокода к созданному заказу")
    void applyValidPromoCode_Success() {
        String orderNumber = adminOrdersPage.getLastOrderNumber();
        ApplyPromoCodeResponse response = userSteps.applyPromoCodeAndValidate(VALID_PROMO_CODE, orderNumber);
        assertTrue(PromoCodeTestData.isSuccessResponse(response.getMessage()));
    }

    @Test
    @Tag("UI" + "API")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("❌ Применение уже использованного промокода")
    void applyUsedPromoCode_ShouldFail() {
        String orderNumber = adminOrdersPage.getLastOrderNumber();
        userSteps.applyPromoCodeAndValidate(VALID_PROMO_CODE, orderNumber);
        ApplyPromoCodeResponse response = step("Повторное применение промокода", () ->
                userSteps.applyPromoCodeAndExpectError(VALID_PROMO_CODE, orderNumber,
                        PromoCodeTestData.ERROR_ALREADY_USED)
        );
        assertTrue(response.getError().contains("Этот промокод уже использовался пользователем")
        );
    }

    @Test
    @Tag("UI" + "API")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("❌ Применение неактивного промокода")
    void applyInactivePromoCode_ShouldFail() {
        String orderNumber = adminOrdersPage.getLastOrderNumber();
        ApplyPromoCodeResponse response = step("Применение неактивного промокода", () ->
                userSteps.applyPromoCodeAndExpectError(INACTIVE_PROMO_CODE, orderNumber, PromoCodeTestData.ERROR_INACTIVE)
        );
        assertTrue(response.getError().contains("Промокод неактивный или неверно настроен")
        );
    }

    @Test
    @Tag("UI" + "API")
    @DisplayName("❌ Применение несуществующего промокода")
    @Severity(SeverityLevel.NORMAL)
    void applyNonExistentPromoCode_ShouldFail() {
        String orderNumber = adminOrdersPage.getLastOrderNumber();
        ApplyPromoCodeResponse response = step("Применение несуществующего промокода", () ->
                userSteps.applyPromoCodeAndExpectError(NON_EXISTENT_PROMO_CODE, orderNumber, PromoCodeTestData.ERROR_NOT_FOUND)
        );
        assertTrue(response.getError().contains("PromoCode is not found by name"));
    }

    @Test
    @Tag("UI" + "API")
    @DisplayName("❌ Применение промокода без авторизации")
    @Severity(SeverityLevel.CRITICAL)
    void applyPromoCode_WithoutAuth_ShouldFail() {
        String orderNumber = adminOrdersPage.getLastOrderNumber();
        ApplyPromoCodeRequest request = ApplyPromoCodeRequest.of(VALID_PROMO_CODE, orderNumber);

        Response response = step("Попытка применения промокода без токена", () ->
                userSteps.applyPromoCode("", request)
        );
        step("Проверка ошибки авторизации", () -> {
        int statusCode = response.getStatusCode();
        assertTrue(statusCode == 401, "Должен вернуть 401. Получен: " + statusCode);
        });
    }

    @Test
    @Tag("UI" + "API")
    @DisplayName("❌ Применение промокода с невалидным токеном")
    @Severity(SeverityLevel.NORMAL)
    void applyPromoCode_WithInvalidToken_ShouldFail() {
        String orderNumber = adminOrdersPage.getLastOrderNumber();
        ApplyPromoCodeRequest request = ApplyPromoCodeRequest.of(VALID_PROMO_CODE, orderNumber);

        Response response = step("Попытка применения промокода без токена", () ->
                userSteps.applyPromoCode("invalid_token_123", request)
        );
        step("Проверка ошибки авторизации", () -> {
            int statusCode = response.getStatusCode();
            assertTrue(statusCode == 401, "Должен вернуть 401. Получен: " + statusCode);
        });
    }

    @Test
    @Tag("UI" + "API")
    @DisplayName("❌ Применение промокода с пустым orderNumber")
    @Severity(SeverityLevel.MINOR)
    void applyPromoCode_WithEmptyOrderNumber_ShouldFail() {
                ApplyPromoCodeResponse response = step("Применение промокода с пустым orderNumber", () ->
                userSteps.applyPromoCodeAndExpectError(VALID_PROMO_CODE, "",
                        PromoCodeTestData.ERROR_EMPTY_PROMO_CODE_ORDER_NUMBER)
        );
        step("Проверка ошибки с пустым orderNumber", () ->
                assertTrue(response.getError().contains("Invalid input. [orderNumber] - Значение не должно быть пустым. [orderNumber] - Значение слишком короткое. Должно быть равно 8 символам или больше. ")
        ));
    }

    @Test
    @Tag("UI" + "API")
    @DisplayName("❌ Применение промокода с пустым promoCodeName")
    @Severity(SeverityLevel.MINOR)
    void applyPromoCode_WithEmptyPromoCodeName_ShouldFail() {
        String orderNumber = adminOrdersPage.getLastOrderNumber();
        ApplyPromoCodeResponse response = step("Применение промокода с пустым promoCodeName", () ->
                userSteps.applyPromoCodeAndExpectError("", orderNumber, PromoCodeTestData.getErrorType("Invalid input. [promoCodeName] - Значение не должно быть пустым. "))
    );
        step("Проверка ошибки с пустым promoCodeName", () ->
                assertTrue(response.getError().contains("Invalid input. [promoCodeName] - Значение не должно быть пустым. ")
                ));
    }
}

