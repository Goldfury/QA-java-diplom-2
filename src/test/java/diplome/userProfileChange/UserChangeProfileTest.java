package diplome.userProfileChange;

import diplome.userDto.User;
import diplome.userDto.UserCreateResponse;
import diplome.userDto.UserProfileChangeResponce;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;

public class UserChangeProfileTest {

    UserCreateResponse userCreateResponse;
    User user = new User("akyl@yandex.ru", "password");
    User userNewProfile = new User("akylnew@yandex.ru", "password123", "AkylbekNew");
    User getUserNewProfile = new User("akylnew@yandex.ru", "password123");
    User userOldProfile = new User("akyl@yandex.ru", "password", "akylbek");
    UserProfileChangeResponce userProfileChangeResponce;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }

    @Test
    @DisplayName("Изменение профиля пользователя")
    @Description("В ответе мы получаем измененный профиль, в теле запроса используются 3 новые данные," +
            " также сравниваются получаемый ответ при смене данных и ответ при получении данныз")
    public void changeUserProfile() {
        Response response = sendPatchRequestChangeUserProfile();
        compareStatusCode(response, 200);
        userProfileChangeResponce = response.body().as(UserProfileChangeResponce.class);
        compareResponce(sendGetRequestUserProfile(),userProfileChangeResponce);
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

    @Step("Запрос на изменение данных профиля юзера")
    public Response sendPatchRequestChangeUserProfile() {
        return given()
                .header("Authorization", sendPostRequestGetUserToken(user))
                .header("Content-type", "application/json")
                .and()
                .body(userNewProfile)
                .when()
                .patch("api/auth/user");
    }

    @Step("Запрос на получение данных пользователя")
    public UserProfileChangeResponce sendGetRequestUserProfile() {
        Response response = given()
                .header("Authorization", sendPostRequestGetUserToken(getUserNewProfile))
                .when()
                .get("/api/auth/user");

        return response.body().as(UserProfileChangeResponce.class);
    }

    @Step("Compare status code")
    public void compareStatusCode(Response response, int code) {
        response.then().statusCode(code);
    }


    @Step("Compare response")
    public void compareResponce(UserProfileChangeResponce response1, UserProfileChangeResponce response) {
        Assert.assertEquals(response1, response);
    }


    @After
    @DisplayName("Возвращение данных обратно")
    @Description("Вызывается заново метод смены данных но только на старые данные")
    public void changeUserProfileBack() {
        Response response = sendPatchRequestChangeUserProfileBack();
        compareStatusCode(response, 200);
    }

    @Step("Запрос на изменение данных профиля юзера")
    public Response sendPatchRequestChangeUserProfileBack() {
        return given()
                .header("Authorization", sendPostRequestGetUserToken(getUserNewProfile))
                .header("Content-type", "application/json")
                .and()
                .body(userOldProfile)
                .when()
                .patch("api/auth/user");
    }

}
