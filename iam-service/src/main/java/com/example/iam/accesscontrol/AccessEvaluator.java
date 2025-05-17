package com.example.iam.accesscontrol;

import com.example.iam.entity.Policy;
import com.example.iam.repository.PolicyRepository;
import com.example.iam.security.UserPrincipal;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import com.example.iam.entity.Resource;
@Component
@RequiredArgsConstructor
public class AccessEvaluator {

    private final PolicyRepository policyRepository;
    private final ResourceMatcher resourceMatcher;

    @Transactional(readOnly = true)
    public boolean isAccessAllowed(String requestPath, String requestMethod) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof UserPrincipal user)) {
            return false;
        }

        Long userId = user.getId();
        var userRoles = user.getAuthorities();

        return policyRepository.findAll().stream()
                .anyMatch(policy -> {
                    boolean isPathMatch = resourceMatcher.matches(policy.getResource(), requestPath, requestMethod);
                    boolean isUserMatch = policy.getSubjectType() == Policy.SubjectType.USER
                            && policy.getSubjectId().equals(userId);
                    boolean isRoleMatch = policy.getSubjectType() == Policy.SubjectType.ROLE &&
                            userRoles.stream()
                                    .anyMatch(role -> role.getAuthority().equals("ROLE_" + policy.getSubjectId()));
                    return isPathMatch && (isUserMatch || isRoleMatch);
                });

    }
}