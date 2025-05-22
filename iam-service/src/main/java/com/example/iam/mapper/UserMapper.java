package com.example.iam.mapper;

import com.example.iam.dto.UserDTO;
import com.example.iam.entity.Role;
import com.example.iam.entity.Scope;
import com.example.iam.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "organization.id", target = "organization_id")
    @Mapping(target = "role_ids", expression = "java(mapRoleIds(user.getRoles()))")
    @Mapping(target = "scope_ids", expression = "java(mapScopeIds(user.getScopes()))")
    UserDTO toDTO(User user);

    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "roles", ignore = true) 
    @Mapping(target = "scopes", ignore = true) 
    User toEntity(UserDTO dto);

    List<UserDTO> toDTOList(List<User> users);
    List<User> toEntityList(List<UserDTO> dtos);

    @Named("mapRoleIds")
    default Set<Long> mapRoleIds(Set<Role> roles) {
        return roles != null ? roles.stream().map(Role::getId).collect(Collectors.toSet()) : null;
    }

    @Named("mapScopeIds")
    default Set<Long> mapScopeIds(Set<Scope> scopes) {
        return scopes != null ? scopes.stream().map(Scope::getId).collect(Collectors.toSet()) : null;
    }
}