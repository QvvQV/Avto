package ru.tutor.test.Ui.orders;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import ru.tutor.base.BaseUiTest;
import ru.tutor.config.TestConfig;
import ru.tutor.flow.admin.AdminFlow;
import ru.tutor.flow.purchase.PurchaseFlow;
import ru.tutor.flow.purchase.PurchaseResult;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.page.orders.SberPage;
import ru.tutor.page.admin.AdminOrdersPage;
import ru.tutor.page.orders.TtPage;
import ru.tutor.steps.AdminSteps;

import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Epic("Платежи через Сбер")
@Feature("Тестирование различных сценариев оплаты")
@DisplayName("🧪 Тестирование платежной системы Сбер")
public class SberErrorTest extends BaseUiTest {

    @AfterEach
    public void quitDriver() {
        closeWebDriver();
        SelenideLogger.removeListener("AllureSelenide");
    }

    private final AdminOrdersPage adminOrdersPage = new AdminOrdersPage();
    private final AdminSteps adminSteps = new AdminSteps(TestConfig.getCurrentApiUrl());
    private final TtPage ttPage = new TtPage();
    private final CloudPage cloudPage = new CloudPage();
    private final SberPage sberPage = new SberPage();

    private final PurchaseFlow purchaseFlow = new PurchaseFlow(ttPage, cloudPage, sberPage);
    private final AdminFlow adminFlow = new AdminFlow(adminOrdersPage);

    @Test
    @AllureId("8")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("⚠️ Оплата картой с фиксированным балансом 100р")
    @Tag("negative/positive")
    void payWithFixBalanceCard() {

        PurchaseResult result = purchaseFlow.premiumFixSber();
        assertNotNull(result.email());
        adminSteps.assertOrderExists(result.email());

    }

    @Test
    @AllureId("6")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("❌ Оплата картой с недостаточными средствами")
    @Tag("negative")
    void payWithInsufficientFundsCard() {

        PurchaseResult result = purchaseFlow.premiumInsufficientFundsSber();
        assertNotNull(result.email());
        adminSteps.assertOrderExists(result.email());

    }

    @Test
    @AllureId("5")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("❌ Оплата просроченной картой")
    @Tag("negative")
    void payWithExpireCard() {

        PurchaseResult result = purchaseFlow.premiumExpiredSber();
        assertNotNull(result.email());
        adminSteps.assertOrderExists(result.email());
    }
}