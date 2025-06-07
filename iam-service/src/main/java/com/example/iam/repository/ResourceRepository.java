package com.example.iam.repository;

import com.example.iam.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    boolean existsByName(String name);
    Optional<Resource> findByName(String name);
    Optional<Resource> findByPath(String path);
    Optional<Resource> findByPathAndMethod(String path, Resource.HttpMethod method);
}
