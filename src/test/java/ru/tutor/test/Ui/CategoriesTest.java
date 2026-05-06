package ru.tutor.test.Ui;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import ru.tutor.base.BaseUiTest;
import ru.tutor.constants.AddressPage;
import ru.tutor.page.Registration;
import ru.tutor.page.modals.*;
import ru.tutor.page.personalAccount.Catalog;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WaitHelper;

import java.util.List;

import static com.codeborne.selenide.Selenide.open;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Каталог курсов")
@Feature("Блок категорий на главной странице")
@DisplayName("Тестирование блока категорий")
public class CategoriesTest extends BaseUiTest {

    private final Catalog catalog = new Catalog();
    private final Registration registration = new Registration();

    // Ожидаемое количество категорий
    private static final int EXPECTED_CATEGORIES_COUNT = 31;


    private String findExpectedUrlForCategory(String categoryName) {
        // Маппинг категорий на URL
        return switch (categoryName) {
            case "Питание" -> "/user/catalog?catalog-category=pitanie";
            case "Красота" -> "/user/catalog?catalog-category=krasota-i-uhod";
            case "Бизнес" -> "/user/catalog?catalog-category=Biznes";
            case "Дизайн" -> "/user/catalog?catalog-category=Dizain";
            case "Монтаж видео" -> "/user/catalog?catalog-category=Montazh-video";
            case "IT" -> "/user/catalog?catalog-category=Programmirovaniye";
            case "Эзотерика" -> "/user/catalog?catalog-category=Ezoterika";
            case "Уход за собой" -> "/user/catalog?catalog-category=Ukhod-za-soboi";
            case "Уход за детьми" -> "/user/catalog?catalog-category=Ukhod-za-det-mi";
            case "Хобби" -> "/user/catalog?catalog-category=Khobbi";
            case "Дом" -> "/user/catalog?catalog-category=Dom";
            case "Отношения" -> "/user/catalog?catalog-category=Otnosheniya";
            case "Танцы" -> "/user/catalog?catalog-category=tantsy";
            case "Рисование" -> "/user/catalog?catalog-category=risovaniye";
            case "Для детей" -> "/user/catalog?catalog-category=Dlya-detei";
            case "ОГЭ" -> "/user/catalog?catalog-category=OG-E";
            case "Спорт" -> "/user/catalog?catalog-category=sport";
            case "Для мужчин" -> "/user/catalog?catalog-category=dlya-muzchin";
            case "Блогинг" -> "/user/catalog?catalog-category=Bloging";
            case "Маркетинг" -> "/user/catalog?catalog-category=Marketing";
            case "Нейросети" -> "/user/catalog?catalog-category=Neiroseti";
            case "Психология" -> "/user/catalog?catalog-category=Psikhologiya";
            case "Профессии" -> "/user/catalog?catalog-category=Novyye-professii";
            case "Заработок" -> "/user/catalog?catalog-category=Zarabotok";
            case "Карьера" -> "/user/catalog?catalog-category=Kar-yera";
            case "Творчество" -> "/user/catalog?catalog-category=Tvorchestvo";
            case "Языки" -> "/user/catalog?catalog-category=Yazyki";
            case "18+" -> "/user/catalog?catalog-category=18";
            case "Животные" -> "/user/catalog?catalog-category=zhivotnyye";
            case "Музыка" -> "/user/catalog?catalog-category=muzyka";
            case "ЕГЭ" -> "/user/catalog?catalog-category=YEG-E";
            default -> "/user/catalog?catalog-category=";
        };
    }

    @BeforeEach
    public void beforeEach() {
        step("Авторизация пользователя", () -> {
                    open(AddressPage.LOGIN);
                    registration.validEmail();
                    registration.validPass();
                    registration.clickBtnLogin();
                    WaitHelper.waitForPageLoad();
                }
        );

        step("Переход в каталог", () -> {
            catalog.verifyCatalogHasCategory();
            catalog.verifyOnCatalogPage();
        });

    }

    @Test
    @Tag("UI")
    @Order(1)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Проверка отображения всех категорий")
    public void verifyAllCategoriesDisplayed() {
        step("Проверить количество категорий", () -> {
            int actualCount = catalog.getCategoriesCount();
            assertEquals(EXPECTED_CATEGORIES_COUNT, actualCount,
                    "Количество категорий не соответствует ожидаемому");

            AllureAttachments.attachText("Количество категорий",
                    String.format("Найдено категорий: %d из %d", actualCount, EXPECTED_CATEGORIES_COUNT));
        });

        step("Проверить отображение всех категорий", catalog::verifyAllCategoriesDisplayed);
    }

    @Test
    @Tag("UI")
    @Order(2)
    @DisplayName("Проверка текстов всех категорий")
    @Severity(SeverityLevel.CRITICAL)
    void verifyAllCategoryText() {

        step("Проверить тексты всех категорий", () -> {
            List<String> allCategoryNames = catalog.getAllCategoryNames();
            int checkedCount = 0;

            for (String categoryName : allCategoryNames) {
                catalog.verifyCategoryDisplayed(categoryName)
                        .verifyCategoryText(categoryName);
                checkedCount++;
            }

            AllureAttachments.attachText("Результат",
                    String.format("✅ Проверено %d категорий", checkedCount));
        });
    }

    @Test
    @Tag("UI")
    @Order(3)
    @DisplayName("Проверка ссылок всех категорий")
    @Severity(SeverityLevel.CRITICAL)
    void verifyAllCategoryLinks() {
        step("Проверить ссылки всех категорий",
                catalog::verifyAllCategoryLinks);
    }

    @Test
    @Tag("UI")
    @Order(4)
    @DisplayName("Проверка иконок всех категорий")
    @Severity(SeverityLevel.NORMAL)
    void verifyAllCategoryIcons() {
        step("Проверить иконки всех категорий", catalog::verifyAllCategoryIcons);
    }

    @Test
    @Tag("UI")
    @Order(5)
    @DisplayName("Переход по категории и проверка URL")
    @Severity(SeverityLevel.CRITICAL)
    void navigateToCategoryAndVerifyUrl() {
        step("Проверить переход по всем категориям", () -> {
            List<String> allCategoryNames = catalog.getAllCategoryNames();
            int checkedCount = 0;

            for (String categoryName : allCategoryNames) {
                String expectedUrl = findExpectedUrlForCategory(categoryName);

                catalog.selectCategoryAndVerifyUrl(categoryName, expectedUrl);

                open(AddressPage.CATALOG);
                WaitHelper.waitForPageLoad();
                checkedCount++;
            }

            AllureAttachments.attachText("Результат",
                    String.format("✅ Проверено %d переходов", checkedCount));
        });
    }

    @Test
    @Tag("UI")
    @Order(6)
    @DisplayName("Проверка горизонтального скроллинга категорий")
    @Severity(SeverityLevel.NORMAL)
    void verifyCategoryHorizontalScroll() {
        step("Проверить скроллинг категорий", () -> {
            long initialPosition = catalog.getScrollPosition();
            catalog.scrollRight(1800);
            catalog.verifyScrollChanged(initialPosition);
            catalog.verifyAllCategoriesVisibleAfterScroll();

            catalog.scrollLeft(1800);

            AllureAttachments.attachText("Скроллинг",
                    "✅ Скроллинг работает корректно");
        });
    }

    @Test
    @Tag("UI")
    @Order(7)
    @DisplayName("Проверка времени загрузки категорий")
    @Severity(SeverityLevel.MINOR)
    void measureCategoriesLoadTime() {
        step("Измерить время загрузки", () -> {
            open(AddressPage.CATALOG);
            long loadTime = catalog.measureLoadTime();
            long maxAllowedTime = 3000;

            assertTrue(loadTime < maxAllowedTime,
                    String.format("❌ Время загрузки категорий (%d ms) превышает допустимый порог (%d ms)",
                            loadTime, maxAllowedTime));

            AllureAttachments.attachText("Производительность",
                    String.format("""
                                    📊 Время загрузки: %d ms
                                    📏 Допустимый порог: %d ms
                                    🎯 Статус: %s
                                    """, loadTime, maxAllowedTime,
                            loadTime < maxAllowedTime ? "✅ Успешно" : "❌ Превышен"));
        });
    }

    @Test
    @Tag("UI")
    @Order(8)
    @DisplayName("Проверка наличия навигационной кнопки")
    @Severity(SeverityLevel.MINOR)
    void verifyNavigationButton() {
        step("Проверить наличие навигационной кнопки", () -> {
            boolean hasButton = catalog.hasNavigationButton();

            if (hasButton) {
                long initialPosition = catalog.getScrollPosition();
                catalog.clickNavigateButton();
                catalog.verifyScrollChanged(initialPosition);

                AllureAttachments.attachText("Навигация",
                        "✅ Кнопка скроллинга работает корректно");
            } else {
                AllureAttachments.attachText("Навигация",
                        "⏭️ Навигационные кнопки не найдены");
            }
        });
    }

    @Test
    @Tag("UI")
    @Order(9)
    @DisplayName("Проверка уникальности названий категорий")
    @Severity(SeverityLevel.NORMAL)
    void verifyCategoryNamesUnique() {
        catalog.nameUnique();
    }

    @Test
    @Tag("UI")
    @Order(10)
    @DisplayName("Проверка наличия курсов во всех категориях")
    @Severity(SeverityLevel.CRITICAL)
    void verifyAllCategoriesHaveCourses() {
        catalog.verifyAllCategoriesHaveCourses();
    }
}