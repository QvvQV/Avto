package ru.tutor.page.Api;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;
import net.datafaker.Faker;
import ru.tutor.base.BaseApi;

import java.util.Locale;

import static io.restassured.RestAssured.given;

public class SendEmailApi extends BaseApi {
    private final Faker faker;

    public SendEmailApi(String baseUrl){
        super(baseUrl);
        this.faker = new Faker(Locale.ENGLISH);
    }

    public Response sendEmail(String name, String email, String content){
        String payload = String.format(
                "{ \"name\": \"%s\", \"email\": \"%s\", \"content\": \"%s\" }",
                name, email, content
        );

        return given()
                .filter(new AllureRestAssured())
                .header("Content-Type", "application/json")
                .body(payload)
                .when()
                .post(baseUrl + "/send-email")
                .then()
                .extract()
                .response();
    }

    // Генерация случайных данных
    public Response sendRandomEmail(){
        String name = faker.name().firstName();
        String email = faker.internet().emailAddress();
        String content = faker.lorem().sentence(10);

        return sendEmail(name, email, content);
    }

    public Response sendEmailWithInvalidEmail(){
        String name = faker.name().firstName();
        String invalidEmail = "уз5052_2@test.ru"; // кривой email
        String content = faker.lorem().sentence();

        return sendEmail(name, invalidEmail, content);
    }

    public Response sendEmailWithEmptyName(){
        String EmptyName = "";
        String email = faker.internet().emailAddress();
        String content = faker.lorem().sentence(10);

        return sendEmail(EmptyName, email, content);
    }

    public Response sendEmailWithEmptyEmail(){
        String name = faker.name().firstName();
        String EmptyEmail = "";
        String content = faker.lorem().sentence(10);

        return sendEmail(name, EmptyEmail, content);
    }

    public Response sendEmailWithEmptyContent(){
        String name = faker.name().firstName();
        String email = faker.internet().emailAddress();
        String EmptyContent = "";

        return sendEmail(name, email, EmptyContent);
    }

    public Response sendEmailWithEmptyBody(){
        String EmptyName = "";
        String EmptyEmail = "";
        String EmptyContent = "";

        return sendEmail(EmptyName, EmptyEmail, EmptyContent);
    }
}
