package auth.mix.vn.v1.permission.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePermissionRequestDto {

    @NotBlank
    @Size(max = 100)
    private String name;
}
