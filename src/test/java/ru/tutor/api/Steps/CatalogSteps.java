package ru.tutor.api.Steps;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import ru.tutor.page.Api.CatalogApi;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CatalogSteps {
    private final CatalogApi catalogApi;

    public CatalogSteps(String baseUrl, String username, String password) {
        this.catalogApi = new CatalogApi(baseUrl, username, password);
    }

    @Step("Проверка всех страниц каталога")
    public void checkAllCatalogPages(List<String> endpoints) {
        Set<String> uniqueEndpoints = new LinkedHashSet<>(endpoints);

        List<String> failedEndpoints = new ArrayList<>();

        for (String endpoint : uniqueEndpoints) {
            boolean success = checkCatalogEndpoint(endpoint);
            if (!success) {
                failedEndpoints.add(endpoint);
            }
        }

        // В конце проверяем, есть ли упавшие страницы
        assertTrue(failedEndpoints.isEmpty(),
                "Следующие эндпоинты вернули не 200: " + String.join(", ", failedEndpoints));
    }

    @Step("Проверка каталога: {endpoint}")
    private boolean checkCatalogEndpoint(String endpoint) {
        try {
            Response resp = catalogApi.getCatalog(endpoint);

            boolean ok = resp.statusCode() == 200;

            // Извлекаем title страницы из HTML
            String title = "";
            if (resp.getContentType().contains("text/html")) {
                Document doc = Jsoup.parse(resp.asString());
                title = doc.title();
            } else if (resp.getContentType().contains("application/json")) {
                title = resp.jsonPath().getString("title");
            }

            // Вложенный шаг с title и кодом

            Allure.step("title: " + title + " — статус: " + resp.statusCode(), () -> {
                String requestLog = "GET " + catalogApi.getBaseUrl() + "/catalog/" + endpoint +
                        "\nAuthorization: " + catalogApi.getAuthHeader();
                Allure.addAttachment("Request", "text/plain", requestLog, ".txt");
                Allure.addAttachment("Response Body", "text/plain", resp.asPrettyString(), ".txt");
            });

            return ok;
        } catch (Exception e) {
            // Логируем исключение, чтобы тест продолжил выполнение
            Allure.step("Эндпоинт " + endpoint + " вызвал исключение: " + e.getMessage());
            return false;
        }
    }

    @Step("Получение каталога по endpoint: {endpoint}")
    public Response getCatalog(String endpoint) {
        return catalogApi.getCatalog(endpoint);
    }

    @Step("Создание элемента каталога в {endpoint}")
    public Response createCatalogItem(String endpoint, String body) {
        return catalogApi.createCatalogItem(endpoint, body);
    }

    @Step("Обновление элемента каталога в {endpoint}")
    public Response updateCatalogItem(String endpoint, String body) {
        return catalogApi.updateCatalogItem(endpoint, body);
    }

    @Step("Удаление элемента каталога из {endpoint}")
    public Response deleteCatalogItem(String endpoint) {
        return catalogApi.deleteCatalogItem(endpoint);
    }
}
