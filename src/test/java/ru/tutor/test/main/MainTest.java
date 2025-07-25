package ru.tutor.test.main;


import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.tutor.page.*;

import java.lang.reflect.Array;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

//import org.junit.jupiter.api.extension.ExtendWith;
//    import org.springframework.test.context.junit.jupiter.SpringExtension;


public class MainTest {

    public static WebDriver driver;

    public static String url = "https://admin:JWwppsEA84B4BozJgE44sNiZZ@ttplace.ru";

    @Before
    public void setupAll() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        driver = new ChromeDriver(options);
        main main = new main(driver);
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
    @DisplayName("clicable from footer menu")
    public void ClickableFromFooterMenu() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.get(url);
        main.scrollTo(driver);

        main.clickCompany();
        wait.until(visibilityOfElementLocated(By.cssSelector(".hero_hero__subtitle-2__JLkI7")));
        Assert.assertEquals("TutorPlace | О компании", driver.getTitle());
        driver.get(url);
        main.scrollTo(driver);

        main.clickSubscription();
        Thread.sleep(700);
        Assert.assertEquals("TutorPlace | О подписке", driver.getTitle());
        driver.get(url);
        main.scrollTo(driver);

        main.clickContacts();
        Thread.sleep(700);
        Assert.assertEquals("TutorPlace | Контакты", driver.getTitle());
        driver.get(url);
        main.scrollTo(driver);

        main.clickWebmaster();
        Thread.sleep(700);
        Assert.assertEquals("TutorPlace | Веб-Мастерам", driver.getTitle());
        driver.get(url);
        main.scrollTo(driver);

        main.clickForAuthors();
        Thread.sleep(700);
        Assert.assertEquals("TutorPlace | Для авторов", driver.getTitle());
        driver.get(url);
        main.scrollTo(driver);

        main.clickInstall();
        Thread.sleep(700);
        Assert.assertEquals("TutorPlace | Установка ПО", driver.getTitle());

        driver.get(url);
        main.scrollTo(driver);
        main.clickHorizon();
        Thread.sleep(700);
        Assert.assertEquals("TutorPlace | Новые горизонты", driver.getTitle());
        driver.get(url);
        main.scrollTo(driver);
        main.clickVacancies();
        Thread.sleep(700);
        Assert.assertEquals("TutorPlace | Вакансии", driver.getTitle());
    }

    @Test
    @DisplayName("api product ")
    public void ApiFooterMenuProduct(){
        driver.get(url);
        List<String> api = Arrays.asList("testkeis", "pilates", "excel", "pitanie", "alcoaddiction", "facebuilding",
               "mindfulness", "selfmassage", "closest", "sommelier", "goodyesight", "egehemistry", "turkisheries",
        "overweight", "ton", "feet", "burnout", "stopcheating", "keto", "telegram",
                "traiding", "numerolog", "constructor", "graphic", "freelance", "html-develop",
                "photo", "baby", "stylist", "tutoring", "preparation", "pelvic", "procreate", "eyebrown", "cardio",
                "ogelit", "ukhod-za-domashnimi-rasteniyami", "stretching", "booty", "press", "yoga", "krugovye",
                "cybersportsmen", "dvoyka", "kravmaga", "infosite", "python", "scalping", "timemanage",
                "matrix", "highheels", "design", "socscience", "infografika", "nekuri", "avito",
                "abuse", "manager", "fing", "neiroseti", "geometry", "midjourney", "target",
                "effectresume", "sellermarketplace", "adobepremiere", "blog", "chess",
                "mfr", "hair", "broadcast", "waist", "pitanie", "mozgofit", "beading", "pregnant",
                "pravil-naya-osanka", "chatbots", "roblox", "copywrighter", "english", "earnings-on-photostocks",
                "videographer", "orgazm", "capcut", "wedding", "energcandle", "flstudio", "firstaid",
                "ogelit", "japmanic", "shpagat", "millioner", "orator", "potency", "ableton", "trav",
                "procrastination", "bentocake", "ppbasis", "blender", "sound-design", "portfolio",
                "arttherapy", "acting", "smallbusiness", "egenglish", "aftereffects", "astrol", "osnovy-nlp",
                "qa", "intervaltrain", "beautyskin", "passion", "affirmations", "adobe-photoshop",
                "psychosomatics", "sobesedovanie", "math", "barre", "neurogym", "taping", "thaiboxing",
        "kundaliniyoga", "smm-menedzher", "fullbody", "marketplace", "interval", "figma", "streamer",
        "rope", "noparasit", "retouch", "pregnantyoga", "bodyflex", "notion", "sleep", "powerpoint",
                "digestion", "couch", "meditations", "event", "careerguid", "succsdating", "screenwriting",
                "otek", "sew", "nailspractice", "direct", "500kcal", "nozhki", "dancefit", "rune", "ved",
                "knitting", "ogemath", "diets", "workout", "investingestate", "clipmaker", "anxiety");

        for (String url : api){
        Response response = given()

                .baseUri("https://api.dev.tutorplace.ru/promo/")

                .contentType(ContentType.JSON)
//                .headers("Authorization", "Basic YWRtaW46Sld3cHBzRUE4NEI0Qm96SmdFNDRzTmlaWg==")

                .when()
                .get(url)

                .then()
                .statusCode(200)
                .extract()
                .response();

        String title = response.path("title");
//        response.prettyPrint();

        System.out.println(title);
        response.jsonPath();
    }
    }

        @Test
    @DisplayName("api category")
    public void ApiFooterMenuCategory(){
        driver.get(url);
//        List<String> api = Arrays.asList("api/config");

//        for (String url : api){
        Response response = given()

                .baseUri("https://api.dev.tutorplace.ru/products")

                .contentType(ContentType.JSON)

                .when()
                .get()

                .then()
                .statusCode(200)
                .extract()
                .response();

//        String title = response.path("listAllCourses");
        response.prettyPrint();

//        System.out.println(title);
        response.jsonPath();
    }
    }

//}