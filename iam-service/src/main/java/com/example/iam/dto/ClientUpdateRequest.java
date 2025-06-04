package com.example.iam.dto;

import lombok.Data;
import java.util.List;

@Data
public class ClientUpdateRequest {
    private List<Long> scopes;
    private String name;
    private String description;
} 