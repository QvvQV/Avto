package ru.tutor.page;

import com.github.dockerjava.api.model.Driver;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;
import static ru.tutor.test.Sber.PaySuccessfull.driver;
//import static com.codeborne.selenide.Selenide.*;
//import static com.codeborne.selenide.Selenide.closeWebDriver;

public class Product extends Driver {

    public Product(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }


    @FindBy(css = ".sc-eqUAAy.ehxRBN.sc-dcJsrY.cdYTVT.tutorial-catalog")
    private static WebElement inputSearch;

    //https://client.dev.tutorplace.ru/user/main?globalSearch=крав


    @FindBy(css = "._ProductCard_1bxex_1")
    private static WebElement product;


    @FindBy(css = "._Button_kio3a_1._fullWidth_kio3a_195")
    private static WebElement clickAdd;

    @FindBy(css = "._control_uynht_17")
    private static WebElement ellipses;

    public static void watchEllipse() {
        try {
            ellipses.isDisplayed();
        } catch (NoSuchElementException e) {
        }

//        ellipses.isDisplayed();
    }

    public static void clickGo() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("._Button_kio3a_1._fullWidth_kio3a_195")));
        clickAdd.click();
    }

    @FindBy(css = "._button_12fnc_39")
    private static WebElement startLesson;

    public static String btnStartLesson() {
        startLesson.isDisplayed();
        return startLesson.getText().trim();
    }

    @FindBy(css = "._bin_uynht_54")
    private static WebElement garbageKurs;

    @FindBy(css = "._Button_kio3a_1._danger_kio3a_110._fullWidth_kio3a_195")
    private static WebElement deleteKurs;

//    public static boolean setDeleteProduct(WebDriver driver) {
//        try {
//            ellipses.isDisplayed();
//            driver.findElements(By.cssSelector("._control_uynht_17")).get(0).click();
    /// /            ellipses.click();
//            ellipses.isSelected();
//            garbageKurs.isDisplayed();
//            garbageKurs.click();
//            deleteKurs.isDisplayed();
//            deleteKurs.click();
//            return true;
//        } catch (NoSuchElementException e) {
//            return false;
//        }
//    }

    @FindBy(css = "._info_8eydn_20 > h4")
    private static WebElement name;

    public static String ProductName() {
        return name.getText().trim();
    }

    public static String setDeleteAllProduct(WebDriver driver) throws Exception {
        List<WebElement> defaultCountProduct = driver.findElements(By.cssSelector("._control_uynht_17"));
        System.out.println("Кол-во добавленных продуктов: " + defaultCountProduct.size() + " шт");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        for (int i = 0; i < defaultCountProduct.toArray(new WebElement[0]).length; i++) {

            try {
//                defaultCountProduct.toArray(new WebElement[0])[0].click();
                driver.findElements(By.cssSelector("._control_uynht_17")).toArray(new WebElement[0])[0].click();
                garbageKurs.click();
                deleteKurs.click();
                System.out.println(i + ".Продукт: " + ProductName() + " удалён");
                Thread.sleep(500);

//                wait.until(numberOfElementsToBeMoreThan(By.cssSelector("._control_uynht_17"), i--));
//                System.out.println(driver.findElements(By.tagName("frame")));
            } catch (Exception exception) {
                throw new Exception(exception);
            }

        }
        return "продуктов больше нет";
    }

    public static String AllProductSearch(WebDriver driver) throws InterruptedException {
        String[] Product = {
                "японский маникюр",
                "крав",
                "Figma",
//                  "дневник",
//                "круговые",

//// прод
//                "бровист",
//                "python",
//                "Пдд",
//                "комфортное пищеварение",
//                "домашнее кардио",
//                "основы трейдинга",
//                "таргетолог",
//                "видеограф",
//                "сценарное мастерство",
//                "рыбалка",
//                "баня",
//                "нейрогимнастика",
//                "свечеварения",
//                "флористика",
//                "вальс",
//                "цигун",
//                "саундхилинг",
//                "рпп",
//                "самообразование",
//                "аюрведа",
//                "ферментация",
//                "спидкубинг",
//                "скальпинг",
//                "леттеринг",
//                "конфликтология",
//                "как стать коучем",
//                "основы правильного питания",
//                "лепка",
//                "геометрия",
//                "подготовка к егэ по русскому языку",
//                "подготовка к егэ по химии",
//                "подготовка к егэ по английскому",
//                "подготовка к егэ по информатике",
//                "подготовка к ЕГЭ по географии",
//                "подготовка к ЕГЭ по истории",
//                "подготовка к ЕГЭ по испанскому",
//                "подготовка к ЕГЭ по французскому",
//                "подготовка к ЕГЭ по немецкому",
//                "подготовка к ЕГЭ по обществ",
//                "подготовка к ЕГЭ по базовой",
//                "scrum-мастер",
//                "Project",
//                "яндекс директ",
//                "яндекс метрика",
//                "роуп скиппинг",
//                "бросаем курить",
//                "госуслуги",
//                "agile",
//                "рельефный пресс",
//                "шахматы",
//                "ремонт телефонов",
//                "развод без",
//                "ведущим",
//                "секрет красивых волос"
        };

        for (int i = 0; i < Product.length; i++) {
            if (inputSearch.isDisplayed() && inputSearch.isEnabled()) {
                inputSearch.sendKeys(Product[i]);
                product.click();
                setClickAdd();
                modal.setKursSuccesfullAdd();
                modal.clickStay();
//                PromoCode.doubleClick(driver);
                System.out.println(Product[i]);
                JavascriptExecutor js = (JavascriptExecutor) driver;
                Thread.sleep(220);
                js.executeScript("arguments[0].value='';", inputSearch);
//        System.out.println(error.getText().trim());
//        doubleClick(driver);
            }
        }
        return "Пустое поле";
    }

//        public static void doubleClick(WebDriver driver) {
//        WebElement clickable = driver.findElement(By.cssSelector(".sc-cwHptR"));
//        new Actions(driver)
//                .doubleClick(clickable)
//                .perform();
//        Clear();
//    }


    public static boolean setClickAdd() {
        try {
//            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
//            wait.until(numberOfElementsToBeMoreThan(By.tagName("button"),2));
//            driver.findElements(By.tagName("button")).get(3).isDisplayed();

            System.out.println(clickAdd.isDisplayed());
//            clickAdd.isDisplayed();
            clickAdd.click();
//            driver.findElements(By.tagName("button")).get(3).click();
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }


    public static void ProductSearch(WebDriver driver) {
        String[] Product = {
                "крав",
                "Figma"
        };

        for (int i = 0; i < Product.length; i++) {
            if (inputSearch.isDisplayed() && inputSearch.isEnabled()) {
                inputSearch.sendKeys(Product[i]);
                product.click();
                setClickAdd();
                modal.setKursSuccesfullAdd();
                modal.clickStay();
                PromoCode.doubleClick(driver);
//        System.out.println(error.getText().trim());
//        doubleClick(driver);
            }
        }
    }

    public static void Clear() {
//        inputSearch.clear();
        inputSearch.sendKeys(Keys.CLEAR);
    }

    public static void diet(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        inputSearch.sendKeys("рацион");
        product.isDisplayed();
        product.click();
        setClickAdd();
        wait.until(visibilityOfElementLocated(By.cssSelector("._content_1l2kq_17")));
    }

    @FindBy(css = ".sc-kuVLHQ.hSxfpw")
    private static WebElement btnOpenWeek;

    public static void clickBtnWeek() {
        if (btnOpenWeek.isDisplayed()) {
            btnOpenWeek.click();
        }
    }

    @FindBy(css = "._PurschasedProductCard_8eydn_1")
    private static WebElement firstProduct;

    public static void clickFirstProduct() {
        firstProduct.click();
    }

    @FindBy(css = ".sc-ksCcjW.kpvfmj.my_courses_step8")
    private static WebElement startLessons;

    public static void getStartLesson() {
        startLessons.click();
    }

    public static void clickBtnFromStartLesson() {
        clickFirstProduct();
        getStartLesson();
    }

    @FindBy(css = ".sc-bXWnss.iqwUOp")
    private static WebElement atrHrefFromLesson;

    public static String getArtHref() {
        return atrHrefFromLesson.getDomAttribute("href");
    }


}


