package com.lambda.service;

import com.lambda.model.entities.Role;

public interface RoleService {
    Role findByAuthority(String name);
    void save(Role role);
}
