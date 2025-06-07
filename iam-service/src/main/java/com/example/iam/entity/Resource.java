package com.example.iam.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.experimental.SuperBuilder;
import java.util.Set;
import java.util.HashSet;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "resources")
@SuperBuilder
public class Resource extends BaseEntity {

    @NotBlank 
    @Size(min = 3, max = 255)
    @Column(nullable = false)
    private String path;

    @NotBlank
    @Size(min = 3, max = 100)
    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "http_method", nullable = false)
    @Enumerated(EnumType.STRING)
    private HttpMethod method;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "resource_permissions",
        joinColumns = @JoinColumn(name = "resource_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    @Builder.Default
    private Set<Permission> permissions = new HashSet<>();

    public enum HttpMethod {
        GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_registry_id", nullable = true)
    private ServiceRegistry serviceRegistry;
}