package auth.mix.vn.v1.role.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class RoleResponseDto {

    private UUID id;
    private String name;
    private String description;
    private boolean enabled;
    private List<String> permissions;
}
