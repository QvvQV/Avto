package ru.tutor.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.tutor.page.*;

import java.time.Duration;

import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;

public class PaySuccessfull {

    public static WebDriver driver;
//    public static String url = "https://dev.tutorplace.ru/catalog/product/excel";
//    https://client.dev.tutorplace.ru/login

    @Before
    public void setupAll() {
        driver = new ChromeDriver();
        ClientPrime Client = new ClientPrime(driver);
        PayMain Pay = new PayMain(driver);
        LoginAndPass loginAndPass = new LoginAndPass(driver);
        modal modal = new modal(driver);
        Card card = new Card(driver);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(7));
        driver.manage().window().setSize(new Dimension(1920, 1080));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
//        driver.get(url);
    }

    @After
    public void quitDriver() {
        driver.close();
        driver.quit();
        driver = null;
    }

//    T-Bank

    @Test
    @DisplayName("Registration on website New User + Success Pay")
    public void RegistrationOnWebsite() {
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.get("https://client.dev.tutorplace.ru/register");
        LoginAndPass.registrationNewUser();
        iframe.getIframeForUserRegistration(driver);
        String title0 = driver.getTitle();
        Assert.assertEquals("Оформить подписку | TutorPlace", title0);
    }

    @Test
    @DisplayName("Success Pay from Excel page")
    public void SuccessPayFromExcelPage() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.get("https://dev.tutorplace.ru/catalog/product/excel");
//        driver.get("https://dev.tutorplace.ru/catalog/product/excel?aff_rid=1012_EN5Q");
//        driver.get("https://dev.tutorplace.ru/catalog/product/excel?aff_rid=1012_EN5S");
//        driver.get("https://dev.tutorplace.ru/catalog/product/excel?aff_rid=1012_EN5U");
        wait.until(elementToBeClickable(By.cssSelector(".font-inter")));
        PayMain.clickBtnStart();
        PayMain.getDostup();
        iframe.getIframeForLand(driver);
    }

//    https://dev.tutorplace.ru/catalog/product/excel?aff_rid=1012_EN5Q - RevShare
//https://dev.tutorplace.ru/catalog/product/excel?aff_rid=1012_EN5S - Гибрид
//https://dev.tutorplace.ru/catalog/product/excel?aff_rid=1012_EN5U - CPA

//
//    Sber
        @Test
    @DisplayName("Success Pay from Excel page from Sber")
    public void SuccessPayFromExcelPageFromSber(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        driver.get("https://dev.tutorplace.ru/catalog/product/excel");
        wait.until(elementToBeClickable(By.cssSelector(".font-inter")));
        PayMain.clickBtnStart();
        PayMain.getDostup();
        iframe.getHandlesFromPageExcelCardSber(driver);
    }
}
