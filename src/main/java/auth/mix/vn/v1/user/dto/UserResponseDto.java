package auth.mix.vn.v1.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UserResponseDto {

    private UUID id;
    private String username;
    private String email;
    private String displayName;
    private boolean enabled;
    private List<String> roles;
    private List<String> permissions;
}
