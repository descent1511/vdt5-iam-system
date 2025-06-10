package com.example.iam.config;

import com.example.iam.dto.OrganizationDTO;
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
import com.example.iam.service.JpaRegisteredClientRepository;
import com.example.iam.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
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
    private final RegisteredClientRepository registeredClientRepository;
    private final OrganizationService organizationService;

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

        // Create the default organization if it doesn't exist.
        // Using OrganizationService will also create the default admin role and user for the org.
        Organization defaultOrg = organizationRepository.findByName(DEFAULT_ORG_NAME).orElseGet(() -> {
            log.info("Default organization not found, creating it via OrganizationService...");
            OrganizationDTO orgDTO = new OrganizationDTO();
            orgDTO.setName(DEFAULT_ORG_NAME);
            orgDTO.setDescription("Default organization created automatically.");
            orgDTO.setActive(true);
            organizationService.createOrganization(orgDTO);
            log.info("Default organization created successfully.");
            return organizationRepository.findByName(DEFAULT_ORG_NAME)
                    .orElseThrow(() -> new IllegalStateException("Default Org couldn't be found after creation."));
        });

        // Initialize the OAuth client for the default organization
        initializeOAuthClient(defaultOrg);

        log.info("Data initialization finished.");
    }

    private void initializeOAuthClient(Organization organization) {
        String clientId = "iam-frontend";
        
        // We must first delete any existing client with the old/bad data format.
        // The read operation (`findBy...`) will fail if the data is bad, so we
        // use a direct delete query that doesn't trigger the entity conversion.
        try {
            com.example.iam.security.OrganizationContextHolder.setOrganizationId(organization.getId());
            clientRepository.deleteByClientIdAndOrganizationId(clientId, organization.getId());
            log.info("Removed any existing OAuth2 client '{}' for organization '{}' to ensure clean data.", clientId, organization.getName());
        } finally {
            com.example.iam.security.OrganizationContextHolder.clear();
        }

        if (clientRepository.findByClientIdAndOrganizationId(clientId, organization.getId()).isEmpty()) {
            
            RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(clientId)
                .clientSecret(passwordEncoder.encode("secret"))
                .clientName("IAM Frontend Application")
                .clientIdIssuedAt(Instant.now())
                .clientAuthenticationMethods(methods -> methods.add(ClientAuthenticationMethod.CLIENT_SECRET_BASIC))
                .authorizationGrantTypes(grantTypes -> {
                    grantTypes.add(AuthorizationGrantType.AUTHORIZATION_CODE);
                    grantTypes.add(AuthorizationGrantType.REFRESH_TOKEN);
                    grantTypes.add(AuthorizationGrantType.CLIENT_CREDENTIALS);
                })
                .redirectUri("http://localhost:3002/login/oauth2/code/custom")
                .scopes(scopes -> {
                    scopes.add(OidcScopes.OPENID);
                    scopes.add(OidcScopes.PROFILE);
                    scopes.add("read");
                    scopes.add("write");
                })
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofMinutes(30))
                        .refreshTokenTimeToLive(Duration.ofDays(7))
                        .authorizationCodeTimeToLive(Duration.ofMinutes(5))
                        .build())
                .build();
            
            // We need to set organization context before saving
            // This is a bit of a workaround because the repository layer handles tenancy.
            // In a real app, client creation would be a service-layer operation.
            try {
                com.example.iam.security.OrganizationContextHolder.setOrganizationId(organization.getId());
                registeredClientRepository.save(registeredClient);
            } finally {
                com.example.iam.security.OrganizationContextHolder.clear();
            }

            log.info("Initialized OAuth2 client '{}' for organization '{}'", clientId, organization.getName());
        }
    }
} 