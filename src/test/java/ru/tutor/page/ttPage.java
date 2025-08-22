package ru.tutor.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;
import static io.qameta.allure.Allure.step;

public class ttPage {
    private static SelenideElement btnOpenFor = $("[data-btn-scroll=\"subscribe\"]");
    private static SelenideElement lite99 = $("[data-subscription-type=\"litenew\"]");
    private static SelenideElement life = $("[data-subscription-type=\"lifetime\"]");
    private static SelenideElement premium = $("[data-subscription-type=\"premium\"]");
    private static SelenideElement textModal = $(".buy-modal_tarrif__qfojr");
    private static SelenideElement closeModal = $(".buy-modal_close__kHp6e");

    public static void clickBtnOpenFor() {
        step("Кликаем по кнопке на главной за 1 руб", () -> {
            btnOpenFor.shouldBe(visible).click();
        });
    }

    public static void clickLite99() {
        step("Кликаем по кнопке Лайт 99", () ->{
        lite99.shouldBe(visible).click();
    });
    }

    public static void clickLife() {
        step("Кликаем по кнопке Пожизненный доступ", () ->{
        sleep(500);
        life.shouldBe(visible).click();
    });
    }

    public static void clickPremium() {
        step("Кликаем по кнопке Премиум", () ->{
        sleep(500);
        premium.shouldBe(visible).click();
    });
    }

    public static String setTextFromModal() {
        return textModal.shouldBe(visible).getText().trim();
    }

    public static void clickBtnClose() {
        step("Закрываем мод окно", () ->{
        closeModal.click();
    });
    }

}
