package ru.tutor.test.Ui.orders;

import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.*;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.junit.jupiter.api.*;
import ru.tutor.api.PaymentApi;
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
import ru.tutor.steps.PaymentSteps;
import ru.tutor.utils.AllureAttachments;

import java.util.List;

import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@Epic("Платежи через Cloud/валидной картой Sber")
@Feature("Тестирование различных сценариев оплаты")
@DisplayName("🧪 Тестирование платежной системы Cloud/валидной картой Sber")
public class CloudTest extends BaseUiTest {

    private final AdminOrdersPage adminOrdersPage = new AdminOrdersPage();
    private final TtPage ttPage = new TtPage();
    private final CloudPage cloudPage = new CloudPage();
    private final SberPage sberPage = new SberPage();

    private final OrderSteps orderSteps = new OrderSteps(
            TestConfig.getApiProdUrl()
    );

    private final PurchaseFlow purchaseFlow = new PurchaseFlow(ttPage, cloudPage, sberPage);
    private final AdminFlow adminFlow = new AdminFlow(adminOrdersPage);
    private final RevshareFlow revshareFlow = new RevshareFlow(cloudPage, orderSteps);

    // ====API====
    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private final PaymentApi paymentApi = new PaymentApi(BASE_URL);
    private final PaymentSteps paymentSteps = new PaymentSteps(BASE_URL);


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
    @DisplayName("Оплата по тестовому домену")
    public void PayFromRevShare() {
        assumeTrue(TestConfig.isProd(),
                "Тест запускается только на прод-окружении (env.ui=prod)");
        revshareFlow.executeProd();
    }


    // ====API====
    @Test
    @Order(1)
    @DisplayName("Успешное получение доступных платёжных API систем на главной")
    @Severity(SeverityLevel.NORMAL)
    @Tag("positive")
    @Tag("smoke")
    void getAvailablePaymentSystemsSuccess() {
        Response response = step("GET /available-payment-systems?aff_rid=" + TestConfig.getAffRid(),
                () -> paymentApi.getAvailablePaymentSystems()
        );
        paymentSteps.assertPaymentSystems(response, "buttons.name");
        paymentSteps.assertModalVersion(response);
    }

    @Test
    @Order(2)
    @DisplayName("Успешное получение доступных платёжных API систем по ручке domain")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("smoke")
    @Tag("positive")
    void getDomainCardioSystemSuccess(){
        assumeTrue(TestConfig.isProd(),
                "Тест запускается только на прод-окружении (env.ui=prod)");

        Response response = step("GET /domain?aff_rid=" + TestConfig.getAffRid(),
                () -> paymentApi.getDomain()
        );
        paymentSteps.assertPaymentSystems(response, "paymentButtons.name");
        paymentSteps.assertModalVersion(response);
    }
}