package diplome.userDto;

import lombok.Data;

@Data
public class UserProfileChangeResponce {
    private boolean success;
    private UserWithoutPass user;

}
