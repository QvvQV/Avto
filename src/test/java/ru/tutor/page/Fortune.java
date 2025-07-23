package ru.tutor.page;

import com.github.dockerjava.api.model.Driver;
import com.github.javafaker.Faker;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static io.restassured.RestAssured.given;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

//public class BodyData(String login, String password) {
//    this.login = login;
//    this.password = password;
//}

public class Fortune extends Driver {

    private static final Faker faker = new Faker(new Locale("ru"));

    public Fortune(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = "._container_1ig12_34")
    private static WebElement bannerFortune;

//    public static boolean waitMyDetails() {
    /// /        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
//        return (myDetails.isDisplayed());
//    }

    @FindBy(css = "._fullWidth_kio3a_195")
    private static WebElement btnGoBanner;

    @FindBy(css = "._head_1m9jn_39")
    private static WebElement showModalText;

    @FindBy(css = "._detail_1m9jn_57")
    private static WebElement showModalText1;

    @FindBy(css = "body > div:nth-child(10) > div > button")
    private static WebElement btnCrossModal;

    @FindBy(css = "body > div:nth-child(10) > div > div > img")
    private static WebElement atrImgMod;

    @FindBy(css = "._spinBtn_r812i_102 ")
    private static WebElement btnSplin;

    @FindBy(css = "._info_1ec1j_78 > div:nth-child(2) > button:nth-child(1)")
    private static WebElement btnPickUp;

    @FindBy(css = "._transparent_kio3a_35._fullWidth_kio3a_195")
    private static WebElement btnTwistAgain;

    @FindBy(css = "._closeBtn_1ec1j_13")
    private static WebElement closeBtn;

    @FindBy(css = "._balance_6m3zm_123")
    private static WebElement valueDimond;

    @FindBy(css = ".hhkNnc")
    private static WebElement getAllPriz;

    @FindBy(css = "._description_1ec1j_85 > p:nth-child(2) > span")
    private static WebElement textPrizFromModal;

    public static void clickBtnGoBanner() {
        btnGoBanner.click();
    }

    public static String showModalText() {
        return showModalText.getText().trim();
    }

    public static String showModalText1() {
        return showModalText1.getText().trim();
    }

    public static void clickBtnCrossModal() {
        btnCrossModal.click();
    }

    public static String atribImgMod() {
        return atrImgMod.getDomAttribute("src");
    }

    public static String textBtnSplin() {
        return btnSplin.getText().trim();
    }

    public static void clickCloseBtn() {
        closeBtn.click();
    }

    public static void clickBtnSplin(WebDriver driver) {
        if ((textBtnSplin().equals("КРУТИТЬ") || textBtnSplin().equals("КРУТИТЬ\n30")) && getValueDimond() >= 30) {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            System.out.println("крутим колесо");
            btnSplin.click();
            wait.until((visibilityOfElementLocated(By.cssSelector("._content_1ec1j_18"))));
//            System.out.println("Алмазов осталось: " + getValueDimond());

        } else
            System.out.println("Бесплатное вращение недоступно и алмазов на счету недостаточно. Кол-во алмазов составляет: " + getValueDimond());
    }

    private static int getValueDimond() {
        return Integer.parseInt(valueDimond.getText().trim());
    }

    //
//    public static void ifParse(){
//        if (getValueDimond()>=30){
//            System.out.println("Крутим дальше");
//        } else System.out.println("Закончили");
//    }
    public static void clickAllPriz() {
        try {
            if (getAllPriz.isDisplayed()) {
                getAllPriz.click();
            }
        } catch (NoSuchElementException e) {
        }
    }

    public static String countPriz(WebDriver driver) throws InterruptedException {
        List<WebElement> defaultCountProduct = driver.findElements(By.cssSelector(".sc-bcgPVu.eCxrUs"));

        System.out.println("Кол-во призов: " + defaultCountProduct.size() + " шт");
        clickAllPriz();
        Thread.sleep(1000);
        List<WebElement> defaultCountProduct1 = driver.findElements(By.cssSelector(".sc-bcgPVu.eCxrUs"));
        System.out.println("Оставшиеся призы получают через обращение. Кол-во оставшихся призов: " + defaultCountProduct1.size() + " шт");

        return "";
    }

    public static void SplinDoing(WebDriver driver) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until((visibilityOfElementLocated(By.cssSelector("._container_1ig12_34"))));
        wait.until(elementToBeClickable(By.cssSelector("._fullWidth_kio3a_195")));
        Fortune.clickBtnGoBanner();
        wait.until(visibilityOfElementLocated(By.cssSelector("._content_1m9jn_7")));
        wait.until(visibilityOfElementLocated(By.cssSelector("._close_1l2kq_40")));
        wait.until(elementToBeClickable(By.cssSelector("body > div:nth-child(10) > div > button")));
        Thread.sleep(500);
        Fortune.clickBtnCrossModal();
        Thread.sleep(500);
        wait.until(elementToBeClickable(By.cssSelector("._spinBtn_r812i_102 ")));
        clickBtnSplin(driver);
    }

    public static String clickBtnPickUp(WebDriver driver) {
        String expected = GameStatsResponse();
        btnPickUp.click();
//        if (btnSplin.isDisplayed()){
//            return "Приз забран";
//        } else {
//            return "Приз получают через обращение";
//        }
        if (driver.getCurrentUrl().equals("https://client.ttplace.ru/user/my-profile/awards")) {
            Assert.assertEquals("https://client.ttplace.ru/user/my-profile/awards", driver.getCurrentUrl());
            return "Приз получают через обращение";
        }
        String text = textPrizFromModal.getText().trim();
//        String firstNumber = text.replaceAll("\\D+", ""); //вычленение значения из приза
        String actual =  GameStatsResponse();
        return "Значение в массиве до клика по колесу: " + expected + "; Приз забран в кол-ве: " + text + "; Значение в массиве после клика по колесу: " + actual;
    }

    public static String GameStatsResponse() {
//        System.out.println(request("GET").getHeaders("Authorization"));

        Response response1 = given()

                .baseUri("https://api.dev.tutorplace.ru/user")

                .contentType(ContentType.JSON)
                .headers("Authorization", "Bearer " + doGet())

                .queryParam("diamond")
                .formParam("diamond")
                .queryParam("experience")
                .formParam("experience")

                .when()
                .get("/game-stats")

                .then()
                .extract()
                .response();

        int diamond = response1.path("diamond");
        int experience = response1.path("experience");

        int[] myArray = {diamond, experience};

        System.out.println("Кол-во алмазов: " + diamond);
        System.out.println("Кол-во опыта: " + experience);

        return Arrays.toString(myArray);
    }

//    class GetTokenTest
//{
//    String getTokenTest ()
//    {
//        RequestSpecification request = RestAssured.given ().relaxedHTTPSValidation ();
//        Response response = request.post (config.getToken);
//        JsonPath jsonPath = response.jsonPath ();
//        String token = jsonPath.get ("access_token");
//        return token;
//    }

    public static void StatsGameResponse() throws InterruptedException {
        Thread.sleep(1500);

        Response response1 = given()
                .baseUri("https://api.dev.tutorplace.ru/user")
                .contentType(ContentType.JSON)
                .headers("Authorization", "Bearer " + doGet())

                .queryParam("type")
                .formParam("type")
                .queryParam("value")
                .formParam("value")
//                .queryParam("name")

                .when()
                .post("/game-wheel/start-game")

                .then()
                .extract()
                .response();
        String type = response1.path("type");

        response1.prettyPrint();

        int value = response1.path("value");

        if (type.equals("diamond")) {
            System.out.println("Кол-во алмазов: " + value);
        }
        if (type.equals("experience")) {
            System.out.println("Кол-во опыта: " + value);
        }
        if (type.equals("valuation")) {
            System.out.println("Перенаправлены на др страницу");
        }
    }

    public static String doGet() {

        String requestBody = "{\"email\": \"tp5052_2@test.ru\", \"password\": \"12345678\"}";
        Response response = given()

                .baseUri("https://api.dev.tutorplace.ru/user")

                .contentType(ContentType.JSON)
                .body(requestBody)

                .queryParam("token")

                .when()
                .post("/login")


                .then()
                .statusCode(200)

                .extract().response();

        String token = response.path("token");
//        response.prettyPrint();
        return token;
    }

}

//    черновой вариант запроса

//        Response response = given()
//
//                .baseUri("https://api.dev.tutorplace.ru/user")
//                .headers("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpYXQiOjE3NTIyMTkyNTIsImV4cCI6MTc1NDgxMTI1Miwicm9sZXMiOlsiUk9MRV9VU0VSIl0sInVzZXJuYW1lIjoidHA1MDUyXzJAdGVzdC5ydSJ9.u9GhRPNAmKQqqhEqEvJyGi5Oq-8vxqKCuP3SkMxbq3J8vjZdFRvuyepleSyMCqWl2xzaTPbV2Y2V2TnB-qgMhrD5-5RgozNQbUEV317_EMbfbx9qeiGfRtad4OGDhOOoY1y43TLCENM1yURGhcji_fPbFyip9hFwzEigAQK_YrVRZU-xJ4qsyaoR4CFCg0P0VVtm8T6PryFCni0Cm2LWg3lkSRUbOfoa6QeTKZUgqpeUDOJk2ljNNbFaVvxxQHxnt7uRKdPmlOoY8hCFl_RbeEPTnzZ12gKqZ9zyktZeOksvjHBRGvNvbDJzXvThRecT4Kkgb1NE6T-qe8RMOFUner-17X5PqU_9lMNOtyYCkiEOe75Xrk83t2iHzUCnTYyFIbed6acrlXqDMOOQUNJY4S5KPN6kYUb0go0Cuzl0dBGIG5DFsIAWi8XxEHsdNAt8ueUSCWbrqvZDaJS6eDgZihodAtaP9g7UtnSoMGHo45vJFcVJmuTOlXMtoO0JLVuL9PXJzBYmW1BsXJ_9GqkQ_d69pbW0xJlsa13D4VEpTWGL44R7jjXt7JphJDEoncnZDSjYq3R6pGVCrjKbtxnD_lDb5Eg49aj_qEUfC2XhyMRSvo-SOh5MddTMsYhzRuF1yJ7yzjFKH5HGItRLiBDePjj3KgS7Dblw801LnaXY4EQ")
//                .queryParam("diamond")
//
//                .when()
//                .get("/game-stats");

//                .then()
//                .statusCode(200)
/// /                        .body("diamond", equalTo((int)410),"experience",equalTo((int)4350)).log().all()
//                .extract().response();

//        );
//        String value = String.valueOf(JsonPath.from(response.body().asString()).getInt("diamond"));
//String type = JsonPath.from(response1.body().asString()).getString("type");
/// /        String jsonString = response.getBody().asString();
/// /        String diamond = JsonPath.from(jsonString).get("diamond");
//        response.prettyPrint();
