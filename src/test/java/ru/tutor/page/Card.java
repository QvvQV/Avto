package ru.tutor.page;

import com.github.dockerjava.api.model.Driver;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static ru.tutor.test.Sber.PaySuccessfull.driver;
//import static com.codeborne.selenide.Selenide.*;
//import static com.codeborne.selenide.Selenide.closeWebDriver;

public class Card extends Driver {

    public Card(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    // Оплата с баннера Excel с помощью Тинькофф карты

    @FindBy(css = "tui-input-card-group > div:nth-child(1) > label")
    private static WebElement text;

    @FindBy(css = "[automation-id=\"tui-input-card-group__card\"]")
    private static WebElement numberCard;

    @FindBy(css = ".text-red-500.text-center")
    private static WebElement textError;


    //тинькофф карта

    public static void numberCardFromExcel() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOf(numberCard));
        numberCard.sendKeys("42424242424242420455555");
//        if (numberCard.isDisplayed()) {
//            try {
//                numberCard.sendKeys("42424242424242420455555");
//
//            } catch (NoSuchElementException e) {
//            }
//        }
    }

    public static void visibleTextError() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));

//        boolean b = textError.isDisplayed();
//        if(b==false){
//            System.out.println("двигаемся дальше");
//        } else if (b==true) {
//            System.out.println("Что-то пошло не так");
//        }
        if (wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".text-red-500.text-center")))) {

            System.out.println("двигаемся дальше");
        }
        System.out.println("Что-то пошло не так");
    }

    public static String textCard() {
        return text.getText().trim();
    }

    // Оплата с баннера в личном кабинете Тинькофф карта

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

    // ввод данных банковской карточки на др вкладке Тинькофф
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


    // Оплата с баннера Excel с помощью Сбер карты

    @FindBy(id = "cardNumber")
    private static WebElement cardSber;

    public static void numberCardSber() {
        cardSber.sendKeys("2202205000012424"); //успешная карта
    }

    @FindBy(id = "expiryDate")
    private static WebElement expireData;

    private static void getExpireData() {
        expireData.sendKeys("0535");
    } //успешная карта

    @FindBy(id = "cvc")
    private static WebElement cvc;

    private static void getCvc() {
        cvc.sendKeys("669");
    } //успешная карта

    @FindBy(css = "._14KXZFF2qamrgjaDJQf2-k._2LEfKwbPi7lA1km6XdQKz0")
    private static WebElement btnPaySber;

    private static void getPaySber() {
        if (btnPaySber.isDisplayed() && btnPaySber.isEnabled()) {
            btnPaySber.click();
        }
    }


    // ввод данных банковской карточки на др вкладке Sber (успешная)
    public static boolean setCardSber() {
        try {
            numberCardSber();
            getExpireData();
            getCvc();
            getPaySber();
            return cardSber.isEnabled();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
