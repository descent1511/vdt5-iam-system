package com.example.iam.repository;

import com.example.iam.entity.Organization;
import com.example.iam.entity.ServiceRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRegistryRepository extends JpaRepository<ServiceRegistry, Long> {
    Optional<ServiceRegistry> findByName(String name);
    Optional<ServiceRegistry> findByNameAndOrganization(String name, Organization organization);
    List<ServiceRegistry> findByOrganization(Organization organization);
} 