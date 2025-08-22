package ru.tutor.test;


import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Issue;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.selenide.AllureSelenide;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import ru.tutor.page.footer;
import ru.tutor.page.ttPage;
import ru.tutor.page.addressPage;

import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class TestTtPage {

    //    @BeforeEach
    @BeforeAll
    public static void setupAll() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        Configuration.browserCapabilities = options;
        Configuration.browserSize = "1920x1080";
        open(addressPage.urlMain);
        SelenideLogger.addListener("allure.properties", new AllureSelenide()
        .screenshots(true));
    }

    @AfterAll
    public static void tearDownAll() {
//        closeWebDriver();
        SelenideLogger.removeListener("allure.properties");
    }

    @DisplayName("Текст внутри модалок на оплату")
    @Test
//        @RepeatedTest(3)

    public void ShowModalText() {

        String expected = "TutorPlace - Платформа для обучения, курсы по подписке, онлайн школа";
        step("Проверяем что мы на нужной странице",()->{
        assertEquals(expected, title());});

        ttPage.clickBtnOpenFor();
        ttPage.clickLite99();
        step("Проверяем текст модалки на соответствие Лайту", () ->
        {
            assertEquals("ПОПРОБУЙ ПЕРВУЮ НЕДЕЛЮ ТАРИФА “ЛАЙТ”", ttPage.setTextFromModal());
        });
        ttPage.clickBtnClose();
        ttPage.clickLife();
        step("Проверяем текст модалки на соответствие Пожизненному доступу", () ->
        {
            assertEquals("ОФОРМИ ПОЖИЗНЕННЫЙ ДОСТУП К БОЛЕЕ ЧЕМ К 400 КУРСАМ", ttPage.setTextFromModal());
        });
        ttPage.clickBtnClose();
        ttPage.clickPremium();
        step("Проверяем текст модалки на соответствие Премиуму", () ->
        {
            assertEquals("ПОПРОБУЙ ПЕРВУЮ НЕДЕЛЮ ТАРИФА “ПРЕМИУМ”", ttPage.setTextFromModal());
        });
        ttPage.clickBtnClose();
    }

    @Issue(value = "tests-73")
    @Test
    @Severity(value = SeverityLevel.MINOR)
    @DisplayName("футер главной страницы")
    public void FooterPage() {
        String expected = "TutorPlace - Платформа для обучения, курсы по подписке, онлайн школа";
        step("Проверяем что мы на нужной странице",()->{
        assertEquals(expected, title());});
        executeJavaScript("window.scrollTo(0, document.body.scrollHeight)");
        step("Проверяем текст футтера", () ->{
        assertEquals("Антикоррупционная политика", footer.setAntyPolicy());
        assertEquals("Образовательная лицензия", footer.setStudy());
        assertEquals("Политика конфиденциальности", footer.setPolicy());
        assertEquals("Пользовательское соглашение", footer.setUserAgreement());
        assertEquals("Публичная оферта", footer.setOffer());
        assertEquals("Руководство пользователя", footer.setManual());
        assertEquals("Согласие на обработку персональных данных", footer.setPersonalDate());
        assertEquals("О компании", footer.setAboutCompany());
        assertEquals("О подписке", footer.setAboutSubscription());
        assertEquals("Контакты", footer.setContacts());
        assertEquals("Новые горизонты", footer.setNewHorizon());
        assertEquals("Вебмастерам", footer.setWebmaster());
        assertEquals("Для авторов", footer.setForAuthors());
        assertEquals("Установка ПО", footer.setInstall());
        assertEquals("Карьера", footer.setCareer());
        assertEquals("8 (800) 555 32 82", footer.setNumber());
        assertEquals("Автоинформатор", footer.setInformation());
        assertEquals("support@tutorplace.ru", footer.setEmailSupport());
        assertEquals("По общим вопросам", footer.setQuastions());
        assertEquals("Все обращения принимаются только в электронном виде\nна почту support@tutorplace.ru или онлайн - чат на сайте", footer.setOnline());
        assertEquals("© OOO «ДИДЖИТАЛ КОНТЕНТ»\n2022–2025", footer.setDidgital());});
    }

    @Test
    @DisplayName("ссылки в атрибуте href")
    public void linksTtPage() {
        String expected = "TutorPlace - Платформа для обучения, курсы по подписке, онлайн школа";
        step("Проверяем что мы на нужной странице",()->{
        assertEquals(expected, title());});
        executeJavaScript("window.scrollTo(0, document.body.scrollHeight)");
        step("Проверяем наличие верных ссылок",()->{

        assertEquals("https://static.tutorplace.ru/docs/anticorruptionpolicy.pdf", footer.atrAntyPolicy());
        assertEquals("https://static.tutorplace.ru/docs/license.pdf", footer.atrStudy());
        assertEquals("https://static.tutorplace.ru/docs/privacy_policy.pdf", footer.atrPolicy());
        assertEquals("https://static.tutorplace.ru/docs/user_agreement.pdf", footer.atrUserAgreement());
        assertEquals("https://static.tutorplace.ru/docs/oferta.pdf", footer.atrOffer());
        assertEquals("https://static.tutorplace.ru/docs/rykovodstvo.pdf", footer.atrManual());
        assertEquals("https://static.tutorplace.ru/docs/personal_data.pdf", footer.atrPersonalDate());
        assertEquals("/about-company", footer.atrAboutCompany());
        assertEquals("/about-subscription", footer.atrAboutSubscription());
        assertEquals("/contacts", footer.atrContacts());
        assertEquals("/new-horizons", footer.atrNewHorizon());
        assertEquals("/webmasters", footer.atrWebmasters());
        assertEquals("/for-authors", footer.atrForAuthors());
        assertEquals("/install", footer.atrInstall());
        assertEquals("/vacancies", footer.atrCareer());
        assertEquals("mailto:support@tutorplace.ru", footer.atrEmailSupport());
        assertEquals("tel:+78005553282", footer.atrNumber());});
    }

    @Test
    @DisplayName("прокликивание футера")
    public void clickFooterTtpage() {
        String expected = "TutorPlace - Платформа для обучения, курсы по подписке, онлайн школа";
        step("Проверяем что мы на нужной странице",()->{
        assertEquals(expected, title());});
        executeJavaScript("window.scrollTo(0, document.body.scrollHeight)");
        footer.clickAboutCompany();
        step ("Сравниваем title стр`", ()->{
        assertEquals("TutorPlace | О компании", title());});
        footer.clickAboutSubscription();
        step ("Сравниваем title стр`", ()->{
        assertEquals("Подписка на курсы - онлайн обучение", title());});
        footer.clickContacts();
        step ("Сравниваем title стр`", ()->{
        assertEquals("TutorPlace | Контакты", title());});
        executeJavaScript("window.scrollTo(0, document.body.scrollHeight)");
        footer.clickNewHorizon();
        step ("Сравниваем title стр`", ()->{
        assertEquals("TutorPlace | Новые горизонты", title());});
        footer.clickWebmasters();
        step ("Сравниваем title стр`", ()->{
        assertEquals("TutorPlace | Веб-Мастерам", title());});
        footer.clickForAuthors();
        step ("Сравниваем title стр`", ()->{
        assertEquals("TutorPlace | Для авторов", title());});
        footer.clickInstall();
        step ("Сравниваем title стр`", ()->{
        assertEquals("TutorPlace | Установка ПО", title());});
        executeJavaScript("window.scrollTo(0, document.body.scrollHeight)");
        footer.clickCareer();
        step ("Сравниваем title стр`", ()->{
        assertEquals("TutorPlace | Вакансии", title());});
    }

    @Test
    @DisplayName("api product ")
    public void ApiFooterMenuProduct(){
step("Задаём список продуктов по которым необходимо пробежаться. Проверяем код ответа на 200", ()-> {
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
    }});
    }

    @Test
    @DisplayName("api category")
    public void ApiFooterMenuCategory(){

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