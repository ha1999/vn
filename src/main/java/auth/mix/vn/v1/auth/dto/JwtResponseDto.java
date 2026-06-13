package auth.mix.vn.v1.auth.dto;

import auth.mix.vn.v1.auth.config.AuthConstants;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class JwtResponseDto {

    private String accessToken;
    private String refreshToken;
    private String tokenType = AuthConstants.BEARER_PREFIX.trim();
    private UUID userId;
    private String username;
    private String email;
    private List<String> permissions;

    public JwtResponseDto(String accessToken, String refreshToken, UUID userId,
                          String username, String email, List<String> permissions) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = AuthConstants.BEARER_PREFIX.trim();
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.permissions = permissions;
    }
}
