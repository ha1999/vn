package auth.mix.vn.authorization;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EndpointPermissionFilter extends OncePerRequestFilter {

    private static final String FORBIDDEN_BODY = """
            {"error": "Forbidden", "message": "%s"}
            """.strip();

    private final PathMatcherService pathMatcherService;
    private final AntPathMatcher publicMatcher = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty()) {
            path = path.substring(contextPath.length());
        }
        return publicMatcher.match(AuthorizationConstants.PUBLIC_API_PATTERN, path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String method = request.getMethod();
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty()) {
            path = path.substring(contextPath.length());
        }

        Optional<EndpointPermission> match = pathMatcherService.findMatch(method, path);

        if (match.isEmpty()) {
            writeForbidden(response, "No permission rule defined for this endpoint");
            return;
        }

        EndpointPermission rule = match.get();

        if (!rule.isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        if (rule.getRequiredPermission() == null || rule.getRequiredPermission().isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            writeForbidden(response, "Authentication required");
            return;
        }

        boolean hasPermission = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(rule.getRequiredPermission()));

        if (hasPermission) {
            filterChain.doFilter(request, response);
        } else {
            writeForbidden(response,
                    "Insufficient permission. Required: " + rule.getRequiredPermission());
        }
    }

    private void writeForbidden(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(FORBIDDEN_BODY.formatted(message));
    }
}
