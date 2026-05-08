package ru.tutor.page;

import com.codeborne.selenide.SelenideElement;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.Keys;

import static org.junit.jupiter.api.Assertions.*;
import ru.tutor.config.TestConfig;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;

public class DevProduct {
    private final Faker faker = new Faker(Locale.ENGLISH);
    private static final SelenideElement btnSave = $$("button").find(exactText("Сохранить"));
    private final SelenideElement h2 = $$("h2").find(exactText("Редактирование продукта"));
    private final SelenideElement h1 = $("h1");
    private final SelenideElement card1Title = $("div:nth-child(2) > div:nth-child(2) > div.input__wrapper > input");
    private final SelenideElement card2Title = $("div:nth-child(2) > div:nth-child(4) > div.input__wrapper > input");
    private final SelenideElement card3Title = $("div:nth-child(2) > div:nth-child(6) > div.input__wrapper > input");
    private final SelenideElement label = $$("label").find(exactText("Навык 1"));
    private final SelenideElement description = $$("[name=\"description\"]").get(3);
    private final SelenideElement sideBArProduct = $$("span").find(exactText("Продукты"));
    private final SelenideElement pencil = $(".table__inner > div:nth-child(3) > div.tableRow__item.tableRow__item--svg.tableRow__item--m > a > svg > path:nth-child(3)");

    public void textH2() {
        step("проверяем, что попали в редактор", () ->
            Assertions.assertEquals("Редактирование продукта", h2.getText().trim())
        );
    }

    public void textH1() {
        step("проверяем текст заголовка Н1", () ->
            Assertions.assertEquals("Продукты", h1.getText().trim())
        );
    }

    public void scrollToElement() {
        step("прокручиваем до элемента", () ->
            executeJavaScript("arguments[0].scrollIntoView(true);", label)
        );
    }

    public void inputText() {
        step("вводим текст в первый модуль", () ->
            description.sendKeys("навык-приветствия")
        );
    }

    public void assertionsText() {
        step("проверяем ранее набранный текст", () ->
            Assertions.assertEquals("навык-приветствия", description.getText().trim())
        );
    }

    public void deleteText() {
        step("удаляем текст в поле", () ->
            description.sendKeys(Keys.CONTROL + "A" + Keys.DELETE)
        );
    }

    public void cleanText() {
        step("проверяем что поле пустое", () ->
            Assertions.assertEquals("", description.getText().trim())
        );
    }

    public void scrollToBtnSave() {
        step("прокручиваем до элемента", () ->
            executeJavaScript("arguments[0].scrollIntoView(true);", btnSave)
        );
        step("кликаем по кнопке", () -> {
            btnSave.click();
            sleep(2000);
        });
    }

    public void visibleCardTitle() {
        step("видимость заголовков", () -> {
            step("заголовок1", () ->
                card1Title.shouldBe(visible)
            );
            step("заголовок2", () ->
                card2Title.shouldBe(visible)
            );
            step("заголовок3", () ->
                card3Title.shouldBe(visible)
            );
        });
    }

    public void clickPencil() {
        doGet();
    }

    public void doGet() {
        step("получаем токен", () -> {
        });
        String requestBody = "{\"username\": \"admin\", \"password\": \"admin\"}";
        Response response = RestAssured
                .given()

                .baseUri(TestConfig.getCurrentApiUrl())

                .contentType(ContentType.JSON)
                .body(requestBody)

                .when()
                .post("/login")

                .then()
                .statusCode(200)

                .extract().response();

        Map<String, String> token = response.getCookies();
        System.out.println("Это ваш токен " + token);

        step("Вносим изменения в существующий продукт, добавляем навыки", () -> {
            JSONObject body = new JSONObject();
            body.put("id", 525);
            body.put("slug", "тест");
            body.put("name", "тест");
            body.put("innerName", "тест");
            body.put("status", 1);
            body.put("public", false);
            body.put("addMaterialsCost", 49);

            // authors
            JSONArray authors = new JSONArray();
            JSONObject author = new JSONObject();
            author.put("id", 167);
            author.put("name", "Антон");
            author.put("profession", "телеграмщик");
            author.put("description", "");
            author.put("photoUrl", "https://static.dev.tutorplace.ru/photo/authors/167/02b14e53dd27d81f1bed.jpg?1731652941");
            authors.put(author);
            body.put("authors", authors);

            // categories
            JSONArray categories = new JSONArray();
            JSONObject category = new JSONObject();
            category.put("id", 129);
            category.put("name", "Тест 18+");
            category.put("slug", "Test-18");
            category.put("is18Plus", true);
            categories.put(category);
            body.put("categories", categories);

            // domains
            JSONArray domains = new JSONArray();
            JSONObject domain = new JSONObject();
            domain.put("id", 90);
            domain.put("name", "teststststst");
            domain.put("link", "teststststst");
            domains.put(domain);
            body.put("domains", domains);

            // recommendations
            JSONArray recommendations = new JSONArray();
            JSONObject rec = new JSONObject();
            rec.put("id", 519);
            rec.put("name", "авваыывыаыв");
            recommendations.put(rec);
            body.put("recommendations", recommendations);

            // skills
            JSONArray skills = new JSONArray();
            skills.put("шмыга злой хоббит. Кто с этим поспорит");
            skills.put("добавим ещё одного персонажа");
            body.put("skills", skills);

            // productCategories
            JSONArray productCategories = new JSONArray();
            productCategories.put("Тест 18+");
            body.put("productCategories", productCategories);

            // примитивные поля
            body.put("card1Description", "uerhg");
            body.put("card1Title", "");
            body.put("card2Description", "");
            body.put("card2Title", "");
            body.put("card3Description", "");
            body.put("card3Title", "");
            body.put("certIssueAvailable", true);
            body.put("codeName", "");
            body.put("complexity", 0);
            body.put("contentAccessDays", 7);
            body.put("contentAvailable", true);
            body.put("courseNovelty", 0);
            body.put("cover", "https://static.dev.tutorplace.ru/cover/products/b9ecee914e45837cf788.png?1758617748");
            body.put("coverMobile", "https://static.dev.tutorplace.ru/coverMobile/products/0ac37a91c183d47b68c0.png?1758617748");
            body.put("currency", "AMD");
            body.put("dashboard", "spy-heart");
            body.put("description", "тест");
            body.put("displayCurrency", "AZN");
            body.put("displayOldPrice", 1);
            body.put("displayPrice", 1);
            body.put("dropChanceAllIssuer", 0);
            body.put("dropChanceSber", 0);
            body.put("dropChanceTink", 0);
            body.put("files", new JSONArray());
            body.put("firstCheckbox", "ваываыва");
            body.put("followingInterval", 168);
            body.put("initialInterval", 72);
            body.put("initialPrice", 1);
            body.put("isCourseOfWeek", false);
            body.put("isFreeRegistration", false);
            body.put("isLightWeight", false);
            body.put("isLite", false);
            body.put("isPopular", true);
            body.put("isPremium", false);
            body.put("isPriceFull", false);
            body.put("isRecommendedHidden", false);
            body.put("isRegistrationWithoutPayment", false);
            body.put("isRenewal", false);
            body.put("isVideoPreviewEnabled", false);
            body.put("language", "ru");
            body.put("mainCategory", "Тест 18+");
            body.put("modulesCount", 1);
            body.put("nextLessonDays", 7);
            body.put("numberContentsToOpen", 7);
            body.put("oldPrice", 1);
            body.put("paymentsLadder", "");
            body.put("pixels", new JSONArray());
            body.put("portionSize", 24);
            body.put("possibilityRepaymentForHalfPrice", false);
            body.put("prepaidPrice", 450);
            body.put("previews", new JSONArray());
            body.put("price", 1);
            body.put("promo", false);
            body.put("promoDate", "2025-07-29 13:15:31");
            body.put("publicDate", "2025-07-29 13:15:31");
            body.put("rating", 5);
            body.put("recalculateDisplayPrice", true);
            body.put("refundInitial", true);
            body.put("reviews", new JSONArray());
            body.put("secondCheckbox", "ываываыва");
            body.put("shortDescription", "");
            body.put("tariff", 0);
            body.put("tariffDescription", "");
            body.put("workScheme", 0);

            // Отправляем запрос
            given()
                    .baseUri(TestConfig.getCurrentAdminDomain())
                    .contentType(ContentType.JSON)
                    .cookies(token)                                 // твои cookies для авторизации
                    .body(body.toString())                          // сериализованный JSON
                    .when()
                    .put("/products/525")
                    .then()
                    .log().all()
                    .statusCode(200);                               // ожидаем успех
        });

        step("проверяем что навыки добавились", () -> {
            Response skillsResponse = given()
                    .baseUri(TestConfig.getCurrentAdminDomain())
                    .cookies(token)
                    .when()
                    .get("/products/525");

            skillsResponse.then()
                    .log().all()
                    .statusCode(200);

            // Проверяем содержимое skills
            List<String> skills = skillsResponse.jsonPath().getList("skills", String.class);
            assertNotNull(skills, "Skills не должен быть null");
            assertTrue(skills.contains("шмыга злой хоббит. Кто с этим поспорит"),
                    "Skills должен содержать 'шмыга злой хоббит. Кто с этим поспорит'");
            assertTrue(skills.contains("добавим ещё одного персонажа"),
                    "Skills должен содержать 'добавим ещё одного персонажа'");
        });
    }
}
