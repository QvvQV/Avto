package ru.tutor.test.split_modal;


import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
//import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.tutor.page.*;

import java.time.Duration;
import java.util.Set;
//import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

//import org.junit.jupiter.api.extension.ExtendWith;
//    import org.springframework.test.context.junit.jupiter.SpringExtension;


public class splitModal {

    public static WebDriver driver;

    public static String url = "https://tester.tutorplace.ru/?utm_source=cpaws&aff_rid=1012_ELQp";

//    @BeforeEach
    @Before
    public void setupAll() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        driver = new ChromeDriver(options);
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
        split split = new split(driver);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(7));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().window().setSize(new Dimension(1920, 1080));
    }

//    @AfterEach
    @After
    public void quitDriver() {
        driver.manage().deleteAllCookies();
        driver.close();
        driver.quit();
    }

//    public static void click(){
//        driver.findElements(By.cssSelector(".btn-main.intro__btn")).get(0);
//    }

    //    @Test
    @DisplayName("Show Modal version From Tester")
    public void ShowModalVersionFromTester() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.get(url);

        Response response = given()
                .baseUri("https://tester.tutorplace.ru")
                .contentType(ContentType.JSON)
                .headers("Referer", "https://tester.tutorplace.ru/?aff_rid=1012_ELQp")
                .formParam("showModalVersion")
                .queryParam("showModalVersion")
                .formParam("splitAlter")
                .queryParam("splitAlter")

                .when()
                .get("/domain?aff_rid=1012_ELQp")
                .then()
                .statusCode(200)
                .extract()
                .response();

        int show = response.path("showModalVersion");
        int split = response.path("splitAlter");
        response.prettyPrint();
        System.out.println(show);
        System.out.println(split);
    }


    //    @Test
    @DisplayName("Show Modal version From Taro")
    public void ShowModalVersionFromTaro() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.get("https://tarobws.tutorplace.ru/?utm_source=cpaws&aff_rid=1012_ELQp");

        Response response = given()
                .baseUri("https://tarobws.tutorplace.ru")
                .contentType(ContentType.JSON)
                .headers("Referer", "https://tarobws.tutorplace.ru/?utm_source=cpaws&aff_rid=1012_ELQp")
                .formParam("showModalVersion")
                .queryParam("showModalVersion")
                .formParam("splitAlter")
                .queryParam("splitAlter")

                .when()
                .get("/domain?utm_source=cpaws&aff_rid=1012_ELQp")
                .then()
                .statusCode(200)
                .extract()
                .response();

        int show = response.path("showModalVersion");
        int split = response.path("splitAlter");
//        response.prettyPrint();
        System.out.println(show);
        System.out.println(split);
    }

//    tester

    @Test
//    @RepeatedTest(333)
    @DisplayName("Show Modal find click Sber")
    public void ShowModalVersionTestSber4() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.get(url);
//        split.clickBtnSave();
        Thread.sleep(500);
        System.out.println(driver.manage().getCookieNamed("split-option"));
//        Set<Cookie> cookies = driver.manage().getCookies();
//        for (Cookie cookie : cookies) {
//            System.out.println("name: " + cookie.getName());
//            if (cookie.getName().equals("split-option")) {
//                System.out.println("куки: " + cookie.getValue());
//            }
//        }
        wait.until(visibilityOfElementLocated(By.id("email")));
        String a = LoginAndPass.emails();
        driver.findElement(By.id("email")).sendKeys(a);
        split.clickMainForma();
        System.out.println(split.showText());
        System.out.println(split.change() + a + " Кнопка Sber");
        split.clickBtnSber();

        Thread.sleep(3500);
    }

    @Test
//    @RepeatedTest(333)
    @DisplayName("Show Modal find click Cloud")
    public void ShowModalVersionTestCloud4() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.get(url);
        Thread.sleep(500);
        System.out.println(driver.manage().getCookieNamed("split-option"));
//        Set<Cookie> cookies = driver.manage().getCookies();
//        for (Cookie cookie : cookies) {
//            System.out.println("name: " + cookie.getName());
//            if (cookie.getName().equals("split-option")) {
//                System.out.println("куки: " + cookie.getValue());
//            }
//        }

        wait.until(visibilityOfElementLocated(By.id("email")));
        String a = LoginAndPass.emails();
        driver.findElement(By.id("email")).sendKeys(a);
        split.clickMainForma();
        System.out.println(split.showText());
        System.out.println(split.change() + a + " Кнопка Cloud");
        split.clickBtnCloud();

        Thread.sleep(3500);
    }

    @Test
//    @RepeatedTest(333)
    @DisplayName("Show Modal find click Other")
    public void ShowModalVersionTestOther4() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.get(url);
        Thread.sleep(500);
        System.out.println(driver.manage().getCookieNamed("split-option"));
//        Set<Cookie> cookies = driver.manage().getCookies();
//        for (Cookie cookie : cookies) {
//            System.out.println("name: " + cookie.getName());
//            if (cookie.getName().equals("split-option")) {
//                System.out.println("куки: " + cookie.getValue());
//            }
//        }

        wait.until(visibilityOfElementLocated(By.id("email")));
        String a = LoginAndPass.emails();
        driver.findElement(By.id("email")).sendKeys(a);
        split.clickMainForma();
        System.out.println(split.showText());
        System.out.println(split.change() + a + " Кнопка Other");
        split.clickBtnOther();

        Thread.sleep(3500);
    }
}