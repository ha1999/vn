package auth.mix.vn.v1.role.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class CreateRoleRequestDto {

    @NotBlank
    @Size(max = 50)
    private String name;

    @Size(max = 255)
    private String description;

    private Set<UUID> permissionIds;
}
