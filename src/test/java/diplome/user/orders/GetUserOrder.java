package diplome.user.orders;

import diplome.ApiUtils;
import diplome.BaseTest;
import diplome.Message;
import diplome.user.dto.request.User;
import diplome.user.dto.response.UserCreateResponse;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class GetUserOrder extends BaseTest {

    UserCreateResponse userCreateResponse;
    User user = new User("akyl@yandex.ru", "password");
    Message message;


    @Test
    @DisplayName("Тест на отображения заказов с проверкой ответа и статус кода")
    public void getUserOrdersTest() {
        Response response = sendGetRequestShowOrder();
        compareStatusAndResponseOfSuccessRequest(response, 200);
    }

    @Step("Отправка запроса на отображение заказов с токеном юзера")
    public Response sendGetRequestShowOrder() {

        return given()
                .header("Authorization", sendPostRequestGetUserToken(user))
                .when()
                .get("/api/orders");
    }

    @Step("Отправка пост запроса на получение токена пользователя через авторизацию")
    public String sendPostRequestGetUserToken(User user) {
        Response response = ApiUtils.sendPostRequest("/api/auth/login",user);
        userCreateResponse = response.body().as(UserCreateResponse.class);

        return userCreateResponse.getAccessToken();
    }

    @Step
    public void compareStatusAndResponseOfSuccessRequest(Response response, int code) {
        response.then().statusCode(200);
        response.then().assertThat().body("success", equalTo(true));
        response.then().assertThat().body("orders", notNullValue());
        response.then().assertThat().body("total", notNullValue());
        response.then().assertThat().body("totalToday", notNullValue());
    }

    @Test
    @DisplayName("Тест запроса отображения заказов без токена")
    @Description("Запрос вызывается и сравнивается ответ")
    public void getUserOrdersWithoutTokenTest() {
        Response response = ApiUtils.sendGetRequest("/api/orders");
        message = response.body().as(Message.class);
        compareStatus(response, message);
    }

    @Step("Проверка статус запроса без токена")
    public void compareStatus(Response response, Message message) {
        ApiUtils.compareStatusCode(response, 401);
        Assert.assertFalse(message.isSuccess());
        Assert.assertEquals("You should be authorised", message.getMessage());
    }

}
