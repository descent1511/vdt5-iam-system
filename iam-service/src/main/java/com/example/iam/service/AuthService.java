package com.example.iam.service;

import com.example.iam.dto.SignupRequest;
import com.example.iam.dto.TokenResponse;
import com.example.iam.entity.Organization;
import com.example.iam.entity.User;
import com.example.iam.exception.ResourceNotFoundException;
import com.example.iam.repository.OrganizationRepository;
import com.example.iam.repository.UserRepository;
import com.example.iam.security.JwtTokenProvider;
import com.example.iam.security.OrganizationContextHolder;
import com.example.iam.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    @Transactional
    public User registerUser(SignupRequest signupRequest) {
        validateSignupRequest(signupRequest);

        Long organizationId = OrganizationContextHolder.getOrganizationId();
        if (organizationId == null) {
            throw new IllegalStateException("Organization ID must be provided in the context for registration.");
        }

        // Check username uniqueness within organization
        if (userRepository.existsByUsernameAndOrganizationId(signupRequest.getUsername(), organizationId)) {
            throw new IllegalArgumentException("Username is already taken in this organization");
        }

        // Check email uniqueness within organization
        if (userRepository.existsByEmailAndOrganizationId(signupRequest.getEmail(), organizationId)) {
            throw new IllegalArgumentException("Email is already in use in this organization");
        }

        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setFullName(signupRequest.getFullName());
        user.setActive(true);
        
        // The new EntityListener will handle setting the organization
        
        log.info("Registering new user {} in organization {}", user.getUsername(), organizationId);
        return userRepository.save(user);
    }

    @Transactional
    public TokenResponse authenticateUser(String username, String password, Long organizationId) {
        // Verify organization exists and is active
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        if (!organization.isActive()) {
            throw new IllegalArgumentException("Organization is not active");
        }

        // Verify user exists in the organization
        User user = userRepository.findByUsernameAndOrganizationId(username, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found in this organization"));

        if (!user.isActive()) {
            throw new IllegalArgumentException("User account is not active");
        }

        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        // Generate tokens with organization context
        String accessToken = tokenProvider.generateAccessToken(authentication, organizationId);
        String refreshToken = tokenProvider.generateRefreshToken(authentication, organizationId);

        log.info("User {} authenticated successfully in organization {}", username, organization.getName());
        return new TokenResponse(accessToken, refreshToken);
    }
    
@Transactional
    public TokenResponse refreshToken(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            throw new IllegalArgumentException("Refresh token cannot be empty");
        }

        if (!tokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String username = tokenProvider.getSubjectFromJWT(refreshToken);
        Long organizationId = tokenProvider.getOrganizationIdFromJWT(refreshToken);
        String subjectType = tokenProvider.getSubjectTypeFromJWT(refreshToken);

        log.info("Refreshing token for user {} in organization {}", username, organizationId);
        if (!"user".equals(subjectType)) {
            throw new IllegalArgumentException("Invalid token type for refresh");
        }

        // Verify organization exists and is active
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        if (!organization.isActive()) {
            throw new IllegalArgumentException("Organization is not active");
        }

        // Verify user exists and is active in the organization
        User user = userRepository.findByUsernameAndOrganizationId(username, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found in this organization"));

        if (!user.isActive()) {
            throw new IllegalArgumentException("User account is not active");
        }

        // Lưu organizationId vào context để sử dụng trong UserDetailsService
        OrganizationContextHolder.setOrganizationId(organizationId);

        try {
            // Tải UserDetails trực tiếp từ UserDetailsService
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (!(userDetails instanceof UserPrincipal userPrincipal) || !userPrincipal.getOrganizationId().equals(organizationId)) {
                throw new IllegalArgumentException("Invalid user or organization mismatch");
            }

            // Tạo Authentication object
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Tạo token mới
            String newAccessToken = tokenProvider.generateAccessToken(authentication, organizationId);
            String newRefreshToken = tokenProvider.generateRefreshToken(authentication, organizationId);

            log.info("Token refreshed for user {} in organization {}", username, organization.getName());
            return new TokenResponse(newAccessToken, newRefreshToken);
        } finally {
            // Xóa organizationId khỏi context để tránh rò rỉ
            OrganizationContextHolder.clear();
        }
    }

    @Transactional(readOnly = true)
    public User getUserByUsernameAndOrganization(String username, Long organizationId) {
        return userRepository.findByUsernameAndOrganizationId(username, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found in this organization"));
    }

    public boolean existsByUsernameAndOrganizationId(String username, Long organizationId) {
        return userRepository.existsByUsernameAndOrganizationId(username, organizationId);
    }

    private void validateSignupRequest(SignupRequest request) {
        if (!StringUtils.hasText(request.getUsername())) {
            throw new IllegalArgumentException("Username is required");
        }
        if (!StringUtils.hasText(request.getEmail())) {
            throw new IllegalArgumentException("Email is required");
        }
        if (!StringUtils.hasText(request.getPassword())) {
            throw new IllegalArgumentException("Password is required");
        }
        if (request.getUsername().length() < 3 || request.getUsername().length() > 50) {
            throw new IllegalArgumentException("Username must be between 3 and 50 characters");
        }
        if (request.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
} 