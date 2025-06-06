package com.example.iam.security;

import com.example.iam.entity.User;
import com.example.iam.entity.ClientApplication;
import com.example.iam.repository.UserRepository;
import com.example.iam.repository.ClientApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ClientApplicationRepository clientApplicationRepository;

    @Value("${app.super-admin.username}")
    private String superAdminUsername;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrClientId) throws UsernameNotFoundException {
        // Handle superadmin separately, as they don't belong to any organization
        if (superAdminUsername.equals(usernameOrClientId)) {
            User superAdmin = userRepository.findByUsername(usernameOrClientId)
                    .orElseThrow(() -> new UsernameNotFoundException("Super admin user not found: " + usernameOrClientId));
            return UserPrincipal.create(superAdmin);
        }

        // For regular users, an organization context is required
        Long organizationId = OrganizationContextHolder.getOrganizationId();
        if (organizationId == null) {
            throw new UsernameNotFoundException("Organization context is required for non-superadmin users.");
        }

        // Try to find a user first
        User user = userRepository.findByUsernameAndOrganizationIdWithRoles(usernameOrClientId, organizationId).orElse(null);
        if (user != null) {
            return UserPrincipal.create(user);
        }

        // If not a user, try to find a client application
        ClientApplication client = clientApplicationRepository.findByClientIdAndOrganizationId(usernameOrClientId, organizationId).orElse(null);
        if (client != null) {
            return ClientPrincipal.create(client);
        }

        throw new UsernameNotFoundException("No user or client found with username/clientId: " + usernameOrClientId + " in the given organization.");
    }

}
