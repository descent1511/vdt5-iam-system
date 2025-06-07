package com.example.iam.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "policies")
@Getter
@Setter
@SuperBuilder
public class Policy extends BaseEntity  {

    @Column(name = "subject_id", nullable = false)
    private Long subjectId;

    @Column(name = "subject_type", nullable = false)
    private SubjectType subjectType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", nullable = false)
    private Resource resource;

    @NotBlank
    @Column(nullable = false)
    private String action;

    @Column(name = "condition_json", columnDefinition = "TEXT")
    private String conditionJson;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Effect effect;

    public enum SubjectType {
        USER,    // For regular users
        ROLE,    // For role-based access
        CLIENT,  // For client applications
        SCOPE    // For scope-based access
    }

    public enum Effect {
        ALLOW,
        DENY
    }
}