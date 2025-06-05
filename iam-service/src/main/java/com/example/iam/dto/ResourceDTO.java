package com.example.iam.dto;

import com.example.iam.entity.Resource.HttpMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ResourceDTO extends BaseDTO {
    private Long id;
    private String name;
    private String description;
    private HttpMethod method;
    private String path;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
