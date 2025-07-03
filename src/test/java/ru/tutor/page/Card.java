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

    public static void numberCardSber116() {
        cardSber.sendKeys("2202205000060910"); //недостаточно средств ошибка 116
    }

    public static void numberCardSber101() {
        cardSber.sendKeys("2202202107593166"); //Карта просрочена ошибка 101
    }

    public static void numberCardSberWith100Rub() {
        cardSber.sendKeys("2202205300068092"); //Остаток на балансе 100 рублей
    }

    @FindBy(id = "expiryDate")
    private static WebElement expireData;

    private static void getExpireData() {
        expireData.sendKeys("0535");
    } //успешная карта

    private static void getExpireData116() {
        expireData.sendKeys("0628");
    } //недостаточно средств ошибка 116

    private static void getExpireData101() {
        expireData.sendKeys("0622");
    } //Карта просрочена ошибка 101

    private static void getExpireDataWith100Rub() {
        expireData.sendKeys("0526");
    } //Остаток на балансе 100 рублей

    @FindBy(id = "cvc")
    private static WebElement cvc;

    private static void getCvc() {
        cvc.sendKeys("669");
    } //успешная карта

    private static void getCvc116() {
        cvc.sendKeys("810");
    } //недостаточно средств ошибка 116

    private static void getCvc101() {
        cvc.sendKeys("123");
    } //Карта просрочена ошибка 101

    private static void getCvcWith100Rub() {
        cvc.sendKeys("583");
    } //Остаток на балансе 100 рублей

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

    // на балансе 100 рублей Sber (успешная)
    public static boolean setCardSberWith100Rub() {
        try {
            numberCardSberWith100Rub();
            getExpireDataWith100Rub();
            getCvcWith100Rub();
            getPaySber();
            return cardSber.isEnabled();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    // ввод данных банковской карточки на др вкладке Sber (недостаточно средств 116)
    public static boolean setCardSber116() {
        try {
            numberCardSber116();
            getExpireData116();
            getCvc116();
            getPaySber();
            return cardSber.isEnabled();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    // ввод данных банковской карточки на др вкладке Sber (просрочена 101)
    public static boolean setCardSber101() {
        try {
            numberCardSber101();
            getExpireData101();
            getCvc101();
            getPaySber();
            return cardSber.isEnabled();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    @FindBy(id = "details-button")
    private static WebElement secondaryBtn;

    private static void clickSecondaryBtn() {
        secondaryBtn.click();
    }

    @FindBy(id = "proceed-link")
    private static WebElement smallLink;

    private static void clickSmallLink() {
        smallLink.isDisplayed();
        smallLink.click();
    }

    public static void transitionLink() {
        clickSecondaryBtn();
        clickSmallLink();
    }

    @FindBy(id = "passwordEdit")
    private static WebElement passwordEdit;

    public static void setPasswordEdit() {
        try {
            passwordEdit.isDisplayed();
            passwordEdit.sendKeys("111111");
        } catch (NoSuchElementException e) {
        }
    }
}
