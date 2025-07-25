package ru.tutor.test.Sber;

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

public class SberError {

    public static WebDriver driver;
    public static String url = "https://admin:JWwppsEA84B4BozJgE44sNiZZ@ttplace.ru/catalog/product/excel";
//    https://client.dev.tutorplace.ru/login

    @Before
    public void setupAll() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        driver = new ChromeDriver(options);
        ClientPrime Client = new ClientPrime(driver);
        PayMain Pay = new PayMain(driver);
        LoginAndPass loginAndPass = new LoginAndPass(driver);
        modal modal = new modal(driver);
        Card card = new Card(driver);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(7));
        driver.manage().window().setSize(new Dimension(1920, 1080));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.get(url);
    }

    @After
    public void quitDriver() {
        driver.close();
    }

    @Test
    @DisplayName("No Money Pay from Excel page from Sber")
    public void NoMoneyPayFromExcelPageFromSber116() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        driver.get(url);
        wait.until(elementToBeClickable(By.cssSelector(".font-inter")));
        PayMain.clickBtnStart();
        PayMain.getDostup();
        System.out.println("Оплата с продукта Excel за 1 руб: " + LoginAndPass.textEmail());
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".order_offer__vyM7e > form > p")));
        iframe.getHandlesFromPageExcelCardSber116(driver);
        Assert.assertEquals("Операция отклонена. Проверьте введенные данные, достаточность средств на карте и повторите операцию", driver.findElements(By.cssSelector("._3CECTWvsozxJbOg_iC0zGe")).get(0).getText());
    }

    @Test
    @DisplayName("Overdue Pay from Excel page from Sber")
    public void OverduePayFromExcelPageFromSber101() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        driver.get(url);
        wait.until(elementToBeClickable(By.cssSelector(".font-inter")));
        PayMain.clickBtnStart();
        PayMain.getDostup();
        System.out.println("Оплата с продукта Excel за 1 руб: " + LoginAndPass.textEmail());
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".order_offer__vyM7e > form > p")));
        iframe.getHandlesFromPageExcelCardSber101(driver);
        Assert.assertEquals("Операция отклонена. Проверьте введенные данные, достаточность средств на карте и повторите операцию", driver.findElements(By.cssSelector("._3CECTWvsozxJbOg_iC0zGe")).get(0).getText());
    }

    @Test
    @DisplayName("PaymentSuccessWith100Rub")
    public void PaymentSuccessWith100Rub() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        driver.get(url);
        wait.until(elementToBeClickable(By.cssSelector(".font-inter")));
        PayMain.clickBtnStart();
        PayMain.getDostup();
        System.out.println("Оплата с продукта Excel за 1 руб на карте 100р: " + LoginAndPass.textEmail());
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".order_offer__vyM7e > form > p")));
        iframe.getHandlesFromPageExcelCardSberWith100Rub(driver);
    }

}
