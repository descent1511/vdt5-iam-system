package com.example.iam.dto;

import com.example.iam.entity.Resource.HttpMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceDTO {
    private Long id;
    private String name;
    private String description;
    private HttpMethod method;
    private String path;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
