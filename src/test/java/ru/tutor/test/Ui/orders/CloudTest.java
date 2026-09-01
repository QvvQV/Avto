package ru.tutor.test.Ui.orders;

import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import ru.tutor.base.BaseUiTest;
import ru.tutor.config.TestConfig;
import ru.tutor.flow.AdminFlow;
import ru.tutor.flow.PurchaseFlow;
import ru.tutor.flow.PurchaseResult;
import ru.tutor.flow.RevshareFlow;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.page.orders.SberPage;
import ru.tutor.constants.AddressPage;
import ru.tutor.page.admin.AdminOrdersPage;
import ru.tutor.page.orders.TtPage;
import ru.tutor.steps.OrderSteps;
import ru.tutor.utils.AllureAttachments;

import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@Epic("Платежи через Cloud/валидной картой Sber")
@Feature("Тестирование различных сценариев оплаты")
@DisplayName("🧪 Тестирование платежной системы Cloud/валидной картой Sber")
public class CloudTest extends BaseUiTest {

    private final AdminOrdersPage adminOrdersPage = new AdminOrdersPage();
    private final TtPage ttPage = new TtPage();
    private final CloudPage cloudPage = new CloudPage();
    private final SberPage sberPage = new SberPage();
    private final Faker faker = new Faker();

    private final OrderSteps orderSteps = new OrderSteps(
            TestConfig.getApiProdUrl()
    );

    private final PurchaseFlow purchaseFlow = new PurchaseFlow(ttPage, cloudPage, sberPage);
    private final AdminFlow adminFlow = new AdminFlow(adminOrdersPage);
    private final RevshareFlow revshareFlow = new RevshareFlow(cloudPage, orderSteps);


    //    @Test
//            @RepeatedTest(2)
    @DisplayName("Оплата с главной на СисАк Oфферум")
    public void PayFromMainOfferrum() {

        PurchaseResult result = purchaseFlow.premiumAffRid();
        assertNotNull(result.email());
        adminFlow.assertOrderExists(result.email());
    }

    @Test
//    @RepeatedTest(10)
    @DisplayName("Оплата с главной за 99 руб")
    public void PayMain99() {
        if (TestConfig.isProd()) {
            purchaseFlow.verifyModalOpens(TtPage.SubscriptionType.LITE);
        } else {
            PurchaseResult result = purchaseFlow.lite();
            assertNotNull(result.email());
            adminFlow.assertOrderExists(result.email());
        }
    }

    @Test
//        @RepeatedTest(3)
    @DisplayName("Оплата с главной за 399 руб")
    public void PayMain399() {
        if (TestConfig.isProd()) {
            purchaseFlow.verifyModalOpens(TtPage.SubscriptionType.PREMIUM);
        } else {

            PurchaseResult result = purchaseFlow.premium();
            assertNotNull(result.email());
            adminFlow.assertOrderExists(result.email());
        }
    }

    @Test
    @DisplayName("Оплата с главной за Пожизненный")
    public void PayMainLife() {
        if (TestConfig.isProd()) {
            purchaseFlow.verifyModalOpens(TtPage.SubscriptionType.LIFE);
        } else {

            PurchaseResult result = purchaseFlow.life();
            assertNotNull(result.email());
            adminFlow.assertOrderExists(result.email());
        }
    }

    @Test
    @DisplayName("Оплата Долями с главной за Пожизненный Cloud")
    public void PayMainLifeInstallments() {
        if (TestConfig.isProd()) {
            purchaseFlow.verifyInstallmentOpens(TtPage.SubscriptionType.LIFE);
        } else {

            PurchaseResult result = purchaseFlow.lifeInstallments();
            assertNotNull(result.email());
            adminFlow.assertOrderExists(result.email());
        }
    }

    @Test
    @DisplayName("Оплата 'Плати частями' через Sber")
    public void PayInstallmentsPartsSber() {
        if (TestConfig.isProd()) {
           purchaseFlow.verifyPartPaymentOpens(TtPage.SubscriptionType.LIFE);
        } else {
            PurchaseResult result = purchaseFlow.partPayment();
            assertNotNull(result.email());
            adminFlow.assertOrderExists(result.email());
        }
    }

    @Test
//            @RepeatedTest(5)
    @DisplayName("Оплата по ссылке revShare PROD")
    public void PayFromRevShare() {
        assumeTrue(TestConfig.isProd(),
                "Тест запускается только на прод-окружении (env.ui=prod)");
        revshareFlow.executeProd();
    }
}