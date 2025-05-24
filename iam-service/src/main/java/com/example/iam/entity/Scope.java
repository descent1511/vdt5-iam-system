package com.example.iam.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "scopes")
@SuperBuilder
public class Scope extends BaseEntity {

    @NotBlank
    @Size(min = 3, max = 100)
    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "scope_permissions",
        joinColumns = @JoinColumn(name = "scope_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();

    // Mối quan hệ nhiều-nhiều với Resource (để ánh xạ scope với endpoint)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "scope_resources",
        joinColumns = @JoinColumn(name = "scope_id"),
        inverseJoinColumns = @JoinColumn(name = "resource_id")
    )
    private Set<Resource> resources = new HashSet<>();
}