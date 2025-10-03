package ru.tutor.test;

import io.qameta.allure.AllureId;
import io.qameta.allure.Owner;
import ru.tutor.base.BaseUiTest;
import ru.tutor.page.*;
import org.junit.jupiter.api.*;

import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;


public class tester extends BaseUiTest {

    //                @AfterEach
//    @AfterAll
//    public static void tearDownAll() {
//        closeWebDriver();
//        SelenideLogger.removeListener("allure.config.properties");
//    }

    @Test
    @AllureId("1")
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
    @AllureId("3")
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
    @AllureId("2")
//    @RepeatedTest(2)
    @DisplayName("Оплата с главной за 399 руб")
    @Owner("Vadim")
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
    @AllureId("4")
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
    @AllureId("4")
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