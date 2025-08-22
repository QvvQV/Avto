package ru.tutor.test.Sber;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
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
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

public class PaySuccessfull {

    public static WebDriver driver;
//    public static String url = "https://dev.tutorplace.ru/catalog/product/excel";
//    https://client.dev.tutorplace.ru/login

    @BeforeEach
//    @Before
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
        SelenideLogger.addListener("allure.properties", new AllureSelenide());
//        driver.get(url);
    }

    @AfterEach
//    @After
    public void quitDriver() {
        driver.close();
        driver.quit();
        driver = null;
    }

//    @Test
    @DisplayName("Registration on website New User + Success Pay")
    public void RegistrationOnWebsite() {
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.get("https://client.ttplace.ru/register");
        LoginAndPass.registrationNewUser();
        iframe.getIframeForUserRegistration(driver);
        String title0 = driver.getTitle();
        Assert.assertEquals("Оформить подписку | TutorPlace", title0);
    }

    //https://dev.tutorplace.ru/catalog/product/excel?aff_rid=1012_EN5U - CPA
    //https://dev.tutorplace.ru/catalog/product/excel?aff_rid=1012_EN5S - Гибрид
//    https://dev.tutorplace.ru/catalog/product/excel?aff_rid=1012_EN5Q - RevShare

}