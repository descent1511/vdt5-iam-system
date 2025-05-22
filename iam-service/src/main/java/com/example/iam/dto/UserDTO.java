package com.example.iam.dto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private Long organization_id;
    private String username;
    private String email;
    private String fullName;
    private Set<Long> role_ids;
    private Set<Long> scope_ids;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}
