package com.example.iam.security;

import com.example.iam.entity.Token;
import com.example.iam.entity.User;
import com.example.iam.entity.Role;
import com.example.iam.entity.Permission;
import com.example.iam.entity.ClientApplication;
import com.example.iam.exception.InvalidTokenException;
import com.example.iam.service.TokenService;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationToken;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.List;
import java.util.Collection;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final String CLAIM_ORGANIZATION_ID = "organizationId";
    private static final String CLAIM_PERMISSIONS = "permissions";
    private static final String CLAIM_TYPE = "type";
    private static final String TYPE_USER = "user";
    private static final String TYPE_CLIENT = "client";

    @Value("${app.jwt.key.private}")
    private Resource privateKeyResource;

    @Value("${app.jwt.key.public}")
    private Resource publicKeyResource;

    @Value("${app.jwt.access-token.expiration}")
    private long jwtExpirationInMs;

    @Value("${app.jwt.refresh-token.expiration}")
    private long refreshExpirationInMs;

    private final TokenService tokenService;

    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;
    private RSAKey rsaJwk;

    @PostConstruct
    public void init() {
        try {
            privateKey = readPrivateKey(privateKeyResource);
            publicKey = readPublicKey(publicKeyResource);
            rsaJwk = new RSAKey.Builder(publicKey)
                    .privateKey(privateKey)
                    .keyID(UUID.randomUUID().toString())
                    .build();
        } catch (Exception e) {
            log.error("Failed to initialize RSA keys", e);
            throw new IllegalStateException("Failed to initialize RSA keys", e);
        }
    }

    public JWKSet getJwkSet() {
        return new JWKSet(rsaJwk);
    }

    public String generateAccessToken(Authentication authentication, Long organizationId) {
        return generateAndSaveToken(authentication, organizationId, jwtExpirationInMs, Token.TokenType.ACCESS);
    }

    public String generateRefreshToken(Authentication authentication, Long organizationId) {
        return generateAndSaveToken(authentication, organizationId, refreshExpirationInMs, Token.TokenType.REFRESH);
    }

    public String generateClientAccessToken(ClientApplication client) {
        ClientPrincipal clientPrincipal = ClientPrincipal.create(client);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                clientPrincipal, "", clientPrincipal.getAuthorities());
        
        return generateAndSaveToken(authentication, client.getOrganization().getId(), jwtExpirationInMs, Token.TokenType.ACCESS);
    }

    private String generateAndSaveToken(Authentication authentication, Long organizationId, long expirationMs, Token.TokenType tokenType) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(expirationMs);

        JwtBuilder tokenBuilder = Jwts.builder()
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .claim(CLAIM_ORGANIZATION_ID, organizationId);

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) principal;
            tokenBuilder.setSubject(userPrincipal.getUsername()).claim(CLAIM_TYPE, TYPE_USER);
            
            if (tokenType == Token.TokenType.ACCESS) {
                Set<String> permissions = userPrincipal.getUser().getRoles().stream()
                        .flatMap(role -> role.getPermissions().stream())
                        .map(Permission::getName)
                        .collect(Collectors.toSet());
                tokenBuilder.claim(CLAIM_PERMISSIONS, permissions);
            }
        } else if (principal instanceof ClientPrincipal) {
            ClientPrincipal clientPrincipal = (ClientPrincipal) principal;
            tokenBuilder.setSubject(clientPrincipal.getUsername()).claim(CLAIM_TYPE, TYPE_CLIENT);
        } else if (authentication.getPrincipal() instanceof UserPrincipal) {
            // This case handles the authentication object from the authorization server flow
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            tokenBuilder.setSubject(userPrincipal.getUsername()).claim(CLAIM_TYPE, TYPE_USER);
            if (tokenType == Token.TokenType.ACCESS) {
                Set<String> permissions = userPrincipal.getUser().getRoles().stream()
                        .flatMap(role -> role.getPermissions().stream())
                        .map(Permission::getName)
                        .collect(Collectors.toSet());
                tokenBuilder.claim(CLAIM_PERMISSIONS, permissions);
            }
        } else {
            // Fallback for other authentication types if needed, or throw error
            // For now, we assume the principal is the subject name (e.g., client_id for client_credentials)
            tokenBuilder.setSubject(authentication.getName());
        }

        String token = tokenBuilder.signWith(privateKey, SignatureAlgorithm.RS256).compact();

        LocalDateTime expiryDateTime = LocalDateTime.ofInstant(expiry, ZoneId.systemDefault());

        if (principal instanceof UserPrincipal) {
            User user = ((UserPrincipal) principal).getUser();
            tokenService.saveToken(token, user, tokenType, expiryDateTime);
        } else if (principal instanceof ClientPrincipal) {
            String clientId = ((ClientPrincipal) principal).getClientId();
            tokenService.saveClientToken(token, clientId, tokenType, expiryDateTime);
        }
            
            return token;
    }

    public String getSubjectFromJWT(String token) {
        return getClaims(token).getSubject();
    }

    public String getSubjectTypeFromJWT(String token) {
        return getClaims(token).get(CLAIM_TYPE, String.class);
    }

    public Long getOrganizationIdFromJWT(String token) {
        Number orgId = getClaims(token).get(CLAIM_ORGANIZATION_ID, Number.class);
        return orgId != null ? orgId.longValue() : null;
    }

    public Set<String> getPermissionsFromJWT(String token) {
        Claims claims = getClaims(token);
        Object permissionsObject = claims.get(CLAIM_PERMISSIONS);
        if (permissionsObject instanceof Collection) {
            return ((Collection<?>) permissionsObject).stream()
                    .map(String::valueOf)
                    .collect(Collectors.toSet());
        }
        return Set.of();
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            boolean isDbValid = tokenService.isTokenValid(token);
            if (!isDbValid) {
                throw new InvalidTokenException("Token not found in DB or is revoked");
            }
            return true;
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token: {}", ex.getMessage());
            throw new InvalidTokenException("Expired JWT token", ex);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw new InvalidTokenException("Invalid JWT token", e);
        }
    }

    private Claims getClaims(String token) {
        try {
        return Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (ExpiredJwtException e) {
            // Re-throw to be handled in validateToken
            throw e;
        } catch (JwtException | IllegalArgumentException e) {
            // Wrap and throw for consistent handling in validateToken
            throw new InvalidTokenException("Failed to parse JWT", e);
        }
    }

    private String readKey(Resource resource) throws IOException {
        try (InputStream inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private RSAPrivateKey readPrivateKey(Resource resource) throws Exception {
        String key = readKey(resource);
        String privateKeyPEM = key
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    private RSAPublicKey readPublicKey(Resource resource) throws Exception {
        String key = readKey(resource);
        String publicKeyPEM = key
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }
}