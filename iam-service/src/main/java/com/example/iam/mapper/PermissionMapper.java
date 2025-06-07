package com.example.iam.mapper;

import com.example.iam.dto.PermissionDTO;
import com.example.iam.entity.Permission;
import com.example.iam.entity.Role;
import com.example.iam.entity.Scope;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    PermissionMapper INSTANCE = Mappers.getMapper(PermissionMapper.class);

    @Mapping(source = "roles", target = "roleIds", qualifiedByName = "rolesToRoleIds")
    @Mapping(source = "scopes", target = "scopeIds", qualifiedByName = "scopesToScopeIds")
    PermissionDTO toDto(Permission permission);
    
    List<PermissionDTO> toDtoList(List<Permission> permissions);

    @Named("rolesToRoleIds")
    default Set<Long> rolesToRoleIds(Set<Role> roles) {
        if (roles == null) {
            return null;
        }
        return roles.stream()
                .map(Role::getId)
                .collect(Collectors.toSet());
    }

    @Named("scopesToScopeIds")
    default Set<Long> scopesToScopeIds(Set<Scope> scopes) {
        if (scopes == null) {
            return null;
        }
        return scopes.stream()
                .map(Scope::getId)
                .collect(Collectors.toSet());
    }
} 