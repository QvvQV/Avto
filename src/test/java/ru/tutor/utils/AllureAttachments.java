package ru.tutor.utils;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;

import java.io.ByteArrayInputStream;

public class AllureAttachments {

    private AllureAttachments() {}

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
        Allure.addAttachment(name, "image/png",
                new ByteArrayInputStream(Selenide.screenshot(OutputType.BYTES)), "png");
    }

    public static void attachPageSource(){
        Allure.addAttachment("Page Source", "text/html",
                com.codeborne.selenide.WebDriverRunner.getWebDriver().getPageSource());
    }

}