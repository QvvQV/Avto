package ru.tutor.flow;

import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.api.PaymentApi;
import ru.tutor.config.TestConfig;
import ru.tutor.constants.AddressPage;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.page.orders.SberPage;
import ru.tutor.page.orders.TtPage;
import ru.tutor.steps.OrderSteps;
import ru.tutor.steps.PaymentSteps;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WaitHelper;

import java.time.Duration;

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

    public PaymentMethodFlow(TtPage ttPage, CloudPage cloudPage, PaymentApi paymentApi, PaymentSteps paymentSteps, SberPage sberPage) {
        this.ttPage = ttPage;
        this.cloudPage = cloudPage;
        this.paymentApi = paymentApi;
        this.paymentSteps = paymentSteps;
        this.sberPage = sberPage;
    }

    private void primeAvailablePaymentSystem() {
        Response response = step("GET /available-payment-systems?aff_rid=" + TestConfig.getAffRid(),
                () -> paymentApi.getAvailablePaymentSystems()
        );
        paymentSteps.assertPaymentSystems(response, "buttons.name");
        paymentSteps.assertModalVersion(response);
    }

    private void openPremiumMethodButtons() {
        step("Открытие главной страницы", () -> {
                if (TestConfig.isProd()) {
                    open(AddressPage.URL_MAIN_REV_PROD);
        } else {
            open(AddressPage.URL_MAIN_REV);
        }
        });
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
        if (TestConfig.isProd()) {
                cloudPage.clickTink(false);
                step("Проверка появления Cloud iframe", () -> {
                    assertTrue(cloudPage.isCloudPaymentFrameVisible(),
                            "Cloud iframe не появился после клика по Тинькофф");
                    AllureAttachments.attachScreenshot("Prod: Cloud iframe после клика по Тинькофф");
                });
                return;
            }
            cloudPage.clickTink(true);
            OrderSteps.validateSuccessfulPayment(cloudPage);
        }

    @Step("Other: кнопка -> Cloud iframe/Sber редирект (dev: оплата, prod: фиксация)")
    public void otherPayment() {
        primeAvailablePaymentSystem();
        openPremiumMethodButtons();
        if (TestConfig.isProd()) {

            cloudPage.clickOther(false);
            step("Проверка появления Cloud iframe или Sber редиректа", () -> {
                boolean cloud = cloudPage.isCloudPaymentFrameVisible();
                String currentUrl = com.codeborne.selenide.WebDriverRunner.url();
                boolean sber = currentUrl.contains("sber")
                        || currentUrl.contains("secure")
                        || currentUrl.contains("pay");
                assertTrue(cloud || sber,
                        "После кнопки Other не открылся ни Cloud-iframe, ни Sber-редирект. URL: " + currentUrl);
                if (sber) AllureAttachments.attachScreenshot("Prod: Sber-редирект после Other");
                else AllureAttachments.attachScreenshot("Prod: Cloud iframe после Other");
            });
            return;
        }
        cloudPage.clickOther(true);
        OrderSteps.validateSuccessfulPayment(cloudPage);
    }

    @Step("Sber: кнопка -> Sber редирект (dev: оплата, prod: фиксация)")
    public void sberCardPayment() {
        primeAvailablePaymentSystem();
        openPremiumMethodButtons();
        if (TestConfig.isProd()) {
            sberPage.clickSber(false);
            step("Фиксация Sber редиректа", () -> {
                String currentUrl = com.codeborne.selenide.WebDriverRunner.url();
                assertTrue(currentUrl.contains("sber")
                        || currentUrl.contains("secure")
                        || currentUrl.contains("pay"),
                        "После кнопки Sber не открылся Sber-редирект. URL: " + currentUrl);
                AllureAttachments.attachScreenshot("Prod: Sber-редирект после Other");
                AllureAttachments.attachText("Sber URL:",  currentUrl);
            });
            return;
        }
        sberPage.clickSber(true);
        sleep(13000);
        WaitHelper.waitFor(() -> WebDriverRunner.url().contains("success"),
                Duration.ofSeconds(15), "Ожидание страницы успеха после оплаты Sber");
        OrderSteps.validateSuccessfulPayment(cloudPage);
    }
}