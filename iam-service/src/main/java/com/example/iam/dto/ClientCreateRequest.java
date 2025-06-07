package com.example.iam.dto;

import lombok.Data;
import java.util.List;
import java.util.Set;

@Data
public class ClientCreateRequest {
    private List<Long> scopes;
    private String name;
    private String description;
    private Set<String> redirectUris;
    private Set<String> authorizationGrantTypes;
    private Set<String> clientAuthenticationMethods;
    private Long organizationId;
}
