package ru.tutor.steps;

/*
 * Модальное окно для Создания Реневалла по email-рассылке
 */

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.openqa.selenium.*;
import ru.tutor.utils.WaitHelper;
import ru.tutor.utils.universalMethod;


import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$$;

public class AdminToUserLoginSteps {

    // Локатор
    private final String YES_TEXT = "Да!";
    private final SelenideElement YES_BUTTON = $$("button").find(exactText(YES_TEXT));

    @Step("Получить токен из ЛК пользователя")
    public static String extractTokenFromCurrentPage() {
        Selenide.sleep(3000);
        WaitHelper.waitForAnimationComplete();
        WaitHelper.waitForPageLoad();

        String token = extractFromLocalStorage();
        if(isValidToken(token)) {
            Allure.addAttachment("✓ Токен найден в localStorage: ", token);
            return token;
        }

        throw new RuntimeException("Токен не найден на странице");
    }

    @Step("Извлечь токен из localStorage")
    public static String extractFromLocalStorage() {
        try {
            JavascriptExecutor js = (JavascriptExecutor) WebDriverRunner.getWebDriver();

            // Сначала проверяем, доступен ли localStorage
            Boolean isLocalStorageAvailable = (Boolean) js.executeScript(
                    "try { return typeof window.localStorage !== 'undefined'; } catch(e) { return false; }");

            if (Boolean.FALSE.equals(isLocalStorageAvailable)) {
                System.out.println("⚠️ localStorage недоступен");
                return null;
            }

            // Пробуем получить токен
            String[] tokenKeys = {
                    "access_token", "token", "auth_token", "jwt",
                    "bearer", "user_token", "session_token"
            };

            for (String key : tokenKeys) {
                try {
                    String token = (String) js.executeScript(
                            "try { return window.localStorage.getItem('" + key + "'); } " +
                                    "catch(e) { return null; }");

                    if (isValidToken(token)) {
                        return token;
                    }
                } catch (Exception e) {
                    // Игнорируем ошибки для конкретного ключа
                }
            }

        } catch (Exception e) {
            System.err.println("Ошибка при доступе к localStorage: " + e.getMessage());
        }

        return null;
    }

    public static boolean isValidToken(String token) {
        return token != null && !token.trim().isEmpty();
    }

    @Step("Переключиться на вкладку и получить токен")
    public static String switchToTabAndGetToken() {
        return extractTokenFromCurrentPage();
    }

    @Step("Проводим оплату подписки из письма email-рассылки")
    public void sendEmailPay() {
        WaitHelper.waitForAnimationComplete();
        universalMethod.clickButton(YES_BUTTON,YES_TEXT);
        WaitHelper.waitForAnimationComplete();
    }

//  TODO: в будущем может пригодится
//   @Step("Получить aff_rid из ЛК пользователя")
//    public static String extractAffRidFromCurrentPage() {
// TODO
//        String affRid = extractFromLocalStorageAffRid();
//        if(isValidToken(affRid)) {
//            System.out.println("✓ aff_rid найден в localStorage: " +  affRid);
//            return affRid;
//        }
// TODO
//        throw new RuntimeException("aff_rid не найден на странице");
//    }
//    @Step("Извлечь аff_rid из localStorage")
//    public static String extractFromLocalStorageAffRid() {
//        try {
//            JavascriptExecutor js = (JavascriptExecutor) WebDriverRunner.getWebDriver();
// TODO:
//            // Сначала проверяем, доступен ли localStorage
//            Boolean isLocalStorageAvailable = (Boolean) js.executeScript(
//                    "try { return typeof window.localStorage !== 'undefined'; } catch(e) { return false; }");
// TODO:
//            if (Boolean.FALSE.equals(isLocalStorageAvailable)) {
//                System.out.println("⚠️ localStorage недоступен");
//                return null;
//            }
// TODO:
//            // Пробуем получить aff_rid
//            String[] affridKeys = {
//                    "aff_rid"
//            };
// TODO:
//            for (String key : affridKeys) {
//                try {
//                    String affrid = (String) js.executeScript(
//                            "try { return window.localStorage.getItem('" + key + "'); } " +
//                                    "catch(e) { return null; }");
// TODO:
//                    if (isValidToken(affrid)) {
//                        return affrid;
//                    }
//                } catch (Exception e) {
//                    // Игнорируем ошибки для конкретного ключа
//                }
//            }
// TODO:
//        } catch (Exception e) {
//            System.err.println("Ошибка при доступе к localStorage: " + e.getMessage());
//        }
// TODO:
//        return null;
//    }
}
