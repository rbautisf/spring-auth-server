package com.nowhere.springauthserver.service;

import com.nowhere.springauthserver.persistence.entity.Role;
import com.nowhere.springauthserver.persistence.entity.Role.RoleType;
import com.nowhere.springauthserver.persistence.repository.RoleRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role getByType(String type) {
        return roleRepository.findByType(RoleType.valueOf(type)).orElseThrow(
                () -> new RuntimeException("Role not found")
        );
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}
