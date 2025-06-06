package com.example.iam.mapper;

import com.example.iam.dto.PolicyDTO;
import com.example.iam.entity.Policy;
import com.example.iam.entity.Resource;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PolicyMapper {

    @Mapping(source = "resource.id", target = "resourceId")
    @Mapping(source = "subjectType", target = "subjectType")
    @Mapping(source = "effect", target = "effect")
    PolicyDTO toDTO(Policy policy);

    @InheritInverseConfiguration
    @Mapping(target = "resource", expression = "java(mapResource(dto.getResourceId()))")
    @Mapping(target = "subjectType", source = "subjectType")
    @Mapping(target = "effect", source = "effect")
    Policy toEntity(PolicyDTO dto);

    default Resource mapResource(Long id) {
        if (id == null) return null;
        Resource r = new Resource();
        r.setId(id);
        return r;
    }

}
