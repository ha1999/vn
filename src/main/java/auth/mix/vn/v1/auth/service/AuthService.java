package auth.mix.vn.v1.auth.service;
import auth.mix.vn.v1.auth.config.JwtTokenProvider;
import auth.mix.vn.v1.auth.dto.JwtResponseDto;
import auth.mix.vn.v1.auth.dto.LoginRequestDto;
import auth.mix.vn.v1.auth.dto.RefreshTokenRequestDto;
import auth.mix.vn.v1.auth.dto.RegisterRequestDto;

import auth.mix.vn.v1.permission.entity.Permission;
import auth.mix.vn.v1.role.entity.Role;
import auth.mix.vn.v1.role.config.RoleName;
import auth.mix.vn.v1.role.repository.RoleRepository;
import auth.mix.vn.v1.user.entity.User;
import auth.mix.vn.v1.user.repository.UserRepository;
import auth.mix.vn.v1.user.dto.UserResponseDto;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public JwtResponseDto register(RegisterRequestDto request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already taken");
        }

        Role userRole = roleRepository.findByName(RoleName.USER.name())
                .orElseThrow(() -> new IllegalStateException("Default role USER not found"));

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setDisplayName(request.getDisplayName() != null ? request.getDisplayName() : request.getUsername());
        user.setEnabled(true);
        user.setRoles(Set.of(userRole));

        user = userRepository.save(user);

        return buildJwtResponse(user);
    }

    public JwtResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByUsername(request.getUsernameOrEmail())
                .or(() -> userRepository.findByEmail(request.getUsernameOrEmail()))
                .orElseThrow(() -> new BadCredentialsException("Invalid username/email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username/email or password");
        }

        if (!user.isEnabled()) {
            throw new BadCredentialsException("Account is disabled");
        }

        return buildJwtResponse(user);
    }

    public JwtResponseDto refresh(RefreshTokenRequestDto request) {
        try {
            if (!jwtTokenProvider.validateToken(request.getRefreshToken())) {
                throw new IllegalArgumentException("Invalid or expired refresh token");
            }

            UUID userId = jwtTokenProvider.getUserIdFromToken(request.getRefreshToken());
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            return buildJwtResponse(user);
        } catch (ExpiredJwtException e) {
            throw new IllegalArgumentException("Refresh token has expired");
        }
    }

    public UserResponseDto me(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return buildUserResponse(user);
    }

    private JwtResponseDto buildJwtResponse(User user) {
        List<String> permissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> permission.getName())
                .distinct()
                .collect(Collectors.toList());

        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getId(), user.getUsername(), permissions);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        return new JwtResponseDto(accessToken, refreshToken, user.getId(),
                user.getUsername(), user.getEmail(), permissions);
    }

    private UserResponseDto buildUserResponse(User user) {
        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        List<String> permissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> permission.getName())
                .distinct()
                .collect(Collectors.toList());

        return new UserResponseDto(user.getId(), user.getUsername(), user.getEmail(),
                user.getDisplayName(), user.isEnabled(), roles, permissions);
    }
}
