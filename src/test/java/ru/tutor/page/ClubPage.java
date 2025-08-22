package ru.tutor.page;

import com.codeborne.selenide.SelenideElement;
import com.github.dockerjava.api.model.Driver;
import com.github.javafaker.Faker;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Locale;

import static com.codeborne.selenide.Selenide.$;
import static org.openqa.selenium.support.ui.ExpectedConditions.*;
import static ru.tutor.test.Club.driver;


public class ClubPage extends Driver {

    private static final Faker faker = new Faker(new Locale("ru"));

    public ClubPage(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = "._MemberClubRequisite_tklds_1")
    private static WebElement myDetails;

    public static boolean waitMyDetails() {
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        return (myDetails.isDisplayed());
    }

    private static SelenideElement fullName = $("[name = \"fullName\"]");
    private static SelenideElement inn = $("[name = \"inn\"]");
    private static SelenideElement bik = $("[name = \"bik\"]");
    private static SelenideElement bankAccount = $("[name = \"bankAccount\"]");
    private static SelenideElement bankKorAccount = $("[name = \"bankKorAccount\"]");
    private static SelenideElement bankName = $("[name = \"bankName\"]");
    private static SelenideElement passSerial = $("[name = \"passSerial\"]");
    private static SelenideElement passNumber = $("[name = \"passNumber\"]");
    private static SelenideElement passIssuer = $("[name = \"passIssuer\"]");
    private static SelenideElement passIssued = $("[name = \"passIssued\"]");
    private static SelenideElement passDepartment = $("[name = \"passDepartment\"]");
    private static SelenideElement dateBirth = $("[name = \"dateBirth\"]");
    private static SelenideElement addressRegister = $("[name = \"addressRegister\"]");
    private static SelenideElement isSameAddress = $("[name = \"isSameAddress\"]");
    private static SelenideElement addressFact = $("[name = \"addressFact\"]");
    private static SelenideElement idEdo = $("[name = \"idEdo\"]");
    private static SelenideElement selfEmployed = $("[id = \"SZ\"]");
    private static SelenideElement individEntrep = $("[id = \"IP\"]");
    private static SelenideElement ogrnip = $("[name = \"ogrnip\"]");

    @FindBy(css = "._radioGroup_1ae8c_36 > h4")
//    @FindBy(css = "._danger_752pl_33")
    private static WebElement allFields;

    public static String getAllFields() {
        return allFields.getText().trim();
    }

    public static void inputFullName() {
        fullName.sendKeys(faker.name().fullName());
    }

    public static String atrInputFullName() {
        return fullName.getDomAttribute("value");
    }

    public static void pgDwnFullName() {
        fullName.sendKeys(Keys.PAGE_DOWN);
    }

    @FindBy(css = "._wrapper_1ery5_1 > form > div:nth-child(1) > div:nth-child(2) > span")
    private static WebElement errorFullName;

    public static String getErrorFullName() {
        return errorFullName.getText().trim();
    }

    public static void inputInn() {

        inn.sendKeys(faker.number().digits(12));
    }

    public static String atrInputInn() {
        return inn.getDomAttribute("placeholder");
    }

    public static void inputBik() {
        bik.sendKeys(faker.number().digits(9));
    }

    public static void pgDnBik() {
        bik.sendKeys(Keys.PAGE_DOWN);
    }

    public static void inputBankAccount() {
        bankAccount.sendKeys(faker.number().digits(20));
    }

    public static void inputBankKorAccount() {
        bankKorAccount.sendKeys(faker.number().digits(20));
    }

    public static void inputBankName() {
        bankName.sendKeys("Банк Санкт-Петербург Промышленный холдинг");
    }

    public static void inputPassSerial() {
        passSerial.sendKeys(faker.number().digits(4));
    }

    public static void inputPassNumber() {
        passNumber.sendKeys(faker.number().digits(6));
    }

    public static void inputPassIssuer() {
        passIssuer.sendKeys("кем");
    }

    public static void inputPassIssued() throws InterruptedException {
//        JavascriptExecutor js = (JavascriptExecutor) driver;
//        js.executeScript("arguments[0].value='30.12.1958';",passIssued);
//        new Actions (driver).moveToElement(passIssued).click().perform();
//        passIssued.sendKeys("30121958");
        String[] Number = {
                "3",
                "0",
                "1",
                "2",
                "1",
                "9",
                "5",
                "8"
        };

        for (int i = 0; i < Number.length; i++) {
            passIssued.sendKeys(Number[i]);
            Thread.sleep(150);
        }
    }


    public static void pgDwnPassIssued() {
        passDepartment.sendKeys(Keys.PAGE_DOWN);
    }

    public static void inputPassDepartment() throws InterruptedException {
//        passDepartment.click();
//        JavascriptExecutor js = (JavascriptExecutor) driver;
//        js.executeScript("arguments[0].value='514875';",passDepartment);
//        passDepartment.sendKeys( "514875");

        String[] Number = {
                "5",
                "1",
                "4",
                "8",
                "7",
                "5",
        };
        for (int i = 0; i < Number.length; i++) {

            passDepartment.sendKeys(Number[i]);
            Thread.sleep(150);
        }
    }

    public static void inputDateBirth() throws InterruptedException {
//        JavascriptExecutor js = (JavascriptExecutor) driver;
//        js.executeScript("arguments[0].value='27.07.1950';",dateBirth);
//        dateBirth.click();
//        dateBirth.sendKeys("27071950");
        String[] Number = {
                "2",
                "7",
                "0",
                "7",
                "1",
                "9",
                "5",
                "0"
        };
//        dateBirth.click();
        for (int i = 0; i < Number.length; i++) {
            dateBirth.sendKeys(Number[i]);
            Thread.sleep(150);
        }
    }


    public static void inputAddressRegister() {
        addressRegister.sendKeys("4012252, Санкт-Петербург, Богач, д5");
    }


    public static void inputIsSameAddress() {
        isSameAddress.click();
    }


    public static boolean displaedAddressFact() {
        return addressFact.isDisplayed();
    }

    public static String inputAddressFact() {
        return addressFact.getDomAttribute("value");
    }

//    public static void inputAddressFact() {
//        addressFact.sendKeys("4012252, Санкт-Петергбург, Богач, д5");
//    }

    @FindBy(css = "._wrapper_1ery5_1 > form > div:nth-child(2) > div:nth-child(12) > input[type=text]")
    private static WebElement numberPhoneSz;

    public static void inputNumberPhoneIP() throws InterruptedException {
//        numberPhoneSz.click();
//        numberPhoneSz.sendKeys("9548562546");
        String[] Number = {
                "9",
                "5",
                "4",
                "8",
                "5",
                "6",
                "2",
                "5",
                "4",
                "6"
        };

        for (int i = 0; i < Number.length; i++) {
            numberPhoneSz.sendKeys(Number[i]);
            Thread.sleep(150);
        }
    }

    @FindBy(css = "._wrapper_8dmmy_1 > form > div:nth-child(1) > div:nth-child(9) > input[type=text]")
    private static WebElement numberPhoneIP;

    public static void inputNumberPhoneSz() throws InterruptedException {
//        numberPhoneIP.click();
//        numberPhoneIP.sendKeys("9548562546");
        String[] Number = {
                "9",
                "5",
                "4",
                "8",
                "5",
                "6",
                "2",
                "5",
                "4",
                "6"
        };


        for (int i = 0; i < Number.length; i++) {
//            numberPhoneIP.click();
            numberPhoneIP.sendKeys(Number[i]);
            Thread.sleep(150);
        }
    }


    public static void inputIdEdo() {
        idEdo.sendKeys("2bm-73");
//        String[] Number = {
//                "2",
//                "b",
//                "m",
//                "-",
//                "7",
//                "3"
//        };
//
//        for (int i = 0; i < Number.length; i++) {
//            idEdo.sendKeys(Number[i]);
//        }
    }

    @FindBy(css = "._wrapper_1ery5_1 > form > div:nth-child(4) > div > label > input[type=checkbox]")
    private static WebElement success;

    public static void getSuccess() {
        success.click();
    }

    @FindBy(tagName = "button")
    private static WebElement btnAdd;

    public static void clickBtnAdd() {
        btnAdd.click();
    }

    public static void doubleClick(WebDriver driver) {
        WebElement clickable = driver.findElement(By.cssSelector(".sc-cwHptR"));
        new Actions(driver)
                .doubleClick(clickable)
                .perform();
        Product.Clear();
    }

    @FindBy(css = "._delete_1ery5_86")
    private static WebElement deleteDetails;

    public static String textDeleteDetails() {
        deleteDetails.isDisplayed();
        return deleteDetails.getText().trim();
    }

    public static void btnDeleteDetail() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(8));
        wait.until(visibilityOfElementLocated(By.cssSelector("._delete_1ery5_86")));
        wait.until(elementToBeClickable(By.cssSelector("._delete_1ery5_86")));
        deleteDetails.isDisplayed();
        deleteDetails.click();
    }

    @FindBy(css = "._delete_8dmmy_86")
    private static WebElement deleteDetailsIp;

    public static String textDeleteDetailsIP() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(8));
        wait.until(visibilityOfElementLocated(By.cssSelector("._delete_8dmmy_86")));
        wait.until(elementToBeClickable(By.cssSelector("._delete_8dmmy_86")));
//        deleteDetailsIp.isDisplayed();
        return deleteDetailsIp.getText().trim();
    }

    public static void btnDeleteDetailIP() {
        deleteDetailsIp.isDisplayed();
        deleteDetailsIp.click();
    }

    @FindBy(css = "._Button_kio3a_1._btn_1n48r_45._danger_kio3a_110._fullWidth_kio3a_195")
    private static WebElement btnDeleteFromModal;

    public static void clickBtnDeleteFromModal() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(8));
        wait.until(visibilityOfElementLocated(By.cssSelector("._Button_kio3a_1._btn_1n48r_45._danger_kio3a_110._fullWidth_kio3a_195")));
        wait.until(elementToBeClickable(By.cssSelector("._Button_kio3a_1._btn_1n48r_45._danger_kio3a_110._fullWidth_kio3a_195")));
        try {
            btnDeleteFromModal.click();
        } catch (NoSuchElementException e) {
        }
    }

    @FindBy(css = "._content_1l2kq_17._content_1n48r_9")
    private static WebElement modalDelete;

    public static boolean displayModalDelete() {
        return modalDelete.isDisplayed();
    }

    @FindBy(css = "._info_1n48r_17")
    private static WebElement question;

    public static String textQuestion() {
        question.isDisplayed();
        return question.getText().trim();
    }

    //Error самозанятый

    @FindBy(css = "._wrapper_1ery5_1 > form > div:nth-child(1) > div:nth-child(2) > span")
    private static WebElement error1;

    @FindBy(css = "._wrapper_1ery5_1 > form > div:nth-child(1) > div:nth-child(3) > span")
    private static WebElement error2;

    @FindBy(css = "._wrapper_1ery5_1 > form > div:nth-child(1) > div:nth-child(4) > span")
    private static WebElement error3;

    @FindBy(css = "._wrapper_1ery5_1 > form > div:nth-child(1) > div:nth-child(5) > span")
    private static WebElement error4;

    @FindBy(css = "._wrapper_1ery5_1 > form > div:nth-child(1) > div:nth-child(6) > span")
    private static WebElement error5;

    @FindBy(css = "._wrapper_1ery5_1 > form > div:nth-child(1) > div:nth-child(7) > span")
    private static WebElement error6;

    @FindBy(css = "._wrapper_1ery5_1 > form > div:nth-child(2) > div:nth-child(2) > span")
    private static WebElement error7;

    @FindBy(css = "._wrapper_1ery5_1 > form > div:nth-child(2) > div:nth-child(3) > span")
    private static WebElement error8;

    @FindBy(css = "._wrapper_1ery5_1 > form > div:nth-child(2) > div:nth-child(4) > span")
    private static WebElement error9;

    @FindBy(css = "._wrapper_1ery5_1 > form > div:nth-child(2) > div:nth-child(5) > span")
    private static WebElement error10;

    @FindBy(css = "._wrapper_1ery5_1 > form > div:nth-child(2) > div:nth-child(7) > span")
    private static WebElement error11;

    @FindBy(css = "._wrapper_1ery5_1 > form > div:nth-child(2) > div:nth-child(8) > span")
    private static WebElement error12;

    @FindBy(css = "._wrapper_1ery5_1 > form > div:nth-child(2) > div:nth-child(9) > span")
    private static WebElement error13;

    @FindBy(css = "._wrapper_1ery5_1 > form > div:nth-child(2) > div:nth-child(11) > span")
    private static WebElement error14;

    @FindBy(css = "._wrapper_1ery5_1 > form > div:nth-child(2) > div:nth-child(12) > span")
    private static WebElement error15;

    @FindBy(css = "._wrapper_1ery5_1 > form > div:nth-child(2) > div:nth-child(13) > span")
    private static WebElement error16;

    @FindBy(css = "._wrapper_1ery5_1 > form > div:nth-child(2) > div:nth-child(6) > span")
    private static WebElement error17;

    public static String getError1() {
        return error1.getText().trim();
    }

    public static String getError2() {
        return error2.getText().trim();
    }

    public static String getError3() {
        return error3.getText().trim();
    }

    public static String getError4() {
        return error4.getText().trim();
    }

    public static String getError5() {
        return error5.getText().trim();
    }

    public static String getError6() {
        return error6.getText().trim();
    }

    public static String getError7() {
        return error7.getText().trim();
    }

    public static String getError8() {
        return error8.getText().trim();
    }

    public static String getError9() {
        return error9.getText().trim();
    }

    public static String getError10() {
        return error10.getText().trim();
    }

    public static String getError11() {
        return error11.getText().trim();
    }

    public static String getError12() {
        return error12.getText().trim();
    }

    public static String getError13() {
        return error13.getText().trim();
    }

    public static String getError14() {
        return error14.getText().trim();
    }

    public static String getError15() {
        return error15.getText().trim();
    }

    public static String getError16() {
        return error16.getText().trim();
    }

    public static String getError17() {
        return error17.getText().trim();
    }

    //Error Индивидуальный предприниматель
    @FindBy(css = "._wrapper_8dmmy_1 > form > div:nth-child(1) > div:nth-child(2) > span")
    private static WebElement errorIP1;

    @FindBy(css = "._wrapper_8dmmy_1 > form > div:nth-child(1) > div:nth-child(3) > span")
    private static WebElement errorIP2;

    @FindBy(css = "._wrapper_8dmmy_1 > form > div:nth-child(1) > div:nth-child(4) > span")
    private static WebElement errorIP3;

    @FindBy(css = "._wrapper_8dmmy_1 > form > div:nth-child(1) > div:nth-child(5) > span")
    private static WebElement errorIP4;

    @FindBy(css = "._wrapper_8dmmy_1 > form > div:nth-child(1) > div:nth-child(6) > span")
    private static WebElement errorIP5;

    @FindBy(css = "._wrapper_8dmmy_1 > form > div:nth-child(1) > div:nth-child(8) > span")
    private static WebElement errorIP6;

    @FindBy(css = "._wrapper_8dmmy_1 > form > div:nth-child(1) > div:nth-child(9) > span")
    private static WebElement errorIP7;

    @FindBy(css = "._wrapper_8dmmy_1 > form > div:nth-child(1) > div:nth-child(10) > span")
    private static WebElement errorIP8;

    @FindBy(css = "._wrapper_8dmmy_1 > form > div:nth-child(1) > div:nth-child(11) > span")
    private static WebElement errorIP9;

    @FindBy(css = "._wrapper_8dmmy_1 > form > div:nth-child(1) > div:nth-child(12) > span")
    private static WebElement errorIP10;

    @FindBy(css = "._wrapper_8dmmy_1 > form > div:nth-child(1) > div:nth-child(13) > span")
    private static WebElement errorIP11;

    @FindBy(css = "._wrapper_8dmmy_1 > form > div:nth-child(1) > div:nth-child(14) > span")
    private static WebElement errorIP12;

    public static String getErrorIP1() {
        return errorIP1.getText().trim();
    }

    public static String getErrorIP2() {
        return errorIP2.getText().trim();
    }

    public static String getErrorIP3() {
        return errorIP3.getText().trim();
    }

    public static String getErrorIP4() {
        return errorIP4.getText().trim();
    }

    public static String getErrorIP5() {
        return errorIP5.getText().trim();
    }

    public static String getErrorIP6() {
        return errorIP6.getText().trim();
    }

    public static String getErrorIP7() {
        return errorIP7.getText().trim();
    }

    public static String getErrorIP8() {
        return errorIP8.getText().trim();
    }

    public static String getErrorIP9() {
        return errorIP9.getText().trim();
    }

    public static String getErrorIP10() {
        return errorIP10.getText().trim();
    }

    public static String getErrorIP11() {
        return errorIP11.getText().trim();
    }

    public static String getErrorIP12() {
        return errorIP12.getText().trim();
    }


    // Самозанятый
    public static void inpullAllFields(WebDriver driver) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        modal.modalTelephone();
        inputFullName();
        pgDwnFullName();
        inputInn();
        inputBik();
        inputBankAccount();
        inputBankKorAccount();
        inputBankName();
        inputPassSerial();
        inputPassNumber();
        inputPassIssuer();
        inputPassIssued();
        pgDwnPassIssued();
        inputPassDepartment();
        inputDateBirth();

        inputAddressRegister();
        inputIsSameAddress();
        Assert.assertEquals(true, displaedAddressFact());
        Assert.assertEquals("4012252, Санкт-Петербург, Богач, д5", inputAddressFact());
        LoginAndPass.getEmail();
        inputNumberPhoneIP();
        inputIdEdo();
        getSuccess();
        clickBtnAdd();
        wait.until(numberOfElementsToBeMoreThan(By.tagName("div"), 60));
        scrollTo(driver);
        System.out.println(textDeleteDetails());
        Assert.assertEquals("Редактировать реквизиты", driver.findElements(By.tagName("button")).get(0).getText());
        Assert.assertEquals("Удалить реквизиты", textDeleteDetails());
        btnDeleteDetail();
        displayModalDelete();
        Assert.assertEquals("Вы действительно хотите удалить реквизит?\n" +
                "После удаления вам необходимо будет заново заполнить анкету и подписать договор для вывода средств", textQuestion());
        clickBtnDeleteFromModal();
        Thread.sleep(1500);
        wait.until(numberOfElementsToBeMoreThan(By.tagName("div"), 58));
        System.out.println("атрибут поля полное Имя =" + atrInputFullName());
        btnIndividEntrep();
    }

    public static void scrollTo(WebDriver driver) {
        new Actions(driver)
                .scrollToElement(deleteDetails)
                .perform();
        deleteDetails.sendKeys(Keys.PAGE_DOWN);
    }

    public static void scrollToIp(WebDriver driver) {
        new Actions(driver)
                .scrollToElement(deleteDetailsIp)
                .perform();
        deleteDetailsIp.sendKeys(Keys.PAGE_DOWN);
    }


    public static void btnSelfEmployed() {
        selfEmployed.click();
    }


    public static void btnIndividEntrep() {
        try {
            individEntrep.isDisplayed();
            individEntrep.click();
        } catch (NoSuchElementException e) {
        }
    }


    public static void inputOgrnip() {
        ogrnip.sendKeys("123456789123456");
    }

    public static String atrOgrip() {
        return ogrnip.getDomAttribute("placeholder");
    }

    // индивидуальный предприниматель
    public static void inpullAllFields1(WebDriver driver) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        modal.modalTelephone();
        btnIndividEntrep();
        inputFullName();
        pgDwnFullName();
        inputOgrnip();
        inputInn();
        inputAddressRegister();
        inputIsSameAddress();
        Assert.assertEquals(true, displaedAddressFact());
        Assert.assertEquals("4012252, Санкт-Петербург, Богач, д5", inputAddressFact());
        LoginAndPass.getEmail();
        inputNumberPhoneSz();
        inputBankAccount();
        inputBik();
        pgDnBik();
        inputBankKorAccount();
        inputBankName();
        inputIdEdo();
        driver.findElements(By.cssSelector("._Checkbox_6p7ti_1")).get(2).click();
        clickBtnAdd();
        wait.until(numberOfElementsToBeMoreThan(By.tagName("div"), 50));
        scrollToIp(driver);
        System.out.println(textDeleteDetailsIP());
        Assert.assertEquals("Редактировать реквизиты", driver.findElements(By.tagName("button")).get(0).getText());
        Assert.assertEquals("Удалить реквизиты", textDeleteDetailsIP());
        btnDeleteDetailIP();
        displayModalDelete();
        Assert.assertEquals("Вы действительно хотите удалить реквизит?\n" +
                "После удаления вам необходимо будет заново заполнить анкету и подписать договор для вывода средств", textQuestion());
        clickBtnDeleteFromModal();
        Thread.sleep(1500);
        wait.until(numberOfElementsToBeMoreThan(By.tagName("div"), 50));
        System.out.println("атрибут поля полное Имя =" + atrInputFullName());
        btnSelfEmployed();
    }

    public static void errorSz() {
        modal.modalTelephone();
        fullName.click();
        inn.click();
        bik.click();

        Assert.assertEquals("Это поле обязательное", getError1());
        Assert.assertEquals("Это поле обязательное", getError2());

        inn.sendKeys("1");
        bankAccount.click();
        bankKorAccount.click();
        Assert.assertEquals("Поле должно иметь 12 символов", getError2());
        Assert.assertEquals("Это поле обязательное", getError3()); //bankAcc
        Assert.assertEquals("Это поле обязательное", getError4());
        pgDwnFullName();

        bankName.click();
        passSerial.click();
        passNumber.click();
        passIssuer.click();

        Assert.assertEquals("Это поле обязательное", getError5());
        Assert.assertEquals("Это поле обязательное", getError6());
        Assert.assertEquals("Это поле обязательное", getError7());
        Assert.assertEquals("Это поле обязательное", getError8());

        bik.sendKeys("1");
        bankAccount.sendKeys("1");
        bankKorAccount.sendKeys("1");
        passSerial.sendKeys("1");
        passNumber.sendKeys("1");
        Assert.assertEquals("Поле должно иметь 9 символов", getError3());
        Assert.assertEquals("Поле должно иметь 20 символов", getError4());
        Assert.assertEquals("Поле должно иметь 20 символов", getError5());
        Assert.assertEquals("Поле должно иметь 4 символов", getError7());
        Assert.assertEquals("Поле должно иметь 6 символов", getError8());

        pgDnBik();

        passIssued.click();
        passDepartment.click();
        dateBirth.click();
        addressRegister.click();
        addressFact.click();
        numberPhoneSz.click();
        idEdo.click();

        Assert.assertEquals("Это поле обязательное", getError9());
        Assert.assertEquals("Дата выдачи должна быть заполнена корректно", getError10());
        Assert.assertEquals("Дата рождения должна быть заполнена корректно", getError11());
        Assert.assertEquals("Это поле обязательное", getError12());
        Assert.assertEquals("Это поле обязательное", getError13());
        Assert.assertEquals("Это поле обязательное", getError15());
        Assert.assertEquals("Поле должно иметь 6 символов", getError17());

        passIssued.sendKeys("1");
        passDepartment.sendKeys("1");
        dateBirth.sendKeys("1");
        LoginAndPass.getEmailForError();
        numberPhoneSz.sendKeys("1");

        Assert.assertEquals("Дата рождения должна быть заполнена корректно", getError11());
        Assert.assertEquals("Введите корректный адрес", getError14());
        Assert.assertEquals("Введите номер полностью", getError15());
        Assert.assertEquals("Это поле обязательное", getError16());

    }

    public static void errorIP() {
        modal.modalTelephone();
        btnIndividEntrep();
        fullName.click();
        ogrnip.click();
        pgDwnFullName();
        inn.click();

        Assert.assertEquals("Это поле обязательное", getErrorIP1());
        Assert.assertEquals("Это поле обязательное", getErrorIP2());

        addressRegister.click();
        addressFact.click();
        numberPhoneIP.click();
        LoginAndPass.getEmailForError();

        Assert.assertEquals("Это поле обязательное", getErrorIP3());
        Assert.assertEquals("Это поле обязательное", getErrorIP4());
        Assert.assertEquals("Это поле обязательное", getErrorIP5());
        Assert.assertEquals("Введите корректный адрес", getErrorIP6());

        ogrnip.sendKeys("1");
        inn.sendKeys("1");
        numberPhoneIP.sendKeys("1");

        Assert.assertEquals("Введите номер полностью", getErrorIP7());
        Assert.assertEquals("Поле должно иметь 15 символов", getErrorIP2());
        Assert.assertEquals("Поле должно иметь 12 символов", getErrorIP3());

        pgDnBik();
        bankAccount.click();
        bik.click();
        bankKorAccount.click();
        bankName.click();
        idEdo.click();

        Assert.assertEquals("Это поле обязательное", getErrorIP8());
        Assert.assertEquals("Это поле обязательное", getErrorIP9());
        Assert.assertEquals("Это поле обязательное", getErrorIP10());
        Assert.assertEquals("Это поле обязательное", getErrorIP11());

        bankAccount.sendKeys("1");
        bik.sendKeys("1");
        bankKorAccount.sendKeys("1");

        Assert.assertEquals("Это поле обязательное", getErrorIP12());
        Assert.assertEquals("Поле должно иметь 20 символов", getErrorIP8());
        Assert.assertEquals("Поле должно иметь 9 символов", getErrorIP9());
        Assert.assertEquals("Поле должно иметь 20 символов", getErrorIP10());
    }

}
