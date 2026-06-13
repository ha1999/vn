package auth.mix.vn.v1.permission.controller;
import auth.mix.vn.v1.permission.dto.*;
import auth.mix.vn.v1.permission.service.PermissionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    public ResponseEntity<PermissionPageResponseDto> listPermissions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        PermissionPageResponseDto response = permissionService.listPermissions(page, limit);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{permId}")
    public ResponseEntity<PermissionResponseDto> getPermission(@PathVariable UUID permId) {
        PermissionResponseDto response = permissionService.getPermission(permId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<PermissionResponseDto> createPermission(@Valid @RequestBody CreatePermissionRequestDto request) {
        PermissionResponseDto response = permissionService.createPermission(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{permId}")
    public ResponseEntity<PermissionResponseDto> updatePermission(
            @PathVariable UUID permId,
            @Valid @RequestBody UpdatePermissionRequestDto request) {
        PermissionResponseDto response = permissionService.updatePermission(permId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{permId}/status")
    public ResponseEntity<PermissionResponseDto> togglePermissionStatus(@PathVariable UUID permId) {
        PermissionResponseDto response = permissionService.togglePermissionStatus(permId);
        return ResponseEntity.ok(response);
    }
}
