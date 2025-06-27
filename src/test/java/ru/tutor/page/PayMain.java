package ru.tutor.page;

import com.github.dockerjava.api.model.Driver;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
//import static com.codeborne.selenide.Selenide.*;
//import static com.codeborne.selenide.Selenide.closeWebDriver;
import org.openqa.selenium.WebDriver;

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

    @FindBy(css = "div.repeat-action > button > div")
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
