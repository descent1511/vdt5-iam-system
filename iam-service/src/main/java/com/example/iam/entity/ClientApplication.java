package com.example.iam.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.HashSet;
import java.util.Set;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "client_applications")
@SuperBuilder
public class ClientApplication extends BaseEntity {

    @NotBlank
    @Size(min = 3, max = 100)
    @Column(name = "client_id", nullable = false)
    private String clientId;

    @NotBlank
    @Column(name = "client_secret", nullable = false)
    private String clientSecret;

    @NotBlank
    @Size(min = 3, max = 100)
    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "client_scopes",
        joinColumns = @JoinColumn(name = "client_id"),
        inverseJoinColumns = @JoinColumn(name = "scope_id")
    )
    private Set<Scope> scopes = new HashSet<>();

    @Column(name = "is_active")
    private boolean active = true;
} 