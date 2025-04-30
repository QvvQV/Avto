package ru.tutor.page;

import com.github.dockerjava.api.model.Driver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class PromoCode extends Driver {

    public PromoCode(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = ".sc-cwHptR")
    private static WebElement inputPromo;

    @FindBy(tagName = "button")
    private static WebElement btnPromo;

    @FindBy(css = "._message_16ynf_52")
    private static WebElement error;

    public static void doubleClick(WebDriver driver) {
        WebElement clickable = driver.findElement(By.cssSelector(".sc-cwHptR"));
        new Actions(driver)
                .doubleClick(clickable)
                .perform();
        Product.Clear();
    }

    //промокоды отсутствующие в системе
    private String[] promoNoActive = new String[0];
//private int defaultCountPromocode;

    public static String PromoNoActive(WebDriver driver) {
//    String[] tmp = new String[promoNoActive.length + 1];
        String[] promoNoActive = {
                "опытБудНеакт",
                "опытБудущАкт",
                "промопосрокуНеакт",
                "промоСрокБесНеАкт",
                "ПромоАктСрокБудущ",
                "ПромоАктСрокБудущ",
                "ПромоБесНеактиБуд",
                "подпискаДо24",
                "алмазыБезСрокНеакт",
                "алмазыСрокНеакт",
                "алмазыБудНеакт",
                "алмазыПрошлоеНеакт",
                "алмазыДо24Неакт",
                "АлмазыСрокБудАкт",
                "алмазыПрошлоеАкт",
                "алмазыДо",
                "алмазыОт",
                "опытПрошлоеНеакт",
                "опытДо25Неакт",
                "опытДейстНеакт",
                "опытБудНеакт",
                "опытОт25Акт",
                "опытОт24Акт",
                "опытБудущАкт",
                "опытПрошлоеАкт"};

        for (int i = 0; i < promoNoActive.length; i++) {
            if (inputPromo.isDisplayed() && inputPromo.isEnabled()) {
                inputPromo.sendKeys(promoNoActive[i]);
                btnPromo.click();
                System.out.println(error.getText().trim());
                doubleClick(driver);
            }
        }
        return "Пустое поле";
    }


    //успешные

    public static String PromoActive(WebDriver driver) {
        String[] promoActive = {"ОпытБессрочный",
                "опытДейстАкт",
                "алмазыБес",
                "АлмазыСрокАкт",
                "ПромоПодпискаБес",
                "промоподпискаПоСроку",
                "123промо"
        };

        for (int i = 0; i < promoActive.length; i++) {
            if (inputPromo.isDisplayed() && inputPromo.isEnabled()) {
                inputPromo.sendKeys(promoActive[i]);
                btnPromo.click();
                System.out.println(error.getText().trim());
                doubleClick(driver);
            }
        }
        return "Пустое поле";
    }
    //"123промо" нет в БД
}
