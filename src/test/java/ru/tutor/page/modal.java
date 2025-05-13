package ru.tutor.page;

import com.github.dockerjava.api.model.Driver;
import com.github.javafaker.Faker;
import com.github.javafaker.PhoneNumber;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import org.openqa.selenium.support.ui.ExpectedCondition;

import java.util.Locale;
import java.util.Objects;
//import static com.codeborne.selenide.Selenide.*;
//import static com.codeborne.selenide.Selenide.closeWebDriver;

public class modal extends Driver {

    public modal(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    // первая модалка приветствие и смена пароля

    @FindBy(xpath = "//*[@id=\"root\"]/div[2]/div/div/div[2]/button")
    private static WebElement nextStep;

    public static String modalWelcomeForNewUser() {
//        if (nextStep.isDisplayed()) {
//            nextStep.click();
//            yourMale.click();
//            inputMale.sendKeys(Keys.ENTER);
//            nextStepTwo.click();
//            return true;
//        }
//        return false;
        try {
            nextStep.isDisplayed();
            nextStep.click();
            yourMale.click();
            inputMale.sendKeys(Keys.ENTER);
            nextStepTwo.click();
            return "Модальное окно приветствия заполнено";
//            return Objects::nonNull;
        } catch (NoSuchElementException e) {
            return "Модального окна приветствия не было";
//            return Objects::isNull;
        }
    }

    @FindBy(id = "react-select-2-placeholder")
    private static WebElement yourMale;

    @FindBy(id = "react-select-2-input")
    private static WebElement inputMale;

    @FindBy(xpath = "//*[@id=\"root\"]/div[2]/div/div/form/div/div[2]/button")
    private static WebElement nextStepTwo;

    // модальное окно для пользователя пришедшего через регистрацию

//    public static boolean modalWelcomeForNewUser() {
//        clickNextStep();
//        yourMale.click();
//        inputMale.sendKeys(Keys.ENTER);
//        nextStepTwo.click();
//        return headerModal.isDisplayed();
//    }

    // модальное окно для пользователя пришедшего с ленда

    //окно ежедневного захода
    @FindBy(css = "._Overlay_cd7ce_1")
    private static WebElement dailyCall;

    @FindBy(css = ".sc-gFqAkR")
    private static WebElement btnCtnForDailyCall;

    public static String modalDailyCall() {
        try {
            dailyCall.isDisplayed();
            btnCtnForDailyCall.click();
            return "Награда за ежедневный заход получена";
        } catch (NoSuchElementException e) {
            return "Модальное окно ежедневного захода не появилось";
        }
    }

    // модалка для ввода телефона

    @FindBy(css = "src")
    private static WebElement search;

    @FindBy(css = "body > div:nth-child(6) > div > div > div > div > div > form > div:nth-child(1) > div > div > label")
    private static WebElement textTelephone;

    @FindBy(css = "body > div:nth-child(6) > div > div > div > div > div > form > div:nth-child(1) > div > div > input")
    private static WebElement inputTelephone;

    @FindBy(css = "body > div:nth-child(6) > div > div > div > div > div > form > div:nth-child(3) > button")
    private static WebElement tie;

    @FindBy(css = ".sc-dtInlm")
    private static WebElement buttonCrossTie;


    public static String modalTelephone() {
        try {
            inputTelephone.isDisplayed();
//            inputTelephone.sendKeys((CharSequence) getNumberTelephone());
            inputTelephone.sendKeys("9548562546");
            buttonCrossTie.click();
            return "Телефон не привязан";
        } catch (NoSuchElementException e) {
            return "Модальное окно для телефона не появилось";
        }
    }

    // Добавление курса
    @FindBy(css = "._Overlay_cd7ce_1._isPopup_cd7ce_11")
    private static WebElement kursSuccessfullAdd;

    public static boolean setKursSuccesfullAdd() {
        try {
            kursSuccessfullAdd.isDisplayed();
            System.out.println(kursSuccessfullAdd.getText());
            return kursSuccessfullAdd.equals("Курс успешно добавлен");
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    @FindBy(css = "._Button_kio3a_1._lightGrey_kio3a_74._fullWidth_kio3a_195")
    private static WebElement stay;

    public static boolean clickStay() {
        try {
            stay.isDisplayed();
            stay.click();
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    @FindBy(css = "button._Button_kio3a_1._fullWidth_kio3a_195")
    private static WebElement yes;

    public static void setYes() {
        yes.isDisplayed();
        yes.click();
    }

    @FindBy(css = "._Button_kio3a_1")
    private static WebElement premiumFor99;

    public static boolean getPremiumFor99() {
        try {
            premiumFor99.isDisplayed();
            premiumFor99.isEnabled();
            System.out.println(premiumFor99.getText().trim());
            premiumFor99.click();
            return true;
        } catch (NoSuchElementException e){
            return false;
        }
    }
}
