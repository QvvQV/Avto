package ru.tutor.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.UniversalMethod;
import ru.tutor.utils.WaitHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$x;

public class FirstCourses {

    private final SelenideElement FIRST_COURSE_CARD = $x("//div[contains(@class, '_catalog_')]//div[contains(@class, '_CatalogCard_')][1]");

    @Step("Клик 'Начать' в первом курсе блока 'первый курс'")
    public void clickStartInFirstCourse() {
        FIRST_COURSE_CARD.shouldBe(visible, Duration.ofSeconds(15)).scrollIntoView(true);
        WaitHelper.waitForAnimationComplete();
        UniversalMethod.clickButton(
                FIRST_COURSE_CARD.$$("button").find(exactText("Начать")),
                "Начать в первом курсе");
        WaitHelper.waitForPageLoad();
        WaitHelper.waitForAnimationComplete();
        AllureAttachments.attachScreenshot("Первый курс открыт");
    }
}
