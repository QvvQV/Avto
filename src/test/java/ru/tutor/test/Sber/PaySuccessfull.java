package ru.tutor.test.Sber;

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
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

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

    @After
    public void quitDriver() {
        driver.close();
        driver.quit();
        driver = null;
    }

    @Test
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

    @Test
    @DisplayName("Success Pay from Excel page from Sber")
    public void SuccessPayFromExcelPageFromSber() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        driver.get("https://admin:JWwppsEA84B4BozJgE44sNiZZ@ttplace.ru/catalog/product/excel");
        wait.until(elementToBeClickable(By.cssSelector(".font-inter")));
        PayMain.clickBtnStart();
        PayMain.getDostup();
        System.out.println("Оплата с продукта Excel за 1 руб: " + LoginAndPass.textEmail());
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".order_offer__vyM7e > form > p")));
        iframe.getHandlesFromPageExcelCardSber(driver);
    }

    @Test
    @DisplayName("Registration on main 1 rub")
    public void RegistrationOnMain1Rub() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.get("https://admin:JWwppsEA84B4BozJgE44sNiZZ@ttplace.ru");
        wait.until(visibilityOfElementLocated(By.cssSelector(".form_form__kHJJ8")));
        LoginAndPass.inputEmail();
        System.out.println("Оплата с главной за 1 руб: " + LoginAndPass.textEmail());
        PayMain.setBtnOpen1Rub();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".form_wrapper__P2PxW > form > p.mt-\\[-20px\\].text-\\[16px\\].text-red-500.text-center")));
        iframe.getHandlesFromPageExcelCardSber(driver);
    }

    @Test
    @DisplayName("Registration on main 99 rub")
    public void RegistrationOnMain99Rub() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.get("https://admin:JWwppsEA84B4BozJgE44sNiZZ@ttplace.ru");
        wait.until(visibilityOfElementLocated(By.cssSelector(".form_form__kHJJ8")));
        PayMain.clickBuyLiteMain(driver);
        wait.until(visibilityOfElementLocated(By.id("radix-:Rrah4flb:")));
        LoginAndPass.getEmailFromLite();
        System.out.println("Оплата с главной за 99 руб: " + LoginAndPass.textEmail());
        PayMain.setBtnOpen99Rub();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".buy-modal_btnGroups__l9_cj > p")));
        iframe.getHandlesFromPageExcelCardSber(driver);
    }

    @Test
    @DisplayName("Registration on main 399 rub")
    public void RegistrationOnMain399Rub() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.get("https://admin:JWwppsEA84B4BozJgE44sNiZZ@ttplace.ru");
        wait.until(visibilityOfElementLocated(By.cssSelector(".form_form__kHJJ8")));
        PayMain.clickBuyPremiumMain(driver);
        wait.until(visibilityOfElementLocated(By.id("radix-:Rrih4flb:")));
        LoginAndPass.getEmailFromLite();
        System.out.println("Оплата с главной за 399 руб: " + LoginAndPass.textEmail());
        PayMain.setBtnOpen99Rub();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".buy-modal_btnGroups__l9_cj > p")));
        iframe.getHandlesFromPageExcelCardSber(driver);
    }

    @Test
    @DisplayName("Registration on main 3990 rub")
    public void RegistrationOnMain3990Rub() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.get("https://admin:JWwppsEA84B4BozJgE44sNiZZ@ttplace.ru");
        wait.until(visibilityOfElementLocated(By.cssSelector(".form_form__kHJJ8")));
        PayMain.clickBuyAllMain(driver);
        wait.until(visibilityOfElementLocated(By.id("radix-:Rrqh4flb:")));
        LoginAndPass.getEmailFromLite();
        System.out.println("Оплата с главной за 3990 руб: " + LoginAndPass.textEmail());
        PayMain.setBtnOpen99Rub();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".buy-modal_btnGroups__l9_cj > p")));
        iframe.getHandlesFromPageExcelCardSber(driver);
    }

    @Test
    @DisplayName("Registration on main 3990 rub Shares")
    public void RegistrationOnMain3990RubShares() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.get("https://admin:JWwppsEA84B4BozJgE44sNiZZ@ttplace.ru");
        wait.until(visibilityOfElementLocated(By.cssSelector(".form_form__kHJJ8")));
        PayMain.clickBuyAllMain(driver);
        wait.until(visibilityOfElementLocated(By.id("radix-:Rrqh4flb:")));
        LoginAndPass.getEmailFromLite();
        System.out.println("Оплата с главной за 3990 руб Долями: " + LoginAndPass.textEmail());
        PayMain.setBtnOpenShares();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".buy-modal_btnGroups__l9_cj > p")));
        iframe.getHandlesFromPageExcelCardSber(driver);
    }

//    @Test
//    @DisplayName("email")
//    public void email(){
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
//        driver.get("https://www.fakemail.net/");
//        wait.until(visibilityOf(LoginAndPass.mail));
//
//
//        String nameEmails = LoginAndPass.mail();
////        System.out.println(nameEmail.getCssValue("//div[@data-email]"));
//        System.out.println(nameEmails);
//        driver.get("https://admin:JWwppsEA84B4BozJgE44sNiZZ@ttplace.ru/catalog/product/excel");
//         wait.until(elementToBeClickable(By.cssSelector(".font-inter")));
//        PayMain.clickBtnStart();
//        PayMain.getMailDostup();

//        List<> nameEmails = new ArrayList<>();

}