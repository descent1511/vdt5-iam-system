package com.example.iam.service;

import com.example.iam.dto.ClientCreateRequest;
import com.example.iam.dto.ClientUpdateRequest;
import com.example.iam.entity.Client;
import com.example.iam.entity.Organization;
import com.example.iam.entity.Permission;
import com.example.iam.exception.ResourceNotFoundException;
import com.example.iam.repository.ClientRepository;
import com.example.iam.repository.OrganizationRepository;
import com.example.iam.repository.PermissionRepository;
import com.example.iam.security.OrganizationContextHolder;
import com.example.iam.audit.Auditable;
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
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final OrganizationRepository organizationRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    public Client findByClientId(String clientId) {
        Long organizationId = OrganizationContextHolder.getOrganizationId();
        if (organizationId == null) {
            throw new IllegalStateException("Organization context not found in security holder.");
        }
        return clientRepository.findByClientIdAndOrganizationId(clientId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "clientId", clientId));
    }

    @Transactional
    @Auditable(action = "UPDATE_CLIENT")
    public Client updateClient(String clientId, ClientUpdateRequest request) {
        Long organizationId = OrganizationContextHolder.getOrganizationId();
        if (organizationId == null) {
            throw new IllegalStateException("Organization context not found in security holder.");
        }
        Client client = findByClientId(clientId); // This already performs the org check

        if (request.getClientName() != null) {
            client.setClientName(request.getClientName());
        }
        if (request.getDescription() != null) {
            client.setDescription(request.getDescription());
        }
        if (request.getRedirectUris() != null) {
            client.setRedirectUris(request.getRedirectUris());
        }
        if (request.getAuthorizationGrantTypes() != null) {
            client.setAuthorizationGrantTypes(request.getAuthorizationGrantTypes());
        }
        if (request.getClientAuthenticationMethods() != null) {
            client.setClientAuthenticationMethods(request.getClientAuthenticationMethods());
        }
        if (request.getScopes() != null) {
            Set<String> scopeNames = getScopeNamesFromIds(request.getScopes());
            client.setScopes(scopeNames);
        }

        return clientRepository.save(client);
    }

    @Transactional
    @Auditable(action = "DELETE_CLIENT")
    public void deleteByClientId(String clientId) {
        Long organizationId = OrganizationContextHolder.getOrganizationId();
        if (organizationId == null) {
            throw new IllegalStateException("Organization context not found in security holder.");
        }
        // Ensure the client exists in the organization before attempting to delete
        if (clientRepository.findByClientIdAndOrganizationId(clientId, organizationId).isEmpty()) {
            throw new ResourceNotFoundException("Client", "clientId", clientId);
        }
        clientRepository.deleteByClientIdAndOrganizationId(clientId, organizationId);
    }

    @Transactional
    @Auditable(action = "CREATE_CLIENT")
    public Client createClient(Long organizationId, ClientCreateRequest request) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", organizationId));

        String rawSecret = UUID.randomUUID().toString();

        Client client = new Client();
        client.setId(UUID.randomUUID().toString());
        client.setClientId(UUID.randomUUID().toString()); // Or a more human-readable generator
        client.setClientSecret(passwordEncoder.encode(rawSecret)); // Store encoded secret
        client.setClientName(request.getClientName());
        client.setDescription(request.getDescription());
        client.setOrganization(organization);
        
        client.setClientAuthenticationMethods(request.getClientAuthenticationMethods());
        client.setAuthorizationGrantTypes(request.getAuthorizationGrantTypes());
        
        client.setRedirectUris(request.getRedirectUris());
    
        client.setScopes(new java.util.HashSet<>(request.getScopes()));

        client.setClientIdIssuedAt(Instant.now());
        client.setClientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build());
        client.setTokenSettings(TokenSettings.builder()
                .accessTokenTimeToLive(Duration.ofHours(1))
                .refreshTokenTimeToLive(Duration.ofDays(7))
                .build());
        
        Client savedClient = clientRepository.save(client);
        // Important: a raw secret is only available at creation time.
        // It's not stored in the entity. We might need a wrapper object if we want to return it.
        // For now, it's not returned.
        return savedClient;
    }

    private Set<String> getScopeNamesFromIds(List<Long> scopes) {
        if (scopes == null || scopes.isEmpty()) {
            return Set.of();
        }
        List<Permission> permissions = permissionRepository.findAllById(scopes);
        if (permissions.size() != scopes.size()) {
            // This indicates some IDs were invalid
            throw new ResourceNotFoundException("Scope", "id", "One or more provided scope IDs were not found.");
        }
        return permissions.stream().map(Permission::getName).collect(Collectors.toSet());
    }
} 