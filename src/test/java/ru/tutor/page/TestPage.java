package ru.tutor.page;

import com.codeborne.selenide.SelenideElement;
import com.github.javafaker.Faker;
import org.openqa.selenium.By;

import java.time.Duration;
import java.util.Locale;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class TestPage {
    private static final Faker faker = new Faker(new Locale("en"));
    public static final String url = "https://admin:JWwppsEA84B4BozJgE44sNiZZ@ttplace.ru/catalog/product/excel";
    private static SelenideElement buyBtn = $$("button").find(exactText("начать обучение за 1 ₽*"));
    private static SelenideElement accessBtn = $(By.id("access-button"));
    private static SelenideElement error = $$("p").find(exactText("Произошла ошибка. Пожалуйста, попробуйте ещё раз."));
    private static SelenideElement email = $("[name=\"email\"]");
    private static SelenideElement card_page_button = $("[data-test=\"cardpay-page-button\"]");
    private static SelenideElement number_card = $("[automation-id=\"tui-input-card-group__card\"]");
    private static SelenideElement cardPayBtn = $("[data-test=\"cardpay-button\"]");
    private static SelenideElement successBtn = $$("button").find(exactText("Успех"));

    public static void clickBtnStart() {
        buyBtn.shouldBe(visible).click();
        email.sendKeys(faker.internet().emailAddress());
        accessBtn.shouldBe(visible).click();
        error.shouldNot(visible);
    }

    public static String atrEmail() {
        return email.getDomAttribute("value");
    }

    private static String cardTinkoff() {
        String tinNumber;
        return tinNumber = "4242424242424242" + faker.number().digits(7);
    }

    public static void clickCardPageBtn() {
        sleep(2000);
        switchTo().frame(0);
        sleep(2000);
        card_page_button.shouldBe(visible).click();
        number_card.shouldBe(visible, Duration.ofSeconds(10)).sendKeys(cardTinkoff());
        cardPayBtn.click();
        switchTo().frame(0);
        successBtn.shouldBe(visible).click();
    }
}