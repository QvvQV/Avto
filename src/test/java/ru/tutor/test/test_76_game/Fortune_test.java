package ru.tutor.test.test_76_game;


import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.tutor.page.*;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.util.Arrays;

import static io.restassured.RestAssured.*;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

public class Fortune_test {

    public static WebDriver driver;

    public static String url = "https://client.ttplace.ru/user/member-club/requisite";

    @Before
    public void setupAll() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        driver = new ChromeDriver();
        ClientPrime Client = new ClientPrime(driver);
        PayMain Pay = new PayMain(driver);
        LoginAndPass loginAndPass = new LoginAndPass(driver);
        ClientLite clientLite = new ClientLite(driver);
        modal modal = new modal(driver);
        Card card = new Card(driver);
        Product product = new Product(driver);
        PromoCode promoCode = new PromoCode(driver);
        ClubPage clubPage = new ClubPage(driver);
        Fortune fortune = new Fortune(driver);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(7));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
    }

    @After
    public void quitDriver() {
        driver.close();
        driver.quit();
    }

    @Test
    @DisplayName("Text Fortune Modal")
    public void TextFortuneModal() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        iframe.BeforeRegistrationStatic(driver);
        wait.until((visibilityOfElementLocated(By.cssSelector("._container_1ig12_34"))));
        wait.until(elementToBeClickable(By.cssSelector("._fullWidth_kio3a_195")));
        Fortune.clickBtnGoBanner();
        wait.until(visibilityOfElementLocated(By.cssSelector("._content_1m9jn_7")));
        wait.until(elementToBeClickable(By.cssSelector("body > div:nth-child(10) > div > button")));
        String expected = "Добро пожаловать\n" +
                "в Колесо Фортуны!\n" +
                "Здесь ты можешь испытать свою удачу. Вращай колесо один раз в день бесплатно и получай разнообразные призы: алмазы и опыт твоего профиля";
        String expected1 = "Так же ты можешь выиграть и более ценные призы: от OZON-сертификатов до нового iPhone!";
        Assert.assertEquals(expected, Fortune.showModalText());
        Assert.assertEquals(expected1, Fortune.showModalText1());
        Assert.assertEquals("/img/profile/leaders-modal-slide-4.png", Fortune.atribImgMod());
        Fortune.clickBtnCrossModal();
        ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("._content_1m9jn_7"));
    }

    @Test
    @DisplayName("Play Fortune get priz from my-profile")
    public void PlayFortuneGetPrizMyProfile() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        iframe.BeforeRegistrationStatic(driver);
        Fortune.SplinDoing(driver);
        wait.until(visibilityOfElementLocated(By.cssSelector("._closeBtn_1ec1j_13")));
        wait.until(elementToBeClickable(By.cssSelector("._closeBtn_1ec1j_13")));
        Fortune.clickCloseBtn();
        wait.until(elementToBeClickable(By.cssSelector("._spinBtn_r812i_102 ")));
        Assert.assertEquals("КРУТИТЬ\n30", Fortune.textBtnSplin());
        driver.get("https://client.ttplace.ru/user/my-profile/awards");
        Fortune.countPriz(driver);

    }

    @Test
    @DisplayName("Play Fortune get priz from Fortune")
    public void GetPrizFromFortune() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        iframe.BeforeRegistrationStatic(driver);
//        Fortune.doGet();

//        String expected = Fortune.GameStatsResponse();

        Fortune.SplinDoing(driver);

//        Fortune.StatsGameResponse();

        System.out.println(Fortune.clickBtnPickUp(driver));

//        String actual = Fortune.GameStatsResponse();

//        System.out.println("Значение до клика по колесу: " + expected);
//        System.out.println("Значение после клика по колесу: " + actual);
//        Arrays.stream(expected).anyMatch(actual::equals);
//        Arrays.stream(actual)
//                .filter(item -> !expected.contains(item))
//                .forEach(item -> System.out.println("Изменилось значение: " + item));
//        Assertions.assertArrayEquals(expected,actual);
    }

//    @Test
    @DisplayName("dump")
    public void GetDump() throws UnsupportedEncodingException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        iframe.BeforeRegistrationStatic(driver);

        for (int i=0; i<1000; i++){
            Response response1 = given()
                    .baseUri("https://api.dev.tutorplace.ru")
                    .contentType(ContentType.JSON)
                    .headers("Authorization", "Bearer " + Fortune.doGet())

                    .when()
                .get("/user/39217/webinars/253/1753194600/chat")

                    .then()
                    .statusCode(200)

                    .extract()
                    .response();

//            System.out.println(response1.body());
            response1.prettyPrint();
            System.out.println("Progon: " + i);

        }
    }
//
//    @Test
//    @DisplayName("Club Error Ip")
//    public void ClubErrorIP() {
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
//        driver.manage().window().setSize(new Dimension(1920, 1080));
//        iframe.BeforeRegistrationStatic(driver);
//        driver.get(url);
//        wait.until(elementToBeClickable(By.cssSelector("._Radio_1o11b_1")));
//        ClubPage.errorIP();
//    }
}