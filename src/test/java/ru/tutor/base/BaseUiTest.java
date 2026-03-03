package ru.tutor.base;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.chrome.ChromeOptions;


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
    public static void setupSelenide() {
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
        Configuration.timeout = 10000;
        Configuration.pageLoadTimeout = 30000;
        Configuration.headless = false;

        SelenideLogger.addListener("AllureSelenide", new AllureSelenide()
                .screenshots(true)
                .savePageSource(true)
                .includeSelenideSteps(true));
    }

//    private static void verifyDriverAvailable() {
//        String driverPath = System.getProperty("webdriver.chrome.driver");
//        File driver = new File(driverPath);
//
//        if (driver.exists()) {
//            System.out.println("✅ ChromeDriver 142.x доступен: " + driverPath);
//        } else {
//            System.err.println("❌ ChromeDriver не найден: " + driverPath);
//            System.err.println("Скачайте вручную и распакуйте:");
//            System.err.println("https://storage.googleapis.com/chrome-for-testing-public/142.0.7444.60/win64/chromedriver-win64.zip");
//        }
    }

//    static

    // КРИТИЧЕСКИ ВАЖНЫЕ НАСТРОЙКИ ДО ЛЮБОЙ ИНИЦИАЛИЗАЦИИ SELENIDE
//        System.setProperty("webdriver.manager.enabled", "false");
//        System.setProperty("selenium.manager.enabled", "false");
//        System.setProperty("se:manager", "false");
//        System.setProperty("webdriver.manager.disabled", "true");

        // Блокируем сетевые запросы Selenium Manager
//        System.setProperty("webdriver.manager.check.interval", "0");
//        System.setProperty("webdriver.manager.cache.valid", "9999");

            // Отключаем ВСЕ логи Selenium
//        System.setProperty("webdriver.chrome.silentOutput", "true");
//        System.setProperty("webdriver.chrome.silentLog", "true");
//        System.setProperty("wdm.printStackTraces", "false");

        // Отключаем логирование на уровне JUL
//        java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(java.util.logging.Level.OFF);
//        java.util.logging.Logger.getLogger("com.codeborne.selenide").setLevel(java.util.logging.Level.OFF);
//}
