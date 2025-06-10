package com.example.iam.mapper;

import com.example.iam.dto.ServiceRegistryDto;
import com.example.iam.entity.ServiceRegistry;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ServiceRegistryMapper {
    ServiceRegistryMapper INSTANCE = Mappers.getMapper(ServiceRegistryMapper.class);

    ServiceRegistryDto toDto(ServiceRegistry entity);

    ServiceRegistry toEntity(ServiceRegistryDto dto);
} 