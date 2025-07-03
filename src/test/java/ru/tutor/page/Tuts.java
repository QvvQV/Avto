package ru.tutor.page;

import com.github.dockerjava.api.model.Driver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class Tuts extends Driver {

    public Tuts(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = ".sc-aNeao.jANqqc")
    public static WebElement main;

    public static String visibleMain() {
        return main.getText().trim();
    }

    @FindBy(css = "._tutsItemWrapper_4b6t0_24")
    private static WebElement firstVideo;

    @FindBy(css = "._activityElement_15bqo_21")
    private static WebElement elementPage;

    public static void clickFirstVideo() {
        firstVideo.click();
        elementPage.isDisplayed();
    }

    @FindBy(css = ".swiper-button-next")
    private static WebElement nextButton;

    @FindBy(css = ".swiper-button-prev")
    private static WebElement prevButton;

    public static void clickBtn(){
        nextButton.click();
        prevButton.click();
    }

    @FindBy(css = ".sc-fggMnX.kGiokb > div > div > div._activityBlock_15bqo_1 > div > button")
    private static WebElement moreDetails;

    public static void clickDetails(){
        System.out.println(moreDetails.isDisplayed());
        if (true){
            moreDetails.click();
        } else {
            nextButton.click();
            moreDetails.click();
        }
    }
}
