package com.example.iam.security;

import com.example.iam.entity.Token;
import com.example.iam.entity.User;
import com.example.iam.entity.ClientApplication;
import com.example.iam.service.TokenService;
import com.example.iam.repository.ClientApplicationRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.access-token.expiration}")
    private int jwtExpirationInMs;

    @Value("${app.jwt.refresh-token.expiration}")
    private int refreshExpirationInMs;

    private final TokenService tokenService;
    private final ClientApplicationRepository clientApplicationRepository;
    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateAccessToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        String token = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();

        // Save token to database
        if (userDetails instanceof UserPrincipal) {
            User user = ((UserPrincipal) userDetails).getUser();
            tokenService.saveToken(token, user, Token.TokenType.ACCESS, 
                LocalDateTime.ofInstant(expiryDate.toInstant(), ZoneId.systemDefault()));
        }

        return token;
    }

    public String generateRefreshToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpirationInMs);

        String token = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();

        // Save token to database
        if (userDetails instanceof UserPrincipal) {
            User user = ((UserPrincipal) userDetails).getUser();
            tokenService.saveToken(token, user, Token.TokenType.REFRESH,
                LocalDateTime.ofInstant(expiryDate.toInstant(), ZoneId.systemDefault()));
        }

        return token;
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            // Check if token is blacklisted
            return tokenService.isTokenValid(authToken);
        } catch (SecurityException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty");
        }
        return false;
    }

    public String generateClientAccessToken(ClientApplication clientApp) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        String token = Jwts.builder()
                .setSubject(clientApp.getClientId())
                .claim("client_id", clientApp.getClientId())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();

        // Save token to database
        tokenService.saveClientToken(token, clientApp.getClientId(), Token.TokenType.ACCESS,
            LocalDateTime.ofInstant(expiryDate.toInstant(), ZoneId.systemDefault()));

        return token;
    }

    public String getClientIdFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("client_id", String.class);
    }

    public Set<String> getScopesFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String scopes = claims.get("scopes", String.class);
        if (scopes != null && !scopes.isEmpty()) {
            return Set.of(scopes.split(" "));
        }
        return Set.of();
    }

    public boolean validateClientToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            // Check if token is blacklisted
            return tokenService.isTokenValid(authToken);
        } catch (SecurityException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty");
        }
        return false;
    }

    public Set<String> getClientScopes(String token) {
        String clientId = getClientIdFromJWT(token);
        return clientApplicationRepository.findByClientId(clientId)
                .map(clientApp -> clientApp.getScopes().stream()
                        .map(scope -> scope.getName())
                        .collect(Collectors.toSet()))
                .orElse(Set.of());
    }
} 