package com.example.iam.service;

import com.example.iam.entity.Token;
import com.example.iam.entity.User;
import com.example.iam.entity.Organization;
import com.example.iam.repository.TokenRepository;
import com.example.iam.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;
    private final OrganizationRepository organizationRepository;

    @Value("${app.super-admin.username}")
    private String superAdminUsername;

    @Transactional
    public void saveToken(String token, User user, Token.TokenType tokenType, LocalDateTime expiresAt) {
        Organization organization = null;

        // Superadmin tokens are not associated with any organization
        if (!superAdminUsername.equals(user.getUsername())) {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                throw new IllegalStateException("No request context found for non-superadmin user");
            }
            
            HttpServletRequest request = attributes.getRequest();
            Object orgIdAttr = request.getAttribute("organizationId");
            if (orgIdAttr == null) {
                throw new IllegalStateException("Organization ID not found in request for non-superadmin user");
            }
            
            Long organizationId = Long.valueOf(orgIdAttr.toString());
            organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new IllegalStateException("Organization not found for ID: " + organizationId));
                
            log.info("Saving token for user: {} in organization: {}", user.getUsername(), organizationId);
        } else {
            log.info("Saving token for superadmin user: {}", user.getUsername());
        }

        // Revoke all existing tokens of the same type for this user
        tokenRepository.findByUserAndTokenTypeAndExpiredFalseAndRevokedFalse(user, tokenType)
            .forEach(existingToken -> {
                existingToken.setRevoked(true);
                tokenRepository.save(existingToken);
            });

        Token tokenEntity = new Token();
        tokenEntity.setToken(token);
        tokenEntity.setUser(user);
        tokenEntity.setTokenType(tokenType);
        tokenEntity.setExpired(false);
        tokenEntity.setRevoked(false);
        tokenEntity.setCreatedAt(LocalDateTime.now());
        tokenEntity.setExpiresAt(expiresAt);
        tokenEntity.setOrganization(organization);
        tokenRepository.save(tokenEntity);
    }

    @Transactional
    public void saveClientToken(String token, String clientId, Token.TokenType tokenType, LocalDateTime expiresAt) {
        // Lấy organization ID từ request attribute
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new IllegalStateException("No request context found");
        }
        
        HttpServletRequest request = attributes.getRequest();
        String orgIdStr = (String) request.getAttribute("organizationId");
        if (orgIdStr == null) {
            throw new IllegalStateException("Organization ID not found in request");
        }
        
        Long organizationId = Long.parseLong(orgIdStr);
        Organization organization = organizationRepository.findById(organizationId)
            .orElseThrow(() -> new IllegalStateException("Organization not found"));
            
        log.info("Saving client token for client: {} in organization: {}", clientId, organizationId);

        // Revoke all existing tokens of the same type for this client
        tokenRepository.findByClientIdAndTokenTypeAndExpiredFalseAndRevokedFalse(clientId, tokenType)
            .forEach(existingToken -> {
                existingToken.setRevoked(true);
                tokenRepository.save(existingToken);
            });

        Token tokenEntity = new Token();
        tokenEntity.setToken(token);
        tokenEntity.setClientId(clientId);
        tokenEntity.setTokenType(tokenType);
        tokenEntity.setExpired(false);
        tokenEntity.setRevoked(false);
        tokenEntity.setCreatedAt(LocalDateTime.now());
        tokenEntity.setExpiresAt(expiresAt);
        tokenEntity.setOrganization(organization);
        tokenRepository.save(tokenEntity);
    }

    @Transactional
    public void revokeToken(String token) {
        tokenRepository.findByToken(token)
            .forEach(tokenEntity -> {
                tokenEntity.setRevoked(true);
                tokenRepository.save(tokenEntity);
            });
    }

    @Transactional
    public void revokeAllUserTokens(User user) {
        tokenRepository.findByUserAndExpiredFalseAndRevokedFalse(user)
                .forEach(token -> {
                    token.setRevoked(true);
                    tokenRepository.save(token);
                });
    }

    public boolean isTokenValid(String token) {
        List<Token> tokens = tokenRepository.findByToken(token);
        if (!tokens.isEmpty()) {
            // Get the most recent token
            Token tokenEntity = tokens.stream()
                .max(Comparator.comparing(Token::getCreatedAt))
                .orElse(null);
                
            if (tokenEntity != null) {
                // Check if token is expired
                if (LocalDateTime.now().isAfter(tokenEntity.getExpiresAt())) {
                    tokenEntity.setExpired(true);
                    tokenRepository.save(tokenEntity);
                    return false;
                }
                return !tokenEntity.isExpired() && !tokenEntity.isRevoked();
            }
        }
        return false;
    }
} 