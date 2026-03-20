package ru.tutor.test.Ui.orders;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.tutor.base.RecurrentPaymentTestBase;
import ru.tutor.flow.SubscriptionTestFlow;
import ru.tutor.test.executor.RecurrentPaymentTestExecutor;

public class UnsubscribeInAdminCheckDateTest extends RecurrentPaymentTestBase {

    private RecurrentPaymentTestExecutor testExecutor;

    @Override
    protected void initializePaymentCycleExecutor() {
        super.initializePaymentCycleExecutor();
        testExecutor = new RecurrentPaymentTestExecutor(
                orderDatesPage,
                adminOrderDetails,
                adminOrdersPage,
                paymentCycleExecutor
        );
    }

    @Test
    @DisplayName("Комплексная проверка дат заказа после серии рекуррентных платежей Cloud Premium")
    void testComplexOrderDateAfterMultiplePayments() {
        testExecutor.executePaymentTest(
                "Cloud Premium",
                () -> new SubscriptionTestFlow(ttPage, cloudPage, sberPage, adminOrdersPage).purchasePremiumSubscription(),
                SubscriptionTestFlow::processRecurrentInAdmin,
                context, 2
        );
    }

    @Test
    @DisplayName("Комплексная проверка дат заказа после серии рекуррентных платежей Cloud Lite")
    void testComplexOrderAfterMultiplePaymentLiteCloud() {
        testExecutor.executePaymentTest(
                "Cloud Lite",
                () -> new SubscriptionTestFlow(ttPage, cloudPage, sberPage, adminOrdersPage).purchaseLiteSubscription(), // Оплата Лайт подписки с главной страницы
                SubscriptionTestFlow::processRecurrentInAdminLite,
                context, 2
        );
    }

    @Test
    @DisplayName("Комплексная проверка дат заказа после серии рекуррентных платежей Sber Premium")
    void testComplexOrderDateAfterMultiplePaymentSber() {
        testExecutor.executePaymentTest(
                "Sber Premium",
                () -> new SubscriptionTestFlow(ttPage, cloudPage, sberPage, adminOrdersPage).purchasePremiumSubscriptionSber(),
                SubscriptionTestFlow::processRecurrentInAdmin,
                context,2
        );
    }

    @Test
    @DisplayName("Комлпексная проверка дат заказа после серии рекуррентных платежей Sber Lite")
    void testComplexOrderAfterMultiplePaymentSberLite() {
        testExecutor.executePaymentTest(
                "Sber Lite",
                () -> new SubscriptionTestFlow(ttPage, cloudPage, sberPage, adminOrdersPage).purchaseLiteSubscriptionSber(),
                SubscriptionTestFlow::processRecurrentInAdminLite,
                context,2
        );
    }
}

// 1) оформить билл с африд ссылкой
// 2) провести билл в админке
// 3) перейти в ЛК через админку
// 4) отписать до Лайта
// 5) обновить url с африд ссылкой
// 6) активировать через письмо внутри ЛК
// 7) провести билл по Лайту