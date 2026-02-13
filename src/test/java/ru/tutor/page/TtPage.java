package ru.tutor.page;

import com.codeborne.selenide.SelenideElement;
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
    private final SelenideElement lite99 = $("[data-subscription-type='litenew']");
    private final SelenideElement life = $("[data-subscription-type='lifetime']");
    private final SelenideElement premium = $("[data-subscription-type='premium']");
    private final SelenideElement textModal = $(".buy-modal_tarrif__qfojr");
    private final SelenideElement closeModal = $(".buy-modal_close__kHp6e");

    public void clickBtnOpenFor() {
        step("Кликаем по кнопке на главной за 1 руб", () ->
            universalMethod.clickButton(btnOpenFor, "Открыть за 1Р")
        );
    }

    public void clickLite99() {
        step("Кликаем по кнопке Лайт 99", () ->
            universalMethod.safeClick(lite99, "Офрмить доступ Лайт 99Р/Мес")
    );
    }

    public void clickLife() {
        step("Кликаем по кнопке Пожизненный доступ", () ->
            universalMethod.safeClick(life, "Оформить доступ Пожизненный 3999Р/Мес")
    );
    }

    public void clickPremium() {
        step("Кликаем по кнопке Премиум", () ->
            universalMethod.safeClick(premium, "Оформить доступ Премиум 399/Мес")
    );
    }

    public String setTextFromModal() {
        return textModal.shouldBe(visible).getText().trim();
    }

    public void clickBtnClose() {
        step("Закрываем мод окно", () ->
            universalMethod.clickButton(closeModal, "Закрытие модального окна")
    );
    }

}
