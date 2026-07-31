package ru.tutor.base;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.chrome.ChromeOptions;
import ru.tutor.config.TestConfig;
import java.io.IOException;


/**
 * Класс для выставления параметров по разным сценариям
 */
public abstract class BaseUiTest {

//    TODO: может пригодится если сама не сможет найти драйвер
//     static {
// TODO:
//        Явно указываем путь к драйверу 142.x
//        String driverPath = "C:\\Users\\user445\\.cache\\selenium\\chromedriver\\win64\\142.0.7444.60\\chromedriver.exe";
//        System.setProperty("webdriver.chrome.driver", driverPath);
//    }


    @BeforeAll
    public static void setupSelenide() throws IOException {
//        verifyDriverAvailable();
        setupBrowserWithIncognito(); // По умолчанию с incognito
    }


    // Установка параметров для браузера с Инкогнито для UI тестов
    protected static void setupBrowserWithIncognito() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--incognito",
                "--disable-dev-shm-usage",
                "--no-sandbox",
                "--disable-gpu",
                "--log-level=3",
                "--disable-extensions",
                "--disable-background-timer-throttling");

//        options.addArguments("--headless=new");
        applyCommonConfig(options);
    }

    // Установка параметров для браузера без Инкогнито для тестов связанных с админкой
    protected static void setupBrowserWithoutIncognito() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--disable-dev-shm-usage",
                "--no-sandbox",
                "--disable-gpu",
                "--log-level=3");
//        options.addArguments("--headless=new");
        applyCommonConfig(options);
    }

    private static void applyCommonConfig(ChromeOptions options) {
        Configuration.browserCapabilities = options;
        Configuration.browserSize = "1920x1080";
        Configuration.timeout = TestConfig.getTimeouts();
        Configuration.pageLoadTimeout = TestConfig.getPageLoadTimeout();
        Configuration.headless = false;

        SelenideLogger.addListener("AllureSelenide", new AllureSelenide()
                .screenshots(true)
                .savePageSource(true)
                .includeSelenideSteps(true));
    }
}