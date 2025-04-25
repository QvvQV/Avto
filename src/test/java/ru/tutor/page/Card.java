package ru.tutor.page;

import com.github.dockerjava.api.model.Driver;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
//import static com.codeborne.selenide.Selenide.*;
//import static com.codeborne.selenide.Selenide.closeWebDriver;

public class Card extends Driver {

    public Card(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    // Оплата с баннера Excel

    @FindBy(css = "tui-input-card-group > div:nth-child(1) > label")
    private static WebElement text;

    @FindBy(css = "[automation-id=\"tui-input-card-group__card\"]")
    private static WebElement numberCard;

    public static void numberCardFromExcel() {
        if (numberCard.isDisplayed()) {
            try {
                numberCard.sendKeys("42424242424242420455555");

            } catch (NoSuchElementException e) {
            }
        }
    }

    public static String textCard() {
        return text.getText().trim();
    }

    // Оплата с баннера в личном кабинете

    @FindBy(id = "card")
    private static WebElement card;

    public static void numberCardFromBanner() {
        card.sendKeys("4242424242424242");
    }

    @FindBy(id = "date")
    private static WebElement data;

    private static void getData() {
        data.sendKeys("0627");
    }

    @FindBy(id = "cvv")
    private static WebElement cvv;

    private static void getCvv() {
        cvv.sendKeys("555");
    }

    @FindBy(tagName = "button")
    private static WebElement btnPay;

    private static void getPay() {
        if (btnPay.isDisplayed() && btnPay.isEnabled()) {
            btnPay.click();
        }
    }

    // ввод данных банковской карточки на др вкладке
    public static boolean setCard() {
        try {
            numberCardFromBanner();
            getData();
            getCvv();
            getPay();
            return card.isEnabled();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    @FindBy(css = "#app > div > div > div > div > div.repeat-action > button")
    private static WebElement successBtn;

    public static void getSuccess() {
        successBtn.isDisplayed();
        successBtn.click();
    }
}
