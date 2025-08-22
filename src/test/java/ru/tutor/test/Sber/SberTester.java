package ru.tutor.test.Sber;


import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Attachment;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeOptions;
import ru.tutor.page.SberPage;
import ru.tutor.page.addressPage;
import ru.tutor.page.ttPage;

import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;


public class SberTester {

    //    @BeforeEach
    @BeforeAll
    public static void setupAll() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        Configuration.browserCapabilities = options;
        Configuration.browserSize = "1920x1080";
        SelenideLogger.addListener("allure.properties", new AllureSelenide()
                .screenshots(true)
                        .savePageSource(true));
    }

    @Attachment(value = "Screenshot", type = "image/png", fileExtension = "png")
    public void takeScreenshot() {
        ((TakesScreenshot) WebDriverRunner.getWebDriver()).getScreenshotAs(OutputType.BYTES);
    }

    @AfterAll
    public static void quitDriver() {
        closeWebDriver();
        SelenideLogger.removeListener("allure.properties");
    }


    @Test
//        @RepeatedTest(3)
    @DisplayName("Оплата по валидной карте с продукта exel")
    public void ShowModalVersionFromTester() {
        step("Открытие страницы", () -> {
        open(addressPage.urlExcel);});

        step("проверяем title", () -> {
            String expected = "Excel-таблицы для начинающих - курс, онлайн обучение бесплатно по подписке";
            Assertions.assertEquals(expected, title());
        });
        SberPage.clickBtnStart();
        SberPage.getApprovedCard();
    }

    @Test
    @DisplayName("Оплата с главной за 99 руб")
    public void PayMain99() {
        step("открытие главной стр", () -> {
            open(addressPage.urlMain);
        });
        step("проверяем title", () -> {
            String expected = "TutorPlace - Платформа для обучения, курсы по подписке, онлайн школа";
            Assertions.assertEquals(expected, title());
        });
        ttPage.clickBtnOpenFor();
        ttPage.clickLite99();
        SberPage.changeWindowHandle();
        SberPage.getApprovedCard();
    }

    @Test
    @DisplayName("Оплата с главной за 399 руб")
    public void PayMain399() {
        step("открытие главной стр", () -> {
            open(addressPage.urlMain);
        });
        step("проверяем title", () -> {
            String expected = "TutorPlace - Платформа для обучения, курсы по подписке, онлайн школа";
            Assertions.assertEquals(expected, title());
        });

        ttPage.clickBtnOpenFor();
        ttPage.clickPremium();
        SberPage.changeWindowHandle();
        SberPage.getApprovedCard();
    }

    @Test
    @DisplayName("Оплата с главной за Пожизненный")
    public void PayMainLife() {
        step("открытие главной стр", () -> {
            open(addressPage.urlMain);
        });
        step("проверяем title", () -> {
            String expected = "TutorPlace - Платформа для обучения, курсы по подписке, онлайн школа";
            Assertions.assertEquals(expected, title());
        });

        ttPage.clickBtnOpenFor();
        ttPage.clickLife();
        SberPage.changeWindowHandle();
        SberPage.getApprovedCard();
    }


    @Test
//        @RepeatedTest(3)
    @DisplayName("Оплата по карте с остатком 100р")
    public void PayFromFixBalanceCard() {
        step("Открытие страницы", () -> {
        open(addressPage.urlExcel);});

        step("проверяем title", () -> {
            String expected = "Excel-таблицы для начинающих - курс, онлайн обучение бесплатно по подписке";
            Assertions.assertEquals(expected, title());
        });
        SberPage.clickBtnStart();
        SberPage.getFixBalanceCard();
        takeScreenshot();
    }

    @Test
//        @RepeatedTest(3)
    @DisplayName("Оплата по карте недостаточно средств")
    public void PayFromCardError116() {
        step("Открытие страницы", () -> {
        open(addressPage.urlExcel);});

        step("проверяем title", () -> {
            String expected = "Excel-таблицы для начинающих - курс, онлайн обучение бесплатно по подписке";
            Assertions.assertEquals(expected, title());
        });
        SberPage.clickBtnStart();
        SberPage.getNoMoneyCard();
        step("Проверяем текст ошибки", () -> {
            Assertions.assertEquals("Операция отклонена. Проверьте введенные данные, достаточность средств на карте и повторите операцию", SberPage.getTextError());
        });
        takeScreenshot();
    }

    @Test
//        @RepeatedTest(3)
    @DisplayName("Оплата по просроченной карты")
    public void PayFromCardError101() {
        step("Открытие страницы", () -> {
        open(addressPage.urlExcel);});

        step("проверяем title", () -> {
            String expected = "Excel-таблицы для начинающих - курс, онлайн обучение бесплатно по подписке";
            Assertions.assertEquals(expected, title());
        });
        SberPage.clickBtnStart();
        SberPage.getExpireCard();
        step("Проверяем текст ошибки", () -> {
            Assertions.assertEquals("Операция отклонена. Проверьте введенные данные, достаточность средств на карте и повторите операцию", SberPage.getTextError());
        });
    }

    //https://dev.tutorplace.ru/catalog/product/excel?aff_rid=1012_EN5U - CPA
    //https://dev.tutorplace.ru/catalog/product/excel?aff_rid=1012_EN5S - Гибрид
//    https://dev.tutorplace.ru/catalog/product/excel?aff_rid=1012_EN5Q - RevShare
}