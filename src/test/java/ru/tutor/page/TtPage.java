package ru.tutor.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.tutor.utils.universalMethod;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static io.qameta.allure.Allure.step;

/*
 * Локаторы для кнопок на главной странице Премиум/Лайт/Пожизненный доступ
 */

public class TtPage {

    // Локаторы кнопок
    private final SelenideElement btnOpenFor = $("[data-btn-scroll='subscribe']");

    // Карточки подписок
    private final SelenideElement lite99 = $("[data-subscription-type='litenew']");
    private final SelenideElement life = $("[data-subscription-type='lifetime']");
    private final SelenideElement premium = $("[data-subscription-type='premium']");

    // Модальное окно
    private final SelenideElement textModal = $(".buy-modal_tarrif__qfojr");
    private final SelenideElement closeModal = $(".buy-modal_close__kHp6e");

    // Enum для типов подписок
    public enum SubscriptionType {
        LITE("litenew", "Лайт 99", "Оформить доступ Лайт 99Р/Мес", "ПОПРОБУЙ ПЕРВУЮ НЕДЕЛЮ ТАРИФА “ЛАЙТ”"),
        LIFE("lifetime", "Пожизненный доступ", "Оформить доступ Пожизненный 3999Р/Мес", "ОФОРМИ ПОЖИЗНЕННЫЙ ДОСТУП К БОЛЕЕ ЧЕМ К 400 КУРСАМ"),
        PREMIUM("premium", "Премиум", "Оформить доступ Премиум 399/Мес", "ПОПРОБУЙ ПЕРВУЮ НЕДЕЛЮ ТАРИФА “ПРЕМИУМ”");

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

        public String getDataAttribute() {
            return dataAttribute;
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
        universalMethod.clickButton(btnOpenFor, "Открыть за 1Р");
        return this;
    }

    @Step("Выбор типа подписки: {type.displayName}")
    public TtPage selectSubscription(SubscriptionType type) {
        step(type.getStepDescription(), () ->
                universalMethod.safeClick(type.getElement(this), type.getDisplayName())
        );
        return this;
    }

    @Step("Получение текста из модального окна")
    public String getTextModal() {
        return textModal.shouldBe(visible).getText().trim();
    }

    @Step("Закрытие модального окна")
    public TtPage closeModal() {
        universalMethod.clickButton(closeModal, "Закрытие модального окна");
        return this;
    }
}
