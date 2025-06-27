package ru.tutor.page;

import com.github.dockerjava.api.model.Driver;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.openqa.selenium.support.ui.ExpectedConditions.numberOfElementsToBeMoreThan;
import static org.openqa.selenium.support.ui.ExpectedConditions.numberOfWindowsToBe;

public class devAdmin extends Driver {

    public devAdmin(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = ".button-modal")
    private static WebElement btnModal;

    public static void clickBtnModal() {
        btnModal.click();
    }

    public static void scrollTo(WebDriver driver) {
        new Actions(driver)
                .scrollToElement(btnModal)
                .perform();
//        deleteDetails.sendKeys(Keys.PAGE_DOWN);
    }

    public static void setInputSum() {
        inputSum.sendKeys("290");
        btnSavePayment.click();
    }

    @FindBy(tagName = "input")
    private static WebElement inputSum;

    @FindBy(css = ".orderPaymentModal__buttons__add")
    private static WebElement btnSavePayment;


    public static void startFromOrder(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(numberOfElementsToBeMoreThan(By.tagName("div"), 9));
        LoginAndPass.registrationUserFromAdminDev();                                                       //валидные данные для входа в админку
        wait.until(numberOfElementsToBeMoreThan(By.cssSelector(".sideBarLink"), 24));
        driver.findElements(By.cssSelector(".sideBarLink")).get(1).click();                                //переход в раздел Заказы
        wait.until(numberOfElementsToBeMoreThan(By.tagName("td"), 10));
//        driver.get("https://admin.dev.tutorplace.ru/orders/all");
        driver.findElements(By.tagName("td")).get(3).click();                                             //переход в последний созданный заказ
        wait.until(numberOfWindowsToBe(2));                                        //ожидание второго окна
        Object[] windowHandles = driver.getWindowHandles().toArray();
        driver.switchTo().window((String) windowHandles[1]);                                            //переход в окно заказа
        wait.until(numberOfElementsToBeMoreThan(By.cssSelector(".order-info-entry"), 30));
        System.out.println(driver.findElements(By.cssSelector(".order-info-entry")).get(3).getText()); //email
        System.out.println(driver.findElements(By.cssSelector(".order-info-entry")).get(7).getText()); //Utm метка
    }

    // CPA успешный платёж
    public static void workWithOrder(WebDriver driver) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        startFromOrder(driver);
        scrollTo(driver);
        Assert.assertEquals("Успешный", driver.findElements(By.cssSelector(".tableRow__item.tableRow__item--m")).get(1).getText());
        System.out.println(driver.findElements(By.cssSelector(".tableRow__item")).get(20).getText());
        Assert.assertEquals("1,00 ₽", driver.findElements(By.cssSelector(".tableRow__item")).get(20).getText());
        Assert.assertEquals("SETUP", driver.findElements(By.cssSelector(".tableRow__item")).get(19).getText());
        clickBtnModal();
        setInputSum();
        Thread.sleep(2500);
        driver.navigate().refresh();
        wait.until(numberOfElementsToBeMoreThan(By.cssSelector(".order-info-entry"), 30));
        scrollTo(driver);
        System.out.println(driver.findElements(By.cssSelector(".tableRow__item")).get(35).getText());
    }

    @FindBy(css = ".buttons__one")
    private static WebElement btnDone;

// отмена подписки админом
    public static void change_subscription(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.navigate().refresh();
        wait.until(numberOfElementsToBeMoreThan(By.cssSelector(".order-info-entry"), 30));
        driver.findElements(By.cssSelector(".button-medium")).get(0).click();
        driver.findElements(By.cssSelector(".buttons__item")).get(1).click();
        btnDone.click();
        scrollTo(driver);
        driver.navigate().refresh();
        scrollTo(driver);
        statusPayment(driver); //Успешный Recurring 290Р //Успешный остаток от аванса 327Р //Успешный переход на Lite
        Assert.assertEquals("RECURRING", driver.findElements(By.cssSelector(".tableRow__item")).get(49).getText());
        Assert.assertEquals("290,00 ₽", driver.findElements(By.cssSelector(".tableRow__item")).get(50).getText());
        Assert.assertEquals("lite", driver.findElements(By.cssSelector(".tableRow__item")).get(79).getText());
        Assert.assertEquals("0,00 ₽", driver.findElements(By.cssSelector(".tableRow__item")).get(80).getText());
    }

    public static void statusPayment(WebDriver driver){
        Assert.assertEquals("Успешный", driver.findElements(By.cssSelector(".tableRow__item.tableRow__item--m")).get(3).getText());
        Assert.assertEquals("Успешный", driver.findElements(By.cssSelector(".tableRow__item.tableRow__item--m")).get(4).getText());
        Assert.assertEquals("Успешный", driver.findElements(By.cssSelector(".tableRow__item.tableRow__item--m")).get(5).getText());
    }

//отмена платежа админом Refund
    public static void changePaymentFromAdmin(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        Assert.assertEquals("290,00 ₽", driver.findElements(By.cssSelector(".tableRow__item")).get(35).getText());
        Assert.assertEquals("Успешный", driver.findElements(By.cssSelector(".tableRow__item.tableRow__item--m")).get(2).getText());
        Assert.assertEquals("RECURRING", driver.findElements(By.cssSelector(".tableRow__item")).get(34).getText());
        driver.findElements(By.tagName("button")).get(5).click();
        wait.until(numberOfElementsToBeMoreThan(By.cssSelector(".tableRow__item"), 50));
        driver.findElements(By.tagName("button")).get(9).click();
        wait.until(numberOfElementsToBeMoreThan(By.cssSelector(".tableRow__item"), 80));
        statusPayment(driver); //Успешный refund -1 //Успешный refund -290 //Успешный переход на Lite
        Assert.assertEquals("REFUND", driver.findElements(By.cssSelector(".tableRow__item")).get(49).getText());
        Assert.assertEquals("-1,00 ₽", driver.findElements(By.cssSelector(".tableRow__item")).get(50).getText());
        Assert.assertEquals("lite", driver.findElements(By.cssSelector(".tableRow__item")).get(79).getText());
        Assert.assertEquals("0,00 ₽", driver.findElements(By.cssSelector(".tableRow__item")).get(80).getText());
    }

//    utm_campaign: 2806824, utm_content: direct_link, utm_medium: 1012_946682, utm_source: cpa, utm_term: 1012_2663648741 is_hybrid: true  (hybrid)
//    utm_campaign: 2806823, utm_content: direct_link, utm_medium: 1012_946682, utm_source: cpa, utm_term: 1012_2663649929 is_hybrid: false (revshare)
//    utm_campaign: 2806825, utm_content: direct_link, utm_medium: 1012_946682, utm_source: cpa, utm_term: 1012_2663644286 is_hybrid: false (CPA)

    // Hybrid успешный платёж
    public static void workWithOrderHybrid(WebDriver driver) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        startFromOrder(driver);
        scrollTo(driver);
        Assert.assertEquals("Успешный", driver.findElements(By.cssSelector(".tableRow__item.tableRow__item--m")).get(1).getText());
        Assert.assertEquals("Успешный", driver.findElements(By.cssSelector(".tableRow__item.tableRow__item--m")).get(2).getText());
        System.out.println(driver.findElements(By.cssSelector(".tableRow__item")).get(20).getText());
        Assert.assertEquals("1,00 ₽", driver.findElements(By.cssSelector(".tableRow__item")).get(20).getText());
        Assert.assertEquals("SETUP", driver.findElements(By.cssSelector(".tableRow__item")).get(19).getText());
        Assert.assertEquals("-450,00 ₽", driver.findElements(By.cssSelector(".tableRow__item")).get(35).getText());
        Assert.assertEquals("PREPAID", driver.findElements(By.cssSelector(".tableRow__item")).get(34).getText());
        clickBtnModal();                                                                                                    //провести платёж
        setInputSum();                                                                                                      //ввод суммы recurring
        Thread.sleep(2500);
        driver.navigate().refresh();
        wait.until(numberOfElementsToBeMoreThan(By.cssSelector(".order-info-entry"), 30));
        scrollTo(driver);
        System.out.println(driver.findElements(By.cssSelector(".tableRow__item")).get(50).getText());
    }

}
