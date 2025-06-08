package com.example.iam.controller;

import com.example.iam.dto.LoginRequest;
import com.example.iam.dto.SignupRequest;
import com.example.iam.dto.TokenResponse;
import com.example.iam.entity.User;
import com.example.iam.entity.Organization;
import com.example.iam.security.JwtTokenProvider;
import com.example.iam.security.UserPrincipal;
import com.example.iam.security.OrganizationContextFilter;
import com.example.iam.service.AuthService;
import com.example.iam.service.TokenService;
import com.example.iam.repository.OrganizationRepository;
import com.example.iam.security.OrganizationContextHolder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final AuthService authService;
    private final TokenService tokenService;
    private final OrganizationRepository organizationRepository;

    @Value("${app.super-admin.username}")
    private String superAdminUsername;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        Long organizationId = getOrganizationIdFromHeader(request);
        String username = loginRequest.getUsername();
        System.out.println("organizationId: " + organizationId);
        System.out.println("username: " + username);
        // Handle superadmin login separately
        if (organizationId == null && superAdminUsername.equals(username)) {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    username,
                    loginRequest.getPassword()
                )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String accessToken = tokenProvider.generateAccessToken(authentication, null);
            String refreshToken = tokenProvider.generateRefreshToken(authentication, null);
            return ResponseEntity.ok(new TokenResponse(accessToken, refreshToken));
        }
        
        // Regular user login
        if (organizationId == null) {
            throw new IllegalArgumentException("Organization ID is required for non-superadmin users.");
        }

        OrganizationContextHolder.setOrganizationId(organizationId);

        try {
        Organization organization = organizationRepository.findById(organizationId)
            .orElseThrow(() -> new IllegalArgumentException("Organization not found"));
            
        if (!organization.isActive()) {
            throw new IllegalArgumentException("Organization is not active");
        }

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    username,
                loginRequest.getPassword()
            )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        String accessToken = tokenProvider.generateAccessToken(authentication, organizationId);
        String refreshToken = tokenProvider.generateRefreshToken(authentication, organizationId);
            
            return ResponseEntity.ok(new TokenResponse(accessToken, refreshToken));
        } finally {
        OrganizationContextHolder.clear();
        }
    }

    private Long getOrganizationIdFromHeader(HttpServletRequest request) {
        String orgIdStr = request.getHeader("X-Organization-Id");
        if (orgIdStr == null || orgIdStr.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(orgIdStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid X-Organization-Id header format.", e);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest signupRequest, HttpServletRequest request) {
        String orgIdStr = (String) request.getAttribute(OrganizationContextFilter.ORGANIZATION_ID_ATTRIBUTE);
        if (orgIdStr == null) {
            throw new IllegalArgumentException("Organization ID not found in request");
        }
        Long organizationId = Long.parseLong(orgIdStr);
        log.info("Processing signup request for username: {} in organization: {}", signupRequest.getUsername(), organizationId);

        // Kiểm tra organization
        Organization organization = organizationRepository.findById(organizationId)
            .orElseThrow(() -> new IllegalArgumentException("Organization not found"));
            
        if (!organization.isActive()) {
            throw new IllegalArgumentException("Organization is not active");
        }

        // Kiểm tra username đã tồn tại trong tổ chức
        if (authService.existsByUsernameAndOrganizationId(signupRequest.getUsername(), organizationId)) {
            throw new IllegalArgumentException("Username already exists in this organization");
        }

        // Lưu organizationId vào SignupRequest
        signupRequest.setOrganizationId(organizationId);

        // Đăng ký user
        User user = authService.registerUser(signupRequest);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7); // Remove "Bearer "
        Long organizationId = tokenProvider.getOrganizationIdFromJWT(jwt);
        String username = tokenProvider.getSubjectFromJWT(jwt);
        
        tokenService.revokeToken(jwt);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        String token = refreshToken.substring(7); // Remove "Bearer "
        TokenResponse newTokens = authService.refreshToken(token);
        return ResponseEntity.ok(newTokens);
    }
}