package com.lambda.service.impl;

import com.lambda.model.entities.Role;
import com.lambda.repositories.RoleRepository;
import com.lambda.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    RoleRepository roleRepository;

    @Override
    public Role findByAuthority(String authority) {
        return roleRepository.findByAuthority(authority);
    }

    @Override
    public void save(Role role) {
        roleRepository.save(role);
    }
}
