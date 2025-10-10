package ru.tutor.utils;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.Wait;

public class WaitHelper {

    public static void waitForPageLoad(){
        Wait().withTimeout(Duration.ofSeconds(30)).until(driver ->
                ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));
    }

    public static void waitForUrlContains(String expectedText, Duration timeout){
        WebDriverWait wait = new WebDriverWait(WebDriverRunner.getWebDriver(), timeout);
        wait.until(driver -> driver.getCurrentUrl().contains(expectedText));
    }

    public static void waitForPaymentProcessing(){
        waitForUrlContains("/success/", Duration.ofSeconds(20));
    }
}


