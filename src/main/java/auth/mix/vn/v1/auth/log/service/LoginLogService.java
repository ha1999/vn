package auth.mix.vn.v1.auth.log.service;

import auth.mix.vn.v1.auth.log.entity.LoginAction;
import auth.mix.vn.v1.auth.log.entity.LoginLog;
import auth.mix.vn.v1.auth.log.repository.LoginLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoginLogService {

    private final LoginLogRepository loginLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(UUID userId, LoginAction action, HttpServletRequest request, String details) {
        LoginLog log = new LoginLog();
        log.setId(UUID.randomUUID());
        log.setUserId(userId);
        log.setAction(action);
        log.setCreatedAt(LocalDateTime.now());

        if (request != null) {
            log.setIpAddress(request.getRemoteAddr());
            log.setUserAgent(request.getHeader("User-Agent"));
        }

        log.setDetails(details);
        loginLogRepository.save(log);
    }
}
