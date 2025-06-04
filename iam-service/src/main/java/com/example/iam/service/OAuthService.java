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

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final ClientApplicationRepository clientApplicationRepository;
    private final ScopeRepository scopeRepository;
    private final PasswordEncoder passwordEncoder;

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
        String clientSecret = generateClientSecret();
        String encodedClientSecret = passwordEncoder.encode(clientSecret);

        Set<Scope> scopes = new HashSet<>();
        for (Long scopeId : request.getScopes()) {
            Scope scope = scopeRepository.findById(scopeId)
                    .orElseThrow(() -> new RuntimeException("Scope not found with id: " + scopeId));
            scopes.add(scope);
        }

        ClientApplication client = new ClientApplication();
        client.setClientId(clientId);
        client.setClientSecret(encodedClientSecret);
        client.setDescription(request.getDescription());
        client.setName(request.getName() != null ? request.getName() : "Client " + clientId);
        client.setScopes(scopes);
        client.setActive(true);
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
    public void deleteClient(Long id) {
        ClientApplication client = clientApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + id));
        clientApplicationRepository.delete(client);
    }

    private String generateClientSecret() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32]; // 32 bytes = 256 bits
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}