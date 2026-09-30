package ru.tutor.test.Ui.orders.admin;

import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.tutor.base.BaseUiTest;
import ru.tutor.config.TestConfig;
import ru.tutor.flow.admin.AdminFlow;
import ru.tutor.flow.admin.AdminPaymentFlow;
import ru.tutor.flow.purchase.PurchaseFlow;
import ru.tutor.page.admin.AdminOrdersPage;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.page.orders.SberPage;
import ru.tutor.page.orders.TtPage;
import ru.tutor.steps.admin.AdminSteps;

import static org.junit.jupiter.api.Assumptions.assumeTrue;
import ru.tutor.constants.Tags;

@Story("Работа с заказами через админку")
@Feature("Заказы в админке")
@Feature("Только dev-окружение")
@DisplayName("Заказы в админке")
public class AdminOrderUnsubscribeTest extends BaseUiTest {

    private final TtPage ttPage = new TtPage();
    private final CloudPage cloudPage = new CloudPage();
    private final SberPage sberPage = new SberPage();
    private final PurchaseFlow purchaseFlow = new PurchaseFlow(ttPage, cloudPage, sberPage);
    private final AdminSteps adminSteps = new AdminSteps(TestConfig.getCurrentApiUrl());
    private final AdminOrdersPage adminOrdersPage = new AdminOrdersPage();

    private final AdminFlow adminFlow = new AdminFlow(adminOrdersPage);
    private final AdminPaymentFlow adminPaymentFlow = new AdminPaymentFlow(purchaseFlow, cloudPage, adminSteps);

    @Test
    @Order(1)
    @Tag(Tags.E2E)
    @DisplayName("Оплата заказа Premium 399 через админку и проверка дат")
    public void PayPremiumViaAdminAndCheckDates() {
        assumeTrue(!TestConfig.isProd(),
                "Тест запускается только на dev-окружении (env.ui != prod)");
        adminPaymentFlow.payPremiumViaAdminAndCheckDates();
    }

    @Test
    @Order(2)
    @Tag(Tags.E2E)
    @DisplayName("Оплата заказа Lite 99 через админку и проверка дат")
    public void PayLiteViaAdminAndCheckDates() {
        assumeTrue(!TestConfig.isProd(),
                "Тест запускается только на dev-окружении (env.ui != prod)");
        adminPaymentFlow.payLiteViaAdminAndCheckDates();
    }

    @Test
    @Order(3)
    @Tag(Tags.E2E)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Отмена подписки заказа через админку и проверка флагов деактивации")
    void unsubscribeOrderInAdminAndCheckDeactivationFlag() {
        assumeTrue(!TestConfig.isProd(),
                "Тест запускается только на dev-окружении (env.ui != prod)");
        adminPaymentFlow.unsubscribeOrderAndCheckDeactivation();
    }

    @Test
    @Order(4)
    @Tag(Tags.E2E)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Оплата Premium через админку, проверка дат и Refund")
    void PayPremiumViaAdminAndRefund() {
        assumeTrue(!TestConfig.isProd(),
                "Тест запускается только на dev-окружении (env.ui != prod)");
        adminPaymentFlow.payPremiumViaAdminAndRefund();
    }
}