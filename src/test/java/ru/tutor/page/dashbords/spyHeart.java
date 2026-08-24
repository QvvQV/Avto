//package ru.tutor.page.dashbords;
//
//import com.codeborne.selenide.SelenideElement;
//import io.qameta.allure.Step;
//import ru.tutor.utils.AllureAttachments;
//import ru.tutor.utils.universalMethod;
//
//import java.time.Duration;
//
//import static com.codeborne.selenide.Condition.*;
//import static com.codeborne.selenide.Selenide.$;
//import static com.codeborne.selenide.Selenide.$$;
//import static io.qameta.allure.Allure.step;
//
//// проект закрыт
//public class spyHeart {
//
//    private static final SelenideElement SPY_HEART = $$("p").find(exactText("SpyHeart"));
//
//    // Локаторы внутри дашборда
//    private static final SelenideElement VK_INPUT = $("#idVk");
//    private static final SelenideElement START_CHEKING = $$("button").find(exactText("Начать проверку"));
//
//
//    @Step("Кликаем по SpyHeart")
//    public static void clickFromSpyHeart() {
//        universalMethod.safeClick(SPY_HEART, "Шпион");
//        AllureAttachments.attachScreenshot("Результат после перехода в дашборд");
//    }
//
//    @Step("Проверка видимости элементов")
//    public static void ckeckVisionElement() {
//        step("Кнопка `Начать проверку` - Неактивна", () -> {
//            START_CHEKING
//                    .shouldBe(visible, Duration.ofSeconds(3))
//                    .shouldBe(disabled);
//        });
//        step("Вводим текст в поле", () -> {
//            String testUrl = "https://vk.com/id1beatbox";
//            VK_INPUT.shouldBe(visible, Duration.ofSeconds(3))
//                    .setValue(testUrl);
//        });
//        step("Кнопка `Начать проверку` - активна", () -> {
//            START_CHEKING
//                    .shouldBe(visible, Duration.ofSeconds(3))
//                    .shouldBe(enabled);
//        });
//    }
//}