package com.example.iam.mapper;

import com.example.iam.dto.ResourceDTO;
import com.example.iam.entity.Resource;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ResourceMapper {
    ResourceDTO toDTO(Resource resource);
    Resource toEntity(ResourceDTO dto);
    List<ResourceDTO> toDTOList(List<Resource> resources);
    List<Resource> toEntityList(List<ResourceDTO> dtos);
}
