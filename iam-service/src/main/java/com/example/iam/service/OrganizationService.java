package com.example.iam.service;

import com.example.iam.dto.OrganizationDTO;
import com.example.iam.entity.Organization;
import com.example.iam.mapper.OrganizationMapper;
import com.example.iam.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final OrganizationMapper organizationMapper;

    public List<OrganizationDTO> getAllOrganizations() {
        List<Organization> organizations = organizationRepository.findAll();
        return organizationMapper.toDTOList(organizations);
    }

    @Transactional
    public OrganizationDTO createOrganization(OrganizationDTO organizationDTO) {
        Organization organization = new Organization();
        organization.setName(organizationDTO.getName());
        organization.setDescription(organizationDTO.getDescription());
        Organization saved = organizationRepository.save(organization);
        return organizationMapper.toDTO(saved);
    }

    public OrganizationDTO getOrganization(Long id) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
        return organizationMapper.toDTO(organization);
    }

    @Transactional
    public OrganizationDTO updateOrganization(Long id, OrganizationDTO organizationDTO) {
        Organization existingOrganization = organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
        
        existingOrganization.setName(organizationDTO.getName());
        existingOrganization.setDescription(organizationDTO.getDescription());
        
        Organization updated = organizationRepository.save(existingOrganization);
        return organizationMapper.toDTO(updated);
    }

    @Transactional
    public void deleteOrganization(Long id) {
        organizationRepository.deleteById(id);
    }
}