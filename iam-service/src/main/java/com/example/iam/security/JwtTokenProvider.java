package com.example.iam.security;

import com.example.iam.entity.ClientApplication;
import com.example.iam.entity.Token;
import com.example.iam.entity.User;
import com.example.iam.service.TokenService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.UserDetails;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.access-token.expiration}")
    private long jwtExpirationInMs;

    @Value("${app.jwt.refresh-token.expiration}")
    private long refreshExpirationInMs;

    private final TokenService tokenService;

    private Key key;

    @PostConstruct
    public void init() {
        if (jwtSecret.length() < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 characters long for HS256");
        }
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateAccessToken(Authentication authentication) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "user");
        claims.put("username", userDetails.getUsername());

        String token = Jwts.builder()
            .setClaims(claims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();

        if (userDetails instanceof UserPrincipal) {
            User user = ((UserPrincipal) userDetails).getUser();
            tokenService.saveToken(token, user, Token.TokenType.ACCESS,
                    LocalDateTime.ofInstant(expiryDate.toInstant(), ZoneId.systemDefault()));
        }
        return token;
    }

    public String generateRefreshToken(Authentication authentication) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpirationInMs);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "user");

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(userDetails.getUsername()))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        if (userDetails instanceof UserPrincipal) {
            User user = ((UserPrincipal) userDetails).getUser();
            tokenService.saveToken(token, user, Token.TokenType.REFRESH,
                LocalDateTime.ofInstant(expiryDate.toInstant(), ZoneId.systemDefault()));
        }

        return token;
    }

    public String generateClientAccessToken(ClientApplication clientApp) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "client");
        claims.put("client_id", clientApp.getClientId());

        String token = Jwts.builder()
                .setSubject(clientApp.getClientId())
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();

        tokenService.saveClientToken(token, clientApp.getClientId(), Token.TokenType.ACCESS,
            LocalDateTime.ofInstant(expiryDate.toInstant(), ZoneId.systemDefault()));

        return token;
    }

    public String getSubjectFromJWT(String token) {
        return getClaims(token).getSubject();
    }

    public String getSubjectTypeFromJWT(String token) {
        return getClaims(token).get("type", String.class);
    }
    public String getUsernameFromJWT(String token) {
        return getClaims(token).get("username", String.class);
    }


    public String getClientIdFromJWT(String token) {
        return getClaims(token).get("client_id", String.class);
    }

    public boolean validateToken(String token) {
        try {
            return tokenService.isTokenValid(token);
        } catch (SecurityException ex) {
            log.error("Invalid JWT signature: {}", ex.getMessage());
            return false;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
            return false;
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token: {}", ex.getMessage());
            return false;
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token: {}", ex.getMessage());
            return false;
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty: {}", ex.getMessage());
            return false;
        }
    }

    public boolean validateClientToken(String token) {
        return validateToken(token);
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}