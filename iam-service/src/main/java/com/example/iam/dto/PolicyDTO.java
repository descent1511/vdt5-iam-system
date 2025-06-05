package com.example.iam.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PolicyDTO extends BaseDTO {
    private Long id;
    private Long subjectId;
    private String subjectType;
    private Long resourceId;
    private Long scopeId;
    private String conditionJson;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String action;
    private String effect;
}
