package com.example.iam.service;

import com.example.iam.dto.OrganizationDTO;
import com.example.iam.entity.Organization;
import com.example.iam.entity.Permission;
import com.example.iam.entity.Role;
import com.example.iam.entity.User;
import com.example.iam.mapper.OrganizationMapper;
import com.example.iam.repository.OrganizationRepository;
import com.example.iam.repository.PermissionRepository;
import com.example.iam.repository.RoleRepository;
import com.example.iam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final OrganizationMapper organizationMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    public List<OrganizationDTO> getAllOrganizations() {
        List<Organization> organizations = organizationRepository.findAll();
        return organizationMapper.toDTOList(organizations);
    }

    @Transactional
    public OrganizationDTO createOrganization(OrganizationDTO organizationDTO) {
        Organization organization = organizationMapper.toEntity(organizationDTO);
        Organization savedOrganization = organizationRepository.save(organization);

        createAdminRoleForOrganization(savedOrganization);
        createAdminUserForOrganization(savedOrganization);

        return organizationMapper.toDTO(savedOrganization);
    }

    private void createAdminRoleForOrganization(Organization organization) {
        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");
        adminRole.setDescription("Administrator role for " + organization.getName());
        adminRole.setOrganization(organization);

        // Assign all existing permissions to the admin role
        List<Permission> allPermissions = permissionRepository.findAll();
        adminRole.setPermissions(new HashSet<>(allPermissions));
        
        roleRepository.save(adminRole);
    }

    private void createAdminUserForOrganization(Organization organization) {
        String adminEmail = "admin@" + organization.getName().toLowerCase().replaceAll("\\s+", "") + ".com";
        if (userRepository.existsByEmail(adminEmail)) {
            return; // Admin user already exists
        }

        Role adminRole = roleRepository.findByNameAndOrganization("ROLE_ADMIN", organization)
            .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN not found for organization " + organization.getName()));

        User adminUser = User.builder()
            .username("admin")
            .email(adminEmail)
            .password(passwordEncoder.encode("admin123")) // Default password, should be changed
            .fullName("Admin " + organization.getName())
            .organization(organization)
            .roles(Set.of(adminRole))
            .enabled(true)
            .build();
        
        userRepository.save(adminUser);
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
        if (organizationDTO.getName() != null) {
            existingOrganization.setName(organizationDTO.getName());
        }
        if (organizationDTO.getDescription() != null) {
            existingOrganization.setDescription(organizationDTO.getDescription());
        }
        if (organizationDTO.getActive() != null) {
            existingOrganization.setActive(organizationDTO.getActive());
        }
        
        Organization updated = organizationRepository.save(existingOrganization);
        return organizationMapper.toDTO(updated);
    }

    @Transactional
    public void deleteOrganization(Long id) {
        organizationRepository.deleteById(id);
    }
}