package diplome.userCreate;

import com.github.javafaker.Faker;
import diplome.userDto.User;
import diplome.userDto.UserCreateResponse;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;

public class UserCreateTest {

    Faker faker = new Faker();
    User user = new User(faker.internet().emailAddress(), faker.internet().password(), faker.name().firstName());
    UserCreateResponse userCreateResponse;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    @DisplayName("Создание пользователя")
    @Description("Проверка кода ответа и сообщения ответа при создании пользователя")
    public void createUserTest() {
        Response response = sendPostRequestCreateUser();
        compareStatusCode(response, 200);
        userCreateResponse = response.body().as(UserCreateResponse.class);
    }

    @Test
    @DisplayName("Повторное создание пользователя")
    @Description("Проверка кода ответа при повторном создании пользователя")
    public void createUserSecondTimeTest() {
        Response response = sendPostRequestCreateUser();
        userCreateResponse = response.body().as(UserCreateResponse.class);
        Response secondResponse = sendPostRequestCreateUser();
        compareStatusCode(secondResponse, 403);

    }

    @Step("Отправка пост запроса на создание пользователя")
    public Response sendPostRequestCreateUser() {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post("/api/auth/register");
    }


    @Step("Compare status code")
    public void compareStatusCode(Response response, int code) {
        response.then().statusCode(code);
    }

    @After
    @DisplayName("Удаление учетной записи")
    @Description("Сравнивается код ответа")
    public void tearDown() {
        compareStatusCode(sendDeleteRequestUser(), 202);
    }

    @Step("Запрос на удаления пользователя по его токену")
    public Response sendDeleteRequestUser() {
        return given()
                .header("Authorization", userCreateResponse.getAccessToken())
                .when()
                .delete("/api/auth/user");
    }

}
