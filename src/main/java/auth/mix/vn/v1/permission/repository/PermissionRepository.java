package auth.mix.vn.v1.permission.repository;
import auth.mix.vn.v1.permission.entity.*;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {
    Optional<Permission> findByName(String name);
}
