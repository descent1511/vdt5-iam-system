package com.example.iam.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

@Data
public class ClientRegistrationRequest {

    @NotBlank(message = "Client name is mandatory")
    private String clientName;

    @NotEmpty(message = "At least one redirect URI is required")
    private Set<String> redirectUris;

    @NotEmpty(message = "At least one scope is required")
    private Set<String> scopes;
} 