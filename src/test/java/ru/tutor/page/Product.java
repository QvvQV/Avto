package ru.tutor.page;

import com.github.dockerjava.api.model.Driver;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
//import static com.codeborne.selenide.Selenide.*;
//import static com.codeborne.selenide.Selenide.closeWebDriver;

public class Product extends Driver {

    public Product(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }


    @FindBy(css = ".sc-cwHptR.igXsWb.sc-cPiKLX.gMcsNn")
    private static WebElement inputSearch;

    //https://client.dev.tutorplace.ru/user/main?globalSearch=крав


    @FindBy(css = "._ProductCard_1bxex_1")
    private static WebElement product;


    @FindBy(css = "._Button_kio3a_1._fullWidth_kio3a_195")
    private static WebElement clickAdd;

//        public static void doubleClick(WebDriver driver) {
//        WebElement clickable = driver.findElement(By.cssSelector(".sc-cwHptR"));
//        new Actions(driver)
//                .doubleClick(clickable)
//                .perform();
//        Clear();
//    }


    public static boolean setClickAdd() {
        try {
            clickAdd.isDisplayed();
            clickAdd.click();
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }


    public static String ProductSearch(WebDriver driver) {
        String[] Product = {
                "крав",
                "Figma"
        };

        for (int i = 0; i < Product.length; i++) {
            if (inputSearch.isDisplayed() && inputSearch.isEnabled()) {
                inputSearch.sendKeys(Product[i]);
                product.click();
                setClickAdd();
                modal.setKursSuccesfullAdd();
                modal.clickStay();
                PromoCode.doubleClick(driver);
//        System.out.println(error.getText().trim());
//        doubleClick(driver);
            }
        }
        return "Пустое поле";
    }

    public static void Clear() {
//        inputSearch.clear();
        inputSearch.sendKeys(Keys.CLEAR);
    }

    public static void diet() {
        inputSearch.sendKeys("рацион");
        product.isDisplayed();
        product.click();
    }

}


