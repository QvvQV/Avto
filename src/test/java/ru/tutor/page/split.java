package ru.tutor.page;

import com.github.dockerjava.api.model.Driver;
import com.github.javafaker.Faker;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Locale;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

public class split extends Driver {

    private static final Faker faker = new Faker(new Locale("ru"));

    public split(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = ".intro__bottom > a")
    private static WebElement btnSave;

    @FindBy(id = "mainFormaSubmitBtn")
    private static WebElement mainForma;

    @FindBy(css = ".modal-again__block > div > div.modal-again__bottom.modal-again__bottom--card-payment > div > p")

//@FindBy(css = "#radix-\\:R1rfnnnlb\\: > div:nth-child(4) > h4")
private static WebElement showModalText1;

@FindBy(css = "#radix-\\:R1rfnnnlb\\: > div:nth-child(3) > h4")
private static WebElement showModalText0;

    @FindBy(css = ".button__option.button__sber.show")
//    @FindBy(css = ".sber")
    private static WebElement btnSber;

    @FindBy(css = ".button.button__option.button__tinkoff.show")
//    @FindBy(css = ".tink")
    private static WebElement btnCloud;

    @FindBy(css = "button.button__option.button__card.show")
//    @FindBy(css = ".other")
    private static WebElement btnOther;

    @FindBy(css = ".action-button")
    private static WebElement clearButton;

    public static void clickBtnSave() {
        btnSave.click();
    }

    public static void clickMainForma() {
        mainForma.click();
    }

    public static String showText() {
        return showModalText1.getText().trim();
    }

    public static String change() {
        if (showText().equals("Оплата картой другого банка")) {
            return "Появилось новая модалка: ";
        } else {
            return "Появилась старая модалка: ";
        }
    }

    public static void clickBtnSber() {
        btnSber.click();
    }

    public static void clickBtnCloud() {
        btnCloud.click();
    }

    public static void clickBtnOther() {
        btnOther.click();
    }

    public static void clickClearButton(){
        clearButton.click();
    }

    public static String textClear(){
        return clearButton.getText().trim();
    }

    public static void deleteCash(WebDriver driver) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        wait.until(visibilityOfElementLocated(By.id(".clearButton")));
        textClear();
        clickClearButton();
        Thread.sleep(500);
    }

}
