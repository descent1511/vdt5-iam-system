package com.example.iam.mapper;

import com.example.iam.dto.ResourceDTO;
import com.example.iam.entity.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.example.iam.entity.Permission;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = {Collectors.class})
public interface ResourceMapper {

    @Mapping(target = "permissions", expression = "java(mapPermissions(resource.getPermissions()))")
    ResourceDTO toDTO(Resource resource);

    @Mapping(target = "permissions", ignore = true)
    Resource toEntity(ResourceDTO dto);

    default List<ResourceDTO> toDTOList(List<Resource> resources) {
        if (resources == null) {
            return null;
        }
        return resources.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    default List<Resource> toEntityList(List<ResourceDTO> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    default Set<String> mapPermissions(Set<Permission> permissions) {
        if (permissions == null) {
            return null;
        }
        return permissions.stream()
                .map(Permission::getName)
                .collect(Collectors.toSet());
    }
}
