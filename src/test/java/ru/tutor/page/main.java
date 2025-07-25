package ru.tutor.page;

import com.github.dockerjava.api.model.Driver;
import com.github.javafaker.Faker;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Locale;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

public class main extends Driver {

    private static final Faker faker = new Faker(new Locale("ru"));

    public main(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = ".header-white")
    private static WebElement labelPlace;

    @FindBy(xpath = "/html/body/main/footer/div/div[1]/section[3]/a[1]")
    private static WebElement footerCompany;

    @FindBy(xpath = "/html/body/main/footer/div/div[1]/section[3]/a[2]")
    private static WebElement footerSubscription;

    @FindBy(xpath = "/html/body/main/footer/div/div[1]/section[3]/a[3]")
    private static WebElement footerContacts;

    @FindBy(xpath = "/html/body/main/footer/div/div[1]/section[2]/a[7]")
    private static WebElement personalDate;

    @FindBy(xpath = "/html/body/main/footer/div/div[1]/section[4]/a[1]")
    private static WebElement webMaster;

    @FindBy(xpath = "/html/body/main/footer/div/div[1]/section[4]/a[2]")
    private static WebElement forAutors;

    @FindBy(xpath = "/html/body/main/footer/div/div[1]/section[4]/a[3]")
    private static WebElement install;

    @FindBy(xpath = "/html/body/main/footer/div/div[1]/section[4]/a[4]")
    private static WebElement newHorizons;

    @FindBy(xpath = "/html/body/main/footer/div/div[1]/section[4]/a[5]")
    private static WebElement vacancies;

    @FindBy(xpath = "/html/body/div[2]/div/a")
    private static WebElement onMain;

    public static void clickLabel() {
        labelPlace.click();
    }

    public static void clickCompany() {
        footerCompany.click();
    }

    public static void clickSubscription() {
        footerSubscription.click();
    }

    public static void clickContacts() {
        footerContacts.click();
    }

    public static void clickWebmaster() {
        webMaster.click();
    }

    public static void clickForAuthors() {
        forAutors.click();
    }

    public static void clickInstall() {
        install.click();
    }

    public static void clickHorizon() {
        newHorizons.click();
    }

    public static void clickVacancies() {
        vacancies.click();
    }

    public static void clickBtn() {
        onMain.click();
    }

    public static void scrollTo(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(0, 5));
        new Actions(driver)
                .scrollToElement(personalDate)
                .perform();
        wait.until(visibilityOfElementLocated(By.xpath("/html/body/main/footer/div/div[1]/section[3]/a[1]")));
    }

    }

