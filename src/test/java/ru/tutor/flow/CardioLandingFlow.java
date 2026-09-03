package ru.tutor.flow;

import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.api.PaymentApi;
import ru.tutor.config.TestConfig;
import ru.tutor.page.CardioLandingPage;
import ru.tutor.steps.PaymentSteps;
import ru.tutor.utils.AllureAttachments;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CardioLandingFlow {

    private final CardioLandingPage cardioLandingPage;
    private final PaymentApi paymentApi;
    private final PaymentSteps paymentSteps;

    public CardioLandingFlow(CardioLandingPage cardioLandingPage,
                             PaymentApi paymentApi, PaymentSteps paymentSteps) {
        this.cardioLandingPage = cardioLandingPage;
        this.paymentApi = paymentApi;
        this.paymentSteps = paymentSteps;
    }

    private void primeCardioDomain() {
        Response response = step("GET /domain?aff_rid=" + TestConfig.getAffRid(),
                () -> paymentApi.getDomain()
        );
        paymentSteps.assertPaymentSystems(response, "paymentButtons.name");
        paymentSteps.assertModalVersion(response);
    }

    @Step("Тинькофф (кардио-лендинг): кнопка -> Cloud iframe")
    public void tinkPayment() {
        primeCardioDomain();
        cardioLandingPage.openPremiumMethodButtonsCardio();
        cardioLandingPage.clickTink();
        step("Проверка появления Cloud iframe", () -> {
            assertTrue(cardioLandingPage.isCloudPaymentFrameVisible(),
                    "Cloud iframe не появился после клика по Тинькофф");
            AllureAttachments.attachScreenshot("Cardio: Cloud iframe после Тинькофф");
        });
    }

    @Step("Other (кардио-лендинг): кнопка -> Cloud iframe/Sber редирект")
    public void otherPayment() {
        primeCardioDomain();
        cardioLandingPage.openPremiumMethodButtonsCardio();
        cardioLandingPage.clickOther();
        step("Проверка Cloud iframe или Sber редиректа", () -> {
            boolean cloud = cardioLandingPage.isCloudPaymentFrameVisible();
            String url = WebDriverRunner.url();
            boolean sber = url.contains("sber") || url.contains("secure") || url.contains("pay");
            assertTrue(cloud || sber,
                    "После Other не открылся ни Cloud-iframe, ни Sber-редирект. URL: " + url);
            AllureAttachments.attachScreenshot(cloud ? "Cardio: Cloud iframe после Other"
                    : "Cardio: Sber-редирект после Other");
        });
    }

    @Step("Сбер (кардио-лендинг): кнопка -> Sber редирект")
    public void sberCardPayment() {
        primeCardioDomain();
        cardioLandingPage.openPremiumMethodButtonsCardio();
        cardioLandingPage.clickSber();
        step("Фиксация Sber редиректа", () -> {
            String url = WebDriverRunner.url();
            assertTrue(url.contains("sber") || url.contains("secure") || url.contains("pay"),
                    "После кнопки Сбер не открылся Sber-редирект. URL: " + url);
            AllureAttachments.attachScreenshot("Cardio: Sber редирект");
            AllureAttachments.attachText("Sber URL (cardio):", url);
        });
    }
}
