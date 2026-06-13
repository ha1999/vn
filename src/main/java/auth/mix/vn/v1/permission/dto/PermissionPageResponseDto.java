package auth.mix.vn.v1.permission.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PermissionPageResponseDto {

    private List<PermissionResponseDto> permissions;
    private long total;
    private int page;
    private int limit;
}
