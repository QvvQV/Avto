package ru.tutor.base;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.chrome.ChromeOptions;

public abstract class BaseUiTest {

    @BeforeAll
    public static void setupSelenide() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito", "--disable-dev-shm-usage", "--no-sandbox");
        options.addArguments("--headless");

        Configuration.browserCapabilities = options;
        Configuration.browserSize = "1920x1080";
        Configuration.timeout = 10000;
        Configuration.pageLoadTimeout = 30000;
        Configuration.headless = false; // headless режим для CI

        // Логирование шагов в Allure
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide()
                .screenshots(true)
                .savePageSource(true)
                .includeSelenideSteps(true));
    }
}
