package ru.tutor.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.tutor.page.*;

import java.time.Duration;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

public class PaySuccessfullLite {

    public static WebDriver driver;
//    public static String url = "https://ttplace.ru/catalog/product/excel";
//    https://client.ttplace.ru/login

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
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(7));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
//        driver.get(url);
    }

    @After
    public void quitDriver() {
        driver.close();
    }

    @Test
    @DisplayName("Downgrade Premium for Lite")
    public void DowngradePremiumForLite() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        iframe.BeforeRegistrationStatic(driver);
        driver.get("https://client.ttplace.ru/user/catalog");
        modal.modalTelephone();
        Product.AllProductSearch(driver);
        driver.get("https://client.ttplace.ru/user/settings/subscription");
        wait.until(elementToBeClickable(By.tagName("button")));
        System.out.println("Подписка " + ClientLite.text());
        ClientLite.getChangeLite();
        Assert.assertEquals("Тарифный план “Полный доступ” отключен", ClientLite.getTextLite());
        driver.get("https://client.ttplace.ru/user/main");
        wait.until(visibilityOfElementLocated(By.cssSelector("._header_i1add_15 > h2")));
    }

    @Test
    @DisplayName("Pay Successfull Lite")
    public void PaySuccessfullLite() throws Exception {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        iframe.BeforeRegistrationStatic(driver);
        driver.get("https://client.ttplace.ru/user/catalog");
        modal.modalTelephone();
//        Product.ProductSearch(driver);
        Product.diet(driver);
        wait.until(visibilityOfElementLocated(By.cssSelector("._Button_kio3a_1._fullWidth_kio3a_195")));
        modal.setYes(driver);
        wait.until(elementToBeClickable(By.cssSelector("._actions_1ydyj_87 > button:nth-child(1)")));
        modal.getPremiumFor99();
        iframe.getIframe(driver);
        driver.get("https://client.ttplace.ru/user/main");
        Product.setDeleteAllProduct(driver);
        Assert.assertEquals("продуктов больше нет", Product.setDeleteAllProduct(driver));
        driver.get("https://client.ttplace.ru/user/settings/subscription");
        wait.until(elementToBeClickable(By.tagName("button")));
        Assert.assertEquals("Полный доступ", ClientPrime.setTextPremium());
    }

    @Test
    @DisplayName("delete kurs")
    public void DeleteKurs() throws Exception {
        iframe.BeforeRegistrationStatic(driver);
        driver.get("https://client.ttplace.ru/user/catalog");
        modal.modalTelephone();
        Product.AllProductSearch(driver);
        driver.get("https://client.ttplace.ru/user/my-courses");
        Product.setDeleteAllProduct(driver);
        Assert.assertEquals("продуктов больше нет", Product.setDeleteAllProduct(driver));
//        Product.setDeleteProduct(driver);

    }

    @Test
    @DisplayName("Open New Week From Kurs")
    public void OpenNewWeekFromKurs() throws Exception {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        iframe.BeforeRegistration(driver);
        Product.clickBtnFromStartLesson();
        wait.until(numberOfElementsToBeMoreThan(By.tagName("a"),13));
        Product.clickBtnWeek();
        modal.getModalOpenWeek();
        iframe.getIframeFromOpenWeek(driver);
        modal.getBtnContinue7Day();
        Product.clickBtnWeek();

        //для курса "Как написать хит"
//        Assert.assertEquals("/user/product/483/03444dcaec/lesson/8",Product.getArtHref());

    }

}
