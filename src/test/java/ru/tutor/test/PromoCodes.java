package ru.tutor.test;

import org.junit.After;
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

public class PromoCodes {

    public static WebDriver driver;

//    public void BeforeRegistration() {
//        driver.get("https://client.dev.tutorplace.ru/login");
//        LoginAndPass.registrationUser();
//        System.out.println(modal.modalTelephone());
//        System.out.println(modal.modalDailyCall());
//    }

    public void doubleClick() {
        WebElement clickable = driver.findElement(By.cssSelector(".sc-cwHptR"));
        new Actions(driver)
                .doubleClick(clickable)
                .perform();
        Product.Clear();
    }

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
        PromoCode promoCode = new PromoCode(driver);
//        iframe iframe = new iframe(driver);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(7));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
//        driver.get(url);
    }

    @After
    public void quitDriver() {
        driver.close();
    }


    @Test
    @DisplayName("Inactive promo code for Lite")
    public void InactivePromoCode() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        iframe.BeforeRegistration(driver);
        driver.get("https://client.dev.tutorplace.ru/user/settings/profile");
        wait.until(elementToBeClickable(By.cssSelector(".sc-cwHptR")));
        System.out.println(PromoCode.setBtnPromo());
        doubleClick();
        System.out.println(PromoCode.setBtnPromo1());
        doubleClick();
        System.out.println(PromoCode.setBtnPromo2());
        doubleClick();
        System.out.println(PromoCode.setBtnPromo3());
        doubleClick();
        System.out.println(PromoCode.setBtnPromo4());
        doubleClick();
        System.out.println(PromoCode.setBtnPromo5());
        doubleClick();
        System.out.println(PromoCode.setBtnPromo6());
        doubleClick();
        System.out.println(PromoCode.setBtnPromo7());
        doubleClick();
        System.out.println(PromoCode.setBtnPromo8());
        doubleClick();
        System.out.println(PromoCode.setBtnPromo9());
        doubleClick();
        System.out.println(PromoCode.setBtnPromo10());
        doubleClick();
        System.out.println(PromoCode.setBtnPromo11());
        doubleClick();
        System.out.println(PromoCode.setBtnPromo12());
        doubleClick();
        System.out.println(PromoCode.setBtnPromo14());
        doubleClick();
        System.out.println(PromoCode.setBtnPromo15());
        doubleClick();
        System.out.println(PromoCode.setBtnPromo16());
        doubleClick();
        System.out.println(PromoCode.setBtnPromo17());
        doubleClick();
        System.out.println(PromoCode.setBtnPromo18());
        doubleClick();
        System.out.println(PromoCode.setBtnPromo19());
        doubleClick();
        System.out.println(PromoCode.setBtnPromo22());
        doubleClick();
        System.out.println(PromoCode.setBtnPromo23());
        doubleClick();
        System.out.println(PromoCode.setBtnPromo24());
        doubleClick();
        System.out.println(PromoCode.setBtnPromo25());
        doubleClick();
        System.out.println(PromoCode.setBtnPromo26());
        doubleClick();
        System.out.println(PromoCode.setBtnPromo27());
        doubleClick();
        System.out.println(PromoCode.setBtnPromo28());
        doubleClick();
        System.out.println(PromoCode.setBtnPromo35());
    }

    @Test
    @DisplayName("Active promo code for Lite")
    public void ActivePromoCode() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        iframe.BeforeRegistration(driver);
        driver.get("https://client.dev.tutorplace.ru/user/settings/profile");
        wait.until(elementToBeClickable(By.cssSelector(".sc-cwHptR")));
        System.out.println(PromoCode.setBtnPromo29());
        doubleClick();
        System.out.println(PromoCode.setBtnPromo30());
        doubleClick();
        System.out.println(PromoCode.setBtnPromo31());
        doubleClick();
        System.out.println(PromoCode.setBtnPromo32());
        doubleClick();
        System.out.println(PromoCode.setBtnPromo33());
        doubleClick();
        System.out.println(PromoCode.setBtnPromo34());
        doubleClick();
        System.out.println(PromoCode.setBtnPromo34());
        doubleClick();
        System.out.println(PromoCode.setBtnPromo35());
        doubleClick();
    }

        @Test
    @DisplayName("Inactive promo code for Lite List")
    public void InactivePromoCodeList() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        iframe.BeforeRegistration(driver);
        driver.get("https://client.dev.tutorplace.ru/user/settings/profile");
        wait.until(elementToBeClickable(By.cssSelector(".sc-cwHptR")));
        System.out.println(PromoCode.setBtnPromo());
        doubleClick();
}
}