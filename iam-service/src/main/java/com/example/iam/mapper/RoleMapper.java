package com.example.iam.mapper;

import com.example.iam.dto.RoleDTO;
import com.example.iam.entity.Permission;
import com.example.iam.entity.Role;
import com.example.iam.entity.Scope;
import com.example.iam.entity.User;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "scope_ids", expression = "java(mapScopeIds(role.getScopes()))")
    @Mapping(target = "user_ids", expression = "java(mapUserIds(role.getUsers()))")
    RoleDTO toDTO(Role role);

    @InheritInverseConfiguration
    @Mapping(target = "scopes", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "users", ignore = true)
    Role toEntity(RoleDTO dto);

    default Set<Long> mapScopeIds(Set<Scope> scopes) {
        return scopes == null ? null :
                scopes.stream().map(Scope::getId).collect(Collectors.toSet());
    }

    default Set<Long> mapUserIds(Set<User> users) {
        return users == null ? null :
                users.stream().map(User::getId).collect(Collectors.toSet());
    }
}
