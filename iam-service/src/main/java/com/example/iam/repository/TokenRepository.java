package com.example.iam.repository;

import com.example.iam.entity.Token;
import com.example.iam.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    List<Token> findByToken(String token);
    List<Token> findByUserId(Long userId);
    List<Token> findByClientId(String clientId);
    List<Token> findByUserAndExpiredFalseAndRevokedFalse(User user);
} 