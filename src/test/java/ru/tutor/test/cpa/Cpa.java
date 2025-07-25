package ru.tutor.test.cpa;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.tutor.page.*;

import java.time.Duration;

import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.numberOfElementsToBeMoreThan;

public class Cpa {

    public static WebDriver driver;
//    public static String url = "https://dev.tutorplace.ru/catalog/product/excel";
//    https://client.dev.tutorplace.ru/login

    //https://dev.tutorplace.ru/catalog/product/excel?aff_rid=1012_EN5U - CPA

    @Before
    public void setupAll() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        driver = new ChromeDriver();
        ClientPrime Client = new ClientPrime(driver);
        PayMain Pay = new PayMain(driver);
        LoginAndPass loginAndPass = new LoginAndPass(driver);
        modal modal = new modal(driver);
        Card card = new Card(driver);
        devAdmin devAdmin = new devAdmin(driver);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(7));
        driver.manage().window().setSize(new Dimension(1920, 1080));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
//        driver.get(url);
    }

    @After
    public void quitDriver() {
        driver.close();
    }

    @Test
    @DisplayName("CPA successful")
    public void CPA_successful() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        driver.get("https://admin:JWwppsEA84B4BozJgE44sNiZZ@ttplace.ru/catalog/product/excel?aff_rid=1012_ENpc");
        wait.until(elementToBeClickable(By.cssSelector(".font-inter")));
        PayMain.clickBtnStart();
        PayMain.getDostup();
        System.out.println("Оплата с продукта Excel за 1 руб CPA RECURRING: " + LoginAndPass.textEmail());
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".order_offer__vyM7e > form > p")));
        iframe.getIframeForLand(driver);
        driver.get("https://admin.ttplace.ru/");
        devAdmin.workWithOrder(driver);
        Assert.assertEquals("290,00 ₽", driver.findElements(By.cssSelector(".tableRow__item")).get(35).getText());
        Assert.assertEquals("Успешный", driver.findElements(By.cssSelector(".tableRow__item.tableRow__item--m")).get(2).getText());
        Assert.assertEquals("RECURRING", driver.findElements(By.cssSelector(".tableRow__item")).get(34).getText());
    }

    @Test
    @DisplayName("CPA successful Refund Admin")
    public void CPA_successful_refund_admin() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        driver.get("https://admin:JWwppsEA84B4BozJgE44sNiZZ@ttplace.ru/catalog/product/excel?aff_rid=1012_ENpc");
        wait.until(elementToBeClickable(By.cssSelector(".font-inter")));
        PayMain.clickBtnStart();
        PayMain.getDostup();
        System.out.println("Оплата с продукта Excel за 1 руб CPA REFUND: " + LoginAndPass.textEmail());
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".order_offer__vyM7e > form > p")));
        iframe.getIframeForLand(driver);
        driver.get("https://admin.ttplace.ru/");
        devAdmin.workWithOrder(driver);
        devAdmin.changePaymentFromAdmin(driver);
        Assert.assertEquals("-290,00 ₽", driver.findElements(By.cssSelector(".tableRow__item")).get(65).getText());
        Assert.assertEquals("REFUND", driver.findElements(By.cssSelector(".tableRow__item")).get(64).getText());
    }
//
//    @Test
//    @DisplayName("PaymentSuccessWith100Rub")
//    public void PaymentSuccessWith100Rub() {
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
//        driver.get("https://dev.tutorplace.ru/catalog/product/excel");
//        wait.until(elementToBeClickable(By.cssSelector(".font-inter")));
//        PayMain.clickBtnStart();
//        PayMain.getDostup();
//        iframe.getHandlesFromPageExcelCardSberWith100Rub(driver);
//    }

}
