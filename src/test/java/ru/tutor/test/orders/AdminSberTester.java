package ru.tutor.test.orders;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.tutor.base.BaseUiTest;
import ru.tutor.config.TestConfig;
import ru.tutor.page.SberPage;
import ru.tutor.page.addressPage;
import ru.tutor.page.ttPage;
import ru.tutor.utils.AllureAttachments;

import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("Платежи через Сбер")
@Feature("Тестирование различных сценариев оплаты")
@DisplayName("🧪 Тестирование платежной системы Сбер")
public class AdminSberTester extends BaseUiTest {

    @AfterEach
    public void quitDriver() {
        closeWebDriver();
        SelenideLogger.removeListener("AllureSelenide");
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("✅ Успешная оплата Пожизненного доступа с главной страницы")
    @Tag("positive")
    void payMainLifetimeAccess(){
        PaymentTestFlow testFlow = new PaymentTestFlow();

        testFlow.openMainPage()
                .verifyMainPage()
                .selectLifetimeAccess()
                .processModalAndPay()
                .payWithApprovedCard()
                .verifySuccess();
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("⚠️ Оплата картой с фиксированным балансом 100р")
    @Tag("negative/positive")
    void payWithFixBalanceCard() {
        PaymentTestFlow testFlow = new PaymentTestFlow();

        testFlow.openMainPage()
                .verifyMainPage()
                .selectPremiumTariff()
                .processModalAndPay()
                .payWithFixedBalanceCard()
                .verifySuccess()
                .captureScreenshot();
    }

    @Test
//    @RepeatedTest(3)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("✅ Успешная оплата Премиум тарифа с главной страницы")
    @Tag("positive")
    void payMainPremiumTariff() {
        PaymentTestFlow testFlow = new PaymentTestFlow();

        testFlow.openMainPage()
                .verifyMainPage()
                .selectPremiumTariff()
                .processModalAndPay()
                .payWithApprovedCard()
                .verifySuccess();
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("✅ Успешная оплата Лайт тарифа с главной страницы")
    @Tag("positive")
    void payMainLiteTariff() {
        PaymentTestFlow testFlow = new PaymentTestFlow();

        testFlow.openMainPage()
                .verifyMainPage()
                .selectLiteTariff()
                .processModalAndPay()
                .payWithApprovedCard()
                .verifySuccess();
    }


    private class PaymentTestFlow {
        public PaymentTestFlow openProductPage() {
            step("Открытие страницы продукта Excel", () -> {
                open(addressPage.urlExcel);
            });
            return this;
        }

        public PaymentTestFlow openMainPage() {
            step("Открытие главной страницы", () -> {
                open(addressPage.urlMain);
            });
            return this;
        }

        public PaymentTestFlow verifyExcelProductPage() {
            step("Проверка заголовка страницы Excel", () -> {
                assertEquals(TestConfig.Texts.EXCEL_PAGE_TITLE, title());
                AllureAttachments.attachText("Проверка страницы", "Страница Excel загружена корректно");
            });
            return this;
        }

        public PaymentTestFlow verifyMainPage() {
            step("Проверка заголовка главной страницы", () -> {
                assertEquals(TestConfig.Texts.MAIN_PAGE_TITLE, title());
                AllureAttachments.attachText("Проверка страницы", "Главная страница загружена корректно");
            });
            return this;
        }

        public PaymentTestFlow startOrder() {
            SberPage.startOrderFromProduct();
            return this;
        }

        public PaymentTestFlow selectLiteTariff() {
            ttPage.clickBtnOpenFor();
            ttPage.clickLite99();
            return this;
        }

        public PaymentTestFlow selectPremiumTariff() {
            ttPage.clickBtnOpenFor();
            ttPage.clickPremium();
            return this;
        }

        public PaymentTestFlow selectLifetimeAccess() {
            ttPage.clickBtnOpenFor();
            ttPage.clickLife();
            return this;
        }

        public PaymentTestFlow processModalAndPay() {
            SberPage.fillEmailAndProcessModal();
            SberPage.switchToNewWindow();
            return this;
        }

        public PaymentTestFlow payWithApprovedCard() {
            SberPage.payWithApprovedCard();
            return this;
        }

        public PaymentTestFlow payWithFixedBalanceCard() {
            SberPage.payWithFixedBalanceCard();
            return this;
        }

        public PaymentTestFlow payWithInsufficientFundsCard() {
            SberPage.payWithInsufficientFundsCard();
            return this;
        }

        public PaymentTestFlow payWithExpiredCard() {
            SberPage.payWithExpiredCard();
            return this;
        }

        public PaymentTestFlow verifySuccess() {
            step("Проверка успешной оплаты", () -> {
                AllureAttachments.attachText("Результат", "Оплата прошла успешно");
            });
            return this;
        }

        public PaymentTestFlow verifyDeclinedPayment() {
            step("Проверка отклоненного платежа", () -> {
                AllureAttachments.attachText("Результат", "Платеж отклонен как и ожидалось");
            });
            return this;
        }

        public PaymentTestFlow virifyErrorMessage(String expectedError){
            step("Проверка сообщения об ошибке", ()->{
                String actualError = SberPage.getErrorText();
                assertEquals(expectedError, actualError);
                AllureAttachments.attachText("Проверка ошибки",
                    "Ожидалось: " + expectedError + "\nФактически: " + actualError);
            });
            return this;
        }

        public PaymentTestFlow captureScreenshot() {
            AllureAttachments.attachScreenshot("Скриншот после теста");
            return this;
        }
    }



    //https://dev.tutorplace.ru/catalog/product/excel?aff_rid=1012_EN5U - CPA
    //https://dev.tutorplace.ru/catalog/product/excel?aff_rid=1012_EN5S - Гибрид
//    https://dev.tutorplace.ru/catalog/product/excel?aff_rid=1012_EN5Q - RevShare
}

//https://dev.tutorplace.ru/catalog/product/excel?aff_rid=1012_EPqz