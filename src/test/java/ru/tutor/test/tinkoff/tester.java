package ru.tutor.test.tinkoff;


import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import org.openqa.selenium.chrome.ChromeOptions;
import ru.tutor.page.CloudPage;
import ru.tutor.page.ttPage;
import ru.tutor.page.addressPage;

import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;


public class tester {
//    public WebDriver driver;

//    final String url = "https://admin:JWwppsEA84B4BozJgE44sNiZZ@ttplace.ru/catalog/product/excel";

    //            @BeforeEach
    @BeforeAll
    public static void setupAll() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        Configuration.browserCapabilities = options;
        Configuration.browserSize = "1920x1080";
//        open(url);
        SelenideLogger.addListener("allure.properties", new AllureSelenide());

    }


    //                @AfterEach
//    @AfterAll
//    public static void tearDownAll() {
//        closeWebDriver();
//        SelenideLogger.removeListener("allure.properties");
//    }

    @Test
//        @RepeatedTest(2)
    @DisplayName("Оплата с продукта exel")
    public void PayFromExcel() {
        step("Открытие страницы", () -> {
        open(addressPage.urlExcel);});
        step("проверяем title", () -> {
            String expected = "Excel-таблицы для начинающих - курс, онлайн обучение бесплатно по подписке";
            Assertions.assertEquals(expected, title());
        });

        CloudPage.clickBtnStart();
        System.out.println("Оплата с продукта Excel за 1 руб: " + CloudPage.atrEmail());
        CloudPage.clickCardPageBtn();
        sleep(1000);
    }

    @Test
    @DisplayName("Оплата с главной за 99 руб")
    public void PayMain99() {
        step("Открытие страницы", () -> {
            open(addressPage.urlMain);
        });
        step("проверяем title", () -> {
            String expected = "TutorPlace - Платформа для обучения, курсы по подписке, онлайн школа";
            Assertions.assertEquals(expected, title());
        });

        ttPage.clickBtnOpenFor();
        ttPage.clickLite99();
        CloudPage.setDateModal();
        System.out.println("Оплата продукта с главной Лайт: " + CloudPage.atrEmail());
        CloudPage.clickCardPageBtn();
    }

    @Test
    @DisplayName("Оплата с главной за 399 руб")
    public void PayMain399() {
        step("Открытие страницы", () -> {
            open(addressPage.urlMain);
        });
        step("проверяем title", () -> {
            String expected = "TutorPlace - Платформа для обучения, курсы по подписке, онлайн школа";
            Assertions.assertEquals(expected, title());
        });

        ttPage.clickBtnOpenFor();
        ttPage.clickPremium();
        CloudPage.setDateModal();
        step("email пользователя", () -> {
            System.out.println("Оплата продукта с главной Премиум: " + CloudPage.atrEmail());
        });
        CloudPage.clickCardPageBtn();
    }

    @Test
    @DisplayName("Оплата с главной за Пожизненный")
    public void PayMainLife() {
        step("Открытие страницы", () -> {
            open(addressPage.urlMain);
        });
        step("проверяем title", () -> {
            String expected = "TutorPlace - Платформа для обучения, курсы по подписке, онлайн школа";
            Assertions.assertEquals(expected, title());
        });

        ttPage.clickBtnOpenFor();
        ttPage.clickLife();
        CloudPage.setDateModal();
        System.out.println("Оплата продукта с главной Пожизненный: " + CloudPage.atrEmail());
        CloudPage.clickCardPageBtn();
    }

    @Test
    @DisplayName("Оплата Долями с главной за Пожизненный")
    public void PayMainLifeInstallments() {
        step("Открытие страницы", () -> {
            open(addressPage.urlMain);
        });
        step("проверяем title", () -> {
            String expected = "TutorPlace - Платформа для обучения, курсы по подписке, онлайн школа";
            Assertions.assertEquals(expected, title());
        });

        ttPage.clickBtnOpenFor();
        ttPage.clickLife();
        CloudPage.setDateModalInstallments();
        System.out.println("Оплата продукта с главной Пожизненный оплата Долями: " + CloudPage.atrEmail());
        CloudPage.clickCardPageBtn();
    }
}