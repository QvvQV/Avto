package ru.tutor.test.tuts_tests_87;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.tutor.page.*;

import java.time.Duration;

import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.numberOfElementsToBeMoreThan;

public class add_kurs_from_video {

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
        Tuts tuts  = new Tuts(driver);
        Product product = new Product(driver);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(7));
        driver.manage().window().setSize(new Dimension(1020, 980));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
//        driver.get(url);
    }

    @After
    public void quitDriver() {
        driver.close();
        driver.quit();
        driver = null;
    }

//    Добавление курса из видео материала тутса

    @Test
    @DisplayName("Добавление курса из видео материала тутса")
    public void addKursFromVideo() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.get("https://client.ttplace.ru/login");
        LoginAndPass.registrationUser();
        wait.until(numberOfElementsToBeMoreThan(By.tagName("a"), 14));
        Assert.assertEquals("Главная", Tuts.visibleMain());
        Tuts.clickFirstVideo();
        Tuts.clickBtn();
        Tuts.clickDetails();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("._Button_kio3a_1._fullWidth_kio3a_195")));
        Product.setClickAdd();
        Product.clickGo();
        Assert.assertEquals("Начать урок", Product.btnStartLesson());

//        String title0 = driver.getTitle();
//        Assert.assertEquals("Оформить подписку | TutorPlace", title0);
    }

    @Test
    @DisplayName("Success Pay from Excel page")
    public void SuccessPayFromExcelPage() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.get("https://ttplace.ru/catalog/product/excel");
//        driver.get("https://dev.tutorplace.ru/catalog/product/excel?aff_rid=1012_EN5U");
//        driver.get("https://dev.tutorplace.ru/catalog/product/excel?aff_rid=1012_EN5S");
//        driver.get("https://dev.tutorplace.ru/catalog/product/excel?aff_rid=1012_EN5Q");

        wait.until(elementToBeClickable(By.cssSelector(".font-inter")));
        PayMain.clickBtnStart();
        PayMain.getDostup();
        iframe.getIframeForLand(driver);
    }

    //https://dev.tutorplace.ru/catalog/product/excel?aff_rid=1012_EN5U - CPA
    //https://dev.tutorplace.ru/catalog/product/excel?aff_rid=1012_EN5S - Гибрид
//    https://dev.tutorplace.ru/catalog/product/excel?aff_rid=1012_EN5Q - RevShare

//    Sber
    @Test
    @DisplayName("Success Pay from Excel page from Sber")
    public void SuccessPayFromExcelPageFromSber() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        driver.get("https://dev.tutorplace.ru/catalog/product/excel");

//        driver.get("https://dev.tutorplace.ru/catalog/product/excel?aff_rid=1012_EN5U");
//        driver.get("https://dev.tutorplace.ru/catalog/product/excel?aff_rid=1012_EN5S");
//        driver.get("https://dev.tutorplace.ru/catalog/product/excel?aff_rid=1012_EN5Q");
        wait.until(elementToBeClickable(By.cssSelector(".font-inter")));
        PayMain.clickBtnStart();
        PayMain.getDostup();
        iframe.getHandlesFromPageExcelCardSber(driver);
    }
}
