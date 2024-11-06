package diplome.user.auth;

import diplome.ApiUtils;
import diplome.BaseTest;
import diplome.Message;
import diplome.user.dto.request.User;
import diplome.user.dto.response.UserCreateResponse;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;

import static io.restassured.RestAssured.given;

public class UserLoginTest extends BaseTest {

    User user = new User("akyl@yandex.ru", "password");
    User userIncorrect = new User("akyl@yandex.ru", "password1");
    UserCreateResponse userCreateResponse;
    Message message;


    @Test
    @DisplayName("Авторизация юзера")
    @Description("Отправляем запрос десериализируем и сравниваем ответ")
    public void logInUserTest() {
        Response response = ApiUtils.sendPostRequest("/api/auth/login", user);
        ApiUtils.compareStatusCode(response, 200);
        userCreateResponse = response.body().as(UserCreateResponse.class);
        compareResponce();
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
        Response response = ApiUtils.sendPostRequest("/api/auth/login", userIncorrect);
        ApiUtils.compareStatusCode(response, 401);
        message = response.body().as(Message.class);
        compareIncorrectResponce();

    }

    @Step("Compare response")
    public void compareIncorrectResponce() {
        Assert.assertFalse(message.isSuccess());
        Assert.assertEquals("email or password are incorrect", message.getMessage());
    }


}
