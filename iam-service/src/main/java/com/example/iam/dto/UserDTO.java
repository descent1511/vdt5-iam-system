package com.example.iam.dto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserDTO extends BaseDTO {
    private Long id;
    private Long organization_id;
    private String username;
    private String email;
    private String fullName;
    private Set<String> roles;
    private Set<String> permissions;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}
