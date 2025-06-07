package com.example.iam.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRegistryDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "Service name cannot be blank")
    private String name;

    private String description;

    @NotBlank(message = "Service URL cannot be blank")
    @URL(message = "Invalid URL format")
    private String url;
} 