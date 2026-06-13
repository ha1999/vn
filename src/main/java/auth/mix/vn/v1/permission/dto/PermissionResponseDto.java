package auth.mix.vn.v1.permission.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class PermissionResponseDto {

    private UUID id;
    private String name;
    private boolean enabled;
}
