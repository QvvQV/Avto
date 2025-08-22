package ru.tutor.page;

import com.github.dockerjava.api.model.Driver;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

public class iframe extends Driver {

    public iframe(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    public static void getIframe(WebDriver driver) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(elementToBeClickable(By.tagName("iframe")));
        driver.switchTo().frame(driver.findElement(By.tagName("iframe")));
        wait.until(visibilityOfElementLocated(By.cssSelector("[data-test=\"cardpay-page-button\"]")));
        Assert.assertEquals("Банковской картой", PayMain.textButton());
        PayMain.clickBankBtn();
        Assert.assertEquals("Номер карты", Card.textCard());
        PayMain.getPay();
        wait.until(elementToBeClickable(By.tagName("iframe")));
        driver.switchTo().frame(driver.findElement(By.tagName("iframe")));
        PayMain.btnSuccess();
        Thread.sleep(1500);
    }

    public static void getIframeFromOpenWeek(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(numberOfElementsToBeMoreThan(By.tagName("iframe"), 1));
        wait.until(elementToBeClickable(By.tagName("iframe")));
        driver.switchTo().frame(1);
        Assert.assertEquals("Банковской картой", PayMain.textButton());
        PayMain.clickBankBtn();
        Assert.assertEquals("Номер карты", Card.textCard());
        PayMain.getPay();
        wait.until(elementToBeClickable(By.tagName("iframe")));
        driver.switchTo().frame(driver.findElement(By.tagName("iframe")));
        PayMain.btnSuccess();
        driver.switchTo().defaultContent();
    }

    public static void BeforeRegistration(WebDriver driver) {
        driver.get("https://client.ttplace.ru/login");
        LoginAndPass.registrationUser();
        System.out.println(modal.modalDailyCall());
        System.out.println(modal.modalTelephone());
    }

        public static void BeforeRegistrationStatic(WebDriver driver) {
        driver.get("https://client.ttplace.ru/login");
        LoginAndPass.registrationUserStatic();
        System.out.println(modal.modalDailyCall());
        System.out.println(modal.modalTelephone());
    }

    public static void getIframeForLand(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(elementToBeClickable(By.tagName("iframe")));
        driver.switchTo().frame(driver.findElement(By.tagName("iframe")));
        wait.until(visibilityOfElementLocated(By.cssSelector("[data-test=\"cardpay-page-button\"]")));
        Assert.assertEquals("Банковской картой", PayMain.textButton());
        PayMain.clickBankBtn();
        Assert.assertEquals("Номер карты", Card.textCard());
        PayMain.getPay();
        wait.until(elementToBeClickable(By.tagName("iframe")));
        driver.switchTo().frame(driver.findElement(By.tagName("iframe")));
        PayMain.btnSuccess();
        driver.switchTo().defaultContent();
        driver.switchTo().frame(driver.findElement(By.tagName("iframe")));
        wait.until(elementToBeClickable(By.cssSelector("payment-module-frontend-payment-module-entry > pm-success > div > button > div"))).click();
        Object[] windowHandles = driver.getWindowHandles().toArray();
        driver.getWindowHandles();
        driver.switchTo().window((String) windowHandles[0]);
        wait.until(numberOfElementsToBeMoreThan(By.tagName("a"), 15));
//        System.out.println(driver.findElements(By.tagName("a")).get(3).getText());

//        driver.findElements(By.tagName("a")).get(3).click();
//        Object[] windowHandles1 = driver.getWindowHandles().toArray();
//        driver.getWindowHandles();
//        driver.switchTo().window((String) windowHandles1[0]);
//        wait.until(numberOfElementsToBeMoreThan(By.tagName("a"), 15));
//        LoginAndPass.registrationFromExel();
//        modal.modalCross();
//        System.out.println(modal.modalDailyCall());
//        System.out.println(modal.modalTelephone());
    }


    public static void getIframeForUserRegistration(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(numberOfElementsToBeMoreThan(By.tagName("button"), 230));
        System.out.println(modal.modalWelcomeForNewUser());
        System.out.println(modal.modalTelephone());
        System.out.println(modal.modalDailyCall());
        driver.get("https://client.ttplace.ru/user/subscribe");
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
    }

    //    Sber
    public static void getHandlesFromPageExcelCardSber(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        wait.until(visibilityOfElementLocated(By.id("cardNumber")));
        wait.until(numberOfElementsToBeMoreThan(By.tagName("div"), 55));
        Card.setCardSber();
        wait.until(numberOfElementsToBeMoreThan(By.tagName("div"), 14));
        wait.until(numberOfElementsToBeMoreThan(By.tagName("a"), 33));
        System.out.println(driver.findElements(By.tagName("a")).get(3).getText());

        driver.findElements(By.tagName("a")).get(3).click();
        Object[] windowHandles = driver.getWindowHandles().toArray();
        driver.getWindowHandles();
        driver.switchTo().window((String) windowHandles[0]);
        wait.until(numberOfElementsToBeMoreThan(By.tagName("a"), 15));
        LoginAndPass.registrationFromExel();
//        modal.modalCross();
//        System.out.println(modal.modalDailyCall());
//        System.out.println(modal.modalTelephone());
    }
}
