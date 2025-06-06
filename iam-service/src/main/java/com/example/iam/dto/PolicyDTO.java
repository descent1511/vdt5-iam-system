package com.example.iam.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;
import com.example.iam.entity.Policy.Effect;
import com.example.iam.entity.Policy.SubjectType;
import lombok.NoArgsConstructor;
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PolicyDTO extends BaseDTO {
    private Long id;
    private Long subjectId;
    private SubjectType subjectType;
    private Long resourceId;
    private Long scopeId;
    private String conditionJson;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String action;
    private Effect effect;
}
