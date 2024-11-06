package diplome.user.create;

import com.github.javafaker.Faker;
import diplome.ApiUtils;
import diplome.BaseTest;
import diplome.user.dto.request.User;
import diplome.user.dto.response.UserCreateResponse;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;

public class UserCreateTest extends BaseTest {

    Faker faker = new Faker();
    User user = new User(faker.internet().emailAddress(), faker.internet().password(), faker.name().firstName());
    UserCreateResponse userCreateResponse;


    @Test
    @DisplayName("Создание пользователя")
    @Description("Проверка кода ответа и сообщения ответа при создании пользователя")
    public void createUserTest() {
        Response response = ApiUtils.sendPostRequest("/api/auth/register",user);
        ApiUtils.compareStatusCode(response, 200);
        userCreateResponse = response.body().as(UserCreateResponse.class);
    }

    @Test
    @DisplayName("Повторное создание пользователя")
    @Description("Проверка кода ответа при повторном создании пользователя")
    public void createUserSecondTimeTest() {
        Response response = ApiUtils.sendPostRequest("/api/auth/register",user);
        userCreateResponse = response.body().as(UserCreateResponse.class);
        Response secondResponse = ApiUtils.sendPostRequest("/api/auth/register",user);
        ApiUtils.compareStatusCode(secondResponse, 403);

    }

    @After
    @DisplayName("Удаление учетной записи")
    @Description("Сравнивается код ответа")
    public void tearDown() {
        ApiUtils.compareStatusCode(sendDeleteRequestUser(),202);
    }

    @Step("Запрос на удаления пользователя по его токену")
    public Response sendDeleteRequestUser() {
        return given()
                .header("Authorization", userCreateResponse.getAccessToken())
                .when()
                .delete("/api/auth/user");
    }

}
