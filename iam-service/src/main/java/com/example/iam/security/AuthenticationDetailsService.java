package com.example.iam.security;

import com.example.iam.entity.User;
import com.example.iam.entity.ClientApplication;
import com.example.iam.repository.UserRepository;
import com.example.iam.repository.ClientApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ClientApplicationRepository clientApplicationRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user details for username: {}", username);
        
        // Try to find a regular user first
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            log.debug("Found regular user: {}", user.getUsername());
            return UserPrincipal.create(user);
        }

        // If not found, try to find a client application
        ClientApplication client = clientApplicationRepository.findByClientId(username).orElse(null);
        if (client != null) {
            log.debug("Found client application: {}", client.getClientId());
            return ClientPrincipal.create(client);
        }

        log.warn("No user or client found with username: {}", username);
        throw new UsernameNotFoundException("No user or client found with username: " + username);
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        log.debug("Loading user details for id: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("No user found with id: {}", id);
                    return new UsernameNotFoundException("No user found with id: " + id);
                });

        return UserPrincipal.create(user);
    }
} 