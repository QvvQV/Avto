package ru.tutor.test.Hybrid;

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

public class Hybrid {

    public static WebDriver driver;
//    public static String url = "https://dev.tutorplace.ru/catalog/product/excel";
//    https://client.dev.tutorplace.ru/login

    //https://dev.tutorplace.ru/catalog/product/excel?aff_rid=1012_EN5S - Hybrid

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
    @DisplayName("Hybrid successful")
    public void Hybrid_successful() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        driver.get("https://admin:JWwppsEA84B4BozJgE44sNiZZ@ttplace.ru/catalog/product/excel?aff_rid=1012_EHNq");                     //переход по гибридной ссылке
        wait.until(elementToBeClickable(By.cssSelector(".font-inter")));                                     //ожидание появления кнопки начать обучение
        PayMain.clickBtnStart();                                                                             //кликнуть по кнопке
        PayMain.getDostup();                                                                                // ожидание на появления поля для ввода почты
        System.out.println("Оплата с продукта Excel за 1 руб Hybrid RECURRING: " + LoginAndPass.textEmail());
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".order_offer__vyM7e > form > p")));
        iframe.getIframeForLand(driver); //заполнение iframe валидными данными тестовой карты clouda + заход в ЛК с заменой пароля
        driver.get("https://admin.ttplace.ru/");                                                      //переход в админку deva
        devAdmin.workWithOrderHybrid(driver);                                                                //работа в админке с созданным заказом
        Assert.assertEquals("290,00 ₽", driver.findElements(By.cssSelector(".tableRow__item")).get(50).getText());
        Assert.assertEquals("Успешный", driver.findElements(By.cssSelector(".tableRow__item.tableRow__item--m")).get(3).getText());
        Assert.assertEquals("RECURRING", driver.findElements(By.cssSelector(".tableRow__item")).get(49).getText());
    }


    @Test
    @DisplayName("Hybrid successful Refund Admin")
    public void Hybrid_successful_refund_admin() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        driver.get("https://admin:JWwppsEA84B4BozJgE44sNiZZ@ttplace.ru/catalog/product/excel?aff_rid=1012_EHNq");
        wait.until(elementToBeClickable(By.cssSelector(".font-inter")));
        PayMain.clickBtnStart();
        PayMain.getDostup();
        System.out.println("Оплата с продукта Excel за 1 руб Hybrid PREPAID_UNSUBSCRIBE: " + LoginAndPass.textEmail());
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".order_offer__vyM7e > form > p")));
        iframe.getIframeForLand(driver);
        driver.get("https://admin.ttplace.ru/");
        devAdmin.workWithOrderHybrid(driver);
        devAdmin.change_subscription(driver);
        Assert.assertEquals("327,00 ₽", driver.findElements(By.cssSelector(".tableRow__item")).get(65).getText());
        Assert.assertEquals("PREPAID_UNSUBSCRIBE", driver.findElements(By.cssSelector(".tableRow__item")).get(64).getText());
    }

//    @Test
//    @DisplayName("Overdue Pay from Excel page from Sber")
//    public void OverduePayFromExcelPageFromSber101() {
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
//        driver.get("https://dev.tutorplace.ru/catalog/product/excel");
//        wait.until(elementToBeClickable(By.cssSelector(".font-inter")));
//        PayMain.clickBtnStart();
//        PayMain.getDostup();
//        iframe.getHandlesFromPageExcelCardSber101(driver);
//        Assert.assertEquals("Операция отклонена. Проверьте введенные данные, достаточность средств на карте и повторите операцию", driver.findElements(By.cssSelector("._3CECTWvsozxJbOg_iC0zGe")).get(0).getText());
//    }
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
