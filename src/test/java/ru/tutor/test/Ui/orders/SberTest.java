package ru.tutor.test.Ui.orders;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import ru.tutor.base.BaseUiTest;
import ru.tutor.config.TestConfig;
import ru.tutor.flow.SubscriptionTestFlow;
import ru.tutor.page.CloudPage;
import ru.tutor.page.SberPage;
import ru.tutor.constants.AddressPage;
import ru.tutor.page.admin.AdminOrdersPage;
import ru.tutor.page.TtPage;
import ru.tutor.utils.AllureAttachments;

import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("Платежи через Сбер")
@Feature("Тестирование различных сценариев оплаты")
@DisplayName("🧪 Тестирование платежной системы Сбер")
public class SberTest extends BaseUiTest {

    @AfterEach
    public void quitDriver() {
        closeWebDriver();
        SelenideLogger.removeListener("AllureSelenide");
    }

    private final AdminOrdersPage adminOrdersPage = new AdminOrdersPage();
    private final TtPage ttPage = new TtPage();
    private final CloudPage cloudPage = new CloudPage();
    private final SberPage sberPage = new SberPage();

    private final SubscriptionTestFlow flow = new SubscriptionTestFlow(ttPage, cloudPage, sberPage, adminOrdersPage);

//    @Test
    @AllureId("7")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("✅ Успешная оплата валидной картой с продукта Excel")
    @Tag("positive")
    void payWithValidCardFromProduct() {
        PaymentTestFlow testFlow = new PaymentTestFlow();

        testFlow.openProductPage()
                .startOrder()
                .payWithApprovedCard()
                .verifySuccess();
    }

    @Test
    @AllureId("9")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("✅ Успешная оплата Пожизненного доступа с главной страницы")
    @Tag("positive")
    void payMainLifetimeAccess() {

        flow.purchaseLifeSubscriptionSber();

    }

    @Test
    @AllureId("8")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("⚠️ Оплата картой с фиксированным балансом 100р")
    @Tag("negative/positive")
    void payWithFixBalanceCard() {

        flow.purchasePremiumSubscriptionFixSber();

    }

    @Test
//    @RepeatedTest(3)
    @AllureId("10")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("✅ Успешная оплата Премиум тарифа с главной страницы")
    @Tag("positive")
    void payMainPremiumTariff() {

        flow.purchasePremiumSubscriptionSber();

    }

    @Test
    @AllureId("11")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("✅ Успешная оплата Лайт тарифа с главной страницы")
    @Tag("positive")
    void payMainLiteTariff() {

        flow.purchaseLiteSubscriptionSber();

    }

    @Test
    @AllureId("6")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("❌ Оплата картой с недостаточными средствами")
    @Tag("negative")
    void payWithInsufficientFundsCard() {

        flow.purchasePremiumSubscriptionInsufficientFundsSber();

    }

    @Test
    @AllureId("5")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("❌ Оплата просроченной картой")
    @Tag("negative")
    void payWithExpireCard() {

        flow.purchasePremiumSubscriptionExpireSber();

    }

    private class PaymentTestFlow {
        public PaymentTestFlow openProductPage() {
            step("Открытие страницы продукта Excel", () ->
                open(AddressPage.urlExcel)
            );
            return this;
        }


        public PaymentTestFlow verifyExcelProductPage() {
            step("Проверка заголовка страницы Excel", () -> {
                assertEquals(TestConfig.Texts.EXCEL_PAGE_TITLE, title());
                AllureAttachments.attachText("Проверка страницы", "Страница Excel загружена корректно");
            });
            return this;
        }

        public PaymentTestFlow startOrder() {
            sberPage.startOrderFromProduct();
            return this;
        }

        public PaymentTestFlow payWithApprovedCard() {
            sberPage.payWithApprovedCard();
            return this;
        }

        public void verifySuccess() {
            step("Проверка успешной оплаты", () ->
                AllureAttachments.attachText("Результат", "Оплата прошла успешно")
            );
        }

        public PaymentTestFlow verifyDeclinedPayment() {
            step("Проверка отклоненного платежа", () ->
                AllureAttachments.attachText("Результат", "Платеж отклонен как и ожидалось")
            );
            return this;
        }
    }


    //https://dev.tutorplace.ru/catalog/product/excel?aff_rid=1012_EN5U - CPA
    //https://dev.tutorplace.ru/catalog/product/excel?aff_rid=1012_EN5S - Гибрид
//    https://dev.tutorplace.ru/catalog/product/excel?aff_rid=1012_EN5Q - RevShare
}

//https://dev.tutorplace.ru/catalog/product/excel?aff_rid=1012_EPqz