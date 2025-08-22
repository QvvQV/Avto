package ru.tutor.page;

import com.codeborne.selenide.SelenideElement;
import com.github.javafaker.Faker;
import org.openqa.selenium.By;

import java.time.Duration;
import java.util.Locale;
import java.util.Set;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;

public class SberPage {
    private static final Faker faker = new Faker(new Locale("en"));
    public static final String url = "https://admin:JWwppsEA84B4BozJgE44sNiZZ@ttplace.ru/catalog/product/excel";
    private static SelenideElement buyBtn = $$("button").find(exactText("начать обучение за 1 ₽*"));
    private static SelenideElement accessBtn = $(By.id("access-button"));
    private static SelenideElement error = $$("p").find(exactText("Произошла ошибка. Пожалуйста, попробуйте ещё раз."));
    private static SelenideElement email = $("[name=\"email\"]");
    private static SelenideElement cardNumber = $("[id=\"cardNumber\"]");
    private static SelenideElement expiryDate = $("[id=\"expiryDate\"]");
    private static SelenideElement cvc = $("[id=\"cvc\"]");
    private static SelenideElement detailBtn = $("[id=\"details-button\"]");
    private static SelenideElement proceedLink = $("[id=\"proceed-link\"]");
    private static SelenideElement successBtn = $$("button").find(exactText("Оплатить"));
    private static SelenideElement passwordEdit = $("[id=\"passwordEdit\"]");
    private static SelenideElement textError = $$("span").find(exactText("Операция отклонена. Проверьте введенные данные, достаточность средств на карте и повторите операцию"));
    private static SelenideElement goShop = $$("button").find(exactText("Перейти в магазин"));


    public static void changeWindowHandle(){
        String a = "ПОПРОБУЙ ПЕРВУЮ НЕДЕЛЮ ТАРИФА “ЛАЙТ”";
        String b = "ПОПРОБУЙ ПЕРВУЮ НЕДЕЛЮ ТАРИФА “ПРЕМИУМ”";
        String c = "ОФОРМИ ПОЖИЗНЕННЫЙ ДОСТУП К БОЛЕЕ ЧЕМ К 400 КУРСАМ";
        String currentWindowHandle = webdriver().driver().getWebDriver().getWindowHandle();
        step("Ввод email. Отсутствие ошибки под полем mail", () ->{
        email.sendKeys(faker.internet().emailAddress());
        if (ttPage.setTextFromModal().equals(a)){
            System.out.println("Оплата продукта с главной Лайт: " + atrEmail());
        } else if (ttPage.setTextFromModal().equals(b)) {
            System.out.println("Оплата продукта с главной Премиум: " + atrEmail());
        } else if (ttPage.setTextFromModal().equals(c)){
            System.out.println("Оплата продукта с главной Пожизненный: " + atrEmail());
        }
        CloudPage.full.shouldBe(visible).click();
        sleep(9000);
        error.shouldNot(visible);});
        Set<String> windowHandles = webdriver().driver().getWebDriver().getWindowHandles();
        for (String windowHandle : windowHandles) {
            if (!windowHandle.equals(currentWindowHandle)) {
                webdriver().driver().getWebDriver().switchTo().window(windowHandle);
                break;
            }
        }
    }

    public static void clickBtnStart() {
        step("клик по кнопке оплатить", () ->{
        buyBtn.shouldBe(visible).click();});
        String currentWindowHandle = webdriver().driver().getWebDriver().getWindowHandle();
        step("Ввод email. Отсутствие ошибки под полем mail", () ->{
        email.sendKeys(faker.internet().emailAddress());
        System.out.println("Оплата с продукта Excel за 1 руб: " + atrEmail());
        accessBtn.shouldBe(visible).click();
        sleep(9000);
        error.shouldNot(visible);});
        Set<String> windowHandles = webdriver().driver().getWebDriver().getWindowHandles();
        for (String windowHandle : windowHandles) {
            if (!windowHandle.equals(currentWindowHandle)) {
                webdriver().driver().getWebDriver().switchTo().window(windowHandle);
                break;
            }
        }
    }

//    Валидная карта

    public static String atrEmail() {
        return email.getDomAttribute("value");
    }

    public static String getTextError() {
        return textError.getText().trim();
    }

    public static void getApprovedCard() {
        step("Ввод валидной карты", () ->{
        cardNumber.shouldBe(visible).sendKeys("2202205000012424");
        expiryDate.sendKeys("0535");
        cvc.sendKeys("669");
        successBtn.shouldBe(visible).click();
        goShop.shouldBe(visible, Duration.ofSeconds(10)).click();});
    }

//    Недостаточно средств

    public static void getNoMoneyCard() {
        step("Ввод данных карты - недостаточно средств", () ->{
        cardNumber.shouldBe(visible).sendKeys("2202205000060910");
        expiryDate.sendKeys("0628");
        cvc.sendKeys("810");
        successBtn.shouldBe(visible).click();
//        detailBtn.shouldBe(visible, Duration.ofSeconds(10)).click();
//        proceedLink.shouldBe(visible).click();
        passwordEdit.shouldBe(visible, Duration.ofSeconds(10)).sendKeys("111111");
        sleep(3000);});
    }

    //Карта просрочена

    public static void getExpireCard() {
        step("Ввод данных карты с истёкшим сроком", () ->{
        cardNumber.shouldBe(visible).sendKeys("2202202107593166");
        expiryDate.sendKeys("0622");
        cvc.sendKeys("123");
        successBtn.shouldBe(visible).click();
//        detailBtn.shouldBe(visible, Duration.ofSeconds(10)).click();
//        proceedLink.shouldBe(visible).click();
        passwordEdit.shouldBe(visible, Duration.ofSeconds(10)).sendKeys("111111");
        sleep(3000);});
    }

//    Фиксированный баланс 100р

    public static void getFixBalanceCard() {
        step("Ввод данных карты с фиксированным остатком", () ->{
        cardNumber.shouldBe(visible).sendKeys("2202205300068092");
        expiryDate.sendKeys("0526");
        cvc.sendKeys("583");
        successBtn.shouldBe(visible).click();
//        detailBtn.shouldBe(visible, Duration.ofSeconds(10)).click();
//        proceedLink.shouldBe(visible).click();
        passwordEdit.shouldBe(visible, Duration.ofSeconds(10)).sendKeys("111111");
        sleep(3000);});
    }

}