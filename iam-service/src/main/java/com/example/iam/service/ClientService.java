package com.example.iam.service;

import com.example.iam.dto.ClientRegistrationRequest;
import com.example.iam.entity.Client;
import com.example.iam.entity.Organization;
import com.example.iam.exception.ResourceNotFoundException;
import com.example.iam.repository.ClientRepository;
import com.example.iam.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Client createClient(Long organizationId, ClientRegistrationRequest request) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", organizationId));

        Client client = new Client();
        client.setId(UUID.randomUUID().toString());
        client.setClientId(UUID.randomUUID().toString()); // Or a more human-readable generator
        client.setClientSecret(passwordEncoder.encode(UUID.randomUUID().toString())); // Generate a secure secret
        client.setClientIdIssuedAt(Instant.now());
        client.setClientName(request.getClientName());
        client.setOrganization(organization);
        
        client.setClientAuthenticationMethods(Set.of(ClientAuthenticationMethod.CLIENT_SECRET_BASIC.getValue()));
        client.setAuthorizationGrantTypes(Set.of(
                AuthorizationGrantType.AUTHORIZATION_CODE.getValue(),
                AuthorizationGrantType.REFRESH_TOKEN.getValue(),
                AuthorizationGrantType.CLIENT_CREDENTIALS.getValue()
        ));
        
        client.setRedirectUris(request.getRedirectUris());
        client.setScopes(request.getScopes());

        client.setClientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build());
        client.setTokenSettings(TokenSettings.builder()
                .accessTokenTimeToLive(Duration.ofHours(1))
                .refreshTokenTimeToLive(Duration.ofDays(7))
                .build());

        return clientRepository.save(client);
    }
} 