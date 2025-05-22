package com.example.iam.service;

import com.example.iam.dto.PolicyDTO;
import com.example.iam.entity.Policy;
import com.example.iam.mapper.PolicyMapper;
import com.example.iam.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PolicyService {

    private final PolicyRepository policyRepository;
    private final PolicyMapper policyMapper;

    public List<PolicyDTO> getAllPolicies() {
        return policyRepository.findAll().stream()
                .map(policyMapper::toDTO)
                .toList();
    }

    public PolicyDTO getPolicyById(Long id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found"));
        return policyMapper.toDTO(policy);
    }

    @Transactional
    public PolicyDTO createPolicy(PolicyDTO dto) {
        Policy policy = policyMapper.toEntity(dto);
        return policyMapper.toDTO(policyRepository.save(policy));
    }

    @Transactional
    public PolicyDTO updatePolicy(Long id, PolicyDTO dto) {
        Policy existing = policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found"));
        Policy updated = policyMapper.toEntity(dto);
        updated.setId(id);
        return policyMapper.toDTO(policyRepository.save(updated));
    }

    @Transactional
    public void deletePolicy(Long id) {
        policyRepository.deleteById(id);
    }
}
