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

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

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

    public void setup(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.switchTo().frame(driver.findElement(By.tagName("iframe")));
        Assert.assertEquals("Банковской картой", PayMain.textButton());
        PayMain.clickBankBtn();
        Assert.assertEquals("Номер карты", Card.textCard());
        PayMain.getPay();
        wait.until(elementToBeClickable(By.tagName("iframe")));
        driver.switchTo().frame(driver.findElement(By.tagName("iframe")));
        PayMain.btnSuccess();
    }

    @After
    public void quitDriver() {
        driver.close();
        driver.quit();
        driver = null;
    }

    @Test
    @DisplayName("Registration on website New User + Success Pay")
    public void RegistrationOnWebsite(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.get("https://client.dev.tutorplace.ru/register");
        LoginAndPass.registrationNewUser();
        wait.until(numberOfElementsToBeMoreThan(By.tagName("button"),240));
        System.out.println(modal.modalWelcomeForNewUser());
        System.out.println(modal.modalTelephone());
        System.out.println(modal.modalDailyCall());
        driver.get("https://client.dev.tutorplace.ru/user/subscribe");
        System.out.println(modal.modalTelephone());
        System.out.println(modal.modalDailyCall());
        PayMain.getAccess();
        wait.until(numberOfWindowsToBe(2));
        Object[] windowHandles = driver.getWindowHandles().toArray();
        System.out.println(driver.getWindowHandles());
        driver.switchTo().window((String) windowHandles[1]);
        String title = driver.getTitle();
        Assert.assertEquals("Покупка доступа TutorPlace", title);
        Card.setCard();
        driver.getWindowHandles();
        wait.until(elementToBeClickable(By.tagName("iframe")));
        driver.switchTo().frame(0);
        Card.getSuccess();
        driver.switchTo().window((String) windowHandles[0]);
        String title0 = driver.getTitle();
        Assert.assertEquals("Оформить подписку | TutorPlace", title0);
    }

    @Test
    @DisplayName("Success Pay from Excel page")
    public void SuccessPayFromExcelPage(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.get("https://dev.tutorplace.ru/catalog/product/excel");
        wait.until(elementToBeClickable(By.cssSelector(".font-inter")));
//        driver.findElements(By.tagName(".font-inter")).get(0).click();
        PayMain.clickBtnStart();
        PayMain.getDostup();
        wait.until(elementToBeClickable(By.tagName("iframe")));
//        System.out.println(driver.findElements(By.tagName("iframe")).get(0).getDomAttribute("src"));
        iframe.getIframe(driver);
        driver.switchTo().defaultContent();
        driver.switchTo().frame(driver.findElement(By.tagName("iframe")));
        wait.until(elementToBeClickable(By.cssSelector("payment-module-frontend-payment-module-entry > pm-success > div > button > div"))).click();
        Object[] windowHandles = driver.getWindowHandles().toArray();
        driver.getWindowHandles();
        driver.switchTo().window((String) windowHandles[0]);
        wait.until(numberOfElementsToBeMoreThan(By.tagName("a"),33));
        System.out.println(driver.findElements(By.tagName("a")).get(3).getText());

        driver.findElements(By.tagName("a")).get(3).click();
        Object[] windowHandles1 = driver.getWindowHandles().toArray();
        driver.getWindowHandles();
        driver.switchTo().window((String) windowHandles1[0]);
    }
}
