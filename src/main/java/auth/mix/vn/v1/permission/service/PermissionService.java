package auth.mix.vn.v1.permission.service;
import auth.mix.vn.v1.permission.entity.*;
import auth.mix.vn.v1.permission.dto.*;
import auth.mix.vn.v1.permission.repository.*;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;

    @Transactional(readOnly = true)
    public PermissionPageResponseDto listPermissions(int page, int limit) {
        Page<Permission> permPage = permissionRepository.findAll(PageRequest.of(page, limit));
        List<PermissionResponseDto> permissions = permPage.getContent().stream()
                .map(this::toPermissionResponse)
                .collect(Collectors.toList());
        return new PermissionPageResponseDto(permissions, permPage.getTotalElements(), page, limit);
    }

    @Transactional(readOnly = true)
    public PermissionResponseDto getPermission(UUID permId) {
        Permission permission = permissionRepository.findById(permId)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found"));
        return toPermissionResponse(permission);
    }

    @Transactional
    public PermissionResponseDto createPermission(CreatePermissionRequestDto request) {
        if (permissionRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalArgumentException("Permission name already exists");
        }

        Permission permission = new Permission();
        permission.setId(UUID.randomUUID());
        permission.setName(request.getName());
        permission.setEnabled(true);

        permission = permissionRepository.save(permission);
        return toPermissionResponse(permission);
    }

    @Transactional
    public PermissionResponseDto updatePermission(UUID permId, UpdatePermissionRequestDto request) {
        Permission permission = permissionRepository.findById(permId)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found"));

        if (request.getName() != null) {
            permissionRepository.findByName(request.getName())
                    .filter(existing -> !existing.getId().equals(permId))
                    .ifPresent(existing -> {
                        throw new IllegalArgumentException("Permission name already exists");
                    });
            permission.setName(request.getName());
        }

        permission = permissionRepository.save(permission);
        return toPermissionResponse(permission);
    }

    @Transactional
    public PermissionResponseDto togglePermissionStatus(UUID permId) {
        Permission permission = permissionRepository.findById(permId)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found"));
        permission.setEnabled(!permission.isEnabled());
        permission = permissionRepository.save(permission);
        return toPermissionResponse(permission);
    }

    private PermissionResponseDto toPermissionResponse(Permission permission) {
        return new PermissionResponseDto(permission.getId(), permission.getName(), permission.isEnabled());
    }
}
