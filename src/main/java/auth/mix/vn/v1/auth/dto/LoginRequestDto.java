package auth.mix.vn.v1.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {

    @NotBlank
    private String usernameOrEmail;

    @NotBlank
    private String password;
}
