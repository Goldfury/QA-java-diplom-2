package diplome.userAuthorization;

import diplome.Message;
import diplome.userDto.User;
import diplome.userDto.UserCreateResponse;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;

public class UserLoginTest {

    User user = new User("akyl@yandex.ru", "password");
    User userIncorrect = new User("akyl@yandex.ru", "password1");
    UserCreateResponse userCreateResponse;
    Message message;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    @DisplayName("Авторизация юзера")
    @Description("Отправляем запрос десериализируем и сравниваем ответ")
    public void logInUserTest() {
        Response response = sendPostRequestLogInUser();
        compareStatusCode(response, 200);
        userCreateResponse = response.body().as(UserCreateResponse.class);
        compareResponce();
    }

    @Step("Отправка пост запроса на авторизацию пользователя")
    public Response sendPostRequestLogInUser() {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post("/api/auth/login");
    }

    @Step("Compare status code")
    public void compareStatusCode(Response response, int code) {
        response.then().statusCode(code);
    }

    @Step("Compare response")
    public void compareResponce() {
        Assert.assertTrue(userCreateResponse.isSuccess());
        Assert.assertNotNull(userCreateResponse.getAccessToken());
        Assert.assertNotNull(userCreateResponse.getRefreshToken());
    }


    @Test
    @DisplayName("Авторизация юзера")
    @Description("Отправляем запрос десериализируем и сравниваем ответ")
    public void LogInWithIncorrectUserTest() {
        Response response = sendPostRequestLogInIncorrectUser();
        compareStatusCode(response, 401);
        message = response.body().as(Message.class);
        compareIncorrectResponce();

    }

    @Step("Отправка пост запроса на авторизацию некорректного пользователя")
    public Response sendPostRequestLogInIncorrectUser() {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(userIncorrect)
                .when()
                .post("/api/auth/login");
    }

    @Step("Compare response")
    public void compareIncorrectResponce() {
        Assert.assertFalse(message.isSuccess());
        Assert.assertEquals("email or password are incorrect", message.getMessage());
    }


}
