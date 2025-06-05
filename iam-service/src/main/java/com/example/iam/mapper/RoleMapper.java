package com.example.iam.mapper;

import com.example.iam.dto.RoleDTO;
import com.example.iam.entity.Permission;
import com.example.iam.entity.Role;
import com.example.iam.entity.User;
import org.springframework.stereotype.Component;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RoleMapper {
    
    public RoleDTO toDTO(Role role) {
        if (role == null) return null;
        
        RoleDTO dto = new RoleDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setDescription(role.getDescription());
        dto.setCreatedAt(role.getCreatedAt());
        dto.setUpdatedAt(role.getUpdatedAt());
        dto.setPermissions(role.getPermissions().stream()
            .map(permission -> permission.getName())
            .collect(Collectors.toSet()));
        return dto;
    }

    public Role toEntity(RoleDTO dto) {
        if (dto == null) return null;
        
        Role role = new Role();
        role.setId(dto.getId());
        role.setName(dto.getName());
        role.setDescription(dto.getDescription());
        return role;
    }

    public Set<Long> mapUserIds(Set<User> users) {
        if (users == null) return null;
        return users.stream()
            .map(User::getId)
            .collect(Collectors.toSet());
    }

    public Set<String> mapPermissions(Set<Permission> permissions) {
        if (permissions == null) return null;
        return permissions.stream()
            .map(Permission::getName)
            .collect(Collectors.toSet());
    }
}
