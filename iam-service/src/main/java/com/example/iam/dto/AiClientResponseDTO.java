package com.example.iam.dto;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class AiClientResponseDTO {
    private String clientName;
    private String description;
    private Set<String> redirectUris;
    private Set<String> authorizationGrantTypes;
    private Set<String> clientAuthenticationMethods;
    private List<String> scopes;
} 