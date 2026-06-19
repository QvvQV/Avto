package ru.tutor.base;

import com.microsoft.playwright.*;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.*;

import java.io.ByteArrayInputStream;
import java.util.List;

public class BaseUiPlaywright {

    protected static Playwright playwright;
    protected static Browser browser;
    protected BrowserContext context;
    protected Page page;

    // ==================== ЖИЗНЕННЫЙ ЦИКЛ ====================

    @BeforeAll
    static void setupPlaywright() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(Boolean.parseBoolean(System.getProperty("headless", "false")))
                .setArgs(List.of(
                        "--disable-dev-shm-usage",
                        "--no-sandbox",
                        "--disable-gpu",
                        "--log-level=3",
                        "--disable-extensions",
                        "--disable-background-timer-throttling",
                        "--headless=new"
                )));
    }

    @AfterAll
    static void tearDownPlaywright() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    @BeforeEach
    void setUp() {
        context = browser.newContext(new  Browser.NewContextOptions()
                .setViewportSize(1920, 1080));
        page = context.newPage();
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        if (page != null) {
            Allure.addAttachment("Скриншот",
                    new ByteArrayInputStream(page.screenshot()));
        }
        if (context != null) context.close();
    }

    // ==================== МЕТОДЫ-ПОМОЩНИКИ ====================

    protected void navigate(String url) {
        page.navigate(url);
    }

    protected String pageTitle() {
        return page.title();
    }

    protected String pageUrl() {
        return page.url();
    }

    protected void waitForContains(String text) {
        page.waitForURL("**" + text + "**");
    }

    protected void waitForLoad() {
        page.waitForLoadState();
    }

    protected void screenshot(String name) {
        Allure.addAttachment(name,
                new ByteArrayInputStream(page.screenshot()));
    }
}
