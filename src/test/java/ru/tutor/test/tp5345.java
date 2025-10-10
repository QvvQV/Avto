package ru.tutor.test;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.AllureId;
import io.qameta.allure.Attachment;
import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.qameta.allure.selenide.AllureSelenide;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import ru.tutor.base.BaseUiTest;
import ru.tutor.page.addressPage;
import ru.tutor.page.devProduct;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;

public class tp5345 extends BaseUiTest {

    @Attachment(value = "Screenshot", type = "image/png", fileExtension = "png")
    public void takeScreenshot() {
        ((TakesScreenshot) getWebDriver()).getScreenshotAs(OutputType.BYTES);
    }

    @AfterAll
    public static void quitDriver() {
        closeWebDriver();
        SelenideLogger.removeListener("AllureSelenide");
    }

//    @Test
    @DisplayName("открытие стр продукта")
    public void WriteTheText() throws JsonProcessingException {
        step("Открытие стр админки", () -> {
            open(addressPage.devAdmin);
        });
        step("проверяем title", () -> {
            String expected = "Online курсы";
            Assertions.assertEquals(expected, title());
        });
        step("регистрируемся", () -> {
            devProduct.login();
        });
//        step("открываем стр продукта №525", () -> {
//            sleep(3000);
//            open(addressPage.product525);
//        });
        devProduct.clickPencil();
        devProduct.textH2();
        devProduct.scrollToElement();
        devProduct.inputText();
        devProduct.scrollToBtnSave();
        step("открываем стр продукта №525", () -> {
            open(addressPage.product525);
        });
        devProduct.textH2();
        devProduct.scrollToElement();
        devProduct.assertionsText();
        devProduct.deleteText();
        devProduct.scrollToBtnSave();
        step("открываем стр продукта №525", () -> {
            open(addressPage.product525);
        });
        devProduct.textH2();
        devProduct.scrollToElement();
        devProduct.cleanText();
    }

//    @Test
    @DisplayName("изменения в модуле н1")
    public void ChangeH1() throws JsonProcessingException {
        step("Открытие стр админки", () -> {
            open(addressPage.devAdmin);
        });
        step("проверяем title", () -> {
            String expected = "Online курсы";
            Assertions.assertEquals(expected, title());
        });
        step("регистрируемся", () -> {
            devProduct.login();
        });
//        step("открываем стр продукта №525", () -> {
//            sleep(3000);
//            open(addressPage.product525);
//        });
        devProduct.clickPencil();
        devProduct.textH1();
        devProduct.scrollToElement();
        devProduct.visibleCardTitle();
    }

     @Test
     @AllureId("36")
     @DisplayName("добавление навыков только за счёт бека, без фронта")
    public void open1() throws JsonProcessingException {
        step("Открытие стр админки", () -> {
            open(addressPage.devAdmin);
        });
//        step("регистрируемся", () -> {
//            devProduct.login();
//        });
//        sleep(2500);
        devProduct.clickPencil();
//        devProduct.textH1();
        }
}


