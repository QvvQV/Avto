package ru.tutor.test.cpa;


import com.codeborne.selenide.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.openqa.selenium.chrome.ChromeOptions;
import ru.tutor.page.CloudPage;
import ru.tutor.page.SberPage;

import static com.codeborne.selenide.Selenide.*;


public class CPATester {


    //    @BeforeEach
    @Before
    public void setupAll() {
        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--incognito");
//        Configuration.browserCapabilities = options;
        open("https://admin:JWwppsEA84B4BozJgE44sNiZZ@ttplace.ru/catalog/product/excel?aff_rid=1012_ENpc");
    }

    @Test
//        @RepeatedTest(3)
    @DisplayName("Оплата по валидной карте с продукта exel")
    public void ShowModalVersionFromTester() {

        String expected = "Excel-таблицы для начинающих | Онлайн-курс | TutorPlace";
        Assert.assertEquals(expected, title());

        CloudPage.clickBtnStart();

        System.out.println("Оплата с продукта Excel за 1 руб по ссылке СРА: " + CloudPage.atrEmail());
        CloudPage.clickCardPageBtn();
        sleep(3000);
    }
}