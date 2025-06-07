package com.example.iam.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Data
@NoArgsConstructor
public class ClientApplicationResponse {
    private String clientId;
    private String name;
    private String description;
    private boolean active;
    private Set<String> scopes;
    private Set<String> redirectUris;
    private Set<String> authorizationGrantTypes;
    private Set<String> clientAuthenticationMethods;
}