package auth.mix.vn.v1.role.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class RolePageResponseDto {

    private List<RoleResponseDto> roles;
    private long total;
    private int page;
    private int limit;
}
