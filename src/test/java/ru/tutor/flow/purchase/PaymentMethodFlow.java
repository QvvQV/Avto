package ru.tutor.flow.purchase;

import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.openqa.selenium.WebDriver;
import ru.tutor.api.orders.PaymentApi;
import ru.tutor.config.TestConfig;
import ru.tutor.constants.AddressPage;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.page.orders.SberPage;
import ru.tutor.page.orders.TtPage;
import ru.tutor.steps.orders.OrderSteps;
import ru.tutor.steps.orders.PaymentSteps;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WaitHelper;

import java.time.Duration;
import java.util.Objects;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.sleep;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PaymentMethodFlow {
    private final TtPage ttPage;
    private final CloudPage cloudPage;
    private final PaymentApi paymentApi;
    private final PaymentSteps paymentSteps;
    private final SberPage sberPage;
    private final PaymentMode mode;

    public PaymentMethodFlow(TtPage ttPage, CloudPage cloudPage, PaymentApi paymentApi,
                             PaymentSteps paymentSteps, SberPage sberPage) {
        this(ttPage, cloudPage, paymentApi, paymentSteps, sberPage,
                TestConfig.isProd() ? PaymentMode.FIXATE : PaymentMode.PAY);
    }

    public PaymentMethodFlow(TtPage ttPage, CloudPage cloudPage, PaymentApi paymentApi,
                             PaymentSteps paymentSteps, SberPage sberPage, PaymentMode mode) {
        this.ttPage = ttPage;
        this.cloudPage = cloudPage;
        this.paymentApi = paymentApi;
        this.paymentSteps = paymentSteps;
        this.sberPage = sberPage;
        this.mode = mode;
    }

    private void primeAvailablePaymentSystem() {
        Response response = step("GET /available-payment-systems?aff_rid=" + TestConfig.getAffRid(),
                paymentApi::getAvailablePaymentSystems);
        paymentSteps.assertPaymentSystems(response, "buttons.name");
        paymentSteps.assertModalVersion(response);
    }

    private void openPremiumMethodButtons(){
        step("Открытие главной страницы", () ->
                open(mode == PaymentMode.FIXATE ? AddressPage.URL_MAIN_REV_PROD :
                        AddressPage.URL_MAIN_REV));
        ttPage.selectSubscription(TtPage.SubscriptionType.PREMIUM);
        step("Ввод email и клик 'Оплатить' -> появление кнопок способов", () -> {
            cloudPage.setDateModal();
            cloudPage.assertPaymentMethodButtonsVisible();
        });
    }

    @Step("Тинькофф: кнопка -> Cloud iframe (dev: оплата, prod: фиксация)")
    public void tinkPayment() {
        primeAvailablePaymentSystem();
        openPremiumMethodButtons();
        cloudPage.clickTink(mode);
        if (mode != PaymentMode.PAY) {
            step("Проверка появление Cloud iframe", () -> {
                assertTrue(cloudPage.isCloudPaymentFrameVisible(),
                        "Cloud iframe не появился после клика по Тинькофф");
                AllureAttachments.attachScreenshot("Prod: Cloud iframe после клика по Тинькофф");
            });
            return;
        }
        OrderSteps.validateSuccessfulPayment(cloudPage);
    }

    @Step("Other: кнопка -> Cloud iframe/Sber редирект (dev: оплата, prod: фиксация)")
    public void otherPayment() {
        primeAvailablePaymentSystem();
        openPremiumMethodButtons();
        cloudPage.clickOther(mode);
        if (mode == PaymentMode.PAY) {
            OrderSteps.validateSuccessfulPayment(cloudPage);
            return;
        }
        step("Проверка появления Cloud iframe или Sber редиректа", this::assertPaymentRedirect);
    }

    @Step("Sber: кнопка -> Sber редирект (dev: оплата,prod: фиксация)")
    public void sberCardPayment() {
        primeAvailablePaymentSystem();
        openPremiumMethodButtons();
        sberPage.clickSber(mode);
        if (mode == PaymentMode.PAY) {
            WaitHelper.waitFor(() -> Objects.requireNonNull(WebDriverRunner.url()).contains("success"),
                    Duration.ofSeconds(30), "Ожидание страницы успеха после оплаты Sber");
            OrderSteps.validateSuccessfulPayment(cloudPage);
            return;
        }
        step("Фиксация Sber редиректа", this::assertPaymentRedirect);
    }

    private void assertPaymentRedirect() {
        String currentUrl = WebDriverRunner.url();
        assertTrue(currentUrl != null
        && (currentUrl.contains("sber") || currentUrl.contains("secure")
        || currentUrl.contains("pay")),
                "После клика не открылся ни Cloud-iframe, ни Sber-редирект. URL: " + currentUrl);
        AllureAttachments.attachScreenshot("Prod: Sber-редирект");
        AllureAttachments.attachText("Sber URL:", String.valueOf(currentUrl));
    }

}