package ru.tutor.page;

import com.github.dockerjava.api.model.Driver;
import com.github.javafaker.Faker;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.Locale;

public class LoginAndPass extends Driver {

    public LoginAndPass(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    private static final Faker faker = new Faker(new Locale("en"));

    //Оплата с главной страницы

    @FindBy(css = "input")
    private static WebElement email;

    public static void inputEmail() {
        email.isDisplayed();
        email.sendKeys("tp4712@test.ru");
//        email.sendKeys("Tin_Empty4@emaily3.pro");

//        email.sendKeys("tp5053_1HybridTinAdmin@emaily3.pro");

//        email.sendKeys("gibridTin_71@emaily3.pro");
//        email.sendKeys("tp5053_1revsharetin@emaily3.pro");


//        email.sendKeys("tp5053_1cpaSber@emaily3.pro");
//        email.sendKeys("gybridSber@emaily3.pro");
//        email.sendKeys("revshareSber@emaily3.pro");
    }

    // Регистрация с клиента

    @FindBy(name = "email")
    private static WebElement emailReg;

    public static void getEmail() {
//        emailReg.sendKeys("OrderDelete6@test.ru");
        emailReg.sendKeys("lognewpay_20@emaily.pro");
    }
//    avotoi@tets.ru

    public static void getEmailFromAdminDev() {
        emailReg.sendKeys("admin");
    }

    @FindBy(name = "password")
    private static WebElement password;

    public static void getPassword() {
        password.sendKeys("12345678");
    }

    public static void getPasswordFromAdminDev() {
        password.sendKeys("admin");
    }

    @FindBy(name = "newPassword")
    private static WebElement newPass;

    public static void getNewPass() {
        newPass.sendKeys("12345678");
    }

    @FindBy(name = "repeatPassword")
    private static WebElement repeatPassword;

    public static void getRepeat() {
        repeatPassword.sendKeys("12345678");
    }

    @FindBy(name = "name")
    private static WebElement name;

    public static void getname() {
        name.sendKeys("dfn");
    }

    @FindBy(css = "#root > div.sc-bmzYkS.oRjkM > div > div > div.sc-hYmls.guJPqE > form > div.sc-bOhtcR.kseIqm > label:nth-child(1) > input")
    private static WebElement selector;

    public static String setSelector() {
        try {
            selector.isSelected();
            selector.click();
            return "Кнопка выбрана";
        } catch (NoSuchElementException e) {
            return "Кнопка не выбрана";
        }
    }

    @FindBy(css = "button")
    private static WebElement nextBtn;

    public static void setNext() {
        nextBtn.click();
    }

    public static void registrationNewUser() {
        getname();
        setNext();
        getEmail();
        getPassword();
        getRepeat();
        setNext();
    }

    public static void registrationUserFromAdminDev() {
        getEmailFromAdminDev();
        getPasswordFromAdminDev();
        setNext();
    }

    public static void registrationUser() {
        try {
            getEmail();
            getPassword();
            ClientPrime.btnEnter();
        } catch (NoSuchElementException e) {
        }
    }

    public static void registrationFromBild() {
        setSelector();
        getname();
        getNewPass();
        getRepeat();
        setNext();
    }

    public static void registrationFromExel() {
        setNext();
        getname();
        getNewPass();
        getRepeat();
        System.out.println(modal.modalWelcomeForExel());
    }

}
