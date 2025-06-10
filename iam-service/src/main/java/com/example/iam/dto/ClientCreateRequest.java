package com.example.iam.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class ClientCreateRequest {

    @NotBlank(message = "Client name is mandatory")
    private String clientName;

    private String description;

    @NotEmpty(message = "At least one redirect URI is required")
    private Set<String> redirectUris;

    @NotEmpty(message = "At least one grant type is required")
    private Set<String> authorizationGrantTypes;

    @NotEmpty(message = "At least one authentication method is required")
    private Set<String> clientAuthenticationMethods;

    @NotEmpty(message = "At least one scope is required")
    private List<String> scopes;
}
