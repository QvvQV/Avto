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

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.options;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

public class splitModal_ttplace {

    public static WebDriver driver;

    public static String url1 = "https://admin:JWwppsEA84B4BozJgE44sNiZZ@ttplace.ru/catalog/product/excel?aff_rid=1012_ELQp";

//    public static void main(String[] args){
//
//    }

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

    @After
    public void quitDriver() {
        driver.manage().deleteAllCookies();
        driver.close();
        driver.quit();
    }

        @Test
//        @RepeatedTest(500)
    @DisplayName("Show Modal version From Tester")
    public void ShowModalVersionFromTester() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.get(url1);

        Response response = given()
                .baseUri("https://api.dev.tutorplace.ru")
                .contentType(ContentType.JSON)
//                .headers("Referer", "https://tester.tutorplace.ru/?aff_rid=1012_ELQp")
                .formParam("showModalVersion")
                .queryParam("showModalVersion")
                .formParam("splitAlter")
                .queryParam("splitAlter")

                .when()
                .get("/available-payment-systems")
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

    //    ttplace

    @Test
//    @RepeatedTest(2)
    @DisplayName("ttplace Show Modal find click Sber")

    public void TtplaceShowModalVersionTestSber() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.get(url1);
//        Thread.sleep(1000);

        wait.until(visibilityOfElementLocated(By.cssSelector(".font-inter")));
        wait.until(elementToBeClickable(By.cssSelector(".font-inter")));
        PayMain.clickBtnStart();
        PayMain.getDostup();
        System.out.println(driver.manage().getCookieNamed("split-option"));
                Set<Cookie> cookies = driver.manage().getCookies();
        for (Cookie cookie : cookies) {
            System.out.println("name: " + cookie.getName());
            if (cookie.getName().equals("split-option")) {
                System.out.println("куки: " + cookie.getValue());
            }
        }
        System.out.println("Оплата с продукта Excel за 1 руб: " + LoginAndPass.textEmail());
//        System.out.println("ваша куки была: " + cookies);

        System.out.println(split.change() + LoginAndPass.textEmail() + " Кнопка Sber" + " Kуки: " + cookies);
        wait.until(elementToBeClickable(By.cssSelector(".sber")));
        split.clickBtnSber();

        Thread.sleep(3500);

        driver.get("chrome://settings/clearBrowserData");
        Thread.sleep(1000);
//        driver.getWindowHandles().toArray();
//        split.deleteCash(driver);
    }

    @Test
    @DisplayName("ttplace Show Modal find click Cloud")
    public void TtplaceShowModalVersionTestCloud() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.get(url1);
        Thread.sleep(500);
        Set<Cookie> cookies = driver.manage().getCookies();
        for (Cookie cookie : cookies) {
            System.out.println("name: " + cookie.getName());
            if (cookie.getName().equals("split-option")) {
                System.out.println("куки: " + cookie.getValue());
            }
        }
        wait.until(elementToBeClickable(By.cssSelector(".font-inter")));

        PayMain.clickBtnStart();
        PayMain.getDostup();
        System.out.println("Оплата с продукта Excel за 1 руб: " + LoginAndPass.textEmail());

        System.out.println(split.change() + LoginAndPass.textEmail() + " Кнопка Cloud" + " куки: " + cookies);
        System.out.println(driver.findElements(By.cssSelector(".tink")).get(0).getText());
        wait.until(elementToBeClickable(By.cssSelector(".tink")));
        split.clickBtnCloud();

        Thread.sleep(3500);
//        split.deleteCash(driver);
    }

//    @Test
    @DisplayName("ttplace Show Modal find click Other")
    public void TtplaceShowModalVersionTestOther() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.get(url1);
        Thread.sleep(500);
        Set<Cookie> cookies = driver.manage().getCookies();
        for (Cookie cookie : cookies) {
            System.out.println("name: " + cookie.getName());
            if (cookie.getName().equals("split-option")) {
                System.out.println("куки: " + cookie.getValue());
            }
        }
        wait.until(elementToBeClickable(By.cssSelector(".font-inter")));

        PayMain.clickBtnStart();
        PayMain.getDostup();
        System.out.println("Оплата с продукта Excel за 1 руб: " + LoginAndPass.textEmail());

        System.out.println(split.change() + LoginAndPass.textEmail() + " Кнопка Other" + " куки: " + cookies);
        wait.until(elementToBeClickable(By.cssSelector(".other")));
        split.clickBtnOther();

        Thread.sleep(3500);
        split.deleteCash(driver);
    }

//    @Test
    @DisplayName("ttplace Show Modal find click Sber")
    public void TtplaceShowModalVersionTestSber1() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.get(url1);
        Thread.sleep(500);
        Set<Cookie> cookies = driver.manage().getCookies();
        for (Cookie cookie : cookies) {
            System.out.println("name: " + cookie.getName());
            if (cookie.getName().equals("split-option")) {
                System.out.println("куки: " + cookie.getValue());
            }
        }
        wait.until(visibilityOfElementLocated(By.cssSelector(".font-inter")));
        wait.until(elementToBeClickable(By.cssSelector(".font-inter")));
        PayMain.clickBtnStart();
        PayMain.getDostup();
        System.out.println("Оплата с продукта Excel за 1 руб: " + LoginAndPass.textEmail());
//        System.out.println("ваша куки была: " + cookies);

        System.out.println(split.change() + LoginAndPass.textEmail() + " Кнопка Sber" + " Kуки: " + cookies);
        wait.until(elementToBeClickable(By.cssSelector(".sber")));
        split.clickBtnSber();

        Thread.sleep(3500);
        split.deleteCash(driver);
    }

//    @Test
//    @DisplayName("Show Modal find click")
//    public void ShowModalVersionTest() throws InterruptedException {
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
//        driver.get(url);
////        driver.findElements(By.cssSelector(".intro__bottom > a")).get(0).click();
//        wait.until(visibilityOfElementLocated(By.id("email")));
//        String a = LoginAndPass.emails();
//        driver.findElement(By.id("email")).sendKeys(a);
////        driver.findElement(By.id("mainFormaSubmitBtn")).click();
////        String b =  driver.findElement(By.cssSelector(".modal-again__text")).getText().trim();
//        if (b.equals("Оплата картой другого банка")) {
//            System.out.println("Появилось новая модалка: " + a);
//        } else
//        {
//            System.out.println("Появилась старая модалка: " + a);
//        }
//        driver.findElements(By.cssSelector(".button__option.button__sber.show")).get(0).click();
//        Thread.sleep(3500);

//        Response response = given()
//                .baseUri("https://tester.tutorplace.ru")
//                .contentType(ContentType.JSON)
//                .headers("Referer", "https://tester.tutorplace.ru/?aff_rid=1012_ELQp")
//                .formParam("showModalVersion")
//                .queryParam("showModalVersion")
//                .formParam("splitAlter")
//                .queryParam("splitAlter")
//
//                .when()
//                .get("/domain?aff_rid=1012_ELQp")
//                .then()
//                .statusCode(200)
//                .extract()
//                .response();
//
//        int show = response.path("showModalVersion");
//        int split = response.path("splitAlter");
////        response.prettyPrint();
////           System.out.println("Email: " + mails );
//        System.out.println("showModalVersion: " + show);
//        System.out.println("SplitModal: " + split);
}