package ru.tutor.page;

import com.github.dockerjava.api.model.Driver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
//import static com.codeborne.selenide.Selenide.*;
//import static com.codeborne.selenide.Selenide.closeWebDriver;

public class ClientPrime extends Driver {

    public ClientPrime(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//*[@id=\"root\"]/div[2]/div/div/div[2]/div/button")
    private static WebElement enterBtn;

    @FindBy(xpath = "//*[@id=\"root\"]/div[2]/div/div/form/div/div[2]/button")
    private static WebElement nextBtn;

    @FindBy(xpath = "//*[@id=\"root\"]/div[2]/div/button")
    private static WebElement close;

    @FindBy(xpath = "/html/body/div[3]/div/div/div/div[2]/button[2]")
    private static WebElement dontAsk;

    @FindBy(xpath = "//*[@id=\"root\"]/div[2]/div[1]/div/div[2]/div[1]")
    private static WebElement searchBtn;


    @FindBy(xpath = "/html/body/div[4]/div/div/div/div[2]/div[3]/button")
    private static WebElement exelProd;

    @FindBy(xpath = "/html/body/div[5]/div/div/div[2]/button[1]")
    private static WebElement ostat;

    @FindBy(xpath = "//*[@id=\"root\"]/div[2]/div[2]/div/div[2]/div/div[1]/div/div/div[1]/div[1]/button/div[2]/button[3]")
    private static WebElement delete;

    @FindBy(xpath = "//*[@id=\"root\"]/div[2]/div[2]/div/div[2]/div/div[1]/div")
    private static WebElement nameSearch;

    //Lite
    @FindBy(xpath = "//*[@id=\"root\"]/div[2]/div[1]/div/div[2]/div[2]/div[2]")
    private static WebElement profil;

    @FindBy(xpath = "//*[@id=\"root\"]/div[2]/div[2]/div/div/div/div[2]/div/button")
    private static WebElement btn_change;

    @FindBy(xpath = "//*[@id=\"root\"]/div[2]/div[2]/div/div/div/div[2]/h4")
    private static WebElement textChange;


    public static void getProfil() {
        profil.click();
    }

    public static void btnEnter() {
        enterBtn.click();
    }

    public static void setNextBtn() {
        nextBtn.click();
    }

    public static void setClose() {
        close.click();
    }

    public static void setAsk() {
        dontAsk.click();
    }

    public static void setSearchBtn() {
        searchBtn.click();
    }

    public static String getMenuSearch() {
        return nameSearch.getText();
    }

    @FindBy(css = "div.sc-hPbuba.kcUBru > h4")
    private static WebElement textPremium;

    public static String setTextPremium() {
        return textPremium.getText().trim();
    }
}
