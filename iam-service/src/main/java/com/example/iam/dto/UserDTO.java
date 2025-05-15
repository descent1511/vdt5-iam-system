package com.example.iam.dto;

import com.example.iam.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private Long organizationId;
    private String username;
    private String email;
    private String password;
    private String fullName;
    private Set<Long> roleIds;
    private Set<Long> scopeIds;

    public static UserDTO fromEntity(User user) {
        if (user == null) {
            return null;
        }
        
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        
        try {
            if (user.getOrganization() != null) {
                dto.setOrganizationId(user.getOrganization().getId());
            }
        } catch (Exception e) {
            // Ignore lazy loading exception for organization
        }
        
        try {
            if (user.getRoles() != null) {
                dto.setRoleIds(user.getRoles().stream()
                    .map(role -> role.getId())
                    .collect(Collectors.toSet()));
            }
        } catch (Exception e) {
            // Ignore lazy loading exception for roles
        }
        
        try {
            if (user.getScopes() != null) {
                dto.setScopeIds(user.getScopes().stream()
                    .map(scope -> scope.getId())
                    .collect(Collectors.toSet()));
            }
        } catch (Exception e) {
            // Ignore lazy loading exception for scopes
        }
        
        return dto;
    }
} 