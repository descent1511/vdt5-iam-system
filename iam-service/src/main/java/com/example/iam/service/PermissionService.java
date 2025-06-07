package com.example.iam.service;

import com.example.iam.dto.PermissionDTO;
import com.example.iam.entity.Permission;
import com.example.iam.mapper.PermissionMapper;
import com.example.iam.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    public List<PermissionDTO> getAllPermissions() {
        List<Permission> permissions = permissionRepository.findAll();
        return permissionMapper.toDtoList(permissions);
    }
} 