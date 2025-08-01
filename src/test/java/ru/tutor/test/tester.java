package ru.tutor.test;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import ru.tutor.page.TestPage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.open;
import static io.restassured.RestAssured.given;


public class tester {
public static String url = TestPage.url;
//    public WebDriver driver;

//    final String url = "https://admin:JWwppsEA84B4BozJgE44sNiZZ@ttplace.ru/catalog/product/excel";

//    @BeforeEach
    @Before
    public void setupAll() {
        open(url);
//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--incognito");
//        driver = new ChromeDriver(options);
//        ClientPrime Client = new ClientPrime(driver);
//        PayMain Pay = new PayMain(driver);
//        LoginAndPass loginAndPass = new LoginAndPass(driver);
//        ClientLite clientLite = new ClientLite(driver);
//        modal modal = new modal(driver);
//        Card card = new Card(driver);
//        Product product = new Product(driver);
//        PromoCode promoCode = new PromoCode(driver);
//        ClubPage clubPage = new ClubPage(driver);
//        Fortune fortune = new Fortune(driver);
//        split split = new split(driver);
//        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(7));
//        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
//        driver.manage().window().setSize(new Dimension(1920, 1080));
    }
//
//    @After
//    public void quitDriver() {
//        driver.manage().deleteAllCookies();
//        driver.close();
//        driver.quit();
//    }

//    public static void click(){
//        driver.findElements(By.cssSelector(".btn-main.intro__btn")).get(0);
//    }

        @Test
//        @RepeatedTest(3)
    @DisplayName("Show Modal version From Tester")
    public void ShowModalVersionFromTester() {

        String expected = "Excel-таблицы для начинающих | Онлайн-курс | TutorPlace";
        Assert.assertEquals(expected, title());

        TestPage.clickBtnStart();

        System.out.println("Оплата с продукта Excel за 1 руб: " + TestPage.atrEmail());
        TestPage.clickCardPageBtn();
}
}