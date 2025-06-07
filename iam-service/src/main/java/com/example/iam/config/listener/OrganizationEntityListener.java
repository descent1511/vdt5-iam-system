package com.example.iam.config.listener;

import com.example.iam.entity.BaseEntity;
import com.example.iam.entity.Organization;
import com.example.iam.repository.OrganizationRepository;
import com.example.iam.security.OrganizationContextHolder;
import jakarta.persistence.PrePersist;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class OrganizationEntityListener {

    private static final String SUPER_ADMIN_ROLE = "ROLE_SUPER_ADMIN";

    @PrePersist
    public void setOrganization(BaseEntity entity) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Do not assign an organization to entities created by a super admin
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(SUPER_ADMIN_ROLE))) {
            return;
        }

        if (entity.getOrganization() == null) {
            Long organizationId = OrganizationContextHolder.getOrganizationId();
            if (organizationId != null) {
                OrganizationRepository organizationRepository = BeanUtil.getBean(OrganizationRepository.class);
                Organization organization = organizationRepository.findById(organizationId)
                        .orElseThrow(() -> new IllegalStateException("Organization with ID " + organizationId + " not found for a non-admin user."));
                entity.setOrganization(organization);
            }
        }
    }
} 