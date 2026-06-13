package auth.mix.vn.v1.auth.log.repository;

import auth.mix.vn.v1.auth.log.entity.LoginLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LoginLogRepository extends JpaRepository<LoginLog, UUID> {
}
