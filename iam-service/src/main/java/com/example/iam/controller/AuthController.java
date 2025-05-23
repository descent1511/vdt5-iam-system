package com.example.iam.controller;

import com.example.iam.dto.LoginRequest;
import com.example.iam.dto.SignupRequest;
import com.example.iam.dto.TokenResponse;
import com.example.iam.dto.ClientResponse;
import com.example.iam.dto.ClientCreateRequest;
import com.example.iam.entity.ClientApplication;
import com.example.iam.entity.Scope;
import com.example.iam.entity.User;
import com.example.iam.repository.ClientApplicationRepository;
import com.example.iam.repository.ScopeRepository;
import com.example.iam.security.JwtTokenProvider;
import com.example.iam.service.AuthService;
import com.example.iam.service.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final AuthService authService;
    private final TokenService tokenService;
    private final ClientApplicationRepository clientApplicationRepository;
    private final ScopeRepository scopeRepository;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        System.out.println("loginRequest: " + loginRequest);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);
        System.out.println("accessToken: " + accessToken);
        System.out.println("refreshToken: " + refreshToken);
        return ResponseEntity.ok(new TokenResponse(accessToken, refreshToken));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest signupRequest) {
        System.out.println("signupRequest: " + signupRequest);
        User user = authService.registerUser(signupRequest);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        String token = refreshToken.substring(7); // Remove "Bearer "
        TokenResponse newTokens = authService.refreshToken(token);
        return ResponseEntity.ok(newTokens);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7); // Remove "Bearer "
        tokenService.revokeToken(jwt);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout-all")
    public ResponseEntity<Void> logoutAll(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7); // Remove "Bearer "
        String username = tokenProvider.getUsernameFromJWT(jwt);
        User user = authService.getUserByUsername(username);
        tokenService.revokeAllUserTokens(user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/client/create")
    public ResponseEntity<ClientResponse> createClientWithScopes(
            @RequestBody ClientCreateRequest request) {
    
        // Tạo clientId và clientSecret
        String clientId = UUID.randomUUID().toString();
        String clientSecret = UUID.randomUUID().toString();
    
        // Lấy scopes từ database
        Set<Scope> scopes = new HashSet<>();
        for (String scopeName : request.getScopeNames()) {
            Scope scope = scopeRepository.findByName(scopeName)
                    .orElseThrow(() -> new RuntimeException("Scope not found: " + scopeName));
            scopes.add(scope);
        }
    
        // Tạo client application mới
        ClientApplication clientApp = new ClientApplication();
        clientApp.setClientId(clientId);
        clientApp.setClientSecret(clientSecret);
        clientApp.setName(request.getName() != null ? request.getName() : "Client " + clientId);
        clientApp.setDescription(request.getDescription());
        clientApp.setScopes(scopes);
        clientApp.setActive(true);
    
        // Lưu client application
        clientApp = clientApplicationRepository.save(clientApp);
    
        // Tạo access token
        String accessToken = tokenProvider.generateClientAccessToken(clientApp);
    
        // Trả về thông tin client và token
        return ResponseEntity.ok(new ClientResponse(
                accessToken,
                clientApp.getName(),
                clientApp.getDescription(),
                request.getScopeNames()
        ));
    }
    
} 