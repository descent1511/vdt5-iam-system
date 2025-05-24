package com.example.iam.service;

import com.example.iam.dto.SignupRequest;
import com.example.iam.entity.Organization;
import com.example.iam.entity.User;
import com.example.iam.repository.OrganizationRepository;
import com.example.iam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setFullName(signupRequest.getFullName());

        if (signupRequest.getOrganizationId() != null) {
            Organization organization = organizationRepository.findById(signupRequest.getOrganizationId())
                .orElseThrow(() -> new RuntimeException("Organization not found"));
            user.setOrganization(organization);
        }
        return userRepository.save(user);
    }


    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
} 