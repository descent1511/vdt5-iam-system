package com.example.iam.repository;

import com.example.iam.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.Set;
import com.example.iam.entity.Organization;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(String name);
    Set<Permission> findByNameIn(Set<String> names);
    Optional<Permission> findByNameAndOrganization(String name, Organization organization);
} 