package com.example.iam.service;
import com.example.iam.dto.ClientCreateRequest;
import com.example.iam.dto.ClientUpdateRequest;
import com.example.iam.entity.ClientApplication;
import com.example.iam.entity.Scope;
import com.example.iam.repository.ClientApplicationRepository;
import com.example.iam.repository.ScopeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import com.example.iam.security.JwtTokenProvider;
import com.example.iam.security.OrganizationContextHolder;
import com.example.iam.repository.OrganizationRepository;
import com.example.iam.entity.Organization;
@Service
@RequiredArgsConstructor
public class OAuthService {

    private final ClientApplicationRepository clientApplicationRepository;
    private final ScopeRepository scopeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final OrganizationRepository organizationRepository;
    @Transactional(readOnly = true)
    public List<ClientApplication> getAllClients() {
        return clientApplicationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public ClientApplication getClientById(String clientId) {
        return clientApplicationRepository.findByClientId(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found: " + clientId));
    }

    @Transactional
    public ClientApplication createClientApplication(ClientCreateRequest request) {
        String clientId = UUID.randomUUID().toString();
        String rawClientSecret = generateClientSecret();
        String encodedClientSecret = passwordEncoder.encode(rawClientSecret);

        Set<Scope> scopes = request.getScopes().stream()
                .map(scopeId -> scopeRepository.findById(scopeId)
                        .orElseThrow(() -> new RuntimeException("Scope not found with id: " + scopeId)))
                .collect(Collectors.toSet());

        ClientApplication client = new ClientApplication();
        client.setClientId(clientId);
        client.setClientSecret(encodedClientSecret);
        client.setDescription(request.getDescription());
        client.setName(request.getName());
        client.setScopes(scopes);
        client.setActive(true);
        client.setRawClientSecret(rawClientSecret);
        Organization organization = organizationRepository.findById(request.getOrganizationId())
                .orElseThrow(() -> new RuntimeException("Organization not found with id: " + request.getOrganizationId()));
        client.setOrganization(organization);
        client.setAccessToken(tokenProvider.generateClientAccessToken(client));
        return clientApplicationRepository.save(client);
    }

    @Transactional
    public ClientApplication updateClient(String clientId, ClientUpdateRequest request) {
        ClientApplication client = getClientById(clientId);
        
        Set<Scope> scopes = new HashSet<>();
        for (Long scopeId : request.getScopes()) {
            Scope scope = scopeRepository.findById(scopeId)
                    .orElseThrow(() -> new RuntimeException("Scope not found with id: " + scopeId));
            scopes.add(scope);
        }

        client.setName(request.getName());
        client.setDescription(request.getDescription());
        client.setScopes(scopes);
        return clientApplicationRepository.save(client);
    }

    @Transactional
    public void deleteClient(String clientId) {
        ClientApplication client = clientApplicationRepository.findByClientId(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + clientId));
        clientApplicationRepository.delete(client);
    }

    public ClientApplication validateClient(String clientId, String clientSecret) {
        ClientApplication client = getClientById(clientId);
        if (!passwordEncoder.matches(clientSecret, client.getClientSecret())) {
            throw new RuntimeException("Invalid client secret");
        }
        return client;
    }

    private String generateClientSecret() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32]; // 32 bytes = 256 bits
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}