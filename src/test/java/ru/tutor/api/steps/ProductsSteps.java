package ru.tutor.api.steps;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.tutor.api.catalog.CatalogProductsApi;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;


public class ProductsSteps {
    private final CatalogProductsApi productApi;

    public ProductsSteps(String baseUrl, String login, String password) {
        this.productApi = new CatalogProductsApi(baseUrl, login, password);
    }

    @Step("Проверка всех продуктов каталога")
    public void checkAllProductsAvailable() {
        List<String> codes = getAllProductCodes();

        if (codes.isEmpty()) {
            Allure.step("Список продуктов пуст!");
            return;
        }

        for (String code : codes) {
            checkProductAvailable(code);
        }
    }

    @Step("Сбор кодов всех продуктов со всех страниц каталога")
    private List<String> getAllProductCodes() {
        return step("Сбор кодов всех продуктов со всех страниц каталога", () -> {
            Set<String> codes = new LinkedHashSet<>();
            int page = 1;

            while (true) {
                Response resp = productApi.getCatalogPage(page);
                resp.then().statusCode(200);

                Document doc = Jsoup.parse(resp.asString());

                // Найдём все ссылки, у которых href содержит "/catalog/product/"
                Elements links = doc.select("a[href*=/catalog/product/]");

                if (links.isEmpty()) {
                    break;  // больше нет страниц с товарами
                }

                for (Element link : links) {
                    String href = link.attr("href");
                    // href может быть вида "/catalog/product/ogebio" или "/catalog/product/ogebio?_rsc=..."
                    String path = href.split("\\?")[0];  // отделим параметры
                    String code = path.substring(path.lastIndexOf("/") + 1);
                    codes.add(code);
                }

                page++;
            }

            return new ArrayList<>(codes);
        });

    }

    @Step("Получить продукт с кодом: {productCode}")
    private void checkProductAvailable(String productCode) {
        // Формируем URL и авторизацию для логирования
        String url = productApi.getBaseUrl() + "/catalog/product/" + productCode;
        String auth = productApi.getAuthHeader();

        // Отправляем запрос через RestAssured и логируем через AllureRestAssured
        Response resp = given()
                .header("Authorization", auth)
                .header("Accept", "*/*")
                .filter(new io.qameta.allure.restassured.AllureRestAssured())
                .get(url)
                .then()
                .extract()
                .response();

        resp.then().statusCode(200);

        // Извлекаем title продукта
        String title;
        if (resp.getContentType().contains("application/json")) {
            title = resp.jsonPath().getString("title");
        } else {
            title = Jsoup.parse(resp.asString()).title();
        }

        // Вложенный шаг с названием продукта
        Allure.step("Продукт: " + title + " - статус: " + resp.statusCode(), () -> {
            // Вложенный attachment с запросом
            String requestLog = "GET " + url + "\nAuthorization: " + auth;
            Allure.addAttachment("Request", "text/plain", requestLog, ".txt");

            // Вложенный attachment с ответом
            Allure.addAttachment("Response Body", "text/plain", resp.asPrettyString(), ".txt");
        });
    }
}
