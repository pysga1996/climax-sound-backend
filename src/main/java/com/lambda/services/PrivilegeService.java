package com.lambda.services;

import com.lambda.models.entities.Privilege;

public interface PrivilegeService {
    Privilege findByName(String name);
    void save(Privilege privilege);
}
