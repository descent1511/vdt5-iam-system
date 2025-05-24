package com.example.iam.repository;

import com.example.iam.entity.Policy;
import com.example.iam.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {
    List<Policy> findByResource(Resource resource);
    List<Policy> findBySubjectIdAndSubjectType(Long subjectId, Policy.SubjectType subjectType);
    Set<Policy> findBySubjectTypeAndSubjectIdAndResource(Policy.SubjectType subjectType, Long subjectId, Resource resource);
}
