package ru.tutor.api;

import io.restassured.response.Response;
import ru.tutor.base.BaseApi;
import ru.tutor.config.ProductConfig;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class ProductApi extends BaseApi {

    public ProductApi(String baseUrl) {
        super(baseUrl);
    }

    public Response getProductInfo(String productId) {
        String apiUrl = ProductConfig.getProductApiUrl(productId);
        if (apiUrl == null) {
            throw new IllegalArgumentException("Unknown product ID: " + productId);
        }

        return given(baseSpec)
                .headers(getDefaultHeaders())
                .queryParams(getQueryParams(productId))
            .when()
                .get(apiUrl);
    }

    private Map<String, String> getDefaultHeaders() {
        return Map.of(
            "Accept", "*/*",
            "Accept-Language", "en-US,en;q=0.9",
            "Cache-Control", "no-cache",
            "Origin", "https://psykatalog.tutorplace.ru",
            "Referer", "https://psykatalog.tutorplace.ru/",
            "X-Requested-With", "XMLHttpRequest",
            "User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
        );
    }

    private Map<String, String> getQueryParams(String productId) {
        return Map.of(
            "utm_source", "cpaws",
            "aff_rid", "1012_ELQp",
            "_ym_debug", "1",
            "utm_product", productId
        );
    }
}
