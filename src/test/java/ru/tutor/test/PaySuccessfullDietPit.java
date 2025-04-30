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

public class PaySuccessfullDietPit {

    public static WebDriver driver;
//    public static String url = "https://dev.tutorplace.ru/catalog/product/excel";
//    https://client.dev.tutorplace.ru/login

    public void PayFromIframe(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
//        driver.get("https://dev.tutorplace.ru/catalog/product/pitanie");
        wait.until(elementToBeClickable(By.cssSelector(".font-inter")));
        PayMain.clickBtnStart();
        PayMain.getDostup();
        wait.until(elementToBeClickable(By.tagName("iframe")));
        iframe.getIframeForLand(driver);
    }

    @Before
    public void setupAll() {
        driver = new ChromeDriver();
        ClientPrime Client = new ClientPrime(driver);
        PayMain Pay = new PayMain(driver);
        LoginAndPass loginAndPass = new LoginAndPass(driver);
        modal modal = new modal(driver);
        Card card = new Card(driver);
        DietPage dietPage = new DietPage(driver);
        iframe iframe = new iframe(driver);
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
    @DisplayName("Success Pay from Diet page")
    public void SuccessPayFromDietPage(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.get("https://dev.tutorplace.ru/catalog/product/pitanie");
        PayFromIframe();
        wait.until(numberOfElementsToBeMoreThan(By.tagName("input"),5));
        LoginAndPass.registrationFromBild();
        wait.until(numberOfElementsToBeMoreThan(By.tagName("h2"),9));
        Assert.assertEquals("Получен доступ\n"+
                "к дневнику питания", DietPage.setH2_1());
        Assert.assertEquals("Поздравляем!\n" +
                "Персональный дневник питания на 7 дней уже ждет тебя на внутренней TutorПочте. Но это еще не все!", DietPage.setP_1());
        Assert.assertEquals("Дальше", DietPage.textBtn());
        DietPage.click();
        Assert.assertEquals("Достигни цели\n" +
                "без срывов и стресса",DietPage.setH2_2());
        Assert.assertEquals("Трекер питания поможет легко придерживаться нормы калорий. Он доступен в разделе “Сервисы”", DietPage.setP_2());
        DietPage.click();
        Assert.assertEquals("Стань гуру правильного питания за месяц",DietPage.setH2_3());
        Assert.assertEquals("Приготовили для тебя подборку\n" +
                "курсов TutorPlace, которые помогут разобраться в теме",DietPage.setP_3());
        DietPage.click();
        Assert.assertEquals("Подберем подходящую активность",DietPage.setH2_4());
        Assert.assertEquals("С тренировками добиться результата проще, их также можно найти в подборке курсов на TutorПочте",DietPage.setP_4());
        DietPage.btnNext();
    }
}
