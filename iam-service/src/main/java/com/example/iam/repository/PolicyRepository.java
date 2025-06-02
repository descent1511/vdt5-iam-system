package com.example.iam.repository;

import com.example.iam.entity.Policy;
import com.example.iam.entity.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {
    List<Policy> findByResource(Resource resource);
    List<Policy> findBySubjectIdAndSubjectType(Long subjectId, Policy.SubjectType subjectType);
    Set<Policy> findBySubjectTypeAndSubjectId(Policy.SubjectType subjectType, Long subjectId);
    Set<Policy> findBySubjectTypeAndSubjectIdAndResource(Policy.SubjectType subjectType, Long subjectId, Resource resource);

    List<Policy> findByEffect(Policy.Effect effect);
    
    @Query("SELECT p FROM Policy p WHERE p.resource.id = :resourceId AND p.action = :action")
    List<Policy> findByResourceAndAction(@Param("resourceId") Long resourceId, @Param("action") String action);

    @Query("SELECT p FROM Policy p WHERE p.subjectType = :subjectType AND p.subjectId = :subjectId AND p.effect = :effect")
    List<Policy> findBySubjectAndEffect(
        @Param("subjectType") Policy.SubjectType subjectType,
        @Param("subjectId") Long subjectId,
        @Param("effect") Policy.Effect effect
    );

    boolean existsBySubjectTypeAndSubjectIdAndResourceAndAction(
        Policy.SubjectType subjectType,
        Long subjectId,
        Resource resource,
        String action
    );

    @Query("SELECT p FROM Policy p WHERE (:search IS NULL OR LOWER(p.action) LIKE LOWER(CONCAT('%', :search, '%'))) AND (:subjectType IS NULL OR p.subjectType = :subjectType) AND (:effect IS NULL OR p.effect = :effect) AND (:resourceId IS NULL OR p.resource.id = :resourceId)")
    Page<Policy> searchPolicies(
            @Param("search") String search,
            @Param("subjectType") Policy.SubjectType subjectType,
            @Param("effect") Policy.Effect effect,
            @Param("resourceId") Long resourceId,
            Pageable pageable
    );
}
