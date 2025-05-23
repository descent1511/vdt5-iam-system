package com.example.iam.service;

import com.example.iam.entity.ClientApplication;
import com.example.iam.entity.Scope;
import com.example.iam.repository.ClientApplicationRepository;
import com.example.iam.repository.ScopeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientApplicationRepository clientRepository;
    private final ScopeRepository scopeRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClientPolicyService clientPolicyService;

    @Transactional
    public ClientApplication createClient(String name, String description, Set<String> scopeNames) {
        log.debug("Creating new client application: {}", name);

        // Generate client credentials
        String clientId = UUID.randomUUID().toString();
        String clientSecret = UUID.randomUUID().toString();

        // Create client application
        ClientApplication client = new ClientApplication();
        client.setClientId(clientId);
        client.setClientSecret(passwordEncoder.encode(clientSecret));
        client.setName(name);
        client.setDescription(description);

        // Set scopes
        Set<Scope> scopes = new HashSet<>();
        for (String scopeName : scopeNames) {
            Scope scope = scopeRepository.findByName(scopeName)
                    .orElseThrow(() -> new RuntimeException("Scope not found: " + scopeName));
            scopes.add(scope);
        }
        client.setScopes(scopes);

        // Save client
        client = clientRepository.save(client);

        // Create policies for the client
        clientPolicyService.createPoliciesForClient(client, scopes);

        // Set raw client secret for response
        client.setClientSecret(clientSecret);

        return client;
    }

    @Transactional
    public ClientApplication updateClient(Long id, String name, String description, Set<String> scopeNames) {
        log.debug("Updating client application: {}", id);

        ClientApplication client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        client.setName(name);
        client.setDescription(description);

        // Update scopes
        Set<Scope> scopes = new HashSet<>();
        for (String scopeName : scopeNames) {
            Scope scope = scopeRepository.findByName(scopeName)
                    .orElseThrow(() -> new RuntimeException("Scope not found: " + scopeName));
            scopes.add(scope);
        }
        client.setScopes(scopes);

        // Update client
        client = clientRepository.save(client);

        // Update policies for the client
        clientPolicyService.updatePoliciesForClient(client, scopes);

        return client;
    }

    @Transactional
    public void deleteClient(Long id) {
        log.debug("Deleting client application: {}", id);

        ClientApplication client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        // Delete policies for the client
        clientPolicyService.deletePoliciesForClient(client);

        // Delete client
        clientRepository.delete(client);
    }
} 