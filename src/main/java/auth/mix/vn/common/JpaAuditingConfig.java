package auth.mix.vn.common;

import auth.mix.vn.v1.auth.config.CustomUserDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<UUID> auditorAware() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(auth -> auth.isAuthenticated()
                        && !"anonymousUser".equals(auth.getPrincipal())
                        && auth.getPrincipal() instanceof CustomUserDetails)
                .map(auth -> ((CustomUserDetails) auth.getPrincipal()).getId());
    }
}
