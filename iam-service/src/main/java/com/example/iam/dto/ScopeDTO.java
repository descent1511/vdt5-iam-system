package com.example.iam.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScopeDTO {
    private Long id;
    private String name;
    private String description;

} 