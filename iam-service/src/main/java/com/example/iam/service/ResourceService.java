package com.example.iam.service;

import com.example.iam.entity.Resource;
import com.example.iam.repository.ResourceRepository;
// import com.example.iam.config.annotation.RequiresOrganizationContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public List<Resource> getAll() {
        return resourceRepository.findAll();
    }

    public Resource getById(Long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found"));
    }

    @Transactional
    public Resource create(Resource resource) {
        if (resourceRepository.existsByName(resource.getName())) {
            throw new RuntimeException("Resource name already exists");
        }
        return resourceRepository.save(resource);
    }

    @Transactional
    public Resource update(Long id, Resource updated) {
        Resource existing = getById(id);
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setMethod(updated.getMethod());
        existing.setPath(updated.getPath());
        return resourceRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        resourceRepository.deleteById(id);
    }
}
