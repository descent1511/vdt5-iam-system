package com.example.iam.service;

import com.example.iam.entity.Token;
import com.example.iam.entity.User;
import com.example.iam.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    public void revokeToken(String token) {
        Optional<Token> tokenOpt = tokenRepository.findByToken(token);
        if (tokenOpt.isPresent()) {
            Token tokenEntity = tokenOpt.get();
            tokenEntity.setRevoked(true);
            tokenRepository.save(tokenEntity);
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
        return tokenRepository.findByToken(token)
                .map(t -> !t.isExpired() && !t.isRevoked())
                .orElse(false);
    }
} 