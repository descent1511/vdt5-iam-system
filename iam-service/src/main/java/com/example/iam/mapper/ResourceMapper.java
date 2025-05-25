package com.example.iam.mapper;

import com.example.iam.dto.ResourceDTO;
import com.example.iam.entity.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ResourceMapper {
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    ResourceDTO toDTO(Resource resource);

    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    Resource toEntity(ResourceDTO dto);

    List<ResourceDTO> toDTOList(List<Resource> resources);
    List<Resource> toEntityList(List<ResourceDTO> dtos);
}
