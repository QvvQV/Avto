package ru.tutor.test;


import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.AllureId;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.tutor.base.BaseUiTest;
import ru.tutor.constants.AddressPage;
import ru.tutor.page.*;

import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class TestTtPage extends BaseUiTest {

    private final TtPage ttPage = new TtPage();

    //    @BeforeEach
    @BeforeAll
    public static void setupAll() {
        open(AddressPage.urlMain);
    }

    @AfterAll
    public static void tearDownAll() {
//        closeWebDriver();
        SelenideLogger.removeListener("AllureSelenide");
    }

    @DisplayName("Текст внутри модалок на оплату")
    @Test
    @AllureId("12")
    //        @RepeatedTest(3)

    public void ShowModalText() {

        String expected = "TutorPlace - Платформа для обучения, курсы по подписке, онлайн школа";
        step("Проверяем что мы на нужной странице", () ->
                assertEquals(expected, title()));

        ttPage.clickBtnOpenFor();

        for (TtPage.SubscriptionType type : TtPage.SubscriptionType.values()) {
            ttPage.selectSubscription(type);
            step("Проверяяем текст модалки для " + type.getDisplayName(), () ->
                    assertEquals(type.getExpectedModalText(), ttPage.getTextModal())
            );
            ttPage.closeModal();
        }
    }

    //    @Issue(value = "tests-73")
    @Test
    @Severity(value = SeverityLevel.MINOR)
    @DisplayName("футер главной страницы")
    public void FooterPage() {
        String expected = "TutorPlace - Платформа для обучения, курсы по подписке, онлайн школа";
        step("Проверяем что мы на нужной странице", () ->
                assertEquals(expected, title()));
        executeJavaScript("window.scrollTo(0, document.body.scrollHeight)");
        step("Проверяем текст футтера", () -> {
                    assertEquals("Антикоррупционная политика", footer.setAntyPolicy());
                    assertEquals("Образовательная лицензия", footer.setStudy());
                    assertEquals("Политика конфиденциальности", footer.setPolicy());
                    assertEquals("Пользовательское соглашение", footer.setUserAgreement());
                    assertEquals("Публичная оферта", footer.setOffer());
//        assertEquals("Руководство пользователя", footer.setManual());
                    assertEquals("Согласие на обработку персональных данных", footer.setPersonalDate());
                    assertEquals("О компании", footer.setAboutCompany());
                    assertEquals("О подписке", footer.setAboutSubscription());
                    assertEquals("Контакты", footer.setContacts());
                    assertEquals("Новые горизонты", footer.setNewHorizon());
                    assertEquals("Вебмастерам", footer.setWebmaster());
                    assertEquals("Для авторов", footer.setForAuthors());
//        assertEquals("Установка ПО", footer.setInstall());
                    assertEquals("Карьера", footer.setCareer());
                    assertEquals("8 (800) 555 32 82", footer.setNumber());
                    assertEquals("Автоинформатор", footer.setInformation());
                    assertEquals("support@tutorplace.ru", footer.setEmailSupport());
                    assertEquals("По общим вопросам", footer.setQuastions());
                    assertEquals("Все обращения принимаются только в электронном виде\nна почту support@tutorplace.ru или онлайн - чат на сайте", footer.setOnline());
                    assertEquals("© OOO «ДИДЖИТАЛ КОНТЕНТ»\n2022–2026", footer.setDidgital());
                }
        );
    }

    @Test
    @DisplayName("ссылки в атрибуте href")
    public void linksTtPage() {
        String expected = "TutorPlace - Платформа для обучения, курсы по подписке, онлайн школа";
        step("Проверяем что мы на нужной странице", () ->
                assertEquals(expected, title()));
        executeJavaScript("window.scrollTo(0, document.body.scrollHeight)");
        step("Проверяем наличие верных ссылок", () -> {

            assertEquals("https://static.ttplace.ru/docs/anticorruptionpolicy.pdf", footer.atrAntyPolicy());
            assertEquals("https://static.ttplace.ru/docs/license.pdf", footer.atrStudy());
            assertEquals("https://static.ttplace.ru/docs/privacy_policy.pdf", footer.atrPolicy());
            assertEquals("https://static.ttplace.ru/docs/user_agreement.pdf", footer.atrUserAgreement());
            assertEquals("https://static.ttplace.ru/docs/oferta.pdf", footer.atrOffer());
//        assertEquals("https://static.ttplace.ru/docs/rykovodstvo.pdf", footer.atrManual());
            assertEquals("https://static.ttplace.ru/docs/personal_data.pdf", footer.atrPersonalDate());
            assertEquals("https://admin:JWwppsEA84B4BozJgE44sNiZZ@ttplace.ru/about-company", footer.atrAboutCompany());
            assertEquals("https://admin:JWwppsEA84B4BozJgE44sNiZZ@ttplace.ru/about-subscription", footer.atrAboutSubscription());
            assertEquals("https://admin:JWwppsEA84B4BozJgE44sNiZZ@ttplace.ru/contacts", footer.atrContacts());
            assertEquals("https://admin:JWwppsEA84B4BozJgE44sNiZZ@ttplace.ru/new-horizons", footer.atrNewHorizon());
            assertEquals("https://admin:JWwppsEA84B4BozJgE44sNiZZ@ttplace.ru/webmasters", footer.atrWebmasters());
            assertEquals("https://admin:JWwppsEA84B4BozJgE44sNiZZ@ttplace.ru/for-authors", footer.atrForAuthors());
//        assertEquals("/install", footer.atrInstall());
            assertEquals("https://admin:JWwppsEA84B4BozJgE44sNiZZ@ttplace.ru/vacancies", footer.atrCareer());
            assertEquals("mailto:support@tutorplace.ru", footer.atrEmailSupport());
            assertEquals("tel:+78005553282", footer.atrNumber());
        });
    }

    @Test
    @DisplayName("прокликивание футера")
    public void clickFooterTtpage() {
        String expected = "TutorPlace - Платформа для обучения, курсы по подписке, онлайн школа";
        assertEquals(expected, title());
        executeJavaScript("window.scrollTo(0, document.body.scrollHeight)");
        footer.clickAboutCompany();
        assertEquals("TutorPlace | О компании", title());
        footer.clickAboutSubscription();
        assertEquals("Подписка на курсы - онлайн обучение", title());
        footer.clickContacts();
        assertEquals("TutorPlace | Контакты", title());
        executeJavaScript("window.scrollTo(0, document.body.scrollHeight)");
        footer.clickNewHorizon();
        assertEquals("TutorPlace | Новые горизонты", title());
        footer.clickWebmasters();
        assertEquals("TutorPlace | Веб-Мастерам", title());
        footer.clickForAuthors();
        assertEquals("TutorPlace | Для авторов", title());
        executeJavaScript("window.scrollTo(0, document.body.scrollHeight)");
        footer.clickCareer();
        assertEquals("TutorPlace | Вакансии", title());
    }

//    @Test
    @AllureId("13")
    @DisplayName("api product ")
    public void ApiFooterMenuProduct() {
        step("Задаём список продуктов по которым необходимо пробежаться. Проверяем код ответа на 200", () -> {
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

            for (String url : api) {
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
        });
    }

    @Test
    @AllureId("1")
    @DisplayName("api category")
    public void ApiFooterMenuCategory() {

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