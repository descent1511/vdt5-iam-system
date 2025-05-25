package com.example.iam.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientResponse {
    private String accessToken;
    private String name;
    private String description;
} 