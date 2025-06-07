package com.example.iam.dto;

import com.example.iam.entity.Resource.HttpMethod;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.NoArgsConstructor;
import java.util.Set;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ResourceDTO extends BaseDTO {
    private Long id;
    private String name;
    private String description;
    private HttpMethod method;
    private String path;
    private Set<String> permissions;
}
