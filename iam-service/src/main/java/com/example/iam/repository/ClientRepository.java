package com.example.iam.repository;

import com.example.iam.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {
    Optional<Client> findByClientIdAndOrganizationId(String clientId, Long organizationId);
} 