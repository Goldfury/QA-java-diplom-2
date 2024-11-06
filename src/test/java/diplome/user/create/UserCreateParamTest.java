package diplome.user.create;

import diplome.ApiUtils;
import diplome.BaseTest;
import diplome.Message;
import diplome.user.dto.request.User;
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
public class UserCreateParamTest extends BaseTest {

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


    @Test
    @DisplayName("Создание пользователя с различными некорректными данными")
    @Description("Проверка кода ответа при создании пользователя с некорректными данными")
    public void createUserWithInvalidDataTest() {
        User user = new User(email, password, name);
        Response response = ApiUtils.sendPostRequest("/api/auth/register", user);
        ApiUtils.compareStatusCode(response, 403);
        message = response.body().as(Message.class);
        compareResponse(message);
    }


    @Step("Compare response")
    public void compareResponse(Message message) {
        Assert.assertEquals("Email, password and name are required fields", message.getMessage());
        Assert.assertFalse(message.isSuccess());
    }

}
