package com.example.iam.service;

import com.example.iam.entity.Organization;
import com.example.iam.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationService {
    private final OrganizationRepository organizationRepository;

    public List<Organization> getAllOrganizations() {
        return organizationRepository.findAll();
    }

    @Transactional
    public Organization createOrganization(Organization organization) {
        return organizationRepository.save(organization);
    }

    public Organization getOrganization(Long id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
    }

    @Transactional
    public Organization updateOrganization(Long id, Organization organization) {
        Organization existingOrganization = getOrganization(id);
        existingOrganization.setName(organization.getName());
        existingOrganization.setDescription(organization.getDescription());
        return organizationRepository.save(existingOrganization);
    }

    @Transactional
    public void deleteOrganization(Long id) {
        Organization organization = getOrganization(id);
        organizationRepository.delete(organization);
    }
}