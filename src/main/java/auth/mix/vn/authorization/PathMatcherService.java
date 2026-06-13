package auth.mix.vn.authorization;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PathMatcherService {

    private final EndpointPermissionRepository endpointPermissionRepository;
    private final PathMatcher pathMatcher = new AntPathMatcher();

    private List<EndpointPermission> cachedRules;

    @PostConstruct
    public void loadRules() {
        this.cachedRules = endpointPermissionRepository.findAllByEnabledTrue().stream()
                .sorted(Comparator.comparingInt(PathMatcherService::specificity).reversed())
                .toList();
        log.info("Loaded {} endpoint permission rules", cachedRules.size());
    }

    public void reload() {
        loadRules();
    }

    public Optional<EndpointPermission> findMatch(String httpMethod, String requestPath) {
        return cachedRules.stream()
                .filter(rule -> methodMatches(rule.getHttpMethod(), httpMethod))
                .filter(rule -> pathMatcher.match(rule.getPathPattern(), requestPath))
                .findFirst();
    }

    private boolean methodMatches(String ruleMethod, String requestMethod) {
        return AuthorizationConstants.METHOD_ANY.equalsIgnoreCase(ruleMethod)
                || ruleMethod.equalsIgnoreCase(requestMethod);
    }

    private static int specificity(EndpointPermission rule) {
        String pattern = rule.getPathPattern();
        int score = 0;
        for (String segment : pattern.split("/")) {
            if (segment.isEmpty()) continue;
            if (segment.contains("**")) {
                score += 1;
            } else if (segment.contains("{") || segment.contains("*")) {
                score += 2;
            } else {
                score += 3;
            }
        }
        if (!AuthorizationConstants.METHOD_ANY.equalsIgnoreCase(rule.getHttpMethod())) {
            score += 1;
        }
        return score;
    }
}
