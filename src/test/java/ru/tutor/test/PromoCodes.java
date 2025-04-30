package ru.tutor.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.tutor.page.*;

import java.time.Duration;

import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;

public class PromoCodes {

    public static WebDriver driver;


    @Before
    public void setupAll() {
        driver = new ChromeDriver();
        ClientPrime Client = new ClientPrime(driver);
        PayMain Pay = new PayMain(driver);
        LoginAndPass loginAndPass = new LoginAndPass(driver);
        ClientLite clientLite = new ClientLite(driver);
        modal modal = new modal(driver);
        Card card = new Card(driver);
        Product product = new Product(driver);
        PromoCode promoCode = new PromoCode(driver);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(7));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
//        driver.get(url);
    }

    @After
    public void quitDriver() {
        driver.close();
        driver.quit();
    }

    @Test
    @DisplayName("Active promo code for Lite")
    public void ActivePromoCode() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        iframe.BeforeRegistration(driver);
        driver.get("https://client.dev.tutorplace.ru/user/settings/profile");
        wait.until(elementToBeClickable(By.cssSelector(".sc-cwHptR")));
        PromoCode.PromoActive(driver);
    }

    @Test
    @DisplayName("Inactive promo code for Lite List")
    public void InactivePromoCodeList() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        iframe.BeforeRegistration(driver);
        driver.get("https://client.dev.tutorplace.ru/user/settings/profile");
        wait.until(elementToBeClickable(By.cssSelector(".sc-cwHptR")));
        PromoCode.PromoNoActive(driver);
    }
}