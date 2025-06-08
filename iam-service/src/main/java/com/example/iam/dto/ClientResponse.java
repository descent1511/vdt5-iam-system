package com.example.iam.dto;

import lombok.Data;

import java.time.Instant;
import java.util.Set;

@Data
public class ClientResponse {
    private String id;
    private String clientId;
    private String clientName;
    private String description;
    private Instant clientIdIssuedAt;
    private Set<String> redirectUris;
    private Set<String> scopes;
    private Set<String> authorizationGrantTypes;
    private Set<String> clientAuthenticationMethods;
} 