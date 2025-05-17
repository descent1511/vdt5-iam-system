package com.example.iam.mapper;

import com.example.iam.dto.ScopeDTO;
import com.example.iam.entity.Scope;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ScopeMapper {
    ScopeDTO toDTO(Scope scope);
    Scope toEntity(ScopeDTO dto);
    List<ScopeDTO> toDTOList(List<Scope> scopes);
}
