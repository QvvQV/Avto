package ru.tutor.page;

import com.codeborne.selenide.SelenideElement;
import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import java.time.Duration;
import java.util.Locale;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;

public class CloudPage {
    private static final Faker faker = new Faker(new Locale("en"));
    private static SelenideElement buyBtn = $$("button").find(exactText("начать обучение за 1 ₽*"));
    private static SelenideElement accessBtn = $(By.id("access-button"));
    private static SelenideElement error = $$("p").find(exactText("Произошла ошибка. Пожалуйста, попробуйте ещё раз."));
    private static SelenideElement email = $("[name=\"email\"]");
    private static SelenideElement card_page_button = $("[data-test=\"cardpay-page-button\"]");
    private static SelenideElement number_card = $("[automation-id=\"tui-input-card-group__card\"]");
    private static SelenideElement cardPayBtn = $("[data-test=\"cardpay-button\"]");
    private static SelenideElement successBtn = $$("button").find(exactText("Успех"));
    public static SelenideElement full = $("[name=\"full\"]");
    private static SelenideElement installments = $("[name=\"installments\"]");

    public static void clickBtnStart() {
        step("Клик по кнопке и ввод email. Отсутствие сообщения под email строчкой", ()->{
        buyBtn.shouldBe(visible).click();
        email.sendKeys(faker.internet().emailAddress());
        accessBtn.shouldBe(visible).click();
        error.shouldNot(visible);});
    }

    public static String atrEmail() {
        return email.getDomAttribute("value");
    }

    private static String cardTinkoff() {
        String tinNumber;
        return tinNumber = "42424242424242420745555";
    }

    // ввод банковских данных в модалку Cloud
    public static void clickCardPageBtn() {
        step("ввод валидных банковских данных", ()->{
        sleep(2000);
        switchTo().frame(0);
        sleep(2000);
        card_page_button.shouldBe(visible).click();
        number_card.shouldBe(visible, Duration.ofSeconds(10)).sendKeys(cardTinkoff());
        cardPayBtn.click();
        switchTo().frame(0);
        successBtn.shouldBe(visible).click();});
    }

    public static void setDateModal(){
        step("Ввод рандомного email. Отсутствие ошибки под полем ввода mail", ()->{
        email.sendKeys(faker.internet().emailAddress());
        full.shouldBe(visible).click();
        error.shouldNot(visible);});
    }

        public static void setDateModalInstallments(){
        step("Ввод почты в поле мод окна. Отсутствие ошибки под полем email", ()->{
        email.sendKeys(faker.internet().emailAddress());
        installments.shouldBe(visible).click();
        error.shouldNot(visible);});
    }
}