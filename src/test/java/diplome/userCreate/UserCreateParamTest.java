package diplome.userCreate;

import diplome.Message;
import diplome.userDto.User;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static io.restassured.RestAssured.given;

@RunWith(Parameterized.class)
public class UserCreateParamTest {

    Message message;
    private String email;
    private String password;
    private String name;

    public UserCreateParamTest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    @Parameterized.Parameters
    public static Object[][] users() {
        return new Object[][]{
                {"", "test", "test"},
                {"test5@mail.ru", "", "test"},
                {"test5@mail.ru", "test", ""},
        };
    }


    @Before
    public void setUp(){
        RestAssured.baseURI="https://stellarburgers.nomoreparties.site/";
    }

    @Test
    @DisplayName("Создание пользователя с различными некорректными данными")
    @Description("Проверка кода ответа при создании пользователя с некорректными данными")
    public void createUserWithInvalidDataTest() {
        Response response = sendPostRequestCreateUser();
        compareStatusCode(response, 403);
        message = response.body().as(Message.class);
        compareResponse(message);
    }

    @Step("Отправка пост запроса на создание пользователя")
    public Response sendPostRequestCreateUser() {
        // Создаем объект User для передачи в тело запроса
        User user = new User(email, password, name);

        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post("/api/auth/register");
    }

    @Step("Compare status code")
    public void compareStatusCode(Response response, int code) {
        response.then().statusCode(code);
    }

    @Step("Compare response")
    public void compareResponse(Message message){
        Assert.assertEquals("Email, password and name are required fields", message.getMessage());
        Assert.assertFalse(message.isSuccess());
    }

}
