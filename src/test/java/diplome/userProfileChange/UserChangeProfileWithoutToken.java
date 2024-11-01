package diplome.userProfileChange;

import diplome.Message;
import diplome.userDto.User;
import diplome.userDto.UserProfileChangeResponce;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;

public class UserChangeProfileWithoutToken {
    User userNewProfile = new User("akylnew@yandex.ru", "password123", "AkylbekNew");
    Message message;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }

    @Test
    @DisplayName("Изменение профиля пользователя без токена")
    @Description("Создается запрос без токена, проверяется на статус код")
    public void changeUserProfile() {
        Response response = sendPatchRequestChangeUserProfile();
        compareStatusCode(response, 401);
        message = response.body().as(Message.class);
        compareResponce(message);
    }

    @Step("Запрос на изменение данных профиля юзера без токена")
    public Response sendPatchRequestChangeUserProfile() {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(userNewProfile)
                .when()
                .patch("api/auth/user");
    }

    @Step("Compare status code")
    public void compareStatusCode(Response response, int code) {
        response.then().statusCode(code);
    }

    @Step("Compare response")
    public void compareResponce(Message message) {
        Assert.assertFalse(message.isSuccess());
        Assert.assertEquals("You should be authorised", message.getMessage());
    }
}
