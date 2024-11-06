package diplome;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class ApiUtils {
    @Step("Compare status code")
    public static void compareStatusCode(Response response, int code) {
        response
                .then()
                .statusCode(code);
    }

    @Step("Send GET request")
    public static Response sendGetRequest(String endpoint) {
        return RestAssured
                .given()
                .get(endpoint);
    }

    @Step("Send POST request")
    public static Response sendPostRequest(String endpoint, Object body) {
        return RestAssured.given()
                .header("Content-type", "application/json")
                .body(body)
                .post(endpoint);
    }

    @Step("Send Patch request")
    public static Response sendPatchRequest(String endpoint, Object body) {
        return RestAssured.given()
                .header("Content-type", "application/json")
                .body(body)
                .patch(endpoint);
    }
}
