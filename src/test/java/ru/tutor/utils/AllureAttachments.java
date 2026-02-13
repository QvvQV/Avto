package ru.tutor.utils;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Allure;
import io.restassured.http.Headers;
import org.openqa.selenium.OutputType;

import java.io.ByteArrayInputStream;
import java.util.Objects;

/**
 * Класс для Отчётов
 */

public class AllureAttachments {

    private AllureAttachments() {
    }

    public static void attachText(String name, String content) {
        Allure.addAttachment(name, "text/plain", content);
    }

    public static void attachJson(String name, String jsonContent) {
        Allure.addAttachment(name, "application/json", jsonContent);
    }

    public static void attachProductComparison(String productId, String expected, String actual) {
        String comparison = String.format(
                "Product: %s\nExpected: %s\nActual: %s\nMatch: %s",
                productId, expected, actual, expected.equals(actual) ? "✅" : "❌"
        );
        attachText("Product Name Comparison - " + productId, comparison);
    }

        public static void attachScreenshot(String name) {
        Allure.addAttachment(name,
            new ByteArrayInputStream(Objects.requireNonNull(Selenide.screenshot(OutputType.BYTES))));
    }

    public static void attachHeaders(String name, Headers headers) {
    StringBuilder headersText = new StringBuilder();
    headers.forEach(header ->
        headersText.append(header.getName())
                  .append(": ")
                  .append(header.getValue())
                  .append("\n"));

    attachText(name, headersText.toString());
}
}