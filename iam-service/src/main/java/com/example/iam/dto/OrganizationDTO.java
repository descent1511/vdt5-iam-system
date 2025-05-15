package com.example.iam.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationDTO {
    private Long id;
    private String name;
    private String description;
    private Set<Long> userIds;

    // Constructor to convert from Organization entity
    public static OrganizationDTO fromEntity(com.example.iam.entity.Organization organization) {
        OrganizationDTO dto = new OrganizationDTO();
        dto.setId(organization.getId());
        dto.setName(organization.getName());
        dto.setDescription(organization.getDescription());
        
        if (organization.getUsers() != null) {
            dto.setUserIds(organization.getUsers().stream()
                .map(user -> user.getId())
                .collect(Collectors.toSet()));
        }
        
        return dto;
    }
} 