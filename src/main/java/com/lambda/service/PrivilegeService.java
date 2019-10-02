package com.lambda.service;

import com.lambda.model.entity.Privilege;

public interface PrivilegeService {
    Privilege findByName(String name);
    void save(Privilege privilege);
}
