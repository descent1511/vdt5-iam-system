package com.example.iam.dto;

import lombok.Data;

@Data
public class AuthorizationRequest {
    private String token;
    private String path;
    private String method;
} 