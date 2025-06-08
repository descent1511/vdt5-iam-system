package com.example.iam.config;

import com.example.iam.entity.Organization;
import com.example.iam.entity.Permission;
import com.example.iam.entity.Role;
import com.example.iam.entity.User;
import com.example.iam.entity.Client;
import com.example.iam.repository.ClientRepository;
import com.example.iam.repository.OrganizationRepository;
import com.example.iam.repository.PermissionRepository;
import com.example.iam.repository.RoleRepository;
import com.example.iam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final OrganizationRepository organizationRepository;
    private final ClientRepository clientRepository;

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

        // Create the default organization first
        Organization defaultOrg = organizationRepository.findByName(DEFAULT_ORG_NAME).orElseGet(() -> {
            log.info("Creating default organization: {}", DEFAULT_ORG_NAME);
            Organization org = Organization.builder()
                .name(DEFAULT_ORG_NAME)
                .description("Default organization created automatically.")
                .active(true)
                .build();
            return organizationRepository.save(org);
        });

        // Initialize the OAuth client for the default organization
        initializeOAuthClient(defaultOrg);

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

        // Configure the Super Admin user
        User superAdmin = userRepository.findByUsername(superAdminUsername)
                .orElseGet(() -> {
                    log.info("Creating {} user for the first time.", superAdminUsername);
                    return User.builder()
                            .username(superAdminUsername)
                            .email(superAdminUsername + "@system.local")
                            .fullName("System Super Admin")
                            .enabled(true)
                            .active(true)
                            .organization(defaultOrg)
                            .build();
                });
        superAdmin.setPassword(passwordEncoder.encode(superAdminPassword));
        superAdmin.setRoles(new HashSet<>(Set.of(superAdminRole)));
        userRepository.save(superAdmin);
        log.info("Super admin user configured successfully.");

        // Create a default user in the default organization
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

    private void initializeOAuthClient(Organization organization) {
        String clientId = "iam-frontend";
        if (clientRepository.findByClientIdAndOrganizationId(clientId, organization.getId()).isEmpty()) {
            Client client = new Client();
            client.setId(UUID.randomUUID().toString());
            client.setClientId(clientId);
            client.setClientSecret(passwordEncoder.encode("secret"));
            client.setClientName("IAM Frontend Application");
            client.setClientIdIssuedAt(Instant.now());
            client.setOrganization(organization);

            client.setClientAuthenticationMethods(Set.of(ClientAuthenticationMethod.CLIENT_SECRET_BASIC.getValue()));
            client.setAuthorizationGrantTypes(Set.of(
                    AuthorizationGrantType.AUTHORIZATION_CODE.getValue(),
                    AuthorizationGrantType.REFRESH_TOKEN.getValue(),
                    AuthorizationGrantType.CLIENT_CREDENTIALS.getValue()
            ));

            client.setRedirectUris(Set.of(
                "http://localhost:3002/login"
            ));
            
            client.setScopes(Set.of(
                OidcScopes.OPENID,
                OidcScopes.PROFILE,
                "read",
                "write"
            ));

            client.setClientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build());
            client.setTokenSettings(TokenSettings.builder()
                    .accessTokenTimeToLive(Duration.ofMinutes(30))
                    .refreshTokenTimeToLive(Duration.ofDays(7))
                    .authorizationCodeTimeToLive(Duration.ofMinutes(5))
                    .build());
            
            clientRepository.save(client);
            log.info("Initialized OAuth2 client '{}' for organization '{}'", clientId, organization.getName());
        }
    }
} 