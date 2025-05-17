package com.example.iam.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PolicyDTO {
    private Long id;
    private Long subjectId;
    private String subjectType;
    private Long resourceId;
    private Long scopeId;
    private String conditionJson;
    private String description;
}
