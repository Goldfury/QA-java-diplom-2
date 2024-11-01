package diplome.orderCreate;

import diplome.Message;
import diplome.ingridientsDto.IngredientsRequest;
import diplome.ingridientsDto.IngridientsResponse;
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

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class OrderCreateTest {

    UserCreateResponse userCreateResponse;
    User user = new User("akyl@yandex.ru", "password");
    IngridientsResponse ingridientsResponse;
    IngredientsRequest ingredientsRequest;
    Message message;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }


    @Test
    @DisplayName("")
    @Description("")
    public void createOrderWithToken() {
        Response response = sendPostRequestCreateOrder();
        compareStatusCode(response, 200);
        comparecreateOrderWithTokenResponse(response);
    }

    @Step("Проверка ответа запроса токеном авторизации проверяет на некоторые поля ответа")
    public void comparecreateOrderWithTokenResponse(Response response) {
        response.then().assertThat().body("success", equalTo(true));
        response.then().assertThat().body("name", notNullValue());
        response.then().assertThat().body("order.ingredients", notNullValue());
        response.then().assertThat().body("order.owner.name", equalTo("akylbek"));
        response.then().assertThat().body("order.owner.email", equalTo("akyl@yandex.ru"));
        response.then().assertThat().body("order.status", equalTo("done"));

    }


    @Step("Отправка запроса на создание бургера с токеном юзера")
    public Response sendPostRequestCreateOrder() {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", sendPostRequestGetUserToken(user))
                .body(addIngridients())
                .when()
                .post("/api/orders");
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


    @Test
    @DisplayName("Заказ бургера без авторизации")
    @Description("Добавили все ингридиенты которые были")
    public void createOrderWithoutToken() {
        Response response = sendPostRequestCreateOrderWithoutToken();
        compareStatusCode(response, 200);
        comparecreateOrderWithoutTokenResponse(response);
    }

    @Step("Проверка ответа запроса токена без авторизации")
    public void comparecreateOrderWithoutTokenResponse(Response response) {
        response.then().assertThat().body("success", equalTo(true));
        response.then().assertThat().body("name", notNullValue());
        response.then().assertThat().body("order.number", notNullValue());
    }


    @Step("Отправка запроса на создание бургера без токена ")
    public Response sendPostRequestCreateOrderWithoutToken() {
        return given()
                .header("Content-type", "application/json")
                .body(addIngridients())
                .when()
                .post("/api/orders");
    }


    @Step("Метод который выдает тело запроса для создания заказа, вытаскиваем все" +
            " данные из списка продуктов и добавляем в один мощный бургер. Мог просто вшить данные но " +
            "я решил не хардкодить данные если они изменятся")
    public IngredientsRequest addIngridients() {
        IngredientsRequest ingredientsRequest = new IngredientsRequest();
        Response response = sendGetRequestListIngridients();
        List<String> all = new ArrayList<>();
        ingridientsResponse = response.body().as(IngridientsResponse.class);

        for (int i = 0; i < ingridientsResponse.getData().size(); i++) {
            all.add(ingridientsResponse.getData().get(i).getId());
        }
        ingredientsRequest.setIngredients(all);

        return ingredientsRequest;
    }


    @Test
    @DisplayName("Проверка запроса отображения списка ингридиетов")
    public void getIngridientsList() {
        Response response = sendGetRequestListIngridients();
        compareStatusCode(response, 200);
        ingridientsResponse = response.body().as(IngridientsResponse.class);
        compareResponse();
    }


    @Step("Send get request для получения списка ингридиентов")
    public Response sendGetRequestListIngridients() {
        return given()
                .when()
                .get("/api/ingredients");
    }

    @Step("Compare status code")
    public void compareStatusCode(Response response, int code) {
        response.then().statusCode(code);
    }

    @Step("Compare response")
    public void compareResponse() {
        Assert.assertTrue(ingridientsResponse.isSuccess());
        Assert.assertNotNull(ingridientsResponse.getData());
    }

    @Test
    @DisplayName("Создание пустого заказа")
    @Description("Тело запроса пустое")
    public void createEmptyOrderTest() {
        Response response = sendPostRequestCreateEmptyOrder();
        compareStatusCode(response, 400);
        message = response.body().as(Message.class);
        compareCreateEmptyOrderResponse(message);
    }


    @Step("Отправка запроса на пустого бургера с токеном юзера")
    public Response sendPostRequestCreateEmptyOrder() {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", sendPostRequestGetUserToken(user))
                .body("{ }")
                .when()
                .post("/api/orders");
    }

    @Step("Compare response")
    public void compareCreateEmptyOrderResponse(Message message) {
        Assert.assertFalse(message.isSuccess());
        Assert.assertEquals("Ingredient ids must be provided", message.getMessage());
    }


    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    @Description("Также проверка по ответам запроса")
    public void createIncorrectOrderTest() {
        Response response = sendPostRequestCreateIncorrectOrder();
        compareStatusCode(response, 400);
        message = response.body().as(Message.class);
        compareCreateIncorrectOrderResponse(message);
    }


    @Step("Отправка запроса c некорректными ингридиентами без токена. Да я вшил тело запроса внутрь так как лень было " +
            "создавать POJO класс для одного запросв")
    public Response sendPostRequestCreateIncorrectOrder() {
        return given()
                .header("Content-type", "application/json")
                .body("{\n" +
                        "  \"ingredients\": [\n" +
                        "    \"61c0c5a71d1f82002bdaaa6d\",\n" +
                        "    \"61c0c5a71d1f82004bdaaa6c\",\n" +
                        "    \"61c0c5a71d1f82005bdaaa74\"\n" +
                        "  ]\n" +
                        "}")
                .when()
                .post("/api/orders");
    }


    @Step("Compare response")
    public void compareCreateIncorrectOrderResponse(Message message) {
        Assert.assertFalse(message.isSuccess());
        Assert.assertEquals("One or more ids provided are incorrect", message.getMessage());
    }
}
