package ru.tutor.utils;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Allure;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class UrlRedirectHelper {

    private UrlRedirectHelper() {}

    /**
     * Заменяет домен в URL (исправленная версия)
     */
    public static String replaceDomain(String originalUrl, String newDomain) {
        try {
            // Убираем протокол из newDomain если он есть
            String cleanNewDomain = newDomain.replaceAll("^https?://", "");

            // Заменяем домен в URL
            String modifiedUrl = originalUrl.replaceFirst(
                "^(https?://)[^/]+",
                "$1" + cleanNewDomain
            );

            Allure.addAttachment("URL модификация",
                "text/plain",
                "Исходный: " + originalUrl + "\nИзмененный: " + modifiedUrl);

            return modifiedUrl;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при модификации URL: " + originalUrl, e);
        }
    }

    /**
     * Простая замена домена через регулярное выражение
     */
    public static String simpleReplaceDomain(String originalUrl, String targetDomain) {
        // Убеждаемся, что targetDomain не содержит протокол
        String cleanTargetDomain = targetDomain.replaceAll("^https?://", "");

        // Заменяем весь домен (все что между :// и следующим /)
        String modifiedUrl = originalUrl.replaceFirst(
            "://[^/]+",
            "://" + cleanTargetDomain
        );

        Allure.addAttachment("Простая замена домена",
            "text/plain",
            "Исходный: " + originalUrl + "\nЦелевой домен: " + cleanTargetDomain + "\nРезультат: " + modifiedUrl);

        return modifiedUrl;
    }

    /**
     * Перехватывает редирект и модифицирует URL перед переходом
     */
    public static void interceptAndModifyRedirect(String expectedRedirectPattern, String targetDomain) {
        WebDriver driver = WebDriverRunner.getWebDriver();

        // Убираем протокол из targetDomain
        String cleanTargetDomain = targetDomain.replaceAll("^https?://", "");

        // Используем JavaScript для перехвата редиректов
        String script =
            "window.originalOpen = window.open;" +
            "window.open = function(url, target, features) {" +
            "   if (url && url.match('" + expectedRedirectPattern + "')) {" +
            "       var modifiedUrl = url.replace(/https?:\\/\\/[^\\/]+/, 'https://" + cleanTargetDomain + "');" +
            "       console.log('Modified redirect: ' + modifiedUrl);" +
            "       return window.originalOpen(modifiedUrl, target, features);" +
            "   }" +
            "   return window.originalOpen(url, target, features);" +
            "};" +
            "" +
            "window.originalLocationAssign = window.location.assign;" +
            "window.location.assign = function(url) {" +
            "   if (url && url.match('" + expectedRedirectPattern + "')) {" +
            "       var modifiedUrl = url.replace(/https?:\\/\\/[^\\/]+/, 'https://" + cleanTargetDomain + "');" +
            "       console.log('Modified location assign: ' + modifiedUrl);" +
            "       return window.originalLocationAssign(modifiedUrl);" +
            "   }" +
            "   return window.originalLocationAssign(url);" +
            "};";

        ((JavascriptExecutor) driver).executeScript(script);
    }

    /**
     * Модифицирует атрибут href у элемента (ИСПРАВЛЕННАЯ ВЕРСИЯ)
     */
    public static void modifyElementHref(String cssSelector, String newDomain) {
        WebDriver driver = WebDriverRunner.getWebDriver();

        // Убираем протокол из newDomain
        String cleanNewDomain = newDomain.replaceAll("^https?://", "");

        // ИСПРАВЛЕНИЕ: используем двойные кавычки для селектора
        String script = String.format(
            "var element = document.querySelector(\"%s\");" +
            "if (element) {" +
            "   var originalHref = element.getAttribute(\"href\");" +
            "   if (originalHref) {" +
            "       var modifiedHref = originalHref.replace(/https?:\\/\\/[^\\/]+/, \"https://%s\");" +
            "       element.setAttribute(\"href\", modifiedHref);" +
            "       console.log(\"Modified href: \" + modifiedHref);" +
            "       return {\"original\": originalHref, \"modified\": modifiedHref};" +
            "   }" +
            "}" +
            "return null;",
            cssSelector.replace("\"", "\\\""), // Экранируем кавычки в селекторе
            cleanNewDomain
        );

        Object result = ((JavascriptExecutor) driver).executeScript(script);

        if (result != null) {
            Allure.addAttachment("Модификация href",
                "text/plain",
                "Селектор: " + cssSelector + "\n" +
                "Результат: " + result.toString());
        }
    }

    /**
     * Модифицирует атрибут href у элемента по XPath (альтернатива)
     */
    public static void modifyElementHrefByXPath(String xpath, String newDomain) {
        WebDriver driver = WebDriverRunner.getWebDriver();

        // Убираем протокол из newDomain
        String cleanNewDomain = newDomain.replaceAll("^https?://", "");

        String script = String.format(
            "var element = document.evaluate(\"%s\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;" +
            "if (element) {" +
            "   var originalHref = element.getAttribute(\"href\");" +
            "   if (originalHref) {" +
            "       var modifiedHref = originalHref.replace(/https?:\\/\\/[^\\/]+/, \"https://%s\");" +
            "       element.setAttribute(\"href\", modifiedHref);" +
            "       console.log(\"Modified href by XPath: \" + modifiedHref);" +
            "       return {\"original\": originalHref, \"modified\": modifiedHref};" +
            "   }" +
            "}" +
            "return null;",
            xpath.replace("\"", "\\\""),
            cleanNewDomain
        );

        Object result = ((JavascriptExecutor) driver).executeScript(script);

        if (result != null) {
            Allure.addAttachment("Модификация href по XPath",
                "text/plain",
                "XPath: " + xpath + "\n" +
                "Результат: " + result.toString());
        }
    }

    /**
     * Ожидает редирект и возвращает модифицированный URL (исправленная версия)
     */
    public static String waitAndModifyRedirect(String originalUrl, String newDomain, long timeoutMs) {
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < timeoutMs) {
            String currentUrl = WebDriverRunner.getWebDriver().getCurrentUrl();

            if (!currentUrl.equals(originalUrl)) {
                // Произошел редирект - модифицируем URL
                String modifiedUrl = simpleReplaceDomain(currentUrl, newDomain);
                Selenide.open(modifiedUrl);

                Allure.addAttachment("Обработан редирект",
                    "text/plain",
                    "Исходный редирект: " + currentUrl + "\n" +
                    "Модифицированный: " + modifiedUrl);

                return modifiedUrl;
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        throw new RuntimeException("Редирект не произошел в течение " + timeoutMs + "мс");
    }

    /**
     * Простая проверка и модификация текущего URL
     */
    public static void checkAndFixCurrentUrl(String expectedDomain) {
        String currentUrl = WebDriverRunner.getWebDriver().getCurrentUrl();
        String cleanExpectedDomain = expectedDomain.replaceAll("^https?://", "");

        if (!currentUrl.contains(cleanExpectedDomain)) {
            String fixedUrl = simpleReplaceDomain(currentUrl, expectedDomain);
            Selenide.open(fixedUrl);

            Allure.addAttachment("Исправлен текущий URL",
                "text/plain",
                "Был: " + currentUrl + "\nСтал: " + fixedUrl);
        }
    }

    /**
     * Модифицирует все ссылки на странице с определенным селектором
     */
    public static void modifyAllLinksWithSelector(String cssSelector, String newDomain) {
        WebDriver driver = WebDriverRunner.getWebDriver();

        // Убираем протокол из newDomain
        String cleanNewDomain = newDomain.replaceAll("^https?://", "");

        String script = String.format(
            "var elements = document.querySelectorAll(\"%s\");" +
            "var results = [];" +
            "for (var i = 0; i < elements.length; i++) {" +
            "   var element = elements[i];" +
            "   var originalHref = element.getAttribute(\"href\");" +
            "   if (originalHref) {" +
            "       var modifiedHref = originalHref.replace(/https?:\\/\\/[^\\/]+/, \"https://%s\");" +
            "       element.setAttribute(\"href\", modifiedHref);" +
            "       results.push({\"original\": originalHref, \"modified\": modifiedHref});" +
            "   }" +
            "}" +
            "return results;",
            cssSelector.replace("\"", "\\\""),
            cleanNewDomain
        );

        Object result = ((JavascriptExecutor) driver).executeScript(script);

        if (result != null) {
            Allure.addAttachment("Модификация всех ссылок",
                "text/plain",
                "Селектор: " + cssSelector + "\n" +
                "Результаты: " + result.toString());
        }
    }
}
