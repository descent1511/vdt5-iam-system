package com.example.iam.mapper;

import com.example.iam.dto.ScopeDTO;
import com.example.iam.entity.Scope;
import com.example.iam.entity.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ScopeMapper {
    @Mapping(target = "permissions", expression = "java(mapPermissions(scope.getPermissions()))")
    ScopeDTO toDTO(Scope scope);
    
    @Mapping(target = "permissions", ignore = true)
    Scope toEntity(ScopeDTO dto);
    
    List<ScopeDTO> toDTOList(List<Scope> scopes);

    default Set<String> mapPermissions(Set<Permission> permissions) {
        if (permissions == null) return null;
        return permissions.stream()
            .map(Permission::getName)
            .collect(Collectors.toSet());
    }

}
