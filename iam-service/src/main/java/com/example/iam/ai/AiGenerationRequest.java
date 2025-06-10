package com.example.iam.ai;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AiGenerationRequest {

    @NotBlank(message = "Entity type cannot be blank")
    private String entityType;

    @NotBlank(message = "User description cannot be blank")
    private String userDescription;
} 