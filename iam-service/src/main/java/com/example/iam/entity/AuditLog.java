package com.example.iam.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private Instant timestamp;

    @Column(nullable = false, updatable = false)
    private String principal;

    @Column(nullable = false, updatable = false)
    private String action;

    @Column(updatable = false)
    private String entityType;

    @Column(updatable = false)
    private String entityId;

    @Lob
    @Column(name = "details", updatable = false, columnDefinition = "TEXT")
    private String details;

    public AuditLog(String principal, String action, String entityType, String entityId, String details) {
        this.timestamp = Instant.now();
        this.principal = principal;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.details = details;
    }
} 