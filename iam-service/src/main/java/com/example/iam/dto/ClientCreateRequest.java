package com.example.iam.dto;

import lombok.Data;
import java.util.List;

@Data
public class ClientCreateRequest {
    private List<String> scopes;
    private String name;
    private String description;
}
