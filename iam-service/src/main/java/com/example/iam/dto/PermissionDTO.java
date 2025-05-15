package com.example.iam.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDTO {
    private Long id;
    private String name;
    private String description;
    private Set<Long> roleIds;
    private Set<Long> scopeIds;

    // Constructor to convert from Permission entity
    public static PermissionDTO fromEntity(com.example.iam.entity.Permission permission) {
        PermissionDTO dto = new PermissionDTO();
        dto.setId(permission.getId());
        dto.setName(permission.getName());
        dto.setDescription(permission.getDescription());
        
        if (permission.getRoles() != null) {
            dto.setRoleIds(permission.getRoles().stream()
                .map(role -> role.getId())
                .collect(Collectors.toSet()));
        }
        
        if (permission.getScopes() != null) {
            dto.setScopeIds(permission.getScopes().stream()
                .map(scope -> scope.getId())
                .collect(Collectors.toSet()));
        }
        
        return dto;
    }
} 