package com.lambda.services;

import com.lambda.models.entities.Role;

public interface RoleService {
    Role findByAuthority(String name);
    void save(Role role);
}
