package com.example.iam.repository;

import com.example.iam.entity.User;
import com.example.iam.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username = :username")
    Optional<User> findByUsernameWithRoles(String username);

    Optional<User> findByUsernameAndOrganizationId(String username, Long organizationId);
    boolean existsByUsernameAndOrganizationId(String username, Long organizationId);
    boolean existsByEmailAndOrganizationId(String email, Long organizationId);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username = :username AND u.organization.id = :organizationId")
    Optional<User> findByUsernameAndOrganizationIdWithRoles(String username, Long organizationId);

    List<User> findByOrganizationId(Long organizationId);
    Optional<User> findByIdAndOrganizationId(Long id, Long organizationId);

    Optional<User> findByUsernameAndOrganization(String username, Organization organization);
} 