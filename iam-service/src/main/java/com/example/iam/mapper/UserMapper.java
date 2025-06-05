package com.example.iam.mapper;

import com.example.iam.dto.UserDTO;
import com.example.iam.entity.Role;
import com.example.iam.entity.Scope;
import com.example.iam.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "organization.id", target = "organization_id")
    @Mapping(source = "roles", target = "roles", qualifiedByName = "mapRoleNames")
    @Mapping(target = "permissions", expression = "java(mapPermissions(user))")
    @Mapping(target = "password", ignore = true)
    UserDTO toDTO(User user);

    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "scopes", ignore = true)
    @Mapping(target = "enabled", constant = "true")
    User toEntity(UserDTO dto);

    List<UserDTO> toDTOList(List<User> users);
    List<User> toEntityList(List<UserDTO> dtos);

    @Named("mapRoleNames")
    default Set<String> mapRoleNames(Set<Role> roles) {
        return roles != null ? roles.stream()
            .map(Role::getName)
            .collect(Collectors.toSet()) : null;
    }

    default Set<String> mapPermissions(User user) {
        if (user == null || user.getRoles() == null) {
            return null;
        }
        return user.getRoles().stream()
            .flatMap(role -> role.getPermissions().stream())
            .map(permission -> permission.getName())
            .collect(Collectors.toSet());
    }
}