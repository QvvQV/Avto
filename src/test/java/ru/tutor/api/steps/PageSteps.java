package ru.tutor.api.steps;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.jsoup.Jsoup;
import ru.tutor.page.api.PageApi;

import org.jsoup.nodes.Document;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PageSteps {
    private final PageApi pageApi;

    public PageSteps(String baseUrl, String username, String password){
        this.pageApi = new PageApi(baseUrl, username, password);
    }

    /**
     * Проверка списка страниц: каждая должна вернуть 200
     * Тест продолжает выполнение даже если одна страница упала
     */

    @Step ("Проверка всех статических страниц")
    public void checkAllPage(List<String> endpoints){
        Set<String> failedPages = new LinkedHashSet<>();

        for (String endpoint : endpoints) {
            try {
                Response resp = pageApi.getPage(endpoint);

                //извлекаем title
                String title = "";
                if (resp.getContentType().contains("text/html")) {
                    Document doc = Jsoup.parse(resp.asString());
                    title = doc.title();
                }

                // Вложенный шаг в Allure с title и кодом ответа
                Allure.step("title: " + title + " - статус: " + resp.statusCode(), ()->{
                    String requestLog = "Get " + pageApi.getBaseUrl() + endpoint +
                            "\nAuthorization: " + pageApi.getAuthHeader();
                    Allure.addAttachment("Request", "text/plain", requestLog, ".txt");
                    Allure.addAttachment("Response Body", "text/plain", resp.asPrettyString(), ".txt");
                });

                if (resp.statusCode() != 200){
                    failedPages.add(endpoint + " (" + resp.statusCode() + ")");
                }
            } catch (Exception e) {
                failedPages.add(endpoint + " (exception: " + e.getMessage() + ")");
            }
        }

        // Проверка в конце: если есть упавшие страницы, тест упадет с описанием
        assertTrue(failedPages.isEmpty(),
                "Следующие страницы вернули ошибки: " + String.join(", ", failedPages));
    }

    @Step("Get запрос страницы {endpoint}")
    public Response getPage(String endpoint) {
        return pageApi.getPage(endpoint);
    }

    @Step("Post запрос на {endpoint}")
    public Response createPage(String endpoint, Object body) {
        return pageApi.createPage(endpoint, body);
    }

    @Step("PUT запрос на {endpoint}")
    public Response updatePage(String endpoint, Object body) {
        return pageApi.updatePage(endpoint, body);
    }

    @Step("Delete запрос на {endpoint}")
    public Response deletePage(String endpoint){
        return pageApi.deletePage(endpoint);
    }
}
