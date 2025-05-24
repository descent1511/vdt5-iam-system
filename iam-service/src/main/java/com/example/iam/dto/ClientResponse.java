package com.example.iam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponse {
    private String accessToken;
    private String name;
    private String description;
    private List<String> scopes;
} 