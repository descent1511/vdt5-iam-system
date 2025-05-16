package com.example.iam.dto;

import com.example.iam.entity.Role;
import com.example.iam.entity.Scope;
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
    private Long organization_id;
    private String username;
    private String email;
    private String fullName;
    private Set<Long> role_ids;
    private Set<Long> scope_ids;

    public static UserDTO fromEntity(User user) {
        if (user == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());

        if (user.getOrganization() != null) {
            dto.setOrganization_id(user.getOrganization().getId());
        }

        if (user.getRoles() != null) {
            dto.setRole_ids(
                user.getRoles().stream()
                    .map(Role::getId)
                    .collect(Collectors.toSet())
            );
        }

        if (user.getScopes() != null) {
            dto.setScope_ids(
                user.getScopes().stream()
                    .map(Scope::getId)
                    .collect(Collectors.toSet())
            );
        }

        return dto;
    }
}
