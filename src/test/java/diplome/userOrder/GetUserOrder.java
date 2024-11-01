package diplome.userOrder;

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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class GetUserOrder {

    UserCreateResponse userCreateResponse;
    User user = new User("akyl@yandex.ru", "password");
    Message message;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }


    @Test
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
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post("/api/auth/login");

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
        Response response = sendGetRequestShowOrderUnknownUser();
        message = response.body().as(Message.class);
        compareStatus(response, 401, message);
    }

    @Step("Отправка запроса на отображение заказов с без токена юзера")
    public Response sendGetRequestShowOrderUnknownUser() {
        return given()
                .when()
                .get("/api/orders");
    }

    @Step("Проверка статус запроса без токена")
    public void compareStatus(Response response, int code, Message message){
        response.then().assertThat().statusCode(code);
        Assert.assertFalse(message.isSuccess());
        Assert.assertEquals("You should be authorised", message.getMessage());
    }

}
