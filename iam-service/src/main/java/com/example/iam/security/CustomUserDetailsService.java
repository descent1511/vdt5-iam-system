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
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ClientApplicationRepository clientApplicationRepository;


    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrClientId) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameWithRoles(usernameOrClientId).orElse(null);
        if (user != null) {
            return UserPrincipal.create(user);
        }

        ClientApplication client = clientApplicationRepository.findByClientId(usernameOrClientId).orElse(null);
        if (client != null) {
            return ClientPrincipal.create(client);
        }

        throw new UsernameNotFoundException("No user or client found with username/clientId: " + usernameOrClientId);
    }

}
