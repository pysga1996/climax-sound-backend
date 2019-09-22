package com.lambda.service;

import com.lambda.model.Privilege;

public interface PrivilegeService {
    Privilege findByName(String name);
}
