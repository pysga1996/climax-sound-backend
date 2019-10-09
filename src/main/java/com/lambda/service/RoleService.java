package com.lambda.service;

import com.lambda.model.entity.Role;

public interface RoleService {
    Role findByName(String name);
    void save(Role role);
}
