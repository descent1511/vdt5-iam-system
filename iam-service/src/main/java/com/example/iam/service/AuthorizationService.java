package com.example.iam.service;

import com.example.iam.entity.*;
import com.example.iam.entity.Resource;
import com.example.iam.repository.ClientApplicationRepository;
import com.example.iam.repository.PolicyRepository;
import com.example.iam.repository.ResourceRepository;
import com.example.iam.repository.UserRepository;
import com.example.iam.security.ClientPrincipal;
import com.example.iam.security.JwtTokenProvider;
import com.example.iam.security.UserPrincipal;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final ResourceRepository resourceRepository;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PolicyRepository policyRepository;
    private final ClientApplicationRepository clientApplicationRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Helper class to hold context derived from the token
    private static class AuthorizationContext {
        final Long organizationId;
        final Set<String> roles;

        AuthorizationContext(Long organizationId, Set<String> roles) {
            this.organizationId = organizationId;
            this.roles = (roles != null) ? roles : Collections.emptySet();
        }
    }

    public boolean checkPermission(String token, String path, String method) {
        if (token == null || !tokenProvider.validateToken(token)) {
            return false;
        }

        List<Resource> matchedResources = resourceRepository.findAll().stream()
                .filter(r -> isPathMatch(path, r.getPath()) && r.getMethod().name().equalsIgnoreCase(method))
                .collect(Collectors.toList());

        if (matchedResources.isEmpty()) {
            log.debug("No resource configured for path {} and method {}. Access granted by default.", path, method);
            return true;
        }

        // --- All context is now derived directly from the token ---
        String subject = tokenProvider.getSubjectFromJWT(token);
        String subjectTypeStr = tokenProvider.getSubjectTypeFromJWT(token);
        Long organizationId = tokenProvider.getOrganizationIdFromJWT(token);

        // --- Determine required actions (permissions) from the matched resources ---
        Set<String> requiredActions = matchedResources.stream()
                .flatMap(resource -> resource.getPermissions().stream())
                .map(Permission::getName)
                .collect(Collectors.toSet());

        Policy.SubjectType subjectType;
        Long subjectPk;
        Set<String> userRoles = Collections.emptySet();
        boolean hasRequiredPermissions = false;

        if ("user".equalsIgnoreCase(subjectTypeStr)) {
            subjectType = Policy.SubjectType.USER;
            User user = userRepository.findByUsernameWithRoles(subject)
                    .orElse(null);
            if (user == null) {
                log.warn("User '{}' from token not found in database.", subject);
                return false;
            }
            subjectPk = user.getId();
            userRoles = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());

            // Step 1: Coarse-grained RBAC check
            if (requiredActions.isEmpty()) {
                hasRequiredPermissions = true;
            } else {
                Set<String> userPermissions = user.getRoles().stream()
                        .flatMap(role -> role.getPermissions().stream())
                        .map(Permission::getName)
                        .collect(Collectors.toSet());
                if (!Collections.disjoint(userPermissions, requiredActions)) {
                    hasRequiredPermissions = true;
                }
            }
        } else if ("client".equalsIgnoreCase(subjectTypeStr)) {
            subjectType = Policy.SubjectType.CLIENT;
            ClientApplication client = clientApplicationRepository.findByClientId(subject)
                    .orElse(null);
            if (client == null) {
                log.warn("Client '{}' from token not found in database.", subject);
                return false;
            }
            subjectPk = client.getId();
            System.out.println("Client: " + client);
            // Clients bypass RBAC and go straight to policy checks
            hasRequiredPermissions = true;
        } else {
            log.warn("Unknown subject type '{}' in token.", subjectTypeStr);
            return false;
        }

        if (!hasRequiredPermissions) {
            log.warn("Initial permission check failed for subject: {}", subject);
            return false;
        }

        log.debug("Initial permission check passed. Proceeding to policy evaluation for subject {}.", subject);

        // Step 2: Fine-grained ABAC (Policy) check
        AuthorizationContext authContext = new AuthorizationContext(organizationId, userRoles);
        List<Long> resourceIds = matchedResources.stream().map(BaseEntity::getId).collect(Collectors.toList());
        List<Policy> relevantPolicies = policyRepository.findBySubjectTypeAndSubjectId(subjectType, subjectPk)
                .stream()
                .filter(p -> resourceIds.contains(p.getResource().getId()))
                .filter(p -> requiredActions.contains(p.getAction())) // Correctly check against resource actions
                .collect(Collectors.toList());

        if (relevantPolicies.isEmpty()) {
            // If no policies are found, the initial RBAC check is the final word.
            log.debug("No specific policies found for subjectId {}. Access granted based on initial permission check.", subjectPk);
            return true;
        }

        // Evaluate DENY policies first
        for (Policy policy : relevantPolicies) {
            if (policy.getEffect() == Policy.Effect.DENY) {
                if (evaluateConditions(policy.getConditionJson(), authContext)) {
                    log.info("Access denied by policy ID {}", policy.getId());
                    return false; // Explicit deny
                }
            }
        }

        // Evaluate ALLOW policies
        for (Policy policy : relevantPolicies) {
            if (policy.getEffect() == Policy.Effect.ALLOW) {
                if (evaluateConditions(policy.getConditionJson(), authContext)) {
                    log.info("Access allowed by policy ID {}", policy.getId());
                    return true; // Explicit allow
                }
            }
        }

        log.warn("No matching ALLOW policy with valid conditions found. Denying access.");
        return false;
    }

    private boolean isPathMatch(String requestPath, String resourcePath) {
        String pattern = resourcePath
                .replaceAll("\\{.*?\\}", "[^/]+")
                .replaceAll("/", "\\\\/");
        pattern = "^" + pattern + "$";
        return requestPath.matches(pattern);
    }

    private boolean evaluateConditions(String conditionJson, AuthorizationContext context) {
        if (!StringUtils.hasText(conditionJson)) {
            return true; // No conditions means the policy applies
        }
        try {
            log.debug("Evaluating conditions: {}", conditionJson);
            JsonNode conditions = objectMapper.readTree(conditionJson);
            if (conditions.isObject()) {
                return evaluateObjectCondition(conditions, context);
            } else if (conditions.isArray()) {
                return evaluateArrayCondition(conditions, context);
            }
            log.warn("Unknown condition format");
            return false;
        } catch (Exception e) {
            log.error("Error evaluating conditions: {}", e.getMessage(), e);
            return false;
        }
    }

    private boolean evaluateObjectCondition(JsonNode condition, AuthorizationContext context) {
        String type = condition.path("type").asText();
        String operator = condition.path("operator").asText();
        JsonNode value = condition.path("value");

        switch (type) {
            case "time":
                return evaluateTimeCondition(operator, value);
            case "date":
                return evaluateDateCondition(operator, value);
            case "ip":
                return evaluateIpCondition(operator, value);
            case "role":
                return evaluateRoleCondition(operator, value, context);
            case "organization":
                return evaluateOrganizationCondition(operator, value, context);
            default:
                log.warn("Unknown condition type: {}", type);
                return false;
        }
    }

    private boolean evaluateArrayCondition(JsonNode conditions, AuthorizationContext context) {
        for (JsonNode condition : conditions) {
            if (!evaluateObjectCondition(condition, context)) {
                return false; // Short circuit if any condition fails
            }
        }
        return true;
    }

    private boolean evaluateTimeCondition(String operator, JsonNode value) {
        LocalTime currentTime = LocalTime.now();
        try {
            switch (operator) {
                case "between":
                    return currentTime.isAfter(LocalTime.parse(value.path("start").asText())) &&
                           currentTime.isBefore(LocalTime.parse(value.path("end").asText()));
                case "before":
                    return currentTime.isBefore(LocalTime.parse(value.asText()));
                case "after":
                    return currentTime.isAfter(LocalTime.parse(value.asText()));
                default:
                    log.warn("Unknown time operator: {}", operator);
                    return false;
            }
        } catch (Exception e) {
            log.error("Error evaluating time condition: {}", e.getMessage(), e);
            return false;
        }
    }

    private boolean evaluateDateCondition(String operator, JsonNode value) {
        LocalDate currentDate = LocalDate.now();
        try {
            switch (operator) {
                case "between":
                    return currentDate.isAfter(LocalDate.parse(value.path("start").asText())) &&
                           currentDate.isBefore(LocalDate.parse(value.path("end").asText()));
                case "before":
                    return currentDate.isBefore(LocalDate.parse(value.asText()));
                case "after":
                    return currentDate.isAfter(LocalDate.parse(value.asText()));
                default:
                    log.warn("Unknown date operator: {}", operator);
                    return false;
            }
        } catch (Exception e) {
            log.error("Error evaluating date condition: {}", e.getMessage(), e);
            return false;
        }
    }

    private boolean evaluateIpCondition(String operator, JsonNode value) {
        String clientIp = getCurrentClientIp();
        try {
            switch (operator) {
                case "in":
                    for (JsonNode ip : value) {
                        if (isIpInRange(clientIp, ip.asText())) return true;
                    }
                    return false;
                case "not_in":
                    for (JsonNode ip : value) {
                        if (isIpInRange(clientIp, ip.asText())) return false;
                    }
                    return true;
                default:
                    log.warn("Unknown IP operator: {}", operator);
                    return false;
            }
        } catch (Exception e) {
            log.error("Error evaluating IP condition: {}", e.getMessage(), e);
            return false;
        }
    }

    private boolean evaluateRoleCondition(String operator, JsonNode value, AuthorizationContext context) {
        try {
            switch (operator) {
                case "has_any":
                case "has":
                    for (JsonNode role : value) {
                        if (context.roles.contains(role.asText())) return true;
                    }
                    return false;
                case "has_all":
                    for (JsonNode role : value) {
                        if (!context.roles.contains(role.asText())) return false;
                    }
                    return true;
                default:
                    log.warn("Unknown role operator: {}", operator);
                    return false;
            }
        } catch (Exception e) {
            log.error("Error evaluating role condition: {}", e.getMessage(), e);
            return false;
        }
    }

    private boolean evaluateOrganizationCondition(String operator, JsonNode value, AuthorizationContext context) {
        if (context.organizationId == null) return false;
        try {
            switch (operator) {
                case "is":
                    return context.organizationId.equals(value.asLong());
                case "in":
                    for (JsonNode orgId : value) {
                        if (context.organizationId.equals(orgId.asLong())) return true;
                    }
                    return false;
                default:
                    log.warn("Unknown organization operator: {}", operator);
                    return false;
            }
        } catch (Exception e) {
            log.error("Error evaluating organization condition: {}", e.getMessage(), e);
            return false;
        }
    }

    private boolean isIpInRange(String ip, String cidr) {
        try {
            String[] parts = cidr.split("/");
            long ipLong = ipToLong(ip);
            long cidrLong = ipToLong(parts[0]);
            long mask = -1L << (32 - Integer.parseInt(parts[1]));
            return (ipLong & mask) == (cidrLong & mask);
        } catch (Exception e) {
            log.error("Error checking IP range for ip {} and cidr {}: {}", ip, cidr, e.getMessage());
            return false;
        }
    }

    private long ipToLong(String ip) {
        String[] octets = ip.split("\\.");
        long result = 0;
        for (int i = 0; i < 4; i++) {
            result <<= 8;
            result |= Long.parseLong(octets[i]);
        }
        return result;
    }

    private String getCurrentClientIp() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            String forwardedFor = attributes.getRequest().getHeader("X-Forwarded-For");
            if (StringUtils.hasText(forwardedFor)) {
                return forwardedFor.split(",")[0].trim();
            }
            return attributes.getRequest().getRemoteAddr();
        }
        return "127.0.0.1";
    }
} 