package ru.tutor.page;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;

public class footer {

    //футер-информация
    private static SelenideElement AntyPolicy = $$("a").find(exactText("Антикоррупционная политика"));
    private static SelenideElement study = $$("a").find(exactText("Образовательная лицензия"));
    private static SelenideElement policy = $$("a").find(exactText("Политика конфиденциальности"));
    private static SelenideElement userAgreement = $$("a").find(exactText("Пользовательское соглашение"));
    private static SelenideElement offer = $$("a").find(exactText("Публичная оферта"));
    private static SelenideElement manual = $$("a").find(exactText("Руководство пользователя"));
    private static SelenideElement personalDate = $$("a").find(exactText("Согласие на обработку персональных данных"));

    //футер- о нас
    private static SelenideElement aboutCompany = $$("a").find(exactText("О компании"));
    private static SelenideElement aboutSubscription = $$("a").find(exactText("О подписке"));
    private static SelenideElement contacts = $$("a").find(exactText("Контакты"));
    private static SelenideElement newHorizons = $$("a").find(exactText("Новые горизонты"));

    // футтер - дополнительно
    private static SelenideElement webmasters = $$("a").find(exactText("Вебмастерам"));
    private static SelenideElement forAuthors = $$("a").find(exactText("Для авторов"));
    private static SelenideElement install = $$("a").find(exactText("Установка ПО"));
    private static SelenideElement career = $$("a").find(exactText("Карьера"));

    // первый блок
    private static SelenideElement number = $$("a").find(exactText("8 (800) 555 32 82"));
    private static SelenideElement information = $$("span").find(exactText("Автоинформатор"));
    private static SelenideElement emailSupport = $$("a").find(exactText("support@tutorplace.ru"));
    private static SelenideElement quastions = $$("span").find(exactText("По общим вопросам"));
    private static SelenideElement online = $$("p").find(exactText("Все обращения принимаются только в электронном виде на почту support@tutorplace.ru или онлайн - чат на сайте"));
    private static SelenideElement didgital = $$("p").find(exactText("© OOO «ДИДЖИТАЛ КОНТЕНТ» 2022–2025"));
    
    public static String setAntyPolicy() {
        return AntyPolicy.getText().trim();
    }

    public static String atrAntyPolicy() {
        return AntyPolicy.getDomAttribute("href");
    }

    public static String setStudy() {
        return study.getText().trim();
    }

    public static String atrStudy() {
        return study.getDomAttribute("href");
    }

    public static String setPolicy() {
        return policy.getText().trim();
    }

    public static String atrPolicy() {
        return policy.getDomAttribute("href");
    }

    public static String setUserAgreement() {
        return userAgreement.getText().trim();
    }

    public static String atrUserAgreement() {
        return userAgreement.getDomAttribute("href");
    }

    public static String setOffer() {
        return offer.getText().trim();
    }

    public static String atrOffer() {
        return offer.getDomAttribute("href");
    }

    public static String setManual() {
        return manual.getText().trim();
    }

    public static String atrManual() {
        return manual.getDomAttribute("href");
    }

    public static String setPersonalDate() {
        return personalDate.getText().trim();
    }

    public static String atrPersonalDate() {
        return personalDate.getDomAttribute("href");
    }

    public static String setAboutCompany() {
        return aboutCompany.getText().trim();
    }

    public static String atrAboutCompany() {
        return aboutCompany.getDomAttribute("href");
    }

    public static void clickAboutCompany(){
       step ("клик по гиперссылке `О компании`", ()->{
        aboutCompany.shouldBe(visible).click();
        sleep(500);});
    }

    public static String setAboutSubscription() {
        return aboutSubscription.getText().trim();
    }

    public static String atrAboutSubscription() {
        return aboutSubscription.getDomAttribute("href");
    }

    public static void clickAboutSubscription(){
        step ("клик по гиперссылке `О Подписке`", ()->{
        aboutSubscription.sendKeys(Keys.END);
        aboutSubscription.shouldBe(visible).click();
        sleep(500);});
    }

    public static String setContacts() {
        return contacts.getText().trim();
    }

    public static String atrContacts() {
        return contacts.getDomAttribute("href");
    }

    public static void clickContacts(){
        step ("клик по гиперссылке `Контакты`", ()->{
        contacts.sendKeys(Keys.END);
        contacts.shouldBe(visible).click();
        sleep(500);});
    }

    public static String setNewHorizon() {
        return newHorizons.getText().trim();
    }

    public static String atrNewHorizon() {
        return newHorizons.getDomAttribute("href");
    }

    public static void clickNewHorizon(){
        step ("клик по гиперссылке `новые горизонты`", ()->{
        newHorizons.sendKeys(Keys.END);
        newHorizons.shouldBe(visible).click();
        sleep(500);});
    }

    public static String setWebmaster() {
        return webmasters.getText().trim();
    }

    public static String atrWebmasters() {
        return webmasters.getDomAttribute("href");
    }

    public static void clickWebmasters(){
        step ("клик по гиперссылке `Вебмастерам`", ()->{
        webmasters.sendKeys(Keys.END);
        webmasters.shouldBe(visible).click();
        sleep(500);});
    }

    public static String setForAuthors() {
        return forAuthors.getText().trim();
    }

    public static String atrForAuthors() {
        return forAuthors.getDomAttribute("href");
    }

    public static void clickForAuthors(){
        step ("клик по гиперссылке `Для авторов`", ()->{
        forAuthors.sendKeys(Keys.END);
        forAuthors.shouldBe(visible).click();
        sleep(500);});
    }

    public static String setInstall() {
        return install.getText().trim();
    }

    public static String atrInstall() {
        return install.getDomAttribute("href");
    }

    public static void clickInstall(){
        step ("клик по гиперссылке `Установка ПО`", ()->{
        install.sendKeys(Keys.END);
        install.shouldBe(visible).click();
        sleep(500);});
    }

    public static String setCareer() {
        return career.getText().trim();
    }

    public static String atrCareer() {
        return career.getDomAttribute("href");
    }

    public static void clickCareer(){
        step ("клик по гиперссылке `Карьера`", ()->{
        career.shouldBe(visible).click();
        sleep(500);});
    }

    public static String setNumber() {
        return number.getText().trim();
    }

    public static String atrNumber() {
        return number.getDomAttribute("href");
    }

    public static String setInformation() {
        return information.getText().trim();
    }

    public static String setEmailSupport() {
        return emailSupport.getText().trim();
    }

    public static String atrEmailSupport() {
        return emailSupport.getDomAttribute("href");
    }

    public static String setQuastions() {
        return quastions.getText().trim();
    }

    public static String setOnline() {
        return online.getText().trim();
    }

    public static String setDidgital() {
        return didgital.getText().trim();
    }

}
