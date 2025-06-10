package com.example.iam.service;

import com.example.iam.dto.UserDTO;
import com.example.iam.entity.Role;
import com.example.iam.entity.Scope;
import com.example.iam.entity.User;
import com.example.iam.entity.Organization;
import com.example.iam.repository.RoleRepository;
import com.example.iam.repository.ScopeRepository;
import com.example.iam.repository.UserRepository;
import com.example.iam.repository.OrganizationRepository;
import com.example.iam.repository.ClientApplicationRepository;
import com.example.iam.audit.Auditable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ScopeRepository scopeRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClientApplicationRepository clientApplicationRepository;

    public boolean isClientId(String id) {
        return clientApplicationRepository.findByClientId(id).isPresent();
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Transactional
    @Auditable(action = "CREATE_USER")
    public User createUser(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setFullName(userDTO.getFullName());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        Set<Role> roles = roleRepository.findByNameIn(userDTO.getRoles())
                    .stream().collect(Collectors.toSet());
        user.setRoles(roles);
        user.setOrganization(organizationRepository.findById(userDTO.getOrganization_id())
                .orElseThrow(() -> new RuntimeException("Organization not found with id: " + userDTO.getOrganization_id())));
        return userRepository.save(user);
    }

    @Transactional
    @Auditable(action = "UPDATE_USER")
    public User updateUser(Long id, UserDTO userDTO) {
        User existingUser = findById(id);

        if (userDTO.getUsername() != null) {
            existingUser.setUsername(userDTO.getUsername());
        }
        if (userDTO.getEmail() != null) {
            existingUser.setEmail(userDTO.getEmail());
        }
        if (userDTO.getFullName() != null) {
            existingUser.setFullName(userDTO.getFullName());
        }
        
        if (userDTO.getRoles() != null) {
            Set<Role> roles = roleRepository.findByNameIn(userDTO.getRoles())
                    .stream().collect(Collectors.toSet());
            existingUser.setRoles(roles);
        }

        if (userDTO.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        if (userDTO.getOrganization_id() != null) {
            Organization organization = organizationRepository.findById(userDTO.getOrganization_id())
                    .orElseThrow(() -> new RuntimeException("Organization not found with id: " + userDTO.getOrganization_id()));
            existingUser.setOrganization(organization);
        }

        return userRepository.save(existingUser);
    }

    @Transactional
    @Auditable(action = "DELETE_USER")
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public Set<Role> getUserRoles(Long id) {
        return findById(id).getRoles();
    }

    @Transactional
    @Auditable(action = "ADD_ROLE_TO_USER")
    public User addRoleToUser(Long userId, Long roleId) {
        User user = findById(userId);
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.getRoles().add(role);
        return userRepository.save(user);
    }

    @Transactional
    @Auditable(action = "REMOVE_ROLE_FROM_USER")
    public User removeRoleFromUser(Long userId, Long roleId) {
        User user = findById(userId);
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.getRoles().remove(role);
        return userRepository.save(user);
    }

    public Set<Scope> getUserScopes(Long id) {
        return findById(id).getScopes();
    }

    @Transactional
    @Auditable(action = "ADD_SCOPE_TO_USER")
    public User addScopeToUser(Long userId, Long scopeId) {
        User user = findById(userId);
        Scope scope = scopeRepository.findById(scopeId)
                .orElseThrow(() -> new RuntimeException("Scope not found"));
        user.getScopes().add(scope);
        return userRepository.save(user);
    }

    @Transactional
    @Auditable(action = "REMOVE_SCOPE_FROM_USER")
    public User removeScopeFromUser(Long userId, Long scopeId) {
        User user = findById(userId);
        Scope scope = scopeRepository.findById(scopeId)
                .orElseThrow(() -> new RuntimeException("Scope not found"));
        user.getScopes().remove(scope);
        return userRepository.save(user);
    }
}
