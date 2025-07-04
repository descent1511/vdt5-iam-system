package com.example.iam.repository;

import com.example.iam.entity.Role;
import com.example.iam.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
    List<Role> findByNameIn(Set<String> names);
    List<Role> findByNameInAndOrganizationId(Set<String> names, Long organizationId);
    Optional<Role> findByIdAndOrganizationId(Long id, Long organizationId);
    Optional<Role> findByNameAndOrganization(String name, Organization organization);
} 