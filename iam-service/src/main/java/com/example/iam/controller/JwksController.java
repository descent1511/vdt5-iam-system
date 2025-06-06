package com.example.iam.controller;

import com.nimbusds.jose.jwk.JWKSet;
import com.example.iam.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class JwksController {

    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/oauth2/jwks.json")
    public Map<String, Object> jwks() {
        JWKSet jwkSet = jwtTokenProvider.getJwkSet();
        return jwkSet.toJSONObject();
    }
} 