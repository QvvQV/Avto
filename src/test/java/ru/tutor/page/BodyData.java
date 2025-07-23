package ru.tutor.page;

import java.io.Serializable;

public class BodyData {

    String login;
    String password;

    public BodyData(String login, String password) {
        this.login = login;
        this.password = password;
    }

//    BodyData bodyData = new BodyData("tp5052_2@test.ru", "12345678");
}
