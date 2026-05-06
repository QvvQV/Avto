package ru.tutor.page;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;
import ru.tutor.utils.WaitHelper;
import ru.tutor.utils.UniversalMethod;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.$$;
import static io.qameta.allure.Allure.step;

public class Footer {

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
    private static final String questions_TEXT = "По общим вопросам";
    private static final String ONLINE_TEXT = "Все обращения принимаются только в электронном виде на почту support@tutorplace.ru или онлайн - чат на сайте";
    private static final String DIGITAL_TEXT = "© OOO «ДИДЖИТАЛ КОНТЕНТ» 2022–2026";

    //футер-информация
    private final SelenideElement AntiPolicy = $$("a").find(exactText(ANTY_POLICY_TEXT));
    private final SelenideElement studyLink = $$("a").find(exactText(STUDY_TEXT));
    private final SelenideElement policyLink = $$("a").find(exactText(POLICY_TEXT));
    private final SelenideElement userAgreement = $$("a").find(exactText(USER_AGREEMENT_TEXT));
    private final SelenideElement offerLink = $$("a").find(exactText(OFFER_TEXT));
    private final SelenideElement manualLink = $$("a").find(exactText(MANUAL_TEXT));
    private final SelenideElement personalDate = $$("a").find(exactText(PERSONAL_DATE_TEXT));

    //футер- о нас
    private final SelenideElement aboutCompany = $$("a").find(exactText(ABOUT_COMPANY_TEXT));
    private final SelenideElement aboutSubscription = $$("a").find(exactText(ABOUT_SUBSCRIPTION_TEXT));
    private final SelenideElement contacts = $$("a").find(exactText(CONTACTS_TEXT));
    private final SelenideElement newHorizons = $$("a").find(exactText(NEW_HORIZONS_TEXT));

    // футтер - дополнительно
    private final SelenideElement webmastersLink = $$("a").find(exactText(WEBMASTERS_TEXT));
    private final SelenideElement forAuthorsLink = $$("a").find(exactText(FOR_AUTHORS_TEXT));
    private final SelenideElement install = $$("a").find(exactText(INSTALL_TEXT));
    private final SelenideElement careerLink = $$("a").find(exactText(CAREER_TEXT));

    // первый блок
    private final SelenideElement number = $$("a").find(exactText(NUMBER_TEXT));
    private final SelenideElement information = $$("span").find(exactText(INFORMATION_TEXT));
    private final SelenideElement emailSupport = $$("a").find(exactText(EMAIL_SUPPORT_TEXT));
    private final SelenideElement questions  = $$("span").find(exactText(questions_TEXT));
    private final SelenideElement online = $$("p").find(exactText(ONLINE_TEXT));
    private final SelenideElement digital = $$("p").find(exactText(DIGITAL_TEXT));

    public String setAntyPolicy() {
        return UniversalMethod.getElementText(AntiPolicy, ANTY_POLICY_TEXT);
    }

    public String atrAntyPolicy() {
        return UniversalMethod.getElementHref(AntiPolicy, ANTY_POLICY_TEXT);
    }

    public String setStudy() {
        return UniversalMethod.getElementText(studyLink, STUDY_TEXT);
    }

    public String atrStudy() {
        return UniversalMethod.getElementHref(studyLink, STUDY_TEXT);
    }

    public String setPolicy() {
        return UniversalMethod.getElementText(policyLink, POLICY_TEXT);
    }

    public String atrPolicy() {
        return UniversalMethod.getElementHref(policyLink, POLICY_TEXT);
    }

    public String setUserAgreement() {
        return UniversalMethod.getElementText(userAgreement, USER_AGREEMENT_TEXT);
    }

    public String atrUserAgreement() {
        return UniversalMethod.getElementHref(userAgreement, USER_AGREEMENT_TEXT);
    }

    public String setOffer() {
        return UniversalMethod.getElementText(offerLink, OFFER_TEXT);
    }

    public String atrOffer() {
        return UniversalMethod.getElementHref(offerLink, OFFER_TEXT);
    }

    public String setManual() {
        return manualLink.getText().trim();
    }

    public String atrManual() {
        return manualLink.getDomAttribute("href");
    }

    public String setPersonalDate() {
        return UniversalMethod.getElementText(personalDate, PERSONAL_DATE_TEXT);
    }

    public String atrPersonalDate() {
        return UniversalMethod.getElementHref(personalDate, PERSONAL_DATE_TEXT);
    }

    public String setAboutCompany() {
        return UniversalMethod.getElementText(aboutCompany, ABOUT_COMPANY_TEXT);
    }

    public String atrAboutCompany() {
        return UniversalMethod.getElementHref(aboutCompany, ABOUT_COMPANY_TEXT);
    }

    public void clickAboutCompany(){
       step ("клик по гиперссылке `О компании`", ()->{
        UniversalMethod.safeClick(aboutCompany,  ABOUT_COMPANY_TEXT);
        WaitHelper.waitForPageLoad();});
    }

    public String setAboutSubscription() {
        return UniversalMethod.getElementText(aboutSubscription, ABOUT_SUBSCRIPTION_TEXT);
    }

    public String atrAboutSubscription() {
        return UniversalMethod.getElementHref(aboutSubscription, ABOUT_SUBSCRIPTION_TEXT);
    }

    public void clickAboutSubscription(){
        step ("Клик по гиперссылке `О Подписке`", ()->{
        aboutSubscription.sendKeys(Keys.END);
        UniversalMethod.safeClick(aboutSubscription, ABOUT_SUBSCRIPTION_TEXT);
        WaitHelper.waitForPageLoad();
            
         });
    }

    public String setContacts() {
        return UniversalMethod.getElementText(contacts, CONTACTS_TEXT);
    }

    public String atrContacts() {
        return UniversalMethod.getElementHref(contacts, CONTACTS_TEXT);
    }

    public void clickContacts(){
        step ("клик по гиперссылке `Контакты`", ()->{
        contacts.sendKeys(Keys.END);
        UniversalMethod.safeClick(contacts, CONTACTS_TEXT);
        WaitHelper.waitForPageLoad();});
    }

    public String setNewHorizon() {
        return UniversalMethod.getElementText(newHorizons, NEW_HORIZONS_TEXT);
    }

    public String atrNewHorizon() {
        return UniversalMethod.getElementHref(newHorizons, NEW_HORIZONS_TEXT);
    }

    public void clickNewHorizon(){
        step ("клик по гиперссылке `новые горизонты`", ()->{
        newHorizons.sendKeys(Keys.END);
        UniversalMethod.safeClick(newHorizons, NEW_HORIZONS_TEXT);
        WaitHelper.waitForPageLoad();});
    }

    public String setWebmaster() {
        return UniversalMethod.getElementText(webmastersLink, WEBMASTERS_TEXT);
    }

    public String atrWebmasters() {
        return UniversalMethod.getElementHref(webmastersLink, WEBMASTERS_TEXT);
    }

    public void clickWebmasters(){
        step ("клик по гиперссылке `Вебмастерам`", ()->{
        webmastersLink.sendKeys(Keys.END);
        UniversalMethod.safeClick(webmastersLink, WEBMASTERS_TEXT);
        WaitHelper.waitForPageLoad();});
    }

    public String setForAuthors() {
        return UniversalMethod.getElementText(forAuthorsLink, FOR_AUTHORS_TEXT);
    }

    public String atrForAuthors() {
        return UniversalMethod.getElementHref(forAuthorsLink, FOR_AUTHORS_TEXT);
    }

    public void clickForAuthors(){
        step ("клик по гиперссылке `Для авторов`", ()->{
        forAuthorsLink.sendKeys(Keys.END);
        UniversalMethod.safeClick(forAuthorsLink, FOR_AUTHORS_TEXT);
        WaitHelper.waitForPageLoad();});
    }

    public String setInstall() {
        return UniversalMethod.getElementText(install, INSTALL_TEXT);
    }

    public String atrInstall() {
        return UniversalMethod.getElementHref(install, INSTALL_TEXT);
    }

    public void clickInstall(){
        step ("клик по гиперссылке `Установка ПО`", ()->{
        install.sendKeys(Keys.END);
        UniversalMethod.safeClick(install, INSTALL_TEXT);
        WaitHelper.waitForPageLoad();});
    }

    public String setCareer() {
        return UniversalMethod.getElementText(careerLink, CAREER_TEXT );
    }

    public String atrCareer() {
        return UniversalMethod.getElementHref(careerLink, CAREER_TEXT );
    }

    public void clickCareer(){
        step ("клик по гиперссылке `Карьера`", ()->{
            UniversalMethod.safeClick(careerLink, CAREER_TEXT);
        WaitHelper.waitForPageLoad();});
    }

    public String setNumber() {
        return UniversalMethod.getElementText(number, NUMBER_TEXT);
    }

    public String atrNumber() {
        return UniversalMethod.getElementHref(number, NUMBER_TEXT);
    }

    public String setInformation() {
        return UniversalMethod.getElementText(information, INFORMATION_TEXT);
    }

    public String setEmailSupport() {
        return UniversalMethod.getElementText(emailSupport, EMAIL_SUPPORT_TEXT);
    }

    public String atrEmailSupport() {
        return UniversalMethod.getElementHref(emailSupport, EMAIL_SUPPORT_TEXT);
    }

    public String setQuestions () {
        return UniversalMethod.getElementText(questions , questions_TEXT);
    }

    public String setOnline() {
        return UniversalMethod.getElementText(online, ONLINE_TEXT);
    }

    public String setDidgital() {
        return UniversalMethod.getElementText(digital, DIGITAL_TEXT);
    }
}