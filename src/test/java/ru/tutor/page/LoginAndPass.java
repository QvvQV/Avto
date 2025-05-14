package ru.tutor.page;

import com.codeborne.selenide.SelenideElement;
import com.github.dockerjava.api.model.Driver;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import com.github.javafaker.Faker;

import java.util.Locale;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.closeWebDriver;

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
        email.sendKeys("TP4516/12@emaily3.pro");
//        email.sendKeys("TP4516/5@emaily3.pro");
//        email.sendKeys("TP4516/4@emaily3.pro");
//        email.sendKeys("TP4516/3@emaily3.pro");
    }

//    TP_4657@emaily3.pro

    // Регистрация с клиента

    @FindBy(name = "email")
    private static WebElement emailReg;

    public static void getEmail() {
        emailReg.sendKeys("TP4516_2@emaily3.pro");
    }

    @FindBy(name = "password")
    private static WebElement password;

    public static void getPassword() {
        password.sendKeys("12345678");
    }

    @FindBy(name = "newPassword")
    private static WebElement newPass;

    public static void getNewPass(){
        newPass.sendKeys("12345678");
    }

    @FindBy(name = "repeatPassword")
    private static WebElement repeatPassword;

    public static void getRepeat(){
        repeatPassword.sendKeys("12345678");
    }

    @FindBy(name = "name")
    private static WebElement name;

    public static void getname() {
        name.sendKeys("dfn");
    }

    @FindBy(css = "#root > div.sc-bmzYkS.oRjkM > div > div > div.sc-hYmls.guJPqE > form > div.sc-bOhtcR.kseIqm > label:nth-child(1) > input")
    private static WebElement selector;

    public static String setSelector(){
        try{
        selector.isSelected();
        selector.click();
        return "Кнопка выбрана";
        } catch (NoSuchElementException e){
          return "Кнопка не выбрана";
        }
    }

    @FindBy(css = "button")
    private static WebElement nextBtn;

    public static void setNext(){
        nextBtn.click();
    }

    public static void registrationNewUser(){
        getname();
        setNext();
        getEmail();
        getPassword();
        getRepeat();
        setNext();
    }

    public static void registrationUser(){
        getEmail();
        getPassword();
        ClientPrime.btnEnter();
    }

    public static void registrationFromBild(){
        setSelector();
        getname();
        getNewPass();
        getRepeat();
        setNext();
    }

}
