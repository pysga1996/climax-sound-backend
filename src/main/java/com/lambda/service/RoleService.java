package com.lambda.service;

import com.lambda.model.Role;

public interface RoleService {
    Role findByName(String name);
}
