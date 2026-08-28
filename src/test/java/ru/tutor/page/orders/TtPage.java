package ru.tutor.page.orders;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.tutor.utils.UniversalMethod;
import ru.tutor.utils.WaitHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;

/*
 * Локаторы для кнопок на главной странице Премиум/Лайт/Пожизненный доступ
 */

public class TtPage {

    // Локаторы кнопок
//    private final SelenideElement btnOpenFor = $("[data-btn-scroll='subscribe']");
    private final SelenideElement btnOpenFor = $$("h2").find(exactText("Выберите оптимальный тариф"));
    private final SelenideElement main = $x("//h2[contains(text(),'Выберите оптимальный тариф')]");

    // Карточки подписок
    private final SelenideElement lite99 = $("[data-subscription-type='litenew']");
    private final SelenideElement life = $("[data-subscription-type='lifetime']");
    private final SelenideElement premium = $("[data-tariff='premium'] [data-subscription-type='premium']");

    // Модальное окно
//    private final SelenideElement textModal = $(".buy-modal_tarrif__qfojr");
    private final SelenideElement textModal = $(".buy-modal-v2-shell_tariffTag__Px_HF");
    private final SelenideElement closeModal = $(".buy-modal-v2-shell_closeBtn__j0Cn0");

    // Enum для типов подписок
    public enum SubscriptionType {
        LITE("litenew", "Лайт 99", "Оформить доступ Лайт 99Р/Мес", "Тариф «Лайт»"),
        LIFE("lifetime", "Пожизненный доступ", "Оформить доступ Пожизненный 3999Р/Мес", "Тариф «Навсегда»"),
        PREMIUM("premium", "Премиум", "Оформить доступ Премиум 399/Мес", "Тариф «Премиум»");

        private final String dataAttribute;
        private final String displayName;
        private final String stepDescription;
        private final String expectedModalText;

        SubscriptionType(String dataAttribute, String displayName, String stepDescription, String expectedModalText) {
            this.dataAttribute = dataAttribute;
            this.displayName = displayName;
            this.stepDescription = stepDescription;
            this.expectedModalText = expectedModalText;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getStepDescription() {
            return stepDescription;
        }

        public String getExpectedModalText() {
            return expectedModalText;
        }

        public SelenideElement getElement(TtPage page) {
            return switch (this) {
                case LITE ->
                     page.lite99;
                case LIFE ->
                     page.life;
                case PREMIUM ->
                     page.premium;
            };
        }
    }

    @Step("Кликаем по кнопке на главной за 1 руб")
    public TtPage clickBtnOpenFor() {

        WaitHelper.waitForPageLoad();
        main.scrollTo();
        System.out.println(main.getText().trim());
        btnOpenFor.scrollTo().shouldBe(visible, Duration.ofSeconds(20));
//        UniversalMethod.safeClick(premium, "Попробовать за 1₽*");
        return this;
    }

    @Step("Выбор типа подписки: {type.displayName}")
    public TtPage selectSubscription(SubscriptionType type) {
        step(type.getStepDescription(), () ->
                UniversalMethod.safeClick(type.getElement(this), type.getDisplayName())
        );
        return this;
    }

    @Step("Получение текста из модального окна")
    public String getTextModal() {
        return textModal.shouldBe(visible).getText().trim();
    }

    @Step("Закрытие модального окна")
    public TtPage closeModal() {
        UniversalMethod.clickButton(closeModal, "Закрытие модального окна");
        return this;
    }
}
