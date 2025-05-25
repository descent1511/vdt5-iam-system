
package com.example.iam.service;
import com.example.iam.dto.ClientCreateRequest;
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

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final ClientApplicationRepository clientApplicationRepository;
    private final ScopeRepository scopeRepository;
    private final PasswordEncoder passwordEncoder;

    // @Transactional(readOnly = true)
    // public ClientResponse generateClientAccessToken(ClientCreateRequest request) {
    //     ClientApplication client = clientApplicationRepository.findByClientId(request.getClientId())
    //             .orElseThrow(() -> new SecurityException("Invalid client ID"));

    //     if (!passwordEncoder.matches(request.getClientSecret(), client.getClientSecret())) {
    //         throw new SecurityException("Invalid client secret");
    //     }

    //     Set<String> allowedScopes = client.getScopes().stream()
    //             .map(Scope::getName)
    //             .collect(Collectors.toSet());
    //     Set<String> requestedScopes = request.getScopes() != null ? request.getScopes() : allowedScopes;
    //     requestedScopes.retainAll(allowedScopes);
    //     if (requestedScopes.isEmpty()) {
    //         throw new IllegalArgumentException("No valid scopes provided");
    //     }

    //     User delegatedUser = null;
    //     if (request.getUserId() != null) {
    //         delegatedUser = userRepository.findById(request.getUserId())
    //                 .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + request.getUserId()));
    //     }

    //     String accessToken = jwtTokenProvider.generateClientAccessToken(client, requestedScopes, delegatedUser);

    //     return TokenResponseDTO.builder()
    //             .accessToken(accessToken)
    //             .tokenType("Bearer")
    //             .expiresIn(jwtTokenProvider.getJwtExpirationInMs() / 1000)
    //             .scope(String.join(" ", requestedScopes))
    //             .build();
    // }

    @Transactional
    public ClientApplication createClientApplication(ClientCreateRequest request) {

        String clientId = UUID.randomUUID().toString();

        // Generate clientSecret
        String clientSecret = generateClientSecret();
        String encodedClientSecret = passwordEncoder.encode(clientSecret);

        Set<Scope> scopes = new HashSet<>();
        for (String scopeName : request.getScopes()) {
            Scope scope = scopeRepository.findByName(scopeName)
                    .orElseThrow(() -> new RuntimeException("Scope not found: " + scopeName));
            scopes.add(scope);
        }

        // Create ClientApplication
        ClientApplication client = new ClientApplication();
        client.setClientId(clientId);
        client.setClientSecret(encodedClientSecret);
        client.setDescription(request.getDescription());
        client.setName(request.getName() != null ? request.getName() : "Client " + clientId);
        client.setScopes(scopes);
        client.setActive(true);
        return   clientApplicationRepository.save(client);
    }
    private String generateClientSecret() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32]; // 32 bytes = 256 bits
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}