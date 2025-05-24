package com.example.iam.service;

import com.example.iam.entity.ClientApplication;
import com.example.iam.entity.Policy;
import com.example.iam.entity.Resource;
import com.example.iam.entity.Scope;
import com.example.iam.repository.PolicyRepository;
import com.example.iam.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Service for managing client application policies.
 * Automatically creates and manages policies for client applications.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClientPolicyService {

    private final PolicyRepository policyRepository;
    private final ResourceRepository resourceRepository;

    /**
     * Creates policies for a client application based on its scopes.
     * Each scope will have a corresponding policy that grants access to the client.
     *
     * @param client The client application
     * @param scopes The scopes to create policies for
     */
    @Transactional
    public void createPoliciesForClient(ClientApplication client, Set<Scope> scopes) {

        // Get all resources that match the scopes
        List<Resource> resources = resourceRepository.findAll().stream()
                .filter(resource -> scopes.stream()
                        .anyMatch(scope -> resource.getScopes().contains(scope)))
                .toList();

        // Create a policy for each resource-scope combination
        for (Resource resource : resources) {
            for (Scope scope : scopes) {
                if (resource.getScopes().contains(scope)) {
                    Policy policy = new Policy();
                    policy.setSubjectId(client.getId());
                    policy.setSubjectType(Policy.SubjectType.CLIENT);
                    policy.setResource(resource);
                    policy.setScope(scope);
                    policy.setDescription(String.format("Policy for client %s to access %s with scope %s",
                            client.getClientId(), resource.getPath(), scope.getName()));

                    policyRepository.save(policy);
                    log.debug("Created policy for client {} to access {} with scope {}",
                            client.getClientId(), resource.getPath(), scope.getName());
                }
            }
        }
    }

    /**
     * Updates policies for a client application when its scopes change.
     *
     * @param client The client application
     * @param newScopes The new set of scopes
     */
    @Transactional
    public void updatePoliciesForClient(ClientApplication client, Set<Scope> newScopes) {
        log.debug("Updating policies for client: {}", client.getClientId());

        // Delete existing policies
        List<Policy> existingPolicies = policyRepository.findBySubjectIdAndSubjectType(
                client.getId(), Policy.SubjectType.CLIENT);
        policyRepository.deleteAll(existingPolicies);

        // Create new policies
        createPoliciesForClient(client, newScopes);
    }

    /**
     * Deletes all policies for a client application.
     *
     * @param client The client application
     */
    @Transactional
    public void deletePoliciesForClient(ClientApplication client) {
        log.debug("Deleting policies for client: {}", client.getClientId());

        List<Policy> policies = policyRepository.findBySubjectIdAndSubjectType(
                client.getId(), Policy.SubjectType.CLIENT);
        policyRepository.deleteAll(policies);
    }
} 