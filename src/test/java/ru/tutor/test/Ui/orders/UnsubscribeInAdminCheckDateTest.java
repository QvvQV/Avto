package ru.tutor.test.Ui.orders;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.tutor.base.RecurrentPaymentTestBase;
import ru.tutor.flow.AdminFlow;
import ru.tutor.flow.PurchaseFlow;
import ru.tutor.flow.PurchaseResult;
import ru.tutor.test.executor.RecurrentPaymentTestExecutor;

public class UnsubscribeInAdminCheckDateTest extends RecurrentPaymentTestBase {

    private RecurrentPaymentTestExecutor testExecutor;
    private PurchaseFlow purchaseFlow;
    private AdminFlow adminFlow;

    @Override
    protected void initializePaymentCycleExecutor() {
        super.initializePaymentCycleExecutor();
        this.purchaseFlow = new PurchaseFlow(ttPage, cloudPage, sberPage);
        this.adminFlow = new AdminFlow(adminOrdersPage);
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
        PurchaseResult result = purchaseFlow.premium();
        adminFlow.processRecurrent(result.email());
        testExecutor.executePaymentTest("Cloud Premium", result.email(), context, 2);
    }

    @Test
    @DisplayName("Комплексная проверка дат заказа после серии рекуррентных платежей Cloud Lite")
    void testComplexOrderAfterMultiplePaymentLiteCloud() {
        PurchaseResult result = purchaseFlow.lite();
        adminFlow.processRecurrentLite(result.email());
        testExecutor.executePaymentTest("Cloud Lite", result.email(), context, 2);
    }

    // Тесты перепишутся под иной функционал
//    @Test
//    @DisplayName("Комплексная проверка дат заказа после серии рекуррентных платежей Sber Premium")
//    void testComplexOrderDateAfterMultiplePaymentSber() {
//        PurchaseResult result = purchaseFlow.premiumSber();
//        adminFlow.processRecurrent(result.email());
//        testExecutor.executePaymentTest("Sber Premium", result.email(), context, 2);
//    }

//    @Test
//    @DisplayName("Комлпексная проверка дат заказа после серии рекуррентных платежей Sber Lite")
//    void testComplexOrderAfterMultiplePaymentSberLite() {
//        PurchaseResult result = purchaseFlow.liteSber();
//        adminFlow.processRecurrentLite(result.email());
//        testExecutor.executePaymentTest("Sber Lite", result.email(), context, 2);
//    }
}

// 1) оформить билл с африд ссылкой
// 2) провести билл в админке
// 3) перейти в ЛК через админку
// 4) отписать до Лайта
// 5) обновить url с африд ссылкой
// 6) активировать через письмо внутри ЛК
// 7) провести билл по Лайту