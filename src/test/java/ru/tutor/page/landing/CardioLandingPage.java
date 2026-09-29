package ru.tutor.page.landing;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import net.datafaker.Faker;
import ru.tutor.constants.AddressPage;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.FrameHelper;
import ru.tutor.utils.UniversalMethod;
import ru.tutor.utils.WaitHelper;

import java.time.Duration;
import java.util.Locale;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;

public class CardioLandingPage {

    private final Faker faker = new Faker(Locale.ENGLISH);
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    private final SelenideElement CTA_BUTTON = $("a.hero__cta-btn[href='#order']");
    private final SelenideElement EMAIL_FIELD = $("#form__email-field");

    private final SelenideElement SUBMIT_BTN   = $("#mainFormaSubmitBtn");

    private final SelenideElement SBER_BTN   = $("button.modal-trust__button--sb");
    private final SelenideElement TINK_BTN   = $("button.modal-trust__button--tb");
    private final SelenideElement OTHER_BTN   = $("button.modal-trust__button--card");
    private final SelenideElement SBERPAY_BTN   = $("button.modal-trust__button--sbpay");

    @Step("Клик по CTA «Попробовать» -> скролл к email-форме")
    public void clickTryButton() {
        UniversalMethod.safeClick(CTA_BUTTON, "Попробовать");
        WaitHelper.waitForPageLoad();
    }

    public void openPremiumMethodButtonsCardio() {
        step("Открытие главной страницы (dev)", () ->
                open(AddressPage.CARDIO_DOMAIN_URL_AFF_RID));
        clickTryButton();
        step("Ввод email и клик 'Оплатить' -> появление кнопок способов", () -> {
            submitEmail();
            assertPaymentMethodButtonsVisible();
        });
    }

    @Step("Ввод email и клик #mainFormaSubmitBtn")
    public void submitEmail(){
        String email = faker.internet().emailAddress();
        step("Ввод email: " + email, () ->
                EMAIL_FIELD.shouldBe(visible, DEFAULT_TIMEOUT).setValue(email));
        UniversalMethod.safeClick(SUBMIT_BTN, "Получить доступ");
        AllureAttachments.attachText("Отправлен email (cardio)", email);
    }

    @Step("Проверка появления 4 кнопок способа оплаты")
    public void assertPaymentMethodButtonsVisible() {
        step("Ожидание всех 4 кнопок способа оплаты", () -> {
            SBERPAY_BTN.shouldBe(visible, DEFAULT_TIMEOUT);
            SBER_BTN.shouldBe(visible, DEFAULT_TIMEOUT);
            TINK_BTN.shouldBe(visible, DEFAULT_TIMEOUT);
            OTHER_BTN.shouldBe(visible, DEFAULT_TIMEOUT);
            AllureAttachments.attachScreenshot("4 кнопки способа оплаты (модалка)");
        });
    }

    @Step("Клик по кнопке Тинькофф")
    public void clickTink() {
        UniversalMethod.safeClick(TINK_BTN, "Кнопка «Тинькофф картой»");
        sleep(2000);
        AllureAttachments.attachScreenshot("Переход по кнопке «Тинькофф картой»");
        FrameHelper.switchToDefaultContent();
    }

    @Step("Клик по кнопке Тинькофф")
    public void clickOther() {
        UniversalMethod.safeClick(OTHER_BTN, "Кнопка «Other»");
        sleep(2000);
        AllureAttachments.attachScreenshot("Переход по кнопке «Other»");
        FrameHelper.switchToDefaultContent();
    }

    @Step("Клик по кнопке «Сбер»")
    public void clickSber() {
        UniversalMethod.safeClick(SBER_BTN, "Кнопка «Sber»");
        sleep(2000);
        AllureAttachments.attachScreenshot("Переход по кнопке «Sber»");
        FrameHelper.switchToDefaultContent();
    }

    @Step("Проверка появления Cloud iframe")
    public boolean isCloudPaymentFrameVisible() {
        FrameHelper.switchToDefaultContent();
        return $$("iframe").stream().anyMatch(frame -> {
            String src = frame.getAttribute("src");
            return src != null && src.contains("cloudpayments");
        });
    }

}
