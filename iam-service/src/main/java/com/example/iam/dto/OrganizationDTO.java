package com.example.iam.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OrganizationDTO extends BaseDTO {
    private Long id;
    private String name;
    private String description;
    private Set<Long> userIds;
} 