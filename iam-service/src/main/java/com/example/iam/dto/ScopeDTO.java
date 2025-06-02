package com.example.iam.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScopeDTO {
    private Long id;
    private String name;
    private String description;
    private Set<String> permissions;
} 