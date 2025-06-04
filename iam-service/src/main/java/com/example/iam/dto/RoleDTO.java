package com.example.iam.dto;

import lombok.Data;
import java.util.Set;

@Data
public class RoleDTO extends BaseDTO {
    private Long id;
    private String name;
    private String description;
    private Set<String> permissions;
} 