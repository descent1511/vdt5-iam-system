package com.example.iam.config.aspect;

import com.example.iam.security.OrganizationContextHolder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class OrganizationFilterAspect {

    @PersistenceContext
    private EntityManager entityManager;

    private static final String SUPER_ADMIN_ROLE = "SUPER_ADMIN";

    @Before("execution(* com.example.iam.repository.*.*(..))")
    public void configureOrganizationFilter() {
        Session session = entityManager.unwrap(Session.class);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Super admin can see all data, so we disable the filter for them.
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(SUPER_ADMIN_ROLE))) {
            session.disableFilter("organizationFilter");
            return;
        }

        // For regular users, apply the filter based on their organization context.
        Long organizationId = OrganizationContextHolder.getOrganizationId();
        if (organizationId != null) {
            session.enableFilter("organizationFilter")
                    .setParameter("organizationId", organizationId);
        } else {
            // If no organization context is found for a non-super-admin, disable the filter.
            // This allows access to non-organization-specific entities but prevents seeing all data.
            session.disableFilter("organizationFilter");
        }
    }
} 