package ru.tutor.page;

import com.codeborne.selenide.SelenideElement;
import com.github.dockerjava.api.model.Driver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
//import static com.codeborne.selenide.Selenide.*;
//import static com.codeborne.selenide.Selenide.closeWebDriver;

public class ClientLite extends Driver {

    public ClientLite(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = ".sc-jXbUNg.erLqJb")
    private static WebElement change_control;

//    private static final SelenideElement change_control = (SelenideElement) $$("div.sc-knbyxe.eHmTwv > div > button");

    public static String text(){
        return change_control.getText().trim();
    }

    public static void getChangeLite() {
        change_control.isDisplayed();
        change_control.click();
    }

    @FindBy(css = ".sc-edcLgS.fGCbex")
    private static WebElement textLite;

        public static String getTextLite() {
            textLite.isDisplayed();
            return textLite.getText().trim();
    }
}
