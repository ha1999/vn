package auth.mix.vn.v1.user.service;
import auth.mix.vn.v1.user.entity.*;
import auth.mix.vn.v1.user.dto.*;
import auth.mix.vn.v1.user.repository.*;

import auth.mix.vn.v1.permission.entity.Permission;
import auth.mix.vn.v1.role.entity.Role;
import auth.mix.vn.v1.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserPageResponseDto listUsers(int page, int limit) {
        Page<User> userPage = userRepository.findAll(PageRequest.of(page, limit));
        List<UserResponseDto> users = userPage.getContent().stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
        return new UserPageResponseDto(users, userPage.getTotalElements(), page, limit);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserDetails(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return toUserResponse(user);
    }

    @Transactional
    public UserResponseDto createUser(CreateUserRequestDto request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already taken");
        }

        Set<Role> roles = resolveRoles(request.getRoleIds());

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setDisplayName(request.getDisplayName() != null ? request.getDisplayName() : request.getUsername());
        user.setEnabled(true);
        user.setRoles(roles);

        user = userRepository.save(user);
        return toUserResponse(user);
    }

    @Transactional
    public UserResponseDto updateUser(UUID userId, UpdateUserRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (request.getEmail() != null) {
            userRepository.findByEmail(request.getEmail())
                    .filter(existing -> !existing.getId().equals(userId))
                    .ifPresent(existing -> {
                        throw new IllegalArgumentException("Email already taken");
                    });
            user.setEmail(request.getEmail());
        }
        if (request.getDisplayName() != null) {
            user.setDisplayName(request.getDisplayName());
        }
        if (request.getRoleIds() != null) {
            user.setRoles(resolveRoles(request.getRoleIds()));
        }

        user = userRepository.save(user);
        return toUserResponse(user);
    }

    @Transactional
    public UserResponseDto toggleUserStatus(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setEnabled(!user.isEnabled());
        user = userRepository.save(user);
        return toUserResponse(user);
    }

    @Transactional
    public void deleteUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found");
        }
        userRepository.deleteById(userId);
    }

    private Set<Role> resolveRoles(Set<UUID> roleIds) {
        Set<Role> roles = new HashSet<>();
        for (UUID roleId : roleIds) {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleId));
            roles.add(role);
        }
        return roles;
    }

    private UserResponseDto toUserResponse(User user) {
        List<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        List<String> permissionNames = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .distinct()
                .collect(Collectors.toList());

        return new UserResponseDto(user.getId(), user.getUsername(), user.getEmail(),
                user.getDisplayName(), user.isEnabled(), roleNames, permissionNames);
    }
}
