package com.example.iam.controller;

import com.example.iam.dto.ClientCreateRequest;
import com.example.iam.dto.ClientCreationResponse;
import com.example.iam.entity.ClientApplication;
import com.example.iam.security.JwtTokenProvider;
import com.example.iam.service.OAuthService;
import com.example.iam.repository.TokenRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
import com.example.iam.dto.ClientResponse;
import com.example.iam.entity.Token;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthService oauthService;
    private final JwtTokenProvider tokenProvider;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<ClientResponse>> getAllClients() {
        List<ClientResponse> responses = oauthService.getAllClients().stream()
            .map(client -> {
                String token = client.getAccessToken();
                return new ClientResponse(
                    client.getId(),
                    client.getAccessToken(),
                    client.getName(),
                    client.getDescription()
                );
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ClientCreationResponse> createClient(@Valid @RequestBody ClientCreateRequest request) {
        ClientApplication client = oauthService.createClientApplication(request);
        String accessToken = tokenProvider.generateClientAccessToken(client);

        return ResponseEntity.ok(new ClientCreationResponse(
                accessToken,
                client.getName(),
                client.getDescription()
        ));
    }

    @DeleteMapping("/{clientId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<Void> deleteClient(@PathVariable String clientId) {
        oauthService.deleteClient(clientId);
        return ResponseEntity.noContent().build();
    }
}