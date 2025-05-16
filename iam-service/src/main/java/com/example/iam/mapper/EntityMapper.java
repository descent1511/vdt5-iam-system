package com.example.iam.mapper;

import com.example.iam.dto.*;
import com.example.iam.entity.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EntityMapper {
    
    public UserDTO toUserDTO(User user) {
        return UserDTO.fromEntity(user);
    }
    
    
    public List<UserDTO> toUserDTOList(List<User> users) {
        return users.stream()
                .map(this::toUserDTO)
                .collect(Collectors.toList());
    }
    
    public RoleDTO toRoleDTO(Role role) {
        return RoleDTO.fromEntity(role);
    }
    
    public List<RoleDTO> toRoleDTOList(List<Role> roles) {
        return roles.stream()
                .map(this::toRoleDTO)
                .collect(Collectors.toList());
    }
    
    public OrganizationDTO toOrganizationDTO(Organization organization) {
        return OrganizationDTO.fromEntity(organization);
    }
    
    public List<OrganizationDTO> toOrganizationDTOList(List<Organization> organizations) {
        return organizations.stream()
                .map(this::toOrganizationDTO)
                .collect(Collectors.toList());
    }

    public ScopeDTO toScopeDTO(Scope scope) {
        return ScopeDTO.fromEntity(scope);
    }
    
    public List<ScopeDTO> toScopeDTOList(List<Scope> scopes) {
        return scopes.stream()
                .map(this::toScopeDTO)
                .collect(Collectors.toList());
    }
} 