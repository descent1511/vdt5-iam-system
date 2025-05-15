package com.example.iam.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {
    private Long id;
    private String name;
    private String description;
    private Set<Long> permissionIds;
    private Set<Long> scopeIds;
    private Set<Long> userIds;

    // Constructor to convert from Role entity
    public static RoleDTO fromEntity(com.example.iam.entity.Role role) {
        RoleDTO dto = new RoleDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setDescription(role.getDescription());
        
        if (role.getPermissions() != null) {
            dto.setPermissionIds(role.getPermissions().stream()
                .map(permission -> permission.getId())
                .collect(Collectors.toSet()));
        }
        
        if (role.getScopes() != null) {
            dto.setScopeIds(role.getScopes().stream()
                .map(scope -> scope.getId())
                .collect(Collectors.toSet()));
        }
        
        if (role.getUsers() != null) {
            dto.setUserIds(role.getUsers().stream()
                .map(user -> user.getId())
                .collect(Collectors.toSet()));
        }
        
        return dto;
    }
} 