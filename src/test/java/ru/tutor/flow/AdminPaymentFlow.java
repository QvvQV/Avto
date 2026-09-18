package ru.tutor.flow;

import io.qameta.allure.Step;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.steps.AdminSteps;
import ru.tutor.steps.OrderSteps;

import static com.codeborne.selenide.Selenide.sleep;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AdminPaymentFlow {
    private final PurchaseFlow purchaseFlow;
    private final CloudPage cloudPage;
    private final AdminSteps  adminSteps;
    private String userEmail;

    public AdminPaymentFlow(PurchaseFlow purchaseFlow, CloudPage cloudPage, AdminSteps adminSteps) {
        this.purchaseFlow = purchaseFlow;
        this.cloudPage = cloudPage;
        this.adminSteps = adminSteps;
    }

    @Step("Оплата заказа Premium в админке и проверка дат")
    public void payPremiumViaAdminAndCheckDates() {
        step("Создание заказа Премиум", () -> {
            PurchaseResult result = purchaseFlow.premium();
            userEmail = result.email();
            assertNotNull(userEmail, "email не должен быть null");
        });
        OrderSteps.validateSuccessfulPayment(cloudPage);
        String orderId = adminSteps.assertOrderExists(userEmail);
        adminSteps.createPaymentAndValidateDates(orderId, 399, 30);
    }

    @Step("Оплата заказа Lite (child Premium) в админке и проверка дат")
    public void payLiteViaAdminAndCheckDates() {
        step("Создание заказа Премиум (содержит дочерний Лайт)", () -> {
            PurchaseResult result = purchaseFlow.lite();
            userEmail = result.email();
            assertNotNull(userEmail, "email не должен быть null");
        });
        OrderSteps.validateSuccessfulPayment(cloudPage);
        String parentOrderId = adminSteps.assertOrderExists(userEmail);
        String liteOrderId = adminSteps.findChildOrderId(parentOrderId);
        adminSteps.createPaymentAndValidateDates(liteOrderId, 99, 30);
    }

    @Step("Отмена подписки заказа через админку и проверка флагов деактивации")
    public void unsubscribeOrderAndCheckDeactivation() {
        step("Создание заказа Premium", () -> {
            PurchaseResult result = purchaseFlow.premium();
            userEmail = result.email();
            assertNotNull(userEmail, "email не должен быть null");
        });
        OrderSteps.validateSuccessfulPayment(cloudPage);
        String orderId = adminSteps.assertOrderExists(userEmail);
        adminSteps.unsubscribeOrder(orderId);
        adminSteps.assertOrderDeactivated(orderId);
    }

    @Step("Оплата Premium в админке + проверка дат + Refund")
    public void payPremiumViaAdminAndRefund(){
        step("Создание заказа Премиум", () -> {
            PurchaseResult result = purchaseFlow.premium();
            userEmail = result.email();
            assertNotNull(userEmail, "email не должен быть null");
        });
        OrderSteps.validateSuccessfulPayment(cloudPage);
        String orderId = adminSteps.assertOrderExists(userEmail);
        adminSteps.createPaymentAndValidateDates(orderId, 399, 30);

        Integer paymentId = adminSteps.findRefundablePaymentId(orderId);
        adminSteps.refundPayment(paymentId);
        adminSteps.assertRefundApplied(orderId, 399);
    }
}
