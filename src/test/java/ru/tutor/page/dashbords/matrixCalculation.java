package ru.tutor.page.dashbords;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WaitHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$$;

public class matrixCalculation {

        private static final SelenideElement MATRIX_CALCULATION = $$("p").find(exactText("Расчет матрицы"));


    @Step("Кликаем по Расчет матрицы")
    public static void clickFromMatrixCalculation() {
        MATRIX_CALCULATION.shouldBe(visible, Duration.ofSeconds(5))
                .shouldBe(interactable)
                .click();
        WaitHelper.waitForAnimationComplete();
        AllureAttachments.attachScreenshot("Результат после клика");
    }
}
