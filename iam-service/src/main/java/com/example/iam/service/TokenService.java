package com.example.iam.service;

import com.example.iam.entity.Token;
import com.example.iam.entity.User;
import com.example.iam.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;

    @Transactional
    public void saveToken(String token, User user, Token.TokenType tokenType, LocalDateTime expiresAt) {
        Token tokenEntity = new Token();
        tokenEntity.setToken(token);
        tokenEntity.setUser(user);
        tokenEntity.setTokenType(tokenType);
        tokenEntity.setExpired(false);
        tokenEntity.setRevoked(false);
        tokenEntity.setCreatedAt(LocalDateTime.now());
        tokenEntity.setExpiresAt(expiresAt);
        tokenRepository.save(tokenEntity);
    }

    @Transactional
    public void saveClientToken(String token, String clientId, Token.TokenType tokenType, LocalDateTime expiresAt) {
        Token tokenEntity = new Token();
        tokenEntity.setToken(token);
        tokenEntity.setClientId(clientId);
        tokenEntity.setTokenType(tokenType);
        tokenEntity.setExpired(false);
        tokenEntity.setRevoked(false);
        tokenEntity.setCreatedAt(LocalDateTime.now());
        tokenEntity.setExpiresAt(expiresAt);
        tokenRepository.save(tokenEntity);
    }

    @Transactional
    public void revokeToken(String token) {
        List<Token> tokens = tokenRepository.findByToken(token);
        if (!tokens.isEmpty()) {
            tokens.forEach(tokenEntity -> {
                tokenEntity.setRevoked(true);
                tokenRepository.save(tokenEntity);
            });
        }
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