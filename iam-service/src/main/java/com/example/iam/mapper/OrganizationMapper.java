package com.example.iam.mapper;

import com.example.iam.dto.OrganizationDTO;
import com.example.iam.entity.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {
    @Mapping(target = "userIds", expression = "java(organization.getUsers().stream().map(user -> user.getId()).collect(java.util.stream.Collectors.toSet()))")
    OrganizationDTO toDTO(Organization organization);

    @Mapping(target = "users", ignore = true)
    Organization toEntity(OrganizationDTO dto);

    List<OrganizationDTO> toDTOList(List<Organization> organizations);
}
