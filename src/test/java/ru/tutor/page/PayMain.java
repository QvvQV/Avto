package ru.tutor.page;

import com.github.dockerjava.api.model.Driver;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
//import static com.codeborne.selenide.Selenide.*;
//import static com.codeborne.selenide.Selenide.closeWebDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.time.Duration;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

public class PayMain extends Driver {
    public PayMain(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    // Оплата банковской картой с баннера в личном кабинете (профиль)
    @FindBy(css = "button")
    private static WebElement access;

    public static void getAccess() {
        if (access.isDisplayed() && access.isEnabled()) {
            access.click();
        }
    }

    // Оплата с помощью банковской карты через страницу продукта Excel
    @FindBy(css = ".font-inter")
    private static WebElement BtnStart;

    public static void clickBtnStart(){
        if (BtnStart.isDisplayed() && BtnStart.isEnabled()){
            BtnStart.click();
        }
    }

    @FindBy(id = "access-button")
    private static WebElement Dostup;

    public static void getDostup() {
        try {
            Dostup.isDisplayed();
            LoginAndPass.inputEmail();
            Dostup.isEnabled();
            Dostup.click();
        } catch (NoSuchElementException e) {
        }

    }

    @FindBy(xpath = "//button[@type=\"submit\"]")
    private static WebElement btnOpen1Rub;

    public static void setBtnOpen1Rub(){
        btnOpen1Rub.click();
    }

    @FindBy(name = "full")
    private static WebElement btnOpen99Rub;

    public static void setBtnOpen99Rub(){
        btnOpen99Rub.click();
    }

    @FindBy(name="installments")
    private static WebElement btnOpenShares;

        public static void setBtnOpenShares(){
        btnOpenShares.click();
    }

    @FindBy(css = ".tarrifs_tarrifs__20kzx > div.premium_wrapper__yr98S > button")
    private static WebElement buyBtn;

     public static void scrollTo(WebDriver driver) {
        new Actions(driver)
                .scrollToElement(buyBtn)
                .perform();
     }

     @FindBy(css = ".tarrifs_tarrifs__20kzx > div.lite_wrapper__sOzEL > button")
     private static WebElement buyLiteMain;

     public static void clickBuyLiteMain(WebDriver driver){
         WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
         scrollTo(driver);
         wait.until(visibilityOfElementLocated(By.cssSelector(".buy_btn__xNJXP")));
         buyLiteMain.click();
     }

     @FindBy(css = ".tarrifs_tarrifs__20kzx > div.premium_wrapper__yr98S > button")
     private static WebElement buyPremiumMain;

     public static void clickBuyPremiumMain(WebDriver driver){
         WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
         scrollTo(driver);
         wait.until(visibilityOfElementLocated(By.cssSelector(".buy_btn__xNJXP")));
         buyPremiumMain.click();
     }

     @FindBy(css = ".tarrifs_tarrifs__20kzx > div.unlim_wrapper__ESiFA > button")
     private static WebElement buyAllMain;

     public static void clickBuyAllMain(WebDriver driver){
         WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
         scrollTo(driver);
         wait.until(visibilityOfElementLocated(By.cssSelector(".buy_btn__xNJXP")));
         buyAllMain.click();
     }

    public static void getMailDostup(){
        LoginAndPass.inputMail();
    }

            public static void main(String[] args) {
            String randomEmail = generateRandomEmail();
            try {
                InternetAddress address = new InternetAddress(randomEmail);
                System.out.println("Generated email address: " + address.toString());
            } catch (AddressException e) {
                System.err.println("Invalid email address format: " + e.getMessage());
            }
        }

        public static String generateRandomEmail() {
            String username = RandomStringUtils.randomAlphanumeric(10); // 10 случайных символов для имени пользователя
            String domain = "example.com"; // Пример домена
            return username + "@" + domain;
        }

    @FindBy(css = "[data-test='cardpay-page-button']")
    private static WebElement BankBtn;

    public static void clickBankBtn() {
        if(BankBtn.isDisplayed()){
                BankBtn.click();
    }
    }

    public static String textButton() {
        return BankBtn.getText().trim();
    }

    @FindBy(css = "card-pay-edit > button")
    private static WebElement Pay;

    public static void getPay() {
        Card.numberCardFromExcel();
        Pay.click();
    }

    @FindBy(css = ".repeat-action > button > div")
    private static WebElement success;

    public static void btnSuccess() {
//        success.isDisplayed();
        success.click();
    }

    @FindBy(css = "payment-module-frontend-payment-module-entry > pm-success > div > button > div")
    private static WebElement shopReturn;
//    body > widget-host-root > tui-root > tui-dropdowns > div > div > div > div > div > payment-module-frontend-payment-module-entry > pm-success > div > button > div

    public static void setShopReturn() {
        shopReturn.isDisplayed();
        shopReturn.click();
    }
}
