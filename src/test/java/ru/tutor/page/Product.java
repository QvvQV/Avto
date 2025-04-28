package ru.tutor.page;

import com.github.dockerjava.api.model.Driver;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
//import static com.codeborne.selenide.Selenide.*;
//import static com.codeborne.selenide.Selenide.closeWebDriver;

public class Product extends Driver {

    public Product(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }


    @FindBy(css = ".sc-cwHptR")
    private static WebElement inputSearch;

    //https://client.dev.tutorplace.ru/user/main?globalSearch=крав


    @FindBy(css = "._ProductCard_1bxex_1")
    private static WebElement product;


    @FindBy(css = "._Button_kio3a_1")
    private static WebElement clickAdd;


    public static boolean setClickAdd() {
        try {
            clickAdd.isDisplayed();
            clickAdd.click();
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public static void addKrav_Maga() {
        inputSearch.sendKeys("крав");
        product.isDisplayed();
        product.click();
        setClickAdd();
        modal.setKursSuccesfullAdd();
        modal.clickStay();
    }

    public static void Targetolog() {
        inputSearch.sendKeys("тарг");
        product.isDisplayed();
        product.click();
        setClickAdd();
        modal.setKursSuccesfullAdd();
        modal.clickStay();
    }

    public static void Clear(){
        inputSearch.clear();
    }

    public static void diet() {
        inputSearch.sendKeys("рацион");
        product.isDisplayed();
        product.click();
    }

}


