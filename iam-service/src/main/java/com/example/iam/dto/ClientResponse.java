package com.example.iam.dto;

import com.example.iam.entity.Scope;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Set;

@Data
@AllArgsConstructor
public class ClientResponse {
    private String accessToken;
    private String name;
    private String description;
} 