package com.example.iam.security;

import com.example.iam.entity.ClientApplication;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
public class ClientPrincipal implements UserDetails {
    private String clientId;
    private String clientSecret;
    private Collection<? extends GrantedAuthority> authorities;
    private ClientApplication client;

    public static ClientPrincipal create(ClientApplication client) {
        List<GrantedAuthority> authorities = client.getScopes().stream()
                .flatMap(scope -> scope.getPermissions().stream())
                .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                .collect(Collectors.toList());
    
        return ClientPrincipal.builder()
                .clientId(client.getClientId())
                .clientSecret(client.getClientSecret())
                .authorities(authorities)
                .client(client)
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return clientSecret;
    }

    @Override
    public String getUsername() {
        return clientId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
} 