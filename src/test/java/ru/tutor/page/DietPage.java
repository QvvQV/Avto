package ru.tutor.page;

import com.github.dockerjava.api.model.Driver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.Collections;
import java.util.List;

public class DietPage extends Driver {

    public DietPage(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    // первая страница ленда
    @FindBy(xpath = "//*[@id=\"root\"]/div[2]/div/div/div[2]/div[1]/div[1]/div/h2")
    private static WebElement h2_1;

    public static String setH2_1() {
        return h2_1.getText().trim();
    }

    @FindBy(xpath = "//*[@id=\"root\"]/div[2]/div/div/div[2]/div[1]/div[1]/div/p")
    private static WebElement p_1;

    public static String setP_1() {
        return p_1.getText().trim();
    }

    @FindBy(css = ".sc-gFqAkR")
    private static WebElement btn;

    public static String textBtn() {
        return btn.getText().trim();
    }

    public static void click() {
        btn.click();
    }

    @FindBy(xpath = "//*[@id=\"root\"]/div[2]/div/div/div[2]/div[3]/button[2]")
    private static WebElement next;

    public static void btnNext() {
        next.click();
    }

    // вторая страница ленда

    @FindBy(xpath = "//*[@id=\"root\"]/div[2]/div/div/div[2]/div[1]/div[2]/div/h2")
    private static WebElement h2_2;

    public static String setH2_2() {
        return h2_2.getText().trim();
    }

    @FindBy(xpath = "//*[@id=\"root\"]/div[2]/div/div/div[2]/div[1]/div[2]/div/p")
    private static WebElement p_2;

    public static String setP_2() {
        return p_2.getText().trim();
    }

    // третья страница ленда

    @FindBy(xpath = "//*[@id=\"root\"]/div[2]/div/div/div[2]/div[1]/div[3]/div/h2")
    private static WebElement h2_3;

    public static String setH2_3() {
        return h2_3.getText().trim();
    }

    @FindBy(xpath = "//*[@id=\"root\"]/div[2]/div/div/div[2]/div[1]/div[3]/div/p")
    private static WebElement p_3;

    public static String setP_3() {
        return p_3.getText().trim();
    }
    // четвертая страница ленда

    @FindBy(xpath = "//*[@id=\"root\"]/div[2]/div/div/div[2]/div[1]/div[4]/div/h2")
    private static WebElement h2_4;

    public static String setH2_4() {
        return h2_4.getText().trim();
    }

    @FindBy(xpath = "//*[@id=\"root\"]/div[2]/div/div/div[2]/div[1]/div[4]/div/p")
    private static WebElement p_4;

    public static String setP_4() {
        return p_4.getText().trim();
    }

    //        List<WebElement> h2 = driver.findElements(By.cssSelector(".sc-jdkBTo"));
//        System.out.println(h2);
}
