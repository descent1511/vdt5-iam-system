package com.example.iam.service;

import com.example.iam.dto.UserDTO;
import com.example.iam.entity.Role;
import com.example.iam.entity.Scope;
import com.example.iam.entity.User;
import com.example.iam.repository.RoleRepository;
import com.example.iam.repository.ScopeRepository;
import com.example.iam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ScopeRepository scopeRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Transactional
    public User createUser(UserDTO userDTO) {
        User user = new User();
        updateUserFromDTO(user, userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(Long id, UserDTO userDTO) {
        User existingUser = findById(id);
        updateUserFromDTO(existingUser, userDTO);
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        return userRepository.save(existingUser);
    }

    private void updateUserFromDTO(User user, UserDTO userDTO) {
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setFullName(userDTO.getFullName());
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public Set<Role> getUserRoles(Long id) {
        User user = findById(id);
        return user.getRoles();
    }

    @Transactional
    public User addRoleToUser(Long userId, Long roleId) {
        User user = findById(userId);
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.getRoles().add(role);
        return userRepository.save(user);
    }

    @Transactional
    public User removeRoleFromUser(Long userId, Long roleId) {
        User user = findById(userId);
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.getRoles().remove(role);
        return userRepository.save(user);
    }

    public Set<Scope> getUserScopes(Long id) {
        User user = findById(id);
        return user.getScopes();
    }

    @Transactional
    public User addScopeToUser(Long userId, Long scopeId) {
        User user = findById(userId);
        Scope scope = scopeRepository.findById(scopeId)
                .orElseThrow(() -> new RuntimeException("Scope not found"));
        user.getScopes().add(scope);
        return userRepository.save(user);
    }

    @Transactional
    public User removeScopeFromUser(Long userId, Long scopeId) {
        User user = findById(userId);
        Scope scope = scopeRepository.findById(scopeId)
                .orElseThrow(() -> new RuntimeException("Scope not found"));
        user.getScopes().remove(scope);
        return userRepository.save(user);
    }
} 