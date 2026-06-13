package auth.mix.vn.v1.permission.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePermissionRequestDto {

    @Size(max = 100)
    private String name;
}
