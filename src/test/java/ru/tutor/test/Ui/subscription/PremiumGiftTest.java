package ru.tutor.test.Ui.subscription;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.tutor.base.RecurrentPaymentTestBase;
import ru.tutor.config.TestConfig;
import ru.tutor.flow.tariff.ReactivatePremiumFlow;
import ru.tutor.steps.OrderPremiumGiftSteps;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Оформление подписки по тарифу Подарок
@Epic("API Тесты пользователя")
@Feature("Премиум подарки")
@DisplayName("Тесты эндпоинта /user/orders/get-premium-gift")
public class PremiumGiftTest extends RecurrentPaymentTestBase {

    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private ReactivatePremiumFlow reactivateFlow;
    private OrderPremiumGiftSteps orderPremiumGift = new OrderPremiumGiftSteps(BASE_URL);


    @Override
    protected void initializePaymentCycleExecutor() {
        super.initializePaymentCycleExecutor();

        this.reactivateFlow = new ReactivatePremiumFlow(
                ttPage, cloudPage, sberPage, adminOrdersPage, adminOrderDetails,
                orderDatesPage, paymentCycleExecutor, context
        );
    }

    @Test
    @Tag("UI" + "API")
    @DisplayName("✅ Успешное получение премиум подарка")
    void restoreYourPremiumAccountUsingAnInternalEmail() {
        reactivateFlow
                .createUserAndRecurrent(1)
                .goToUserPersonalAccount()
                .downgradeToPremiumGift();
    }

    @Test
    @Tag("UI" + "API")
    @DisplayName("❌ Получение премиум подарка без авторизации")
    void getPremiumGift_WithoutAuth_ShouldFail(){
        reactivateFlow
                .createUserAndRecurrent(1)
                .goToUserPersonalAccount();
        Response response = step ("Попытка получения подарка без токена", ()-> orderPremiumGift.getPremiumGift("")
        );

        step("Проверка ошибки авторизации", ()-> {
            int statusCode = response.getStatusCode();
            assertTrue(statusCode == 401, "Должен вернуть 401. Получен: " + statusCode);
        });
    }

    @Test
    @Tag("UI" + "API")
    @DisplayName("❌ Получение премиум подарка с невалидным токеном")
    void getPremiumGift_WithInvalidToken_ShouldFail(){
        reactivateFlow
                .createUserAndRecurrent(1)
                .goToUserPersonalAccount();
        Response response = step ("Попытка получения подарка без токена", ()->
                orderPremiumGift.getPremiumGift("invalid-token")
        );

        step("Проверка ошибки авторизации", ()-> {
            int statusCode = response.getStatusCode();
            assertTrue(statusCode == 401, "Должен вернуть 401. Получен: " + statusCode);
        });
    }

    @Test
    @DisplayName("🔄 Повторное получение премиум подарка")
    @Tag("negative")
    @Tag("functional")
    void getPremiumGift_Twice_ShouldFail() {
        reactivateFlow
                .createUserAndRecurrent(1)
                .goToUserPersonalAccount()
                .downgradeToPremiumGift();
    }
}
