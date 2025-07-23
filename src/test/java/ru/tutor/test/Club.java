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

public class Club {

    public static WebDriver driver;

    public static String url = "https://client.ttplace.ru/user/member-club/requisite";

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
        ClubPage clubPage = new ClubPage(driver);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(7));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
    }

    @After
    public void quitDriver() {
        driver.close();
        driver.quit();
    }

    @Test
    @DisplayName("Club Success From Self Employd")
    public void ClubSuccessFromSelfEmployd() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        iframe.BeforeRegistrationStatic(driver);
        driver.get(url);
        wait.until(elementToBeClickable(By.cssSelector("._Radio_1o11b_1")));
        Assert.assertEquals("Выберите тип налогообложения", ClubPage.getAllFields());
        ClubPage.inpullAllFields(driver);
        Assert.assertEquals("ОГРНИП", ClubPage.atrOgrip());
    }

    @Test
    @DisplayName("Club Success From Individ Entrep")
    public void ClubSuccessFromIndividEntrep() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        iframe.BeforeRegistrationStatic(driver);
        driver.get(url);
        wait.until(elementToBeClickable(By.cssSelector("._Radio_1o11b_1")));
        Assert.assertEquals("Выберите тип налогообложения", ClubPage.getAllFields());
        ClubPage.inpullAllFields1(driver);
        Assert.assertEquals("ИНН", ClubPage.atrInputInn());
    }

    @Test
    @DisplayName("Club Error Sz")
    public void ClubErrorSz() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.manage().window().setSize(new Dimension(1920, 1080));
        iframe.BeforeRegistrationStatic(driver);
        driver.get(url);
        wait.until(elementToBeClickable(By.cssSelector("._Radio_1o11b_1")));
        ClubPage.errorSz();
    }

    @Test
    @DisplayName("Club Error Ip")
    public void ClubErrorIP() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.manage().window().setSize(new Dimension(1920, 1080));
        iframe.BeforeRegistrationStatic(driver);
        driver.get(url);
        wait.until(elementToBeClickable(By.cssSelector("._Radio_1o11b_1")));
        ClubPage.errorIP();
    }
}