package com.example.iam.mapper;

import com.example.iam.dto.OrganizationDTO;
import com.example.iam.entity.Organization;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {
    OrganizationDTO toDTO(Organization organization);
    Organization toEntity(OrganizationDTO dto);
    List<OrganizationDTO> toDTOList(List<Organization> organizations);
}
