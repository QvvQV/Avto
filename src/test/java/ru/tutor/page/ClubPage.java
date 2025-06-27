package ru.tutor.page;

import com.github.dockerjava.api.model.Driver;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Objects;

import static org.openqa.selenium.support.ui.ExpectedConditions.numberOfElementsToBeMoreThan;

public class ClubPage extends Driver {

    public ClubPage(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = "._MemberClubRequisite_tklds_1")
    private static WebElement myDetails;

    public static boolean waitMyDetails() {
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        return (myDetails.isDisplayed());
    }

    @FindBy(css = "._danger_1ery5_29")
    private static WebElement allFields;

    public static String getAllFields() {
        return allFields.getText().trim();
    }

    @FindBy(name = "fullName")
    private static WebElement fullName;

    public static void inputFullName() {
        fullName.sendKeys("Дуся Лопатин Ибрагимовна");
    }

    public static String atrInputFullName(){
        return fullName.getDomAttribute("value");
    }

    public static void pgDwnFullName() {
        fullName.sendKeys(Keys.PAGE_DOWN);
    }

    @FindBy(css = "#root > div > div.sc-btdwOF.iXpDDK > div._MemberClubRequisite_tklds_1 > div > div._wrapper_1ery5_1 > form > div:nth-child(1) > div:nth-child(2) > span")
    private static WebElement errorFullName;

    public static String getErrorFullName() {
        return errorFullName.getText().trim();
    }

    @FindBy(name = "inn")
    private static WebElement inn;

    public static void inputInn() {
        inn.sendKeys("123456789011");
    }

    public static String atrInputInn(){
        return inn.getDomAttribute("placeholder");
    }

    @FindBy(name = "bik")
    private static WebElement bik;

    public static void inputBik() {
        bik.sendKeys("123456789");
    }

    public static void pgDnBik() {
        bik.sendKeys(Keys.PAGE_DOWN);
    }

    @FindBy(name = "bankAccount")
    private static WebElement bankAccount;

    public static void inputBankAccount() {
        bankAccount.sendKeys("12345678912345678912");
    }

    @FindBy(name = "bankKorAccount")
    private static WebElement bankKorAccount;

    public static void inputBankKorAccount() {
        bankKorAccount.sendKeys("12345678912345678912");
    }

    @FindBy(name = "bankName")
    private static WebElement bankName;

    public static void inputBankName() {
        bankName.sendKeys("Банк Санкт-Петербург Промышленный холдинг");
    }

    @FindBy(name = "passSerial")
    private static WebElement passSerial;

    public static void inputPassSerial() {
        passSerial.sendKeys("4118");
    }

    @FindBy(name = "passNumber")
    private static WebElement passNumber;

    public static void inputPassNumber() {
        passNumber.sendKeys("454785");
    }

    @FindBy(name = "passIssuer")
    private static WebElement passIssuer;

    public static void inputPassIssuer() {
        passIssuer.sendKeys("кем");
    }

    @FindBy(name = "passIssued")
    private static WebElement passIssued;

    public static void inputPassIssued() {
        passIssued.click();
        passIssued.sendKeys("30121958");
        //        String[] Number = {
//                "3",
//                "0",
//                "1",
//                "2",
//                "1",
//                "9",
//                "5",
//                "8"
//        };
//
//        for (int i = 0; i < Number.length; i++) {
//            passIssued.sendKeys(Number[i]);
//        }
    }

    public static void pgDwnPassIssued() {
        passIssued.sendKeys(Keys.PAGE_DOWN);
    }

    @FindBy(name = "passDepartment")
    private static WebElement passDepartment;

    public static void inputPassDepartment() {
        passDepartment.click();
        passDepartment.sendKeys("514875");
//        String[] Number = {
//                "5",
//                "1",
//                "4",
//                "8",
//                "7",
//                "5",
//        };
//        passDepartment.click();
//        for (int i = 0; i < Number.length; i++) {
//
//            passDepartment.sendKeys(Number[i]);
//        }
    }

    @FindBy(name = "dateBirth")
    private static WebElement dateBirth;

    public static void inputDateBirth() {
        dateBirth.click();
        dateBirth.sendKeys("27071950");
//        String[] Number = {
//                "2",
//                "7",
//                "0",
//                "7",
//                "1",
//                "9",
//                "5",
//                "0"
//        };
//        dateBirth.click();
//        for (int i = 0; i < Number.length; i++) {
//            dateBirth.sendKeys(Number[i]);
//        }
    }

    @FindBy(name = "addressRegister")
    private static WebElement addressRegister;

    public static void inputAddressRegister() {
        addressRegister.sendKeys("4012252, Санкт-Петергбург, Богач, д5");
    }

    @FindBy(name = "isSameAddress")
    private static WebElement isSameAddress;

    public static void inputIsSameAddress() {
        isSameAddress.click();
    }

    @FindBy(name = "addressFact")
    private static WebElement addressFact;

    public static boolean displaedAddressFact() {
        return addressFact.isDisplayed();
    }

    public static String inputAddressFact() {
        return addressFact.getDomAttribute("value");
    }

//    public static void inputAddressFact() {
//        addressFact.sendKeys("4012252, Санкт-Петергбург, Богач, д5");
//    }

    @FindBy(css = "#root > div.sc-fggMnX.dQvEiM > div.sc-knHXcE.kIqAIF > div._MemberClubRequisite_tklds_1 > div > div._wrapper_1ery5_1 > form > div:nth-child(2) > div:nth-child(12) > input[type=text]")
    private static WebElement numberPhoneSz;

    public static void inputNumberPhoneIP() {
        numberPhoneSz.click();
        numberPhoneSz.sendKeys("9548562546");
        //        String[] Number = {
//                "9",
//                "5",
//                "4",
//                "8",
//                "5",
//                "6",
//                "2",
//                "5",
//                "4",
//                "6"
//        };
//
//        for (int i = 0; i < Number.length; i++) {
//            numberPhoneSz.sendKeys(Number[i]);
//        }
    }

    @FindBy(css = "#root > div > div.sc-knHXcE.kIqAIF > div._MemberClubRequisite_tklds_1 > div > div._wrapper_8dmmy_1 > form > div:nth-child(1) > div:nth-child(9) > input[type=text]")
    private static WebElement numberPhoneIP;

    public static void inputNumberPhoneSz() {
        numberPhoneIP.click();
        numberPhoneIP.sendKeys("9548562546");
//        String[] Number = {
//                "9",
//                "5",
//                "4",
//                "8",
//                "5",
//                "6",
//                "2",
//                "5",
//                "4",
//                "6"
//        };
//        numberPhoneIP.click();
//
//        for (int i = 0; i < Number.length; i++) {
//            numberPhoneIP.sendKeys(Number[i]);
//        }
    }

    @FindBy(name = "idEdo")
    private static WebElement idEdo;

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

    @FindBy(css = "#root > div.sc-fggMnX.dQvEiM > div.sc-knHXcE.kIqAIF > div._MemberClubRequisite_tklds_1 > div > div._wrapper_1ery5_1 > form > div:nth-child(4) > div > label > input[type=checkbox]")
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
        deleteDetails.isDisplayed();
        deleteDetails.click();
    }

        @FindBy(css = "._delete_8dmmy_86")
    private static WebElement deleteDetailsIp;

    public static String textDeleteDetailsIP() {
        deleteDetailsIp.isDisplayed();
        return deleteDetailsIp.getText().trim();
    }

    public static void btnDeleteDetailIP() {
        deleteDetailsIp.isDisplayed();
        deleteDetailsIp.click();
    }

    @FindBy(css = "._Button_kio3a_1._btn_1n48r_45._danger_kio3a_110._fullWidth_kio3a_195")
    private static WebElement btnDeleteFromModal;

    public static void clickBtnDeleteFromModal() {
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
        inputPassDepartment();
        inputDateBirth();
        pgDwnPassIssued();
        inputAddressRegister();
        inputIsSameAddress();
        Assert.assertEquals(true, displaedAddressFact());
        Assert.assertEquals("4012252, Санкт-Петергбург, Богач, д5", inputAddressFact());
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
        System.out.println("атрибут=" + atrInputFullName());
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

    @FindBy(id = "SZ")
    private static WebElement selfEmployed;

    public static void btnSelfEmployed() {
        selfEmployed.click();
    }

    @FindBy(id = "IP")
    private static WebElement individEntrep;

    public static void btnIndividEntrep() {
        try {
            individEntrep.isDisplayed();
            individEntrep.click();
        } catch (NoSuchElementException e) {
        }
    }

    @FindBy(name = "ogrnip")
    private static WebElement ogrnip;

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
        Assert.assertEquals("4012252, Санкт-Петергбург, Богач, д5", inputAddressFact());
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
        System.out.println("атрибут=" + atrInputFullName());
        btnSelfEmployed();
    }

    public static void errorSz(WebDriver driver){
        modal.modalTelephone();
        fullName.click();
        inn.click();
        Assert.assertEquals("Это поле обязательное", driver.findElements(By.tagName("span")).get(5).getText());
        pgDwnFullName();
        bik.click();
        Assert.assertEquals("Это поле обязательное", driver.findElements(By.tagName("span")).get(6).getText());
        inn.sendKeys("1");
        Assert.assertEquals("Поле должно иметь 12 символов", driver.findElements(By.tagName("span")).get(6).getText());
        Assert.assertEquals("Это поле обязательное", driver.findElements(By.tagName("span")).get(7).getText());
        bankAccount.click();
        bik.sendKeys("1");
        Assert.assertEquals("Поле должно иметь 9 символов",driver.findElements(By.tagName("span")).get(7).getText());
        Assert.assertEquals("Это поле обязательное", driver.findElements(By.tagName("span")).get(8).getText());
        bankAccount.sendKeys("1");
        Assert.assertEquals("Поле должно иметь 20 символов",driver.findElements(By.tagName("span")).get(8).getText());
        bankKorAccount.click();
        bankName.click();
        Assert.assertEquals("Это поле обязательное", driver.findElements(By.tagName("span")).get(9).getText());
        bankKorAccount.sendKeys("1");
        Assert.assertEquals("Поле должно иметь 20 символов",driver.findElements(By.tagName("span")).get(9).getText());
        Assert.assertEquals("Это поле обязательное", driver.findElements(By.tagName("span")).get(10).getText());
        passSerial.click();
        passNumber.click();
        Assert.assertEquals("Это поле обязательное", driver.findElements(By.tagName("span")).get(11).getText());
        passSerial.sendKeys("1");
        Assert.assertEquals("Поле должно иметь 4 символов",driver.findElements(By.tagName("span")).get(11).getText());
        Assert.assertEquals("Это поле обязательное", driver.findElements(By.tagName("span")).get(12).getText());
        passIssuer.click();
        passNumber.sendKeys("1");
        Assert.assertEquals("Поле должно иметь 6 символов",driver.findElements(By.tagName("span")).get(12).getText());
        Assert.assertEquals("Это поле обязательное", driver.findElements(By.tagName("span")).get(13).getText());
        pgDnBik();
        passIssued.click();
        passDepartment.click();
        Assert.assertEquals("Дата выдачи должна быть заполнена корректно",driver.findElements(By.tagName("span")).get(14).getText());
        passIssued.sendKeys("1");
        Assert.assertEquals("Поле должно иметь 6 символов", driver.findElements(By.tagName("span")).get(15).getText());

        passDepartment.sendKeys("1");
        dateBirth.click();
        addressRegister.click();
        Assert.assertEquals("Дата выдачи должна быть заполнена корректно",driver.findElements(By.tagName("span")).get(14).getText());
        Assert.assertEquals("Дата рождения должна быть заполнена корректно",driver.findElements(By.tagName("span")).get(16).getText());
        dateBirth.sendKeys("1");
        Assert.assertEquals("Дата рождения должна быть заполнена корректно",driver.findElements(By.tagName("span")).get(16).getText());
        Assert.assertEquals("Это поле обязательное", driver.findElements(By.tagName("span")).get(17).getText());
        addressFact.click();
        numberPhoneSz.click();
        idEdo.click();
        Assert.assertEquals("Это поле обязательное", driver.findElements(By.tagName("span")).get(18).getText());
        Assert.assertEquals("Это поле обязательное", driver.findElements(By.tagName("span")).get(19).getText());
        numberPhoneSz.sendKeys("1");
        Assert.assertEquals("Введите номер полностью", driver.findElements(By.tagName("span")).get(19).getText());
    }

        public static void errorIP(WebDriver driver){
        modal.modalTelephone();
        btnIndividEntrep();
        fullName.click();
        ogrnip.click();
        Assert.assertEquals("Это поле обязательное", driver.findElements(By.tagName("span")).get(5).getText());
        pgDwnFullName();
        inn.click();
        Assert.assertEquals("Это поле обязательное", driver.findElements(By.tagName("span")).get(6).getText());
        ogrnip.sendKeys("1");
        inn.sendKeys("1");
        Assert.assertEquals("Поле должно иметь 15 символов", driver.findElements(By.tagName("span")).get(6).getText());
        Assert.assertEquals("Поле должно иметь 12 символов", driver.findElements(By.tagName("span")).get(7).getText());
        addressRegister.click();
        addressFact.click();
        numberPhoneIP.click();
        Assert.assertEquals("Это поле обязательное", driver.findElements(By.tagName("span")).get(8).getText());
        Assert.assertEquals("Это поле обязательное", driver.findElements(By.tagName("span")).get(9).getText());
        bankAccount.click();
        Assert.assertEquals("Это поле обязательное", driver.findElements(By.tagName("span")).get(10).getText());
        numberPhoneIP.sendKeys("1");
        Assert.assertEquals("Введите номер полностью", driver.findElements(By.tagName("span")).get(10).getText());
        Assert.assertEquals("Это поле обязательное", driver.findElements(By.tagName("span")).get(11).getText());
        bik.click();
        bankAccount.sendKeys("1");
        bik.sendKeys("1");
        Assert.assertEquals("Поле должно иметь 20 символов",driver.findElements(By.tagName("span")).get(11).getText());
        Assert.assertEquals("Поле должно иметь 9 символов",driver.findElements(By.tagName("span")).get(12).getText());
        pgDnBik();
        bankKorAccount.click();
        bankName.click();
        idEdo.click();
        Assert.assertEquals("Это поле обязательное", driver.findElements(By.tagName("span")).get(13).getText());
        Assert.assertEquals("Это поле обязательное", driver.findElements(By.tagName("span")).get(14).getText());
        bankKorAccount.sendKeys("1");
        Assert.assertEquals("Поле должно иметь 20 символов",driver.findElements(By.tagName("span")).get(13).getText());
        Assert.assertEquals("Это поле обязательное", driver.findElements(By.tagName("span")).get(15).getText());
    }

}
