package ru.tutor.test.Ui.orders;

import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import ru.tutor.base.BaseUiTest;
import ru.tutor.config.TestConfig;
import ru.tutor.flow.AdminFlow;
import ru.tutor.flow.AdminPaymentFlow;
import ru.tutor.flow.PurchaseFlow;
import ru.tutor.flow.PurchaseResult;
import ru.tutor.page.admin.AdminOrdersPage;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.page.orders.SberPage;
import ru.tutor.page.orders.TtPage;
import ru.tutor.steps.AdminSteps;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@Story("Работа с заказами через админку")
@Feature("Админ-API")
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
    @DisplayName("Оплата заказа Premium 399 через админку и проверка дат")
    public void PayPremiumViaAdminAndCheckDates() {
        assumeTrue(!TestConfig.isProd(),
                "Тест запускается только на dev-окружении (env.ui != prod)");
        adminPaymentFlow.payPremiumViaAdminAndCheckDates();
    }

    @Test
    @Order(2)
    @DisplayName("Оплата заказа Lite 99 через админку и проверка дат")
    public void PayLiteViaAdminAndCheckDates() {
        assumeTrue(!TestConfig.isProd(),
                "Тест запускается только на dev-окружении (env.ui != prod)");
        adminPaymentFlow.payLiteViaAdminAndCheckDates();
    }

    @Test
    @Order(3)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Отмена подписки заказа через админку и проверка флагов деактивации")
    void unsubscribeOrderInAdminAndCheckDeactivationFlag() {
        assumeTrue(!TestConfig.isProd(),
                "Тест запускается только на dev-окружении (env.ui != prod)");
        adminPaymentFlow.unsubscribeOrderAndCheckDeactivation();
    }
}