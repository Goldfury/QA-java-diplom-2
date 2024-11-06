package diplome.user.profile;

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

import static io.restassured.RestAssured.given;

public class UserChangeProfileWithoutToken extends BaseTest {
    User userNewProfile = new User("akylnew@yandex.ru", "password123", "AkylbekNew");
    Message message;

    @Test
    @DisplayName("Изменение профиля пользователя без токена")
    @Description("Создается запрос без токена, проверяется на статус код")
    public void changeUserProfile() {
        Response response = ApiUtils.sendPatchRequest("api/auth/user", userNewProfile);
        ApiUtils.compareStatusCode(response, 401);
        message = response.body().as(Message.class);
        compareResponce(message);
    }

    @Step("Compare response")
    public void compareResponce(Message message) {
        Assert.assertFalse(message.isSuccess());
        Assert.assertEquals("You should be authorised", message.getMessage());
    }
}
