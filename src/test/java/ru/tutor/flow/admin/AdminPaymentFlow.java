package ru.tutor.flow.admin;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import ru.tutor.flow.purchase.PurchaseFlow;
import ru.tutor.flow.purchase.PurchaseResult;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.steps.admin.AdminSteps;
import ru.tutor.steps.orders.OrderSteps;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AdminPaymentFlow {
    private final PurchaseFlow purchaseFlow;
    private final CloudPage cloudPage;
    private final AdminSteps adminSteps;

    public AdminPaymentFlow(PurchaseFlow purchaseFlow, CloudPage cloudPage, AdminSteps adminSteps) {
        this.purchaseFlow = purchaseFlow;
        this.cloudPage = cloudPage;
        this.adminSteps = adminSteps;
    }

    private static final int PREMIUM_PAY = 399;
    private static final int LITE_PAY = 99;
    private static final int DAYS = 30;

    private String createOrderAndGetOrderId(Allure.ThrowableRunnable<PurchaseResult> purchase, String stepName) {
        PurchaseResult result = step(stepName, purchase);
        String email = result.email();
        assertNotNull(email, "email не должен быть null");
        OrderSteps.validateSuccessfulPayment(cloudPage);
        return adminSteps.assertOrderExists(email);
    }

    @Step("Оплата заказа Premium в админке и проверка дат")
    public void payPremiumViaAdminAndCheckDates() {
        step("Создание заказа Премиум", () -> {
            String orderId = createOrderAndGetOrderId(purchaseFlow::premium, "Создание заказа Премиум");
            adminSteps.createPaymentAndValidateDates(orderId, PREMIUM_PAY, DAYS);
        });
    }

    @Step("Оплата заказа Lite (child Premium) в админке и проверка дат")
    public void payLiteViaAdminAndCheckDates() {
        step("Создание заказа Премиум (содержит дочерний Лайт)", () -> {
            String orderId = createOrderAndGetOrderId(purchaseFlow::lite, "Создание заказа Лайт");
            adminSteps.createPaymentAndValidateDates(orderId, LITE_PAY, DAYS);
        });
    }

    @Step("Отмена подписки заказа через админку и проверка флагов деактивации")
    public void unsubscribeOrderAndCheckDeactivation() {
        String orderId = createOrderAndGetOrderId(purchaseFlow::premium, "Создание заказа Премиум");
        adminSteps.unsubscribeOrder(orderId);
        adminSteps.assertOrderDeactivated(orderId);
    }

    @Step("Оплата Premium в админке + проверка дат + Refund")
    public void payPremiumViaAdminAndRefund() {
        String orderId = createOrderAndGetOrderId(purchaseFlow::premium, "Создание заказа Премиум");
        adminSteps.createPaymentAndValidateDates(orderId, PREMIUM_PAY, DAYS);

        Integer paymentId = adminSteps.findRefundablePaymentId(orderId);
        adminSteps.refundPayment(paymentId);
        adminSteps.assertRefundApplied(orderId, PREMIUM_PAY);
    }
}