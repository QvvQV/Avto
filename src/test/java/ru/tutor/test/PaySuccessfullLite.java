package ru.tutor.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.tutor.page.*;

import java.time.Duration;

import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

public class PaySuccessfullLite {

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
        ClientLite clientLite = new ClientLite(driver);
        Product product = new Product(driver);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(7));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
//        driver.get(url);
    }

    @After
    public void quitDriver() {
        driver.close();
    }

    @Test
    @DisplayName("Downgrade Premium for Lite")
    public void DowngradePremiumForLite() {
        iframe.BeforeRegistration(driver);
        driver.get("https://client.dev.tutorplace.ru/user/settings/subscription");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(elementToBeClickable(By.tagName("button")));
        System.out.println("Подписка " + ClientLite.text());
        ClientLite.getChangeLite();
        Assert.assertEquals("Тарифный план “Полный доступ” отключен", ClientLite.getTextLite());
    }

    @Test
    @DisplayName("Pay Successfull Lite")
    public void PaySuccessfullLite() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        iframe.BeforeRegistration(driver);
        driver.get("https://client.dev.tutorplace.ru/user/catalog");
        Product.ProductSearch(driver);
        Product.diet();
        modal.setYes();
        wait.until(elementToBeClickable(By.cssSelector("div._actions_1ydyj_87 > button:nth-child(1)")));
        modal.getPremiumFor99();
        iframe.getIframe(driver);
        driver.get("https://client.dev.tutorplace.ru/user/settings/subscription");
        wait.until(elementToBeClickable(By.tagName("button")));
        Assert.assertEquals("Полный доступ", ClientPrime.setTextPremium());
    }

    @Test
    @DisplayName("delete kurs")
    public void DeleteKurs() throws Exception {
        iframe.BeforeRegistration(driver);
        driver.get("https://client.dev.tutorplace.ru/user/catalog");
        Product.AllProductSearch(driver);
        driver.get("https://client.dev.tutorplace.ru/user/my-courses");
        Product.setDeleteAllProduct(driver);
        Assert.assertEquals("продуктов больше нет", Product.setDeleteAllProduct(driver));
//        Product.setDeleteProduct(driver);

    }
}
