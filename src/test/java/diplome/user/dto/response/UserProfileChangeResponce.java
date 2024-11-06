package diplome.user.dto.response;

import diplome.user.dto.request.UserWithoutPass;
import lombok.Data;

@Data
public class UserProfileChangeResponce {
    private boolean success;
    private UserWithoutPass user;

}
