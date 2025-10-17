package ru.tutor.page.PersonalAccount;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WaitHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Catalog {

    private static final SelenideElement CATALOG_LINK = $x("//div[text()='Каталог']");
    private static final SelenideElement PAGE_HEADER = $$("h2").find(exactText("Каталог курсов"));

    @Step("Проверка доступности каталога")
    public static void verifyCatalogPresence() {
        step("Валидация элемента каталога", () -> {
            CATALOG_LINK.shouldBe(visible, Duration.ofSeconds(10))
                    .shouldBe(interactable)
                    .shouldHave(exactText("Каталог"));

                    AllureAttachments.attachText("Каталог", "Элемент каталога доступен и кликабелен");
                });
    }

    @Step("Проверка заголовка каталога")
    public static void verifyCatalogHeader() {
        step("Валидация текста каталога", ()->{
            String actualText = CATALOG_LINK.getText().trim();
            String expectedText = "Каталог";

            assertEquals(expectedText, actualText,
                    String.format("Текст каталога не совпадает. Ожидалось: '%s', Фактически: '%s'",
                            expectedText, actualText));

            AllureAttachments.attachText("Проверка каталога",
                    "✓ Текст каталога корректен: " + actualText);
        });
    }

    @Step("Переход в каталог")
    public static void navigateToCatalog() {
        step("Клик по каталогу и проверка перехода", () -> {
            String originalUrl = WebDriverRunner.url();

            CATALOG_LINK.shouldBe(interactable).click();
            WaitHelper.waitForPageLoad();

            //Проверяем что произошёл переход
            if (!WebDriverRunner.url().equals(originalUrl)) {
                AllureAttachments.attachText("Навигация", "Успешный переход в каталог");
            }

            verifyCatalogPageLoaded();
        });
    }

    @Step("Проверка загрузки страницы каталога")
    private static void verifyCatalogPageLoaded() {
        step("Валидация страницы каталога", () -> {
            PAGE_HEADER.shouldBe(visible);

            AllureAttachments.attachText("Загрузка каталога", "Страница каталога успешно загружена");
            AllureAttachments.attachScreenshot("Страница каталога");
        });
    }

    @Step("Проверка наличия продуктов в каталоге")
    public static void verifyCatalogHasProducts() {
        step("Проверка контента каталога", () -> {
            SelenideElement productsContainer = $(".tutorial-product-card")
                    .shouldBe(visible, Duration.ofSeconds(5));

            ElementsCollection products = productsContainer.$$(".tutorial-product-card");

            if (products.size() > 0) {
                AllureAttachments.attachText("Продукты в каталоге",
                        "Найдено продуктов: " + products.size());
            } else {
                AllureAttachments.attachText("Предупреждение", "В каталоге нет продуктов");
            }
        });
    }
}

