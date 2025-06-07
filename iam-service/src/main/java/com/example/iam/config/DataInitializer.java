package com.example.iam.config;

import com.example.iam.entity.Organization;
import com.example.iam.entity.Permission;
import com.example.iam.entity.Role;
import com.example.iam.entity.User;
import com.example.iam.repository.OrganizationRepository;
import com.example.iam.repository.PermissionRepository;
import com.example.iam.repository.RoleRepository;
import com.example.iam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final OrganizationRepository organizationRepository;

    @Value("${app.super-admin.username}")
    private String superAdminUsername;

    @Value("${app.super-admin.password}")
    private String superAdminPassword;

    private static final String SUPER_ADMIN_ROLE_NAME = "ROLE_SUPER_ADMIN";
    private static final String DEFAULT_ORG_NAME = "Default Organization";

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Starting data initialization...");

        // Create a default organization if it doesn't exist
        organizationRepository.findByName(DEFAULT_ORG_NAME).orElseGet(() -> {
            log.info("Creating default organization: {}", DEFAULT_ORG_NAME);
            Organization defaultOrg = Organization.builder()
                .name(DEFAULT_ORG_NAME)
                .description("Default organization created automatically.")
                .active(true)
                .build();
            return organizationRepository.save(defaultOrg);
        });

        // Create a global permission for super admin
        Permission systemAdminPermission = permissionRepository.findByName("SYSTEM_ADMINISTRATION")
                .orElseGet(() -> {
                    log.info("Creating SYSTEM_ADMINISTRATION permission.");
                    return permissionRepository.save(Permission.builder()
                            .name("SYSTEM_ADMINISTRATION")
                            .description("Full access to all system resources.")
                            .build());
                });

        // Create the Super Admin role if it doesn't exist
        Role superAdminRole = roleRepository.findByName(SUPER_ADMIN_ROLE_NAME)
                .orElseGet(() -> {
                    log.info("Creating {} role.", SUPER_ADMIN_ROLE_NAME);
                    Role role = Role.builder()
                            .name(SUPER_ADMIN_ROLE_NAME)
                            .description("Super Administrator role with system-wide access.")
                            .permissions(Set.of(systemAdminPermission))
                            .build();
                    return roleRepository.save(role);
                });

        // Find or create the Super Admin user, and always ensure their details are correct.
        User superAdmin = userRepository.findByUsername(superAdminUsername)
                .orElseGet(() -> {
                    log.info("Creating {} user for the first time.", superAdminUsername);
                    return User.builder()
                            .username(superAdminUsername)
                            .email(superAdminUsername + "@system.local") // This email must be unique
                            .fullName("System Super Admin")
                            .enabled(true)
                            .active(true)
                            .organization(null) // Super admin does not belong to any organization
                            .build();
                });

        // Always ensure the password and roles are up-to-date
        superAdmin.setPassword(passwordEncoder.encode(superAdminPassword));
        superAdmin.setRoles(new HashSet<>(Set.of(superAdminRole)));

        userRepository.save(superAdmin);
        log.info("Super admin user configured successfully.");

        // Create a default user in the default organization
        Organization defaultOrg = organizationRepository.findByName(DEFAULT_ORG_NAME)
            .orElseThrow(() -> new IllegalStateException("Default organization not found, cannot create default user."));

        if (!userRepository.existsByUsernameAndOrganizationId("user", defaultOrg.getId())) {
            log.info("Creating default user 'user' in organization '{}'", DEFAULT_ORG_NAME);
            User defaultUser = User.builder()
                .username("user")
                .email("user@default.org")
                .password(passwordEncoder.encode("password"))
                .fullName("Default User")
                .enabled(true)
                .active(true)
                .organization(defaultOrg)
                .build();
            userRepository.save(defaultUser);
        }
        
        log.info("Data initialization finished.");
    }
} 