package auth.mix.vn.v1.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class UpdateUserRequestDto {

    @Size(max = 100)
    @Email
    private String email;

    @Size(max = 100)
    private String displayName;

    private Set<UUID> roleIds;
}
