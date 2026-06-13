package auth.mix.vn.seed;

import auth.mix.vn.authorization.AuthorizationConstants;
import auth.mix.vn.authorization.EndpointPermission;
import auth.mix.vn.authorization.EndpointPermissionRepository;
import auth.mix.vn.authorization.PathMatcherService;
import auth.mix.vn.v1.permission.entity.Permission;
import auth.mix.vn.v1.permission.config.PermissionName;
import auth.mix.vn.v1.permission.repository.PermissionRepository;
import auth.mix.vn.v1.role.entity.Role;
import auth.mix.vn.v1.role.config.RoleName;
import auth.mix.vn.v1.role.repository.RoleRepository;
import auth.mix.vn.v1.user.entity.User;
import auth.mix.vn.v1.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final EndpointPermissionRepository endpointPermissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final PathMatcherService pathMatcherService;

    @Override
    @Transactional
    public void run(String... args) {
        if (permissionRepository.count() > 0) {
            log.info("Data already seeded, skipping");
            return;
        }

        log.info("Seeding initial data...");

        Permission userReadPermission = createPermission(PermissionName.USER_READ);
        Permission userCreatePermission = createPermission(PermissionName.USER_CREATE);
        Permission userWritePermission = createPermission(PermissionName.USER_WRITE);
        Permission userDeletePermission = createPermission(PermissionName.USER_DELETE);
        Permission roleReadPermission = createPermission(PermissionName.ROLE_READ);
        Permission roleCreatePermission = createPermission(PermissionName.ROLE_CREATE);
        Permission roleWritePermission = createPermission(PermissionName.ROLE_WRITE);
        Permission roleDeletePermission = createPermission(PermissionName.ROLE_DELETE);
        Permission adminAccessPermission = createPermission(PermissionName.ADMIN_ACCESS);
        Permission endpointPermissionRead = createPermission(PermissionName.ENDPOINT_PERMISSION_READ);
        Permission endpointPermissionWrite = createPermission(PermissionName.ENDPOINT_PERMISSION_WRITE);
        Permission userDisablePermission = createPermission(PermissionName.USER_DISABLE);
        Permission roleDisablePermission = createPermission(PermissionName.ROLE_DISABLE);
        Permission permissionReadPermission = createPermission(PermissionName.PERMISSION_READ);
        Permission permissionCreatePermission = createPermission(PermissionName.PERMISSION_CREATE);
        Permission permissionWritePermission = createPermission(PermissionName.PERMISSION_WRITE);
        Permission permissionDisablePermission = createPermission(PermissionName.PERMISSION_DISABLE);

        Role adminRole = createRole(RoleName.ADMIN.name(), "Full system access",
                userReadPermission, userCreatePermission, userWritePermission, userDeletePermission,
                roleReadPermission, roleCreatePermission, roleWritePermission, roleDeletePermission,
                adminAccessPermission, endpointPermissionRead, endpointPermissionWrite,
                userDisablePermission, roleDisablePermission,
                permissionReadPermission, permissionCreatePermission, permissionWritePermission,
                permissionDisablePermission);

        createRole(RoleName.MODERATOR.name(), "Can manage users and view roles",
                userReadPermission, userCreatePermission, userWritePermission,
                roleReadPermission);

        createRole(RoleName.USER.name(), "Basic user with read access",
                userReadPermission);

        User adminUser = new User();
        adminUser.setId(UUID.randomUUID());
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@vn.local");
        adminUser.setPassword(passwordEncoder.encode("admin123"));
        adminUser.setDisplayName("Administrator");
        adminUser.setEnabled(true);
        adminUser.setRoles(Set.of(adminRole));
        userRepository.save(adminUser);

        log.info("Created admin user: admin / admin123");

        createEndpointRule(AuthorizationConstants.METHOD_ANY, "/api/auth/**", null, true);

        createEndpointRule(AuthorizationConstants.METHOD_ANY, "/swagger-ui.html", null, true);
        createEndpointRule(AuthorizationConstants.METHOD_ANY, "/swagger-ui/**", null, true);
        createEndpointRule(AuthorizationConstants.METHOD_ANY, "/v3/api-docs/**", null, true);
        createEndpointRule(AuthorizationConstants.METHOD_ANY, "/api-docs/**", null, true);

        createEndpointRule("GET", "/api/users", PermissionName.USER_READ, true);
        createEndpointRule("GET", "/api/users/{id}", PermissionName.USER_READ, true);
        createEndpointRule("POST", "/api/users", PermissionName.USER_CREATE, true);
        createEndpointRule("PUT", "/api/users/{id}", PermissionName.USER_WRITE, true);
        createEndpointRule("DELETE", "/api/users/{id}", PermissionName.USER_DELETE, true);
        createEndpointRule("PATCH", "/api/users/{id}/status", PermissionName.USER_DISABLE, true);

        createEndpointRule("GET", "/api/roles", PermissionName.ROLE_READ, true);
        createEndpointRule("POST", "/api/roles", PermissionName.ROLE_CREATE, true);
        createEndpointRule("PUT", "/api/roles/{id}", PermissionName.ROLE_WRITE, true);
        createEndpointRule("DELETE", "/api/roles/{id}", PermissionName.ROLE_DELETE, true);
        createEndpointRule("PATCH", "/api/roles/{id}/status", PermissionName.ROLE_DISABLE, true);

        createEndpointRule("GET", "/api/permissions", PermissionName.PERMISSION_READ, true);
        createEndpointRule("GET", "/api/permissions/{permId}", PermissionName.PERMISSION_READ, true);
        createEndpointRule("POST", "/api/permissions", PermissionName.PERMISSION_CREATE, true);
        createEndpointRule("PUT", "/api/permissions/{permId}", PermissionName.PERMISSION_WRITE, true);
        createEndpointRule("PATCH", "/api/permissions/{permId}/status", PermissionName.PERMISSION_DISABLE, true);

        createEndpointRule(AuthorizationConstants.METHOD_ANY, "/api/admin/**", PermissionName.ADMIN_ACCESS, true);

        createEndpointRule("GET", "/api/endpoint-permissions", PermissionName.ENDPOINT_PERMISSION_READ, true);
        createEndpointRule("PUT", "/api/endpoint-permissions/{id}", PermissionName.ENDPOINT_PERMISSION_WRITE, true);

        pathMatcherService.reload();
        log.info("Seeding complete");
    }

    private Permission createPermission(String name) {
        Permission permission = new Permission();
        permission.setId(UUID.randomUUID());
        permission.setName(name);
        return permissionRepository.save(permission);
    }

    private Role createRole(String name, String description, Permission... permissions) {
        Role role = new Role();
        role.setId(UUID.randomUUID());
        role.setName(name);
        role.setDescription(description);
        role.setPermissions(Set.of(permissions));
        return roleRepository.save(role);
    }

    private void createEndpointRule(String httpMethod, String pathPattern, String requiredPermission, boolean enabled) {
        EndpointPermission endpointPermission = new EndpointPermission();
        endpointPermission.setId(UUID.randomUUID());
        endpointPermission.setHttpMethod(httpMethod);
        endpointPermission.setPathPattern(pathPattern);
        endpointPermission.setRequiredPermission(requiredPermission);
        endpointPermission.setEnabled(enabled);
        endpointPermissionRepository.save(endpointPermission);
    }
}
