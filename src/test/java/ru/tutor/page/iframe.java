package ru.tutor.page;

import com.github.dockerjava.api.model.Driver;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

//import static ru.tutor.test.PaySuccessfullDietPit.driver;
//import static ru.tutor.test.PaySuccessfullLite.driver;

public class iframe extends Driver {

    public iframe(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    public static void getIframe(WebDriver driver){
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

        public static void BeforeRegistration(WebDriver driver) {
        driver.get("https://client.dev.tutorplace.ru/login");
        LoginAndPass.registrationUser();
        System.out.println(modal.modalTelephone());
        System.out.println(modal.modalDailyCall());
    }
}
