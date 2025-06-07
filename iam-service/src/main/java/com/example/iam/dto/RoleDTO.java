package com.example.iam.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import java.util.Set;
import lombok.NoArgsConstructor;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class RoleDTO extends BaseDTO {
    private String name;
    private String description;
    private Set<String> permissions;
} 