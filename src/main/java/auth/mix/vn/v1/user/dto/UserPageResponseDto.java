package auth.mix.vn.v1.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserPageResponseDto {

    private List<UserResponseDto> users;
    private long total;
    private int page;
    private int limit;
}
