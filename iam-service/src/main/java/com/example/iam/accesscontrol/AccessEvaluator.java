package com.example.iam.accesscontrol;

import com.example.iam.entity.Policy;
import com.example.iam.entity.Role;
import com.example.iam.repository.PolicyRepository;
import com.example.iam.repository.RoleRepository;
import com.example.iam.security.UserPrincipal;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccessEvaluator {
    private static final Logger log = LoggerFactory.getLogger(AccessEvaluator.class);
    private final PolicyRepository policyRepository;
    private final ResourceMatcher resourceMatcher;
    private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public boolean isAccessAllowed(String requestPath, String requestMethod) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof UserPrincipal user)) {
            return false;
        }

        Long userId = user.getId();
        var userRoles = user.getAuthorities();

        var results = policyRepository.findAll().stream()
                .map(policy -> {
                    boolean isPathMatch = resourceMatcher.matches(policy.getResource(), requestPath, requestMethod);
                    boolean isUserMatch = policy.getSubjectType() == Policy.SubjectType.USER
                            && policy.getSubjectId().equals(userId);
                    boolean isRoleMatch = policy.getSubjectType() == Policy.SubjectType.ROLE &&
                            userRoles.stream()
                                    .anyMatch(role -> {
                                        Role policyRole = roleRepository.findById(policy.getSubjectId()).orElse(null);
                                        return policyRole != null && 
                                               role.getAuthority().equals("ROLE_" + policyRole.getName());
                                    });

                    return isPathMatch && (isUserMatch || isRoleMatch);
                })
                .collect(Collectors.toList());

        boolean allowed = results.contains(true);
        
        return allowed;
    }

}