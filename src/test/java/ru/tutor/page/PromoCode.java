package ru.tutor.page;

import com.github.dockerjava.api.model.Driver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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


    //промокоды отсутствующие в системе
//private String[] promoNoActive =new String[0];
//private int defaultCountPromocode;
//
//public void add (String promoNoAct){
//    String[] tmp = new String[promoNoActive.length + 1];
//    for (int i = 0; i < promoNoActive.length; i++){
//        tmp[i] = promoNoActive[i];
//    }
//    tmp[tmp.length-1] = promoNoAct;
//    promoNoActive=tmp;
//}

    public static String setBtnPromo() {
//        String[] promoNoActive = {"опытБудНеакт", "опытБудущАкт"};
        if (inputPromo.isDisplayed() && inputPromo.isEnabled()) {
            inputPromo.sendKeys("опытБудНеакт");
            btnPromo.click();
            return error.getText().trim();
        }
        return "ничего не произошло";
    }

    public static String setBtnPromo1() {
        if (inputPromo.isDisplayed() && inputPromo.isEnabled()) {
            inputPromo.sendKeys("опытБудущАкт");
            btnPromo.click();
            return error.getText().trim();
        }
        return "ничего не произошло";
    }

    public static String setBtnPromo2() {
        if (inputPromo.isDisplayed() && inputPromo.isEnabled()) {
            inputPromo.sendKeys("промопосрокуНеакт");
            btnPromo.click();
            return error.getText().trim();
        }
        return "ничего не произошло";
    }

    public static String setBtnPromo3() {
        if (inputPromo.isDisplayed() && inputPromo.isEnabled()) {
            inputPromo.sendKeys("промоСрокБесНеАкт");
            btnPromo.click();
            return error.getText().trim();
        }
        return "ничего не произошло";
    }

    public static String setBtnPromo4() {
        if (inputPromo.isDisplayed() && inputPromo.isEnabled()) {
            inputPromo.sendKeys("ПромоАктСрокБудущ");
            btnPromo.click();
            return error.getText().trim();
        }
        return "ничего не произошло";
    }

    public static String setBtnPromo5() {
        if (inputPromo.isDisplayed() && inputPromo.isEnabled()) {
            inputPromo.sendKeys("ПромоАктСрокБудущ");
            btnPromo.click();
            return error.getText().trim();
        }
        return "ничего не произошло";
    }

    public static String setBtnPromo6() {
        if (inputPromo.isDisplayed() && inputPromo.isEnabled()) {
            inputPromo.sendKeys("ПромоБесНеактиБуд");
            btnPromo.click();
            return error.getText().trim();
        }
        return "ничего не произошло";
    }

    public static String setBtnPromo7() {
        if (inputPromo.isDisplayed() && inputPromo.isEnabled()) {
            inputPromo.sendKeys("подпискаДо24");
            btnPromo.click();
            return error.getText().trim();
        }
        return "ничего не произошло";
    }

    public static String setBtnPromo8() {
        if (inputPromo.isDisplayed() && inputPromo.isEnabled()) {
            inputPromo.sendKeys("алмазыБезСрокНеакт");
            btnPromo.click();
            return error.getText().trim();
        }
        return "ничего не произошло";
    }

    public static String setBtnPromo9() {
        if (inputPromo.isDisplayed() && inputPromo.isEnabled()) {
            inputPromo.sendKeys("алмазыСрокНеакт");
            btnPromo.click();
            return error.getText().trim();
        }
        return "ничего не произошло";
    }

    public static String setBtnPromo10() {
        if (inputPromo.isDisplayed() && inputPromo.isEnabled()) {
            inputPromo.sendKeys("алмазыБудНеакт");
            btnPromo.click();
            return error.getText().trim();
        }
        return "ничего не произошло";
    }

    public static String setBtnPromo11() {
        if (inputPromo.isDisplayed() && inputPromo.isEnabled()) {
            inputPromo.sendKeys("алмазыПрошлоеНеакт");
            btnPromo.click();
            return error.getText().trim();
        }
        return "ничего не произошло";
    }

    public static String setBtnPromo12() {
        if (inputPromo.isDisplayed() && inputPromo.isEnabled()) {
            inputPromo.sendKeys("алмазыДо24Неакт");
            btnPromo.click();
            return error.getText().trim();
        }
        return "ничего не произошло";
    }

    public static String setBtnPromo14() {
        if (inputPromo.isDisplayed() && inputPromo.isEnabled()) {
            inputPromo.sendKeys("АлмазыСрокБудАкт");
            btnPromo.click();
            return error.getText().trim();
        }
        return "ничего не произошло";
    }

    public static String setBtnPromo15() {
        if (inputPromo.isDisplayed() && inputPromo.isEnabled()) {
            inputPromo.sendKeys("алмазыПрошлоеАкт");
            btnPromo.click();
            return error.getText().trim();
        }
        return "ничего не произошло";
    }

    public static String setBtnPromo16() {
        if (inputPromo.isDisplayed() && inputPromo.isEnabled()) {
            inputPromo.sendKeys("алмазыДо");
            btnPromo.click();
            return error.getText().trim();
        }
        return "ничего не произошло";
    }

    public static String setBtnPromo17() {
        if (inputPromo.isDisplayed() && inputPromo.isEnabled()) {
            inputPromo.sendKeys("алмазыОт");
            btnPromo.click();
            return error.getText().trim();
        }
        return "ничего не произошло";
    }


    public static String setBtnPromo18() {
        if (inputPromo.isDisplayed() && inputPromo.isEnabled()) {
            inputPromo.sendKeys("опытПрошлоеНеакт");
            btnPromo.click();
            return error.getText().trim();
        }
        return "ничего не произошло";
    }


    public static String setBtnPromo19() {
        if (inputPromo.isDisplayed() && inputPromo.isEnabled()) {
            inputPromo.sendKeys("опытДо25Неакт");
            btnPromo.click();
            return error.getText().trim();
        }
        return "ничего не произошло";
    }


    public static String setBtnPromo22() {
        if (inputPromo.isDisplayed() && inputPromo.isEnabled()) {
            inputPromo.sendKeys("опытОт25Неакт");
            btnPromo.click();
            return error.getText().trim();
        }
        return "ничего не произошло";
    }


    public static String setBtnPromo23() {
        if (inputPromo.isDisplayed() && inputPromo.isEnabled()) {
            inputPromo.sendKeys("опытДейстНеакт");
            btnPromo.click();
            return error.getText().trim();
        }
        return "ничего не произошло";
    }

    public static String setBtnPromo24() {
        if (inputPromo.isDisplayed() && inputPromo.isEnabled()) {
            inputPromo.sendKeys("опытБудНеакт");
            btnPromo.click();
            return error.getText().trim();
        }
        return "ничего не произошло";
    }

    public static String setBtnPromo25() {
        if (inputPromo.isDisplayed() && inputPromo.isEnabled()) {
            inputPromo.sendKeys("опытОт25Акт");
            btnPromo.click();
            return error.getText().trim();
        }
        return "ничего не произошло";
    }

    public static String setBtnPromo26() {
        if (inputPromo.isDisplayed() && inputPromo.isEnabled()) {
            inputPromo.sendKeys("опытОт24Акт");
            btnPromo.click();
            return error.getText().trim();
        }
        return "ничего не произошло";
    }

    public static String setBtnPromo27() {
        if (inputPromo.isDisplayed() && inputPromo.isEnabled()) {
            inputPromo.sendKeys("опытБудущАкт");
            btnPromo.click();
            return error.getText().trim();
        }
        return "ничего не произошло";
    }

    public static String setBtnPromo28() {
        if (inputPromo.isDisplayed() && inputPromo.isEnabled()) {
            inputPromo.sendKeys("опытПрошлоеАкт");
            btnPromo.click();
            return error.getText().trim();
        }
        return "ничего не произошло";
    }


    //успешные

    public static String setBtnPromo29() {
        if (inputPromo.isDisplayed() && inputPromo.isEnabled()) {
            inputPromo.sendKeys("опытДейстАкт");
            btnPromo.click();
            return error.getText().trim();
        }
        return "ничего не произошло";
    }

    public static String setBtnPromo30() {
        if (inputPromo.isDisplayed() && inputPromo.isEnabled()) {
            inputPromo.sendKeys("ОпытБессрочный");
            btnPromo.click();
            return error.getText().trim();
        }
        return "ничего не произошло";
    }

    public static String setBtnPromo31() {
        if (inputPromo.isDisplayed() && inputPromo.isEnabled()) {
            inputPromo.sendKeys("алмазыБес");
            btnPromo.click();
            return error.getText().trim();
        }
        return "ничего не произошло";
    }

    public static String setBtnPromo32() {
        if (inputPromo.isDisplayed() && inputPromo.isEnabled()) {
            inputPromo.sendKeys("АлмазыСрокАкт");
            btnPromo.click();
            return error.getText().trim();
        }
        return "ничего не произошло";
    }

    public static String setBtnPromo33() {
        if (inputPromo.isDisplayed() && inputPromo.isEnabled()) {
            inputPromo.sendKeys("ПромоПодпискаБес");
            btnPromo.click();
            return error.getText().trim();
        }
        return "ничего не произошло";
    }

    public static String setBtnPromo34() {
        if (inputPromo.isDisplayed() && inputPromo.isEnabled()) {
            inputPromo.sendKeys("промоподпискаПоСроку");
            btnPromo.click();
            return error.getText().trim();
        }
        return "ничего не произошло";
    }

    //нет в БД

    public static String setBtnPromo35() {
        if (inputPromo.isDisplayed() && inputPromo.isEnabled()) {
            inputPromo.sendKeys("123промо");
            btnPromo.click();
            return error.getText().trim();
        }
        return "ничего не произошло";
    }


}
