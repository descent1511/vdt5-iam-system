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

    @Mapping(target = "user_ids", expression = "java(mapUserIds(role.getUsers()))")
    @Mapping(target = "permissions", expression = "java(mapPermissions(role.getPermissions()))")
    RoleDTO toDTO(Role role);

    @InheritInverseConfiguration
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "users", ignore = true)
    Role toEntity(RoleDTO dto);

    default Set<Long> mapUserIds(Set<User> users) {
        return users == null ? null :
                users.stream().map(User::getId).collect(Collectors.toSet());
    }

    default Set<String> mapPermissions(Set<Permission> permissions) {
        return permissions == null ? null :
                permissions.stream().map(Permission::getName).collect(Collectors.toSet());
    }
}
