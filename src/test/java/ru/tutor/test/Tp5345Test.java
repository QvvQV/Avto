package ru.tutor.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.*;
import ru.tutor.base.BaseUiTest;
import ru.tutor.page.*;
import ru.tutor.page.admin.AdminLoginPage;
import ru.tutor.page.devProduct;

import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;

public class Tp5345Test extends BaseUiTest {


    @AfterAll
    public static void quitDriver() {
        closeWebDriver();
        SelenideLogger.removeListener("AllureSelenide");
    }

//    @Test
    @DisplayName("открытие стр продукта")
    public void WriteTheText()  {
        step("Открытие стр админки", () ->
            open(AddressPage.devAdmin)
        );
        step("проверяем title", () -> {
            String expected = "Online курсы";
            Assertions.assertEquals(expected, title());
        });
        step("регистрируемся",
            AdminLoginPage::login
        );
        devProduct.clickPencil();
        devProduct.textH2();
        devProduct.scrollToElement();
        devProduct.inputText();
        devProduct.scrollToBtnSave();
        step("открываем стр продукта №525", () ->
            open(AddressPage.product525)
        );
        devProduct.textH2();
        devProduct.scrollToElement();
        devProduct.assertionsText();
        devProduct.deleteText();
        devProduct.scrollToBtnSave();
        step("открываем стр продукта №525", () ->
            open(AddressPage.product525)
        );
        devProduct.textH2();
        devProduct.scrollToElement();
        devProduct.cleanText();
    }

//    @Test
    @DisplayName("изменения в модуле н1")
    public void ChangeH1() {
        step("Открытие стр админки", () ->
            open(AddressPage.devAdmin)
        );
        step("проверяем title", () -> {
            String expected = "Online курсы";
            Assertions.assertEquals(expected, title());
        });
        step("регистрируемся",
            AdminLoginPage::login
        );
        devProduct.clickPencil();
        devProduct.textH1();
        devProduct.scrollToElement();
        devProduct.visibleCardTitle();
    }

     @Test
     @AllureId("36")
     @DisplayName("добавление навыков только за счёт бека, без фронта")
    public void open1() {
        step("Открытие стр админки", () ->
            open(AddressPage.devAdmin)
        );

        devProduct.clickPencil();

        }
}


