package com.example.iam.service;

import com.example.iam.entity.Scope;
import com.example.iam.repository.ScopeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScopeService {

    private final ScopeRepository scopeRepository;


    public List<Scope> getAllScopes() {
        return scopeRepository.findAll();
    }

    @Transactional
    public Scope createScope(Scope scope) {
        return scopeRepository.save(scope);
    }

    public Scope getScope(Long id) {
        return scopeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scope not found"));
    }

    @Transactional
    public Scope updateScope(Long id, Scope scope) {
        Scope existingScope = getScope(id);
        existingScope.setName(scope.getName());
        existingScope.setDescription(scope.getDescription());
        return scopeRepository.save(existingScope);
    }

    @Transactional
    public void deleteScope(Long id) {
        Scope scope = getScope(id);
        scopeRepository.delete(scope);
    }
} 