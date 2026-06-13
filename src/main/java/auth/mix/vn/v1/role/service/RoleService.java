package auth.mix.vn.v1.role.service;
import auth.mix.vn.v1.role.entity.*;
import auth.mix.vn.v1.role.dto.*;
import auth.mix.vn.v1.role.repository.*;

import auth.mix.vn.v1.permission.entity.Permission;
import auth.mix.vn.v1.permission.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Transactional(readOnly = true)
    public RolePageResponseDto listRoles(int page, int limit) {
        Page<Role> rolePage = roleRepository.findAll(PageRequest.of(page, limit));
        List<RoleResponseDto> roles = rolePage.getContent().stream()
                .map(this::toRoleResponse)
                .collect(Collectors.toList());
        return new RolePageResponseDto(roles, rolePage.getTotalElements(), page, limit);
    }

    @Transactional(readOnly = true)
    public RoleResponseDto getRole(UUID roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));
        return toRoleResponse(role);
    }

    @Transactional
    public RoleResponseDto createRole(CreateRoleRequestDto request) {
        if (roleRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalArgumentException("Role name already exists");
        }

        Set<Permission> permissions = resolvePermissions(request.getPermissionIds());

        Role role = new Role();
        role.setId(UUID.randomUUID());
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        role.setEnabled(true);
        role.setPermissions(permissions);

        role = roleRepository.save(role);
        return toRoleResponse(role);
    }

    @Transactional
    public RoleResponseDto updateRole(UUID roleId, UpdateRoleRequestDto request) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        if (request.getName() != null) {
            roleRepository.findByName(request.getName())
                    .filter(existing -> !existing.getId().equals(roleId))
                    .ifPresent(existing -> {
                        throw new IllegalArgumentException("Role name already exists");
                    });
            role.setName(request.getName());
        }
        if (request.getDescription() != null) {
            role.setDescription(request.getDescription());
        }
        if (request.getPermissionIds() != null) {
            role.setPermissions(resolvePermissions(request.getPermissionIds()));
        }

        role = roleRepository.save(role);
        return toRoleResponse(role);
    }

    @Transactional
    public RoleResponseDto toggleRoleStatus(UUID roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));
        role.setEnabled(!role.isEnabled());
        role = roleRepository.save(role);
        return toRoleResponse(role);
    }

    private Set<Permission> resolvePermissions(Set<UUID> permissionIds) {
        if (permissionIds == null) {
            return new HashSet<>();
        }
        Set<Permission> permissions = new HashSet<>();
        for (UUID permId : permissionIds) {
            Permission permission = permissionRepository.findById(permId)
                    .orElseThrow(() -> new IllegalArgumentException("Permission not found: " + permId));
            permissions.add(permission);
        }
        return permissions;
    }

    private RoleResponseDto toRoleResponse(Role role) {
        List<String> permissionNames = role.getPermissions().stream()
                .map(Permission::getName)
                .collect(Collectors.toList());

        return new RoleResponseDto(role.getId(), role.getName(),
                role.getDescription(), role.isEnabled(), permissionNames);
    }
}
