package com.example.iam.mapper;

import com.example.iam.dto.ResourceDTO;
import com.example.iam.entity.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.example.iam.entity.Permission;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ResourceMapper {

    @Mapping(target = "permissions", expression = "java(mapPermissions(resource.getPermissions()))")
    ResourceDTO toDTO(Resource resource);

    @Mapping(target = "permissions", ignore = true)
    Resource toEntity(ResourceDTO dto);

    List<ResourceDTO> toDTOList(List<Resource> resources);
    List<Resource> toEntityList(List<ResourceDTO> dtos);

    default Set<String> mapPermissions(Set<Permission> permissions) {
        if (permissions == null) {
            return null;
        }
        return permissions.stream()
                .map(Permission::getName)
                .collect(Collectors.toSet());
    }
}
