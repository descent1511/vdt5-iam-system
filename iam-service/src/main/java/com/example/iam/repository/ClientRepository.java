package com.example.iam.repository;

import com.example.iam.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {
    Optional<Client> findByClientIdAndOrganizationId(String clientId, Long organizationId);

    List<Client> findAllByOrganizationId(Long organizationId);

    @Modifying
    @Query("delete from Client c where c.clientId = ?1 and c.organization.id = ?2")
    void deleteByClientIdAndOrganizationId(String clientId, Long organizationId);
} 