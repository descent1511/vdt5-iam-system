package com.example.iam.accesscontrol;

import com.example.iam.entity.Policy;
import com.example.iam.entity.Role;
import com.example.iam.entity.Scope;
import com.example.iam.entity.Resource;
import com.example.iam.repository.PolicyRepository;
import com.example.iam.repository.RoleRepository;
import com.example.iam.repository.ResourceRepository;
import com.example.iam.security.UserPrincipal;
import com.example.iam.security.ClientPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Evaluates access control policies for both users and client applications.
 * Supports role-based and scope-based access control.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AccessEvaluator {
    
    private final PolicyRepository policyRepository;
    private final ResourceMatcher resourceMatcher;
    private final RoleRepository roleRepository;
    private final ResourceRepository resourceRepository;

    /**
     * Evaluates whether the current principal has access to the requested resource.
     *
     * @param requestPath the path of the requested resource
     * @param requestMethod the HTTP method of the request
     * @return true if access is allowed, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean isAccessAllowed(String requestPath, String requestMethod) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.debug("No authenticated principal found");
            return false;
        }

        Object principal = authentication.getPrincipal();
        System.out.println("principal: " + principal);

        if (principal instanceof UserPrincipal userPrincipal) {
            return evaluateUserAccess(userPrincipal, requestPath, requestMethod);
        } else if (principal instanceof ClientPrincipal clientPrincipal) {
            return evaluateClientAccess(clientPrincipal, requestPath, requestMethod);
        }


        return false;
    }

    /**
     * Evaluates access for a user principal.
     */
    private boolean evaluateUserAccess(UserPrincipal userPrincipal, String requestPath, String requestMethod) {
        Long userId = userPrincipal.getId();
        Set<String> userRoles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        System.out.println("ok");
        // Get all applicable policies for the resource
        Set<Policy> applicablePolicies = policyRepository.findAll().stream()
                .filter(policy -> resourceMatcher.matches(policy.getResource(), requestPath, requestMethod))
                .collect(Collectors.toSet());

        if (applicablePolicies.isEmpty()) {
            log.debug("No applicable policies found for path: {} and method: {}", requestPath, requestMethod);
            return false;
        }

        return applicablePolicies.stream().anyMatch(policy -> {
            // Check user-specific policy
            if (policy.getSubjectType() == Policy.SubjectType.USER && 
                policy.getSubjectId().equals(userId)) {
                log.debug("User-specific policy match found for user: {}", userId);
                return true;
            }

            // Check role-based policy
            if (policy.getSubjectType() == Policy.SubjectType.ROLE) {
                Role policyRole = roleRepository.findById(policy.getSubjectId()).orElse(null);
                if (policyRole != null && userRoles.contains("ROLE_" + policyRole.getName())) {
                    log.debug("Role-based policy match found for user: {} with role: {}", 
                            userId, policyRole.getName());
                    return true;
                }
            }

            return false;
        });
    }

    /**
     * Evaluates access for a client application principal.
     */
    private boolean evaluateClientAccess(ClientPrincipal clientPrincipal, String requestPath, String requestMethod) {
        // Get client's scopes from authorities
        Set<String> clientScopes = clientPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> auth.startsWith("SCOPE_"))
                .map(auth -> auth.substring(6)) // Remove "SCOPE_" prefix
                .collect(Collectors.toSet());

        log.debug("Evaluating client access with scopes: {}", clientScopes);

        // Get all resources that match the client's scopes
        Set<Resource> accessibleResources = resourceRepository.findAll().stream()
                .filter(resource -> resource.getScopes().stream()
                        .anyMatch(scope -> clientScopes.contains(scope.getName())))
                .collect(Collectors.toSet());

        // Check if any of the accessible resources match the requested path and method
        return accessibleResources.stream()
                .anyMatch(resource -> resourceMatcher.matches(resource, requestPath, requestMethod));
    }
}