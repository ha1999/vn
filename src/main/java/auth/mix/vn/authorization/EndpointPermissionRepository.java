package auth.mix.vn.authorization;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EndpointPermissionRepository extends JpaRepository<EndpointPermission, UUID> {
    List<EndpointPermission> findAllByEnabledTrue();
}
