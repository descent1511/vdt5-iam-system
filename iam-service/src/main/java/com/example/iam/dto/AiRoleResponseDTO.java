package com.example.iam.dto;

import lombok.Data;
import java.util.Set;

@Data
public class AiRoleResponseDTO {
    private String name;
    private String description;
    private Set<String> permissions;
} 