package com.example.iam.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.util.HashSet;
import java.util.Set;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
@Data
@Entity
@Table(name = "organizations")
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@SuperBuilder
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private boolean active = true;

    @Column(unique = true)
    private String domain;

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL)
    private Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL)
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL)
    private Set<Policy> policies = new HashSet<>();

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL)
    private Set<Resource> resources = new HashSet<>();

    public void addUser(User user) {
        users.add(user);
        user.setOrganization(this);
    }

    public void removeUser(User user) {
        users.remove(user);
        user.setOrganization(null);
    }

    public void addRole(Role role) {
        roles.add(role);
        role.setOrganization(this);
    }

    public void removeRole(Role role) {
        roles.remove(role);
        role.setOrganization(null);
    }
} 