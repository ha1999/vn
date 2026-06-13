package auth.mix.vn.v1.role.controller;
import auth.mix.vn.v1.role.dto.*;
import auth.mix.vn.v1.role.service.RoleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<RolePageResponseDto> listRoles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        RolePageResponseDto response = roleService.listRoles(page, limit);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<RoleResponseDto> getRole(@PathVariable UUID roleId) {
        RoleResponseDto response = roleService.getRole(roleId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<RoleResponseDto> createRole(@Valid @RequestBody CreateRoleRequestDto request) {
        RoleResponseDto response = roleService.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{roleId}")
    public ResponseEntity<RoleResponseDto> updateRole(
            @PathVariable UUID roleId,
            @Valid @RequestBody UpdateRoleRequestDto request) {
        RoleResponseDto response = roleService.updateRole(roleId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{roleId}/status")
    public ResponseEntity<RoleResponseDto> toggleRoleStatus(@PathVariable UUID roleId) {
        RoleResponseDto response = roleService.toggleRoleStatus(roleId);
        return ResponseEntity.ok(response);
    }
}
