package com.example.iam.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScopeDTO {
    private Long id;
    private String name;
    private String description;
    private Set<Long> permissionIds;
    private Set<Long> userIdIds;
    private Set<Long> roleIds;

    // Constructor to convert from Scope entity
    public static ScopeDTO fromEntity(com.example.iam.entity.Scope scope) {
        ScopeDTO dto = new ScopeDTO();
        dto.setId(scope.getId());
        dto.setName(scope.getName());
        dto.setDescription(scope.getDescription());
        
        if (scope.getPermissions() != null) {
            dto.setPermissionIds(scope.getPermissions().stream()
                .map(permission -> permission.getId())
                .collect(Collectors.toSet()));
        }
        
        if (scope.getUsers() != null) {
            dto.setUserIdIds(scope.getUsers().stream()
                .map(user -> user.getId())
                .collect(Collectors.toSet()));
        }
        
        if (scope.getRoles() != null) {
            dto.setRoleIds(scope.getRoles().stream()
                .map(role -> role.getId())
                .collect(Collectors.toSet()));
        }
        
        return dto;
    }
} 