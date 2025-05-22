package com.example.iam.mapper;

import com.example.iam.dto.PolicyDTO;
import com.example.iam.entity.Policy;
import com.example.iam.entity.Resource;
import com.example.iam.entity.Scope;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PolicyMapper {

    @Mapping(source = "resource.id", target = "resourceId")
    @Mapping(source = "scope.id", target = "scopeId")
    @Mapping(source = "subjectType", target = "subjectType")
    PolicyDTO toDTO(Policy policy);

    @InheritInverseConfiguration
    @Mapping(target = "resource", expression = "java(mapResource(dto.getResourceId()))")
    @Mapping(target = "scope", expression = "java(mapScope(dto.getScopeId()))")
    @Mapping(target = "subjectType", expression = "java(dto.getSubjectType() != null ? com.example.iam.entity.Policy.SubjectType.valueOf(dto.getSubjectType()) : null)")
    Policy toEntity(PolicyDTO dto);

    default Resource mapResource(Long id) {
        if (id == null) return null;
        Resource r = new Resource();
        r.setId(id);
        return r;
    }

    default Scope mapScope(Long id) {
        if (id == null) return null;
        Scope s = new Scope();
        s.setId(id);
        return s;
    }
}
