package diplome.user.dto.response;

import diplome.user.dto.request.UserWithoutPass;
import lombok.Data;

import static io.restassured.RestAssured.given;

@Data
public class UserCreateResponse {
    private boolean success;
    private UserWithoutPass user;
    private String accessToken;
    private String refreshToken;




}
