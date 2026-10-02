package ru.tutor.utils;

public final class ModalLogger {

    private ModalLogger() {
    }

    public static void logModalType(String modalText, String email) {
        switch (modalText) {
            case "Тариф «Лайт»":
                AllureAttachments.attachText("Оплата продукта", "Лайт тариф, email: " + email);
                break;
            case "Тариф «Премиум»":
                AllureAttachments.attachText("Оплата продукта", "Премиум тариф, email: " + email);
                break;
            case "Тариф «Навсегда»":
                AllureAttachments.attachText("Оплата продукта", "Пожизненный доступ, email: " + email);
                break;
            default:
                AllureAttachments.attachText("Оплата продукта", "Неизвестный тариф, email: " + email);
        }
    }
}
