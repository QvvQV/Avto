package ru.tutor.page;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;
import ru.tutor.utils.universalMethod;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.sleep;
import static io.qameta.allure.Allure.step;

public class footer {

    private static final String ANTY_POLICY_TEXT = "Антикоррупционная политика";
    private static final String STUDY_TEXT = "Образовательная лицензия";
    private static final String POLICY_TEXT = "Политика конфиденциальности";
    private static final String USER_AGREEMENT_TEXT = "Пользовательское соглашение";
    private static final String OFFER_TEXT = "Публичная оферта";
    private static final String MANUAL_TEXT = "Руководство пользователя";
    private static final String PERSONAL_DATE_TEXT = "Согласие на обработку персональных данных";
    private static final String ABOUT_COMPANY_TEXT = "О компании";
    private static final String ABOUT_SUBSCRIPTION_TEXT = "О подписке";
    private static final String CONTACTS_TEXT = "Контакты";
    private static final String NEW_HORIZONS_TEXT = "Новые горизонты";
    private static final String WEBMASTERS_TEXT = "Вебмастерам";
    private static final String FOR_AUTHORS_TEXT = "Для авторов";
    private static final String INSTALL_TEXT = "Установка ПО";
    private static final String CAREER_TEXT = "Карьера";
    private static final String NUMBER_TEXT = "8 (800) 555 32 82";
    private static final String INFORMATION_TEXT = "Автоинформатор";
    private static final String EMAIL_SUPPORT_TEXT = "support@tutorplace.ru";
    private static final String QUASTIONS_TEXT = "По общим вопросам";
    private static final String ONLINE_TEXT = "Все обращения принимаются только в электронном виде на почту support@tutorplace.ru или онлайн - чат на сайте";
    private static final String DIGITAL_TEXT = "© OOO «ДИДЖИТАЛ КОНТЕНТ» 2022–2026";

    //футер-информация
    private static final SelenideElement AntyPolicy = $$("a").find(exactText(ANTY_POLICY_TEXT));
    private static final SelenideElement study = $$("a").find(exactText(STUDY_TEXT));
    private static final SelenideElement policy = $$("a").find(exactText(POLICY_TEXT));
    private static final SelenideElement userAgreement = $$("a").find(exactText(USER_AGREEMENT_TEXT));
    private static final SelenideElement offer = $$("a").find(exactText(OFFER_TEXT));
    private static final SelenideElement manual = $$("a").find(exactText(MANUAL_TEXT));
    private static final SelenideElement personalDate = $$("a").find(exactText(PERSONAL_DATE_TEXT));

    //футер- о нас
    private static final SelenideElement aboutCompany = $$("a").find(exactText(ABOUT_COMPANY_TEXT));
    private static final SelenideElement aboutSubscription = $$("a").find(exactText(ABOUT_SUBSCRIPTION_TEXT));
    private static final SelenideElement contacts = $$("a").find(exactText(CONTACTS_TEXT));
    private static final SelenideElement newHorizons = $$("a").find(exactText(NEW_HORIZONS_TEXT));

    // футтер - дополнительно
    private static final SelenideElement webmasters = $$("a").find(exactText(WEBMASTERS_TEXT));
    private static final SelenideElement forAuthors = $$("a").find(exactText(FOR_AUTHORS_TEXT));
    private static final SelenideElement install = $$("a").find(exactText(INSTALL_TEXT));
    private static final SelenideElement career = $$("a").find(exactText(CAREER_TEXT));

    // первый блок
    private static final SelenideElement number = $$("a").find(exactText(NUMBER_TEXT));
    private static final SelenideElement information = $$("span").find(exactText(INFORMATION_TEXT));
    private static final SelenideElement emailSupport = $$("a").find(exactText(EMAIL_SUPPORT_TEXT));
    private static final SelenideElement quastions = $$("span").find(exactText(QUASTIONS_TEXT));
    private static final SelenideElement online = $$("p").find(exactText(ONLINE_TEXT));
    private static final SelenideElement digital = $$("p").find(exactText(DIGITAL_TEXT));

    public static String setAntyPolicy() {
        return universalMethod.getElementText(AntyPolicy, ANTY_POLICY_TEXT);
    }

    public static String atrAntyPolicy() {
        return universalMethod.getElementHref(AntyPolicy, ANTY_POLICY_TEXT);
    }

    public static String setStudy() {
        return universalMethod.getElementText(study, STUDY_TEXT);
    }

    public static String atrStudy() {
        return universalMethod.getElementHref(study, STUDY_TEXT);
    }

    public static String setPolicy() {
        return universalMethod.getElementText(policy, POLICY_TEXT);
    }

    public static String atrPolicy() {
        return universalMethod.getElementHref(policy, POLICY_TEXT);
    }

    public static String setUserAgreement() {
        return universalMethod.getElementText(userAgreement, USER_AGREEMENT_TEXT);
    }

    public static String atrUserAgreement() {
        return universalMethod.getElementHref(userAgreement, USER_AGREEMENT_TEXT);
    }

    public static String setOffer() {
        return universalMethod.getElementText(offer, OFFER_TEXT);
    }

    public static String atrOffer() {
        return universalMethod.getElementHref(offer, OFFER_TEXT);
    }

    public static String setManual() {
        return manual.getText().trim();
    }

    public static String atrManual() {
        return manual.getDomAttribute("href");
    }

    public static String setPersonalDate() {
        return universalMethod.getElementText(personalDate, PERSONAL_DATE_TEXT);
    }

    public static String atrPersonalDate() {
        return universalMethod.getElementHref(personalDate, PERSONAL_DATE_TEXT);
    }

    public static String setAboutCompany() {
        return universalMethod.getElementText(aboutCompany, ABOUT_COMPANY_TEXT);
    }

    public static String atrAboutCompany() {
        return universalMethod.getElementHref(aboutCompany, ABOUT_COMPANY_TEXT);
    }

    public static void clickAboutCompany(){
       step ("клик по гиперссылке `О компании`", ()->{
        universalMethod.safeClick(aboutCompany,  ABOUT_COMPANY_TEXT);
        sleep(500);});
    }

    public static String setAboutSubscription() {
        return universalMethod.getElementText(aboutSubscription, ABOUT_SUBSCRIPTION_TEXT);
    }

    public static String atrAboutSubscription() {
        return universalMethod.getElementHref(aboutSubscription, ABOUT_SUBSCRIPTION_TEXT);
    }

    public static void clickAboutSubscription(){
        step ("Клик по гиперссылке `О Подписке`", ()->{
        aboutSubscription.sendKeys(Keys.END);
        universalMethod.safeClick(aboutSubscription, ABOUT_SUBSCRIPTION_TEXT);
        sleep(500);});
    }

    public static String setContacts() {
        return universalMethod.getElementText(contacts, CONTACTS_TEXT);
    }

    public static String atrContacts() {
        return universalMethod.getElementHref(contacts, CONTACTS_TEXT);
    }

    public static void clickContacts(){
        step ("клик по гиперссылке `Контакты`", ()->{
        contacts.sendKeys(Keys.END);
        universalMethod.safeClick(contacts, CONTACTS_TEXT);
        sleep(500);});
    }

    public static String setNewHorizon() {
        return universalMethod.getElementText(newHorizons, NEW_HORIZONS_TEXT);
    }

    public static String atrNewHorizon() {
        return universalMethod.getElementHref(newHorizons, NEW_HORIZONS_TEXT);
    }

    public static void clickNewHorizon(){
        step ("клик по гиперссылке `новые горизонты`", ()->{
        newHorizons.sendKeys(Keys.END);
        universalMethod.safeClick(newHorizons, NEW_HORIZONS_TEXT);
        sleep(500);});
    }

    public static String setWebmaster() {
        return universalMethod.getElementText(webmasters, WEBMASTERS_TEXT);
    }

    public static String atrWebmasters() {
        return universalMethod.getElementHref(webmasters, WEBMASTERS_TEXT);
    }

    public static void clickWebmasters(){
        step ("клик по гиперссылке `Вебмастерам`", ()->{
        webmasters.sendKeys(Keys.END);
        universalMethod.safeClick(webmasters, WEBMASTERS_TEXT);
        sleep(500);});
    }

    public static String setForAuthors() {
        return universalMethod.getElementText(forAuthors, FOR_AUTHORS_TEXT);
    }

    public static String atrForAuthors() {
        return universalMethod.getElementHref(forAuthors, FOR_AUTHORS_TEXT);
    }

    public static void clickForAuthors(){
        step ("клик по гиперссылке `Для авторов`", ()->{
        forAuthors.sendKeys(Keys.END);
        universalMethod.safeClick(forAuthors, FOR_AUTHORS_TEXT);
        sleep(500);});
    }

    public static String setInstall() {
        return universalMethod.getElementText(install, INSTALL_TEXT);
    }

    public static String atrInstall() {
        return universalMethod.getElementHref(install, INSTALL_TEXT);
    }

    public static void clickInstall(){
        step ("клик по гиперссылке `Установка ПО`", ()->{
        install.sendKeys(Keys.END);
        universalMethod.safeClick(install, INSTALL_TEXT);
        sleep(500);});
    }

    public static String setCareer() {
        return universalMethod.getElementText(career, CAREER_TEXT );
    }

    public static String atrCareer() {
        return universalMethod.getElementHref(career, CAREER_TEXT );
    }

    public static void clickCareer(){
        step ("клик по гиперссылке `Карьера`", ()->{
            universalMethod.safeClick(career, CAREER_TEXT);
        sleep(500);});
    }

    public static String setNumber() {
        return universalMethod.getElementText(number, NUMBER_TEXT);
    }

    public static String atrNumber() {
        return universalMethod.getElementHref(number, NUMBER_TEXT);
    }

    public static String setInformation() {
        return universalMethod.getElementText(information, INFORMATION_TEXT);
    }

    public static String setEmailSupport() {
        return universalMethod.getElementText(emailSupport, EMAIL_SUPPORT_TEXT);
    }

    public static String atrEmailSupport() {
        return universalMethod.getElementHref(emailSupport, EMAIL_SUPPORT_TEXT);
    }

    public static String setQuastions() {
        return universalMethod.getElementText(quastions, QUASTIONS_TEXT);
    }

    public static String setOnline() {
        return universalMethod.getElementText(online, ONLINE_TEXT);
    }

    public static String setDidgital() {
        return universalMethod.getElementText(digital, DIGITAL_TEXT);
    }
}